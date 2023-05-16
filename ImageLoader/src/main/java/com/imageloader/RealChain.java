package com.imageloader;

import android.graphics.Bitmap;

import com.imageloader.loader.ILoader;

import java.io.IOException;
import java.util.List;

public class RealChain {

    private int index;

    public Request getRequest() {
        return request;
    }

    private List<ILoader> iLoaderList;

    private Request request;

    public RealChain(int index, List<ILoader> iLoaderList, Request request) {
        this.index = index;
        this.iLoaderList = iLoaderList;
        this.request = request;
    }

    public Bitmap proceed(Request request) throws IOException {

        if (index > iLoaderList.size()) {
            throw new AssertionError();
        }

        ILoader iLoader = iLoaderList.get(index);

        RealChain realChain = new RealChain(index + 1, iLoaderList, request);

        return iLoader.load(realChain);
    }

}
