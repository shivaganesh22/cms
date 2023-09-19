package com.complaint.management.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

import javax.mail.MessagingException;

public class complaintAdapter extends RecyclerView.Adapter<complaintAdapter.MyViewHolder> {

    public complaintAdapter(Context context, ArrayList<complaintsGetter> list) {
        this.context = context;
        this.list = list;



    }

    Context context;
    ArrayList<complaintsGetter>list;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        complaintsGetter com=list.get(position);
        holder.date.setText(com.getDate());
        holder.email.setText(com.getEmail());
        holder.main.setText(com.getMain_problem());
        if (!com.getSub_problem().equals("")) {
            holder.sub.setVisibility(View.VISIBLE);
            holder.subtext.setVisibility(View.VISIBLE);
            holder.sub.setText(com.getSub_problem());
        } else {
            holder.sub.setVisibility(View.GONE);
            holder.subtext.setVisibility(View.GONE);
        }
        if (com.getStatus().toLowerCase().equals("pending")||com.getStatus().toLowerCase().equals("rejected")){
            holder.status.setText(com.getStatus());
            holder.status.setTextColor(Color.RED);

        }
        else if (com.getStatus().toLowerCase().equals("approved")){

            holder.status.setText(com.getStatus());
            holder.status.setTextColor(Color.GREEN);
        }


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DialogPlus dialogPlus=DialogPlus.newDialog(context)
                        .setContentHolder(new ViewHolder(R.layout.complaint_dialog))
                        .create();
                View myview=dialogPlus.getHolderView();
                final TextView chatStatus=myview.findViewById(R.id.comChatStatus);
                final TextView name=myview.findViewById(R.id.comName);
                final TextView mobile=myview.findViewById(R.id.comMobile);
                final TextView text=myview.findViewById(R.id.comText);
                final TextView email=myview.findViewById(R.id.comEmail);
                final TextView date=myview.findViewById(R.id.comDate);
                final TextView main=myview.findViewById(R.id.comMain);
                final TextView sub=myview.findViewById(R.id.comSub);
                final TextView usrstatus=myview.findViewById(R.id.usrStatus);

                final TextView status= myview.findViewById(R.id.comStatus);
                final TextView description=myview.findViewById(R.id.comDescription);
                final Button approve=myview.findViewById(R.id.comApprove);
                final Button reject =myview.findViewById(R.id.comReject);
                final Button chat=myview.findViewById(R.id.comChat);
                final Button noti=myview.findViewById(R.id.comNotify);
                final Button sendmail=myview.findViewById(R.id.comEmailsend);
                final Button disable=myview.findViewById(R.id.comDisable);
                final Button enable=myview.findViewById(R.id.comEnable);
                email.setText(com.getEmail());
                name.setText(com.getUsername());
                mobile.setText(com.getMobile());

                date.setText(com.getDate());
                description.setText(com.getDescription());
                main.setText(com.getMain_problem());
                String[] usrData = new String[3];

                ValueEventListener userValueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userGetter userdata = dataSnapshot.getValue(userGetter.class);
                        if (userdata != null ) {
                            usrData[0]=userdata.getChat();
                            usrData[1]=userdata.getStatus();
                            usrData[2]=userdata.getToken();
                            chatStatus.setText(usrData[0]);
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
                                chatStatus.setTextColor(Color.GREEN);
                            }
                            else{
                                chatStatus.setTextColor(Color.RED);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                };
                FirebaseDatabase.getInstance().getReference("Users").child(com.getId()).addValueEventListener(userValueEventListener);



                chat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(context,chatting.class);
                        i.putExtra("username",com.getUsername());
                        i.putExtra("email",com.getEmail());
                        i.putExtra("receiverId",com.getId());
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

                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("Username",com.getUsername());
                        map.put("Email",com.getEmail());
                        map.put("Mobile",com.getMobile());
                        map.put("Main_problem",com.getMain_problem());
                        map.put("Sub_problem",com.getSub_problem());
                        map.put("Description",com.getDescription());
                        map.put("Date",com.getDate());
                        map.put("Status","Approved");
                        map.put("Id",com.getId());

                        FirebaseDatabase.getInstance().getReference("Complaints").child(com.getKey()).updateChildren(map);

                        status.setText("Approved");
                        status.setTextColor(Color.GREEN);
                    }

                });
                reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("Username",com.getUsername());
                        map.put("Email",com.getEmail());
                        map.put("Mobile",com.getMobile());
                        map.put("Main_problem",com.getMain_problem());
                        map.put("Sub_problem",com.getSub_problem());
                        map.put("Description",com.getDescription());
                        map.put("Date",com.getDate());
                        map.put("Status","Rejected");
                        map.put("Id",com.getId());
                        FirebaseDatabase.getInstance().getReference("Complaints").child(com.getKey()).updateChildren(map);
                        status.setText("Rejected");
                        status.setTextColor(Color.RED);


                    }
                });

                disable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("Chat","false");
                        FirebaseDatabase.getInstance().getReference("Users").child(com.getId()).updateChildren(map);
                        chatStatus.setText("false");
                        chatStatus.setTextColor(Color.RED);


                    }

                });

                enable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String,Object> map=new HashMap<>();

                        map.put("Chat","true");

                        FirebaseDatabase.getInstance().getReference("Users").child(com.getId()).updateChildren(map);
                        chatStatus.setText("true");
                        chatStatus.setTextColor(Color.GREEN);

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

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView date,main,sub,status,subtext,email;
CardView card;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subtext=itemView.findViewById(R.id.subText);
            email=itemView.findViewById(R.id.listEmail);
            date=itemView.findViewById(R.id.listDate);
            main=itemView.findViewById(R.id.listMain);
            card=itemView.findViewById(R.id.complaint_item);
            sub=itemView.findViewById(R.id.listSub);
            status=itemView.findViewById(R.id.listStatus);
        }
    }
}
