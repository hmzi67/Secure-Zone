package io.github.hmzi67.securezone.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.github.hmzi67.securezone.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }
    private void init() {
        // goBack
        binding.goBack.setOnClickListener(v -> onBackPressed());

        // setting up the local storage settings.
        pref = getSharedPreferences("Settings", MODE_PRIVATE);

        // Updating the settings
        binding.videoCapturing.setOnClickListener(view -> updateSettings("VC", binding.videoCapturing.isChecked()));
        binding.imageCapturing.setOnClickListener(view -> updateSettings("IC", binding.imageCapturing.isChecked()));
        binding.liveAlert.setOnClickListener(view -> updateSettings("LA", binding.liveAlert.isChecked()));
        binding.noisySound.setOnClickListener(view -> {
            updateSettings("NS", binding.noisySound.isChecked());
        });

        binding.audioRecording.setOnClickListener(view -> updateSettings("AR", binding.audioRecording.isChecked()));
        binding.aiAssistance.setOnClickListener(view -> updateSettings("AI", binding.aiAssistance.isChecked()));

        updateUI();
    }

    // update the settings toggle buttons on User Interface
    private void updateUI() {
        binding.videoCapturing.setChecked(pref.getBoolean("VC", false));
        binding.imageCapturing.setChecked(pref.getBoolean("IC", false));
        binding.liveAlert.setChecked(pref.getBoolean("LA", false));
        binding.noisySound.setChecked(pref.getBoolean("NS", false));
        binding.audioRecording.setChecked(pref.getBoolean("AR", false));
        binding.aiAssistance.setChecked(pref.getBoolean("AI", false));
    }

    private void updateSettings(String key, boolean value) {
        pref = getSharedPreferences("Settings", MODE_PRIVATE);
        editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}