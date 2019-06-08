package org.kllbff.mygallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKScopes;

import org.kllbff.mygallery.animations.HideAnimation;
import org.kllbff.mygallery.animations.ShowAnimation;
import org.kllbff.mygallery.photos.VkPhotos;

public class SplashActivity extends AppCompatActivity implements VKCallback<VKAccessToken> {
    private TextView messageView;
    private Button vkLoginButton, tryConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        MyGalleryApplication.currentActivity = this;

        messageView = findViewById(R.id.message);
        vkLoginButton = findViewById(R.id.vk_login);
        tryConnectButton = findViewById(R.id.try_connect);

        startPreparation();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.vk_login:
                VKSdk.login(this, VKScopes.PHOTOS);
            break;
            case R.id.try_connect:
                tryConnectButton.startAnimation(new HideAnimation(tryConnectButton));
                startPreparation();
            break;
        }
    }

    public void startPreparation() {
        if(!checkInternet()) {
            Log.i("Preparing", "No connection to Internet");
            messageView.setText(R.string.label_no_connection);
            tryConnectButton.startAnimation(new ShowAnimation(tryConnectButton));
        } else if(!VKSdk.isLoggedIn()) {
            Log.i("Preparing", "Unauthorized user");
            messageView.setText(R.string.label_need_login);
            vkLoginButton.startAnimation(new ShowAnimation(vkLoginButton));
        } else {
            Log.i("Preparing", "Success");
            loadAlbums();
        }
    }

    private void loadAlbums() {
        NetworkThread.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                VkPhotos photos = VkPhotos.getInstance();
                try {
                    if(!photos.isAlbumsLoaded()) {
                        photos.loadAlbumsList();
                        Thread.sleep(1000);
                    }
                    startActivityForResult(new Intent(SplashActivity.this, MainActivity.class), 0x15);
                } catch(Exception e) {
                    Log.e("Preparing", "Failed to load albums list", e);
                }
            }
        });
    }

    private boolean checkInternet() {
        ConnectivityManager connectivityManager = ((ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE));
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        return network != null && network.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, this)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if(resultCode == -2) {
            finish();
        } else if(resultCode == -3) {
                NetworkThread.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            VkPhotos.getInstance().downloadAlbumPhotos();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("AlbumLoaded", true);
                            Thread.sleep(1000);
                            startActivityForResult(intent, 0x15);
                        } catch (Exception e) {
                            Log.e("Preparing", "Failed to load album photos", e);
                        }
                    }
                });

        } else {
            startPreparation();
        }
    }

    @Override
    public void onResult(VKAccessToken res){
        vkLoginButton.startAnimation(new HideAnimation(vkLoginButton));
        messageView.setText(R.string.label_preparing);
    }

    @Override
    public void onError(VKError error){
        if(error.errorCode != VKError.VK_CANCELED) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.label_login_error);
            dialogBuilder.setMessage(getString(R.string.text_login_error) + "\n" + error.errorMessage);
            dialogBuilder.setNegativeButton(R.string.label_exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SplashActivity.this.finish();
                }
            });
            dialogBuilder.setCancelable(false);
            dialogBuilder.create().show();
        }
    }
}
