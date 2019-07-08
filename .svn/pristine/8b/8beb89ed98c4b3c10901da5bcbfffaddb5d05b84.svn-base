package com.frxs.delivery.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ewu.core.utils.ToastUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateService {
    private static final String TAG = UpdateService.class.getSimpleName();

    private Activity activity;
    private String downloadUrl;
    private boolean isForceUpdate;

    private String appName = "delivery.apk";
    private String savePath = "";

    private String apkFilePath = "";


    public UpdateService(Activity activity, String url, String apkName, boolean isForceUpdate) {
        this.activity = activity;
        this.downloadUrl = url;
        this.isForceUpdate = isForceUpdate;

        if (!TextUtils.isEmpty(apkName)) {
            this.appName = apkName + ".apk";
        }
    }

    public void downFile() {
        new DownloadTask(downloadUrl).execute();
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        private String path;

        public DownloadTask(String url) {
            this.path = url;

            dialog = new ProgressDialog(activity);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(!isForceUpdate);
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/.um/apk";
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.connect();
                    dialog.setMax(conn.getContentLength());
                    int response = conn.getResponseCode();

                    if (response == 200) {
                        InputStream in = conn.getInputStream();
                        File f = new File(file, appName);
                        if (f.exists()) {
                            f.delete();
                        }

                        f.createNewFile();

                        FileOutputStream fos = new FileOutputStream(f);
                        BufferedInputStream bis = new BufferedInputStream(in);
                        byte[] buffer = new byte[1024];
                        int len;
                        int total = 0;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            total += len;
                            //获取当前下载量
                            dialog.setProgress(total);
                        }
                        fos.close();
                        bis.close();
                        in.close();

                        Message myMessage = new Message();
                        myMessage.what = 1;
                        myMessage.obj = f.getPath();
                        mHandler.sendMessage(myMessage);
                    } else {
                        Message myMessage = new Message();
                        myMessage.what = 0;
                        myMessage.obj = "";
                        mHandler.sendMessage(myMessage);
                    }

                }
            } catch (Exception ex) {
                Message myMessage = new Message();
                myMessage.what = 0;
                myMessage.obj = "";
                mHandler.sendMessage(myMessage);
            }


            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog != null) {
                dialog.dismiss();
            }

        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    ToastUtils.show(activity, "无法下载APK文件，请重试");
                    break;
                }
                case 1: {
                    String filepath = (String) msg.obj;
                    installApk(filepath);
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    private void installApk(String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        activity.startActivity(i);

        if (isForceUpdate) {
            activity.finish();
        }
    }


}
