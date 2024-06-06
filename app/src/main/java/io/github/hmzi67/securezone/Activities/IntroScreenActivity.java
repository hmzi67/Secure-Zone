package io.github.hmzi67.securezone.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_screen);
// when the activity is about to be launch we need to check id it's opened before or not
        if (restorePrefData() ) {
            Intent mainActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(mainActivity);
            finish();
        }

        // tab init
        tabIndicator = findViewById(R.id.tab_indicator);
        // btn init
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
//        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);

        // fill list screen
        List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Safety over speed", "", R.drawable.ic_login_screen));
        mList.add(new ScreenItem("Stay Connected", "", R.drawable.ic_avatar_placeholder));
        mList.add(new ScreenItem("Safe Travel", "", R.drawable.ic_avatar_placeholder));

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager2);
        introViewPagerAdapter = new IntroViewPageAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tabLayout with viewPager
        tabIndicator.setupWithViewPager(screenPager);

        //next button click Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(IntroScreenActivity.this, LoginActivity.class));
                savePrefsData();
                finish();
            }
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

        // todo: add an animation to get started button
        // setup animation
    }
}