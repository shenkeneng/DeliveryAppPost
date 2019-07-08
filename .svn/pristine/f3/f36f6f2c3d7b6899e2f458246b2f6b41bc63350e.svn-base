package com.frxs.delivery.comms;

/**
 * Created by ewu on 2016/3/23.
 */
public class Config {
    public static final String PREFS_NAME = "MyFrefsFile";

    public static final String KEY_USER = "key_user";

    public static final String KEY_DEVICE_ID = "key_device_id";//设备id
    // 远程服务器网络 (0:线上环境、1：测试站点、2：演示环境、3：post请求地址、4:TF本机、5：集成环境、6：DL本机)
    public static int networkEnv = 0;

    public static String getBaseUrl(int environment) {
        networkEnv = environment;
        return getBaseUrl();
    }

    public static String getBaseUrl() {
        String BASE_URL = "";
        if (networkEnv == 0) {
            BASE_URL = "http://orderapi.erp2.frxs.com/api/";
        } else if (networkEnv == 1) {
            BASE_URL = "http://b2btest.frxs.cn/api/";
        } else if (networkEnv == 2) {
            BASE_URL = "http://yfborderapi.erp2.frxs.com/api/";// 预发布环境
        } else if (networkEnv == 3) {
            BASE_URL = "http://192.168.8.214:8002/api/Deliver/";
        } else if (networkEnv == 4) {
            BASE_URL = "http://192.168.8.242:8085/api/";
        } else if (networkEnv == 5) {
            BASE_URL = "http://192.168.8.142:8089/api/";
        } else if (networkEnv == 6) {
            //BASE_URL = "http://f3dh.frxs.com/api/";// 演示环境
            //BASE_URL = "http://192.168.8.63:8088/api/";
            //BASE_URL = "http://192.168.8.125:8099/api/";
            //BASE_URL = "http://192.168.8.246:8081/api/";
            //BASE_URL = "http://192.168.8.156:8089/api/";//DDY
            //BASE_URL = "http://192.168.8.135:8056/api/";//TT
            //BASE_URL = "http://192.168.8.210:8099/api/";//DF
            //BASE_URL =  "http://192.168.8.197:8082/api/";//CG
            BASE_URL = "http://192.168.8.125:8099/api/";
        }
        return BASE_URL;
    }

    public static String getSubimgUrl(){
        String SUBIMG_SERVER = "";// 获取当前图片上传地址
        if (networkEnv > 0){
            SUBIMG_SERVER = "http://itestimage.frxs.cn/api/";//测试环境
            //SUBIMG_SERVER = "http://192.168.8.142:8082/api/";//集成环境
        } else {
            SUBIMG_SERVER = "http://imagesup.erp2.frxs.com/api/";
        }

        return SUBIMG_SERVER;
    }
}
