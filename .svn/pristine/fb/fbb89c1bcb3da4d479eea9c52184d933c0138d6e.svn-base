package com.frxs.delivery.fragment;

import android.view.View;

import com.ewu.core.base.BaseFragment;
import com.frxs.delivery.rest.service.ApiService;
import com.frxs.delivery.application.FrxsApplication;

public abstract class FrxsFragment extends BaseFragment {
    protected ApiService mService;

    public ApiService getService() {
        if (mService == null) {
            mService = FrxsApplication.getRestClient().getApiService();
        }

        return mService;
    }

    protected abstract int getLayoutId();

    protected abstract void initViews(View view);

    protected abstract void initEvent();

    protected abstract void initData();

    public void showProgressDialog() {
        if (isAdded()) {
            mActivity.showProgressDialog();
        }
    }

    public void dismissProgressDialog() {
        mActivity.dismissProgressDialog();
    }

}
