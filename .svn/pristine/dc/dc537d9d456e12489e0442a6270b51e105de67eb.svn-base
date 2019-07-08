package com.frxs.delivery.model;

import java.io.Serializable;
import java.util.List;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/05/12
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class ScanLoadingInfo implements Serializable{

    private int OrderNumber;// 订单总数

    private double TotalAmt;// 订单总金额

    private double ShippingAmt;//金额区间 下限

    private double ShippingMaxAmt;// 金额区间 上限

    private int ShippingSerialNumber;// 当前车次

    private String ShipDate;//指派日期

    private List<OrderInfo> OrderDetail;

    private int OrdersCount;

    private double MaxShopDistance;// 最远公里数

    private double TotalBasePoint;// 合计绩效积分

    private double BasePointExt; // 司机额外绩效积分

    private int ShopCount;// 门店数

    public int getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        OrderNumber = orderNumber;
    }

    public double getTotalAmt() {
        return TotalAmt;
    }

    public void setTotalAmt(double totalAmt) {
        TotalAmt = totalAmt;
    }

    public double getShippingAmt() {
        return ShippingAmt;
    }

    public void setShippingAmt(double shippingAmt) {
        ShippingAmt = shippingAmt;
    }

    public double getShippingMaxAmt() {
        return ShippingMaxAmt;
    }

    public void setShippingMaxAmt(double shippingMaxAmt) {
        ShippingMaxAmt = shippingMaxAmt;
    }

    public int getShippingSerialNumber() {
        return ShippingSerialNumber;
    }

    public void setShippingSerialNumber(int shippingSerialNumber) {
        ShippingSerialNumber = shippingSerialNumber;
    }

    public List<OrderInfo> getOrderDetail() {
        return OrderDetail;
    }

    public void setOrderDetail(List<OrderInfo> orderDetail) {
        OrderDetail = orderDetail;
    }

    public String getShipDate() {
        return ShipDate;
    }

    public void setShipDate(String shipDate) {
        ShipDate = shipDate;
    }

    public static class OrderInfo implements Serializable {

        private String OrderId;

        private double TotalProductAmt;


        public String getOrderId() {
            return OrderId;
        }

        public void setOrderId(String orderId) {
            OrderId = orderId;
        }

        public double getTotalProductAmt() {
            return TotalProductAmt;
        }

        public void setTotalProductAmt(double totalProductAmt) {
            TotalProductAmt = totalProductAmt;
        }
    }

    public int getOrdersCount() {
        return OrdersCount;
    }

    public void setOrdersCount(int ordersCount) {
        OrdersCount = ordersCount;
    }

    public double getMaxShopDistance() {
        return MaxShopDistance;
    }

    public void setMaxShopDistance(double maxShopDistance) {
        MaxShopDistance = maxShopDistance;
    }

    public double getTotalBasePoint() {
        return TotalBasePoint;
    }

    public void setTotalBasePoint(double totalBasePoint) {
        TotalBasePoint = totalBasePoint;
    }

    public double getBasePointExt() {
        return BasePointExt;
    }

    public void setBasePointExt(double basePointExt) {
        BasePointExt = basePointExt;
    }

    public int getShopCount() {
        return ShopCount;
    }

    public void setShopCount(int shopCount) {
        ShopCount = shopCount;
    }
}
