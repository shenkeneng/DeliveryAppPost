package com.frxs.delivery.fragment;


import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.frxs.delivery.R;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.GetDeliverOrderInfo;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.DensityUtils;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import retrofit2.Call;


/**
 * 订单信息 by Tipier
 */
public class OrderInformationFragment extends FrxsFragment {

    private TextView tvOrderID;//单号

    private TextView tvOrderNumber;//流水号

    private TextView tvOrderTime;//下单日期

    private TextView tvOrderCount;//代购数量

    private TextView tvOrderAmount;//订单金额

    private TextView tvStoreIntegral;//门店积分

    private GetDeliverOrderInfo orderInfo;//订单信息

    private String strOrderID;//订单ID

    private LinearLayout llPackInfo;// 拣货完成

    private LinearLayout llPicking;// 正在拣货

    private LinearLayout llWaitingPick;// 等待拣货

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_order_information;
    }

    @Override
    protected void initViews(View view) {
        /**
         * 实例化控件
         */
        tvOrderID = (TextView) view.findViewById(R.id.tv_order_id);
        tvOrderNumber = (TextView) view.findViewById(R.id.tv_order);
        tvOrderTime = (TextView) view.findViewById(R.id.tv_order_time);
        tvOrderCount = (TextView) view.findViewById(R.id.tv_order_count);
        tvOrderAmount = (TextView) view.findViewById(R.id.tv_order_amount);
        tvStoreIntegral = (TextView) view.findViewById(R.id.tv_store_integral);
        llPackInfo = (LinearLayout) view.findViewById(R.id.ll_pack_info);
        llPicking = (LinearLayout) view.findViewById(R.id.ll_picking);
        llWaitingPick = (LinearLayout) view.findViewById(R.id.ll_waiting_pick);

        /**
         * 获取订单ID
         */
        Bundle bundle = getArguments();
        if (bundle != null) {
            strOrderID = bundle.getString("ORDERID");
        }

    }

    /**
     * 设置订单信息内容
     */
    private void setValue(GetDeliverOrderInfo orderInfo) {
        /**
         * 流水号处理
         */
        String strStationNumber = orderInfo.getStationNumber();
        if (strStationNumber.equals("")) {
            strStationNumber = "";
        }
        tvOrderID.setText("单号：" + orderInfo.getOrderId());//单号
        tvOrderNumber.setText(strStationNumber);//流水号
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//年月日格式
        try {
            //字符串转日期
            Date proStart = sdf.parse(orderInfo.getOrderDate());
            //日期转字符串
            String strProStart = sdf.format(proStart);
            tvOrderTime.setText("下单日期：" + strProStart);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvOrderCount.setText("商品数量：" + DensityUtils.subZeroAndDot(MathUtils.twolittercountString(orderInfo.getSaleQty())));//代购数量
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (userInfo.getIsEnabledFreightCar() == 0) {
            tvOrderAmount.setText("订单金额：" + "￥" + MathUtils.twolittercountString(orderInfo.getPayAmount()));//订单金额
            tvStoreIntegral.setText("门店积分：" + MathUtils.twolittercountString(orderInfo.getTotalPoint()));//门店积分
        } else if (userInfo.getIsEnabledFreightCar() == 1){
            tvOrderAmount.setText(getString(R.string.tv_base_point)+ "：" + MathUtils.twolittercountString(orderInfo.getTotalBasePoint()));//绩效分
            tvStoreIntegral.setText(getString(R.string.tv_point_ex)+ "：" + MathUtils.twolittercountString(orderInfo.getBasePointExt()));//附加分
        }
        if (orderInfo.getStatus() == 2) {// 等待拣货
            // 隐藏显示该状态的布局
            llPackInfo.setVisibility(View.GONE);
            llWaitingPick.setVisibility(View.VISIBLE);
            llPicking.setVisibility(View.GONE);
            TextView tvWaitingCount = (TextView) llWaitingPick.findViewById(R.id.tv_waiting_count);// 等待拣货的门店数
            TextView tvWaitingInfo = (TextView) llWaitingPick.findViewById(R.id.tv_waiting_info);// 等待拣货的信息

            // 等待拣货和正在拣货的总和
            int pendingPickNum = TextUtils.isEmpty(orderInfo.getWaitingPacking()) ? 0 : Integer.parseInt(orderInfo.getWaitingPacking());
            int beingPickNum = TextUtils.isEmpty(orderInfo.getIsPicking()) ? 0 : Integer.parseInt(orderInfo.getIsPicking());
            int sum = pendingPickNum + beingPickNum;
            tvWaitingCount.setText(Html.fromHtml("前面还有<font color=red>" + String.valueOf(sum) + "</font>个订单未发货"));
            tvWaitingInfo.setText("( 等待拣货" + pendingPickNum +"个 +" + " 正在拣货" + beingPickNum + "个 )");

        } else if (orderInfo.getStatus() == 3) {// 正在拣货
            // 隐藏显示该状态的布局
            llPackInfo.setVisibility(View.GONE);
            llWaitingPick.setVisibility(View.GONE);
            llPicking.setVisibility(View.VISIBLE);
            TextView tvPickingTime = (TextView) llPicking.findViewById(R.id.tv_picking_time);// 开始拣货的时间
            List<GetDeliverOrderInfo.ShelfAreaListBean> shelfAreaList = orderInfo.getShelfAreaList();
            if (shelfAreaList != null) {
                StringBuilder shelfAreaFlag = new StringBuilder("");
                for (GetDeliverOrderInfo.ShelfAreaListBean shelfArea : shelfAreaList) {
                    shelfAreaFlag.append(shelfArea.getShelfAreaName());
                    switch (shelfArea.getFlag()) {
                        case 1: {// 未拣货
                            shelfAreaFlag.append("(○)：   " + (shelfArea.getBeginTime() == null ? "-- : --" : shelfArea.getBeginTime()) + "\r\n");
                            break;
                        }

                        case 2: {// 进行中
                            shelfAreaFlag.append("(△)：   " + (shelfArea.getBeginTime() == null ? "-- : --" : shelfArea.getBeginTime()) + "\r\n");
                            break;
                        }

                        case 3: {// 已拣货
                            shelfAreaFlag.append("(√)：   "  + (shelfArea.getBeginTime() == null ? "-- : --" : shelfArea.getBeginTime()) + "\r\n");
                            break;
                        }
                    }
                }
                shelfAreaFlag.delete(shelfAreaFlag.length() - 2, shelfAreaFlag.length());
                tvPickingTime.setText(shelfAreaFlag.toString());
            } else {
                tvPickingTime.setVisibility(View.GONE);
            }
        } else {
            // 隐藏显示该状态的布局
            llPackInfo.setVisibility(View.VISIBLE);
            llWaitingPick.setVisibility(View.GONE);
            llPicking.setVisibility(View.GONE);
            TextView tvPickingTurnover = (TextView) llPackInfo.findViewById(R.id.tv_picking_turnover);//周转箱
            TextView tvPickingBox = (TextView) llPackInfo.findViewById(R.id.tv_picking_box);//纸箱数
            TextView tvPickingFragile = (TextView) llPackInfo.findViewById(R.id.tv_picking_fragile);//易碎品
            TextView tvPickingRemark = (TextView) llPackInfo.findViewById(R.id.tv_picking_remark);//备注
            tvPickingTurnover.setText("周转箱(个)：" + String.valueOf(orderInfo.getPackage1Qty()));
            tvPickingBox.setText("纸箱数(件)：" + String.valueOf(orderInfo.getPackage2Qty()));
            tvPickingFragile.setText("中    包(包)：" + String.valueOf(orderInfo.getPackage3Qty()));
            tvPickingRemark.setText("备注：" + orderInfo.getRemark());
        }
    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {
        reqOrderInfo();
    }

    /**
     * 订单信息数据请求
     */
    private void reqOrderInfo() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetDeliverOrderInfo"));
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("OrderId", strOrderID);
        getService().GetDeliverOrderInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<GetDeliverOrderInfo>>() {
            @Override
            public void onResponse(ApiResponse<GetDeliverOrderInfo> result, int code, String msg) {
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        orderInfo = result.getData();
                        if (orderInfo != null) {
                            setValue(orderInfo);
                        }
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<GetDeliverOrderInfo>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }

    }

}
