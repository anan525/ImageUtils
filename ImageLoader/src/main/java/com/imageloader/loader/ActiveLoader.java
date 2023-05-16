package com.imageloader.loader;

import android.graphics.Bitmap;
import android.util.Log;

import com.imageloader.RealChain;
import com.imageloader.Utils.StringUtils;
import com.imageloader.caches.CacheManager;

import java.io.IOException;

public class ActiveLoader implements ILoader {
    private static final String TAG = "ActiveLoader";

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

        Bitmap proceed = realChain.proceed(realChain.getRequest());
        if (proceed != null) {
            //缓存到这里
            CacheManager.getInstance().saveBpToActive(md5, proceed);
        }

        return proceed;
    }
}
