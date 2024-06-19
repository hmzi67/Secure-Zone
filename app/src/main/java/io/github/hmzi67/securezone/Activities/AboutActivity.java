package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ConfirmDialog;
import io.github.hmzi67.securezone.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        init();
        setContentView(binding.getRoot());
    }

    private void init() {
        // go back
        binding.goBack.setOnClickListener(view -> onBackPressed());

        // ready the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // on edit profile
        binding.editProfile.setOnClickListener(view -> {
            // TODO
        });

        // on delete account
        binding.eduUserDelete.setOnClickListener(view -> {
            ConfirmDialog cd = new ConfirmDialog(AboutActivity.this);
            cd.setCanceledOnTouchOutside(false);
            cd.setDialog_headline("Confirm to Delete");
            cd.setDialog_body("Are you sure to delete your account and all data?");
            cd.setYes_btn_text("Delete");
            cd.setNo_btn_text("Cancel");

            cd.getYes_btn().setOnClickListener(view1 -> {
                firebaseAuth.getCurrentUser().delete();
                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).removeValue();
                startActivity(new Intent(AboutActivity.this, LoginActivity.class));
                finish();
                cd.dismiss();
            });

            cd.getNo_btn().setOnClickListener(view2 -> {
                cd.dismiss();
            });

            cd.show();
        });

        // on logout
        binding.eduUserLogout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(AboutActivity.this, LoginActivity.class));
            finish();
        });

        // show the user details
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users user = snapshot.getValue(Users.class);
                    binding.userName.setText(user.getUserName());
                    binding.userEmail.setText(user.getUserEmail());
                    // binding.userProfilePic.set
                    if (!user.getUserProfileImg().isEmpty()) {
                        Picasso.get().load(user.getUserProfileImg()).into(binding.userProfilePic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AboutActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}