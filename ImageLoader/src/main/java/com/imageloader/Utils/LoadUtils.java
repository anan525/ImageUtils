package com.imageloader.Utils;

import android.content.Context;

import com.imageloader.caches.CacheManager;

public class LoadUtils {

    private static final String TAG = "LoadUtils";

    public static RequestManager with(Context context) {
        //初始化缓存
        CacheManager.getInstance().init(context);
        return RequestManager.getInstance();
    }
}
