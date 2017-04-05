package cn.bingoogolapple.photopicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import cn.bingoogolapple.photopicker.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/6/24 下午4:23
 * 描述:
 */
public class BGAImageView extends AppCompatImageView {
    private int mDefaultImageId;
    private int mCornerRadius = 0;
    private boolean mIsCircle = false;
    private boolean mIsSquare = false;
    private int mBorderWidth = 0;
    private int mBorderColor = Color.WHITE;

    private Paint mBorderPaint;

    private Delegate mDelegate;

    public BGAImageView(Context context) {
        this(context, null);
    }

    public BGAImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BGAImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initCustomAttrs(context, attrs);

        initBorderPaint();

        setDefaultImage();
    }

    private void initBorderPaint() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BGAImageView);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.BGAImageView_android_src) {
            mDefaultImageId = typedArray.getResourceId(attr, 0);
        } else if (attr == R.styleable.BGAImageView_bga_iv_isCircle) {
            mIsCircle = typedArray.getBoolean(attr, mIsCircle);
        } else if (attr == R.styleable.BGAImageView_bga_iv_cornerRadius) {
            mCornerRadius = typedArray.getDimensionPixelSize(attr, mCornerRadius);
        } else if (attr == R.styleable.BGAImageView_bga_iv_isSquare) {
            mIsSquare = typedArray.getBoolean(attr, mIsSquare);
        } else if (attr == R.styleable.BGAImageView_bga_iv_borderWidth) {
            mBorderWidth = typedArray.getDimensionPixelSize(attr, mBorderWidth);
        } else if (attr == R.styleable.BGAImageView_bga_iv_borderColor) {
            mBorderColor = typedArray.getColor(attr, mBorderColor);
        }
    }

    private void setDefaultImage() {
        if (mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        setImageDrawable(getResources().getDrawable(resId));
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                super.setImageDrawable(getRoundedDrawable(getContext(), bitmap, mCornerRadius));
            } else {
                super.setImageDrawable(drawable);
            }
        } else if (drawable instanceof BitmapDrawable && mIsCircle) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                super.setImageDrawable(getCircleDrawable(getContext(), bitmap));
            } else {
                super.setImageDrawable(drawable);
            }
        } else {
            super.setImageDrawable(drawable);
        }
        notifyDrawableChanged(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsCircle || mIsSquare) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
            int childWidthSize = getMeasuredWidth();
            heightMeasureSpec = widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsCircle && mBorderWidth > 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 1.0f * mBorderWidth / 2, mBorderPaint);
        }
    }

    private void notifyDrawableChanged(Drawable drawable) {
        if (mDelegate != null) {
            mDelegate.onDrawableChanged(drawable);
        }
    }

    public void setDelegate(Delegate delegate) {
        mDelegate = delegate;
    }

    public interface Delegate {
        void onDrawableChanged(Drawable drawable);
    }

    public static RoundedBitmapDrawable getCircleDrawable(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        circleDrawable.setAntiAlias(true);
        circleDrawable.setCircular(true);
        return circleDrawable;
    }

    public static RoundedBitmapDrawable getRoundedDrawable(Context context, Bitmap bitmap, float cornerRadius) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        roundedBitmapDrawable.setCornerRadius(cornerRadius);
        return roundedBitmapDrawable;
    }
}