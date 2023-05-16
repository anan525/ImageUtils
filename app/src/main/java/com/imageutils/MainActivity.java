package com.imageutils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.imageloader.Utils.LoadUtils;
import com.imageloader.Utils.StringUtils;
import com.imageloader.caches.CacheManager;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv_bg = findViewById(R.id.iv_bg);


        findViewById(R.id.button).setOnClickListener(l -> {
            LoadUtils.with(this)
                    .load("https://img.zhisheji.com/bbs/forum/201401/05/153945tbr7pg5torfzptso.jpg")
                    .into(iv_bg);
        });

        findViewById(R.id.button2).setOnClickListener(l -> {
            CacheManager.getInstance().removeLruCache(StringUtils.getMd5("https://img.zhisheji.com/bbs/forum/201401/05/153945tbr7pg5torfzptso.jpg"));
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return super.onTouchEvent(event);
    }
}