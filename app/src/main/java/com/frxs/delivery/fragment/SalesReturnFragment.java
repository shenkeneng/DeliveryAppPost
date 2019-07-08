package com.frxs.delivery.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.frxs.delivery.R;
import com.frxs.delivery.widget.PagerSlidingTabStrip;

/**
 * Created by shenpei on 2017/6/6.
 * 退货管理TAB页
 */

public class SalesReturnFragment extends FrxsFragment {

    private WaitReceivFragment waitReceivFragment;// 等待收货

    private ReceivedFragment ReceivedFragment;// 正在收货

    private PagerSlidingTabStrip salesTablayout;

    private ViewPager salesViewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sales_return;
    }

    @Override
    protected void initViews(View view) {
        view.findViewById(R.id.tv_title_left).setVisibility(View.GONE);// 隐藏回退按钮
        view.findViewById(R.id.tv_title_right).setOnClickListener(this);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_title);
        titleTv.setText("门店退货");
        salesTablayout = (PagerSlidingTabStrip) view.findViewById(R.id.sales_tablayout);
        salesViewPager = (ViewPager) view.findViewById(R.id.sales_pager);
        salesViewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        salesTablayout.setViewPager(salesViewPager);
        setTabsValue();
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_right: {// 刷新数据
                if (waitReceivFragment != null) {
                    waitReceivFragment.refresDate();
                }
                if (ReceivedFragment != null) {
                    ReceivedFragment.refresDate();
                }
                break;
            }
        }
    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        salesTablayout.setShouldExpand(true);
        salesTablayout.setDividerColor(Color.parseColor("#e6e6e6"));
        salesTablayout.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        salesTablayout.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        salesTablayout.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
        salesTablayout.setIndicatorColor(Color.parseColor("#DB251F"));
        salesTablayout.setSelectedTextColor(Color.parseColor("#DB251F"));
        salesTablayout.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private String tabTitles[] = new String[]{getResources().getString(R.string.waiting_receiv), getResources().getString(R.string.received)};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    if (null == waitReceivFragment) {
                        waitReceivFragment = new WaitReceivFragment();
                    }
                    return waitReceivFragment;
                }
                case 1: {
                    if (null == ReceivedFragment) {
                        ReceivedFragment = new ReceivedFragment();
                    }
                    return ReceivedFragment;
                }
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
