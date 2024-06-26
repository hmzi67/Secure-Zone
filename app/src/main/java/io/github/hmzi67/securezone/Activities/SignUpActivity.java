package io.github.hmzi67.securezone.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    String downloadURL;

    private ProgressStatus progressStatus;

    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        // ready the firebase
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = storage.getReference();

        // go back
        binding.goBack.setOnClickListener(view -> onBackPressed());

        // on signin link
        binding.loginLink.setOnClickListener(view -> onBackPressed());

        // upload user profile image
         binding.changeAvatar.setOnClickListener(view -> selectImage());

         // Signup button clicked.
        binding.signupButton.setOnClickListener(view -> signUp());

        // continue with google
        binding.googleSignin.setOnClickListener(view -> {
            continueGoogle();
        });
    }

    private void continueGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(SignUpActivity.this, options);

        Intent intent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                progressStatus = new ProgressStatus(SignUpActivity.this);
                progressStatus.setTitle("Creating Account");
                progressStatus.show();
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                    firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String phoneNumber = firebaseAuth.getCurrentUser().getPhoneNumber() == null ? "" : firebaseAuth.getCurrentUser().getPhoneNumber();
                            Users user = new Users(firebaseAuth.getCurrentUser().getDisplayName(), firebaseAuth.getCurrentUser().getEmail(), phoneNumber, firebaseAuth.getCurrentUser().getPhotoUrl().toString(), "", "", "", "");
                            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").setValue(user).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    progressStatus.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Exception Occur", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();

            long fileSize = getFileSize(filePath);
            if (fileSize != -1 && fileSize <= (1024 * 1024)) {
                try {
                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    binding.changeAvatar.setImageBitmap(bitmap);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "File size exceeds 1 MB limit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long getFileSize(Uri uri) {
        // Resolve the file path from URI
        String filePath = getPath(this, uri);
        if (filePath != null) {
            // Create File object from path
            File file = new File(filePath);
            // Return the file size in bytes
            return file.length();
        } else {
            return -1;
        }
    }

    public static String getPath(Context context, Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private void signUp() {
        String userName = binding.username.getText().toString();
        String userEmail = binding.userEmail.getText().toString();
        String userPhoneNumber = binding.userPhone.getText().toString();
        String userPassword = binding.userPassword.getText().toString();
        String userPasswordConfirm = binding.userConfirmPassword.getText().toString();

        if (userName.isEmpty() || userEmail.isEmpty() || userPhoneNumber.isEmpty() || userPassword.isEmpty() || userPasswordConfirm.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Fill the form first.", Toast.LENGTH_SHORT).show();
        } else {
            if (isValid(userEmail)) {
                if (userPhoneNumber.length() < 11) {
                    Toast.makeText(SignUpActivity.this, "Please use the valid phone number.", Toast.LENGTH_SHORT).show();
                }
                if (!userPassword.matches(userPasswordConfirm)) {
                    Toast.makeText(SignUpActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                }

                progressStatus = new ProgressStatus(SignUpActivity.this);
                progressStatus.setTitle("Creating Account");
                progressStatus.setCanceledOnTouchOutside(false);

                progressStatus.show();
                // uploadImage();

                firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       uploadImage(userName, userEmail, userPhoneNumber);
//                       SharedPreferences pref = getSharedPreferences("ProfileDetails", MODE_PRIVATE);
//                       String profileImage = pref.getString("url", null);
//                       Users user = new Users(userName, userEmail, userPhoneNumber, "", "", "", "", "");
//                       user.setUserProfileImg(downloadURL);


//                       firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").setValue(user).addOnCompleteListener(task1 -> {
//                           if (task1.isSuccessful()) {
//                               progressStatus.dismiss();
//                               firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
//                                   if (task2.isSuccessful()) {
//                                       Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
//                                       startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
//                                       finish();
//                                   } else {
//                                       Toast.makeText(this, "Exception Occur", Toast.LENGTH_SHORT).show();
//                                   }
//                               });
//                           } else {
//                               Toast.makeText(this, "Exception Occur", Toast.LENGTH_SHORT).show();
//                           }
//                       });
                   }
                });

            } else {
                Toast.makeText(SignUpActivity.this, "Invalid Email! Please use valid email.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImage(String userName, String userEmail, String userPhone) {
        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid().toString());

            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadURL = uri.toString();

                Log.d("sdf", downloadURL);
                Users user = new Users(userName, userEmail, userPhone, uri.toString(), "", "", "", "");
                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").setValue(user).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        progressStatus.dismiss();
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Exception Occur", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Exception Occur", Toast.LENGTH_SHORT).show();
                    }
                });
//                SharedPreferences pref = getSharedPreferences("ProfileDetails", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("url", uri.toString());
//                editor.apply();
            })).addOnFailureListener(e -> {
                // Error, Image not uploaded
                Toast.makeText(SignUpActivity.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Users user = new Users(userName, userEmail, userPhone, "", "", "", "", "");
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").setValue(user).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    progressStatus.dismiss();
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Exception Occur", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Exception Occur", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean isValid(String email) {
        return email.matches(emailPattern);
    }
}