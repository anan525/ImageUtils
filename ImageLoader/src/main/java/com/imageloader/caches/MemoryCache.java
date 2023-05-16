package com.imageloader.caches;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemoryCache extends LruCache<String, Bitmap> {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public MemoryCache(int maxSize) {
        super(maxSize);
    }


    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        //数据被删除了，这里应该加入到缓冲池中
        BitmapUsePool.getInstance().put(oldValue);
    }



}
