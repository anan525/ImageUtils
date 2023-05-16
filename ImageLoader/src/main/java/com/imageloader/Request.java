package com.imageloader;

import android.graphics.Bitmap;

import com.imageloader.loader.ActiveLoader;
import com.imageloader.loader.CacheLoader;
import com.imageloader.loader.DiskCacheLoader;
import com.imageloader.loader.ILoader;
import com.imageloader.loader.NetworkLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Request {

    private static final String TAG = "Request";
    private List<ILoader> loaders;

    private ImageLoaderCallBack imageLoaderCallBack;

    private String url;

    public Request(Builder builder) {
        url = builder.url;
        loaders = new ArrayList<>();
        loaders.add(new ActiveLoader());
        loaders.add(new CacheLoader());
        loaders.add(new DiskCacheLoader());
        loaders.add(new NetworkLoader());
    }

    public String getUrl() {
        return url;
    }

    public Bitmap request() {
        return getBitmapFromChain();
    }

    private Bitmap getBitmapFromChain() {
        Bitmap bitmap = null;
        if (loaders != null && loaders.size() > 0) {
            try {
                RealChain realChain = new RealChain(0, loaders, this);
                bitmap = realChain.proceed(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public static class Builder {

        private String url;

        public Builder() {
        }

        public Builder load(String url) {
            this.url = url;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}
