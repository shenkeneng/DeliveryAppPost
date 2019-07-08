package com.frxs.delivery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.InputUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.widget.ClearEditText;

import retrofit2.Call;

/**
 * 修改密码 by Tiepier
 */
public class UpdatePswActivity extends FrxsActivity {

    private ClearEditText cetOldPsw;

    private ClearEditText cetNewPsw;

    private ClearEditText cetNewPswSure;

    private Button btnUpdatePsw;// 确认提交

    private TextView tvTitle;//标题

    private TextView tvLeft;//返回

    private TextView tvRight;

    private String strOldPsw;// 旧密码

    private String srtNewPsw;// 新密码

    private String srtNewPswSure;// 确认新密码

    @Override
    protected int getLayoutId() {
        return R.layout.activity_password;
    }

    @Override
    protected void initViews() {

        /**
         * 实例化控件
         */
        cetOldPsw = (ClearEditText) findViewById(R.id.cet_pwd_old);
        cetNewPsw = (ClearEditText) findViewById(R.id.cet_pwd_new);
        cetNewPswSure = (ClearEditText) findViewById(R.id.cet_pwd_new_sure);
        tvLeft = (TextView) findViewById(R.id.tv_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_title_right);
        btnUpdatePsw = (Button) findViewById(R.id.btn_update_password);

        tvTitle.setText(R.string.title_update_password);
        tvRight.setVisibility(View.INVISIBLE);


    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initEvent() {
        btnUpdatePsw.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_update_password: {
                if (TextUtils.isEmpty(cetOldPsw.getText().toString().trim())) {
                    ToastUtils.show(this, R.string.tips_not_null_pw_old);//旧密码不能为空
                    shakeView(cetOldPsw);
                } else if (TextUtils.isEmpty(cetNewPsw.getText().toString().trim())) {
                    ToastUtils.show(this, R.string.tips_not_null_pw_new);//新密码不能为空
                    shakeView(cetNewPsw);
                } else if (TextUtils.isEmpty(cetNewPswSure.getText().toString().trim())) {
                    ToastUtils.show(this, R.string.tips_not_null_pw_new_sure);//确认新密码不能为空
                    shakeView(cetNewPswSure);
                } else {
                    strOldPsw = cetOldPsw.getText().toString().trim();
                    srtNewPsw = cetNewPsw.getText().toString().trim();
                    srtNewPswSure = cetNewPswSure.getText().toString().trim();
                    if (InputUtils.isNumericOrLetter(srtNewPsw)) {
                        if (srtNewPswSure.equals(srtNewPsw)) {
                            requestUpdatePassword();
                        } else {
                            ToastUtils.show(this, R.string.tips_new_password_error);// 新密码确认错误
                            shakeView(cetNewPswSure);
                        }
                    } else if (srtNewPsw.equals(strOldPsw)) {
                        ToastUtils.show(this, R.string.tips_old_new_not_same);//新密码不能和旧密码相同
                    } else {
                        ToastUtils.show(this, R.string.tips_input_limit);// 密码只能由数字、字母组成
                        shakeView(cetNewPsw);
                    }
                }
                break;
            }
        }
    }

    private void requestUpdatePassword() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("DeliverUpdatePwd"));
        params.put("UserAccount", userInfo.getUserAccount());
        params.put("OldUserPwd", strOldPsw);
        params.put("NewUserPwd", srtNewPsw);
        params.put("UserType", "2");
        getService().DeliverUpdatePwd(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Object>>() {

            @Override
            public void onResponse(ApiResponse<Object> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(UpdatePswActivity.this, R.string.tips_update_success);//密码修改成功
                    finish();
                } else if (result.getFlag().equals("1")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdatePswActivity.this);
                    builder.setMessage(result.getInfo() + "，无法修改密码。");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(UpdatePswActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    if (!isFinishing()) {
                        builder.create().show();
                    } else {
                        Intent intent = new Intent(UpdatePswActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }


    /**
     * 窗口抖动
     */

    private void shakeView(EditText edit) {
        DisplayUtil.shakeView(this, edit);
        edit.requestFocus();
    }
}
