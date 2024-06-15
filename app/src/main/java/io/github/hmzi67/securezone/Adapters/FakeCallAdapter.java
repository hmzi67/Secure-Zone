package io.github.hmzi67.securezone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        holder.callName.setText(fakeCalls.get(position).getCallName());
        holder.callNumber.setText(fakeCalls.get(position).getCallNumber());

        holder.container.setOnClickListener(view -> {
            Toast.makeText(context, "call started", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return fakeCalls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView container;
        private ImageView callImage;
        private TextView callName;
        private TextView callNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // finding views
            container = itemView.findViewById(R.id.container);
            callImage = itemView.findViewById(R.id.callImage);
            callName = itemView.findViewById(R.id.callName);
            callNumber = itemView.findViewById(R.id.callNumber);
        }
    }
}
