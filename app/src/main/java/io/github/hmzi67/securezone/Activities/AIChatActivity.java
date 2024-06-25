package io.github.hmzi67.securezone.Activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.ai.client.generativeai.BuildConfig;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
            if (!binding.messageText.getText().toString().isEmpty()) {
                AllMessagesModel model = new AllMessagesModel(binding.messageText.getText().toString(), "ME");
                String query = binding.messageText.getText().toString();
                binding.messageText.setText("");
                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Chat").push().setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getMessages();
                        getAIResponse(query);
                    }
                });
            }
        });

        // delete the chat
        binding.deleteChat.setOnClickListener(view -> {
            firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Chat").removeValue().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   allMessages.clear();
                   adapter.notifyDataSetChanged();
                   Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
               }
            });
        });
    }

    private void getAIResponse(String query) {
        binding.aiStatus.setText("Thinking...");
        GenerativeModel gm = new GenerativeModel("gemini-pro", "AIzaSyBhxrFX8eNZkWZ-9PGGf2d7haaUOmTTTzg");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(query)
                .build();

        Executor executor =  Executors.newFixedThreadPool(10);

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                runOnUiThread(() -> binding.aiStatus.setText("Online"));
                AllMessagesModel model = new AllMessagesModel(resultText, "BOT");
                binding.messageText.setText("");
                firebaseDatabase.getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid().toString()).child("Chat").push().setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getMessages();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
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
                    binding.rvUserChat.scrollToPosition(allMessages.size() -1);
                } else {
                    binding.introChatScreen.setVisibility(View.VISIBLE);
                    allMessages.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
