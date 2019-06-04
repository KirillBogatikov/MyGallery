package org.kllbff.mygallery;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class MainActivity extends AppCompatActivity implements VKCallback<VKAccessToken>, DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(VKSdk.isLoggedIn()) {
            startActivity(new Intent(this, GalleryActivity.class));
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, this)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResult(VKAccessToken res){
        Toast.makeText(this, "Tocken received: " + res, Toast.LENGTH_LONG).show();
    }

    public void onLoginButtonClick(View view) {
        VKSdk.login(this, "photos");
    }

    private void showExitDialog(int id, String vkMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(id);
        dialogBuilder.setMessage(vkMessage);
        dialogBuilder.setNeutralButton(R.string.label_exit, this);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create().show();
    }

    @Override
    public void onError(VKError error){
        switch(error.errorCode) {
            case VKError.VK_CANCELED: showExitDialog(R.string.exit_message_cancelled, error.errorMessage); break;
            default: showExitDialog(R.string.exit_message_failed, error.errorMessage); break;
        }
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        finish();
    }
}

