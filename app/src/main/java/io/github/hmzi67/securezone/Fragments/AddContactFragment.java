package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentAddContactBinding;


public class AddContactFragment extends Fragment {

    private FragmentAddContactBinding binding;


    public AddContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_add_contact, container, false);
        binding = FragmentAddContactBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

    }
}