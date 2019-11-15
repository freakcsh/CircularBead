package com.freak.circularbead.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.DrawableRes;

public class UriUtil {
    public static Uri drawableToUri(Context context, @DrawableRes int drawable) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + context.getResources().getResourcePackageName(drawable) + "/"
                + context.getResources().getResourceTypeName(drawable) + "/"
                + context.getResources().getResourceEntryName(drawable));
        return imageUri;
    }
}
