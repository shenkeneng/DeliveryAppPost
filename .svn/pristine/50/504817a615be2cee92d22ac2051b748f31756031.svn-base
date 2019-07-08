package com.frxs.delivery.zxing;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ewu.core.widget.MaterialDialog;
import com.frxs.delivery.FrxsActivity;
import com.frxs.delivery.R;
import com.frxs.delivery.ScanLoadingActivity;
import com.frxs.delivery.application.FrxsApplication;
import com.frxs.delivery.model.ScanLoadingInfo;
import com.frxs.delivery.model.UserInfo;
import com.frxs.delivery.rest.model.AjaxParams;
import com.frxs.delivery.rest.model.ApiResponse;
import com.frxs.delivery.rest.service.SimpleCallback;
import com.frxs.delivery.zxing.camera.CameraManager;
import com.frxs.delivery.zxing.decoding.CaptureActivityHandler;
import com.frxs.delivery.zxing.decoding.InactivityTimer;
import com.frxs.delivery.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

import retrofit2.Call;


/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/05/10
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class CaptureActivity extends FrxsActivity implements SurfaceHolder.Callback {

    private CaptureActivityHandler handler;

    private ViewfinderView viewfinderView;

    private boolean hasSurface;

    private String strSearch;// 搜索商品编号

    private TextView tvResult;

    private Vector<BarcodeFormat> decodeFormats;

    private String characterSet;

    private InactivityTimer inactivityTimer;

    private MediaPlayer mediaPlayer;

    private boolean playBeep;

    private static final float BEEP_VOLUME = 0.10f;

    private boolean vibrate;

    private TextView tvTitle;

    private Button btnConfirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_capture;
    }

    @Override
    protected void initViews() {
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnConfirm = (Button) findViewById(R.id.capture_restart_scan);
        findViewById(R.id.tv_title_right).setVisibility(View.GONE);
        tvTitle.setText("扫一扫");
    }

    @Override
    protected void initEvent() {
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.capture_restart_scan:
//                if (!TextUtils.isEmpty(strSearch)) {
//                    CameraManager.get().startPreview();
//                    Message msg = handler.obtainMessage();
//                    msg.what = R.id.restart_preview;
//                    handler.sendMessage(msg);
//                    tvResult.setText("");
//                    Intent intent = new Intent(CaptureActivity.this, ProductListActivity.class);
//                    intent.putExtra("SEARCH", strSearch);
//                    startActivity(intent);
//                    strSearch = "";
//                    finish();
//                } else {
//                    ToastUtils.show(CaptureActivity.this, "请扫描商品");
//                }
//                break;
            case R.id.tv_title_left:
                finish();
            default:
                break;
        }
    }

    @Override
    protected void initData() {
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        playBeepSoundAndVibrate();
        tvResult.setText("扫描结果：" + result.getText());
        strSearch = result.getText();
        if (TextUtils.isEmpty(strSearch)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            CameraManager.get().stopPreview();
            Message msg = handler.obtainMessage();
            msg.what = R.id.restart_preview;
            handler.sendMessage(msg);
            tvResult.setText("");
            //请求当前用户订单
            requestPreSweepLoading(strSearch);
            strSearch = "";
        }
    }

    /**
     * 请求扫码预订单
     * @param scanCode
     */
    private void requestPreSweepLoading(String scanCode) {
        showProgressDialog();

        UserInfo userInfo = FrxsApplication.getInstance().getUserInfo();
        AjaxParams params = new AjaxParams();
        params.put("WID", userInfo.getWareHouseWID());
        params.put("ShippingUserID", userInfo.getEmpID());
        params.put("Code", scanCode);

        getService().PreSweepLoading(params.getUrlParams()).enqueue(new SimpleCallback<ApiResponse<ScanLoadingInfo>>() {
            @Override
            public void onResponse(ApiResponse<ScanLoadingInfo> result, int code, String msg) {
                dismissProgressDialog();
                if (result.getFlag().equals("0")) {
                    if (result.getData() != null) {
                        ScanLoadingInfo scanLoadingInfo = result.getData();
                        // 当前订单数不为0 跳转接单页面
                        if (null != scanLoadingInfo.getOrderDetail() && scanLoadingInfo.getOrderDetail().size() > 0){
                            Intent intent = new Intent(CaptureActivity.this, ScanLoadingActivity.class);
                            intent.putExtra("Content", scanLoadingInfo);
                            startActivity(intent);
                            finish();
                        } else {// 当前订单数量为0 返回扫码页面 提示信息
                            showInfoDialog(result.getInfo());
                        }
                    } else {// 当前返回数量为0 返回扫码页面 提示信息
                        showInfoDialog(result.getInfo());
                    }
                } else {
                    showInfoDialog(result.getInfo());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ScanLoadingInfo>> call, Throwable t) {
                super.onFailure(call, t);
                dismissProgressDialog();
                showInfoDialog(t.getMessage());
            }
        });
    }

    /**
     * 显示信息对话框
     * @param info
     */
    private void showInfoDialog(String info) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_selector);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tv_message);
        tvMessage.setText(info);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_confirm);
        dialog.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
        btnConfirm.setText("确定");// 确定停留当前页面
        dialog.setCancelable(false);// 设置按back键Dialog不消失
        dialog.show();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.init(this.getApplicationContext());
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            final MaterialDialog materialDialog = new MaterialDialog(this);
            materialDialog.setMessage("无法获取摄像头权限，请检查是否已经打开摄像头权限。");
            materialDialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();

            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {

        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CaptureActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
