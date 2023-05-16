package com.imageloader.loader;

import android.graphics.Bitmap;

import com.imageloader.RealChain;
import com.imageloader.caches.BitmapUsePool;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class NetworkLoader implements ILoader {

    public Bitmap load(RealChain realChain) throws IOException {
        String url = realChain.getRequest().getUrl();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .build();
        okhttp3.Request okhttpRequest = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = okHttpClient.newCall(okhttpRequest).execute();
        //防止2 次decodeStream返回空bitmap
        byte[] bytes = response.body().bytes();

        return BitmapUsePool.getInstance().getBpWithByte(bytes);
    }

}
