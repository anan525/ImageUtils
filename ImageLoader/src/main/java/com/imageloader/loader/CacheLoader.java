package com.imageloader.loader;

import android.graphics.Bitmap;
import android.util.Log;

import com.imageloader.RealChain;
import com.imageloader.Utils.StringUtils;
import com.imageloader.caches.CacheManager;

import java.io.IOException;

public class CacheLoader implements ILoader {

    private static final String TAG = "CacheLoader";

    @Override
    public Bitmap load(RealChain realChain) throws IOException {
        String url = realChain.getRequest().getUrl();

        String md5 = StringUtils.getMd5(url);
        //从活动中找
        Bitmap bpFromActive = CacheManager.getInstance().getBPFromActive(md5);
        if (bpFromActive != null) {
            Log.d(TAG, "find in Active");
            return bpFromActive;
        }
        //从内存缓存中找
        Bitmap proceed = realChain.proceed(realChain.getRequest());
        if (proceed != null) {
            CacheManager.getInstance().saveBpToMomory(md5, proceed);
        }

        return proceed;
    }
}
