package com.complaint.management.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class userAdapter extends RecyclerView.Adapter<userAdapter.MyViewHolder> {
    Context context;
    ArrayList<userGetter>list;


    public userAdapter(Context context, ArrayList<userGetter> list) {
        this.context = context;

        this.list = list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new userAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        userGetter com=list.get(position);



            holder.email.setText(com.getEmail());
            holder.username.setText(com.getUsername());


            if (com.getMobile() != null) {
                holder.mobiletext.setVisibility(View.VISIBLE);

                holder.mobile.setVisibility(View.VISIBLE);
                holder.mobile.setText(com.getMobile());

            } else {
                holder.mobiletext.setVisibility(View.GONE);

                holder.mobile.setVisibility(View.GONE);
            }
        if(com.getStatus().equalsIgnoreCase("online")){
            holder.status.setText(com.getStatus());
            holder.status.setTextColor(Color.GREEN);
        }
        else{
            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
            String dd=sdf.format(new Date(Long.parseLong(com.getStatus())));
            holder.status.setText(dd);
            holder.status.setTextColor(Color.RED);
        }

            holder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final DialogPlus dialogPlus=DialogPlus.newDialog(holder.card.getContext())
                            .setContentHolder(new ViewHolder(R.layout.user_dialog))
                            .create();
                    View myview=dialogPlus.getHolderView();
                    final TextView name=myview.findViewById(R.id.usrName);
                    final TextView mobile=myview.findViewById(R.id.usrMobile);
                    final TextView email=myview.findViewById(R.id.usrEmail);
                    final TextView date=myview.findViewById(R.id.usrDOB);
                    final TextView chat=myview.findViewById(R.id.usrChat);
                    final TextView address=myview.findViewById(R.id.usrAddress);
                    final TextView usrstatus=myview.findViewById(R.id.userStatus);

                    final Button enable=myview.findViewById(R.id.usrEnable);
                    final Button disable =myview.findViewById(R.id.usrDisable);
                    final Button chatting=myview.findViewById(R.id.usrChatting);
                    final Button sendmail=myview.findViewById(R.id.usrEmailsend);
                    final Button noti=myview.findViewById(R.id.usrNotify);
                    email.setText(com.getEmail());
                    name.setText(com.getUsername());
                    mobile.setText(com.getMobile());
                    date.setText(com.getDOB());
                    chat.setText(com.getChat());
                    if(com.getChat().equals("true")){
                        chat.setTextColor(Color.GREEN);
                    }else if(com.getChat().equals("false")){
                        chat.setTextColor(Color.RED);
                    }
                    if(com.getStatus().equalsIgnoreCase("online")){
                        usrstatus.setText(com.getStatus());
                        usrstatus.setTextColor(Color.GREEN);
                    }
                    else{
                        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
                        String dd=sdf.format(new Date(Long.parseLong(com.getStatus())));
                        usrstatus.setText(dd);
                       usrstatus.setTextColor(Color.RED);
                    }
                    String[] usrData = new String[3];

                    ValueEventListener userValueEventListener = new ValueEventListener() {


                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userGetter userdata = dataSnapshot.getValue(userGetter.class);
                            if (userdata != null ) {
                                usrData[0]=userdata.getChat();
                                usrData[1]=userdata.getStatus();
                                usrData[2]=userdata.getToken();
                                chat.setText(usrData[0]);
                                if(usrData[1].equalsIgnoreCase("online")){
                                    usrstatus.setText(usrData[1]);
                                    usrstatus.setTextColor(Color.GREEN);
                                }
                                else{
                                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
                                    String dd=sdf.format(new Date(Long.parseLong(usrData[1])));
                                    usrstatus.setText(dd);
                                    usrstatus.setTextColor(Color.RED);
                                }
                                if (usrData[0].equalsIgnoreCase("true")){
                                    chat.setTextColor(Color.GREEN);
                                }
                                else{
                                    chat.setTextColor(Color.RED);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    };
                    FirebaseDatabase.getInstance().getReference("Users").child(com.getKey()).addValueEventListener(userValueEventListener);


                    address.setText(com.getAddress());
                    dialogPlus.show();
                    enable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String,Object> map=new HashMap<>();

                            map.put("Chat","true");

                            FirebaseDatabase.getInstance().getReference("Users").child(list.get(holder.getAdapterPosition()).getKey()).updateChildren(map);
                            chat.setText("true");
                            chat.setTextColor(Color.GREEN);

                        }

                    });


                    chatting.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i=new Intent(context,chatting.class);
                            i.putExtra("username",com.getUsername());
                            i.putExtra("email",com.getEmail());
                            i.putExtra("receiverId",com.getKey());
                            i.putExtra("status",usrData[1]);
                            i.putExtra("token",usrData[2]);
                            if (usrData[0].equalsIgnoreCase("true")){
                                context.startActivity(i);
                            }
                            else{
                                Toast.makeText(context,"Please enable chat",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    sendmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog emailDialog = new Dialog(context);
                            emailDialog.setContentView(R.layout.email_layout);
                            emailDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            emailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            emailDialog.show();
                            final EditText subject=emailDialog.findViewById(R.id.emailSubject);
                            final EditText message=emailDialog.findViewById(R.id.emailMessage);
                            final Button sendemail=emailDialog.findViewById(R.id.sendEmail);
                            sendemail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (subject.getText().toString().isEmpty()||message.getText().toString().isEmpty()){
                                        Toast.makeText(context,"Fill all details",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        new SendEmailTask(context).execute(subject.getText().toString(), message.getText().toString(), com.getEmail());
                                        emailDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });


                    disable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String,Object> map=new HashMap<>();

                            map.put("Chat","false");

                            FirebaseDatabase.getInstance().getReference("Users").child(list.get(holder.getAdapterPosition()).getKey()).updateChildren(map);
                         chat.setText("false");
                         chat.setTextColor(Color.RED);


                        }

                    });


                    noti.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog notDialog = new Dialog(context);
                            notDialog.setContentView(R.layout.notification);
                            notDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            notDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            notDialog.show();



                            final EditText title=notDialog.findViewById(R.id.notTitle);
                            final EditText message=notDialog.findViewById(R.id.notMessage);
                            final Button sendnot=notDialog.findViewById(R.id.sendNot);






                            sendnot.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String tit= title.getText().toString();
                                    String msg=message.getText().toString();
                                    if (tit.isEmpty() || msg.isEmpty()){
                                        Toast.makeText(context,"Fill all details",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        // Set the notification payload
                                        JSONObject payload = new JSONObject();
                                        try {
                                            payload.put("title", tit);
                                            payload.put("body", msg);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

// Set the request body
                                        JSONObject requestBody = new JSONObject();

                                        try {
                                            requestBody.put("to", usrData[2]);
                                            requestBody.put("priority", "high");
                                            requestBody.put("notification", payload);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


// Send the POST request to the FCM API
                                        RequestQueue queue = Volley.newRequestQueue(myview.getContext());
                                        JsonObjectRequest request = new JsonObjectRequest(
                                                Request.Method.POST,
                                                "https://fcm.googleapis.com/fcm/send",
                                                requestBody,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        notDialog.dismiss();
                                                        Toast.makeText(context,"Notification sent successfully",Toast.LENGTH_SHORT).show();
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(context,"Failed to sent,Please try again",Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                        ) {
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                // Set the authorization header with the FCM server key
                                                Map<String, String> headers = new HashMap<>();
                                                headers.put("Authorization", "key=AAAA208xe4g:APA91bH5M2ZviCaUFNmkeNtqQTA_ULC1jSOT3cgFU6Ut26rucxl4U7dOFZ3BpqWDGlcieXOYVCJY2sdyuherzD7-VoBzBGgGZwUzGTPLfVK8Es5F0p0yjttAoeFAK9IwzZspPJBrI4Z8");
                                                headers.put("Content-Type", "application/json");
                                                return headers;
                                            }
                                        };
                                        queue.add(request);

                                    }

                                }
                            });

                        }
                    });





                }
            });

//        }




//
//        else{
//            holder.card.setVisibility(View.GONE);
//            holder.card.getLayoutParams().height = 0;
//        }


    }


    @Override
    public int getItemCount() {

    return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView email,username,address,DOB,mobile,mobiletext,status;
        CardView card;

       public MyViewHolder(@NonNull View itemView) {
           super(itemView);
           email=itemView.findViewById(R.id.listEmailuser);
           username=itemView.findViewById(R.id.listUsername);
           status=itemView.findViewById(R.id.listStatus);
           mobiletext=itemView.findViewById(R.id.mobileText);

           mobile=itemView.findViewById(R.id.listMobile);
           card=itemView.findViewById(R.id.user_item);


       }
   }

}
