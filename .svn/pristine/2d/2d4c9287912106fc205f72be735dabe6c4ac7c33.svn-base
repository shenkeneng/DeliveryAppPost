/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package com.ewu.core.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;

    private ToastUtils() {}

    public static void show(Context context, CharSequence text, int duration) {
        Toast.makeText(context, text, duration).show();
    }
    public static void show(Context context, int resId, int duration) {
        show(context, context.getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId) {
        show(context, context.getText(resId));
    }

    public static void showLongToast(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showShortToast(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(msg);
        }
        mToast.show();

    }
}
