package com.frxs.delivery.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.ewu.core.utils.EasyPermissionsEx;
import com.frxs.delivery.BillActivity;
import com.frxs.delivery.FinshDeliveryActivity;
import com.frxs.delivery.LoginActivity;
import com.frxs.delivery.R;
import com.frxs.delivery.UpdatePswActivity;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.UserInfo;

/**
 * 关于我 by Tiepier
 */
public class MineFragment extends FrxsFragment {
    private TextView tvLeft;
    private TextView tvRight;
    private TextView tvTitle;//标题
    private TextView tvUserInfo;//用户信息
    private TextView tvChangePWD;//修改密码
    private TextView tvVersion;//版本号
    private TextView tvSignOut;//退出用户
    private TextView tvBill;//对账单
    private TextView tvFinshDelivery;//完成配送订单
    // 请求文件存储权限的标识码
    private static final int MY_PERMISSIONS_REQUEST_WES = 4;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initViews(View view) {
        /**
         * 实例化控件
         */
        tvLeft = (TextView) view.findViewById(R.id.tv_title_left);
        tvRight = (TextView) view.findViewById(R.id.tv_title_right);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvUserInfo = (TextView) view.findViewById(R.id.tv_user_info);
        tvChangePWD = (TextView) view.findViewById(R.id.tv_change_password);
        tvVersion = (TextView) view.findViewById(R.id.tv_version_id);
        tvSignOut = (TextView) view.findViewById(R.id.tv_sign_out);
        tvBill = (TextView) view.findViewById(R.id.tv_bill);
        //TODO: 是否开启电子签名功能 （0：不启用；1：启用不要验证身份；2：启用并验证身份）
        tvFinshDelivery = (TextView) view.findViewById(R.id.tv_finsh_delivery);

        tvLeft.setVisibility(View.INVISIBLE);
        tvRight.setVisibility(View.INVISIBLE);
        tvTitle.setText(R.string.title_mine);
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(userInfo.getUserMobile())) { //电话可为空 用户名不能为空
            tvUserInfo.setText(userInfo.getEmpName() + "   +86 " + userInfo.getUserMobile());//用户信息
        } else {
            tvUserInfo.setText(userInfo.getEmpName());
        }
        tvVersion.setText(mActivity.getResources().getString(R.string.tv_version_id, getVersion()));
        tvBill.setVisibility(userInfo.getIsMaster().equals("1") || userInfo.getIsShippingCar() == 0 ? View.GONE : View.VISIBLE);// 当前用户为组长、外协司机隐藏对账单
    }

    @Override
    protected void initEvent() {
        tvSignOut.setOnClickListener(this);//退出用户
        tvChangePWD.setOnClickListener(this);//修改密码
        tvVersion.setOnClickListener(this);//版本更新
        tvBill.setOnClickListener(this);//对账单
        tvFinshDelivery.setOnClickListener(this);//完成配送订单
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出用户
            case R.id.tv_sign_out:
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.putExtra("SIGNOUT", true);
                startActivity(intent);
                mActivity.finish();
                FrxsApplication.getInstance().setUserPwd("");
                FrxsApplication.getInstance().getUserInfo().setIsMaster("");
                break;
            //修改密码
            case R.id.tv_change_password:
                Intent toPassWord = new Intent(mActivity, UpdatePswActivity.class);
                startActivity(toPassWord);
                break;
            //版本更新
            case R.id.tv_version_id:
                if (EasyPermissionsEx.hasPermissions(mActivity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
                    // 允许 - 执行更新方法
                    if (FrxsApplication.getInstance().isNeedCheckUpgrade()) {
                        FrxsApplication.getInstance().prepare4Update(mActivity, false);
                    }
                } else {
                    // 不允许 - 弹窗提示用户是否允许放开权限
                    EasyPermissionsEx.executePermissionsRequest(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WES);
                }
                break;
            //对账单
            case R.id.tv_bill:
                Intent toBill = new Intent(mActivity, BillActivity.class);
                startActivity(toBill);
                break;
            //完成配送订单
            case R.id.tv_finsh_delivery:
                Intent toFinsh = new Intent(mActivity, FinshDeliveryActivity.class);
                startActivity(toFinsh);
                break;
        }
    }

    /**
     * 获取系统版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = mActivity.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mActivity.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
}
