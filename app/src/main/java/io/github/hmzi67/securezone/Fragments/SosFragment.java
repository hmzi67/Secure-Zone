package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentSosBinding;


public class SosFragment extends Fragment {
    private FragmentSosBinding binding;
    private CountDownTimer countDownTimer;
    private final int COUNTDOWN_SECONDS = 3;
    private boolean isLongPress;

    public SosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSosBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        binding.helpNeeded.setOnClickListener(view -> {
            if (countDownTimer == null)
                startCountdown();
            else {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        });
    }

    private void startCountdown() {
        binding.icon.setVisibility(View.GONE);
        binding.successStatus.setVisibility(View.GONE);
        binding.time.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                binding.time.setText(String.valueOf(secondsRemaining + 1));
            }

            @Override
            public void onFinish() {
                binding.time.setVisibility(View.GONE);
                binding.icon.setImageResource(R.drawable.ic_check);
                binding.icon.setVisibility(View.VISIBLE);
                binding.successStatus.setVisibility(View.VISIBLE);
                countDownTimer = null;
            }
        }.start();

    }

}