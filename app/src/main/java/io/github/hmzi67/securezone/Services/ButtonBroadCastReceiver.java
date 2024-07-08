package io.github.hmzi67.securezone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import io.github.hmzi67.securezone.R;

public class ButtonBroadCastReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private SharedPreferences pref;
    private boolean isPlaying = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Initialize MediaPlayer only once
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
            pref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        }

        // receiving volume changes
        if (intent.getAction() != null && intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
            int volumeType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1);
            int volumeDirection = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", -1);
            int newVolume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);

            if (volumeType == AudioManager.STREAM_MUSIC) {
                if (newVolume > volumeDirection) {
                    // volume up
                } else if (newVolume < volumeDirection) {
                    if (pref.getBoolean("NS", false)) {
                        if (isPlaying) {
                            mediaPlayer.pause();
                            isPlaying = false;
                        } else {
                            mediaPlayer.start();
                            isPlaying = true;
                        }
                    }
                }
            }
        }
    }
}
