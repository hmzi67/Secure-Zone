package io.github.hmzi67.securezone.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityAiChatBinding;

public class AIChatActivity extends AppCompatActivity {
    private ActivityAiChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        // go back
        binding.goBack.setOnClickListener(view -> onBackPressed());


    }
}