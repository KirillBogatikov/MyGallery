package org.kllbff.mygallery;

import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

public class NetworkThread extends Thread {
    private static NetworkThread instance;

    public static NetworkThread getInstance() {
        if(instance == null) {
            instance = new NetworkThread();
            instance.start();
            while(instance.handler == null) {
                //wait
            }
        }
        return instance;
    }

    private NetworkThread() {}

    private Handler handler;

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        Looper.loop();
    }

    public Handler getHandler() {
        return handler;
    }

    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
