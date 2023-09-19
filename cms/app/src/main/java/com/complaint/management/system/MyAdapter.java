package com.complaint.management.system;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<complaint_class> list;

    public MyAdapter(Context context, ArrayList<complaint_class> list) {
        this.context = context;
        this.list = list;
    }
    public void setFilteredList(ArrayList<complaint_class> list){
        this.list=list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        complaint_class com=list.get(position);

            holder.date.setText(com.getDate());
            holder.main.setText(com.getMain_problem());

        ValueEventListener userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                complaint_class facdata = dataSnapshot.getValue(complaint_class.class);
                if (facdata.getStatus().toLowerCase().equals("pending")||facdata.getStatus().toLowerCase().equals("rejected")){
                    holder.status.setText(facdata.getStatus());
                    holder.status.setTextColor(Color.RED);

                }
                else if (facdata.getStatus().toLowerCase().equals("approved")){

                    holder.status.setText(facdata.getStatus());
                    holder.status.setTextColor(Color.GREEN);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        FirebaseDatabase.getInstance().getReference("Complaints").child(com.getKey()).addValueEventListener(userValueEventListener);



        if (!com.getSub_problem().equals("")) {
                holder.sub.setVisibility(View.VISIBLE);
                holder.subtext.setVisibility(View.VISIBLE);
                holder.sub.setText(com.getSub_problem());
            } else {
                holder.sub.setVisibility(View.GONE);
                holder.subtext.setVisibility(View.GONE);
            }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus= DialogPlus.newDialog(context)
                        .setContentHolder(new ViewHolder(R.layout.complaint_dialog))
                        .create();
                View myview=dialogPlus.getHolderView();
                final TextView date=myview.findViewById(R.id.comDate);
                final TextView main=myview.findViewById(R.id.comMain);
                final TextView sub=myview.findViewById(R.id.comSub);
                final TextView text=myview.findViewById(R.id.comText);
                final TextView status= myview.findViewById(R.id.comStatus);
                final TextView description=myview.findViewById(R.id.comDescription);
                date.setText(com.getDate());
                description.setText(com.getDescription());
                main.setText(com.getMain_problem());


                if (com.getStatus().toLowerCase().equals("pending")||com.getStatus().toLowerCase().equals("rejected")){
                    status.setText(com.getStatus());
                    status.setTextColor(Color.RED);

                }
                else if (com.getStatus().toLowerCase().equals("approved")){

                    status.setText(com.getStatus());
                    status.setTextColor(Color.GREEN);
                }
                if (!com.getSub_problem().equals("")) {
                    sub.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    sub.setText(com.getSub_problem());
                } else {
                    sub.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                }
                dialogPlus.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date,main,sub,status,subtext,description;
        CardView card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            subtext=itemView.findViewById(R.id.listText);
            date=itemView.findViewById(R.id.listDate);
            main=itemView.findViewById(R.id.listMain);
            card=itemView.findViewById(R.id.usecom);
            sub=itemView.findViewById(R.id.listSub);
            status=itemView.findViewById(R.id.listStatus);
        }
    }
}
