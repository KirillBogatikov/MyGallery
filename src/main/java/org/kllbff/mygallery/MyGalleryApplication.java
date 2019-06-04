package org.kllbff.mygallery;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class MyGalleryApplication extends Application {
    public static Activity currentActivity;
    private VKAccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate( ) {
        super.onCreate();
        accessTokenTracker = new VKAccessTokenTracker() {
            public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
                if(newToken == null && currentActivity != null) {
                    currentActivity.finish();
                }
            }
        };
        accessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
