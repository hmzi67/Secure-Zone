package io.github.hmzi67.securezone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.github.hmzi67.securezone.Activities.EditContactActivity;
import io.github.hmzi67.securezone.Activities.InCommingCallActivity;
import io.github.hmzi67.securezone.Modals.FakeCallModel;
import io.github.hmzi67.securezone.R;

public class FakeCallAdapter extends RecyclerView.Adapter<FakeCallAdapter.ViewHolder>{

    private ArrayList<FakeCallModel> fakeCalls = new ArrayList<>();
    private Context context;

    public void setFakeCalls(ArrayList<FakeCallModel> fakeCalls, Context context) {
        this.fakeCalls = fakeCalls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_fake_calls, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FakeCallAdapter.ViewHolder holder, int position) {
        if (!fakeCalls.get(position).getCallImage().isEmpty())
            Picasso.get().load(fakeCalls.get(position).getCallImage()).placeholder(R.drawable.ic_avatar_placeholder).into(holder.callImage);
        else
            Picasso.get().load(R.drawable.ic_avatar_placeholder).into(holder.callImage);
        holder.callName.setText(fakeCalls.get(position).getCallName());
        holder.callNumber.setText(fakeCalls.get(position).getCallNumber());

        holder.editContact.setOnClickListener(view -> {
            Intent editContact = new Intent(context, EditContactActivity.class);
            editContact.putExtra("id", fakeCalls.get(position).getCallId());
            editContact.putExtra("userphoto", fakeCalls.get(position).getCallImage());
            editContact.putExtra("username", fakeCalls.get(position).getCallName());
            editContact.putExtra("usernumber", fakeCalls.get(position).getCallNumber());
            context.startActivity(editContact);
        });

        holder.container.setOnClickListener(view -> {
            Intent intent = new Intent(context, InCommingCallActivity.class);
            intent.putExtra("userphoto", fakeCalls.get(position).getCallImage());
            intent.putExtra("username", fakeCalls.get(position).getCallName());
            intent.putExtra("usernumber", fakeCalls.get(position).getCallNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fakeCalls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView container;
        private ImageView editContact;
        private ImageView callImage;
        private TextView callName;
        private TextView callNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            callImage = itemView.findViewById(R.id.callImage);
            callName = itemView.findViewById(R.id.callName);
            callNumber = itemView.findViewById(R.id.callNumber);
            editContact = itemView.findViewById(R.id.editContact);
        }
    }
}
