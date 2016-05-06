package com.lzh.repeatloadingimageviewdemo.repeatLoadingImageView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;

public class RepeatLoadingImageView extends ImageView {

    private Paint mImagePaint;
    private int mImageHeight, mImageWidth;
    private boolean mIsAutoStart = true;
    private int mMaskColor = Color.RED;
    private ClipDrawable mClipDrawable;
    private Drawable mMaskDrawable;
    private int maskHeight;
    private int mProgress;
    private ObjectAnimator mAnimator;
    private long mAnimDuration = 2500;
    private float mScaleX, mScaleY;
    private int mGravity = Gravity.LEFT;
    private int mOrientaion = ClipDrawable.HORIZONTAL;

    public RepeatLoadingImageView(Context context) {
        super(context);
        init();
    }

    public RepeatLoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(context, attrs);
    }

    public RepeatLoadingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        setMaskColor(mMaskColor);
    }

    public void setMaskColor(int maskColor) {
        initMaskBitmap(maskColor);
        initAnim();
    }

    private void init() {
        if (mImagePaint == null) {
            mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mImagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //measure the matrix size of ImageView drawable
        float[] f = new float[9];
        getImageMatrix().getValues(f);
        mScaleX = f[Matrix.MSCALE_X];
        mScaleY = f[Matrix.MSCALE_Y];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScaleX, mScaleY);
        mClipDrawable.setBounds(getDrawable().getBounds());
        mClipDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (mMaskColor != Color.TRANSPARENT) {
            init();
            initMaskBitmap(mMaskColor);
            initAnim();
        }
    }

    private Bitmap combineBitmap(Bitmap bg, Bitmap fg) {
        Bitmap bmp = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bg, 0, 0, null);
        canvas.drawBitmap(fg, 0, 0, mImagePaint);
        return bmp;
    }

    private void initMaskBitmap(int maskColor) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bgBmp = ((BitmapDrawable) drawable).getBitmap();
        mImageWidth = drawable.getIntrinsicWidth();
        mImageHeight = drawable.getIntrinsicHeight();

        Bitmap fgBmp = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888);
        Canvas fgCanvas = new Canvas(fgBmp);
        fgCanvas.drawColor(maskColor);

        Bitmap bitmap = combineBitmap(bgBmp, fgBmp);
        mMaskDrawable = new BitmapDrawable(null, bitmap);
        mClipDrawable = new ClipDrawable(mMaskDrawable, mGravity, mOrientaion);
    }

    private void setMaskHeight(int y) {
        maskHeight = y;
        postInvalidate();
    }

    private void initAnim() {
        stopAnim();
        mAnimator = ObjectAnimator.ofInt(mClipDrawable, "level", 0, 50000);
        mAnimator.setDuration(mAnimDuration);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
        if (mIsAutoStart) {
            mAnimator.start();
        }
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mClipDrawable.setLevel(mProgress * 100);
        postInvalidate();
    }

    private void stopAnim() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    public void setMaskAnimDuration(long duration) {
        mAnimDuration = duration;
        initAnim();
    }

    public void startMaskAnim() {
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

}
