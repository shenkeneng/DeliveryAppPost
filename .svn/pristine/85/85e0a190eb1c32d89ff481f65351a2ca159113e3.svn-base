package com.frxs.delivery.model;

import java.io.Serializable;
import java.util.List;

/**
 * 正在拣货 by Tiepier
 */
public class PickingData implements Serializable {

    private static final long serialVersionUID = -821952123468542807L;
    private String OrderId;//订单编码
    private double PayAmount;//支付金额
    private String ShopCode;//门店编码
    private int ShopID;//门店ID
    private String ShopName;//门店名称
    private Integer StationNumber;//待装区编号
    private int WID;//仓库ID
    private List<ShelfAreaListBean> ShelfAreaList;//货区集合
    private int ShippingSerialNumber;//车次

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String OrderId) {
        this.OrderId = OrderId;
    }

    public double getPayAmount() {
        return PayAmount;
    }

    public void setPayAmount(double PayAmount) {
        this.PayAmount = PayAmount;
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

    public Integer getStationNumber() {
        return StationNumber;
    }

    public void setStationNumber(Integer StationNumber) {
        this.StationNumber = StationNumber;
    }

    public int getWID() {
        return WID;
    }

    public void setWID(int WID) {
        this.WID = WID;
    }

    public List<ShelfAreaListBean> getShelfAreaList() {
        return ShelfAreaList;
    }

    public void setShelfAreaList(List<ShelfAreaListBean> ShelfAreaList) {
        this.ShelfAreaList = ShelfAreaList;
    }

    public static class ShelfAreaListBean {
        private int Flag;//货区状态
        private String ShelfAreaName;//货区名称

        public int getFlag() {
            return Flag;
        }

        public void setFlag(int Flag) {
            this.Flag = Flag;
        }

        public String getShelfAreaName() {
            return ShelfAreaName;
        }

        public void setShelfAreaName(String ShelfAreaName) {
            this.ShelfAreaName = ShelfAreaName;
        }
    }

    public int getShippingSerialNumber() {
        return ShippingSerialNumber;
    }

    public void setShippingSerialNumber(int shippingSerialNumber) {
        ShippingSerialNumber = shippingSerialNumber;
    }
}
