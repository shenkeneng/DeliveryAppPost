package com.ewu.core.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ewu on 2016/11/12.
 */

public class CheckUtils {

    /**
     * 判断字符串是否为Null或""
     *
     * @param str
     * @return true=>Null或"";false=>不为Null或""
     */
    public static boolean strIsNull(String str) {
        if (null != str && !"".equals(str))
            return false;
        return true;
    }

    /**
     * 判断字符串是否为正整数
     * +1, +2, +3……算作正整数
     * 0,1.0,2.0,3.0……不算做正整数
     *
     * @param str
     * @return true=>是;false=>不是
     */
    public static boolean strIsPositiveInteger(String str) {
        if (strIsNull(str))
            return false;
        Pattern p = Pattern.compile("[1-9]\\d*");
        Pattern p1 = Pattern.compile("\\+[1-9]\\d*");
        Matcher m = p.matcher(str);
        Matcher m1 = p1.matcher(str);
        if (m.matches() || m1.matches())
            return true;
        return false;
    }

    /**
     * 判断字符串是否为负整数
     * 0,-1.0,-2.0,-3.0……不算做负整数
     *
     * @param str
     * @return true=>是;false=>不是
     */
    public static boolean strIsNegativeInteger(String str) {
        if (strIsNull(str))
            return false;
        Pattern p = Pattern.compile("-[1-9]\\d*");
        Matcher m = p.matcher(str);
        if (m.matches())
            return true;
        return false;
    }

    /**
     * 判断字符串是否是整数
     * +xxx,xxx,-xxx,+0,-0,0都算作整数
     *
     * @param str
     * @return true=>是;false=>不是
     */
    public static boolean strIsInteger(String str) {
        if (strIsNull(str))
            return false;
        Pattern p = Pattern.compile("((-|\\+)?[1-9]\\d*)|((-|\\+)?0)");
        Matcher m = p.matcher(str);
        if (m.matches())
            return true;
        return false;
    }


    /**
     * 判断字符串是否为正浮点型（Float or Double）数据
     * x.xx或+x.xx都算作正浮点数
     * 0.0不算做正浮点数
     *
     * @param str
     * @return true=>是;false=>不是
     */
    public static boolean strIsPositiveFloat(String str) {
        if (strIsNull(str))
            return false;
        if (strIsPositiveInteger(str) || strIsNegativeInteger(str))
            return false;
        Pattern p = Pattern.compile("[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*");
        Pattern p1 = Pattern.compile("\\+([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)");
        Matcher m = p.matcher(str);
        Matcher m1 = p1.matcher(str);
        if (m.matches() || m1.matches())
            return true;
        return false;
    }

    /**
     * 判断字符串是否为负浮点型（Float or Double）数据
     * -0.0不算做负浮点数
     *
     * @param str
     * @return true=>是;false=>不是
     */
    public static boolean strIsNegativeFloat(String str) {
        if (strIsNull(str))
            return false;
        if (strIsPositiveInteger(str) || strIsNegativeInteger(str))
            return false;
        Pattern p = Pattern.compile("-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)");
        Matcher m = p.matcher(str);
        if (m.matches())
            return true;
        return false;
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return true=>是;false=>不是
     */
    public static boolean strIsNumber(String str) {
        if (strIsNull(str))
            return false;
        if (strIsInteger(str) || strIsNegativeFloat(str) || strIsPositiveFloat(str))
            return true;
        return false;
    }


    /**
     * 当前是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetwork(Context context) {
        if (null != context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    /**
     * 获取网络连接类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        if (null != context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
            return networkInfo == null ? -1 : networkInfo.getType();
        }
        return -1;
    }

    /**
     * 获取网络连接类型名称
     * disconnect：断开连接，wifi:WIFI,eg:高速移动网络（3G）,2g，wap，wnknown:未知
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        if (null != context) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo;
            String type = "disconnect";
            if (manager == null || (networkInfo = manager.getActiveNetworkInfo()) == null) {
                return type;
            }
            if (networkInfo.isConnected()) {
                String typeName = networkInfo.getTypeName();
                if ("WIFI".equalsIgnoreCase(typeName)) {
                    type = "wifi";
                } else if ("MOBILE".equalsIgnoreCase(typeName)) {
                    String proxyHost = android.net.Proxy.getDefaultHost();
                    type = strIsNull(proxyHost) ? (isFastMobileNetwork(context) ? "eg" : "2g")
                            : "wap";
                } else {
                    type = "unknown";
                }
            }
        }
        return "unknown";
    }

    /**
     * 是否是快速移动网络
     *
     * @param context
     * @return
     */
    public static boolean isFastMobileNetwork(Context context) {
        if (null != context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return false;
            }

            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }
        return false;
    }


    /**
     * 判断当前应用是否运行在前台
     *
     * @param context
     * @return true 前台
     */
    public static boolean isForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    /**
     * 检查SD卡是否存在
     *
     * @return
     */
    public static boolean isExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param path
     * @return
     */
    public static boolean isExistFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 是否是手机号码
     *
     * @param mobile
     * @return true:是
     */
    public static boolean isMobile(String mobile) {
        Pattern p = Pattern
                .compile("^((14[0-9])|(17[0-9])|(13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    /**
     * 是否是固定电话
     *
     * @param phone
     * @return true：是
     */
    public static boolean isLandlineTelePhone(String phone) {
        String str = "^((\\d{3,4}\\-)|)\\d{7,8}(|([-\\u8f6c]{1}\\d{1,5}))$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 是否是邮政编码
     *
     * @param zipCode
     * @return true:是
     */
    public static boolean isZipCode(String zipCode) {
        String str = "^[1-9]\\d{5}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(zipCode);
        return m.matches();
    }

    /**
     * 是否是邮箱
     *
     * @param email
     * @return true:是
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 一般昵称规则验证
     * 是否由汉字字符数字下划线组成
     *
     * @param nick
     * @return true:是
     */
    public static boolean isNickname(String nick) {
        String str = "[\u4e00-\u9fa5_a-zA-Z0-9_]+";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(nick);
        return m.matches();
    }
}
