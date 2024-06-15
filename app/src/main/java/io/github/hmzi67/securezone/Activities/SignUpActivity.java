package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    String downloadURL;

    private ProgressStatus progressStatus;
    private String userImagePath;


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
        binding.loginLink.setOnClickListener(view -> {
            onBackPressed();
        });

        // upload user profile image
         binding.changeAvatar.setOnClickListener(view -> {
             selectImage();
         });

         // Signup button clicked.
        binding.signupButton.setOnClickListener(view -> {
//            uploadImage();
            signUp();
        });
    }

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
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.changeAvatar.setImageBitmap(bitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid().toString());

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadURL = uri.toString();

                            Log.d("sdf", downloadURL);
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
                    Toast.makeText(SignUpActivity.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                       uploadImage();
                       SharedPreferences pref = getSharedPreferences("ProfileDetails", MODE_PRIVATE);
                       String profileImage = pref.getString("url", null);
                       Users user = new Users(userName, userEmail, userPhoneNumber, downloadURL, "", "", "", "");


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
                });

            } else {
                Toast.makeText(SignUpActivity.this, "Invalid Email! Please use valid email.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValid(String email) {
        return email.matches(emailPattern);
    }
}