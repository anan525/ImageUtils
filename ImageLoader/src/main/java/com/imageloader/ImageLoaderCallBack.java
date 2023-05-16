package com.imageloader;

import android.graphics.Bitmap;

public interface ImageLoaderCallBack {

    void loadSuccess(Bitmap bitmap);


    void loadError(int code, String msg);
}
