package com.ewu.core.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.ewu.core.R;

/**
 * Created by ewu on 2016/2/18.
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(getLayoutId());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialog));// 正在加载数据，请稍等......

        initViews();
        initEvent();
        initData();


    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void initEvent();

    protected abstract void initData();


    @Override
    public void onClick(View view) {

    }

    public void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }
}
