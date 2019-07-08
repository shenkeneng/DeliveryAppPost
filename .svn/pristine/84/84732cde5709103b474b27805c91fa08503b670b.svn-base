
package com.frxs.delivery.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author cate 2014-12-20 下午2:33:54
 */

public class NoScrollGridView extends GridView
{
	
	public NoScrollGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public NoScrollGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public NoScrollGridView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
	// int childCount = getChildCount();
	// int x = 0;
	// int y = 0;
	// int row = 0;
	//
	// for (int index = 0; index < childCount; index++) {
	// final View child = getChildAt(index);
	// if (child.getVisibility() != View.GONE) {
	// child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
	// // 此处增加onlayout中的换行判断，用于计算所需的高度
	// int width = child.getMeasuredWidth();
	// int height = child.getMeasuredHeight();
	//
	// x += width;
	// y = row * height + height;
	// if (x > maxWidth) {
	// x = width;
	// row++;
	// y = row * height + height;
	// }
	// }
	// }
	// // 设置容器所需的宽度和高度
	// setMeasuredDimension(maxWidth, y);
	// }
	//
	// @Override
	// protected void onLayout(boolean changed, int l, int t, int r, int b) {
	// final int childCount = getChildCount();
	// int maxWidth = r - l;
	// int x = 0;
	// int y = 0;
	// int row = 0;
	// for (int i = 0; i < childCount; i++) {
	// final View child = this.getChildAt(i);
	// if (child.getVisibility() != View.GONE) {
	// int width = child.getMeasuredWidth();
	// int height = child.getMeasuredHeight();
	// x += width+DensityUtils.dip2px(getContext(), 20);
	// y = row * height + height;
	// if (x > maxWidth) {
	// x = width;
	// row++;
	// y = row * height + height+DensityUtils.dip2px(getContext(), 10);
	// }
	// child.layout(x - width, y - height, x, y);
	// }
	// }
	// }
	
}
