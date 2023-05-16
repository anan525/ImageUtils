package com.imageloader.caches;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;

public class ActiveCache {

    private static final String TAG = "ActiveCache";
    private volatile static ActiveCache activeCache;

    private HashMap<String, MonitorRefrence> cacheMap;

    private ReferenceQueue queue;
    private final Thread thread;
    private boolean isThreadClosed = false;

    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            while (!isThreadClosed) {
                if (queue != null) {
                    try {
                        MonitorRefrence monitorRefrence = (MonitorRefrence) queue.remove();
                        if (cacheMap != null && !cacheMap.isEmpty()) {
                            MonitorRefrence removeValue = cacheMap.remove(monitorRefrence.getKey());
                            Bitmap bitmap = removeValue.get();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                bitmap.recycle();
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.d(TAG, "" + e.getMessage());

                    }
                }
            }
        }
    };

    public static ActiveCache getActiveCache() {
        if (activeCache == null) {
            synchronized (ActiveCache.class) {
                if (activeCache == null) {
                    activeCache = new ActiveCache();
                }
            }
        }
        return activeCache;
    }

    private ActiveCache() {
        cacheMap = new HashMap<>();
        thread = new Thread(runnable);
    }


    public void closeThread() {
        isThreadClosed = true;
        thread.interrupt();
        try {
            thread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "thread closed error");
        }
    }

    public void put(String key, Bitmap value) {
        cacheMap.put(key, new MonitorRefrence(value, getRefrenceQueue(), key));
    }

    public Bitmap get(String key) {
        WeakReference<Bitmap> valueWeakReference = cacheMap.get(key);
        if (valueWeakReference != null) {
            return valueWeakReference.get();
        }
        return null;
    }

    public void remove(String key) {
        if (cacheMap.containsKey(key)) {
            MonitorRefrence remove = cacheMap.remove(key);
        }
    }

    static class MonitorRefrence extends WeakReference<Bitmap> {
        private String key;

        public String getKey() {
            return key;
        }

        public MonitorRefrence(Bitmap referent, ReferenceQueue<Bitmap> q, String key) {
            super(referent, q);
            this.key = key;
        }
    }

    public ReferenceQueue<Bitmap> getRefrenceQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<Bitmap>();
            thread.start();
        }
        return queue;
    }
}
