package com.frxs.delivery.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.R;
import com.frxs.delivery.ReceivingActivity;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.model.WaitReceiveList;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MathUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by shenpei on 2017/6/6.
 * 已取货
 */

public class ReceivedFragment extends FrxsFragment {

    private ListView receivLv;

    private QuickAdapter<WaitReceiveList.ApplyForSaleBackListBean> adapter;

    private TextView orderTotalTv;

    private TextView orderAmount;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_receiving;
    }

    @Override
    protected void initViews(View view) {
        view.findViewById(R.id.ll_search).setVisibility(View.GONE);
        receivLv = (ListView) view.findViewById(R.id.lv_receiv);
        orderTotalTv = (TextView) view.findViewById(R.id.tv_order_total);
        orderAmount = (TextView) view.findViewById(R.id.tv_order_amount);
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        reqGetReceivedList();
        adapter = new QuickAdapter<WaitReceiveList.ApplyForSaleBackListBean>(mActivity, R.layout.item_wait_receiv) {
            @Override
            protected void convert(BaseAdapterHelper helper, final WaitReceiveList.ApplyForSaleBackListBean item) {
                helper.setVisible(R.id.ll_receiving, false);
                helper.setText(R.id.tv_order_id, item.getApplyBackID());
                helper.setText(R.id.tv_shop_name, item.getShopName());
                helper.setText(R.id.tv_order_amount, MathUtils.twolittercountString(item.getTotalBackTotalAmt()));
                helper.setText(R.id.tv_order_state, "状态：" + item.getStatusName());

                helper.setOnClickListener(R.id.ll_received_info, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isFastDoubleClick()) {
                            return;
                        }
                        Intent intent = new Intent(mActivity, ReceivingActivity.class);
                        intent.putExtra("APPLY_BACK_ID", item.getApplyBackID());
                        intent.putExtra("GOOD", item);
                        startActivity(intent);
                    }
                });
            }
        };

        receivLv.setAdapter(adapter);
    }

    private void reqGetReceivedList() {
        mActivity.showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("WID", userInfo.getWareHouseWID());
        params.put("LineIDs", userInfo.getLineIDs());
        params.put("Status", 5);//2:等待取货   5:已取货
        params.put("UserId", userInfo.getEmpID());
        params.put("UserName", userInfo.getEmpName());

        getService().GetApplyForSaleBackList(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<WaitReceiveList>>() {
            @Override
            public void onResponse(ApiResponse<WaitReceiveList> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        WaitReceiveList data = result.getData();
                        List<WaitReceiveList.ApplyForSaleBackListBean> applyForSaleBackList = data.getApplyForSaleBackList();
                        adapter.replaceAll(applyForSaleBackList);
                        if (applyForSaleBackList != null && applyForSaleBackList.size() > 0) {
                            orderTotalTv.setText("总订单：" + applyForSaleBackList.size());
                            double totalAmt = 0.0;
                            for (WaitReceiveList.ApplyForSaleBackListBean backOrder : applyForSaleBackList) {
                                totalAmt += backOrder.getTotalBackTotalAmt();
                            }
                            orderAmount.setText("总金额：" + MathUtils.twolittercountString(totalAmt));
                        } else {
                            ToastUtils.show(mActivity, "已取货中暂无订单");
                        }
                    } else {
                        ToastUtils.show(mActivity, "已取货中暂无订单");
                    }
                } else {
                    ToastUtils.show(mActivity, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WaitReceiveList>> call, Throwable t) {
                dismissProgressDialog();
                super.onFailure(call, t);
                ToastUtils.show(mActivity, t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public void refresDate() {
        reqGetReceivedList();
    }

    @Override
    public void onResume() {
        super.onResume();
        reqGetReceivedList();
    }
}
