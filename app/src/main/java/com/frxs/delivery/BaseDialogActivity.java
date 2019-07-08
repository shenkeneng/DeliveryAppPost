package com.frxs.delivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;


import java.lang.reflect.Field;

public abstract class BaseDialogActivity extends FrxsActivity
{
	
	private WindowManager wManager;
	
	private LayoutParams layoutParams;

	private ImageView dissView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);
		// 设置窗口显示位置
		DisplayMetrics dm = getResources().getDisplayMetrics();
		LayoutParams lp = getWindow().getAttributes();
		lp.width = dm.widthPixels;
		lp.height = dm.heightPixels * 2 / 3;
		lp.gravity = Gravity.BOTTOM;
		getWindow().setAttributes(lp);

		setFinishOnTouchOutside(true);

		dissView = new ImageView(this);
		dissView.setImageResource(R.mipmap.icon_delete_view);

		// 获得控件的尺寸
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		dissView.measure(w, h);
		int height = dissView.getMeasuredHeight();

		wManager = getWindowManager();
		Point outSize = new Point();
		wManager.getDefaultDisplay().getSize(outSize);

		// 获得状态栏的尺寸
		int statusBarHeight = getStatusBarHeight(getApplicationContext());
		layoutParams = new LayoutParams();
		
		// 设置悬浮窗的尺寸，为自适应
		layoutParams.width = LayoutParams.WRAP_CONTENT;
		layoutParams.height = LayoutParams.WRAP_CONTENT;
		
		// 设置悬浮窗图片背景色为透明
		layoutParams.format = PixelFormat.RGBA_8888;
		
		// 设置悬浮窗之外的界面可以操作
		layoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		
		// 计算悬浮窗的位置（x,y轴）
		// X轴=屏幕的宽度的20分之1，假设屏幕宽度为1080，即x轴：54=1080/20
		layoutParams.x = outSize.x / 20;
		// Y轴=屏幕的高度-弹出Activity的高度-状态栏的高度+悬浮窗高度的一半，假设屏幕高度为1920，即Y轴：616=1920-1280-60+36
		layoutParams.y = outSize.y - (outSize.y * 2 / 3) - (statusBarHeight + height / 2);
		
		// 设置悬浮窗的摆放位置
		layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
		// 添加视图及属性
		wManager.addView(dissView, layoutParams);//qwtypsdfghjkzxcbm
		
		dissView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				wManager.removeView(dissView);
				finish();
			}
		});
		
	}
	
	// 获取手机状态栏高度
	public static int getStatusBarHeight(Context context)
	{
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return statusBarHeight;
	}
}
