package io.github.hmzi67.securezone.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.hmzi67.securezone.Adapters.FakeCallAdapter;
import io.github.hmzi67.securezone.Modals.FakeCallModel;
import io.github.hmzi67.securezone.databinding.FragmentFakeCallBinding;

public class FakeCallFragment extends Fragment {

    private FragmentFakeCallBinding binding;
    private ArrayList<FakeCallModel> fakeCalls;
    private FakeCallAdapter adapter;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    public FakeCallFragment() {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFakeCallBinding.inflate(inflater, container, false);
        init();
        return binding.getRoot();
    }



    private void init() {
        // initialize the database
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // ready the fake call adapter
        fakeCalls = new ArrayList<>();
        adapter = new FakeCallAdapter();
        adapter.setFakeCalls(fakeCalls, getContext());
        binding.rvFakeCalls.setAdapter(adapter);
        binding.rvFakeCalls.setLayoutManager(new LinearLayoutManager(getContext()));

        // get users
        getUsers();
    }

    private void getUsers() {
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (fakeCalls != null) fakeCalls.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    FakeCallModel model = ds.getValue(FakeCallModel.class);
                    model.setCallId(ds.getKey());
                    fakeCalls.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}