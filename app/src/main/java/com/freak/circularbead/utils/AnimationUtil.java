package com.freak.circularbead.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;

/**
 * Created by Freak on 2019/10/22.
 */
public class AnimationUtil {
    public static void startAnimation(View view, Context context, @AnimRes int animResId) {
        Animation inAnim = AnimationUtils.loadAnimation(context, animResId);
        inAnim.setDuration(1000);
        view.startAnimation(inAnim);
    }
}
