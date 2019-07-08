package com.frxs.delivery.model;

import java.io.Serializable;
import java.util.List;

/**
 * 订单详情-->订单信息 by Tiepier
 */
public class GetDeliverOrderInfo implements Serializable {

    private static final long serialVersionUID = -4806447994020302996L;
    private String OrderDate;//下单时间
    private String OrderId;//单号
    private int Package1Qty;//周转箱
    private int Package2Qty;//纸箱数
    private int Package3Qty;//易碎品
    private double PayAmount;//订单金额
    private String Remark;//备注
    private double SaleQty;//订单数量
    private String StationNumber;//流水号
    private int Status;//订单状态
    private double TotalPoint;//门店积分
    private List<ShelfAreaListBean> ShelfAreaList;//货区集合
    private String WaitingPacking;//等待拣货的门店数
    private String IsPicking;//正在拣货的门店数
    private double BasePointExt;// 附加分
    private double TotalBasePoint;// 绩效分

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String OrderDate) {
        this.OrderDate = OrderDate;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String OrderId) {
        this.OrderId = OrderId;
    }

    public int getPackage1Qty() {
        return Package1Qty;
    }

    public void setPackage1Qty(int Package1Qty) {
        this.Package1Qty = Package1Qty;
    }

    public int getPackage2Qty() {
        return Package2Qty;
    }

    public void setPackage2Qty(int Package2Qty) {
        this.Package2Qty = Package2Qty;
    }

    public int getPackage3Qty() {
        return Package3Qty;
    }

    public void setPackage3Qty(int Package3Qty) {
        this.Package3Qty = Package3Qty;
    }

    public double getPayAmount() {
        return PayAmount;
    }

    public void setPayAmount(double PayAmount) {
        this.PayAmount = PayAmount;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public double getSaleQty() {
        return SaleQty;
    }

    public void setSaleQty(double SaleQty) {
        this.SaleQty = SaleQty;
    }

    public String getStationNumber() {
        return StationNumber;
    }

    public void setStationNumber(String StationNumber) {
        this.StationNumber = StationNumber;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public double getTotalPoint() {
        return TotalPoint;
    }

    public void setTotalPoint(double TotalPoint) {
        this.TotalPoint = TotalPoint;
    }

    public List<ShelfAreaListBean> getShelfAreaList() {
        return ShelfAreaList;
    }

    public void setShelfAreaList(List<ShelfAreaListBean> ShelfAreaList) {
        this.ShelfAreaList = ShelfAreaList;
    }

    public static class ShelfAreaListBean {
        /**
         * Flag : 2
         * ShelfAreaName : 大货区
         */

        private int Flag;
        private String ShelfAreaName;
        private String BeginTime;

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

        public String getBeginTime() {
            return BeginTime;
        }

        public void setBeginTime(String beginTime) {
            BeginTime = beginTime;
        }
    }

    public String getWaitingPacking() {
        return WaitingPacking;
    }

    public void setWaitingPacking(String waitingPacking) {
        WaitingPacking = waitingPacking;
    }

    public String getIsPicking() {
        return IsPicking;
    }

    public void setIsPicking(String isPicking) {
        IsPicking = isPicking;
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
}
