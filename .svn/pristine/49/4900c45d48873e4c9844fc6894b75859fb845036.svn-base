package com.frxs.delivery.service.apkUpdate;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.ewu.core.utils.LogUtils;
import com.ewu.core.utils.PrefUtils;
import com.ewu.core.utils.ToastUtils;
import com.frxs.delivery.comms.GlobelDefines;
import com.frxs.delivery.widget.ProgressAlertDialog;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2017/03/16
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class DownloadService {

    public static final String DOWNLOAD_APK_NAME = "app-order.apk";
    public static final Uri CONTENT_URI  = Uri.parse("content://downloads/my_downloads");
    private Context context;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    private long downloadId = -1;
    private Handler handler;
    private ProgressAlertDialog dialog;
    private DownloadObserver downloadObserver;
    private boolean isForceUpdate;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DownloadService(Context activity, String downloadUrl, boolean isForceUpdate) {
        this.context = activity.getApplicationContext();
        this.isForceUpdate = isForceUpdate;
        handler = new MyHandler();
        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle(context.getResources().getString(com.ewu.core.R.string.app_name));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadUrl));
        request.setMimeType(mimeString);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(true);
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , DOWNLOAD_APK_NAME) ;


        if (isForceUpdate) {
            dialog = new ProgressAlertDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(!isForceUpdate);
            dialog.setTvCloseListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.dismiss();
                        //删除下载任务 退出应用
                        downloadId = (Long) PrefUtils.getValue(context, GlobelDefines.KEY_DOWNLOAD_ID, -1l);
                        downloadManager.remove(downloadId);
                        System.exit(0);
                    }
                }
            });
        }
    }

    public void execute() {
        removeOldApk();
        downloadId = (Long) PrefUtils.getValue(context, GlobelDefines.KEY_DOWNLOAD_ID, -1l);
        if (downloadId != -1) {
            //删除下载任务，包括通知栏和APK文件
            downloadManager.remove(downloadId);
        }

        downloadId = downloadManager.enqueue(request);
        PrefUtils.setValue(context, GlobelDefines.KEY_DOWNLOAD_ID, downloadId);
        downloadObserver = new DownloadObserver(handler, downloadManager, downloadId);
        context.getContentResolver().registerContentObserver(CONTENT_URI, true, downloadObserver);
        if (null != dialog) {
            dialog.show();
        }
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int status = (Integer) msg.obj;
                switch (status) {
                    case DownloadManager.STATUS_RUNNING:
                    case DownloadManager.STATUS_PENDING:
                    case DownloadManager.STATUS_PAUSED: {
                        if (null != dialog && dialog.isShowing()) {
                            int curSize = msg.arg1;
                            int totalSize = msg.arg2;
                            if (dialog.getMax() != totalSize) {
                                dialog.setMax(totalSize / 1024);
                            }
                            dialog.setProgress(curSize / 1024);
                        }
                        break;
                    }
                    case DownloadManager.STATUS_SUCCESSFUL: {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        String apkFilePath = buildApkFilePath();
                        installApk(context, apkFilePath);
                        context.getContentResolver().unregisterContentObserver(downloadObserver);
                        break;
                    }
                    case DownloadManager.STATUS_FAILED: {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        downloadManager.remove(downloadId);
                        context.getContentResolver().unregisterContentObserver(downloadObserver);
                        ToastUtils.showLongToast(context, "程序更新失败");
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

    private String buildApkFilePath() {
        return new StringBuilder(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())
                .append(File.separator).append(DownloadService.DOWNLOAD_APK_NAME)
                .toString();
    }

    private void installApk(Context context, String filePath) {
        File file = new File(filePath);
        if(file.exists()){
            openFile(file,context);
        }else{
            Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
        }

        if (isForceUpdate) {
            System.exit(0);
        }
    }

    /**
     * 删除上次更新存储在本地的apk
     */
    private void removeOldApk() {
        //获取老ＡＰＫ的存储路径
        String apkFilePath = buildApkFilePath();
        File fileName = new File(apkFilePath);
        LogUtils.i("老APK的存储路径 =" + apkFilePath);

        if (fileName != null && fileName.exists() && fileName.isFile()) {
            fileName.delete();
            LogUtils.i("存储器内存在老APK，进行删除操作");
        }
    }

    public void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Uri uriForFile = FileProvider.getUriForFile(var1, var1.getApplicationContext().getPackageName() + ".provider", var0);
            var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        }else{
            var2.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }
        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }
}
