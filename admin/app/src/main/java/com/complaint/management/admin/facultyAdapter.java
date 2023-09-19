package com.complaint.management.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class facultyAdapter extends RecyclerView.Adapter<facultyAdapter.MyViewHolder> {
    private Context context;
    ArrayList<facultyGetter> list;

    public facultyAdapter(Context context, ArrayList<facultyGetter> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.faculty_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        facultyGetter com=list.get(position);
        final String[] Data =new String[6];
        ValueEventListener userValueEventListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                facultyGetter facdata = dataSnapshot.getValue(facultyGetter.class);
                Data[0] =facdata.getUsername();
                Data[1]=facdata.getStatus();
                Data[2]=facdata.getDesignation();
                Data[3]=facdata.getMobile();
                Data[5]=facdata.getToken();
                Data[4]=facdata.getExperience();

                if( Data[1].equalsIgnoreCase("online")){
                    holder.status.setText(Data[1]);
                    holder.status.setTextColor(Color.GREEN);
                }
                else{
                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
                    String dd=sdf.format(new Date(Long.parseLong(Data[1])));
                    holder.status.setText(dd);
                    holder.status.setTextColor(Color.RED);
                }
                holder.designation.setText(Data[2]);
                holder.name.setText(Data[0]);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        FirebaseDatabase.getInstance().getReference("Users").child(com.getKey()).addValueEventListener(userValueEventListener);


        holder.email.setText(com.getEmail());





        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogPlus dialogPlus=DialogPlus.newDialog(holder.card.getContext())
                        .setContentHolder(new ViewHolder(R.layout.faculty_dialog))
                        .create();
                View myview=dialogPlus.getHolderView();
                final TextView name=myview.findViewById(R.id.diafacName);
                final TextView mobile=myview.findViewById(R.id.diafacMobile);
                final TextView email=myview.findViewById(R.id.diafacEmail);
                final TextView designation=myview.findViewById(R.id.diafacDesignation);
                final TextView experience=myview.findViewById(R.id.diafacExperience);
                final TextView facstatus=myview.findViewById(R.id.diafacStatus);

                final Button edit=myview.findViewById(R.id.facUpdate);
                final Button delete =myview.findViewById(R.id.facDelete);
                final Button chatting=myview.findViewById(R.id.facChatting);
                final Button sendmail=myview.findViewById(R.id.facEmailsend);
                final Button noti=myview.findViewById(R.id.facNotify);


                ValueEventListener userValueEventListener = new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        facultyGetter facdata = dataSnapshot.getValue(facultyGetter.class);
                        Data[0] =facdata.getUsername();
                        Data[1]=facdata.getStatus();
                        Data[2]=facdata.getDesignation();
                        Data[3]=facdata.getMobile();
                        Data[5]=facdata.getToken();
                        Data[4]=facdata.getExperience();

                        if(Data[1].equalsIgnoreCase("online")){
                            facstatus.setText(Data[1]);
                            facstatus.setTextColor(Color.GREEN);
                        }
                        else{
                            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
                            String dd=sdf.format(new Date(Long.parseLong(Data[1])));
                            facstatus.setText(dd);
                            facstatus.setTextColor(Color.RED);
                        }

                        name.setText(Data[0]);

                        mobile.setText(Data[3]);
                        designation.setText(Data[2]);
                        experience.setText(Data[4]);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                };
                FirebaseDatabase.getInstance().getReference("Users").child(com.getKey()).addValueEventListener(userValueEventListener);



                email.setText(com.getEmail());



                dialogPlus.show();

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

                chatting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(context,chatting.class);
                        i.putExtra("username",Data[0]);
                        i.putExtra("email",com.getEmail());
                        i.putExtra("receiverId",com.getKey());
                        i.putExtra("status",Data[1]);
                        i.putExtra("token",Data[5]);

                            context.startActivity(i);


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
                                        requestBody.put("to", Data[5]);
                                        requestBody.put("priority", "high");
                                        requestBody.put("notification", payload);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


// Send the POST request to the FCM API
                                    RequestQueue queue = Volley.newRequestQueue(context);
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

              edit.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      final Dialog updateDialog = new Dialog(context);
                      updateDialog.setContentView(R.layout.update_faculty);
                      updateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                      updateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                      final EditText name=updateDialog.findViewById(R.id.facupName);
                      final TextView email=updateDialog.findViewById(R.id.facupEmail);
                      final TextView mobile=updateDialog.findViewById(R.id.facupNumber);
                      final TextView designation=updateDialog.findViewById(R.id.facupDesignation);
                      final TextView experience=updateDialog.findViewById(R.id.facupExperience);



                      name.setText(Data[0]);
                      email.setText(com.getEmail());
                      mobile.setText(Data[3]);
                      designation.setText(Data[2]);
                      experience.setText(Data[4]);



                      final Button update=updateDialog.findViewById(R.id.faculty_updatebtn);

                      updateDialog.show();
                      update.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              String username=name.getText().toString();
                              String phone=mobile.getText().toString();
                              String Designation=designation.getText().toString();
                              String Exp=experience.getText().toString();
                              if(username.isEmpty()||phone.isEmpty()||Designation.isEmpty()||Exp.isEmpty()){
                                  Toast.makeText(context,"Fill all details",Toast.LENGTH_SHORT).show();
                              }
                              else{
                                  HashMap<String,Object> map=new HashMap<>();
                                  map.put("Username",username.toUpperCase());
                                  map.put("Mobile",phone);
                                  map.put("Designation",Designation);
                                  map.put("Experience",Exp);
                                  FirebaseDatabase.getInstance().getReference().child("Users").child(com.getKey()).updateChildren(map);
                                  Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show();
                                  updateDialog.dismiss();


                              }


                          }
                      });


                  }
              });


           delete.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setMessage("Are you sure you want to delete ?");
                   builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           HashMap<String,Object> map=new HashMap<>();
                           map.put("State","false");
                           FirebaseDatabase.getInstance().getReference("Users").child(com.getKey()).updateChildren(map);
                           dialog.dismiss();
                           dialogPlus.dismiss();
                       }
                   });
                   builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
                   AlertDialog dialog = builder.create();
                   dialog.show();

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
        TextView name,email,designation,status;CardView card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card=itemView.findViewById(R.id.faculty_card);
            name=itemView.findViewById(R.id.facUsername);
            email=itemView.findViewById(R.id.facEmail);
            status=itemView.findViewById(R.id.facStatus);
            designation=itemView.findViewById(R.id.facDesignation);

        }
    }
}
