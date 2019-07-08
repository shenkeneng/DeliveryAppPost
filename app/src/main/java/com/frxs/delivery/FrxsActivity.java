package com.frxs.delivery;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.ewu.core.base.BaseActivity;
import com.ewu.core.utils.EasyPermissionsEx;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.ApiService;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import retrofit2.Call;

/**
 * Created by ewu on 2016/3/24.
 */
public abstract class FrxsActivity extends BaseActivity {

    protected ApiService mService;

    //用来控制应用前后台切换的标识
    private boolean applicationBroughtToBackground = true;

    private Dialog dialog;

    // 相机权限标识
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;

    // GPS权限标识
    private static final int MY_PERMISSIONS_REQUEST_GPS = 3;

    // 请求文件存储权限的标识码
    private static final int MY_PERMISSIONS_REQUEST_WES = 4;

    // 手机权限标识
    private static final int MY_PERMISSIONS_REQUEST_PHONE_STATE = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(this instanceof SplashActivity) && !(this instanceof BaseDialogActivity)) {
            // 判断当前用户是否允许此权限
            if (EasyPermissionsEx.hasPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                // 允许 - 执行更新方法
                if (FrxsApplication.getInstance().isNeedCheckUpgrade()) {
                    FrxsApplication.getInstance().prepare4Update(this, false);
                }
            } else {
                // 不允许 - 弹窗提示用户是否允许放开权限
                EasyPermissionsEx.executePermissionsRequest(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WES);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                setTranslucentStatus(true);
            }

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.frxs_red);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public ApiService getService() {
        mService = FrxsApplication.getRestClient().getApiService();
        return mService;
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void initEvent();

    protected abstract void initData();

    public void onBack(View view) {
        finish();
    }

    public void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    /**
     * 查询有没有超过规则时间未完成配送的订单(初始化时、从后台切从前台时、点击完成装车时都需判断)
     */
    public void reqQueryOverTimeDeliverOrder() {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        params.put("EmpID", userInfo.getEmpID());
        params.put("WID", userInfo.getWareHouseWID());
        getService().GetOverTimeDeliverOrder(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(ApiResponse<Integer> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")){
                    if (result.getData() > 0){
                        if (dialog == null) {
                            dialog = new Dialog(FrxsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                        }
                        dialog.setContentView(R.layout.dialog_selector);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
                        tvMessage.setText(getResources().getString(R.string.dialog_not_delivery));
                        dialog.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
                        dialog.show();
                        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
                        btnConfirm.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                if (FrxsActivity.this instanceof HomeActivity) {
                                    FragmentTabHost mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
                                    mTabHost.setCurrentTab(1);
                                } else {
                                    Intent intent = new Intent(FrxsActivity.this, HomeActivity.class);
                                    intent.putExtra("TAB", 1);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                } else {
                    ToastUtils.show(FrxsActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(FrxsActivity.this, t.getMessage() + "请求失败");
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (applicationBroughtToBackground) {
            applicationBroughtToBackground = false;
           //判断当前账号是否是组长（不是组长查询是否有超过规定时间未完成配送的订单）
            UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
            if (null != userInfo){
                if (!userInfo.isMaster() && ! (this instanceof SplashActivity) && ! (this instanceof LoginActivity)
                        && ! (this instanceof TakePictureActivity) && ! (this instanceof SignatureActivity)) {
                    reqQueryOverTimeDeliverOrder();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        applicationBroughtToBackground = SystemUtils.isApplicationBroughtToBackground(this);
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public boolean hasCameraPermissions(){
        // 判断当前用户是否允许相机权限
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.CAMERA})) {
            return true;
        } else {
            // 不允许 - 弹窗提示用户是否允许放开权限
            EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
    }

    public boolean hasGPSPermissions(){
        // 判断当前用户是否允许GPS权限
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
            return true;
        } else {
            // 不允许 - 弹窗提示用户是否允许放开权限
            EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_GPS);
            return false;
        }
    }

    public boolean hasSignPermissions(){
        // 判断当前用户是否允许GPS权限
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA
        , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
            return true;
        } else {
            // 不允许 - 弹窗提示用户是否允许放开权限
            EasyPermissionsEx.executePermissionsRequest(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA
                            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_PHONE_STATE);
            return false;
        }
    }

    /**
     * 请求用户是否放开权限的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 已获取权限 继续运行应用
                    if (FrxsApplication.getInstance().isNeedCheckUpgrade()) {
                        FrxsApplication.getInstance().prepare4Update(this, false);
                    }
                } else {
                    // 不允许放开权限后，提示用户可在去设置中跳转应用设置页面放开权限。
                    if (!EasyPermissionsEx.somePermissionPermanentlyDenied(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                        EasyPermissionsEx.goSettings2PermissionsDialog(this, "需要文件存储权限来下载更新的内容,但是该权限被禁止,你可以到设置中更改");
                    }
                }
                break;
            }
        }
    }
}
