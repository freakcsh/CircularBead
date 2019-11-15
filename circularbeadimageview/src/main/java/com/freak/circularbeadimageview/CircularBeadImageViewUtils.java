package com.freak.circularbeadimageview;


import android.content.Context;

public class CircularBeadImageViewUtils {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}