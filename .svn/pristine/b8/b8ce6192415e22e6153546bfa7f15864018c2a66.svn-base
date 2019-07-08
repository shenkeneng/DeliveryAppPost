package com.frxs.delivery;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.frxs.delivery.comms.Config;

/**
 * Created by Chentie on 2017/8/25.
 */

public class SignSuceessActivity extends FrxsActivity {

    private TextView tvShopName;
    private TextView tvFinish;
    private TextView tvLookSign;
    private String orderID;
    private String signID;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_suceess;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.tv_title_right).setVisibility(View.INVISIBLE);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText("完成签名");
        tvShopName = (TextView) findViewById(R.id.tv_shop_name);
        tvFinish = (TextView) findViewById(R.id.tv_finish);
        tvLookSign = (TextView) findViewById(R.id.tv_look_sign);
    }

    @Override
    protected void initEvent() {
        tvFinish.setOnClickListener(this);
        tvLookSign.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            String shopName = intent.getStringExtra("shop_name");
            orderID = intent.getStringExtra("order_id");
            signID = intent.getStringExtra("sign_id");
            String signSize = intent.getStringExtra("sign_size");
            String pictureSize = intent.getStringExtra("picture_size");
            if (Config.networkEnv == 1 && !TextUtils.isEmpty(signSize) && !TextUtils.isEmpty(pictureSize)) {
                tvShopName.setText(String.format(getString(R.string.shop_delied_sucess_test), shopName, signSize, pictureSize));
            } else {
                tvShopName.setText(String.format(getString(R.string.shop_delied_sucess), shopName));
            }
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_finish:
                finish();
                break;

            case R.id.tv_look_sign:
                Intent intent = new Intent(this, LookSignatureActivity.class);
                intent.putExtra("order_id", orderID);
                intent.putExtra("sign_id", signID);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
