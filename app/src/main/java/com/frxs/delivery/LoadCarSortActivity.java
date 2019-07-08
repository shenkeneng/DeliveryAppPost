package com.frxs.delivery;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.adapter.CartPagerAdapter;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.LoadCarSortData;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.MD5;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;

/**
 * Created by Chentie on 2017/10/26.
 */

public class LoadCarSortActivity extends FrxsActivity {

    private TextView tvCancle;
    private TextView tvConfirm;
    private RecyclerView recyclerView;
    private DragAdapter adapter;
    private List<LoadCarSortData.LoadCarSortDataBean> list = new ArrayList<LoadCarSortData.LoadCarSortDataBean>();
    private int carNum;
    private String from = "";
    private String shipDate;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_freight_car_sort;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.tv_title_left).setVisibility(View.INVISIBLE);
        findViewById(R.id.tv_title_right).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.tv_title)).setText(getString(R.string.load_car_sort));

        tvCancle = (TextView)findViewById(R.id.tv_cancel_sort);
        tvConfirm = (TextView)findViewById(R.id.tv_confirm_sort);
        recyclerView = (RecyclerView)findViewById(R.id.rvView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            carNum = intent.getIntExtra("car_num", 0);
            from = intent.getStringExtra("from");
            shipDate = intent.getStringExtra("ShipDate");
        }

    }

    @Override
    protected void initEvent() {
        tvCancle.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN ,ItemTouchHelper.RIGHT) {
            RecyclerView.ViewHolder vh = null;
            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder != null) {
                    vh = viewHolder;
                    vh.itemView.findViewById(R.id.ll_shop).setBackgroundResource(R.color.frxs_gray_dark);
                }
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(list, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
                viewHolder.itemView.findViewById(R.id.ll_shop).setBackgroundResource(R.drawable.shape_item);
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        new ItemTouchHelper(mCallback).attachToRecyclerView(recyclerView);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        getLoadCarShopSort();
    }

    private void getLoadCarShopSort() {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("GetWaitDeliverInfo"));
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(userInfo.getIsMaster())) {
            params.put("EmpId", String.valueOf(userInfo.getEmpID()));
            params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
            params.put("ShippingSerialNumber", carNum);
            params.put("ShipDate", shipDate);
        }
        getService().GetLoadCarSortInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<LoadCarSortData>>() {
            @Override
            public void onResponse(ApiResponse<LoadCarSortData> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        LoadCarSortData loadCarSortData = result.getData();
                        if (loadCarSortData.getLoadCarSortData() != null) {
                            list = loadCarSortData.getLoadCarSortData();
                            adapter = new DragAdapter(LoadCarSortActivity.this, list);
                            recyclerView.setAdapter(adapter);
                        } else {
                            ToastUtils.show(LoadCarSortActivity.this, "获取排单信息失败！");
                        }
                    }else {
                        ToastUtils.show(LoadCarSortActivity.this, "获取排单信息失败！");
                    }
                }else {
                    ToastUtils.show(LoadCarSortActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoadCarSortData>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(LoadCarSortActivity.this, t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_cancel_sort:
                if (from.equals("wait")) {
                    finish();
                } else { // 从扫码装车页面进入排序页面 按取消时排序方式为待装区排序
                    setLoadCarSortInfo(1);
                }
                break;

            case R.id.tv_confirm_sort:
                setLoadCarSortInfo(0);
                break;

            default:
                break;
        }
    }

    /**
     * 排序方式（0：手动排序， 1：待装区排序）
     */
    private void setLoadCarSortInfo(int sortType) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("ShippingSerialNumber", carNum);
        params.put("ShipDate", shipDate);
        if (sortType == 0) {
            String shopNumbers = getShopNumbers();
            params.put("ShopNumbers", shopNumbers);
        } else {
            params.put("IsStationNumber", 1);
        }
        getService().SetLoadCarSortInfo(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    ToastUtils.show(LoadCarSortActivity.this, "排序成功");
                    finish();
                } else {
                    ToastUtils.show(LoadCarSortActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                ToastUtils.show(LoadCarSortActivity.this, t.getMessage());
            }
        });
    }

    private String getShopNumbers() {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(list.get(i).getShopID()).append(":").append(i + 1);
        }
        return sb.toString();
    }


    public class DragAdapter extends RecyclerView.Adapter<DragAdapter.ViewHolder> {

        private Context context;
        private List<LoadCarSortData.LoadCarSortDataBean> list;

        public DragAdapter(Context context, List<LoadCarSortData.LoadCarSortDataBean> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shop_srot,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.shopName.setText(list.get(position).getShopName());
            holder.shopNum.setText(String.valueOf(position + 1));
            holder.shopNum.setText(String.valueOf(position+1));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder {
            public TextView shopNum;
            public TextView shopName;
            public ViewHolder(View view){
                super(view);
                shopNum = (TextView) view.findViewById(R.id.tv_shop_num);
                shopName = (TextView) view.findViewById(R.id.tv_shop_name);
            }
        }
    }


}
