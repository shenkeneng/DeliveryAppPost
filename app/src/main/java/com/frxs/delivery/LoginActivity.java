package com.frxs.delivery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.ewu.core.utils.DisplayUtil;
import com.ewu.core.utils.InputUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.comms.Config;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import retrofit2.Call;

/**
 * 用户登录 by Tiepier
 */
public class LoginActivity extends FrxsActivity {

    private Button loginBtn;

    private EditText edtAccount;

    private EditText edtPassword;

    private View envHiddenBtn;// 选择环境的暗门

    private String strUserName;// 账号

    private String strPassWord;// 密码

    private TextView tvLeft;
    private TextView tvRight;
    private TextView tvTitle;//标题

    private String[] environments = {"线上环境", "测试环境", "开发环境"/*, "微信环境"*/};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {

        /**
         * 实例化控件
         */
        loginBtn = (Button) findViewById(R.id.login_commit_btn);//登录
        edtAccount = (EditText) findViewById(R.id.login_account_edit);// 用户名编辑框
        edtPassword = (EditText) findViewById(R.id.login_password_edit);// 密码编辑框
        envHiddenBtn = findViewById(R.id.select_environment);// 环境选择按钮
        tvLeft = (TextView) findViewById(R.id.tv_title_left);
        tvRight = (TextView) findViewById(R.id.tv_title_right);
        tvTitle = (TextView) findViewById(R.id.tv_title);//标题

        tvLeft.setVisibility(View.INVISIBLE);
        tvRight.setVisibility(View.INVISIBLE);
        tvTitle.setText(R.string.title_login);
        /**
         * 保存登录名
         */
        if (FrxsApplication.getInstance().getUserInfo() != null) {
            edtAccount.setText(FrxsApplication.getInstance().getUserInfo().getUserAccount());// 保存用户名
            edtAccount.setSelection(edtAccount.getText().length());// 光标位置移动到最后
        }

    }

    @Override
    protected void initData() {
//        FrxsApplication.getInstance().prepare4Update(this, false);
    }

    @Override
    protected void initEvent() {
        loginBtn.setOnClickListener(this);//登录事件
        /**
         * 暗门选择事件
         */
        envHiddenBtn.setOnClickListener(new View.OnClickListener() {
            int keyDownNum = 0;

            @Override
            public void onClick(View view) {
                keyDownNum++;
                if (keyDownNum == 9) {
                    ToastUtils.show(LoginActivity.this, "再点击1次进入环境选择模式");
                }
                if (keyDownNum == 10) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                    dialog.setTitle(getResources().getString(R.string.tips_environment, getEnvironmentName(FrxsApplication.getInstance().getEnvironment())));
                    dialog.setCancelable(false);
                    dialog.setItems(environments, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            if (FrxsApplication.getInstance().getEnvironment() == which) {
                                return;
                            }
                            if (which != 0) {
                                final AlertDialog verifyMasterDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                View contentView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_evironments, null);
                                final EditText pswEt = (EditText) contentView.findViewById(R.id.password_et);
                                contentView.findViewById(R.id.confirm_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (TextUtils.isEmpty(pswEt.getText().toString().trim())) {
                                            ToastUtils.show(LoginActivity.this, "密码不能为空！");
                                            return;
                                        }

                                        if (!pswEt.getText().toString().trim().equals(getString(R.string.str_psw))) {
                                            ToastUtils.show(LoginActivity.this, "密码错误！");
                                            return;
                                        }

                                        chooseEnvironment(which);
                                        FrxsApplication.getInstance().setEnvironment(which);//存储所选择环境
                                        FrxsApplication.getInstance().renewRestClient();
                                        verifyMasterDialog.dismiss();
                                    }
                                });

                                contentView.findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        verifyMasterDialog.dismiss();
                                    }
                                });
                                verifyMasterDialog.setView(contentView);
                                verifyMasterDialog.show();

                            } else {
                                chooseEnvironment(which);
                                FrxsApplication.getInstance().setEnvironment(which);//存储所选择环境
                                FrxsApplication.getInstance().renewRestClient();
                            }

                        }
                    });
                    dialog.setNegativeButton(getString(R.string.tips_dialog_cancle),
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                    keyDownNum = 0;
                }
            }
        });


    }


    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.login_commit_btn: {
                if (TextUtils.isEmpty(edtAccount.getText().toString().trim())) {
                    ToastUtils.show(this, R.string.tips_null_account);// 账号不能为空
                    shakeView(edtAccount);
                } else if (TextUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    ToastUtils.show(LoginActivity.this, R.string.tips_null_password);// 密码不能为空
                    shakeView(edtPassword);
                } else {
                    strUserName = edtAccount.getText().toString().trim();
                    strPassWord = edtPassword.getText().toString().trim();
                    if (InputUtils.isNumericOrLetter(strPassWord)) {
                        requestLogin();
                    } else {
                        ToastUtils.show(LoginActivity.this, getString(R.string.tips_input_limit));// 密码只能由数字、字母组成
                        shakeView(edtPassword);
                    }
                }
                break;
            }
        }
    }

    /**
     * 登录网络请求
     */

    private void requestLogin() {
        showProgressDialog();

        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("DeliverLogin"));
        params.put("UserAccount", strUserName);
        params.put("UserPwd", strPassWord);
        params.put("UserType", "2");
        getService().DeliverLogin(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<UserInfo>>() {
            @Override
            public void onResponse(ApiResponse<UserInfo> result, int code, String msg) {
                UserInfo userInfo = result.getData();
                if (null != userInfo) {
                    FrxsApplication.getInstance().setUserInfo(userInfo);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    ToastUtils.show(LoginActivity.this, "登录成功");
                } else {
                    ToastUtils.show(LoginActivity.this, result.getInfo());
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<UserInfo>> call, Throwable t) {
                dismissProgressDialog();
                ToastUtils.show(LoginActivity.this, "登录失败" + t.getMessage());
            }
        });
    }

    /**
     * 暗门选择提示内容
     */
    private String getEnvironmentName(int which) {
        String envName = "线上环境";
        switch (which) {
            case 0:// 线上环境
            {
                envName = "线上环境";
                break;
            }
            case 1:// 测试环境
            {
                envName = "测试环境" + Config.getBaseUrl();
                break;
            }
            case 2:// 预发布环境
            {
                envName = "预发布环境";
                break;
            }
            default:
                break;
        }

        return envName;
    }

    /**
     * 暗门环境
     */
    private void chooseEnvironment(int which) {
        switch (which) {
            case 0:// 线上环境
            case 1:// 测试环境
            case 2:// 预发布环境
            default:
                Config.networkEnv = 0;
                break;
        }
    }

    /**
     * 窗口抖动
     */
    private void shakeView(EditText edit) {
        DisplayUtil.shakeView(this, edit);
        edit.requestFocus();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
        dialog.show();
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);// 取消
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
                FrxsApplication.getInstance().exitApp(0);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();// 应用程序退出对话框
        }
        return super.onKeyDown(keyCode, event);
    }

}
