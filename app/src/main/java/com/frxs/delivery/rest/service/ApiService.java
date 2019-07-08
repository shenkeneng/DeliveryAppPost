package com.frxs.delivery.rest.service;


import com.frxs.delivery.model.AppVersionGetRespData;
import com.frxs.delivery.model.BillData;
import com.frxs.delivery.model.FinshDeliverOders;
import com.frxs.delivery.model.GetDeliverOrderInfo;
import com.frxs.delivery.model.GetDeliverOrderList;
import com.frxs.delivery.model.GetDeliverProductInfo;
import com.frxs.delivery.model.GetWaitDeliverInfo;
import com.frxs.delivery.model.LoadCarSortData;
import com.frxs.delivery.model.OrderSigns;
import com.frxs.delivery.model.PostSubOrderSigns;
import com.frxs.delivery.model.PostSubmitTakeOrder;
import com.frxs.delivery.model.PostSweepLoading;
import com.frxs.delivery.model.SaleBackOrderInfo;
import com.frxs.delivery.model.ScanLoadingInfo;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.model.WaitReceiveList;
import com.frxs.delivery.rest.model.ApiResponse;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
public interface ApiService {

    /****************************************GET请求************************************************/
    /**
     * 版本更新
     *
     * @param jsonData
     * @return
     */
//    @GET("Api?ActionName=AppVersion.UpdateGet&format=JSON")
//    Call<ApiResponse<AppVersionGetRespData>> GetAppVersion(@Query("Data") String jsonData);

    /****************************************POST请求************************************************/

    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("Deliver/DeliverLogin")
    Call<ApiResponse<UserInfo>> DeliverLogin(@FieldMap Map<String, Object> params);

    /**
     * 修改密码
     */
    @FormUrlEncoded
    @POST("Deliver/DeliverUpdatePwd")
    Call<ApiResponse<Object>> DeliverUpdatePwd(@FieldMap Map<String, Object> params);

    /**
     * 等待配送
     */
    @FormUrlEncoded
    @POST("Deliver/GetWaitDeliverInfo")
    Call<ApiResponse<GetWaitDeliverInfo>> GetWaitDeliverInfo(@FieldMap Map<String, Object> params);

    /**
     * 完成装车
     */
    @FormUrlEncoded
    @POST("Deliver/SetDeliverStatus")
    Call<ApiResponse<Object>> SetDeliverStatus(@FieldMap Map<String, Object> params);

    /**
     * 正在配送
     */
    @FormUrlEncoded
    @POST("Deliver/GetDeliverOrderList")
    Call<ApiResponse<GetDeliverOrderList>> GetDeliverOrderList(@FieldMap Map<String, Object> params);

    /**
     * 完成配送
     */
    @FormUrlEncoded
    @POST("Deliver/SetDeliveredStatus")
    Call<ApiResponse<Object>> SetDeliveredStatus(@FieldMap Map<String, Object> params);

    /**
     * 订单信息
     */
    @FormUrlEncoded
    @POST("Deliver/GetDeliverOrderInfo")
    Call<ApiResponse<GetDeliverOrderInfo>> GetDeliverOrderInfo(@FieldMap Map<String, Object> params);

    /**
     * 商品清单
     */
    @FormUrlEncoded
    @POST("Deliver/GetDeliverProductInfo")
    Call<ApiResponse<GetDeliverProductInfo>> GetDeliverProductInfo(@FieldMap Map<String, Object> params);

    /**
     * 对账单
     */
    @FormUrlEncoded
    @POST("Deliver/GetSaleOrderTotalInfo")
    Call<ApiResponse<BillData>> GetSaleOrderTotalInfo(@FieldMap Map<String, Object> params);

    /**
     * 对账详情
     */
    @FormUrlEncoded
    @POST("Deliver/GetSaleOrderDetailInfo")
    Call<ApiResponse<BillData>> GetSaleOrderDetailInfo(@FieldMap Map<String, Object> params);

    /**
     * 版本更新
     */
    @FormUrlEncoded
    @POST("AppVersion/AppVersionUpdateGet")
    Call<ApiResponse<AppVersionGetRespData>> AppVersionUpdateGet(@FieldMap Map<String, Object> params);

    /**
     * 是否有超过规定时间未完成配送的订单
     */
    @FormUrlEncoded
    @POST("Deliver/CountOverTimeDeliverOrder")
    Call<ApiResponse<Integer>> GetOverTimeDeliverOrder(@FieldMap Map<String, Object> params);

    /**
     * 统计未配送完成订单数及配送车次
     */
    @FormUrlEncoded
    @POST("Deliver/CountUnfinishedOrderAndShippingNumber")
    Call<ApiResponse<JsonObject>> CountUnfinishedOrderAndShippingNumber(@FieldMap Map<String, Object> params);

    /**
     * 货配车扫码预装车接口
     */
    @FormUrlEncoded
    @POST("Deliver/PreSweepLoading")
    Call<ApiResponse<ScanLoadingInfo>> PreSweepLoading(@FieldMap Map<String, Object> params);

    /**
     * 货配车扫码预装车接口
     */
    @POST("Deliver/SweepLoading")
    Call<ApiResponse<List<Object>>> SweepLoading(@Body PostSweepLoading editCart);

    /**
     * 获取退货申请单数据
     */
    @FormUrlEncoded
    @POST("Deliver/GetApplyForSaleBackPageList")
    Call<ApiResponse<WaitReceiveList>> GetApplyForSaleBackList(@FieldMap Map<String, Object> params);

    /**
     * 获取退货申请单详情
     */
    @FormUrlEncoded
    @POST("Deliver/GetFullApplyForSaleBackDetailList")
    Call<ApiResponse<List<SaleBackOrderInfo>>> GetApplyForSaleBackInfo(@FieldMap Map<String, Object> params);

    /**
     * 完成收货
     */
    @POST("Deliver/SubmitApplyForSaleBackTake")
    Call<ApiResponse<Boolean>> SubmitApplyForSaleBackTake(@Body PostSubmitTakeOrder submitTakeOrder);

    /**
     * 获取完成配送订单列表
     */
    @FormUrlEncoded
    @POST("Deliver/GetFinshDeliverOrders")
    Call<ApiResponse<FinshDeliverOders>> GetFinshDliverOrders(@FieldMap Map<String, Object> params);

    /**
     * 上传签名内容
     * @return
     */
    @POST("Deliver/OrderSigns")
    Call<ApiResponse<String>> SubOrderSigns (@Body PostSubOrderSigns postSubOrderSigns);

    /**
     * 上传签名、现场图片
     * @param parts
     * @return
     */
    @Multipart
    @POST("ImageApi/SaveShopSignImages")
    Call<String> SubmitSignImage (@PartMap Map<String, RequestBody> parts);

    /**
     * 查看签名内容
     * @return
     */
    @FormUrlEncoded
    @POST("Deliver/GetOrderSigns")
    Call<ApiResponse<OrderSigns>> GetOrderSign (@FieldMap Map<String, Object> params);

    /**
     * 电子签名门店签署验证
     * @return
     */
    @FormUrlEncoded
    @POST("Deliver/SignsAuthentication")
    Call<ApiResponse<String>> ShopSignVerify (@FieldMap Map<String, Object> params);

    /**
     * 获取当前车次门店排序信息
     * @return
     */
    @FormUrlEncoded
    @POST("Deliver/GetLoadCarSortInfo")
    Call<ApiResponse<LoadCarSortData>> GetLoadCarSortInfo (@FieldMap Map<String, Object> params);

    /**
     * 提交当前车次门店排序信息
     * @return
     */
    @FormUrlEncoded
    @POST("Deliver/SetLoadCarSortInfo")
    Call<ApiResponse<String>> SetLoadCarSortInfo (@FieldMap Map<String, Object> params);
}
