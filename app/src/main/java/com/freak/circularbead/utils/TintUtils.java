package com.freak.circularbead.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.core.graphics.drawable.DrawableCompat;


/**
 *
 * @author freak
 * @date 2019/9/11.
 */
public class TintUtils {

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    public static GradientDrawable createColorShape(int color, float topLeft, float topRight, float bottomRight, float bottomLeft) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(new float[]{topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft});
        drawable.setColor(color);
        return drawable;
    }
}
