package io.github.hmzi67.securezone.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import io.github.hmzi67.securezone.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() { /* private initializer method */ }
}