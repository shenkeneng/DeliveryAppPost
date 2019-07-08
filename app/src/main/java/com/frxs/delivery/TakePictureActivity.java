package com.frxs.delivery;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.ewu.core.model.TException;
import com.ewu.core.utils.BitmapUtil;
import com.ewu.core.utils.DataSizeUtils;
import com.ewu.core.utils.EasyPermissionsEx;
import com.ewu.core.utils.LogUtils;
import com.ewu.core.utils.SharedPreferencesHelper;
import com.ewu.core.utils.SystemUtils;
import com.ewu.core.utils.TUriParse;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.comms.Config;
import com.frxs.delivery.model.OrderSigns;
import com.frxs.delivery.model.PostSubOrderSigns;
import com.frxs.delivery.model.ShopImagePath;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.RestClient;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.utils.CoordinateUtils;
import com.frxs.delivery.utils.MD5;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/09/29
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class TakePictureActivity extends FrxsActivity {
    private static final String TAG = "TakePictureActivity";
    public final static int RC_PICK_PICTURE_FROM_CAPTURE = 1003;
    private Uri outPutUri;
    private TextView titleTv;
    private TextView takePictureTv;
    private TextView backTv;
    private LocationManager locationManager;
    private String latLongString;// 经纬度
    private HashMap<String, String> ImgUrlMap = new HashMap<String, String>();// 存储签名、现场url的map
    private byte[] picByteArray; //存储拍照压缩之后的图片
    private String orderId;// 订单ID
    private int shopId;// 门店ID
    private String shopCode;// 门店code
    private String shopName;
    private ImageView ivPicture;
    private String signData;
    private String pictureData;
    private Bitmap signImg;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_take_picture;
    }

    @Override
    protected void initViews() {
        titleTv = (TextView) findViewById(R.id.tv_title);
        takePictureTv = (TextView) findViewById(R.id.take_picture_tv);
        backTv = (TextView) findViewById(R.id.return_tv);
        ivPicture = (ImageView) findViewById(R.id.iv_picture);
        findViewById(R.id.tv_title_right).setVisibility(View.INVISIBLE);
        titleTv.setText("拍照签名");

        // 获取经纬度
        formListenerGetLocation();
        startCamera();
    }

    @Override
    protected void initEvent() {
        backTv.setOnClickListener(this);
        takePictureTv.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("order_id");
            shopId = intent.getIntExtra("shop_id", -1);
            shopCode = intent.getStringExtra("shop_code");
            shopName = intent.getStringExtra("shop_name");
            signImg = intent.getParcelableExtra("sign");
            if (signImg != null) {
                signData = DataSizeUtils.getDataSize(BitmapUtil.bitmap2Byte(signImg));
            }
        }
        // 获取电话服务
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String deviceId = helper.getString(Config.KEY_DEVICE_ID, "");
        if (TextUtils.isEmpty(deviceId)) {
            String id = SystemUtils.getPhoneIMEI(this);

            if (id.equals("000000000000000") || TextUtils.isEmpty(id)) {
                LogUtils.d("IMEI---" + id);
                getSerialNumber();
            } else {
                helper.putValue(Config.KEY_DEVICE_ID, id);
            }
        } else {
            helper.putValue(Config.KEY_DEVICE_ID, deviceId);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RC_PICK_PICTURE_FROM_CAPTURE == requestCode) {
            if ( resultCode == 0) {
                finish();
                return;
            }
            try {
                String originalPath = TUriParse.getFilePathWithUri(outPutUri, this);
                compress(originalPath);
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    private void compress(String originalPath) {
        if (TextUtils.isEmpty(originalPath)){
            return;
        }

        compressImageByPixel(originalPath, 800, 300*1024);
    }

    /**
     * 按比例缩小图片的像素以达到压缩的目的
     * @author JPH
     * @param imgPath
     * @return
     * @date 2014-12-5下午11:30:59
     */
    private Bitmap compressImageByPixel(String imgPath, int maxPixel, int maxSize) {
        if(imgPath==null){
            ToastUtils.show(this,"要压缩的文件不存在");
            return null;
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(imgPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;

        Bitmap originalBitmap = BitmapFactory.decodeFile(imgPath);
        Log.i("WMTest", "createScaledBitmap压缩前图片的大小" + originalBitmap.getByteCount()
                + "byte宽度为" + originalBitmap.getWidth() + "高度为" + originalBitmap.getHeight());
        int be = 1;
        if (width >= height && width > maxPixel) {//缩放比,用高或者宽其中较大的一个数据进行计算
            be = (int) (newOpts.outWidth / maxPixel);
            be++;
        } else if (width < height && height > maxPixel) {
            be = (int) (newOpts.outHeight / maxPixel);
            be++;
        }

        newOpts.inSampleSize = be;//设置采样率
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//当系统内存不够时候图片自动被回收
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        Log.i("WMTest", "inSampleSize=" + be + " 设置采样率压缩后图片的大小" + bitmap.getByteCount()
                + "byte宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());

        int dstWidth = 0;
        int dstHeight = 0;
        if (width > height) {
            dstWidth = maxPixel;
            dstHeight = Math.round((float)height /  (float)width * maxPixel);
        } else {
            dstWidth = Math.round((float)width / (float)height * maxPixel);
            dstHeight = maxPixel;
        }

        Bitmap bitmap2 =  Bitmap.createScaledBitmap(originalBitmap, dstWidth, dstHeight, true);
        Log.i("WMTest", "createScaledBitmap压缩后图片的大小" + bitmap2.getByteCount()
                + "byte宽度为" + bitmap2.getWidth() + "高度为" + bitmap2.getHeight());

        compressImageByQuality(bitmap,imgPath, maxSize);//压缩好比例大小后再进行质量压缩

        return bitmap;
    }

    /**
     * 多线程压缩图片的质量
     * @author JPH
     * @param bitmap 内存中的图片
     * @param imgPath 图片的保存路径
     * @date 2014-12-5下午11:30:43
     */
    private void compressImageByQuality(final Bitmap bitmap, final String imgPath, final int maxSize){
        if(bitmap==null){
            ToastUtils.show(this,"像素压缩失败,bitmap is null");
            return;
        }

        Observable.create(new ObservableOnSubscribe<byte[]>() {
            @Override
            public void subscribe(ObservableEmitter e) throws Exception {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int options = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，把压缩后的数据存放到baos中 (100表示不压缩，0表示压缩到最小)
                while (baos.toByteArray().length > maxSize) {//循环判断如果压缩后图片是否大于指定大小,大于继续压缩
                    baos.reset();//重置baos即让下一次的写入覆盖之前的内容
                    options -= 5;//图片质量每次减少5
                    if(options<=5)options=5;//如果图片质量小于5，为保证压缩后的图片质量，图片最底压缩质量为5
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//将压缩后的图片保存到baos中
                    if(options==5)break;//如果图片的质量已降到最低则，不再进行压缩
                }
                e.onNext(baos.toByteArray());
            }
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        TakePictureActivity.this.showProgressDialog();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] bytes) throws Exception {
                        //TODO 提交压缩后的图片
                        TakePictureActivity.this.dismissProgressDialog();
                        picByteArray = bytes;
                        Bitmap pictureImg = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ivPicture.setImageBitmap(pictureImg);
                        pictureData = DataSizeUtils.getDataSize(bytes.length);
                        Log.i("WMTest", "预提交压缩后图片的大小" + bytes.length);
                    }
                });
    }

    /**
     * 初始化定位监听
     */
    private void formListenerGetLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, mLocationListener01);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, mLocationListener01);
        Location gps = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (gps != null) {
            latLongString =  CoordinateUtils.transform2Mars(gps.getLatitude(), gps.getLongitude(), true);
        }
    }

    /**
     * 通过location获取当前设备的具体位置
     *
     * @param location
     * @return
     */
    private String updateToNewLocation(Location location) {
        if (location != null) {
            latLongString =  CoordinateUtils.transform2Mars(location.getLatitude(), location.getLongitude(), true);
        }

        return latLongString;
    }

    /**
     * 设置监听器，自动更新的最小时间为间隔N秒(1秒为1*1000，这样写主要为了方便)或最小位移变化超过N米
     */
    public LocationListener mLocationListener01 = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }


        @Override
        public void onProviderDisabled(String provider) {
            updateToNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 销毁时解除定位监听器
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(mLocationListener01);
            locationManager = null;
        }
        if (signImg != null) {
            signImg = null;
        }
        if (ImgUrlMap != null) {
            ImgUrlMap.clear();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.take_picture_tv:
                if (!EasyPermissionsEx.hasPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA})) {
                    ToastUtils.show(this, "请开启位置、相机权限才能继续完成配送");
                    finish();
                    return;
                }
                if (picByteArray == null) {
                    ToastUtils.show(this, "请拍摄现场照片！");
                    return;
                }
                finishOrderSign();
                break;
            case R.id.return_tv:
                startCamera();
                break;

            default:
                break;
        }
    }

    public void startCamera(){
        File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        outPutUri = Uri.fromFile(file);
        if (Build.VERSION.SDK_INT >= 23) {
            this.outPutUri = TUriParse.convertFileUriToFileProviderUri(this, outPutUri);
        } else {
            this.outPutUri = outPutUri;
        }
        startActivityForResult(getCaptureIntent(outPutUri), RC_PICK_PICTURE_FROM_CAPTURE);
    }

    public static Intent getCaptureIntent(Uri outPutUri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);//将拍取的照片保存到指定URI
        return intent;
    }

    /**
     * 完成订单签名
     */
    private void finishOrderSign() {
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        if (userInfo.getIsSigns() == 1) {// 1：启用不要验证身份 直接上传图片获取地址；
            subSignImg();// 上传签名图片与现场照片 获取图片地址
        } else if (userInfo.getIsSigns() == 2) { // 2：启用并验证身份
            showVerifyDialog();
        } else {
            ToastUtils.show(TakePictureActivity.this, "电子签名功能未启用");
        }
    }

    /**
     * 开始门店签署验证弹窗
     */
    private void showVerifyDialog() {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_verify);
        dialog.setCancelable(true);// 设置点击屏幕Dialog不消失
        dialog.show();
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);// 确定
        final Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);// 取消
        btnConfirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText passwordEd = (EditText) dialog.findViewById(R.id.ed_password);
                String psw = passwordEd.getText().toString().trim();
                if (psw.length() < 6 || psw.length() > 20) {
                    ToastUtils.show(TakePictureActivity.this, "密码长度为6-20个字符");
                } else {
                    if (shopId < 0) {
                        ToastUtils.show(TakePictureActivity.this, "未获取到该订单的门店信息");
                        return;
                    }
                    shopSignVerify(passwordEd.getText().toString().trim(), dialog);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 门店签署验证
     */
    private void shopSignVerify(String passWord, final Dialog dialog) {
        showProgressDialog();
        AjaxParams params = new AjaxParams();
        params.put("ShopID", shopId);
        params.put("Password", passWord);

        getService().ShopSignVerify(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    dialog.dismiss();
                    subSignImg();
                } else {
                    ToastUtils.show(TakePictureActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(TakePictureActivity.this, t.getMessage());
            }
        });
    }

    /**
     * 提交签名、现场图片]
     */
    public void subSignImg() {
        showProgressDialog();
        //获取签名的图片
        Map<String, RequestBody> multiParts = new HashMap<String, RequestBody>();
        if (signImg != null) {
            multiParts.put("photos\"; filename=\"" + "sign", RequestBody.create(MediaType.parse("image"), BitmapUtil.bitmap2Bytes(signImg)));
        }
        if (picByteArray != null) {
            multiParts.put("photos\"; filename=\"" + "pictureImg", RequestBody.create(MediaType.parse("image"), picByteArray));
        }

        new RestClient(Config.getSubimgUrl()).getApiService().SubmitSignImage(multiParts).enqueue(new SimpleCallback<String>() {

            @Override
            public void onResponse(String result, int code, String msg) {
                dismissProgressDialog();
                // 去除json转义符
                String data = result.replace("\\\"", "");
                Type type = new TypeToken<ArrayList<JsonObject>>() {
                }.getType();
                ArrayList<JsonObject> jsonObjects = new Gson().fromJson(data, type);
                ArrayList<ShopImagePath> tmpBackList = new ArrayList<>();
                for (JsonObject jsonObject : jsonObjects) {
                    tmpBackList.add(new Gson().fromJson(jsonObject, ShopImagePath.class));
                }
                // 图片是否上传成功
                for (int i = 0; i < tmpBackList.size(); i++) {
                    if (tmpBackList.get(i).getCode().equals("0")) {
                        ShopImagePath imageDate = tmpBackList.get(i);
                        if (imageDate.getData() != null) {
                            if (imageDate.getInfo().equals("sign")) {
                                ImgUrlMap.put("sign", imageDate.getData().getImgPath());
                                ImgUrlMap.put("sign120", imageDate.getData().getImgPath120());
                            } else if (imageDate.getInfo().equals("pictureImg")) {
                                ImgUrlMap.put("pictureImg", imageDate.getData().getImgPath());
                                ImgUrlMap.put("pictureImg120", imageDate.getData().getImgPath120());
                            }
                        }
                    } else {
                        ToastUtils.show(TakePictureActivity.this, "完成签名失败，请重新签名!");
                        return;
                    }
                }

                if (!TextUtils.isEmpty(ImgUrlMap.get("sign")) && !TextUtils.isEmpty(ImgUrlMap.get("sign120")) && !TextUtils.isEmpty(ImgUrlMap.get("pictureImg"))) {
                    subSignInfo(); // 上传签名信息
                } else {
                    ToastUtils.show(TakePictureActivity.this, "完成签名失败，请重新提交!");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(TakePictureActivity.this, t.getMessage());
            }
        });
    }


    /**
     * 提交签名信息
     */
    private void subSignInfo() {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        PostSubOrderSigns postSubOrderSigns = new PostSubOrderSigns();
        postSubOrderSigns.setOrderId(orderId.split(",")[0]);// 订单id
        postSubOrderSigns.setWID(userInfo.getWareHouseWID());// 仓库id
        postSubOrderSigns.setUserId(userInfo.getEmpID()); // 用户id
        postSubOrderSigns.setUserName(userInfo.getEmpName()); // 用户名

        OrderSigns orderSigns = new OrderSigns();
        orderSigns.setWID(userInfo.getWareHouseWID());// 仓库id
        orderSigns.setBillNo(orderId.split(",")[0]);// 订单id
        orderSigns.setBillType(1);// 订单类型
        orderSigns.setCusID("1");// 客户id
        orderSigns.setCusType(1);// 客户类型
        orderSigns.setID("");
        orderSigns.setModifyUserID(userInfo.getEmpID()); // 用户id
        orderSigns.setModifyUserName(userInfo.getEmpName()); // 用户名
        if (userInfo.getIsSigns() == 2) {//是否开启签署密码验证  开启需上传门店id与门店code
            orderSigns.setSigerUserID(String.valueOf(shopId));// 门店id
            orderSigns.setSigerUserName(shopCode);// 门店code
        }
        if (!TextUtils.isEmpty(latLongString)) {
            String[] location = latLongString.split(",");
            if (location.length > 1) {
                orderSigns.setLat(location[0]);
                orderSigns.setLng(location[1]);
            }
        }
        SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
        String deviceId = helper.getString(Config.KEY_DEVICE_ID, "");
        orderSigns.setMacID(deviceId);
        orderSigns.setSignUrl(ImgUrlMap.get("sign"));
        orderSigns.setPhotoUrl1(ImgUrlMap.get("pictureImg"));
        orderSigns.setSignSmallUrl(ImgUrlMap.get("sign120"));

        postSubOrderSigns.setSigns(orderSigns);

        getService().SubOrderSigns(postSubOrderSigns).enqueue(new SimpleCallback<ApiResponse<String>>() {
            @Override
            public void onResponse(ApiResponse<String> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (!TextUtils.isEmpty(result.getInfo())) {
                        if (TextUtils.isEmpty(String.valueOf(result.getData()))) {
                            ToastUtils.show(TakePictureActivity.this, "完成签名失败，请重新提交!");
                            return;
                        }

                        reqCompleteDelivery(orderId, String.valueOf(result.getData()));
                    }
                } else {
                    ToastUtils.show(TakePictureActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(TakePictureActivity.this, t.getMessage());
            }
        });
    }

    /**
     * 完成配送数据请求
     */
    private void reqCompleteDelivery(String strOrderID, final String signId) {
        showProgressDialog();
        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("Sign", MD5.ToMD5("SetDeliveredStatus"));
        params.put("WID", String.valueOf(userInfo.getWareHouseWID()));
        params.put("OrderId", strOrderID);
        params.put("EmpId", String.valueOf(userInfo.getEmpID()));
        params.put("EmpName", userInfo.getEmpName());
        params.put("SignID", signId);
        getService().SetDeliveredStatus(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<Object>>() {

            @Override
            public void onResponse(ApiResponse<Object> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    Intent intent = new Intent(TakePictureActivity.this, SignSuceessActivity.class);
                    intent.putExtra("shop_name", shopName);
                    intent.putExtra("order_id", orderId);
                    intent.putExtra("sign_id", signId);
                    intent.putExtra("picture_size", pictureData);
                    intent.putExtra("sign_size",  signData);
                    startActivity(intent);
                    SignatureActivity.signatureActivity.finish();
                    finish();
                } else {
                    ToastUtils.show(TakePictureActivity.this, result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                ToastUtils.show(TakePictureActivity.this, t.getMessage());
            }
        });
    }

    private void getSerialNumber() {
        String serialnum = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
            LogUtils.d("SERIAL_NUM---" + serialnum);
        } catch (Exception ignored) {
        }

        if (!TextUtils.isEmpty(serialnum)) {
            SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
            helper.putValue(Config.KEY_DEVICE_ID, serialnum);
        } else {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            LogUtils.d("ANDROID_ID---" + androidId);
            if (!TextUtils.isEmpty(androidId)) {
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
                helper.putValue(Config.KEY_DEVICE_ID, androidId);
            } else {
                String uuid = SystemUtils.newRandomUUID();
                SharedPreferencesHelper helper = SharedPreferencesHelper.getInstance(this, Config.PREFS_NAME);
                helper.putValue(Config.KEY_DEVICE_ID, uuid);
                LogUtils.d("UUID---" + uuid);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
