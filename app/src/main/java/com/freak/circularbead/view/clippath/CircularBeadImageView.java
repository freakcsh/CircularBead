package com.freak.circularbead.view.clippath;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.freak.circularbead.R;


public class CircularBeadImageView extends AppCompatImageView {
    private Context context;

    private boolean isCircle; // 是否显示为圆形，如果为圆形则设置的corner无效
    private boolean isCoverSrc; // border、inner_border是否覆盖图片
    private int borderWidth; // 边框宽度
    private int borderColor = Color.WHITE; // 边框颜色
    private int innerBorderWidth; // 内层边框宽度
    private int innerBorderColor = Color.WHITE; // 内层边框充色

    private int cornerRadius; // 统一设置圆角半径，优先级高于单独设置每个角的半径
    private int cornerTopLeftRadius; // 左上角圆角半径
    private int cornerTopRightRadius; // 右上角圆角半径
    private int cornerBottomLeftRadius; // 左下角圆角半径
    private int cornerBottomRightRadius; // 右下角圆角半径

    private int maskColor; // 遮罩颜色

    private Xfermode xfermode;

    private int width;
    private int height;
    private float radius;

    private float[] borderRadii;
    private float[] srcRadii;

    private RectF srcRectF; // 图片占的矩形区域
    private RectF borderRectF; // 边框的矩形区域

    private Paint paint;
    private Path path; // 用来裁剪图片的ptah
    private Path srcPath; // 图片区域大小的path
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader bitmapShader;

    /**
     * 画笔
     */

    public CircularBeadImageView(Context context) {
        this(context, null);
    }

    public CircularBeadImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularBeadImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircularBeadImageView, 0, 0);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.CircularBeadImageView_CircularBeadIsCoverSrc) {
                isCoverSrc = ta.getBoolean(attr, isCoverSrc);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadIsCircle) {
                isCircle = ta.getBoolean(attr, isCircle);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadBorderWidth) {
                borderWidth = ta.getDimensionPixelSize(attr, borderWidth);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadBorderColor) {
                borderColor = ta.getColor(attr, borderColor);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadInnerBorderWidth) {
                innerBorderWidth = ta.getDimensionPixelSize(attr, innerBorderWidth);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadInnerBorderColor) {
                innerBorderColor = ta.getColor(attr, innerBorderColor);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadCornerRadius) {
                cornerRadius = ta.getDimensionPixelSize(attr, cornerRadius);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadCornerTopLeftRadius) {
                cornerTopLeftRadius = ta.getDimensionPixelSize(attr, cornerTopLeftRadius);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadCornerTopRightRadius) {
                cornerTopRightRadius = ta.getDimensionPixelSize(attr, cornerTopRightRadius);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadCornerBottomLeftRadius) {
                cornerBottomLeftRadius = ta.getDimensionPixelSize(attr, cornerBottomLeftRadius);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadCornerBottomRightRadius) {
                cornerBottomRightRadius = ta.getDimensionPixelSize(attr, cornerBottomRightRadius);
            } else if (attr == R.styleable.CircularBeadImageView_CircularBeadMaskColor) {
                maskColor = ta.getColor(attr, maskColor);
            }
        }
        ta.recycle();

        borderRadii = new float[8];
        srcRadii = new float[8];

        borderRectF = new RectF();
        srcRectF = new RectF();

        paint = new Paint();
        path = new Path();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            srcPath = new Path();
        }

        calculateRadii();
        clearInnerBorderWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        initBorderRectF();
        initSrcRectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 使用图形混合模式来显示指定区域的图片
        canvas.saveLayer(srcRectF, null, Canvas.ALL_SAVE_FLAG);
        if (!isCoverSrc) {
            float sx = 1.0f * (width - 2 * borderWidth - 2 * innerBorderWidth) / width;
            float sy = 1.0f * (height - 2 * borderWidth - 2 * innerBorderWidth) / height;
            // 缩小画布，使图片内容不被borders覆盖
            canvas.scale(sx, sy, width / 2.0f, height / 2.0f);
        }
        super.onDraw(canvas);
        paint.reset();
        path.reset();
        srcPath.reset();
        if (getDrawable() != null) {
            Bitmap bitmap = drawableToBitmap(getDrawable());
            bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(bitmapShader);
        }


        if (isCircle) {
            path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW);
        } else {
            path.addRoundRect(srcRectF, srcRadii, Path.Direction.CCW);
        }
        //设置抗锯齿
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        //设置混合模式
        paint.setXfermode(xfermode);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(path, paint);
        } else {
            srcPath.addRect(srcRectF, Path.Direction.CCW);
            // 计算tempPath和path的差集
            srcPath.op(path, Path.Op.DIFFERENCE);
            canvas.drawPath(srcPath, paint);
        }
        // 还原混合模式
        paint.setXfermode(null);
        // 绘制遮罩
        if (maskColor != 0) {
            paint.setColor(maskColor);
            canvas.drawPath(path, paint);
        } else {
            //在没有图片或者颜色资源的时候，设置一个默认的颜色 TODO 需要开启一个属性开启和关闭，避免不需要显示的情况
            paint.setColor(Color.WHITE);
            canvas.drawPath(path, paint);
        }
        // 恢复画布
        canvas.restore();
        // 绘制边框
        drawBorders(canvas);
    }

    private void drawBorders(Canvas canvas) {
        if (isCircle) {
            if (borderWidth > 0) {
                drawCircleBorder(canvas, borderWidth, borderColor, radius - borderWidth / 2.0f);
            }
            if (innerBorderWidth > 0) {
                drawCircleBorder(canvas, innerBorderWidth, innerBorderColor, radius - borderWidth - innerBorderWidth / 2.0f);
            }
        } else {
            if (borderWidth > 0) {
                drawRectFBorder(canvas, borderWidth, borderColor, borderRectF, borderRadii);
            }
        }
    }

    private void drawCircleBorder(Canvas canvas, int borderWidth, int borderColor, float radius) {
        initBorderPaint(borderWidth, borderColor);
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CCW);
        canvas.drawPath(path, paint);
    }

    private void drawRectFBorder(Canvas canvas, int borderWidth, int borderColor, RectF rectF, float[] radii) {
        initBorderPaint(borderWidth, borderColor);
        path.addRoundRect(rectF, radii, Path.Direction.CCW);
        canvas.drawPath(path, paint);
    }

    private void initBorderPaint(int borderWidth, int borderColor) {
        path.reset();
        paint.setStrokeWidth(borderWidth);
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 计算外边框的RectF
     */
    private void initBorderRectF() {
        if (!isCircle) {
            borderRectF.set(borderWidth / 2.0f, borderWidth / 2.0f, width - borderWidth / 2.0f, height - borderWidth / 2.0f);
        }
    }

    /**
     * 计算图片原始区域的RectF
     */
    private void initSrcRectF() {
        if (isCircle) {
            radius = Math.min(width, height) / 2.0f;
            srcRectF.set(width / 2.0f - radius, height / 2.0f - radius, width / 2.0f + radius, height / 2.0f + radius);
        } else {
            srcRectF.set(0, 0, width, height);
            if (isCoverSrc) {
                srcRectF = borderRectF;
            }
        }
    }

    /**
     * 计算RectF的圆角半径
     */
    private void calculateRadii() {
        if (isCircle) {
            return;
        }
        if (cornerRadius > 0) {
            for (int i = 0; i < borderRadii.length; i++) {
                borderRadii[i] = cornerRadius;
                srcRadii[i] = cornerRadius - borderWidth / 2.0f;
            }
        } else {
            borderRadii[0] = borderRadii[1] = cornerTopLeftRadius;
            borderRadii[2] = borderRadii[3] = cornerTopRightRadius;
            borderRadii[4] = borderRadii[5] = cornerBottomRightRadius;
            borderRadii[6] = borderRadii[7] = cornerBottomLeftRadius;

            srcRadii[0] = srcRadii[1] = cornerTopLeftRadius - borderWidth / 2.0f;
            srcRadii[2] = srcRadii[3] = cornerTopRightRadius - borderWidth / 2.0f;
            srcRadii[4] = srcRadii[5] = cornerBottomRightRadius - borderWidth / 2.0f;
            srcRadii[6] = srcRadii[7] = cornerBottomLeftRadius - borderWidth / 2.0f;
        }
    }

    private void calculateRadiiAndRectF(boolean reset) {
        if (reset) {
            cornerRadius = 0;
        }
        calculateRadii();
        initBorderRectF();
        invalidate();
    }

    /**
     * 目前圆角矩形情况下不支持inner_border，需要将其置0
     */
    private void clearInnerBorderWidth() {
        if (!isCircle) {
            this.innerBorderWidth = 0;
        }
    }

    public void isCoverSrc(boolean isCoverSrc) {
        this.isCoverSrc = isCoverSrc;
        initSrcRectF();
        invalidate();
    }

    public void isCircle(boolean isCircle) {
        this.isCircle = isCircle;
        clearInnerBorderWidth();
        initSrcRectF();
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = CircularBeadImageViewUtils.dp2px(context, borderWidth);
        calculateRadiiAndRectF(false);
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    public void setInnerBorderWidth(int innerBorderWidth) {
        this.innerBorderWidth = CircularBeadImageViewUtils.dp2px(context, innerBorderWidth);
        clearInnerBorderWidth();
        invalidate();
    }

    public void setInnerBorderColor(@ColorInt int innerBorderColor) {
        this.innerBorderColor = innerBorderColor;
        invalidate();
    }

    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = CircularBeadImageViewUtils.dp2px(context, cornerRadius);
        calculateRadiiAndRectF(false);
    }

    public void setCornerTopLeftRadius(int cornerTopLeftRadius) {
        this.cornerTopLeftRadius = CircularBeadImageViewUtils.dp2px(context, cornerTopLeftRadius);
        calculateRadiiAndRectF(true);
    }

    public void setCornerTopRightRadius(int cornerTopRightRadius) {
        this.cornerTopRightRadius = CircularBeadImageViewUtils.dp2px(context, cornerTopRightRadius);
        calculateRadiiAndRectF(true);
    }

    public void setCornerBottomLeftRadius(int cornerBottomLeftRadius) {
        this.cornerBottomLeftRadius = CircularBeadImageViewUtils.dp2px(context, cornerBottomLeftRadius);
        calculateRadiiAndRectF(true);
    }

    public void setCornerBottomRightRadius(int cornerBottomRightRadius) {
        this.cornerBottomRightRadius = CircularBeadImageViewUtils.dp2px(context, cornerBottomRightRadius);
        calculateRadiiAndRectF(true);
    }

    public void setMaskColor(@ColorInt int maskColor) {
        this.maskColor = maskColor;
        invalidate();
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
