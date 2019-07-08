package com.frxs.delivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.DateUtil;
import com.ewu.core.utils.SortListUtil;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.comms.GlobelDefines;
import com.frxs.delivery.model.DeliveryvOrderSectionListItem;
import com.frxs.delivery.model.FinshDeliverOders;
import com.frxs.delivery.model.SectionListItem;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/8/10.
 */

public class FinshDeliveryActivity extends FrxsActivity {

    private ListView lvOrderFinsh;
    private QuickAdapter<DeliveryvOrderSectionListItem> adapter;
    private List<DeliveryvOrderSectionListItem> sectionList = new ArrayList<DeliveryvOrderSectionListItem>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_finsh_delivery;
    }

    @Override
    protected void initViews() {
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("配送完成");
        findViewById(R.id.tv_title_right).setVisibility(View.INVISIBLE);
        lvOrderFinsh = (ListView) findViewById(R.id.lv_order_finsh);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reqFinshDeliverOrders();
    }

    @Override
    protected void initData() {
        adapter = new QuickAdapter<DeliveryvOrderSectionListItem>(this, R.layout.item_delivered_order) {
            @Override
            protected void convert(BaseAdapterHelper helper, final DeliveryvOrderSectionListItem item) {
                final FinshDeliverOders.WaitDeliverDataBean orderItem = (FinshDeliverOders.WaitDeliverDataBean) item.getItem();
                LinearLayout headLayout = helper.getView(R.id.head_layout);
                if (item.getType() == SectionListItem.SECTION) {
                    headLayout.setVisibility(View.VISIBLE);
                    Date date = DateUtil.string2Date(orderItem.getShippingEndDate(), "yyyy-MM-dd");
                    helper.setText(R.id.tv_order_time,  DateUtil.format(date, "yyyy-MM-dd"));
                    helper.setText(R.id.tv_order_count, String.format(getString(R.string.order_count), String.valueOf(item.getOrderCount())));
                    helper.setText(R.id.tv_total_amt, String.format(getString(R.string.order_amt), MathUtils.twolittercountString(item.getPayAmount())));
                } else {
                    headLayout.setVisibility(View.GONE);
                }
                helper.setText(R.id.tv_order_id, String.format(getString(R.string.order_id), orderItem.getOrderId()));
                helper.setText(R.id.tv_shop_name, String.format(getString(R.string.shop_name), orderItem.getShopName()));
                helper.setText(R.id.tv_order_amt, String.format(getString(R.string.order_amt), MathUtils.twolittercountString(orderItem.getPayAmount())));
                TextView tvSign = helper.getView(R.id.tv_sign);
                /**
                 * 启用电子签名才显示入口
                 */
                tvSign.setVisibility(View.VISIBLE);
                if (orderItem.getIsSign() == 1) {
                    tvSign.setSelected(false);
                    tvSign.setText("查看签名");
                    tvSign.setVisibility(View.VISIBLE);
                } else {
                    tvSign.setVisibility(View.GONE);
                }

                /**
                 * 跳转签名或查看签名页面
                 */
                helper.setOnClickListener(R.id.tv_sign, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isFastDoubleClick()) {
                            return;
                        }
                        if (orderItem.getIsSign() == 1){// 查看签名不需权限
                            Intent intent = new Intent(FinshDeliveryActivity.this, LookSignatureActivity.class);
                            intent.putExtra("sign_id", orderItem.getSignID());
                            startActivity(intent);
                        } else {
                            ToastUtils.show(FinshDeliveryActivity.this, "电子签名功能未启用");
                        }
                    }
                });
            }
        };
        lvOrderFinsh.setAdapter(adapter);
        reqFinshDeliverOrders();
    }

    /**
     * 请求完成配送列表
     */
    private void reqFinshDeliverOrders() {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        params.put("Sign", MD5.ToMD5("GetWaitDeliverInfo"));
        if (!TextUtils.isEmpty(userInfo.getIsMaster())) {
            if (userInfo.getIsMaster().equals("1")) {
                params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
            } else {
                params.put("EmpId", String.valueOf(userInfo.getEmpID()));
                params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
                params.put("LineIDs", userInfo.getLineIDs());
            }
        }

        getService().GetFinshDliverOrders(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<FinshDeliverOders>>() {
            @Override
            public void onResponse(ApiResponse<FinshDeliverOders> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        List<FinshDeliverOders.WaitDeliverDataBean> waitDeliverData = result.getData().getWaitDeliverData();
                        if (waitDeliverData != null) {
                            if (waitDeliverData.size() > 0) {
                                SortListUtil.sort(waitDeliverData, "getShippingEndDate", SortListUtil.ORDER_BY_DESC);
                                packageSectionList(waitDeliverData);
                                adapter.replaceAll(sectionList);
                            } else {
                                ToastUtils.show(FinshDeliveryActivity.this, "暂无完成配送订单");
                            }
                        } else {
                            ToastUtils.show(FinshDeliveryActivity.this, "暂无完成配送订单");
                        }
                    } else {
                        ToastUtils.show(FinshDeliveryActivity.this, "暂无完成配送订单");
                    }
                } else {
                    ToastUtils.show(FinshDeliveryActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FinshDeliverOders>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(FinshDeliveryActivity.this, t.getMessage());
            }
        });
    }

    private void packageSectionList(List<FinshDeliverOders.WaitDeliverDataBean> itemList) {
        sectionList.clear();
        int sectionPosition = -1;

        for (int i = 0; i < itemList.size(); i++) {
            Date beginTime = DateUtil.string2Date(itemList.get(i).getShippingEndDate(), "yyyy-MM-dd");
            String currentStr =  DateUtil.format(beginTime, "yyyy-MM-dd");
            String previewStr = " ";
            if ((i - 1) >= 0) {
                Date date = DateUtil.string2Date(itemList.get(i - 1).getShippingEndDate(), "yyyy-MM-dd");
                previewStr = DateUtil.format(date, "yyyy-MM-dd");
            }
            if (!previewStr.equals(currentStr)) {
                DeliveryvOrderSectionListItem sectionItem = new DeliveryvOrderSectionListItem(itemList.get(i), SectionListItem.SECTION, itemList.get(i).getShippingEndDate());
                double orderAmt = itemList.get(i).getPayAmount();
                int orderCount = 1;
                sectionItem.setPayAmount(orderAmt);
                sectionItem.setOrderCount(orderCount);
                sectionList.add(sectionItem);
                sectionPosition = i;
            } else {
                DeliveryvOrderSectionListItem listItem = new DeliveryvOrderSectionListItem(itemList.get(i), SectionListItem.ITEM, itemList.get(i).getShippingEndDate());
                if (sectionPosition >= 0) {
                    DeliveryvOrderSectionListItem sectionItem = sectionList.get(sectionPosition);
                    double orderAmt = sectionItem.getPayAmount() + itemList.get(i).getPayAmount();
                    int orderCount = sectionItem.getOrderCount() + 1;
                    sectionItem.setPayAmount(orderAmt);
                    sectionItem.setOrderCount(orderCount);
                    sectionList.add(listItem);
                }
            }
        }
    }

}
