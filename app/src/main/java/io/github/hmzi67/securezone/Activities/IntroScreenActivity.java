package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.hmzi67.securezone.Adapters.IntroViewPageAdapter;
import io.github.hmzi67.securezone.Modals.ScreenItem;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityIntroScreenBinding;

public class IntroScreenActivity extends AppCompatActivity {
    ActivityIntroScreenBinding binding;
    private ViewPager screenPager;
    IntroViewPageAdapter introViewPagerAdapter;
    private Button btnNext;
    TabLayout tabIndicator;
    int position = 0;
    Button btnGetStarted;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);

        // Firebase Database realtime sync enabled.
        if (Firebase.class == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseDatabase.getInstance().getReference().keepSynced(true);
        }

        // when the activity is about to be launch we need to check id it's opened before or not
        if (restorePrefData() ) {
            // ready the firebase services
            firebaseAuth = FirebaseAuth.getInstance();

            // whether the user is login or not
            if (firebaseAuth.getCurrentUser() != null) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            } else {
                Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }

        tabIndicator = findViewById(R.id.tab_indicator);
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);

        // fill list screen
        List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Safety over speed", "Safe walk analyzes indicates and crime reports, So we can recommend you to safest route to reach your destination.", R.drawable.ic_intro_screen_1));
        mList.add(new ScreenItem("Stay Connected", "Share live location with your loved ones and get immediate access to the safety toolkit. \nYou are not alone:)", R.drawable.ic_intro_screen_2));
        mList.add(new ScreenItem("Safe Travel", "It's not about the destination, It's about the journey", R.drawable.ic_intro_screen_3));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager2);
        introViewPagerAdapter = new IntroViewPageAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tabLayout with viewPager
        tabIndicator.setupWithViewPager(screenPager);


        //next button click Listener
        btnNext.setOnClickListener(view -> {
            position = screenPager.getCurrentItem();
            if(position < mList.size()){
                position++;
                screenPager.setCurrentItem(position);
            }
            //when we reach the last page
            // will show get started button and hide all tab indicators
            if(position == mList.size() -1){
                loadLastScreen();
            }
        });

        // tab layout add change Listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == mList.size() -1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // get started button clicked
        btnGetStarted.setOnClickListener(view -> {
            startActivity(new Intent(IntroScreenActivity.this, LoginActivity.class));
            savePrefsData();
            finish();
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpenedBefore = pref.getBoolean("isIntroOpened", false);
        return isIntroActivityOpenedBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();
    }

    // will show getstarted button and hide all tab indicators
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
    }
}