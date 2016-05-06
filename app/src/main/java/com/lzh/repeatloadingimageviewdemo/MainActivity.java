package com.lzh.repeatloadingimageviewdemo;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv = (ImageView) findViewById(R.id.iv_line_anim);
        AnimationDrawable drawable = (AnimationDrawable) iv.getDrawable();
        drawable.start();
    }
}
