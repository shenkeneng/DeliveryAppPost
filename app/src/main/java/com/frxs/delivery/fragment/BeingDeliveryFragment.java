package com.frxs.delivery.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.FrxsActivity;
import com.frxs.delivery.OrderDetailActivity;
import com.frxs.delivery.R;
import com.frxs.delivery.SignatureActivity;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.comms.GlobelDefines;
import com.frxs.delivery.model.GetDeliverOrderList;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.model.WaitDeliverData;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;

/**
 * 正在配送 by Tipier
 */
public class BeingDeliveryFragment extends FrxsFragment {

    private TextView tvLeft;
    private TextView tvTitle;//标题
    private TextView tvRefresh;//刷新
    private TextView tvDeliveryCount;//配送门店数
    private ListView lvDeliveryBeing;//正在配送列表
    private QuickAdapter<WaitDeliverData> beingAdapter;
    private GetDeliverOrderList beingDeliveryInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_being_delivery;
    }

    @Override
    protected void initViews(View view) {
        /**
         * 实例化控件
         */
        tvLeft = (TextView) view.findViewById(R.id.tv_title_left);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);//标题
        tvRefresh = (TextView) view.findViewById(R.id.tv_title_right);//刷新
        tvDeliveryCount = (TextView) view.findViewById(R.id.tv_delivery_count);//配送门店数
        lvDeliveryBeing = (ListView) view.findViewById(R.id.lv_being_delivery);//正在配送列表

        tvLeft.setVisibility(View.INVISIBLE);

        tvTitle.setText(R.string.title_being_delivery);//正在配送

    }

    private void setValue(GetDeliverOrderList beingDeliveryInfo) {
        tvDeliveryCount.setText("配送门店数：" + beingDeliveryInfo.getDeliverCount());
    }

    @Override
    protected void initEvent() {
        tvRefresh.setOnClickListener(this);
    }

    @Override
    protected void initData() {
            beingAdapter = new QuickAdapter<WaitDeliverData>(mActivity, R.layout.item_being_delivery) {
                @Override
                protected void convert(BaseAdapterHelper helper, final WaitDeliverData item) {
                    /**
                     * 判断当前账号是否是组长（是组长隐藏操作按钮）
                     */
                    final UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
                    if (!TextUtils.isEmpty(userInfo.getIsMaster())) {
                        if (userInfo.getIsMaster().equals("1")) {
                            helper.setVisible(R.id.line, false);
                            helper.setVisible(R.id.ll_submit, false);
                        } else {
                            helper.setVisible(R.id.line, true);
                            helper.setVisible(R.id.ll_submit, true);
                        }
                    }
                    /**
                     * 流水号处理
                     */
                    String strStationNumber = item.getStationNumber();
                    if (strStationNumber.equals("")) {
                        strStationNumber = "0";
                    }
                    helper.setText(R.id.tv_order_id, "订单：" + item.getOrderId());//订单编号
                    helper.setText(R.id.tv_store_name, "门店：" + item.getShopName());//门店名称
                    helper.setText(R.id.tv_order_count, String.format("%1$03d", Integer.valueOf(strStationNumber)));//流水号
                    helper.setText(R.id.tv_store_info, "店主：" + item.getRevLinkMan() + " " + item.getRevTelephone());//门店信息


                    /**
                     * 拨打电话
                     */
                    helper.setOnClickListener(R.id.img_call, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item.getRevTelephone()));
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
                     * 完成配送
                     */
                    helper.setOnClickListener(R.id.tv_call, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (CommonUtils.isFastDoubleClick()) {
                                return;
                            }
                            if (userInfo.getIsSigns() == 0) {   //0：不启用；
                                showDialog(item.getOrderId());
                            } else if (userInfo.getIsSigns() == 1 || userInfo.getIsSigns() == 2) {
                                if (checkGPSIsOpen()) {
                                    showSignAllDialog(item);
                                } else {
                                    openGPSSettings(item);
                                }
                            } else {
                                ToastUtils.show(mActivity, "电子签名功能未启用");
                            }
                        }
                    });

                    /**
                     * 订单车次 (当前模式为货配车模式并且该订单有车次)
                     */
                    TextView cartNumTv = helper.getView(R.id.tv_cart_num);
                    LinearLayout pointLl = helper.getView(R.id.ll_point);
                    LinearLayout payTypeLl = helper.getView(R.id.ll_pay_type);
                    if (1 == userInfo.getIsEnabledFreightCar() && item.getShippingSerialNumber() > 0) {
                        cartNumTv.setVisibility(View.VISIBLE);
                        cartNumTv.setText("车次：" + String.valueOf(item.getShippingSerialNumber()));
                        pointLl.setVisibility(View.VISIBLE);
                        helper.setText(R.id.tv_performance_point, getString(R.string.tv_base_point) + "：" + MathUtils.twolittercountString(item.getTotalBasePoint()));
                        helper.setText(R.id.tv_attach_point, getString(R.string.tv_point_ex) + "：" + MathUtils.twolittercountString(item.getBasePointExt()));
                        payTypeLl.setVisibility(View.GONE);
                    } else {
                        cartNumTv.setVisibility(View.GONE);
                        pointLl.setVisibility(View.GONE);
                        payTypeLl.setVisibility(View.VISIBLE);
                        /**
                         * 结算方式显示处理
                         */
                        if (item.getSettleTypeName().toString().equals("司机带回")) {
                            helper.setTextColor(R.id.tv_payment_name, Color.parseColor("#d80c18"));
                        } else {
                            helper.setTextColor(R.id.tv_payment_name, Color.parseColor("#00cc66"));
                        }
                        helper.setText(R.id.tv_payment_name, item.getSettleTypeName());//结算方式
                    }

                }
            };
        lvDeliveryBeing.setAdapter(beingAdapter);
        reqDeliveryBeing();
    }

    private void showSignAllDialog(final WaitDeliverData item) {
        if (((FrxsActivity) mActivity).hasSignPermissions()) {
            final StringBuffer orders = new StringBuffer(item.getOrderId());
            double orderAmt = item.getPayAmount();
            if (FrxsApplication.getInstance().getUserInfo().getIsEnabledFreightCar() == 1) {
                if (TextUtils.isEmpty(item.getShipDate()) && item.getShippingSerialNumber() <= 0) {
                    ToastUtils.show(mActivity, "暂无车次或指派日期信息，无法完成配送");
                    return;
                }
                for (WaitDeliverData wd : beingDeliveryInfo.getWaitDeliverData()) {
                    if (item.getShippingSerialNumber() == wd.getShippingSerialNumber() && item.getShipDate().equals(wd.getShipDate())
                            && item.getShopID() == wd.getShopID() && !item.getOrderId().equals(wd.getOrderId())) {
                        orders.append(",").append(wd.getOrderId());
                        orderAmt = MathUtils.add(orderAmt, wd.getPayAmount());
                    }
                }
            }
            final double allOrderAmt = orderAmt;
            List<String> orderList = Arrays.asList(String.valueOf(orders).split(","));
            if (orderList.size() > 1) {
                final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.setContentView(R.layout.dialog_selector);
                dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
                TextView messageTv = (TextView) dialog.findViewById(R.id.tv_message);
                messageTv.setText(String.format(getString(R.string.dialog_sign_all), item.getShopName(),
                        String.valueOf(item.getShippingSerialNumber()), String.valueOf(orderList.size()), orderAmt));
                dialog.show();
                Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
                Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);// 取消
                TextView closeTv = (TextView) dialog.findViewById(R.id.id_close);
                closeTv.setVisibility(View.VISIBLE);
                btnConfirm.setText("是");
                btnCancel.setText("否");
                btnConfirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        gotoSignatureActivity(item, String.valueOf(orders), allOrderAmt);//合并签名
                        dialog.dismiss();

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        gotoSignatureActivity(item, item.getOrderId(), item.getPayAmount());//单笔签名
                        dialog.dismiss();
                    }
                });

                closeTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            } else {
                gotoSignatureActivity(item, item.getOrderId(), item.getPayAmount());//单笔签名
            }
        }  else {
            ToastUtils.show(mActivity, "请开启位置、相机权限才能继续完成配送");
        }
    }

    private void gotoSignatureActivity(final WaitDeliverData item, String orderIds, double orderAmt) {
        dismissProgressDialog();
        Intent intent = new Intent(mActivity, SignatureActivity.class);
        intent.putExtra("from", "Delivery");
        intent.putExtra("order_id", orderIds);
        intent.putExtra("shop_id", item.getShopID());
        intent.putExtra("shop_name", item.getShopName());
        intent.putExtra("shop_code", String.valueOf(item.getShopCode()));
        intent.putExtra("sign_amount", orderAmt);
        startActivity(intent);  //1：启用不要验证身份；2：启用并验证身份
    }

    @Override
    public void onResume() {
        super.onResume();
        reqDeliveryBeing();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_title_right:
                reqDeliveryBeing();
                break;
        }
    }

    /**
     * 正在配送数据请求
     */
    public void reqDeliveryBeing() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetWaitDeliverInfo"));
        if (!TextUtils.isEmpty(userInfo.getIsMaster())){
            if (userInfo.getIsMaster().equals("1")){
                params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
            }else{
                params.put("EmpId", String.valueOf(userInfo.getEmpID()));
                params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
                params.put("LineIDs", userInfo.getLineIDs());
            }
        }
        getService().GetDeliverOrderList(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<GetDeliverOrderList>>() {
            @Override
            public void onResponse(ApiResponse<GetDeliverOrderList> result, int code, String msg) {
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        beingDeliveryInfo = result.getData();
                        if (beingDeliveryInfo != null) {
                            setValue(beingDeliveryInfo);
                            //正在配送列表
                            List<WaitDeliverData> wd = beingDeliveryInfo.getWaitDeliverData();
                            beingAdapter.replaceAll(wd);
                            if (!(beingDeliveryInfo.getDeliverCount() > 0)){
                                ToastUtils.show(mActivity, "正在配送列表暂无订单");
                            }
                        }
                    } else {
                        ToastUtils.show(mActivity, result.getInfo());
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<GetDeliverOrderList>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(mActivity, t.getMessage());
            }
        });

    }

    /**
     * 完成配送数据请求
     */
    private void reqCompleteDelivery(String strOrderID) {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("SetDeliveredStatus"));
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("OrderId", strOrderID);
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("EmpName", userInfo.getEmpName());
        getService().SetDeliveredStatus(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Object>>() {

            @Override
            public void onResponse(ApiResponse<Object> result, int code, String msg) {
                if (result.getFlag().equals("0")) {
                    reqDeliveryBeing();
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }

    private void showDialog(final String strOrderID) {
        final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_selector);
        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
        dialog.show();
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);// 取消
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                reqCompleteDelivery(strOrderID);//完成配送
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
     * 检测GPS是否打开
     *
     * @return
     */
    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps && network) {
            return true;
        }

        return false;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings(WaitDeliverData item) {
        if (checkGPSIsOpen()) {
            showSignAllDialog(item);
        } else {
            //没有打开则弹出对话框
            AlertDialog dialog = new AlertDialog.Builder(mActivity)
                    .setTitle("提示")
                    .setMessage("需进入下一步必须打开GPS功能。")
                    .setPositiveButton("去开启",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //跳转GPS设置界面
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, GlobelDefines.REQ_GPS_CODE);
                                }
                            })
                    // 拒绝, 退出应用
                    .setNegativeButton("取消", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}
