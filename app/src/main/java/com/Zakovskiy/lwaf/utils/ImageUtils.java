package com.Zakovskiy.lwaf.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.GenericRequest;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

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
        } else {
            glide.skipMemoryCache(true);
            glide.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        glide.into(image);
    }

    public static void loadTarget(Context context, Object url, View image, Boolean crope, Boolean cache) {
        DrawableTypeRequest glide = Glide.with(context).load(url);
        if (crope) {
            glide.fitCenter();
            glide.centerCrop();
        }
        glide.skipMemoryCache(true);
        if (cache) {
            glide.diskCacheStrategy(DiskCacheStrategy.ALL);
        }
        glide.into(new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable resource, GlideAnimation<? super GlideBitmapDrawable> glideAnimation) {
                byte[] chunk = resource.getBitmap().getNinePatchChunk();
                Logs.info("y");
                NinePatchDrawable drawable = new NinePatchDrawable(context.getResources(), resource.getBitmap(), chunk, null, null);
                image.setBackground(drawable);
            }
        });
    }
}
