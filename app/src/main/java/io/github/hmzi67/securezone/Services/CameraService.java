package io.github.hmzi67.securezone.Services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.Objects;

public class CameraService extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "Type : " + getIntent().getStringExtra("Type"), Toast.LENGTH_SHORT).show();

        if (Objects.equals(getIntent().getStringExtra("Type"), "video")) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if(Objects.equals(getIntent().getStringExtra("Type"), "image")) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(cameraIntent);
            finish();
        }

    }
}



