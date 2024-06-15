package io.github.hmzi67.securezone.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import io.github.uxlabspk.cloudmeeting.Models.AllMessagesModel;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Date;

import io.github.hmzi67.securezone.Adapters.MessageAdapter;
import io.github.hmzi67.securezone.R;
import io.github.hmzi67.securezone.databinding.ActivityAiChatBinding;

public class AIChatActivity extends AppCompatActivity {
    private ActivityAiChatBinding binding;
    private ArrayList<io.github.uxlabspk.cloudmeeting.Models.AllMessagesModel> allMessages = new ArrayList<>();
    private MessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
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
            AllMessagesModel model = new AllMessagesModel(binding.messageText.getText().toString(), "mAuth.getUid()", new Date().getTime());
            binding.messageText.setText("");
//            mDatabase.getReference().child("chats").child(senderRoom).child("messages").push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    mDatabase.getReference().child("chats")
//                            .child(receiverRoom)
//                            .child("messages")
//                            .push().setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                }
//                            });
//                }
//            });
        });

    }

//    private void getMessages() {
//        mDatabase.getReference().child("chats").child(senderRoom).child("messages").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (allMessages != null) allMessages.clear();
//                for (DataSnapshot ds: snapshot.getChildren()) {
//                    AllMessagesModel model = ds.getValue(AllMessagesModel.class);
//                    allMessages.add(model);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

}