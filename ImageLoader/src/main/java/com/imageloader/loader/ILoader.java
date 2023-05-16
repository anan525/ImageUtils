package com.imageloader.loader;

import android.graphics.Bitmap;

import com.imageloader.RealChain;
import com.imageloader.Request;

import java.io.IOException;

public interface ILoader {

    Bitmap load(RealChain realChain)  throws IOException;
}
