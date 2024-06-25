package io.github.hmzi67.securezone.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityInCommingCallBinding;

public class InCommingCallActivity extends AppCompatActivity {
    private ActivityInCommingCallBinding binding;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInCommingCallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        // getting intent data
        binding.name.setText(getIntent().getStringExtra("username"));

        // ready the media player
        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // accept on click listener
        binding.btnAnswer.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            else {
                mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }

        });

        // decline on click listener
        binding.btnDecline.setOnClickListener(view -> {
            mediaPlayer.stop();
            onBackPressed();

        });
    }
}