package io.github.hmzi67.securezone.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import io.github.hmzi67.securezone.Modals.AllMessagesModel;
import io.github.hmzi67.securezone.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private ArrayList<AllMessagesModel> allMessages = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_chat_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AllMessagesModel message = allMessages.get(position);

        if (message.getSentBy().matches("ME")) {
            holder.left_chat_view.setVisibility(View.GONE);
            holder.right_chat_view.setVisibility(View.VISIBLE);
            holder.right_chat_text.setText(message.getMessage());
        } else {
            holder.right_chat_view.setVisibility(View.GONE);
            holder.left_chat_view.setVisibility(View.VISIBLE);
            holder.left_chat_text.setText(message.getMessage());
        }

        // copy message text
        holder.copyLeftText.setOnClickListener(view -> {
            copyToClipboard(context, holder.left_chat_text.getText().toString());
        });

        holder.copyRightText.setOnClickListener(view -> {
            copyToClipboard(context, holder.right_chat_text.getText().toString());
        });

    }

    private void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Message Text ", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return allMessages.size();
    }

    public void setAllMessages(ArrayList<AllMessagesModel> allMessages, Context context) {
        this.allMessages = allMessages;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout left_chat_view, right_chat_view;
        TextView left_chat_text, right_chat_text;
        ImageButton copyLeftText, copyRightText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // finding the views.
            left_chat_view = itemView.findViewById(R.id.left_chat_view);
            right_chat_view = itemView.findViewById(R.id.right_chat_view);
            left_chat_text = itemView.findViewById(R.id.left_chat_text);
            right_chat_text = itemView.findViewById(R.id.right_chat_text);
            copyLeftText = itemView.findViewById(R.id.copyLeftText);
            copyRightText = itemView.findViewById(R.id.copyRightText);
        }
    }
}
