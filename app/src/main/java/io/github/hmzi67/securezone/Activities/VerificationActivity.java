package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.concurrent.TimeUnit;

import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityVerificationBinding;

public class VerificationActivity extends AppCompatActivity {
    private ActivityVerificationBinding binding;
    private String verificationCodeSentBySystem;
    private String phoneNo;
    private String userName;
    private String userEmail;
    private String userImagePath;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        // go back
        binding.goBack.setOnClickListener(view -> onBackPressed());

        //
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        phoneNo = getIntent().getStringExtra("phoneNo");
        userName = getIntent().getStringExtra("userName");
        userEmail = getIntent().getStringExtra("userEmail");
        userImagePath = getIntent().getStringExtra("userPhoto");

        sendVerificationCodeToUser(phoneNo);

        // set the otp box
        setOTPInputBox();

        //
        binding.verify.setOnClickListener(view -> {
            String code = String.valueOf(binding.otpBox1.getText()) +
                    binding.otpBox2.getText() +
                    binding.otpBox3.getText() +
                    binding.otpBox4.getText() +
                    binding.otpBox5.getText() +
                    binding.otpBox6.getText();
            verifyCode(code);
        });
    }

    private void setOTPInputBox() {
        binding.otpBox1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(editable!=null){
                    if(editable.length()==1)
                        binding.otpBox2.requestFocus();
                }
            }
        });
        binding.otpBox2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1)
                        binding.otpBox3.requestFocus();
                }
            }
        });
        binding.otpBox3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1)
                        binding.otpBox4.requestFocus();
                }
            }
        });
        binding.otpBox5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1)
                        binding.otpBox5.requestFocus();
                }
            }
        });
        binding.otpBox6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable!=null){
                    if(editable.length()==1)
                        binding.otpBox6.requestFocus();
                }
            }
        });
    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92" + phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );

//        PhoneAuthOptions options =
//                PhoneAuthOptions.newBuilder(firebaseAuth)
//                        .setPhoneNumber(phoneNo)       // Phone number to verify
//                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                        .setActivity(this)                 // (optional) Activity for callback binding
//                        // If no activity is passed, reCAPTCHA verification can not be used.
//                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
//                        .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeSentBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerificationActivity.this, "Error :" + e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeSentBySystem, code);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SharedPreferences pref = getSharedPreferences("ProfileDetails", MODE_PRIVATE);
                    Users user = new Users(userName, userEmail, phoneNo, pref.getString("url", null), "", "", "", "");
                    firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").setValue(user).addOnCompleteListener(task1 -> {
                       if (task1.isSuccessful()) {
                           uploadImage();
                           Toast.makeText(VerificationActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);
                       } else {
                           Toast.makeText(VerificationActivity.this, "Exception Occur", Toast.LENGTH_SHORT).show();
                       }
                    });
                } else {
                    Toast.makeText(VerificationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage() {
        if (userImagePath != null) {
            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid().toString());

            ref.putFile(Uri.parse(userImagePath)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // downloadURL = uri.toString();

                            // Log.d("sdf", downloadURL);
                            SharedPreferences pref = getSharedPreferences("ProfileDetails", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("url", uri.toString());
                            editor.apply();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    // Error, Image not uploaded
                    Toast.makeText(VerificationActivity.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}