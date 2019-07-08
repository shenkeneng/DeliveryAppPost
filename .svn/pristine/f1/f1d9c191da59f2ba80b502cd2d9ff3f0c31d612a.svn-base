/**
 * <p>
 * Copyright: Copyright (c) 2014
 * Company: ZTE
 * Description: 这里写这个文件是干什么用的
 * </p>
 * @Title AccessoriesPagerAdapter.java
 * @Package com.hbcloud.haojihui.adapter
 * @version 1.0
 * @author wsy
 * @date 2014-11-6
 */
package com.ewu.core.adpter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;

/** 
 * 某某某类
 * @ClassName:AccessoriesPagerAdapter 
 * @Description: 这里用一句话描述这个类的作用 
 * @author: ewu
 * @date: 2014-11-6
 *  
 */

public abstract class RefreshableFragPagerAdapterEx<T> extends FragmentPagerAdapter
{
	private int mChildCount = 0;

	protected FragmentManager mFragmentManager;

    protected List<List<T>> dataArrayList;

    /*
         * 用户获取当前viewpager的fragment，mFrgs更新之后，当前的frament并不会使用新new的fragment，而是复用之前的。所以这里使用hashmap要保存当前的fragment
         */
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    /**
     * @param fm
     */
    public RefreshableFragPagerAdapterEx(FragmentManager fm)
    {
        super(fm);
        mFragmentManager = fm;
    }

    /**
     * @param fm
     */
    public RefreshableFragPagerAdapterEx(FragmentManager fm, List<List<T>> dataList)
    {
        super(fm);
        mFragmentManager = fm;
        this.dataArrayList = dataList;
    }

    @Override
    public int getItemPosition(Object object)
    {
    	if (mChildCount > 0) {
    		mChildCount--;
    		return POSITION_NONE;
    	}
        return super.getItemPosition(object);
    }

    public Fragment getFragment(int position) {
        return registeredFragments.get(position);
    }
    
    /**
     * 重新设置页面内容
     * @param items
     */
	public void setPagerItems(List<List<T>> items) {
		if (items != null) {
//            List<Fragment> fragments = mFragmentManager.getFragments();
//            if (null != fragments && fragments.size() > 0) {
//                for (int i = 0; i < fragments.size(); i++) {
//                    mFragmentManager.beginTransaction().remove(fragments.get(i)).commitAllowingStateLoss();
//                }
//            }

//            if (items.size() == 1){
//                for (int i = 0; i < 2; i++) {
//                    mFragmentManager.beginTransaction().remove(mFrgs.get(i));
//                }
//            }

            this.dataArrayList = items;
			mChildCount = getCount();
		}
	}
    
   
    /**
     * 这里写方法名
     * <p>
     * Description: 这里用一句话描述这个方法的作用
     * <p>
     * @date 2014-11-6
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position)
    {
        return super.getPageTitle(position);
    }

    /**
     * 这里写方法名
     * <p>
     * Description: 这里用一句话描述这个方法的作用
     * <p>
     * @date 2014-11-6
     * @param arg0
     * @return
     */
    @Override
    public Fragment getItem(int position)
    {
        if (null == dataArrayList || dataArrayList.size() == 0 || dataArrayList.size() < position)
        {
           return null;
        }

        Fragment fragment = newFragmentInstance(position, dataArrayList.get(position));
        return fragment;
    }

    protected abstract Fragment newFragmentInstance(int position, List<T> dataList);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        registeredFragments.remove(position);
    }

    /**
     * 这里写方法名
     * <p>
     * Description: 这里用一句话描述这个方法的作用
     * <p>
     * @date 2014-11-6
     * @return
     */
    @Override
    public int getCount()
    {
        if (null == dataArrayList || dataArrayList.size() == 0)
        {
           return 0;
        }

//        if (dataArrayList.size() == 2)
//        {
//            return 1;
//        }

        return dataArrayList.size();
    }

}
