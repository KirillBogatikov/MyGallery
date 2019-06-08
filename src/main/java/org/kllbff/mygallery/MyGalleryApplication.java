package org.kllbff.mygallery;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

/**
 * Приложение MyGallery
 * <p>Использование собственной реализации метода {@link #onCreate()} необходимо для правильной инициализации VkApi SDK</p>
 */
public class MyGalleryApplication extends Application {
    public static Activity currentActivity;
    private VKAccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate( ) {
        super.onCreate();
        accessTokenTracker = new VKAccessTokenTracker() {
            public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
                if(newToken == null && currentActivity != null) {
                    /*
                     токен может быть отозван пользователем (в настройках аккаунта можно удалить
                     подключенное к аккаунту приложение) и системой vk.com (смена пароля пользователя)
                     В этом случае необходимо завершить текущую активити
                     */
                    currentActivity.setResult(-2);
                    currentActivity.finish();
                }
            }
        };
        accessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }
}
