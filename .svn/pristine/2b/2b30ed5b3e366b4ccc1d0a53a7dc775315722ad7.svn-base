package com.frxs.delivery;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ewu.core.utils.CommonUtils;
import com.ewu.core.utils.EasyPermissionsEx;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.comms.GlobelDefines;
import com.frxs.delivery.widget.LinePathView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chentie on 2017/8/3.
 */
public class SignatureActivity extends FrxsActivity {

    LinePathView linePathView;
    TextView cleanTv;
    TextView saveTv;
    TextView titleTv;
    private TextView shopNameTv;
    private TextView orderIdTv;
    private TextView signAmtTv;
    private String orderId;// 订单ID
    private String shopCode;// 门店code
    private int shopId;// 门店ID
    private double signAmt;
    private String shopName;
    public static SignatureActivity signatureActivity;
    private Bitmap signImg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_signature;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.tv_title_right).setVisibility(View.INVISIBLE);
        linePathView = (LinePathView) findViewById(R.id.line_path_view);
        cleanTv = (TextView) findViewById(R.id.tv_clean);
        saveTv = (TextView) findViewById(R.id.tv_save);
        titleTv = (TextView) findViewById(R.id.tv_title);
        titleTv.setText("电子签名");
        shopNameTv = (TextView) findViewById(R.id.shop_name_tv);
        orderIdTv = (TextView) findViewById(R.id.order_id_tv);
        signatureActivity = this;
        signAmtTv = (TextView) findViewById(R.id.sign_amount_tv);
    }

    @Override
    protected void initEvent() {
        saveTv.setOnClickListener(this);
        cleanTv.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
//            from = intent.getStringExtra("from");
            orderId = intent.getStringExtra("order_id");
            shopId = intent.getIntExtra("shop_id", -1);
            shopCode = intent.getStringExtra("shop_code");
            shopName = intent.getStringExtra("shop_name");
            signAmt = intent.getDoubleExtra("sign_amount", 0f);
        }

        initOrderInfo();
    }

    private void initOrderInfo() {
        shopNameTv.setText(TextUtils.isEmpty(shopName) ? "" : shopName);
        List<String> orderIdList = Arrays.asList(orderId.split(","));
        if (orderIdList.size() > 1) {
            orderIdTv.setText(String.format(getString(R.string.order_id_kv), String.valueOf(orderIdList.size())));
        } else {
            orderIdTv.setText(String.format(getString(R.string.order_id_kv), orderId));
        }
        signAmtTv.setText(String.format(getString(R.string.sign_amt_kv), signAmt));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_clean:
                linePathView.clear();
                linePathView.setDisableSizeChange(false);
                break;

            case R.id.tv_save:
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                }
                if (checkGPSIsOpen()) {
                    hasSignPermission();
                } else {
                    openGPSSettings();
                }
                break;

            default:
                break;
        }
    }

    private void hasSignPermission() {
        if (EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA})) {
            if (!linePathView.getTouched()) {
                Toast.makeText(SignatureActivity.this, "您没有签名~", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                signImg = linePathView.save();
                linePathView.setDisableSizeChange(true);
                Intent intent = new Intent(SignatureActivity.this, TakePictureActivity.class);
                intent.putExtra("shop_name", shopName);
                intent.putExtra("order_id", orderId);
                intent.putExtra("shop_id", shopId);
                intent.putExtra("shop_code", shopCode);
                intent.putExtra("sign",  signImg);
                startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ToastUtils.show(this, "请开启位置、相机、手机权限才能继续完成配送");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        hasSignPermissions();
    }

    /**
     * 检测GPS是否打开
     *
     * @return
     */
    public boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
    private void openGPSSettings() {
        if (checkGPSIsOpen()) {
            hasSignPermission();
        } else {
            //没有打开则弹出对话框
            AlertDialog dialog = new AlertDialog.Builder(this)
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
