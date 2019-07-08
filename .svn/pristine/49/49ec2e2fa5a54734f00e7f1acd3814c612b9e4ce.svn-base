package com.ewu.core.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ewu.core.R;

public abstract class BaseFragment extends Fragment implements OnClickListener
{
	
	protected BaseActivity mActivity;
	
	protected ProgressDialog progressDialog;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(getLayoutId(), container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		initViews(view);
		initEvent();
		initData();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		mActivity = (BaseActivity) activity;
		progressDialog = new ProgressDialog(mActivity);
		progressDialog.setMessage(getString(R.string.progress_dialog));// 正在加载数据，请稍等......
	}

	protected abstract int getLayoutId();
	protected abstract void initViews(View view);
	protected abstract void initEvent();
	protected abstract void initData();

}
