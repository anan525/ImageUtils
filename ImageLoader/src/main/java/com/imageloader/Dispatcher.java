package com.imageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Dispatcher {

    private Handler handler = new Handler(Looper.getMainLooper());

    private ThreadPoolExecutor threadPoolExecutor;

    public ThreadPoolExecutor getExecuteService() {
        if (threadPoolExecutor == null) {
            threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
        }
        return threadPoolExecutor;
    }


    public Dispatcher() {
    }

    public void enque(ImageView imageView, Request request) {
        getExecuteService().execute(() -> {
            Bitmap bitmap = request.request();
            handler.post(() -> {
                imageView.setImageBitmap(bitmap);
            });
        });
    }
}
