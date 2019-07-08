package com.ewu.core.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;

/**
 * <pre>
 *     author : ewu
 *     e-mail : xxx@xx
 *     time   : 2018/04/11
 *     desc   : xxxx描述
 *     version: 1.0
 * </pre>
 */
public class FaceView extends View {
    private Paint mPaint = null;
    private String mColor = "#42ed45";
    private ArrayList<RectF> mFaces = null;

    public FaceView(Context context) {
        super(context);
        init(context);
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor(mColor));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.getResources().getDisplayMetrics()));
        mPaint.setAntiAlias(true);
    }

    public void setFaces(ArrayList<RectF> faces) {
        mFaces = faces;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mFaces) {
            for (RectF face : mFaces) {
                canvas.drawRect(face, mPaint);
            }
        }
    }
}
