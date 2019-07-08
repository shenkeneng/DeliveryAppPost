package com.frxs.delivery.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.ewu.core.utils.CheckUtils;
import com.ewu.core.utils.SystemUtils;
import com.frxs.delivery.R;


/**
 * @author cate 2014-12-18 上午10:31:00
 */

public class CountEditText extends LinearLayout implements OnClickListener {

    private int mCount = 1;

    private EditText mEdit;

    private ImageView mMin;

    private ImageView mAdd;

    private onCountChangeListener mOnCountChangeListener;

    private OnClickListener mOnClickListener;

    private int maxCount = Integer.MAX_VALUE;

    @SuppressLint("NewApi")
    public CountEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public CountEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CountEditText(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LinearLayout view = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_countedittext, null);
        mMin = (ImageView) view.findViewById(R.id.count_sub);
        mAdd = (ImageView) view.findViewById(R.id.count_add);
        mEdit = (EditText) view.findViewById(R.id.count_edit);
        mEdit.setText(String.valueOf(mCount));
        mMin.setOnClickListener(this);
        mAdd.setOnClickListener(this);
        mEdit.setSelection(mEdit.getText().length());
        this.addView(view);
        if (SystemUtils.getSDKVersion() < 16) {
            // mMin.setBackgroundDrawable(ViewUtils.getStateDrawable(getContext(),
            // normal, active, disable));
        } else {
            // mMin.setBackground(ViewUtils.getStateDrawable(getContext(),
            // normal, active, disable));
        }

        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mEdit.setSelection(mEdit.getText().length());
                String content = s.toString().trim();
                if (CheckUtils.strIsNumber(content)) {
                    mCount = Integer.valueOf(content);
                } else {
                    mCount = 1;
                }
            }
        });
    }

    public void setEditTextClickale(OnClickListener listener) {
        mOnClickListener = listener;
        mEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (null != mOnClickListener) {
                        mOnClickListener.onClick(v);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.count_sub: {
                if (getCount() > 0) {
                    mCount--;
                    if (mCount == 0) {
                        mMin.setEnabled(false);
                        mMin.setImageResource(R.mipmap.icon_subtract_disable);
                    }
                    if (mCount < getMaxCount()) {
                        mAdd.setEnabled(true);
                        mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
                    }
                    mEdit.setText(String.valueOf(mCount));
                    if (mOnCountChangeListener != null) {
                        mOnCountChangeListener.onCountSub(mCount);
                    }
                }
                break;
            }
            case R.id.count_add: {
                if (getCount() < maxCount) {
                    mCount++;
                    if (mCount > 0) {
                        mMin.setEnabled(true);
                        mMin.setImageResource(R.mipmap.icon_subtract_enable);
                    }
                    if (mCount == maxCount) {
                        mAdd.setEnabled(false);
                        mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
                    }
                    mEdit.setText(String.valueOf(mCount));
                    if (mOnCountChangeListener != null) {
                        mOnCountChangeListener.onCountAdd(mCount);
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    public onCountChangeListener getOnCountChangeListener() {
        return mOnCountChangeListener;
    }

    public void setOnCountChangeListener(onCountChangeListener mOnCountChangeListener) {
        this.mOnCountChangeListener = mOnCountChangeListener;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
        if (mCount <= 0) {
            mMin.setEnabled(false);
            mMin.setImageResource(R.mipmap.icon_subtract_disable);

            mEdit.setText(String.valueOf(mCount));
        } else if (0 < mCount && mCount < getMaxCount()) {
            mMin.setEnabled(true);
            mMin.setImageResource(R.mipmap.icon_subtract_enable);
            mAdd.setEnabled(true);
            mAdd.setImageResource(R.mipmap.icon_red_cross_enable);

            mEdit.setText(String.valueOf(mCount));
        } else {
            // 当前购物车数量与最大库存相等时显示最大库存，且“+”不可操作“-”号可操作|陈铁
            mEdit.setText(String.valueOf(maxCount));
            mMin.setEnabled(true);
            mMin.setImageResource(R.mipmap.icon_subtract_enable);
            mAdd.setEnabled(false);
            mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
        }

    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
        mCount = 0;
        mEdit.setText(String.valueOf(mCount));
        mMin.setEnabled(false);
        mMin.setImageResource(R.mipmap.icon_subtract_disable);
        if (maxCount > 0) {
            mAdd.setEnabled(true);
            mAdd.setImageResource(R.mipmap.icon_red_cross_enable);
        } else {
            mAdd.setEnabled(false);
            mAdd.setImageResource(R.mipmap.icon_red_cross_disable);
        }

    }

    public interface onCountChangeListener {

        void onCountAdd(int count);

        void onCountSub(int count);
    }
}
