package com.ewu.core.utils;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/04/09
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class CameraHelper implements Camera.PreviewCallback{
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int mDisplayOrientation = 0; //预览旋转的角度
    private int picWidth = 2160;        //保存图片的宽
    private int picHeight = 3840;      //保存图片的高

    private Activity mActivity;
    private CallBack mCallBack = null;   //自定义的回调

    public interface CallBack {
        void onPreviewFrame(byte[] data);
        void onTakePic(byte[] data);
        void onFaceDetect(ArrayList<RectF> faces);
    }

    public CameraHelper(Activity activity, SurfaceView surfaceView) {
        mActivity = activity;
        mSurfaceView = surfaceView;
        mSurfaceHolder = mSurfaceView.getHolder();

        init();
    }

    private void init() {
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (null == mCamera) {
                    openCamera(mCameraFacing);
                }
                startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                releaseCamera();
            }
        });
    }

    private boolean openCamera(int cameraFacing) {
        boolean supportCameraFacing = supportCameraFacing(cameraFacing);
        if (supportCameraFacing) {
            try {
                mCamera = Camera.open(cameraFacing);
                initParameters(mCamera);
                mCamera.setPreviewCallback(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                ToastUtils.show(mActivity, "打开相机失败!");
            }
        }

        return supportCameraFacing;
    }

    private void initParameters(Camera camera) {
        if (null != camera) {
            try {
                mParameters = camera.getParameters();
                mParameters.setPreviewFormat(ImageFormat.RGB_565);

                Camera.Size bestPreviewSize = getBestSize(mSurfaceView.getWidth(), mSurfaceView.getHeight(), mParameters.getSupportedPreviewSizes());
                //设置预览尺寸
                mParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
                Camera.Size bestPicSize = getBestSize(picWidth, picHeight, mParameters.getSupportedPictureSizes());
                //设置保存图片尺寸
                mParameters.setPictureSize(bestPicSize.width, bestPicSize.height);

                if (isSupportFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }

                camera.setParameters(mParameters);
            } catch (Exception ex) {
                ex.printStackTrace();
                //ToastUtils.show(mActivity, "相机初始化失败!");
            }
        }
    }

    //开始预览
    public void startPreview() {
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            setCameraDisplayOrientation(mActivity);
            mCamera.startPreview();
            startFaceDetect();
        } catch (IOException e) {e.printStackTrace();
            e.printStackTrace();
        }
    }

    public void stopPreview() {
        mCamera.stopPreview();
    }

    public void takePic() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                if (null != mCallBack) {
//                    mCamera.startPreview();
                    mCallBack.onTakePic(bytes);
                }
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (null != mCallBack) {
            mCallBack.onPreviewFrame(bytes);
        }
    }

    private void startFaceDetect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mCamera.getParameters().getMaxNumDetectedFaces() > 0) {
                mCamera.startFaceDetection();
                mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                    @Override
                    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                        if (null != mCallBack) {
                            mCallBack.onFaceDetect(transForm(faces));
                        }
                        LogUtils.d("检测到 ${faces.size} 张人脸");
                    }
                });
            }
        }
    }

    public void exchangeCamera() {
        releaseCamera();
        mCameraFacing = (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);

        openCamera(mCameraFacing);
        startPreview();
    }

    private void setCameraDisplayOrientation(Activity activity) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraFacing, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int screenDegree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                screenDegree = 0;
                break;
            case Surface.ROTATION_90:
                screenDegree = 90;
                break;
            case Surface.ROTATION_180:
                screenDegree = 180;
                break;
            case Surface.ROTATION_270:
                screenDegree = 270;
                break;
            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDisplayOrientation = (info.orientation + screenDegree) % 360;
            mDisplayOrientation = (360 - mDisplayOrientation) % 360;
        } else {
            mDisplayOrientation = (info.orientation - screenDegree + 360) % 360;
        }

        mCamera.setDisplayOrientation(mDisplayOrientation);

        LogUtils.d("屏幕的旋转角度 : $rotation");
        LogUtils.d("setDisplayOrientation(result) : $mDisplayOrientation");
    }

    //将相机中用于表示人脸矩形的坐标转换成UI页面的坐标
    private ArrayList<RectF> transForm(Camera.Face[] faces) {
        Matrix matrix = new Matrix();
        // Need mirror for front camera.
        boolean mirror = (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        matrix.setScale(mirror ? -1f : 1f, 1f);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(mDisplayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(mSurfaceView.getWidth() / 2000f, mSurfaceView.getHeight() / 2000f);
        matrix.postTranslate(mSurfaceView.getWidth() / 2f, mSurfaceView.getHeight() / 2f);

        ArrayList<RectF> rectList = new ArrayList<>();
        for (Camera.Face face : faces) {
            RectF srcRect = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                srcRect = new RectF(face.rect);
            }
            RectF dstRect = new RectF(0f, 0f, 0f, 0f);
            matrix.mapRect(dstRect, srcRect);
            rectList.add(dstRect);
        }

        return rectList;
    }

    //获取与指定宽高相等或最接近的尺寸
    private Camera.Size getBestSize(int targetWidth, int targetHeight, List<Camera.Size> sizeList) {
        Camera.Size bestSize = null;
        double targetRatio =  ((double) targetHeight)/targetWidth; //目标大小的宽高比
        double minDiff = targetRatio;

        for (Camera.Size size: sizeList) {
            if (size.width == targetWidth && size.height == targetHeight) {
                bestSize = size;
                break;
            }

            double supportedRatio = ((double) size.width) / size.height;
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio);
                bestSize = size;
            }
        }

        LogUtils.d("目标尺寸 ：$targetWidth * $targetHeight ，   比例  $targetRatio");
        LogUtils.d("最优尺寸 ：${bestSize?.height} * ${bestSize?.width}");
        return bestSize;
    }

    //判断是否支持某个相机
    private boolean supportCameraFacing(int cameraFacing) {
        Camera.CameraInfo info  = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraFacing) {
                return true;
            }
        }
        return false;
    }

    //判断是否支持某一对焦模式
    private boolean isSupportFocus(String focusMode) {
        boolean autoFocus = false;
        List<String> listFocusMode = mParameters.getSupportedFocusModes();
        for (String mode : listFocusMode) {
            if (mode == focusMode) {
                autoFocus = true;
            }

            LogUtils.d("相机支持的对焦模式： " + mode);
        }

        return autoFocus;
    }

    public Camera getmCamera() {
        return mCamera;
    }

    public int getCameraFacing() {
        return mCameraFacing;
    }

    public void addCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public void releaseCamera() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
