package com.frxs.delivery.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chentie on 2017/6/17.
 */

public class WaitReceiveList implements Serializable {

    private String ApplyForSaleBackResponseStr;
    private int Total;
    private List<ApplyForSaleBackListBean> ApplyForSaleBackList;

    public String getApplyForSaleBackResponseStr() {
        return ApplyForSaleBackResponseStr;
    }

    public void setApplyForSaleBackResponseStr(String ApplyForSaleBackResponseStr) {
        this.ApplyForSaleBackResponseStr = ApplyForSaleBackResponseStr;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    public List<ApplyForSaleBackListBean> getApplyForSaleBackList() {
        return ApplyForSaleBackList;
    }

    public void setApplyForSaleBackList(List<ApplyForSaleBackListBean> ApplyForSaleBackList) {
        this.ApplyForSaleBackList = ApplyForSaleBackList;
    }

    public static class ApplyForSaleBackListBean implements Serializable {
        private String ApplyBackID;
        private String BackDate;
        private String ConfTime;
        private int ConfUserID;
        private String ConfUserName;
        private String CreateTime;
        private int CreateUserID;
        private String CreateUserName;
        private String ModifyTime;
        private int ModifyUserID;
        private String ModifyUserName;
        private double PayAmount;
        private String ReceivedTime;
        private int ReceivedUserID;
        private String ReceivedUserName;
        private String Remark;
        private String ShopCode;
        private int ShopID;
        private String ShopName;
        private String StartReceiveTime;
        private int StartReceiveUserID;
        private String StartReceiveUserName;
        private int Status;
        private String StatusName;
        private String SubWCode;
        private int SubWID;
        private String SubWName;
        private int TakeBackCarOwnerType;
        private String TakeBackTime;
        private double TakeBackTotalQty;
        private int TakeBackUserID;
        private String TakeBackUserName;
        private double TotalAddAmt;
        private double TotalBackAmt;
        private double TotalBackQty;
        private double TotalBackTotalAmt;
        private double TotalBasePoint;
        private String WCode;
        private int WID;
        private String WName;

        public String getApplyBackID() {
            return ApplyBackID;
        }

        public void setApplyBackID(String ApplyBackID) {
            this.ApplyBackID = ApplyBackID;
        }

        public String getBackDate() {
            return BackDate;
        }

        public void setBackDate(String BackDate) {
            this.BackDate = BackDate;
        }

        public String getConfTime() {
            return ConfTime;
        }

        public void setConfTime(String ConfTime) {
            this.ConfTime = ConfTime;
        }

        public int getConfUserID() {
            return ConfUserID;
        }

        public void setConfUserID(int ConfUserID) {
            this.ConfUserID = ConfUserID;
        }

        public String getConfUserName() {
            return ConfUserName;
        }

        public void setConfUserName(String ConfUserName) {
            this.ConfUserName = ConfUserName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public int getCreateUserID() {
            return CreateUserID;
        }

        public void setCreateUserID(int CreateUserID) {
            this.CreateUserID = CreateUserID;
        }

        public String getCreateUserName() {
            return CreateUserName;
        }

        public void setCreateUserName(String CreateUserName) {
            this.CreateUserName = CreateUserName;
        }

        public String getModifyTime() {
            return ModifyTime;
        }

        public void setModifyTime(String ModifyTime) {
            this.ModifyTime = ModifyTime;
        }

        public int getModifyUserID() {
            return ModifyUserID;
        }

        public void setModifyUserID(int ModifyUserID) {
            this.ModifyUserID = ModifyUserID;
        }

        public String getModifyUserName() {
            return ModifyUserName;
        }

        public void setModifyUserName(String ModifyUserName) {
            this.ModifyUserName = ModifyUserName;
        }

        public double getPayAmount() {
            return PayAmount;
        }

        public void setPayAmount(double PayAmount) {
            this.PayAmount = PayAmount;
        }

        public String getReceivedTime() {
            return ReceivedTime;
        }

        public void setReceivedTime(String ReceivedTime) {
            this.ReceivedTime = ReceivedTime;
        }

        public int getReceivedUserID() {
            return ReceivedUserID;
        }

        public void setReceivedUserID(int ReceivedUserID) {
            this.ReceivedUserID = ReceivedUserID;
        }

        public String getReceivedUserName() {
            return ReceivedUserName;
        }

        public void setReceivedUserName(String ReceivedUserName) {
            this.ReceivedUserName = ReceivedUserName;
        }

        public String getRemark() {
            return Remark;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
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

        public String getStartReceiveTime() {
            return StartReceiveTime;
        }

        public void setStartReceiveTime(String StartReceiveTime) {
            this.StartReceiveTime = StartReceiveTime;
        }

        public int getStartReceiveUserID() {
            return StartReceiveUserID;
        }

        public void setStartReceiveUserID(int StartReceiveUserID) {
            this.StartReceiveUserID = StartReceiveUserID;
        }

        public String getStartReceiveUserName() {
            return StartReceiveUserName;
        }

        public void setStartReceiveUserName(String StartReceiveUserName) {
            this.StartReceiveUserName = StartReceiveUserName;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int Status) {
            this.Status = Status;
        }

        public String getStatusName() {
            return StatusName;
        }

        public void setStatusName(String StatusName) {
            this.StatusName = StatusName;
        }

        public String getSubWCode() {
            return SubWCode;
        }

        public void setSubWCode(String SubWCode) {
            this.SubWCode = SubWCode;
        }

        public int getSubWID() {
            return SubWID;
        }

        public void setSubWID(int SubWID) {
            this.SubWID = SubWID;
        }

        public String getSubWName() {
            return SubWName;
        }

        public void setSubWName(String SubWName) {
            this.SubWName = SubWName;
        }

        public int getTakeBackCarOwnerType() {
            return TakeBackCarOwnerType;
        }

        public void setTakeBackCarOwnerType(int TakeBackCarOwnerType) {
            this.TakeBackCarOwnerType = TakeBackCarOwnerType;
        }

        public String getTakeBackTime() {
            return TakeBackTime;
        }

        public void setTakeBackTime(String TakeBackTime) {
            this.TakeBackTime = TakeBackTime;
        }

        public double getTakeBackTotalQty() {
            return TakeBackTotalQty;
        }

        public void setTakeBackTotalQty(double TakeBackTotalQty) {
            this.TakeBackTotalQty = TakeBackTotalQty;
        }

        public int getTakeBackUserID() {
            return TakeBackUserID;
        }

        public void setTakeBackUserID(int TakeBackUserID) {
            this.TakeBackUserID = TakeBackUserID;
        }

        public String getTakeBackUserName() {
            return TakeBackUserName;
        }

        public void setTakeBackUserName(String TakeBackUserName) {
            this.TakeBackUserName = TakeBackUserName;
        }

        public double getTotalAddAmt() {
            return TotalAddAmt;
        }

        public void setTotalAddAmt(double TotalAddAmt) {
            this.TotalAddAmt = TotalAddAmt;
        }

        public double getTotalBackAmt() {
            return TotalBackAmt;
        }

        public void setTotalBackAmt(double TotalBackAmt) {
            this.TotalBackAmt = TotalBackAmt;
        }

        public double getTotalBackQty() {
            return TotalBackQty;
        }

        public void setTotalBackQty(double TotalBackQty) {
            this.TotalBackQty = TotalBackQty;
        }

        public double getTotalBackTotalAmt() {
            return TotalBackTotalAmt;
        }

        public void setTotalBackTotalAmt(double TotalBackTotalAmt) {
            this.TotalBackTotalAmt = TotalBackTotalAmt;
        }

        public double getTotalBasePoint() {
            return TotalBasePoint;
        }

        public void setTotalBasePoint(double TotalBasePoint) {
            this.TotalBasePoint = TotalBasePoint;
        }

        public String getWCode() {
            return WCode;
        }

        public void setWCode(String WCode) {
            this.WCode = WCode;
        }

        public int getWID() {
            return WID;
        }

        public void setWID(int WID) {
            this.WID = WID;
        }

        public String getWName() {
            return WName;
        }

        public void setWName(String WName) {
            this.WName = WName;
        }
    }
}
