package io.github.hmzi67.securezone.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import io.github.hmzi67.securezone.Activities.SignUpActivity;
import io.github.hmzi67.securezone.Modals.FakeCallModel;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.FragmentAddContactBinding;


public class AddContactFragment extends Fragment {

    private FragmentAddContactBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    String downloadURL;
    private ProgressStatus progressStatus;


    public AddContactFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddContactBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        // ready the firebase
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = storage.getReference();

        // pick user image
        binding.changeAvatar.setOnClickListener(view -> selectImage());

        // add contact
        binding.saveContact.setOnClickListener(view -> saveContact());

    }


    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                binding.changeAvatar.setImageBitmap(bitmap);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveContact() {
        String userName = binding.userName.getText().toString();
        String userNumber = binding.userPhoneNumber.getText().toString();
        progressStatus = new ProgressStatus(getContext());
        progressStatus.setTitle("Creating Contact");

        if (!userName.isEmpty() && !userNumber.isEmpty() && !filePath.equals(null)) {
            progressStatus.show();

            StorageReference ref = storageReference.child("images/" + firebaseAuth.getCurrentUser().getUid() + "/" + userName);
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadURL = uri.toString();
                    FakeCallModel contact = new FakeCallModel("", downloadURL, userName, userNumber);
                    firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Contacts").push().setValue(contact).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressStatus.dismiss();
                            Toast.makeText(getContext(), "Contact added successfully", Toast.LENGTH_SHORT).show();
                            resetPage();
                        }
                    });
                });
            });
        } else if (!userName.isEmpty() && !userNumber.isEmpty()) {
            progressStatus.show();
            downloadURL = "";
            FakeCallModel contact = new FakeCallModel("", downloadURL, userName, userNumber);
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Contacts").push().setValue(contact).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressStatus.dismiss();
                    Toast.makeText(getContext(), "Contact added successfully", Toast.LENGTH_SHORT).show();
                    resetPage();
                }
            });
        } else {
            Toast.makeText(getContext(), "First fill the details", Toast.LENGTH_SHORT).show();
        }

    }

    private void resetPage() {
        binding.userName.setText("");
        binding.userPhoneNumber.setText("");
        binding.changeAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
    }
}