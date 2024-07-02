package io.github.hmzi67.securezone.Widgets;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import io.github.hmzi67.securezone.Activities.MainActivity;
import io.github.hmzi67.securezone.R;

public class CustomGestureListener extends GestureDetector.SimpleOnGestureListener {
    MediaPlayer mediaPlayer;
    private SharedPreferences pref;
    private boolean isPlaying = false;

    public CustomGestureListener(Context context) {
        this.pref = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        this.mediaPlayer = MediaPlayer.create(context, R.raw.alarm); // Replace with your actual audio file
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (isPlaying && pref.getBoolean("NS", false)) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync(); // Prepare the MediaPlayer again to allow for restarting if needed
            isPlaying = false;
        } else {
            mediaPlayer.start();
            isPlaying = true;
        }
    }
}

