package com.complaint.management.system;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class adminAdapter extends RecyclerView.Adapter<adminAdapter.MyViewHolder> {
    Context context;
    ArrayList<adminGetter> list;

    public adminAdapter(Context context, ArrayList<adminGetter> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.userlist,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        adminGetter com=list.get(position);
        holder.username.setText(com.getUsername());
        holder.email.setText(com.getEmail());

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,chatting.class);
                i.putExtra("username",com.getUsername());
                i.putExtra("email",com.getEmail());
                i.putExtra("receiverId",com.getKey());
                i.putExtra("status",com.getStatus());
                i.putExtra("token",com.getToken());
                context.startActivity(i);
            }
        });
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
        if (com.getStatus().equalsIgnoreCase("online")){
            holder.status.setText(com.getStatus());
            holder.status.setTextColor(Color.GREEN);
        }
        else{
            String dd=sdf.format(new Date(Long.parseLong(com.getStatus())));
            holder.status.setText("Lastseen :"+dd);
            holder.status.setTextColor(Color.BLACK);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView email,username,status;
        CardView card;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        card=itemView.findViewById(R.id.user_item);
        status=itemView.findViewById(R.id.usrStatus);
        username=itemView.findViewById(R.id.listUsername);
        email=itemView.findViewById(R.id.listEmailuser);

    }
}
}
