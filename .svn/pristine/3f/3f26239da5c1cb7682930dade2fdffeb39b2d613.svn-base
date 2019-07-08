package com.frxs.delivery;

import android.app.Dialog;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.PostSweepLoading;
import com.frxs.delivery.model.ScanLoadingInfo;
import com.frxs.delivery.model.SweepLoading;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MathUtils;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/05/11
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ScanLoadingActivity extends FrxsActivity {

    private TextView trainTv;//车次
    private String scanCode;
    private ScanLoadingInfo scanLoadingInfo;
    //private TextView orderNumTv;//订单数
    //private TextView orderAmountTv;//订单总金额
    private TextView availableAmountTv;//可配送金额
    private TextView totalPointTv;//绩效分
    private TextView extPointTv;//附加绩效分
    private TextView shopCountTv;//门店数
    private TextView maxDistanceTv;//最远公里数
    private TextView remarkTv;//备注说明

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_loading;
    }

    @Override
    protected void initViews() {
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.tv_title_right).setVisibility(View.GONE);
        titleTv.setText(R.string.title_scan_load);
        findViewById(R.id.finish_btn).setOnClickListener(this);
        findViewById(R.id.cancel_btn).setOnClickListener(this);
        trainTv = (TextView)findViewById(R.id.train_tv);
        totalPointTv = (TextView) findViewById(R.id.total_point_tv);
        extPointTv = (TextView) findViewById(R.id.ext_point_tv);
        shopCountTv = (TextView) findViewById(R.id.shop_count_tv);
        maxDistanceTv = (TextView) findViewById(R.id.max_distance_tv);
        //orderNumTv = (TextView) findViewById(R.id.order_num_tv);
        //orderAmountTv = (TextView) findViewById(R.id.order_amount_tv);
        availableAmountTv = (TextView) findViewById(R.id.available_amount_tv);
        remarkTv = (TextView) findViewById(R.id.tv_remark);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (null != intent) {
            scanLoadingInfo = (ScanLoadingInfo) intent.getSerializableExtra("Content");
        }

        if (scanLoadingInfo == null){
            showInfoDialog("当前仓库无订单数据，请稍后重新扫描。");
        } else {
            updateUI(scanLoadingInfo);
        }
    }

    private void updateUI(ScanLoadingInfo scanLoadingInfo){
        totalPointTv.setText(Html.fromHtml(String.format(getString(R.string.total_point), scanLoadingInfo.getTotalBasePoint())));
        extPointTv.setText(Html.fromHtml(String.format(getString(R.string.ext_point), scanLoadingInfo.getBasePointExt())));
        shopCountTv.setText(Html.fromHtml(String.format(getString(R.string.shop_count), scanLoadingInfo.getShopCount())));
        maxDistanceTv.setText(Html.fromHtml(String.format(getString(R.string.max_distance), scanLoadingInfo.getMaxShopDistance())));
        trainTv.setText(Html.fromHtml(String.format(getString(R.string.train), scanLoadingInfo.getShippingSerialNumber())));
        //orderNumTv.setText(Html.fromHtml(String.format(getString(R.string.order_num), scanLoadingInfo.getOrdersCount())));
       // orderAmountTv.setText(Html.fromHtml(String.format(getString(R.string.order_amount), scanLoadingInfo.getTotalAmt())));
        availableAmountTv.setText(Html.fromHtml(String.format(getString(R.string.available_amount), scanLoadingInfo.getShippingAmt(), scanLoadingInfo.getShippingMaxAmt())));
        if (scanLoadingInfo.getTotalAmt() < scanLoadingInfo.getShippingAmt()) {//指定的订单总金额小于规定区间金额 弹窗提示
            double subNumber = MathUtils.sub(scanLoadingInfo.getShippingAmt(), scanLoadingInfo.getTotalAmt());
            remarkTv.setText(String.format(getString(R.string.remark), MathUtils.roundUp(subNumber, 2)));
        } else {
            remarkTv.setText("本车次已达到起载金额");
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.finish_btn:
                if (null != scanLoadingInfo && null != scanLoadingInfo.getOrderDetail() && scanLoadingInfo.getOrderDetail().size() > 0) {
                    if (scanLoadingInfo.getTotalAmt() < scanLoadingInfo.getShippingAmt()){//指定的订单总金额小于规定区间金额 弹窗提示
                        showDialog();
                    } else { // 指定订单总金额不小于规定区间金额 用户接收订单 
                        requestSweepLoading();
                    }
                } else {
                    showInfoDialog("当前仓库无订单数据，请稍后重新扫描。");
                }
                break;
            case R.id.cancel_btn:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 显示信息对话框
     * @param info
     */
    private void showInfoDialog(String info) {
        final Dialog dialog = new Dialog(ScanLoadingActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_selector);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
        tvMessage.setText(info);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnScan = (Button) dialog.findViewById(R.id.btn_confirm);
        btnConfirm.setText("确定");// 确定停留当前页面
        btnScan.setText("返回首页");// 返回扫码页面或等待列表页面
        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
        dialog.show();
        btnScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void requestSweepLoading() {
        showProgressDialog();

        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        List<ScanLoadingInfo.OrderInfo> orderDetail = scanLoadingInfo.getOrderDetail();
        List<SweepLoading> sweepLadingList = new ArrayList<SweepLoading>();
        for (ScanLoadingInfo.OrderInfo item : orderDetail){
            SweepLoading sweepLoading = new SweepLoading();
            sweepLoading.setOrderId(item.getOrderId());
            sweepLoading.setTotalProductAmt(item.getTotalProductAmt());
            sweepLadingList.add(sweepLoading);
        }
        PostSweepLoading postSweepLoading =  new PostSweepLoading();
        postSweepLoading.setWID(userInfo.getWareHouseWID());
        postSweepLoading.setShippingUserID(userInfo.getEmpID());
        postSweepLoading.setUserName(userInfo.getEmpName());
        postSweepLoading.setListOrder(sweepLadingList);

        getService().SweepLoading(postSweepLoading).enqueue(new SimpleCallback<ApiResponse<List<Object>>>() {
            @Override
            public void onResponse(ApiResponse<List<Object>>result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(ScanLoadingActivity.this, "货配车扫码装车成功!");
                    Intent intent = new Intent(ScanLoadingActivity.this, LoadCarSortActivity.class);
                    intent.putExtra("from", "scan");
                    intent.putExtra("car_num", scanLoadingInfo.getShippingSerialNumber());
                    intent.putExtra("ShipDate", scanLoadingInfo.getShipDate());
                    startActivity(intent);
                    finish();
                } else {
                    showInfoDialog(result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Object>>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                showInfoDialog(t.getMessage());
            }
        });
    }

    /**
     * 提示是否继续装车对话框
     */
    private void showDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_selector);
        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
        dialog.show();
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);// 取消
        TextView messageTv = (TextView) dialog.findViewById(R.id.tv_message);
        messageTv.setText(R.string.message_dialog);
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 确认接收订单
                requestSweepLoading();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 取消退回扫码页面
                dialog.dismiss();
                finish();
            }
        });
    }
}
