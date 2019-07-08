package com.frxs.delivery.fragment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
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
import com.frxs.delivery.widget.ClearEditText;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.List;

import retrofit2.Call;

/**
 * Created by shenpei on 2017/6/6.
 * 等待取货
 */

public class WaitReceivFragment extends FrxsFragment {

    private ListView receivLv;

    private QuickAdapter<WaitReceiveList.ApplyForSaleBackListBean> adapter;

    private LinearLayout llSearch;

    private ClearEditText ctContext;

    private TextView searchTv;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_receiving;
    }

    @Override
    protected void initViews(View view) {
        view.findViewById(R.id.ll_order_info).setVisibility(View.GONE);
        ctContext = (ClearEditText) view.findViewById(R.id.ct_context);
        searchTv = (TextView) view.findViewById(R.id.tv_search);
        llSearch = (LinearLayout) view.findViewById(R.id.ll_search);
        receivLv = (ListView) view.findViewById(R.id.lv_receiv);
    }

    @Override
    protected void initEvent() {
        searchTv.setOnClickListener(this);
        ctContext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString().trim();
                if (TextUtils.isEmpty(search)){
                    reqGetWaitBackList(search, 2);
                }
            }
        });
    }

    @Override
    protected void initData() {
        adapter = new QuickAdapter<WaitReceiveList.ApplyForSaleBackListBean>(mActivity, R.layout.item_wait_receiv) {
            @Override
            protected void convert(BaseAdapterHelper helper, final WaitReceiveList.ApplyForSaleBackListBean item) {
                helper.setVisible(R.id.img_right, false);
                helper.setText(R.id.tv_order_id, item.getApplyBackID());
                helper.setText(R.id.tv_shop_name, item.getShopName());
                helper.setText(R.id.tv_order_amount, MathUtils.twolittercountString(item.getPayAmount()));
                helper.setText(R.id.tv_order_state, "状态：" + item.getStatusName());
                /**
                 * 点击司机收货进入收货页面
                 */
                helper.setOnClickListener(R.id.tv_receiving, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (CommonUtils.isFastDoubleClick()) {
                            return;
                        }
                        Intent intent = new Intent(mActivity, ReceivingActivity.class);
                        intent.putExtra("APPLY_BACK_ID", item.getApplyBackID());
                        intent.putExtra("GOOD", item);
                        intent.putExtra("FROM", "wait");
                        startActivity(intent);
                    }
                });
            }
        };
        receivLv.setAdapter(adapter);
    }

    /**
     * 请求获取等待取货订单列表
     * @param search
     * @param backType
     */
    private void reqGetWaitBackList(final String search, int backType) {
        mActivity.showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("WID", userInfo.getWareHouseWID());
        params.put("LineIDs", userInfo.getLineIDs());
        if (!TextUtils.isEmpty(search)) {
            params.put("Search", search);
        }
        params.put("Status", backType);//2:等待取货   5:已取货
        params.put("UserId", userInfo.getEmpID());
        params.put("UserName", userInfo.getEmpName());

        getService().GetApplyForSaleBackList(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<WaitReceiveList>>() {
            @Override
            public void onResponse(ApiResponse<WaitReceiveList> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")){
                    if (result.getData() != null) {
                        WaitReceiveList data = result.getData();
                        List<WaitReceiveList.ApplyForSaleBackListBean> applyForSaleBackList = data.getApplyForSaleBackList();
                        adapter.replaceAll(applyForSaleBackList);
                        if (data.getTotal() <= 0) {
                            if (TextUtils.isEmpty(search)) {
                                ToastUtils.show(mActivity, "等待取货中暂无订单");
                            } else {
                                ToastUtils.show(mActivity, "未查到该门店的订单");
                            }
                        }
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
        switch (v.getId()) {
            case R.id.tv_search: //搜索退货订单
                String keyword = ctContext.getText().toString().trim();
                if (!TextUtils.isEmpty(keyword)) {
                    reqGetWaitBackList(keyword, 2);
                } else {
                    ToastUtils.show(mActivity, "请输入门店编号进行搜索!");
                }
                break;

            default:
                break;
        }
    }

    public void refresDate(){
        ctContext.setText("");
        reqGetWaitBackList("", 2);
    }

    @Override
    public void onResume() {
        super.onResume();
        reqGetWaitBackList(ctContext.getText().toString().trim(), 2);
    }
}
