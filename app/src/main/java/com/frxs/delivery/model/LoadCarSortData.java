package com.frxs.delivery.model;

import java.util.List;

/**
 * Created by Chentie on 2017/10/30.
 */

public class LoadCarSortData {

    private int TotalCount;
    private int UserId;
    private List<LoadCarSortDataBean> LoadCarSortData;

    public int getTotalCount() {
        return TotalCount;
    }

    public void setTotalCount(int TotalCount) {
        this.TotalCount = TotalCount;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
    }

    public List<LoadCarSortDataBean> getLoadCarSortData() {
        return LoadCarSortData;
    }

    public void setLoadCarSortData(List<LoadCarSortDataBean> LoadCarSortData) {
        this.LoadCarSortData = LoadCarSortData;
    }

    public static class LoadCarSortDataBean {
        /**
         * OrderId : 200000787740
         * ShippingNumber : 0
         * ShippingSerialNumber : 4
         * ShopCode : 00010007
         * ShopID : 81
         * ShopName : （00010007）*3芙蓉府后街
         */

        private String OrderId;
        private int ShippingNumber;
        private int ShippingSerialNumber;
        private String ShopCode;
        private int ShopID;
        private String ShopName;

        public String getOrderId() {
            return OrderId;
        }

        public void setOrderId(String OrderId) {
            this.OrderId = OrderId;
        }

        public int getShippingNumber() {
            return ShippingNumber;
        }

        public void setShippingNumber(int ShippingNumber) {
            this.ShippingNumber = ShippingNumber;
        }

        public int getShippingSerialNumber() {
            return ShippingSerialNumber;
        }

        public void setShippingSerialNumber(int ShippingSerialNumber) {
            this.ShippingSerialNumber = ShippingSerialNumber;
        }

        public String getShopCode() {
            return ShopCode;
        }

        public void setShopCode(String ShopCode) {
            this.ShopCode = ShopCode;
        }

        public int getShopID() {
            return ShopID;
        }

        public void setShopID(int ShopID) {
            this.ShopID = ShopID;
        }

        public String getShopName() {
            return ShopName;
        }

        public void setShopName(String ShopName) {
            this.ShopName = ShopName;
        }
    }
}
