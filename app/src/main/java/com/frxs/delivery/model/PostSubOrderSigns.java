package com.frxs.delivery.model;

/**
 * Created by Chentie on 2017/8/29.
 */

public class PostSubOrderSigns {

    private String OrderId;// 订单ID
    private int WID;
    private OrderSigns Signs;
    private int UserId;
    private String UserName;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public int getWID() {
        return WID;
    }

    public void setWID(int WID) {
        this.WID = WID;
    }

    public OrderSigns getSigns() {
        return Signs;
    }

    public void setSigns(OrderSigns signs) {
        Signs = signs;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
