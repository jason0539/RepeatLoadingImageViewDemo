package com.lzh.repeatloadingimageviewdemo.repeatLoadingImageView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

public class RepeatImageView extends ImageView {

    private Paint mImagePaint;
    private boolean mIsAutoStart = false;
    private ClipDrawable mClipDrawable;
    private ObjectAnimator mAnimator;
    private long mAnimDuration = 1000;
    private float mScaleX, mScaleY;
    private int mGravity = Gravity.LEFT;
    private int mOrientaion = ClipDrawable.HORIZONTAL;

    public RepeatImageView(Context context) {
        super(context);
        init();
    }

    public RepeatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RepeatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initPaint();
        initBitmap();
        initAnim();
    }

    private void initPaint() {
        if (mImagePaint == null) {
            mImagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mImagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        }
    }

    private void initBitmap() {
        Drawable drawable = getDrawable();
        Bitmap bgBmp = ((BitmapDrawable) drawable).getBitmap();
        Drawable mMaskDrawable = new BitmapDrawable(null, bgBmp);
        mClipDrawable = new ClipDrawable(mMaskDrawable, mGravity, mOrientaion);
    }

    private void initAnim() {
        stopAnim();
        mAnimator = ObjectAnimator.ofInt(mClipDrawable, "level", 0, 10000);
        mAnimator.setDuration(mAnimDuration);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                postInvalidate();
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animListener != null) {
                    animListener.onAnimEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        if (mIsAutoStart) {
            mAnimator.start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float[] f = new float[9];
        getImageMatrix().getValues(f);
        mScaleX = f[Matrix.MSCALE_X];
        mScaleY = f[Matrix.MSCALE_Y];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needSuperOnDraw) {
            super.onDraw(canvas);
        } else {
            canvas.save();
            canvas.scale(mScaleX, mScaleY);
            mClipDrawable.setBounds(getDrawable().getBounds());
            mClipDrawable.draw(canvas);
            canvas.restore();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        init();
        initBitmap();
        initAnim();
    }

    boolean needSuperOnDraw = false;

    public void setImageDrawableDontUseAnim(Drawable drawable) {
        super.setImageDrawable(drawable);
        needSuperOnDraw = true;
    }

    private void stopAnim() {
        if (mAnimator != null && (mAnimator.isRunning() || mAnimator.isStarted())) {
            mAnimator.cancel();
        }
    }

    public void setAnimDuration(long duration) {
        mAnimDuration = duration;
        initAnim();
    }

    public void startAnim() {
        if (mAnimator != null) {
            mAnimator.start();
        }
    }

    private AnimListener animListener;

    public void setAnimationListener(AnimListener listener) {
        animListener = listener;
    }

    public interface AnimListener {
        void onAnimEnd();
    }
}
