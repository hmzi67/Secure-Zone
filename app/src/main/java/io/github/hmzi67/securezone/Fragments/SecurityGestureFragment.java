package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import io.github.hmzi67.securezone.databinding.FragmentSecurityGestureBinding;


public class SecurityGestureFragment extends Fragment {

    private FragmentSecurityGestureBinding binding;

    public SecurityGestureFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSecurityGestureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}