package org.kllbff.mygallery.photos;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.kllbff.mygallery.GalleryActivity;
import org.kllbff.mygallery.SplashActivity;

public class LoadingThread extends Thread {
    private static Handler instance;

    public static Handler getHandler() {
        if(instance == null) {
            new LoadingThread().start();
        }
        while(instance == null) {}
        return instance;
    }

    private LoadingThread() {}

    public void run() {
        Looper.prepare();
        instance = new Handler();
        Looper.loop();
    }
}
