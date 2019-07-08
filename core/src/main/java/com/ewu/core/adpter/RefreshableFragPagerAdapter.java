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

public class RefreshableFragPagerAdapter extends FragmentPagerAdapter
{
	private int mChildCount = 0;

	private FragmentManager mFragmentManager;
	
    protected List<Fragment> mFrgs = null;

    /*
         * 用户获取当前viewpager的fragment，mFrgs更新之后，当前的frament并不会使用新new的fragment，而是复用之前的。所以这里使用hashmap要保存当前的fragment
         */
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    /**
     * @param fm
     */
    public RefreshableFragPagerAdapter(FragmentManager fm)
    {
        super(fm);
        mFragmentManager = fm;
    }
    
    /**
     * @param fm
     */
    public RefreshableFragPagerAdapter(FragmentManager fm, List<Fragment> frgs)
    {
        super(fm);
        mFragmentManager = fm;
        mFrgs = frgs;
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
	public void setPagerItems(List<Fragment> items) {
		if (items != null) {
			for (int i = 0; i < mFrgs.size(); i++) {
				mFragmentManager.beginTransaction().remove(mFrgs.get(i)).commitAllowingStateLoss();
			}
			
			mFrgs = items;
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
        if (null == mFrgs || mFrgs.size() == 0)
        {
           return null;
        }

        Fragment fragment = mFrgs.get(position);
        return fragment;
    }

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
        if (null == mFrgs || mFrgs.size() == 0)
        {
           return 0;
        }
        
        return mFrgs.size();
    }

}
