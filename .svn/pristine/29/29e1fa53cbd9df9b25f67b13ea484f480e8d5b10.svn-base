package com.frxs.delivery;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.BillData;
import com.frxs.delivery.model.BillList;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.DensityUtils;
import com.frxs.delivery.utils.MD5;
import com.frxs.delivery.utils.MathUtils;
import com.frxs.delivery.widget.NoScrollGridView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import retrofit2.Call;


/**
 * 对账单 by Tiepier
 */
public class BillActivity extends FrxsActivity {

    private TextView tvTitle;//标题

    private TextView tvLeft;//返回

    private TextView tvRight;

    private ListView lvBill;

    private QuickAdapter<BillList> billAdapter;

    private NoScrollGridView mPopGrid;

    private LinearLayout mPopContent;

    private PopupWindow mWindow;

    private View mPopView;

    private String[] mBillTime;

    private int mCurrentType = 0;

    private BillData billData;

    private TextView tvBillCount;//总订单数

    private TextView tvBillIntegral;//总积分

    private TextView tvBillAmount;//总金额

    private String strNowTime;//系统当前年月

    private String strLastTime;//系统上一个月份

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bill;
    }

    @Override
    protected void initViews() {

        /**
         * 实例化控件
         */
        tvLeft = (TextView) findViewById(R.id.tv_title_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_title_right);
        lvBill = (ListView) findViewById(R.id.lv_bill);

        tvTitle.setText(R.string.title_bill);
        tvRight.setVisibility(View.INVISIBLE);

        tvBillCount = (TextView) findViewById(R.id.tv_bill_count);
        tvBillIntegral = (TextView) findViewById(R.id.tv_bill_integral);
        tvBillAmount = (TextView) findViewById(R.id.tv_bill_amount);

        /**
         * 弹窗选择处理
         */
        mBillTime = getResources().getStringArray(R.array.bill_time);
        tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_white_down, 0);
        mPopView = LayoutInflater.from(this).inflate(R.layout.pop_select_bill, null);
        mPopContent = (LinearLayout) mPopView.findViewById(R.id.content);
        mPopGrid = (NoScrollGridView) mPopView.findViewById(R.id.gridView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_btn, R.id.btn, mBillTime);
        mPopGrid.setAdapter(adapter);
        mWindow = new PopupWindow(mPopView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mWindow.setAnimationStyle(R.style.ZoomAnimation);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        mWindow.setBackgroundDrawable(dw);
        mWindow.setOutsideTouchable(true);

        /**
         * 获取系统事件处理
         */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //系统当前时间
        Date now = calendar.getTime();
        //系统上月时间
        calendar.add(Calendar.MONTH, -1); //月份减1
        Date lastMonth = calendar.getTime(); //结果
        //时间格式化
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        strNowTime = String.valueOf(sf.format(now));
        strLastTime = String.valueOf(sf.format(lastMonth));

    }

    /**
     * 设置对账单信息
     */
    private void setValue(BillData billData) {
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (userInfo.getIsEnabledFreightCar() == 0) {
            findViewById(R.id.ll_freightcar_no).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_orders_amt).setVisibility(View.VISIBLE);
            tvBillCount.setText(String.valueOf(billData.getTotalOrderCount()));//总订单数
            tvBillIntegral.setText(MathUtils.twolittercountString(billData.getTotalPoint()));//总积分
            tvBillAmount.setText("￥" + MathUtils.twolittercountString(billData.getTotalProductAmt()));//总金额
        } else {
            findViewById(R.id.ll_freightcar_yes).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_orders_amt).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.tv_bill_point)).setText(MathUtils.twolittercountString(billData.getTotalPoint2()));
            ((TextView)findViewById(R.id.tv_bill_remark)).setText(String.format(getString(R.string.point_tv), MathUtils.twolittercountString(billData.getTotalBasePoint())));
        }
    }

    @Override
    protected void initData() {
        billAdapter = new QuickAdapter<BillList>(this, R.layout.item_bill_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, final BillList item) {
                helper.setText(R.id.tv_bill_time, item.getPackingTime());//对账单时间
                UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
                if (userInfo.getIsEnabledFreightCar() == 0) {
                    helper.setText(R.id.tv_bill_count, String.valueOf(item.getTotalOrderCount()));//订单数量
                    helper.setText(R.id.tv_bill_integral, MathUtils.twolittercountString(item.getTotalPoint()));//积分
                    helper.setText(R.id.tv_bill_amount, "￥" + MathUtils.twolittercountString(item.getTotalProductAmt()));//金额
                } else {
                    helper.setText(R.id.tv_bill_count, MathUtils.twolittercountString(item.getTotalBasePoint()));//绩效分
                    helper.setText(R.id.tv_bill_integral, MathUtils.twolittercountString(item.getBasePointExt()));//附加分
                    helper.setText(R.id.tv_bill_amount, MathUtils.twolittercountString(item.getAdjPoint()));//调整分
                }
                /**
                 * 查看对账单详情
                 */
                helper.setOnClickListener(R.id.ll_bill_info, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BillActivity.this, BillDetailsActivity.class);
                        intent.putExtra("TIME", item.getPackingTime());
                        startActivity(intent);

                    }
                });
            }
        };
        lvBill.setAdapter(billAdapter);
        reqBillList(strNowTime);
    }

    @Override
    protected void initEvent() {
        /**
         * 标题点击监听事件
         */
        tvTitle.setOnClickListener(this);

        /**
         * 弹窗监听事件
         */
        mPopView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mWindow.dismiss();
            }
        });

        /**
         * 弹窗内容选择监听事件
         */
        mPopGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取某个指定position的view，并对该view进行刷新。
                mPopGrid.getChildAt(mCurrentType).findViewById(R.id.btn).setSelected(false);
                view.findViewById(R.id.btn).setSelected(true);
                /**
                 *
                 */
                switch (position) {
                    // 对账单-本月
                    case 0:
                        tvTitle.setText("配送明细·" + mBillTime[position]);
                        setPosition(position);
                        reqBillList(strNowTime);
                        break;
                    // 对账单-上月
                    case 1:
                        tvTitle.setText("配送明细·" + mBillTime[position]);
                        setPosition(position);
                        reqBillList(strLastTime);
                        break;

                    default:
                        break;
                }
                mWindow.dismiss();
            }

        });

        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_white_down, 0);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_title:
                mWindow.showAsDropDown(view, 0, DensityUtils.dip2px(this, 1));
                mPopContent.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_zoomin2));
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_white_up, 0);
                break;
        }
    }

    /**
     * 对账单数据请求
     */
    public void reqBillList(String strTime) {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetSaleOrderTotalInfo"));
        params.put("SearchMonth", strTime);
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("LineIDs", userInfo.getLineIDs());
        getService().GetSaleOrderTotalInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<BillData>>() {
            @Override
            public void onResponse(ApiResponse<BillData> result, int code, String msg) {
                if (result != null) {
                    if (result.getFlag().equals("0")) {
                        billData = result.getData();
                        if (billData != null) {
                            setValue(billData);
                            //对账单列表
                            List<BillList> bl = billData.getSaleOrderData();
                            billAdapter.replaceAll(bl);
                        }
                    } else {
                        ToastUtils.show(BillActivity.this, result.getInfo());
                    }
                }
                dismissProgressDialog();
            }

            @Override
            public void onFailure(Call<ApiResponse<BillData>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
            }
        });
    }
    public void setPosition(int position) {
        mCurrentType = position;
    }
}
