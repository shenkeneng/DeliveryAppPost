package com.frxs.delivery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.ewu.core.adpter.RefreshableFragPagerAdapter;
import com.frxs.delivery.fragment.ShippingDataFragment;
import java.util.List;

/**
 * Created by Chentie on 2017/5/10.
 */

public class CartPagerAdapter extends RefreshableFragPagerAdapter {

    private int currentPosition = 0;

    /**
     * @param fm
     */
    public CartPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * @param fm
     */
    public CartPagerAdapter(FragmentManager fm, List<Fragment> frgs) {
        super(fm, frgs);
    }

    public void setmCurrentFragment(int position) {
        currentPosition = position;
    }

    public int getCurrentFragmentPosition() {
        return currentPosition;
    }

    public ShippingDataFragment getCurrentFragment() {
        return (ShippingDataFragment) getFragment(currentPosition);
    }

    public boolean checkCanDoRefresh() {
        ShippingDataFragment currentFragment = getCurrentFragment();
        if (currentFragment == null) {
            return true;
        }
        return currentFragment.checkCanDoRefresh();
    }
}
