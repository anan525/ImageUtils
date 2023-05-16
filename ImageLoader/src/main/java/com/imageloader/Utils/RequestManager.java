package com.imageloader.Utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.imageloader.Dispatcher;
import com.imageloader.Request;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class RequestManager {

    private static final String TAG = "LoadUtils";

    private volatile static RequestManager instance;

    private Request.Builder builder;

    private final Dispatcher dispatcher;

    public static RequestManager getInstance() {
        if (instance == null) {
            synchronized (RequestManager.class) {
                if (instance == null) {
                    instance = new RequestManager();
                }
            }
        }
        return instance;
    }

    private RequestManager() {
        dispatcher = new Dispatcher();
    }

    /**
     * @param url
     * @return
     */
    public RequestManager load(String url) {
        builder = new Request.Builder()
                .load(url);
        return this;
    }

    public void into(ImageView imageView) {
        dispatcher.enque(imageView, builder.build());
    }
}
