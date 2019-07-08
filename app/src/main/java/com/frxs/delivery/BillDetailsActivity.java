package com.frxs.delivery;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.ewu.core.utils.SortListObjectUtil;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.BillData;
import com.frxs.delivery.model.BillList;
import com.frxs.delivery.model.SectionListItem;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * 对账详情 by Tiepier
 */
public class BillDetailsActivity extends FrxsActivity {

    private TextView tvTitle;//标题

    private TextView tvLeft;//返回

    private TextView tvRight;

    private ListView lvBillDetails;

    private QuickAdapter<BillList> billDetailsAdapter;

    private BillData billData;

    private TextView tvBillDetailsTime;//时间

    private TextView tvBillDetailsCount;//订单数

    private TextView tvBillDetailsIntegral;//积分

    private TextView tvBillDetailsAmount;//金额

    private String strSearchDate;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill_details;
    }

    @Override
    protected void initViews() {

        /**
         * 实例化控件
         */
        tvLeft = (TextView) findViewById(R.id.tv_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_title_right);
        lvBillDetails = (ListView) findViewById(R.id.lv_bill_details);

        tvBillDetailsTime = (TextView) findViewById(R.id.tv_bill_details_time);
        tvBillDetailsCount = (TextView) findViewById(R.id.tv_bill_details_count);
        tvBillDetailsIntegral = (TextView) findViewById(R.id.tv_bill_details_integral);
        tvBillDetailsAmount = (TextView) findViewById(R.id.tv_bill_details_amount);

        tvTitle.setText(R.string.title_bill_detalis);
        tvRight.setVisibility(View.INVISIBLE);

        /**
         * 获取时间参数
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strSearchDate = getIntent().getStringExtra("TIME");
        }

    }


    /**
     * 设置对账单详情信息汇总
     */
    private void setValue(BillData billData) {
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (userInfo.getIsEnabledFreightCar() == 0) {
            findViewById(R.id.ll_bill_info).setVisibility(View.VISIBLE);
            tvBillDetailsCount.setText(String.valueOf(billData.getTotalOrderCount() + "笔"));//总订单数
            tvBillDetailsIntegral.setText(MathUtils.twolittercountString(billData.getTotalPoint()));//总积分
            tvBillDetailsAmount.setText("￥" + MathUtils.twolittercountString(billData.getTotalProductAmt()));//总金额
        } else {
            TextView tvCartBillInfo = (TextView) findViewById(R.id.tv_bill_cart_info);
            tvCartBillInfo.setVisibility(View.VISIBLE);
            Serializable carNum = billData.getGroupNnumbers().size() > 0 ? String.valueOf(billData.getGroupNnumbers().size()) : "暂无";
            tvCartBillInfo.setText(String.format(getString(R.string.tv_bill_cart_info), carNum,
                    String.valueOf(billData.getShopCount()), billData.getTotalBasePoint(), billData.getTotalBasePointExt(), billData.getTotalAdjPoint()));
            TextView pointTv = (TextView) findViewById(R.id.tv_point);
            pointTv.setText(R.string.tv_base_point);
            TextView pointExTv = (TextView) findViewById(R.id.tv_point_ex);
            pointExTv.setText(R.string.tv_point_ex);
        }
        tvBillDetailsTime.setText(billData.getPackingTime());//时间
    }

    @Override
    protected void initData() {
        billDetailsAdapter = new QuickAdapter<BillList>(this, R.layout.item_bill_details_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, BillList item) {
                TextView tvCarNum = helper.getView(R.id.tv_car_num);
                helper.setText(R.id.tv_bill_shop_id, item.getShopCode());//门店编号
                helper.setText(R.id.tv_bill_order_id, item.getOrderId());//订单编号
                UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
                if (userInfo.getIsEnabledFreightCar() == 0) {
                    tvCarNum.setVisibility(View.GONE);
                    helper.setText(R.id.tv_bill_integral, MathUtils.twolittercountString(item.getTotalPoint()));//积分
                    helper.setText(R.id.tv_bill_amount, "￥" + MathUtils.twolittercountString(item.getTotalProductAmt()));//金额
                } else {
                    if (item.getType() == SectionListItem.SECTION && item.getShippingSerialNumber() > 0) {
                        tvCarNum.setVisibility(View.VISIBLE);
                        tvCarNum.setText(String.format(getString(R.string.car_sort), String.valueOf(item.getShippingSerialNumber())));
                    } else {
                        tvCarNum.setVisibility(View.GONE);
                    }
                    helper.setText(R.id.tv_bill_integral, MathUtils.twolittercountString(item.getTotalBasePoint()));//绩效分
                    helper.setText(R.id.tv_bill_amount, MathUtils.twolittercountString(item.getBasePointExt()));//附加分
                }
            }
        };
        lvBillDetails.setAdapter(billDetailsAdapter);
        reqBillDetailsList();
    }

    @Override
    protected void initEvent() {
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
        }
    }

    /**
     * 对账详情数据请求
     */
    public void reqBillDetailsList() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetSaleOrderDetailInfo"));
        params.put("SearchDate", strSearchDate);
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("LineIDs", userInfo.getLineIDs());
        getService().GetSaleOrderDetailInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<BillData>>() {
            @Override
            public void onResponse(ApiResponse<BillData> result, int code, String msg) {
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        billData = result.getData();
                        if (billData != null) {
                            setValue(billData);
                            //对账详情列表
                            List<BillList> bl = billData.getSaleOrderData();
                            packageSectionList(bl, billDetailsAdapter);
                        }
                    } else {
                        ToastUtils.show(BillDetailsActivity.this, result.getInfo());
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<BillData>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    private void packageSectionList(List<BillList> bl, QuickAdapter<BillList> billDetailsAdapter) {
        SortListObjectUtil.sort(bl, "getShippingSerialNumber", SortListObjectUtil.ORDER_BY_ASC);
        List<BillList> newBillList = new ArrayList<BillList>();
        int sectionPosition = -1;

        for (int i = 0; i < bl.size(); i++) {
            int currentStr = bl.get(i).getShippingSerialNumber();
            int previewStr = (i - 1) >= 0 ? bl.get(i - 1).getShippingSerialNumber() : -1;
            if (previewStr != currentStr) {
                bl.get(i).setType(SectionListItem.SECTION);
                newBillList.add(bl.get(i));
                sectionPosition = i;
            } else {
                bl.get(i).setType(SectionListItem.ITEM);
                if (sectionPosition >= 0) {
                    newBillList.add(bl.get(i));
                }
            }
        }
        billDetailsAdapter.replaceAll(newBillList);

    }

}
