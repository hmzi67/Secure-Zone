package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import io.github.hmzi67.securezone.Modals.FakeCallModel;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.ActivityEditContactBinding;

public class EditContactActivity extends AppCompatActivity {

    private ActivityEditContactBinding binding;
    private String id;
    private String userPhoto;
    private String userName;
    private String userNumber;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    String downloadURL;
    private ProgressStatus progressStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        // ready the firebase
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = storage.getReference();

        // goBack
        binding.goBack.setOnClickListener(view -> onBackPressed());

        // getting the data from intent.
        id = getIntent().getStringExtra("id");
        userPhoto = getIntent().getStringExtra("userphoto");
        userName = getIntent().getStringExtra("username");
        userNumber = getIntent().getStringExtra("usernumber");

        // setting the data.
        binding.userName.setText(userName);
        binding.userPhoneNumber.setText(userNumber);
        if (!userPhoto.isEmpty())
            Picasso.get().load(userPhoto).into(binding.userAvatar);
        else
            Picasso.get().load(R.drawable.ic_avatar_placeholder).into(binding.userAvatar);
        // select image
        binding.userAvatar.setOnClickListener(view -> selectImage());
        // update contact
        binding.saveContact.setOnClickListener(view -> updateContact());
        // delete contact
        binding.deleteContact.setOnClickListener(view -> deleteContact());
    }

    // set the contact image
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    // on activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.userAvatar.setImageBitmap(bitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // on contact deletion
    private void deleteContact() {
        progressStatus = new ProgressStatus(this);
        progressStatus.setTitle("Deleting");
        progressStatus.show();
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Contacts").child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressStatus.dismiss();
                Toast.makeText(this, "Contact deleted successfully", Toast.LENGTH_SHORT).show();
                resetUI();
            }
        });
    }

    // on contact update
    private void updateContact() {
        progressStatus = new ProgressStatus(this);
        progressStatus.setTitle("Updating");
        progressStatus.show();


        if (filePath != null) {
            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid() + "/" + userName);
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadURL = uri.toString();
                    FakeCallModel contact = new FakeCallModel(id, downloadURL, binding.userName.getText().toString(), binding.userPhoneNumber.getText().toString());
                    firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Contacts").child(id).setValue(contact).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressStatus.dismiss();
                            Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                            resetUI();
                        }
                    });
                });
            });
        } else {
            FakeCallModel contact = new FakeCallModel(id, userPhoto, binding.userName.getText().toString(), binding.userPhoneNumber.getText().toString());
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Contacts").child(id).setValue(contact).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressStatus.dismiss();
                    Toast.makeText(this, "Contact updated successfully", Toast.LENGTH_SHORT).show();
                    resetUI();
                }
            });
        }
    }

    // after successful transaction(contact update or delete) reset the fields.
    private void resetUI() {
        binding.userName.setText("");
        binding.userPhoneNumber.setText("");
        binding.userAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
    }
}