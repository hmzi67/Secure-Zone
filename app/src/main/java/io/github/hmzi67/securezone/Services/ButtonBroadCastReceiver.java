package io.github.hmzi67.securezone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class ButtonBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            int volumeType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
            int volumeDirection = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_DIRECTION", -1);

            if (volumeType == AudioManager.STREAM_MUSIC) {
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (volumeDirection == AudioManager.ADJUST_RAISE) {
                    // Volume up button pressed
                    Toast.makeText(context, "Volume up", Toast.LENGTH_SHORT).show();
                } else if (volumeDirection == AudioManager.ADJUST_LOWER) {
                    // Volume down button pressed
                    Toast.makeText(context, "Volume down", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
