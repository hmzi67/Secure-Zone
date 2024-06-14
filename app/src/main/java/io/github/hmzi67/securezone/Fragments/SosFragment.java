package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentSosBinding;


public class SosFragment extends Fragment {
    private FragmentSosBinding binding;

    public SosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_sos, container, false);
        binding = FragmentSosBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

    }

}