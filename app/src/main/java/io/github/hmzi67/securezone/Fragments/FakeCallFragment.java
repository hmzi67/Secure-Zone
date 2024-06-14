package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentFakeCallBinding;

public class FakeCallFragment extends Fragment {

    private FragmentFakeCallBinding binding;

    public FakeCallFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // return inflater.inflate(R.layout.fragment_fake_call, container, false);
        binding = FragmentFakeCallBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

    }
}