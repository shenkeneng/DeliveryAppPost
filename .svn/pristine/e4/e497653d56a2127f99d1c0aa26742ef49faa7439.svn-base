package com.frxs.delivery.application;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import com.allenliu.versionchecklib.core.http.HttpParams;
import com.allenliu.versionchecklib.core.http.HttpRequestMethod;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.ewu.core.base.BaseActivity;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.R;
import com.frxs.delivery.comms.Config;
import com.frxs.delivery.comms.GlobelDefines;
import com.frxs.delivery.model.AppVersionGetRespData;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.RestClient;
import com.ewu.core.utils.SerializableUtil;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.frxs.delivery.rest.model.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by ewu on 2016/2/18.
 */
public class FrxsApplication extends Application {
    private static FrxsApplication mInstance;
    private static RestClient restClient;
    private UserInfo mUserInfo;// 用户信息
    private boolean needCheckUpgrade = true; // 是否需要检测更新
    private static SparseArray<RestClient> restClientSparseArray = new SparseArray<RestClient>();
    private Activity mActivity;
    private DownloadBuilder builder;

    public static FrxsApplication getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Not yet initialized");
        }

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance != null) {
            throw new IllegalStateException("Not a singleton");
        }

        mInstance = this;

        initData();

        restClient = new RestClient(Config.getBaseUrl());
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public void renewRestClient() {
        restClient = new RestClient(Config.getBaseUrl(getEnvironment()));
    }

    private void initData() {
        Config.networkEnv = getEnvironment();
        // Get the user Info
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String userStr = helper.getString(Config.KEY_USER, "");
        if (!TextUtils.isEmpty(userStr)) {
            Object object = null;
            try {
                object = SerializableUtil.str2Obj(userStr);
                if (null != object) {
                    mUserInfo = (UserInfo) object;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.mUserInfo = userInfo;

        String userStr = "";
        try {
            userStr = SerializableUtil.obj2Str(userInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        helper.putValue(Config.KEY_USER, userStr);
    }

    public UserInfo getUserInfo() {
        if (null == mUserInfo) {
            initData();
        }

        return mUserInfo;
    }

    public void setUserPwd(String passWord) {
        if (null != mUserInfo) {
            mUserInfo.setUserPwd(passWord);
            setUserInfo(mUserInfo);
        }
    }

    public void setEnvironment(int environmentId) {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        helper.putValue(GlobelDefines.KEY_ENVIRONMENT, environmentId);
    }

    public int getEnvironment() {
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, GlobelDefines.PREFS_NAME);
        return helper.getInt(GlobelDefines.KEY_ENVIRONMENT, Config.networkEnv);
    }

    public boolean isNeedCheckUpgrade() {
        return needCheckUpgrade;
    }

    /**
     * 更新版本的网路请求
     *
     * @param activity
     */
    public void prepare4Update(final Activity activity, final boolean isShow) {
        if (!SystemUtils.checkNet(this) || !SystemUtils.isNetworkAvailable(this)) {
            ToastUtils.show(this, "网络不可用");
            return;
        }
        mActivity = activity;
        ((BaseActivity) mActivity).showProgressDialog();
        //开始检测了升级之后，设置标志位为不再检测升级
        if (needCheckUpgrade) {
            needCheckUpgrade = false;
        } else {
            return;
        }
        String url = Config.getBaseUrl(getEnvironment()) + "AppVersion/AppVersionUpdateGet";
        HttpParams httpParams = new HttpParams();
        httpParams.put("SysType", 0); // 0:android;1:ios
        httpParams.put("AppType", 2); // 软件类型(0:兴盛店订货平台, 1:拣货APP. 2:兴盛店配送APP,3:装箱APP, 4:采购APP, 5:网络店订货平台,6：网络店配送APP,9退货库app)
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(url)
                .setRequestMethod(HttpRequestMethod.POSTJSON)
                .setRequestParams(httpParams)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        ((BaseActivity) mActivity).dismissProgressDialog();
                        Type type = new TypeToken<ApiResponse<AppVersionGetRespData>>() {
                        }.getType();
                        ApiResponse<AppVersionGetRespData> respData = new Gson().fromJson(result, type);
                        int versionCode = Integer.valueOf(SystemUtils.getVersionCode(getApplicationContext()));
                        if (respData.getData() == null) {
                            ToastUtils.show(activity, "更新接口无数据");
                            return null;
                        }
                        if (versionCode >= respData.getData().getCurCode()) {
                            ToastUtils.show(activity, "已是最新版本");
                            return null;
                        }
                        if (respData.getData().getUpdateFlag() == 0) {
                            return null;
                        }
                        if (respData.getData().getUpdateFlag() == 2) {
                            builder.setForceUpdateListener(new ForceUpdateListener() {
                                @Override
                                public void onShouldForceUpdate() {
                                    forceUpdate();
                                }
                            });
                        }
                        return crateUIData(respData.getData().getDownUrl(), respData.getData().getUpdateRemark());
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {
                        ((BaseActivity) mActivity).dismissProgressDialog();
                        ToastUtils.show(activity, "request failed");

                    }
                });
        builder.setShowNotification(true);
        builder.setShowDownloadingDialog(true);
        builder.setShowDownloadFailDialog(true);
        builder.setForceRedownload(true);
        builder.excuteMission(activity);
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData(String downloadUrl, String updateRemark) {
        UIData uiData = UIData.create();
        uiData.setTitle(getString(R.string.update_title));
        uiData.setDownloadUrl(downloadUrl);
        uiData.setContent(updateRemark);
        return uiData;
    }

    /**
     * 强制更新操作
     */
    private void forceUpdate() {
        mActivity.finish();
    }

    public void exitApp(int code) {
        System.exit(code);
    }

}
