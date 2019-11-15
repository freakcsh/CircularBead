package com.freak.circularbeadimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class CircularBeadView extends AppCompatImageView {
    private int mWidth;
    private int mHeight;

    private int corners;
    private int bottomLeftRadius;
    private int bottomRightRadius;
    private int topLeftRadius;
    private int topRightRadius;
    private int strokeWidth;
    private int strokeColor = Color.WHITE;
    private int solidColor = Color.WHITE;
    private boolean isImageView = false;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader bitmapShader;
    /**
     * 3x3 矩阵，主要用于缩小放大
     */
    private Matrix mMatrix;
    /**
     * 画笔
     */
    private Paint mPaint;


    public CircularBeadView(Context context) {
        this(context, null);
    }

    public CircularBeadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularBeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularBeadView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CircularBeadView_corners) {
                corners = typedArray.getDimensionPixelSize(attr, corners);
            } else if (attr == R.styleable.CircularBeadView_bottomLeftRadius) {
                bottomLeftRadius = typedArray.getDimensionPixelSize(attr, bottomLeftRadius);
            } else if (attr == R.styleable.CircularBeadView_bottomRightRadius) {
                bottomRightRadius = typedArray.getDimensionPixelSize(attr, bottomRightRadius);
            } else if (attr == R.styleable.CircularBeadView_topLeftRadius) {
                topLeftRadius = typedArray.getDimensionPixelSize(attr, topLeftRadius);
            } else if (attr == R.styleable.CircularBeadView_topRightRadius) {
                topRightRadius = typedArray.getDimensionPixelSize(attr, topRightRadius);
            } else if (attr == R.styleable.CircularBeadView_strokeWidth) {
                strokeWidth = typedArray.getDimensionPixelSize(attr, strokeWidth);
            } else if (attr == R.styleable.CircularBeadView_strokeColor) {
                strokeColor = typedArray.getColor(attr, strokeColor);
            } else if (attr == R.styleable.CircularBeadView_solidColor) {
                solidColor = typedArray.getColor(attr, solidColor);
            } else if (attr == R.styleable.CircularBeadView_isImageView) {
                isImageView = typedArray.getBoolean(attr, isImageView);
            }
        }
        typedArray.recycle();
        mMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        if (getDrawable() == null) {
            return;
        }
        Bitmap bitmap = drawableToBitmap(getDrawable());
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //缩放比例
        float scale = 1.0f;
        if (!(bitmap.getWidth() == getWidth() && bitmap.getHeight() == getHeight())){
            // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(),
                    getHeight() * 1.0f / bitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale,scale);
        // 设置变换矩阵
        bitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mPaint.setShader(bitmapShader);

       RectF rectF= new RectF(0,0,getWidth(),getHeight());
        canvas.drawRoundRect(rectF,20,20,mPaint);
//        super.onDraw(canvas);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            return bitmapDrawable.getBitmap();
        }
        // 当设置不为图片，为颜色时，获取的drawable宽高会有问题，所有当为颜色时候获取控件的宽高
        int width = drawable.getIntrinsicWidth() <= 0 ? getWidth() : drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight() <= 0 ? getHeight() : drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }
}
