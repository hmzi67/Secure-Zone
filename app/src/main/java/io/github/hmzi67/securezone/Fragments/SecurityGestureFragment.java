package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentSecurityGestureBinding;


public class SecurityGestureFragment extends Fragment {

    private FragmentSecurityGestureBinding binding;

    public SecurityGestureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_security_gesture, container, false);
        binding = FragmentSecurityGestureBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

    }
}