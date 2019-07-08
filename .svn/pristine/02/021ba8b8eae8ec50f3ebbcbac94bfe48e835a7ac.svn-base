package com.ewu.core.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by ewu on 2016/6/1.
 */
public class ImageLoader {

    public static void loadImage(Context context, ImageView imageView, int placeholderResId) {
        Glide.with(context).load(placeholderResId).into(imageView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context).load(imageUrl).into(imageView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView, int errorResId) {
        Glide.with(context).load(imageUrl).error(errorResId).into(imageView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView imageView, int placeholderResId , int errorResId) {
        Glide.with(context).load(imageUrl).placeholder(placeholderResId).error(errorResId).into(imageView);
    }
}
