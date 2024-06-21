package io.github.hmzi67.securezone.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ConfirmDialog;
import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Users user;
    private String purpose;

    private StorageReference storageReference;
    private FirebaseStorage storage;

    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    String downloadURL;

    private ProgressStatus progressStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init() {
        //ready the database
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = storage.getReference();
        progressStatus = new ProgressStatus(this);

        // goBack
        binding.goBack.setOnClickListener(view -> onBackPressed());

        // Date of Birth
        binding.dateOfBirth.setOnClickListener(view -> {
            @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, dayOfMonth) -> {
                binding.dateOfBirth.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, 2000, 0, 1);
            datePickerDialog.show();
        });

        // setting gender spinner
        String[] testArray = {"Male", "Female", "Not Preferred"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, testArray );
        binding.selectGender.setAdapter(spinnerArrayAdapter);

        // image update
        binding.changeAvatar.setOnClickListener(view -> selectImage());


        // update the profile
        binding.updateSettings.setOnClickListener(view -> uploadImage());
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
        progressStatus.setTitle("Updating Profile");
        progressStatus.show();
        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid().toString());

            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                downloadURL = uri.toString();

                Log.d("sdf", downloadURL);
                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Profile").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            user = snapshot.getValue(Users.class);
                            if (!binding.firstName.getText().toString().isEmpty() && !binding.lastName.getText().toString().isEmpty())
                                user.setUserName(binding.firstName.getText() + " " + binding.lastName.getText());

                            // app purpose radio buttons
                            purpose = "";
                            if (binding.workRadioButton.isChecked()) purpose = "Work";
                            if (binding.travelRadioButton.isChecked()) purpose = "Travel";
                            if (binding.schoolRadioButton.isChecked()) purpose = "School";
                            if (binding.familyRadioButton.isChecked()) purpose = "Family";
                            if (binding.peaceRadioButton.isChecked()) purpose = "Peace of Mind";

                            user.setUserProfileImg(downloadURL);
                            user.setUserCountry(binding.countryCode.getSelectedCountryName().toString());
                            user.setUserDateOfBirth(binding.dateOfBirth.getText().toString());
                            user.setUserGender(binding.selectGender.getSelectedItem().toString());
                            user.setUserPurposeOfUse(purpose.toString());

                            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Profile").setValue(user).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    progressStatus.dismiss();
                                    Toast.makeText(EditProfileActivity.this, "Successfully Updated your profile", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

            })).addOnFailureListener(e -> {
                // Error, Image not uploaded
                Toast.makeText(EditProfileActivity.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        user = snapshot.getValue(Users.class);
                        if (!binding.firstName.getText().toString().isEmpty() && !binding.lastName.getText().toString().isEmpty())
                            user.setUserName(binding.firstName.getText() + " " + binding.lastName.getText());

                        // app purpose radio buttons
                        purpose = "";
                        if (binding.workRadioButton.isChecked()) purpose = "Work";
                        if (binding.travelRadioButton.isChecked()) purpose = "Travel";
                        if (binding.schoolRadioButton.isChecked()) purpose = "School";
                        if (binding.familyRadioButton.isChecked()) purpose = "Family";
                        if (binding.peaceRadioButton.isChecked()) purpose = "Peace of Mind";

                        user.setUserCountry(binding.countryCode.getSelectedCountryName().toString());
                        user.setUserDateOfBirth(binding.dateOfBirth.getText().toString());
                        user.setUserGender(binding.selectGender.getSelectedItem().toString());
                        user.setUserPurposeOfUse(purpose.toString());

                        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("Profile").setValue(user).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressStatus.dismiss();
                                Toast.makeText(EditProfileActivity.this, "Successfully Updated your profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }
}