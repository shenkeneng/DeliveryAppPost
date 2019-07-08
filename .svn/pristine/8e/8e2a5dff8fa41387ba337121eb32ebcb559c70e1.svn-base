package com.frxs.delivery;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ewu.core.utils.ImageLoader;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.model.OrderSigns;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MathUtils;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/8/3.
 */

public class LookSignatureActivity extends FrxsActivity {

    TextView titleTv;
    ImageView ivSign;
    TextView signTimeTv;
    TextView signIdTv;
    TextView gpsTv;
    TextView addressTv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_look_signature;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.tv_title_right).setVisibility(View.INVISIBLE);
        titleTv = (TextView)findViewById(R.id.tv_title);
        titleTv.setText("电子签名明细");
        ivSign = (ImageView) findViewById(R.id.iv_sign);
        signTimeTv = (TextView) findViewById(R.id.tv_sign_time);
        gpsTv = (TextView) findViewById(R.id.tv_gps);
        signIdTv = (TextView) findViewById(R.id.tv_sign_id);
        addressTv = (TextView) findViewById(R.id.tv_address);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String signId = bundle.getString("sign_id");
            if (!TextUtils.isEmpty(signId)) {
                lookOrderSign(signId);
            } else {
                ToastUtils.show(this, "该订单暂无签名信息");
            }
        }
    }

    private void lookOrderSign(String signId) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("SignID", signId);

        getService().GetOrderSign(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<OrderSigns>>() {
            @Override
            public void onResponse(ApiResponse<OrderSigns> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        OrderSigns orderSigns = result.getData();
                        if (orderSigns.getSignUrl() != null) {
                            String signUrl = orderSigns.getSignUrl();
                            if (!orderSigns.getSignUrl().startsWith("http")) {
                                signUrl = "http://" + signUrl;
                            }
                            ImageLoader.loadImage(LookSignatureActivity.this, signUrl, ivSign, R.mipmap.showcase_product_default);
                        }

                        if (!TextUtils.isEmpty(orderSigns.getLng()) && !TextUtils.isEmpty(orderSigns.getLat())) {
                            gpsTv.setText(String.format(getString(R.string.gps), MathUtils.round(Double.valueOf(orderSigns.getLng()), 6), MathUtils.round(Double.valueOf(orderSigns.getLat()), 6)));
                        } else {
                            gpsTv.setText("GPS坐标：");
                        }

                        if (!TextUtils.isEmpty(orderSigns.getID())) {
                            signIdTv.setText(String.format(getString(R.string.sign_device_id), orderSigns.getMacID()));
                        } else {
                            signIdTv.setText(String.format(getString(R.string.sign_device_id), ""));
                        }

                        if (!TextUtils.isEmpty(orderSigns.getModifyTime())) {
                            signTimeTv.setText(String.format(getString(R.string.sign_time), orderSigns.getModifyTime()));
                        } else {
                            signTimeTv.setText(String.format(getString(R.string.sign_time), ""));
                        }

                        if (!TextUtils.isEmpty(orderSigns.getFullAddress())) {
                            addressTv.setText(String.format(getString(R.string.address), orderSigns.getFullAddress()));
                        } else {
                            addressTv.setText(String.format(getString(R.string.address), ""));
                        }

                    }
                } else {
                    ToastUtils.show(LookSignatureActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderSigns>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(LookSignatureActivity.this, t.getMessage());
            }
        });
    }
}
