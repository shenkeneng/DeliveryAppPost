package com.frxs.delivery;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.ewu.core.utils.CheckUtils;
import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.ToastUtils;
import com.ewu.core.widget.MaterialDialog;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.PostSubmitTakeOrder;
import com.frxs.delivery.model.SaleBackOrderInfo;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.model.WaitReceiveList;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MathUtils;
import com.frxs.delivery.widget.CountEditText;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by shenpei on 2017/6/7.
 * 正在取货订单商品列表页面
 */

public class ReceivingActivity extends FrxsActivity {

    private ListView receivingGoodsLv;// 商品列表

    private TextView completeReceivTv;// 完成收货

    private QuickAdapter<SaleBackOrderInfo> adapter;

    private String applyBackId;//退货单ID

    private List<SaleBackOrderInfo> saleBackList;

    private String from;// 判断是否隐藏订单信息栏

    private TextView shopNameTv;// 门店名称

    private TextView orderIdTv;// 订单ID

    private TextView goodCountTv;// 总商品数量

    private WaitReceiveList.ApplyForSaleBackListBean goodBack;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_receiving;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.tv_title_right).setVisibility(View.GONE);
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText("商品列表");
        receivingGoodsLv = (ListView) findViewById(R.id.lv_receiving_goods);
        completeReceivTv = (TextView) findViewById(R.id.tv_complete_receiv);
        shopNameTv = (TextView) findViewById(R.id.tv_shop_name);
        orderIdTv = (TextView) findViewById(R.id.tv_order_id);
        goodCountTv = (TextView) findViewById(R.id.tv_goods_count);
        applyBackId = getIntent().getStringExtra("APPLY_BACK_ID");
        from = getIntent().getStringExtra("FROM");
        goodBack = (WaitReceiveList.ApplyForSaleBackListBean) getIntent().getSerializableExtra("GOOD");
    }

    @Override
    protected void initEvent() {
        completeReceivTv.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        reqSaleBackOrderInfo();
        /**
         * 来自已取货页面 （显示总金额）
         */
        if (TextUtils.isEmpty(from)) {
            completeReceivTv.setText("取货金额：");
            completeReceivTv.setTextColor(getResources().getColor(R.color.red));
            completeReceivTv.setBackgroundColor(getResources().getColor(R.color.white));
        }
        adapter = new QuickAdapter<SaleBackOrderInfo>(this, R.layout.item_receiving) {
            @Override
            protected void convert(BaseAdapterHelper helper, final SaleBackOrderInfo item) {
                helper.setText(R.id.tv_goods_name, item.getProductName());
                helper.setText(R.id.tv_encoding, TextUtils.isEmpty(item.getSKU()) ? "编码:" : "编码:" + item.getSKU());
                String barCode = item.getBarCode().split(",")[0];
                helper.setText(R.id.tv_bar_code, TextUtils.isEmpty(barCode) ? "条码:" : "条码:" + barCode);
                double deliveryPrice = item.getBackPrice() * (1 + item.getShopAddPerc());
                helper.setText(R.id.tv_goods_price, "单价:￥" + MathUtils.twolittercountString(deliveryPrice) + "/" + item.getBackUnit());
                helper.setText(R.id.tv_confirm_count, "确认数量:" + (int)MathUtils.round(item.getBackQty(), 0) + item.getBackUnit());
                if (TextUtils.isEmpty(from)) {
                    helper.setVisible(R.id.count_edit_text, false);
                    helper.setVisible(R.id.tv_receiv_count, true);
                    helper.setText(R.id.tv_receiv_count, "取货数量:" + (int)MathUtils.round(item.getTakeBackQty(), 0) + item.getBackUnit());
                } else {
                    /**
                     * 修改商品数量
                     */
                    final CountEditText countEditText = helper.getView(R.id.count_edit_text);
                    countEditText.setMaxCount(9999);
                    countEditText.setCount((int) item.getBackQty());//初始化商品数量
                    countEditText.setEditTextClickale(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final MaterialDialog dialog = new MaterialDialog(ReceivingActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(ReceivingActivity.this);
                            final View view = inflater.inflate(R.layout.dialog_modify_num, null);
                            dialog.setContentView(view);
                            ((TextView) view.findViewById(R.id.my_title_tv)).setText("修改取货数量");
                            final EditText countEt = (EditText) view.findViewById(R.id.count_edit_et);
                            countEt.setText(String.valueOf(countEditText.getCount()));
                            dialog.setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String strCount = countEt.getText().toString().trim();
                                    int count = 0;
                                    if (CheckUtils.strIsNumber(strCount)) {
                                        count = Integer.valueOf(strCount);
                                        if (count < 0) {
                                            count = 0;
                                        }
                                        if (count > 9999){
                                            count = 9999;
                                        }
                                    }
                                    countEditText.setCount(count);

                                    if (count != item.getBackQty()) {
                                        double backQty = goodBack.getTotalBackQty() - item.getBackQty();
                                        item.setBackQty(count);
                                        adapter.notifyDataSetChanged();
                                        setCurrentTotalBackQty(count, backQty);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            dialog.setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    });
                    // 监听数量的变化
                    countEditText.setOnCountChangeListener(new CountEditText.onCountChangeListener() {
                        @Override
                        public void onCountAdd(int count) {
                            int cartCount = countEditText.getCount();
                            double backQty = goodBack.getTotalBackQty() - item.getBackQty();
                            item.setBackQty(cartCount);
                            adapter.notifyDataSetChanged();
                            setCurrentTotalBackQty(count, backQty);
                        }

                        @Override
                        public void onCountSub(int count) {
                            int cartCount = countEditText.getCount();
                            double backQty = goodBack.getTotalBackQty() - item.getBackQty();
                            item.setBackQty(cartCount);
                            adapter.notifyDataSetChanged();
                            setCurrentTotalBackQty(count, backQty);
                        }
                    });
                }
            }
        };
        receivingGoodsLv.setAdapter(adapter);
    }

    /**
     * 设置当前商品总数量
     * @param count
     * @param backQty
     */
    private void setCurrentTotalBackQty(int count, double backQty) {
        backQty += count;
        goodBack.setTotalBackQty(backQty);
        goodCountTv.setText("商品总数量：" + MathUtils.doubleTrans(MathUtils.round(backQty, 2)));
    }

    /**
     * 请求退货单的详情
     */
    private void reqSaleBackOrderInfo() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("ApplyBackID", applyBackId);
        params.put("WID", userInfo.getWareHouseWID());
        params.put("WarehouseId", userInfo.getWareHouseWID());
        params.put("UserId", userInfo.getEmpID());
        params.put("UserName", userInfo.getEmpName());

        getService().GetApplyForSaleBackInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<List<SaleBackOrderInfo>>>() {
            @Override
            public void onResponse(ApiResponse<List<SaleBackOrderInfo>> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        saleBackList = result.getData();
                        shopNameTv.setText("门店名称：" + saleBackList.get(0).getShopName());
                        orderIdTv.setText("单号：" + saleBackList.get(0).getApplyBackID());
                        adapter.replaceAll(saleBackList);
                        // 设置商品总数量
                        if (TextUtils.isEmpty(from)) {
                            completeReceivTv.setText("取货金额：" + MathUtils.twolittercountString(goodBack.getTotalBackTotalAmt()));// 订单为已取货时设置商品总金额
                            goodCountTv.setText("商品总数量：" + MathUtils.doubleTrans(MathUtils.round(goodBack.getTakeBackTotalQty(), 2)));
                        } else {
                            goodCountTv.setText("商品总数量：" + MathUtils.doubleTrans(MathUtils.round(goodBack.getTotalBackQty(), 2)));
                        }
                    } else {
                        ToastUtils.show(ReceivingActivity.this, "该订单暂无详情数据");
                    }
                } else {
                    ToastUtils.show(ReceivingActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SaleBackOrderInfo>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(ReceivingActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (CommonUtils.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_complete_receiv:// 完成收货
                int goodsCount = 0;// 提交时所有商品收货总数量
                for (SaleBackOrderInfo good : saleBackList) {
                    goodsCount += good.getBackQty();
                }
                if (goodsCount <= 0) { // 商品总数量小于等于0不允许提交订单
                    ToastUtils.show(this, getString(R.string.tips_goods_count));
                    return;
                }
                submitTakeOrder();
                break;
        }
    }

    /**
     * 完成收货
     */
    private void submitTakeOrder() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        PostSubmitTakeOrder postSTO = new PostSubmitTakeOrder();
        postSTO.setApplyBackID(applyBackId);
        postSTO.setUserId(userInfo.getEmpID());
        postSTO.setUserName(userInfo.getEmpName());
        postSTO.setWarehouseId(userInfo.getWareHouseWID());
        postSTO.setWID(userInfo.getWareHouseWID());
        List<SaleBackOrderInfo> TakeBackDetails = new ArrayList<SaleBackOrderInfo>();
        for (SaleBackOrderInfo good : saleBackList) {
            SaleBackOrderInfo saleBackOrderInfo = new SaleBackOrderInfo();
            saleBackOrderInfo.setTakeBackQty(good.getBackQty());
            saleBackOrderInfo.setID(good.getID());
            saleBackOrderInfo.setUnit(good.getUnit());
            saleBackOrderInfo.setUnitPrice(good.getUnitPrice());
            saleBackOrderInfo.setShopAddPerc(good.getShopAddPerc());
            TakeBackDetails.add(saleBackOrderInfo);
        }
        postSTO.setTakeBackDetails(TakeBackDetails);

        getService().SubmitApplyForSaleBackTake(postSTO).enqueue(new SimpleCallback<ApiResponse<Boolean>>() {
            @Override
            public void onResponse(ApiResponse<Boolean> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(ReceivingActivity.this, "该订单已完成收货!");
                    finish();
                } else {
                    ToastUtils.show(ReceivingActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Boolean>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(ReceivingActivity.this, t.getMessage());
            }
        });

    }

}
