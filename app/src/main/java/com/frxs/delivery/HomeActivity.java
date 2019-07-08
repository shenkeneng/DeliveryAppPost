package com.frxs.delivery;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.ewu.core.utils.EasyPermissionsEx;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.fragment.BeingDeliveryFragment;
import com.frxs.delivery.fragment.MineFragment;
import com.frxs.delivery.fragment.SalesReturnFragment;
import com.frxs.delivery.fragment.WaitingDeliveryFragment;
import com.frxs.delivery.zxing.CaptureActivity;

/**
 * 首页 by Tiepier
 */

public class HomeActivity extends FrxsActivity {

    private FragmentTabHost mTabHost;

    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {WaitingDeliveryFragment.class,
            BeingDeliveryFragment.class, SalesReturnFragment.class, MineFragment.class};

    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_waiting_icon_selector,
            R.drawable.tab_being_icon_selector, R.drawable.tab_return_icon_selector, R.drawable.tab_mine_icon_selector};

    // Tab选项卡的文字
    private String mTextviewArray[] = {"等待配送", "正在配送", "门店退货", "关于我"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initViews() {

        mTabHost = (FragmentTabHost) findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_content_layout);

        // 得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景
//            mTabHost.getTabWidget().getChildAt(i)
//                    .setBackgroundResource(R.drawable.main_tab_item_bg);
        }
    }

    @Override
    protected void initData() {
//        FrxsApplication.getInstance().prepare4Update(this, false);
    }

    @Override
    protected void initEvent() {
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.view_tab_item, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_title_right: {
                break;
            }
            default:
                break;
        }
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

    public void setCurrentTab(int index) {
        mTabHost.setCurrentTab(index);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            int index = intent.getIntExtra("TAB", -1);
            if (index != -1) {
                setCurrentTab(index);
            }
        }
    }

    public Fragment getCurrentFragment() {
        int currentTab = mTabHost.getCurrentTab();
        return getFragment(currentTab);
    }

    public Fragment getFragment(int tabIndex) {
        return getSupportFragmentManager().findFragmentByTag(mTextviewArray[tabIndex]);
    }
}
