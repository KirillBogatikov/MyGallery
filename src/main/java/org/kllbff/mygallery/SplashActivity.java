package org.kllbff.mygallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.kllbff.mygallery.org.kllbff.mygallery.animations.HideAnimation;
import org.kllbff.mygallery.org.kllbff.mygallery.animations.ShowAnimation;

import static com.vk.sdk.api.VKError.VK_CANCELED;

public class SplashActivity extends AppCompatActivity implements VKCallback<VKAccessToken>, DialogInterface.OnClickListener {
    private Animation show, hide;
    private View loginZone, loadingZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MyGalleryApplication.currentActivity = this;

        loginZone = findViewById(R.id.login_zone);
        loadingZone = findViewById(R.id.loading_zone);

        if(VKSdk.isLoggedIn()) {
            //preload
        } else {
            loginZone.startAnimation(new ShowAnimation(loginZone));
            loadingZone.startAnimation(new HideAnimation(loadingZone));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, this)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResult(VKAccessToken res){
        loadingZone.startAnimation(new ShowAnimation(loadingZone));
        loginZone.startAnimation(new HideAnimation(loginZone));
    }

    public void onLoginButtonClick(View view) {
        VKSdk.login(this, "photos");
    }

    public void onExitButtonClick(View view) { finish(); }

    private void showErrorDialog(String vkMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.label_login_error);
        dialogBuilder.setMessage(getString(R.string.text_login_error) + "\n" + vkMessage);
        dialogBuilder.setNegativeButton(R.string.label_exit, this);
        dialogBuilder.setPositiveButton(R.string.label_try_again, this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create().show();
    }

    @Override
    public void onError(VKError error){
        if(error.errorCode != VK_CANCELED) {
            showErrorDialog(error.errorMessage);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_NEGATIVE: finish(); break;
            case DialogInterface.BUTTON_POSITIVE: onLoginButtonClick(null); break;
        }
    }
}

