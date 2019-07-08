package com.frxs.delivery.model;

import java.io.Serializable;

/**
 * 对账单&对账详情列表 by Tiepier
 */
public class BillList implements Serializable {


    private static final long serialVersionUID = 6010285927859766365L;

    /**
     * 对账单&对账详情公用
     */
    private double TotalProductAmt;//金额
    private double TotalPoint;//积分
    private double BasePointExt = 0.00;// 附加分
    private double TotalBasePoint;// 绩效分
    /**
     * 对账单
     */
    private String PackingTime;//时间
    private int TotalOrderCount;//订单数
    private double AdjPoint;//配送明细调整分
    /**
     * 对账详情
     */
    private String OrderId;//订单编号
    private String ShopCode;//门店编码
    private int ShippingSerialNumber;//车次
    private int type ;

    public String getPackingTime() {
        return PackingTime;
    }

    public void setPackingTime(String packingTime) {
        PackingTime = packingTime;
    }

    public double getTotalProductAmt() {
        return TotalProductAmt;
    }

    public void setTotalProductAmt(double totalProductAmt) {
        TotalProductAmt = totalProductAmt;
    }

    public int getTotalOrderCount() {
        return TotalOrderCount;
    }

    public void setTotalOrderCount(int totalOrderCount) {
        TotalOrderCount = totalOrderCount;
    }

    public double getTotalPoint() {
        return TotalPoint;
    }

    public void setTotalPoint(double totalPoint) {
        TotalPoint = totalPoint;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getShopCode() {
        return ShopCode;
    }

    public void setShopCode(String shopCode) {
        ShopCode = shopCode;
    }

    public double getBasePointExt() {
        return BasePointExt;
    }

    public void setBasePointExt(double basePointExt) {
        BasePointExt = basePointExt;
    }

    public double getTotalBasePoint() {
        return TotalBasePoint;
    }

    public void setTotalBasePoint(double totalBasePoint) {
        TotalBasePoint = totalBasePoint;
    }

    public int getShippingSerialNumber() {
        return ShippingSerialNumber;
    }

    public void setShippingSerialNumber(int shippingSerialNumber) {
        ShippingSerialNumber = shippingSerialNumber;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getAdjPoint() {
        return AdjPoint;
    }

    public void setAdjPoint(double adjPoint) {
        AdjPoint = adjPoint;
    }
}
