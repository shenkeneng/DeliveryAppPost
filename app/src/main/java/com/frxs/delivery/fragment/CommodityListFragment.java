package com.frxs.delivery.fragment;


import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.frxs.delivery.R;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.GetDeliverProductInfo;
import com.frxs.delivery.model.ProdcutDetailList;
import com.frxs.delivery.model.ProductData;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.DensityUtils;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;


/**
 * 商品清单 by Tipier
 */
public class CommodityListFragment extends FrxsFragment {

    private RadioGroup radioGroupTab;

    private HorizontalScrollView mHorizontalScrollView;

    private ImageView mSliderImageView;

    private float mCurrentCheckedRadioLeft;   // 当前被选中的RadioButton距离左侧的距离

    private ListView lvCommodityList;//商品清单

    private QuickAdapter<ProdcutDetailList> productAdapter;

    private GetDeliverProductInfo productInfo;

    private String strOrderID;//订单ID

    private static final int BASEID = 1000;//RadioButton动态ID

    private List<View> mRaidoButtonList = new ArrayList<View>();

    private LayoutInflater mInflater;

    private TextView tvTypeSum;// 大货商品总数

    private TextView tvTypeLine;// 大货总条目数

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_commodity_list;
    }

    @Override
    protected void initViews(View view) {
        /**
         * 实例化控件
         */
        radioGroupTab = (RadioGroup) view.findViewById(R.id.radio_group);
        mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontal_scrollview);
        mSliderImageView = (ImageView) view.findViewById(R.id.img);
        lvCommodityList = (ListView) view.findViewById(R.id.lv_commodity_list);
        tvTypeSum = (TextView) view.findViewById(R.id.tv_type_sum);
        tvTypeLine = (TextView) view.findViewById(R.id.tv_type_line);
        /**
         * 获取订单ID
         */
        Bundle bundle = getArguments();
        if (bundle != null) {
            strOrderID = bundle.getString("ORDERID");
        }
        /**
         * 货区分类切换事件监听
         */
        radioGroupTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                List<ProductData> productDataList = productInfo.getProductData();
                if (checkedId >= BASEID && checkedId < BASEID + productDataList.size()) {
                    int offset = checkedId - BASEID;
                    /**
                     * 设置RadioButton底部线条切换动画
                     */
                    AnimationSet animationSet = new AnimationSet(true);
                    TranslateAnimation translateAnimation;
                    translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, getResources().getDimension(
                            R.dimen.radio_button)
                            * (offset), 0, 0);
                    animationSet.addAnimation(translateAnimation);
                    animationSet.setFillBefore(true);
                    animationSet.setFillAfter(true);
                    animationSet.setDuration(100);
                    mSliderImageView.startAnimation(animationSet);
                    /**
                     * 设置获取名称对应的商品内容
                     */
                    ProductData productData = productDataList.get(offset);
                    List<ProdcutDetailList> typeProductList = productData.getProdcutDetailList();
                    productAdapter.replaceAll(typeProductList);
                    /**
                     * 设置商品的总数量和条目数
                     */
                    double sum = 0;
                    for (ProdcutDetailList prodcutList : typeProductList) {
                        tvTypeLine.setText("（共计" +  typeProductList.size() + "行）");
                        sum += prodcutList.getSaleQty();
                    }
                    tvTypeSum.setText("商品总数量：" + DensityUtils.subZeroAndDot(MathUtils.twolittercountString(sum)) + "        ");

                }
                /**
                 *设置RadioButton切换效果
                 */
                mCurrentCheckedRadioLeft = getCurrentCheckedRadioLeft();
                mHorizontalScrollView.smoothScrollTo((int) mCurrentCheckedRadioLeft - (int) getResources().getDimension(R.dimen.radio_button), 0);
            }
        });

    }

    @Override
    protected void initEvent() {
    }

    @Override
    protected void initData() {

        mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        productAdapter = new QuickAdapter<ProdcutDetailList>(mActivity, R.layout.item_commodity_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, ProdcutDetailList item) {
                helper.setText(R.id.tv_goods_name, item.getProductName());
                TextView tvGoodsCount = helper.getView(R.id.tv_goods_count);
                String strSaleUnit = item.getSaleUnit();
                if (strSaleUnit == null || TextUtils.isEmpty(strSaleUnit)) {
                    strSaleUnit = "";
                }
                double saleQty = item.getSaleQty() == null ? 0 : item.getSaleQty();
                tvGoodsCount.setText(Html.fromHtml("数量：<b><font color=red>" + DensityUtils.subZeroAndDot(MathUtils.twolittercountString(saleQty)) + strSaleUnit + "</font></b>"));
                helper.setText(R.id.tv_goods_remark, "备注：" + item.getRemark());
            }
        };
        lvCommodityList.setAdapter(productAdapter);
        requestCommodityList();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }

    }


    /**
     * 获得当前被选中的RadioButton距离左侧的距离
     */
    private float getCurrentCheckedRadioLeft() {
        for (int i = 0; i < radioGroupTab.getChildCount(); i++) {
            RadioButton radioBtn = (RadioButton) radioGroupTab.getChildAt(i);
            if (radioBtn.isChecked()) {
                return getResources().getDimension(R.dimen.radio_button) * i;
            }
        }

        return 0f;
    }

    /**
     * 设置货区类型
     */
    private void packageShelfAreaViews(List<ProductData> productDataList) {
        mRaidoButtonList.clear();
        for (int i = 0; i < productDataList.size(); i++) {
            ProductData item = productDataList.get(i);
            mInflater.inflate(R.layout.item_radio_button, radioGroupTab);
            RadioButton typeRadioBtn = (RadioButton) radioGroupTab.getChildAt(i);
            typeRadioBtn.setText(item.getShelfAreaType());
            typeRadioBtn.setId(BASEID + i);
            mRaidoButtonList.add(typeRadioBtn);
        }
        //默认选中第一个RadioButton
        RadioButton typeRadioBtnDefault = (RadioButton) radioGroupTab.getChildAt(0);
        typeRadioBtnDefault.setChecked(true);
    }


    /**
     * 商品清单数据请求
     */
    public void requestCommodityList() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetDeliverProductInfo"));
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("OrderId", strOrderID);
        getService().GetDeliverProductInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<GetDeliverProductInfo>>() {
            @Override
            public void onResponse(ApiResponse<GetDeliverProductInfo> result, int code, String msg) {
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        productInfo = result.getData();
                        if (productInfo != null) {

                            List<ProductData> productDataList = productInfo.getProductData();
                            if (null != productDataList && productDataList.size() > 0) {
                                packageShelfAreaViews(productDataList);

                                //商品清单, 默认加载第一个分类的列表
                                List<ProdcutDetailList> typeProductList = productDataList.get(0).getProdcutDetailList();
                                productAdapter.replaceAll(typeProductList);

                            }
                        }
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<GetDeliverProductInfo>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });

    }

}
