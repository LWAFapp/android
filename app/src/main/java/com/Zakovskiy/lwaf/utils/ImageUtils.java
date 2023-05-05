package com.Zakovskiy.lwaf.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.GenericRequest;
public class ImageUtils {

    public static void loadImage(Context context, Object url, ImageView image) {
        loadImage(context, url, image, true, false);
    }

    public static void loadImage(Context context, Object url, ImageView image, Boolean crope, Boolean cache) {
        DrawableTypeRequest glide = Glide.with(context).load(url);
        if (crope) {
            glide.fitCenter();
            glide.centerCrop();
        }
        if (cache) {
            glide.diskCacheStrategy(DiskCacheStrategy.ALL);
        }
        glide.into(image);
    }
}
