package io.github.hmzi67.securezone.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.hmzi67.securezone.Adapters.FakeCallAdapter;
import io.github.hmzi67.securezone.Modals.FakeCallModel;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.FragmentFakeCallBinding;

public class FakeCallFragment extends Fragment {

    private FragmentFakeCallBinding binding;
    private ArrayList<FakeCallModel> fakeCalls;
    private FakeCallAdapter adapter;

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


        fakeCalls = new ArrayList<>();
        adapter = new FakeCallAdapter();
        adapter.setFakeCalls(fakeCalls, getContext());
        binding.rvFakeCalls.setAdapter(adapter);
        binding.rvFakeCalls.setLayoutManager(new LinearLayoutManager(getContext()));
        getUsers();

    }

    private void getUsers() {
        // getting data from db
        FakeCallModel model = new FakeCallModel("", "A", "909090909008");
        fakeCalls.add(model);
        fakeCalls.add(model);
        fakeCalls.add(model);

        adapter.notifyDataSetChanged();
    }
}