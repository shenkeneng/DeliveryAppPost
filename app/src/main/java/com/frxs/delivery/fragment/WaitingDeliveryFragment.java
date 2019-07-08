package com.frxs.delivery.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.ToastUtils;
import com.ewu.core.widget.slidingtabs.SlidingTabLayout;
import com.frxs.delivery.HomeActivity;
import com.frxs.delivery.LoadCarSortActivity;
import com.frxs.delivery.OrderDetailActivity;
import com.frxs.delivery.R;
import com.frxs.delivery.adapter.CartPagerAdapter;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.GetWaitDeliverInfo;
import com.frxs.delivery.model.PickingData;
import com.frxs.delivery.model.ShippingDataGrouped;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.model.WaitDeliverData;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import com.frxs.delivery.widget.NoScrollListView;
import com.frxs.delivery.zxing.CaptureActivity;
import com.google.gson.JsonObject;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * 等待配送 by Tiepier
 */
public class WaitingDeliveryFragment extends FrxsFragment implements SlidingTabLayout.TabAdapter {

    private TextView tvSore;

    private TextView tvTitle;//标题

    private TextView tvRefresh;//刷新

    private NoScrollListView lvDeliveryWaiting;//等待配送列表

    private NoScrollListView lvDeliveryPicking;//正在拣货列表

    private TextView tvOrderTotal;//总订单

    private TextView tvOrderAmount;//总金额

    private TextView tvOrderWaitingCount;

    private TextView tvOrderPickingCount;

    private GetWaitDeliverInfo waitDeliverInfo;

    private QuickAdapter<WaitDeliverData> waitingAdapter;

    private QuickAdapter<PickingData> pickingAdatper;

    private Dialog dialog;

    private TextView scanPromptTv;

    private LinearLayout scanLoadView;

    private LinearLayout orderLoadView;

    private TextView scanTv;//点击扫码获取装车订单

    private LinearLayout carOrderView;

    private SlidingTabLayout homeTabLayout;

    private ViewPager tabViewPager;

    protected CartPagerAdapter cartPagerAdapter;

    private TextView hintTv;

    private List<ShippingDataGrouped> shippingDataGrouped;

    private List<Fragment> mFraments = new ArrayList<Fragment>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_waiting_delivery;
    }

    @Override
    protected void initViews(View view) {
        // 标题栏
        tvRefresh = (TextView) view.findViewById(R.id.tv_title_left);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);//标题
        tvSore = (TextView) view.findViewById(R.id.tv_title_right);//刷新
        tvTitle.setText(R.string.title_waiting_delivery);//等待配送
        tvRefresh.setVisibility(View.VISIBLE);
        tvSore.setVisibility(View.GONE);

        // 扫码装车页面
        scanLoadView = (LinearLayout)view.findViewById(R.id.scan_load_layout);
        scanPromptTv = (TextView) view.findViewById(R.id.scan_prompt_tv);
        scanTv = (TextView) view.findViewById(R.id.scan_tv);

        // 正常配送页面5
        orderLoadView = (LinearLayout)view.findViewById(R.id.order_load_layout);
        tvOrderTotal = (TextView) view.findViewById(R.id.tv_order_total);//总订单
        tvOrderAmount = (TextView) view.findViewById(R.id.tv_order_amount);//总金额
        tvOrderWaitingCount = (TextView) view.findViewById(R.id.tv_order_waiting_count);
        tvOrderPickingCount = (TextView) view.findViewById(R.id.tv_order_picking_count);
        lvDeliveryWaiting = (NoScrollListView) view.findViewById(R.id.lv_waiting_delivery);//等待配送
        lvDeliveryPicking = (NoScrollListView) view.findViewById(R.id.lv_picking_delivery);//正在拣货
        lvDeliveryWaiting.setFocusable(false);
        // 货配车页面
        carOrderView = (LinearLayout)view.findViewById(R.id.ll_cart_order);
        homeTabLayout = (SlidingTabLayout) view.findViewById(R.id.home_tab_layout);
        tabViewPager = (ViewPager) view.findViewById(R.id.tab_view_pager);
        hintTv = (TextView) view.findViewById(R.id.tv_hint);

    }

    /**
     * 设置订单数量
     */
    private void setValue(GetWaitDeliverInfo waitDeliverInfo) {
        tvOrderTotal.setText("总订单:" + waitDeliverInfo.getTotalCount());//总订单
        tvOrderAmount.setText("总金额:" + MathUtils.twolittercountString(waitDeliverInfo.getTotalAmount()));
        tvOrderWaitingCount.setText("可配送的订单数：" + waitDeliverInfo.getWaitDeliverCount());//等待配送的订单数
        tvOrderPickingCount.setText("等待配送的订单数：" + waitDeliverInfo.getPickingCount());//正在拣货的订单数
    }

    @Override
    protected void initEvent() {
        tvRefresh.setOnClickListener(this);
        scanTv.setOnClickListener(this);
        tvSore.setOnClickListener(this);
        homeTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                cartPagerAdapter.setmCurrentFragment(position);
                notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void initData() {
        final UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (userInfo.getIsEnabledFreightCar() == 0 || userInfo.getIsMaster().equals("1")) {
            /**
             * 等待配送
             */
            waitingAdapter = new QuickAdapter<WaitDeliverData>(mActivity, R.layout.item_waiting_delivery) {

                @Override
                protected void convert(BaseAdapterHelper helper, final WaitDeliverData item) {
                    /**
                     * 判断当前账号是否是组长（是组长隐藏操作按钮）
                     */
                    String isMaster = userInfo.getIsMaster();
                    if (!TextUtils.isEmpty(isMaster)) {
                        if (isMaster.equals("1")) {
                            helper.setVisible(R.id.ll_submit, false);
                        } else {
                            helper.setVisible(R.id.ll_submit, true);
                        }
                    }

                    /**
                     * 结算金额
                     */
                    helper.setText(R.id.tv_pay_amount, "金额：" + MathUtils.twolittercountString(item.getPayAmount()));

                    /**
                     * 流水号处理
                     */
                    String strStationNumber = item.getStationNumber();
                    if (strStationNumber.equals("")) {
                        strStationNumber = "0";
                    }
                    helper.setText(R.id.tv_order_id, "订单：" + String.valueOf(item.getOrderId()));//订单编号
                    helper.setText(R.id.tv_store_name, "门店：" + item.getShopName());//门店名称
                    helper.setText(R.id.tv_order_count, String.format("%1$03d", Integer.valueOf(strStationNumber)));//流水号
                    helper.setText(R.id.tv_store_info, "店主：" + item.getRevLinkMan() + " " + item.getRevTelephone());//门店信息

                    /**
                     * 结算方式显示处理
                     */
                    if (item.getSettleTypeName().toString().equals("司机带回")) {
                        helper.setTextColor(R.id.tv_payment_name, Color.parseColor("#d80c18"));
                    } else {
                        helper.setTextColor(R.id.tv_payment_name, Color.parseColor("#00cc66"));
                    }
                    helper.setText(R.id.tv_payment_name, item.getSettleTypeName());//结算方式

                    /**
                     * 拨打电话
                     */
                    helper.setOnClickListener(R.id.img_call, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getRevTelephone()));
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            mActivity.startActivity(intent);
                        }
                    });
                    /**
                     * 查看详情
                     */
                    helper.setOnClickListener(R.id.img_right, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (CommonUtils.isFastDoubleClick()) {
                                return;
                            }
                            Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                            intent.putExtra("ORDERID", item.getOrderId());//订单详情所需参数：订单ID
                            startActivity(intent);
                        }
                    });

                    /**
                     * 完成装车
                     */
                    helper.setOnClickListener(R.id.tv_call, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (CommonUtils.isFastDoubleClick()) {
                                return;
                            }
                            showDialog(item.getOrderId());
                        }
                    });
                }
            };

            /**
             * 正在拣货
             */
            pickingAdatper = new QuickAdapter<PickingData>(mActivity, R.layout.item_picking_delivery) {
                @Override
                protected void convert(BaseAdapterHelper helper, PickingData item) {
                    helper.setText(R.id.tv_pay_amount, "金额：" + MathUtils.twolittercountString(item.getPayAmount()));
                    helper.setText(R.id.tv_order_id, "订单：" + String.valueOf(item.getOrderId()));
                    helper.setText(R.id.tv_store_name, "门店：" + item.getShopName());
                    if (item.getStationNumber() == null) {
                        helper.setVisible(R.id.tv_station_num, false);
                    } else {
                        helper.setVisible(R.id.tv_station_num, true);
                        helper.setText(R.id.tv_station_num, String.format("%1$03d", item.getStationNumber()));
                    }
                    List<PickingData.ShelfAreaListBean> shelfAreaList = item.getShelfAreaList();
                    if (shelfAreaList != null) {
                        StringBuilder shelfAreaFlag = new StringBuilder("");
                        for (int i = 0; i < shelfAreaList.size(); i++) {
                            if (i == 3) {
                                shelfAreaFlag.append("\r\n");
                            }
                            shelfAreaFlag.append(shelfAreaList.get(i).getShelfAreaName());
                            switch (shelfAreaList.get(i).getFlag()) {
                                case 1: {// 未拣货
                                    shelfAreaFlag.append("(○)   ");
                                    break;
                                }

                                case 2: {// 进行中
                                    shelfAreaFlag.append("(△)   ");
                                    break;
                                }

                                case 3: {// 已拣货
                                    shelfAreaFlag.append("(√)   ");
                                    break;
                                }
                            }
                        }
                        helper.setText(R.id.tv_shlef_type, shelfAreaFlag.toString());
                    } else {
                        helper.setVisible(R.id.tv_shlef_type, false);
                    }
                }
            };

            lvDeliveryWaiting.setAdapter(waitingAdapter);
            lvDeliveryPicking.setAdapter(pickingAdatper);
            lvDeliveryPicking.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (CommonUtils.isFastDoubleClick()) {
                        return;
                    }
                    PickingData item = pickingAdatper.getItem(position);
                    Intent intent = new Intent(mActivity, OrderDetailActivity.class);
                    intent.putExtra("ORDERID", item.getOrderId());//订单详情所需参数：订单ID
                    startActivity(intent);
                }
            });
        } else {
            final int[] colors={0xFFFFFFFF,0xFF654321,0xFF336699};
            cartPagerAdapter = new CartPagerAdapter(getChildFragmentManager(), mFraments);
            tabViewPager.setAdapter(cartPagerAdapter);
            homeTabLayout.setCustomTabView(R.layout.view_sliding_tab_item, R.id.textview);
            homeTabLayout.setDistributeEvenly(true);
            homeTabLayout.setTabAdapter(this);
            homeTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.red));
            homeTabLayout.setViewPager(tabViewPager);
            homeTabLayout.setSelectedIndicatorHeight(3);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && null != cartPagerAdapter && null != cartPagerAdapter.getCurrentFragment()) {
            cartPagerAdapter.getCurrentFragment().notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }


    @Override
    public void onClick(View view) {
        if (CommonUtils.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_title_left:
                requestData();
                break;
            case R.id.scan_tv:// 6.0-7.0系统扫码前判断是否获取相机权限
                if (((HomeActivity) mActivity).hasCameraPermissions()) {
                    ToastUtils.show(mActivity, "正在启动...");
                    Intent intent = new Intent(mActivity, CaptureActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_title_right:
                showShopSortDialog();
                break;
            default:
                break;
        }

    }

    private void requestData() {
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (null == userInfo) {
            ToastUtils.show(mActivity, "数据异常，没有找到用户信息");
            return;
        }

        if (!userInfo.isMaster() && userInfo.getIsShippingCar() != 0) {
            if (1 == userInfo.getIsEnabledFreightCar()) { //不是组长、不是外协司机、启用货配车（0:不启用; 1:启用）
                requestNeedScanForLoading();
            } else {// （不是组长、不启用货配车 -> 判断是否有超过时间未完成配送的订单）
                reqQueryOverTimeDeliverOrder(null);
            }
        } else { //（是组长、外协司机 -> 请求等待配送订单列表）
            if (userInfo.getIsShippingCar() == 0) {
                reqQueryOverTimeDeliverOrder(null);
            } else {
                reqDeliveryWaiting();
            }
        }
    }

    private void requestNeedScanForLoading() {
        showProgressDialog();
        final UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("ShippingUserID", userInfo.getEmpID()); //配送员ID
        params.put("WID", userInfo.getWareHouseWID()); //仓库ID

        getService().CountUnfinishedOrderAndShippingNumber(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<JsonObject>>() {
            @Override
            public void onResponse(ApiResponse<JsonObject> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    JsonObject jsonData = result.getData();
                    if (null != jsonData) {
                        int unfinishOrderNumber = jsonData.get("UnfinishOrderNumber").getAsInt();
                        orderLoadView.setVisibility(View.GONE);
                        carOrderView.setVisibility(View.GONE);
                        if (unfinishOrderNumber > 0) {
                            tvRefresh.setVisibility(View.VISIBLE);
                            tvSore.setVisibility(View.VISIBLE);
                            scanLoadView.setVisibility(View.GONE);
                            tvTitle.setText(R.string.title_waiting_delivery);//等待配送
                            if (!userInfo.isMaster()) {//（不是组长 -> 判断是否有超过时间未完成配送的订单）
                                reqQueryOverTimeDeliverOrder(null);
                            } else {//（是组长 -> 请求等待配送订单列表）
                                reqDeliveryWaiting();
                            }
                            return;
                        } else {
                            scanLoadView.setVisibility(View.VISIBLE);
                            tvRefresh.setVisibility(View.INVISIBLE);
                            tvSore.setVisibility(View.INVISIBLE);
                            tvTitle.setText(R.string.title_scan_load);//扫码装车
                            int deliveryTrain = jsonData.get("ShippingSerialNumber").getAsInt();
                            scanPromptTv.setText(Html.fromHtml(String.format(getString(R.string.scan_prompt), userInfo.getEmpName(), deliveryTrain)));
                        }
                    } else {
                        ToastUtils.show(mActivity, "数据返回错误：没有返回任何数据");
                    }
                } else {
                    ToastUtils.show(mActivity, "判断是否需要扫描装车失败：" + result.getInfo());
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<JsonObject>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtils.show(mActivity, "请求失败：" + t.getMessage());
                dismissProgressDialog();
            }
        });
    }

    /**
     * 等待配送数据请求
     */
    public void reqDeliveryWaiting() {
        showProgressDialog();
        final UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetWaitDeliverInfo"));
        if (!TextUtils.isEmpty(userInfo.getIsMaster())) {
            if (userInfo.getIsMaster().equals("1")) {
                params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
            } else {
                params.put("EmpId", String.valueOf(userInfo.getEmpID()));
                params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
                params.put("LineIDs", userInfo.getLineIDs());
                //params.put("SortType", sortType);
            }
        }
        getService().GetWaitDeliverInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<GetWaitDeliverInfo>>() {
            @Override
            public void onResponse(ApiResponse<GetWaitDeliverInfo> result, int code, String msg) {
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        waitDeliverInfo = result.getData();
                        if (waitDeliverInfo != null) {
                            if (userInfo.getIsEnabledFreightCar() == 0 || userInfo.getIsMaster().equals("1")) {
                                orderLoadView.setVisibility(View.VISIBLE);
                                setValue(waitDeliverInfo);
                                //等待配送
                                List<WaitDeliverData> wd = waitDeliverInfo.getWaitDeliverData();
                                waitingAdapter.replaceAll(wd);
                                //正在拣货
                                List<PickingData> pd = waitDeliverInfo.getPickingData();
                                pickingAdatper.replaceAll(pd);

                                if (!(Integer.valueOf(waitDeliverInfo.getTotalCount()) > 0)) {
                                    ToastUtils.show(mActivity, "等待配送列表暂无订单");
                                }
                            } else {
                                orderLoadView.setVisibility(View.GONE);
                                if (waitDeliverInfo.getShippingDataGrouped() != null && waitDeliverInfo.getShippingDataGrouped().size() > 0) {
                                    shippingDataGrouped = waitDeliverInfo.getShippingDataGrouped();
                                    updateAdvertisements();
                                    hintTv.setVisibility(View.GONE);
                                    carOrderView.setVisibility(View.VISIBLE);
                                    tvSore.setVisibility(View.VISIBLE);
                                }else {
                                    tvSore.setVisibility(View.GONE);
                                    hintTv.setVisibility(View.VISIBLE);
                                    carOrderView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<GetWaitDeliverInfo>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });

    }

    /**
     * 完成装车数据请求
     */
    private void reqCompleteLoad(String strOrderID) {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("SetDeliverStatus"));
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("OrderId", strOrderID);
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("EmpName", userInfo.getEmpName());
        getService().SetDeliverStatus(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Object>>() {

            @Override
            public void onResponse(ApiResponse<Object> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    reqDeliveryWaiting();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(result.getInfo());
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(mActivity, "装车失败");
            }
        });
    }

    /**
     * 完成装车对话框
     */
    public void showDialog(final String strOrderID) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_selector);
        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
        dialog.show();
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);// 取消
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                reqQueryOverTimeDeliverOrder(strOrderID);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 查询有没有超过规则时间未完成配送的订单
     */
    private void reqQueryOverTimeDeliverOrder(final String strOrderID) {
        showProgressDialog();
        final UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("EmpID", userInfo.getEmpID());
        params.put("WID", userInfo.getWareHouseWID());
        getService().GetOverTimeDeliverOrder(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(ApiResponse<Integer> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() > 0) {
                        dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
                        dialog.setContentView(R.layout.dialog_selector);
                        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
                        tvMessage.setText(getResources().getString(R.string.dialog_not_delivery));
                        dialog.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
                        dialog.show();
                        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
                        btnConfirm.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                FragmentTabHost mTabHost = (FragmentTabHost) mActivity.findViewById(R.id.tabhost);
                                mTabHost.setCurrentTab(1);
                            }
                        });
                    } else if (null != strOrderID) {
                        reqCompleteLoad(strOrderID);//完成装车
                    } else {
                        reqDeliveryWaiting();
                    }
                } else if (null != strOrderID) {
                    ToastUtils.show(mActivity, result.getInfo());
                } else {
                    reqDeliveryWaiting();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                if (!TextUtils.isEmpty(strOrderID)) {
                    ToastUtils.show(mActivity, t.getMessage());
                } else {
                    reqDeliveryWaiting();
                }
            }
        });
    }

    private void updateAdvertisements() {
        if (null != shippingDataGrouped || shippingDataGrouped.size() > 0) {
            mFraments.clear();
            mFraments = new ArrayList<Fragment>();

            for (int i = 0; i < shippingDataGrouped.size(); i++) {
                ShippingDataFragment cateGoodsListfragment = new ShippingDataFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product_list", (Serializable)shippingDataGrouped.get(i).getShippingData());
                bundle.putSerializable("index", i);
                cateGoodsListfragment.setArguments(bundle);
                mFraments.add(cateGoodsListfragment);
            }

            cartPagerAdapter.setPagerItems(mFraments);
            homeTabLayout.notifyDataSetChanged();
            cartPagerAdapter.notifyDataSetChanged();
        }
    }

    public List<ShippingDataGrouped.ShippingData> getFragmentProductList(int index) {
        if (null != shippingDataGrouped && shippingDataGrouped.size() > index) {
            return shippingDataGrouped.get(index).getShippingData();
        }

        return null;
    }

    protected void notifyDataSetChanged() {
        ShippingDataFragment currentFrg = cartPagerAdapter.getCurrentFragment();
        if (null != currentFrg) {
            currentFrg.notifyDataSetChanged();
        }
    }

    /**
     * 显示排序对话框
     */
    private void showShopSortDialog() {
        View popView = LayoutInflater.from(mActivity).inflate(R.layout.dialog_shop_sort, null);
        TextView stationSort = (TextView) popView.findViewById(R.id.tv_station_sort);
        TextView shopSort = (TextView) popView.findViewById(R.id.tv_shop_sort);

        final PopupWindow window = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 使其聚集
        window.setFocusable(true);
        // 设置允许在外点击消失
        window.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置基于某控件下方弹出
        window.showAsDropDown(tvSore, 0, 0);
        // 点击弹出后设置阴影
        setBackgroundAlpha(true);
        stationSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadCarSortInfo();
                window.dismiss();
            }
        });

        shopSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, LoadCarSortActivity.class);
                if (shippingDataGrouped.get(tabViewPager.getCurrentItem()).getGroupNumber() > 0) {
                    intent.putExtra("from", "wait");
                    intent.putExtra("car_num", shippingDataGrouped.get(tabViewPager.getCurrentItem()).getGroupNumber());
                    intent.putExtra("ShipDate", shippingDataGrouped.get(tabViewPager.getCurrentItem()).getShipDate());//TODO:
                }
                startActivity(intent);
                window.dismiss();
            }
        });

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            setBackgroundAlpha(false);
                                        }
                                    }
        );


    }

    public void setBackgroundAlpha(boolean isShow) {
        if (isShow) {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.alpha = 0.5f; //0.0-1.0
            mActivity.getWindow().setAttributes(lp);
        } else {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.alpha = 1f;
            mActivity.getWindow().setAttributes(lp);
        }
    }

    /**
     * 设置为待装区排序
     */
    private void setLoadCarSortInfo() {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("ShippingSerialNumber", shippingDataGrouped.get(tabViewPager.getCurrentItem()).getGroupNumber());
        params.put("ShipDate", shippingDataGrouped.get(tabViewPager.getCurrentItem()).getShipDate());//TODO:
        params.put("IsStationNumber", 1);
        getService().SetLoadCarSortInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    reqDeliveryWaiting();
                } else {
                    ToastUtils.show(mActivity, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtils.show(mActivity, t.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public CharSequence getTitle(int position) {
        if (shippingDataGrouped.size() > position) {
            String carNum = String.format(getString(R.string.car_sort), String.valueOf(shippingDataGrouped.get(position).getGroupNumber()));
            return carNum;
        } else {
            return "暂无车次";
        }
    }

    @Override
    public int getImageId(int position) {
        return R.mipmap.ic_launcher;
    }

    @Override
    public int getTabWidth(int position) {
        return 0;
    }

    @Override
    public void loadBitmap(View imageView, int position, boolean isSelect) {

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
