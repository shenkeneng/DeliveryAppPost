package com.frxs.delivery.model;

import java.io.Serializable;
import java.util.List;

/**
 * 等待配送列表 by Tiepier
 */
public class GetWaitDeliverInfo implements Serializable {

    private static final long serialVersionUID = 1995937153807271402L;

    private String TotalCount;//总订单统计
    private String WaitDeliverCount;//等待配送统计
    private String PickingCount;//正在拣货统计
    private double TotalAmount;// 总金额
    private List<WaitDeliverData> WaitDeliverData;//等待配送列表
    private List<PickingData> PickingData;//正在拣货列表
    private List<ShippingDataGrouped> ShippingDataGrouped;// 所有车次


    public String getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(String totalCount) {
        TotalCount = totalCount;
    }

    public String getWaitDeliverCount() {
        return WaitDeliverCount;
    }

    public void setWaitDeliverCount(String waitDeliverCount) {
        WaitDeliverCount = waitDeliverCount;
    }

    public String getPickingCount() {
        return PickingCount;
    }

    public void setPickingCount(String pickingCount) {
        PickingCount = pickingCount;
    }

    public List<com.frxs.delivery.model.WaitDeliverData> getWaitDeliverData() {
        return WaitDeliverData;
    }

    public void setWaitDeliverData(List<com.frxs.delivery.model.WaitDeliverData> waitDeliverData) {
        WaitDeliverData = waitDeliverData;
    }

    public List<com.frxs.delivery.model.PickingData> getPickingData() {
        return PickingData;
    }

    public void setPickingData(List<com.frxs.delivery.model.PickingData> pickingData) {
        PickingData = pickingData;
    }

    public double getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        TotalAmount = totalAmount;
    }

    public List<com.frxs.delivery.model.ShippingDataGrouped> getShippingDataGrouped() {
        return ShippingDataGrouped;
    }

    public void setShippingDataGrouped(List<com.frxs.delivery.model.ShippingDataGrouped> shippingDataGrouped) {
        ShippingDataGrouped = shippingDataGrouped;
    }
}
