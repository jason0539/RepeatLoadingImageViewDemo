package com.lzh.repeatloadingimageviewdemo;

import com.lzh.repeatloadingimageviewdemo.repeatLoadingImageView.RepeatImageView;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RepeatImageView sloganIv = (RepeatImageView) findViewById(R.id.img2);
        final RepeatImageView lineIv = (RepeatImageView) findViewById(R.id.iv_line_anim);

        lineIv.setAnimationListener(new RepeatImageView.AnimListener() {
            @Override
            public void onAnimEnd() {
                Drawable drawable = getResources().getDrawable(R.drawable.animation);
                lineIv.setImageDrawableDontUseAnim(drawable);
                AnimationDrawable animationDrawable = (AnimationDrawable) lineIv.getDrawable();
                animationDrawable.start();
            }
        });
        sloganIv.setAnimationListener(new RepeatImageView.AnimListener() {
            @Override
            public void onAnimEnd() {
                lineIv.setAnimDuration(600);
                lineIv.startAnim();
            }
        });

        sloganIv.startAnim();
    }
}
