package com.frxs.delivery.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 用户信息 by Tiepier
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -3562402325575431570L;

    private int EmpID;//用户编号
    private String EmpName;//用户名
    private int WID;//子仓库ID(Warehouse.WID)
    private int WareHouseWID; //仓库ID
    private String IsMaster;//是否是组长(0:不是；1：是)
    private String UserMobile;//用户手机
    private String IsFrozen;//是否冻结(0:未冻结;1:已冻结)
    private String IsLocked;//是否锁定(0:未锁定;1:已锁定)
    private String IsDeleted;//是否删除(0:未删除;1:已删除)
    private String PasswordSalt;//32位密码盐值
    private int APPKEY;//随机生成的32位GUID;存在用户的redis中
    private String UserAccount;//登录名
    private String UserPwd;//登录密码
    private String LineIDs;//线路ID
    private int IsEnabledFreightCar; //是否启用货配车（0:不启用; 1:启用）
    private int IsShippingCar;//0：是外协司机
    private int IsSigns; //是否开启电子签名功能 （0：不启用；1：启用不要验证身份；2：启用并验证身份）

    public int getWareHouseWID() {
        return WareHouseWID;
    }

    public void setWareHouseWID(int wareHouseWID) {
        WareHouseWID = wareHouseWID;
    }

    public String getLineIDs() {
        return LineIDs;
    }

    public void setLineIDs(String lineIDs) {
        LineIDs = lineIDs;
    }

    public String getUserAccount() {
        return UserAccount;
    }

    public void setUserAccount(String userAccount) {
        UserAccount = userAccount;
    }

    public String getUserPwd() {
        return UserPwd;
    }

    public void setUserPwd(String userPwd) {
        UserPwd = userPwd;
    }

    public int getEmpID() {
        return EmpID;
    }

    public void setEmpID(int empID) {
        EmpID = empID;
    }

    public String getEmpName() {
        return EmpName;
    }

    public void setEmpName(String empName) {
        EmpName = empName;
    }

    public int getWID() {
        return WID;
    }

    public void setWID(int WID) {
        this.WID = WID;
    }

    public String getIsMaster() {
        return IsMaster;
    }

    public void setIsMaster(String isMaster) {
        IsMaster = isMaster;
    }

    public String getUserMobile() {
        return UserMobile;
    }

    public void setUserMobile(String userMobile) {
        UserMobile = userMobile;
    }

    public String getIsFrozen() {
        return IsFrozen;
    }

    public void setIsFrozen(String isFrozen) {
        IsFrozen = isFrozen;
    }

    public String getIsLocked() {
        return IsLocked;
    }

    public void setIsLocked(String isLocked) {
        IsLocked = isLocked;
    }

    public String getIsDeleted() {
        return IsDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        IsDeleted = isDeleted;
    }

    public String getPasswordSalt() {
        return PasswordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        PasswordSalt = passwordSalt;
    }

    public int getAPPKEY() {
        return APPKEY;
    }

    public void setAPPKEY(int APPKEY) {
        this.APPKEY = APPKEY;
    }

    public int getIsEnabledFreightCar() {
        return IsEnabledFreightCar;
    }

    public void setIsEnabledFreightCar(int isEnabledFreightCar) {
        IsEnabledFreightCar = isEnabledFreightCar;
    }

    public boolean isMaster() {
        if (!TextUtils.isEmpty(IsMaster) && IsMaster.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    public int getIsShippingCar() {
        return IsShippingCar;
    }

    public void setIsShippingCar(int isShippingCar) {
        IsShippingCar = isShippingCar;
    }

    public int getIsSigns() {
        return IsSigns;
    }

    public void setIsSigns(int isSigns) {
        IsSigns = isSigns;
    }
}
