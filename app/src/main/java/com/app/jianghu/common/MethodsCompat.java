package com.app.jianghu.common;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.provider.MediaStore;

/**
 * Android各版本的兼容方法
 * @author songqy
 * @version 1.0.0
 * @created 2015-04-27
 */
public class MethodsCompat {
	
    @TargetApi(5)
    public static void overridePendingTransition(Activity activity, int enter_anim, int exit_anim) {
       	activity.overridePendingTransition(enter_anim, exit_anim);
    }

    @TargetApi(7)
    public static Bitmap getThumbnail(ContentResolver cr, long origId, int kind, Options options) {
       	return MediaStore.Images.Thumbnails.getThumbnail(cr,origId,kind, options);
    }
    
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

}