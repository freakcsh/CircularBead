package com.freak.circularbeadimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;


public class CircularBeadClipPathImageView extends AppCompatImageView {
    private int mWidth;
    private int mHeight;
    private Path path;
    private int corners;
    private int bottomLeftRadius;
    private int bottomRightRadius;
    private int topLeftRadius;
    private int topRightRadius;
    private boolean isCircularImageView = false;
    private int clipPathPaintColor = Color.WHITE;
    private Paint paint;
    private final Matrix mShaderMatrix = new Matrix();
    private int mBitmapWidth;
    private int mBitmapHeight;
    private BitmapShader mBitmapShader;
    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private static final int COLOR_DRAWABLE_DIMENSION = 2;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    public CircularBeadClipPathImageView(Context context) {
        this(context, null);
    }

    public CircularBeadClipPathImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularBeadClipPathImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularBeadClipPathImageView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.CircularBeadClipPathImageView_clipPathCorners) {
                corners = typedArray.getDimensionPixelSize(attr, corners);
            } else if (attr == R.styleable.CircularBeadClipPathImageView_clipPathBottomLeftRadius) {
                bottomLeftRadius = typedArray.getDimensionPixelSize(attr, bottomLeftRadius);
            } else if (attr == R.styleable.CircularBeadClipPathImageView_clipPathBottomRightRadius) {
                bottomRightRadius = typedArray.getDimensionPixelSize(attr, bottomRightRadius);
            } else if (attr == R.styleable.CircularBeadClipPathImageView_clipPathTopLeftRadius) {
                topLeftRadius = typedArray.getDimensionPixelSize(attr, topLeftRadius);
            } else if (attr == R.styleable.CircularBeadClipPathImageView_clipPathTopRightRadius) {
                topRightRadius = typedArray.getDimensionPixelSize(attr, topRightRadius);
            } else if (attr == R.styleable.CircularBeadClipPathImageView_clipPathIsCircularImageView) {
                isCircularImageView = typedArray.getBoolean(attr, isCircularImageView);
            } else if (attr == R.styleable.CircularBeadClipPathImageView_clipPathPaintColor) {
                clipPathPaintColor = typedArray.getColor(attr, clipPathPaintColor);
            }
        }
        typedArray.recycle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(new OutlineProvider());
        }
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(clipPathPaintColor);
        paint.setStyle(Paint.Style.FILL);
        if (corners != 0) {
            bottomLeftRadius = corners;
            bottomRightRadius = corners;
            topLeftRadius = corners;
            topRightRadius = corners;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isCircularImageView) {
            //参数说明：第一第二个参数：圆心坐标 第三个参数：圆的半径，第四个参数：绕圆轮廓的方向
            path.addCircle(mWidth / 2, mHeight / 2, mWidth / 2, Path.Direction.CW);
            canvas.drawPath(path, paint);
            canvas.clipPath(path);
        } else {
            //这里做下判断，只有图片的宽高大于设置的圆角距离的时候才进行裁剪
            //左边上下圆弧的半径的最大值
            int maxLeft = Math.max(topLeftRadius, bottomLeftRadius);
            //右边上下圆弧的半径的最大值
            int maxRight = Math.max(topRightRadius, bottomRightRadius);
            int minWidth = maxLeft + maxRight;
            //上边左右圆弧的最大值
            int maxTop = Math.max(topLeftRadius, topRightRadius);
            //下边左右圆弧的最大值
            int maxBottom = Math.max(bottomLeftRadius, bottomRightRadius);
            int minHeight = maxTop + maxBottom;
            if (mWidth >= minWidth && mHeight > minHeight) {
                //四个角：右上，右下，左下，左上
                //起点：左上圆弧的坐标点
                path.moveTo(topLeftRadius, 0);
                //连线：从起点连接到右上圆弧的坐标点
                path.lineTo(mWidth - topRightRadius, 0);
                //二阶贝塞尔曲线画右上边圆弧：第一个参数：切线的交点坐标  第二个参数：终点坐标
                path.quadTo(mWidth, 0, mWidth, topRightRadius);
                //连线：从右上边圆弧终点连接到右下边圆弧起点
                path.lineTo(mWidth, mHeight - bottomRightRadius);
                //二阶贝塞尔曲线绘制右下边圆弧
                path.quadTo(mWidth, mHeight, mWidth - bottomRightRadius, mHeight);
                //连线右下圆弧和左下圆弧的线
                path.lineTo(bottomLeftRadius, mHeight);
                //绘制左下圆弧
                path.quadTo(0, mHeight, 0, mHeight - bottomLeftRadius);
                //连线左下圆弧和左上圆弧
                path.lineTo(0, topLeftRadius);
                //绘制左上圆弧
                path.quadTo(0, 0, topLeftRadius, 0);
                canvas.drawPath(path, paint);
                //画布剪裁，设置画布的显示区域 ：将当前剪辑与指定路径相交。
                canvas.clipPath(path);
            }
        }

        super.onDraw(canvas);
    }

    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMENSION, COLOR_DRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class OutlineProvider extends ViewOutlineProvider {

        @Override
        public void getOutline(View view, Outline outline) {
            Rect bounds = new Rect();
            mBorderRect.roundOut(bounds);
            outline.setRoundRect(bounds, bounds.width() / 2.0f);
        }

    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }
}
