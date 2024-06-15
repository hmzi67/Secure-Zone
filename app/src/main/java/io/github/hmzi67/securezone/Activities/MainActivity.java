package io.github.hmzi67.securezone.Activities;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.hmzi67.securezone.Fragments.AddContactFragment;
import io.github.hmzi67.securezone.Fragments.FakeCallFragment;
import io.github.hmzi67.securezone.Fragments.HomeFragment;
import io.github.hmzi67.securezone.Fragments.SecurityGestureFragment;
import io.github.hmzi67.securezone.Fragments.SosFragment;
import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.Widgets.ProgressStatus;
import io.github.hmzi67.securezone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  {
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        FloatingActionButton fab = binding.fab;
        Toolbar toolbar = binding.homeToolbar;
        NavigationView navigationView = binding.navView;
        DrawerLayout drawerLayout = binding.drawerLayout;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if(firebaseAuth.getCurrentUser() != null) {
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    assert users != null;
                    String userName = users.getUserName();
                    String[] Parts = userName.split(" ");
                    binding.toolbarText.setText("Hello, " + Parts[0] + "!");
                    CircleImageView cv = binding.userImg.findViewById(R.id.userImg);
                    if (!users.getUserProfileImg().isEmpty())
                        Picasso.get().load(users.getUserProfileImg()).placeholder(R.drawable.ic_logo).into(cv);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
        }

//        navigationView.setNavigationItemSelectedListener(item -> {
//            int itemId = item.getItemId();
//
//            if (itemId == R.id.nav_home) {
//                Toast.makeText(MainActivity.this, "Home Clicked", Toast.LENGTH_SHORT).show();
//            } else if (itemId == R.id.nav_logout) {
//                Toast.makeText(MainActivity.this, "Logout Clicked", Toast.LENGTH_SHORT).show();
//            } else if (itemId == R.id.nav_about) {
//                Toast.makeText(MainActivity.this, "About Clicked", Toast.LENGTH_SHORT).show();
//            }
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//            return true;
//        });

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_navigation_home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.bottom_navigation_fakeCall) {
                replaceFragment(new FakeCallFragment());
            } else if (itemId == R.id.bottom_navigation_addPerson) {
                replaceFragment(new AddContactFragment());
            } else if (itemId == R.id.bottom_navigation_google) {
                replaceFragment(new SecurityGestureFragment());
            }

            return true;
        });

        fab.setOnClickListener(view -> {
            startActivity(new Intent(this, AIChatActivity.class));
        });


        init();
    }



    private void init() {
        // ready the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // getting user image
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                assert users != null;
                TextView tx = binding.navView.getHeaderView(0).findViewById(R.id.userName);
                tx.setText(users.getUserName());
                TextView tx1 = binding.navView.getHeaderView(0).findViewById(R.id.userEmail);
                tx1.setText(users.getUserEmail());
                CircleImageView cv = binding.navView.getHeaderView(0).findViewById(R.id.userImg);
                if (!users.getUserProfileImg().isEmpty())
                    Picasso.get().load(users.getUserProfileImg()).placeholder(R.drawable.ic_logo).into(cv);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout videoLayout = dialog.findViewById(R.id.layoutVideo);
        LinearLayout shortsLayout = dialog.findViewById(R.id.layoutShorts);
        LinearLayout liveLayout = dialog.findViewById(R.id.layoutLive);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        videoLayout.setOnClickListener(view -> {
            dialog.dismiss();
            Toast.makeText(MainActivity.this,"Upload a Video is clicked",Toast.LENGTH_SHORT).show();
        });

        shortsLayout.setOnClickListener(view -> {
            dialog.dismiss();
            Toast.makeText(MainActivity.this,"Create a short is Clicked",Toast.LENGTH_SHORT).show();
        });

        liveLayout.setOnClickListener(view -> {
            dialog.dismiss();
            Toast.makeText(MainActivity.this,"Go live is Clicked",Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}

