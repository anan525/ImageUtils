package com.imageloader.loader;

import android.graphics.Bitmap;

import com.imageloader.RealChain;
import com.imageloader.Request;
import com.imageloader.Utils.StringUtils;
import com.imageloader.caches.BitmapUsePool;
import com.imageloader.caches.CacheManager;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;

public class DiskCacheLoader implements ILoader {

    @Override
    public Bitmap load(RealChain realChain) throws IOException {
        Request request = realChain.getRequest();
        String url = request.getUrl();
        String md5 = StringUtils.getMd5(url);
        //从文件缓存中找
        DiskLruCache.Snapshot bpFromLocal = CacheManager.getInstance().getBPFromLocal(md5);
        if (bpFromLocal != null) {
            InputStream inputStream = bpFromLocal.getInputStream(0);
            if (inputStream != null) {
                Bitmap bpWithStream = BitmapUsePool.getInstance().getBpWithStream(inputStream);
                if (bpWithStream != null) {
                    return bpWithStream;
                }
            }
        }
        Bitmap proceed = realChain.proceed(request);

        if (proceed != null) {

            CacheManager.getInstance().saveBpToLocal(StringUtils.getMd5(url), proceed);

        }
        return proceed;

    }
}
