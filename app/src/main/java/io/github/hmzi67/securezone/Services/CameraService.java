package io.github.hmzi67.securezone.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class CameraService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Start Camera", Toast.LENGTH_SHORT).show();
        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(cameraIntent);
        stopSelf(); // Stop the service once the camera intent is fired
        return START_NOT_STICKY;
    }
}

