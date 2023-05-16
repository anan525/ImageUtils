package com.imageloader.caches;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class CacheManager {

    private static final String TAG = "CacheManager";
    private volatile static CacheManager instance;

    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }

    private static final int MAX_FILE_CACHE_SIZE = 1024 * 1024 * 60;

    private static final int MAX_CACHE_SIZE = 1024 * 1024 * 20;

    private DiskLruCache diskLruCache;

    private MemoryCache lruCache;

    private ActiveCache activeCache;

    private CacheManager() {

    }


    /**
     * 初始化.
     *
     * @param context
     */
    public CacheManager init(Context context) {
        try {
            if (diskLruCache == null) {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                int versionCode = packageInfo.versionCode;
                diskLruCache = DiskLruCache.open(context.getCacheDir(), versionCode, 1, MAX_FILE_CACHE_SIZE);
            }
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
        if (lruCache == null) {
            lruCache = new MemoryCache(MAX_CACHE_SIZE);
        }

        if (activeCache == null) {
            activeCache = ActiveCache.getActiveCache();
        }
        return instance;
    }


    public Bitmap getBPFromActive(String key) {
        return activeCache.get(key);
    }

    public Bitmap getBPFromCache(String key) {
        return lruCache.get(key);
    }

    public DiskLruCache.Snapshot getBPFromLocal(String key) throws IOException {
        return diskLruCache.get(key);
    }

    /**
     * 写入本地磁盘缓存.
     */
    public void saveBpToLocal(String key, Bitmap inputStream) {
        try {
            DiskLruCache.Editor edit = diskLruCache.edit(key);
            if (edit != null) {
                //这里直接将inputSteam保存到diskLrucache;
                int byteCount = inputStream.getByteCount();
                ByteBuffer allocate = ByteBuffer.allocate(byteCount);
                inputStream.copyPixelsToBuffer(allocate);
                byte[] array = allocate.array();
                OutputStream outputStream = edit.newOutputStream(0);
                outputStream.write(array);
                //这里不需要close,后面还需要使用
                outputStream.close();
                edit.commit();
                Log.d(TAG, "saveBpToLocal  success");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "saveBpToLocal  error  " + e.getMessage());
        }

    }


    /**
     * 写入活动缓存.
     */
    public void saveBpToActive(String key, Bitmap bitmap) {
        activeCache.put(key, bitmap);

    }

    /**
     * 写入内存缓存.
     */
    public void saveBpToMomory(String key, Bitmap bitmap) {
        lruCache.put(key, bitmap);

    }

    public void removeLruCache(String key) {
        lruCache.remove(key);
        activeCache.remove(key);
    }
}
