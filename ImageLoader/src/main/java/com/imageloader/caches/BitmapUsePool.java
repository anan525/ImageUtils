package com.imageloader.caches;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

public class BitmapUsePool extends LruCache<Integer, Bitmap> {

    private static final String TAG = "BitmapUsePool";

    private TreeMap<Integer, String> bpPool;
    //最大20M
    private static final int MAX_POOL_SIZE = 1024 * 1024 * 60;

    private volatile static BitmapUsePool instance;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    private BitmapUsePool(int maxSize) {
        super(maxSize);

        bpPool = new TreeMap<>();
    }

    public static BitmapUsePool getInstance() {
        if (instance == null) {
            synchronized (BitmapUsePool.class) {
                if (instance == null) {
                    instance = new BitmapUsePool(MAX_POOL_SIZE);
                }
            }
        }
        return instance;
    }


    /**
     * 更加size获取bpPool
     *
     * @param width
     * @param height
     * @param config
     * @return
     */
    public Bitmap get(int width, int height, Bitmap.Config config) {

        Integer sizeByConfigue = getSizeByConfigue(width, height, config);

        Integer integer = bpPool.ceilingKey(sizeByConfigue);

        if (integer != null) {

            Bitmap bitmap = get(integer);
            if (bitmap != null) {
                Log.d(TAG, "find a bitmap");
            }

            return bitmap;
        }
        Log.d(TAG, "not find a bitmap");
        return null;
    }


    /**
     * 加入数据.
     *
     * @param bitmap
     */
    public void put(Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            Log.d(TAG, "bitmap is not mutable");
            //return;
        }
        //单个大小最大10M
        Integer size = getSize(bitmap);
        if (size > MAX_POOL_SIZE) {
            Log.d(TAG, "bitmap is too large ");
            //太大了就直接recycle
            bitmap.recycle();
            return;
        }
        //保存
        bpPool.put(size, null);

        put(size, bitmap);
    }

    /**
     * 获取bitmap的size.
     *
     * @param bitmap
     * @return
     */
    private Integer getSize(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (config == Bitmap.Config.ARGB_8888) {
            return 4 * width * height;
        } else if (config == Bitmap.Config.ARGB_4444 || config == Bitmap.Config.RGB_565) {
            return 2 * width * height;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return width * height;
        }
        return 2 * width * height;
    }


    /**
     * 获取bitmap的size.
     *
     * @return
     */
    private Integer getSizeByConfigue(int width, int height, Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4 * width * height;
        } else if (config == Bitmap.Config.ARGB_4444 || config == Bitmap.Config.RGB_565) {
            return 2 * width * height;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return width * height;
        }
        return 2 * width * height;
    }

    /**
     * 根据服用池创建bitmap
     *
     * @param inputStream
     * @return
     */
    public Bitmap getBpWithByte(byte[] inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //TODO 获取bitmap
        BitmapFactory.decodeByteArray(inputStream, 0, inputStream.length, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        Bitmap.Config outConfig = Bitmap.Config.RGB_565;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            outConfig = options.outConfig;
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapUsePool.getInstance().get(outWidth, outHeight, outConfig);
        if (bitmap != null) {
            //使用bitmap创建新的bitmap
            options.inBitmap = bitmap;
            Bitmap realBitmap = BitmapFactory.decodeByteArray(inputStream, 0, inputStream.length, options);
            Log.d(TAG, "bitmap is 复用了 bpPool ");
            return realBitmap;
        } else {
            Bitmap realBitmap = BitmapFactory.decodeByteArray(inputStream, 0, inputStream.length, options);
            return realBitmap;
        }
    }


    /**
     * 根据服用池创建bitmap
     *
     * @param inputStream
     * @return
     */
    public Bitmap getBpWithStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = inputStream.read(bytes)) != -1) {
            byteArrayOutputStream.write(bytes, 0, len);
        }
        //这里不需要close,后面还需要使用
        inputStream.close();
        byteArrayOutputStream.close();
        byte[] bytes1 = byteArrayOutputStream.toByteArray();
        return getBpWithByte(bytes1);
    }
}
