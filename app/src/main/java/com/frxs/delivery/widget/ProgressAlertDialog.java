package com.frxs.delivery.widget;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.frxs.delivery.R;
import java.text.NumberFormat;

/**
 * Created by Chentie on 2017/2/22.
 */

public class ProgressAlertDialog extends AlertDialog {

    private ProgressBar progressBar;// 进度条

    private TextView tvPercent;// 百分比

    private TextView tvNumber;// 进度单位

    private String progressNumberFormat;// 下载数据

    private int mMax;// 获取设置的最大值

    private boolean mHasSatred;

    private int mProgress;// 获取设置的进度值

    private NumberFormat numberFormat;// 格式化数值

    private Handler mhandler;

    private int mProgressStyle;// 进度条样式

    private TextView tvClose;

    private View.OnClickListener cListener;

    public ProgressAlertDialog(Context context) {
        super(context);
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progreess_update);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        tvPercent = (TextView)findViewById(R.id.tv_percent);
        tvNumber = (TextView)findViewById(R.id.tv_number);
        tvClose = (TextView) findViewById(R.id.tv_close);
        tvClose.setOnClickListener(cListener);

        mhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int pro = progressBar.getProgress();
                int max = progressBar.getMax();
                double dProgress = (double)pro/(double)(1024);
                double dMax = (double)max/(double)(1024);
                if (progressNumberFormat != null) {
                    String format = progressNumberFormat;
                    tvNumber.setText(String.format(format, dProgress, dMax));
                } else {
                    tvNumber.setText("");
                }
                if (numberFormat != null) {
                    double percent = (double) pro / (double) max;
                    SpannableString tmp = new SpannableString(numberFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                            0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvPercent.setText(tmp);
                } else {
                    tvPercent.setText("");
                }
            }
        };
        onProgressChange();

        if (mMax > 0){
            setMax(mMax);
        }

        if (mProgress > 0){
            setProgress(mProgress);
        }

    }

    public void initFormats() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            progressNumberFormat = "%1.2fM/%2.2fM";
            numberFormat = NumberFormat.getPercentInstance();
            numberFormat.setMaximumFractionDigits(0);
        }
    }

    private void onProgressChange(){
        mhandler.sendEmptyMessage(0);
    }

    public int getMax() {
        if (progressBar != null){
            return progressBar.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (progressBar != null){
            progressBar.setMax(max);
            onProgressChange();
        }else{
            mMax = max;
        }
    }

    public void setIndeterminate(boolean indeterminate){
        if (progressBar != null){
            progressBar.setIndeterminate(indeterminate);
        }
    }



    public void setProgress(int value){
        if (mHasSatred){
            progressBar.setProgress(value);
            onProgressChange();
        }else{
            mProgress = value;
        }
    }

    public void setProgressStyle(int style){
        mProgressStyle = style;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHasSatred = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasSatred = false;
    }

    public void setTvCloseListener(View.OnClickListener closeListener) {
        this.cListener = closeListener;
    }
}
