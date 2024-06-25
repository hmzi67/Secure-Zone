package io.github.hmzi67.securezone.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.hmzi67.securezone.Fragments.AddContactFragment;
import io.github.hmzi67.securezone.Fragments.FakeCallFragment;
import io.github.hmzi67.securezone.Fragments.HomeFragment;
import io.github.hmzi67.securezone.Fragments.SecurityGestureFragment;
import io.github.hmzi67.securezone.Fragments.SosFragment;
import io.github.hmzi67.securezone.Modals.Users;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferences pref;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private boolean isRecording = false;
    private MediaRecorder mediaRecorder;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 2) {
                Toast.makeText(getApplicationContext(), "Device has shaken.", Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    startIntent();
                }
            }

        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // startIntent();
                pref = getSharedPreferences("Settings", MODE_PRIVATE);
                if (pref.getBoolean("VC", false)) {
                    startIntent();
                } else {
                    Toast.makeText(MainActivity.this, "Video capturing is off by default", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               startRecording();
            } else {
                // Permission denied
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            // Video recorded successfully, you can do something with the saved video URI
            Uri videoUri = data.getData();
            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
            // Use the videoUri to do something (e.g., display it in a VideoView)
        } else {
            pref = getSharedPreferences("Settings", MODE_PRIVATE);
            if (pref.getBoolean("VC", false)) {
                startIntent();
            } else {
                Toast.makeText(MainActivity.this, "Video capturing is off by default", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == 22 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            // Save the image bitmap to a file or another storage location
            saveImageToStorage(bitmap);
//            Uri filePath = data.getData();
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//
//            // Save the image bitmap to a file or another storage location
//            saveImageToStorage(imageBitmap);
        }

        if (requestCode == 22 && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            // Save the image bitmap to a file or another storage location
            saveImageToStorage(bitmap);
        }
    }

    private void saveImageToStorage(Bitmap bitmap) {
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = new File(storageDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(this, "Image saved successfully"+ storageDir, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageCapture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 22);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private void startIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        pref = getSharedPreferences("Settings", MODE_PRIVATE);
        binding.fab.setVisibility(pref.getBoolean("AI", false) ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Volume down button is pressed
            Toast.makeText(MainActivity.this, "Volume Down Button Pressed", Toast.LENGTH_SHORT).show();

            pref = getSharedPreferences("Settings", MODE_PRIVATE);
            if (pref.getBoolean("IC", false)) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission not granted, request it
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.CAMERA},
                            1001);
                } else {
                    // Permission already granted, proceed with camera operations
                    startImageCapture();
                }
            } else {
                Toast.makeText(MainActivity.this, "Image capturing is off by default", Toast.LENGTH_SHORT).show();
            }

            return true; // Consume the event
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast.makeText(MainActivity.this, "Volume Up Button Pressed", Toast.LENGTH_SHORT).show();

            pref = getSharedPreferences("Settings", MODE_PRIVATE);
            if (pref.getBoolean("AR", false)) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            200);
                } else {
                    if (!isRecording) {
                        Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_SHORT).show();
                        startRecording();
                    } else {
                        Toast.makeText(MainActivity.this, "Recording stopped", Toast.LENGTH_SHORT).show();
                        stopRecording();
                    }
                }
            } else {
                Toast.makeText(MainActivity.this, "Audio Recording is off by default", Toast.LENGTH_SHORT).show();
            }


            return true; // Consume the event
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void startRecording() {

        if (checkPermissions()) {
//            String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
            String outputFile = getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/recording_" + System.currentTimeMillis() + ".3gp";

            File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(outputFile);

            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaRecorder.start();
            isRecording = true;
        }
    }

    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


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
            navigationView.setCheckedItem(R.id.nav_home);
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.bottom_navigation_home)
                replaceFragment(new HomeFragment());
            else if (itemId == R.id.bottom_navigation_fakeCall)
                replaceFragment(new FakeCallFragment());
            else if (itemId == R.id.bottom_navigation_addPerson)
                replaceFragment(new AddContactFragment());
            else if (itemId == R.id.bottom_navigation_google)
                replaceFragment(new SecurityGestureFragment());

            return true;
        });
        binding.sos.setOnClickListener(view -> {
            replaceFragment(new SosFragment());
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            } else if (id == R.id.nav_logout) {
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        fab.setOnClickListener(view -> startActivity(new Intent(this, AIChatActivity.class)));

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

        // Gesture code

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecording) {
            stopRecording();
        }
    }
}

