package io.github.hmzi67.securezone.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import io.github.hmzi67.securezone.Adapters.MessageAdapter;
import io.github.hmzi67.securezone.Modals.AllMessagesModel;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityAiChatBinding;

public class AIChatActivity extends AppCompatActivity {
    private ActivityAiChatBinding binding;
    private ArrayList<AllMessagesModel> allMessages = new ArrayList<>();
    private MessageAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        // ready the firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // check messages.
        getMessages();

        // go back
        binding.goBack.setOnClickListener(view -> onBackPressed());

        // getMessages();

        // setting the recycler view
        adapter = new MessageAdapter();
        adapter.setAllMessages(allMessages, this);
        binding.rvUserChat.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        llm.setSmoothScrollbarEnabled(true);
        binding.rvUserChat.setLayoutManager(llm);

        // on message sent.
        binding.sendBtn.setOnClickListener(view -> {
            AllMessagesModel model = new AllMessagesModel(binding.messageText.getText().toString(), "ME");
            binding.messageText.setText("");
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Chat").push().setValue(model).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    getMessages();
                }
            });
        });
    }

    private void getMessages() {
        firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (allMessages != null) allMessages.clear();
                if (snapshot.exists()) {
                    binding.introChatScreen.setVisibility(View.GONE);
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        AllMessagesModel model = ds.getValue(AllMessagesModel.class);
                        allMessages.add(model);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    binding.introChatScreen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}