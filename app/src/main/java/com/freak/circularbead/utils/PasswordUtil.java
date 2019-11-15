package com.freak.circularbead.utils;

import android.graphics.Rect;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class PasswordUtil {
    public static void showOrHidePassword(EditText editText, boolean isShow, ImageView imageView) {
        if (!isShow) {
            //显示密码
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setSelected(false);
        } else {
            //隐藏密码
            editText.setTransformationMethod(new TransformationMethod() {
                @Override
                public CharSequence getTransformation(CharSequence source, View view) {
                    return new PasswordCharSequence(source);
                }

                @Override
                public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

                }
            });
            imageView.setSelected(true);
        }
    }

    public static void showOrHidePassword(TextView textView, boolean isShow, ImageView imageView) {
        if (!isShow) {
            //显示密码
            textView.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setSelected(false);
        } else {
            //隐藏密码
            textView.setTransformationMethod(new TransformationMethod() {
                @Override
                public CharSequence getTransformation(CharSequence source, View view) {
                    return new PasswordCharSequence(source);
                }

                @Override
                public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

                }
            });
            imageView.setSelected(true);
        }
    }

    /**
     * 将密码转换成*显示
     */
    private static class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source;
        }

        @Override
        public char charAt(int index) {
            //这里返回的char，就是密码的样式，注意，是char类型的
            return '*';
        }

        @Override
        public int length() {
            return mSource.length();
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end);
        }
    }
}
