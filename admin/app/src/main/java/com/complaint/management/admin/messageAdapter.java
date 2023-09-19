package com.complaint.management.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class messageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    int ITEM_SEND = 1, ITEM_RECEIVE = 2;
    ArrayList<messageGetter> list;

    public messageAdapter(Context context, ArrayList<messageGetter> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_layout, parent, false);
            return new ReceiverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        messageGetter msg = list.get(position);
        if (holder.getClass() == senderViewHolder.class) {
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.msgsender.setText(msg.getMessage());
        }
        else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.msgreceiver.setText(msg.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        messageGetter msg = list.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(msg.getSenderId()))
            return ITEM_SEND;
        else
            return ITEM_RECEIVE;
    }

    public class senderViewHolder extends RecyclerView.ViewHolder {
        TextView msgsender;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            msgsender = itemView.findViewById(R.id.sendermsg);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView msgreceiver;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            msgreceiver = itemView.findViewById(R.id.receivermsg);
        }
    }
}
