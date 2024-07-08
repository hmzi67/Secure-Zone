package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String emailPattern = "[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+";
    private ProgressStatus progressStatus;

    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        // ready Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // create account link
        binding.createAccountLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });

        // reset password
        binding.resetPassword.setOnClickListener(view -> {
            String email = binding.userEmail.getText().toString();
            if(email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
            else if(isValid(email)) {
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

        // continue with google
        binding.googleSignin.setOnClickListener(view -> {
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, options);

            Intent intent = googleSignInClient.getSignInIntent();
            activityResultLauncher.launch(intent);
        });
    }

    // activity result for continue with google.
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                progressStatus = new ProgressStatus(LoginActivity.this);
                progressStatus.setTitle("Signing In");
                progressStatus.show();
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                    firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                        if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                            if (task.isSuccessful()) {
                                String phoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber() == null ? "" : firebaseAuth.getCurrentUser().getPhoneNumber();
                                Users user = new Users(firebaseAuth.getCurrentUser().getDisplayName(), firebaseAuth.getCurrentUser().getEmail(), phoneNumber, firebaseAuth.getCurrentUser().getPhotoUrl().toString(), "", "", "", "");
                                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        progressStatus.dismiss();
                                        Toast.makeText(LoginActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Exception Occur", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            if (task.isSuccessful()) {
                                progressStatus.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    // method for login user
    private void loginUser() {
        // getting email and password from user
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
//        if (email.matches(emailPattern))
//            return true; //email.matches(emailPattern);
//        return false;
        return email.matches(emailPattern);
    }
}