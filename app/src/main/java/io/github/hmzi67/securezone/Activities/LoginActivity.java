package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.ActivityLoginBinding;
import io.github.hmzi67.securezone.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private String emailPattern = "[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+";
    private ProgressStatus progressStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // create account link
        binding.createAccountLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        // ready Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // reset password
        binding.resetPassword.setOnClickListener(view -> {
            String email = binding.userEmail.getText().toString();
            if(isValid(email)) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                   if (task.isSuccessful()) Toast.makeText(LoginActivity.this, "Check your email for reset link", Toast.LENGTH_SHORT).show();
                   else Toast.makeText(LoginActivity.this, "Some thing went wrong.", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(LoginActivity.this, "Please Enter the valid email", Toast.LENGTH_SHORT).show();
            }
        });

        // ready the progress dialog
        progressStatus = new ProgressStatus(LoginActivity.this);
        progressStatus.setCanceledOnTouchOutside(false);
        progressStatus.setTitle("Authenticating...");


        // perform login
        binding.loginButton.setOnClickListener(view -> {
            loginUser();
        });
    }

    private void loginUser() {
        String userEmail = binding.userEmail.getText().toString();
        String userPassword = binding.signinUserPassword.getText().toString();

        if (isValid(userEmail)) {
            if (userPassword.length() < 6) {
                Toast.makeText(this, "Password is smaller than 6 digits", Toast.LENGTH_SHORT).show();
            } else {
                progressStatus.show();
                firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressStatus.dismiss();
                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this,"Unverified Email. Verify your email first!",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressStatus.dismiss();
                        Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Invalid Email! Please enter valid email", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValid(String email) {
        return true; //email.matches(emailPattern);
    }
}