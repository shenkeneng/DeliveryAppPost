package com.frxs.delivery.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.LogUtils;
import com.frxs.delivery.HomeActivity;
import com.frxs.delivery.OrderDetailActivity;
import com.frxs.delivery.R;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.ShippingDataGrouped;
import com.frxs.delivery.utils.MathUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chentie on 2017/10/28.
 */

public class ShippingDataFragment extends FrxsFragment {

    private ListView preProductLv;

    protected QuickAdapter<ShippingDataGrouped.ShippingData> shippingOrderAdapter;

    private TextView carInfoTv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_car_order;
    }

    @Override
    protected void initViews(View view) {
        preProductLv = (ListView) view.findViewById(R.id.lv_shipping);
        carInfoTv = (TextView) view.findViewById(R.id.tv_car_info);

    }

    private void setValue(List<ShippingDataGrouped.ShippingData> shippingData) {
        double totalPoint = 0.0;
        double totalPointEx = 0.0;
        double maxShopDistance = 0.0;
        int shopCount = 0;
        List<String> shopIds = new ArrayList<String>();
        for (ShippingDataGrouped.ShippingData data : shippingData) {
            totalPoint = MathUtils.add(totalPoint, data.getTotalBasePoint());
            totalPointEx = MathUtils.add(totalPointEx, data.getBasePointExt());
            maxShopDistance = maxShopDistance > data.getShopDistance() ? maxShopDistance : data.getShopDistance();
            if (!shopIds.contains(String.valueOf(data.getShopID()))) {
                shopCount++;
                shopIds.add(String.valueOf(data.getShopID()));
            }
        }
        carInfoTv.setText(String.format(getString(R.string.tv_car_info), String.valueOf(shippingData.size()),
                String.valueOf(shopCount), totalPoint, totalPointEx, maxShopDistance));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            shippingOrderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void initEvent() {

    }


    @Override
    protected void initData() {
        WaitingDeliveryFragment waitingDeliveryFragment = (WaitingDeliveryFragment) ((HomeActivity) mActivity).getFragment(0);
        int index = getArguments().getInt("index", 0);
        List<ShippingDataGrouped.ShippingData> shippingData = waitingDeliveryFragment.getFragmentProductList(index);
        if (shippingData != null && shippingData.size() > 0) {
            setValue(shippingData);
            shippingOrderAdapter = new QuickAdapter<ShippingDataGrouped.ShippingData>(mActivity, R.layout.item_order_car) {
                @Override
                protected void convert(BaseAdapterHelper helper, final ShippingDataGrouped.ShippingData item) {
                    /**
                     * 判断当前账号是否是组长（是组长隐藏操作按钮）
                     */
                    String isMaster = FrxsApplication.getInstance().getUserInfo().getIsMaster();
                    if (!TextUtils.isEmpty(isMaster)) {
                        if (isMaster.equals("1")) {
                            helper.getView(R.id.ll_submit).setVisibility(View.GONE);
                        } else {
                            helper.getView(R.id.ll_submit).setVisibility(View.VISIBLE);
                        }
                    }
                    if (item.getShippingType() == 1) {// 0：可装车 1：不可装车
                        helper.setBackgroundRes(R.id.tv_call, R.drawable.shape_gray_rectangle);
                        helper.getView(R.id.rl_store_info).setVisibility(View.GONE);
                    } else {
                        helper.setBackgroundRes(R.id.tv_call, R.drawable.shape_bule_rectangle);
                        helper.getView(R.id.rl_store_info).setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_store_info, "店主：" + item.getRevLinkMan() + " " + item.getRevTelephone());//门店信息
                    }
                    helper.setText(R.id.tv_store_name, item.getShopName());
                    helper.setText(R.id.tv_order_count, item.getStationNumber());
                    helper.setText(R.id.tv_performance_point, getString(R.string.tv_base_point) + "：" + MathUtils.twolittercountString(item.getTotalBasePoint()));
                    helper.setText(R.id.tv_attach_point, getString(R.string.tv_point_ex) + "：" + MathUtils.twolittercountString(item.getBasePointExt()));
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
                    helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                            if (CommonUtils.isFastDoubleClick() || item.getShippingType() == 1) {
                                return;
                            }
                            ((WaitingDeliveryFragment) ((HomeActivity) mActivity).getFragment(0)).showDialog(item.getOrderId());
                        }
                    });
                }
            };
            preProductLv.setAdapter(shippingOrderAdapter);
            if (shippingData != null && shippingData.size() > 0) {
                shippingOrderAdapter.replaceAll(shippingData);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    public boolean checkCanDoRefresh() {
        if (null == preProductLv.getChildAt(0)) {
            LogUtils.d("null == gvGoodsGrid.getChildAt(0)");
        }
        if (shippingOrderAdapter == null || shippingOrderAdapter.getCount() == 0 || shippingOrderAdapter == null || null == preProductLv.getChildAt(0)) {
            return true;
        }

        LogUtils.d(String.format("checkCanDoRefresh: %s %s", preProductLv.getFirstVisiblePosition(), preProductLv.getChildAt(0).getTop()));
        return preProductLv.getFirstVisiblePosition() == 0 && preProductLv.getChildAt(0).getTop() == 0;
    }

    public void notifyDataSetChanged() {
        if (null != shippingOrderAdapter) {
            shippingOrderAdapter.notifyDataSetChanged();
        }
    }
}
