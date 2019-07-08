package com.frxs.delivery.model;

/**
 * Created by Chentie on 2017/5/18.
 */

public class SweepLoading {

    /*"OrderId": "200000000647",
                "TotalProductAmt": 5808.0000*/

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
