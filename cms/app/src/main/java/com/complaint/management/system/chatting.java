package com.complaint.management.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class chatting extends AppCompatActivity {
    FirebaseUser currentUser;
    TextView userName,Email,Status;
    CardView sendButton;
    EditText messageBox;
    String username,email,Token,senderId,receiverId,userStatus;
    String senderRoom,receiverRoom;
    DatabaseReference database;
    messageAdapter myAdapter;
    RecyclerView recyclerView;
    DatabaseReference mPresenceRef;
    private ValueEventListener userValueEventListener;
    ArrayList<messageGetter> list;
    private ValueEventListener senderListener;

    @Override
    protected void onResume() {
        super.onResume();
        updatePresence("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updatePresence(System.currentTimeMillis()+"");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        username=getIntent().getStringExtra("username");
        email=getIntent().getStringExtra("email");
        senderId=currentUser.getUid();

        userStatus=getIntent().getStringExtra("status");
        Token=getIntent().getStringExtra("token");
        receiverId=getIntent().getStringExtra("receiverId");
        userName=findViewById(R.id.chattingUsername);
        Email=findViewById(R.id.chattingEmail);
        sendButton=findViewById(R.id.messageSend);
        messageBox=findViewById(R.id.messageBox);
        Status=findViewById(R.id.chattingStatus);
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminGetter userdata = dataSnapshot.getValue(adminGetter.class);


                if (userdata != null && userdata.getStatus() != null) {

                    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
                    userStatus=(userdata.getStatus());
                    if(userStatus.equalsIgnoreCase("online")){
                        Status.setText("("+userStatus+")");
                        Status.setTextColor(Color.GREEN);
                    }
                    else{
                        String dd=sdf.format(new Date(Long.parseLong(userStatus)));
                        Status.setText("(Lastseen :"+dd+")");
                        Status.setTextColor(Color.RED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("Users").child(receiverId).addValueEventListener(userValueEventListener);
        final String[] senderName = new String[1];
        senderListener = new ValueEventListener() {
            private boolean chatDisabled=false;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminGetter senderdata = dataSnapshot.getValue(adminGetter.class);


                if (senderdata != null) {
                    senderName[0] =senderdata.getUsername();
                    if(senderdata.getChat().equalsIgnoreCase("false") &&!chatDisabled){
                        Toast.makeText(chatting.this,"Chat is disabled",Toast.LENGTH_SHORT).show();
                        chatDisabled = true;
                        startActivity(new Intent(chatting.this,MainActivity.class));

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("Users").child(senderId).addValueEventListener(senderListener);



        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy hh:mm a");
        if(userStatus.equalsIgnoreCase("online")){
            Status.setText("("+userStatus+")");
            Status.setTextColor(Color.GREEN);
        }
        else{
            String dd=sdf.format(new Date(Long.parseLong(userStatus)));
            Status.setText("(Lastseen :"+dd+")");
            Status.setTextColor(Color.RED);
        }
        System.out.println(userStatus);
        Email.setText(email);
        userName.setText(username);
        Date date=new Date();
        senderRoom = senderId+receiverId;
        receiverRoom = receiverId+senderId;
        mPresenceRef = FirebaseDatabase.getInstance().getReference("Users").child(senderId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView=findViewById(R.id.allMessages);
        recyclerView.setLayoutManager(linearLayoutManager);

        database= FirebaseDatabase.getInstance().getReference("Chats").child(senderRoom).child("messages");
        list=new ArrayList<>();
        myAdapter=new messageAdapter(this,list);
        recyclerView.setAdapter(myAdapter);

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(myAdapter.getItemCount() - 1);
            }
        });

        // Declare a threshold value for keyboard height
        final int KEYBOARD_THRESHOLD = 200;

// Add an OnGlobalLayoutListener to detect keyboard open/close
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int previousHeight = 0;
            @Override
            public void onGlobalLayout() {
                int currentHeight = recyclerView.getHeight();
                if (previousHeight != 0 && Math.abs(currentHeight - previousHeight) > KEYBOARD_THRESHOLD && !list.isEmpty()) {
                    // Keyboard is open, scroll RecyclerView to bottom
                    recyclerView.smoothScrollToPosition(list.size() - 1);
                }
                previousHeight = currentHeight;
            }
        });

// Add a click listener to the message box to show the keyboard
        messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(messageBox, InputMethodManager.SHOW_IMPLICIT);
            }
        });




        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messageGetter com=dataSnapshot.getValue(messageGetter.class);
                        list.add(com);



                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=messageBox.getText().toString();
                if (msg.isEmpty()){
                    Toast.makeText(chatting.this,"Enter message",Toast.LENGTH_SHORT).show();
                }
                else{
                    messageBox.setText("");
                    messageGetter messagegetter=new messageGetter(senderId,date.getTime()+"",msg);
                    FirebaseDatabase.getInstance().getReference().child("Chats").child(senderRoom).child("messages").push().setValue(messagegetter)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseDatabase.getInstance().getReference().child("Chats").child(receiverRoom).child("messages").push().setValue(messagegetter)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(!userStatus.equalsIgnoreCase("online")){
                                                        JSONObject payload = new JSONObject();
                                                        try {
                                                            payload.put("title",senderName[0] );
                                                            payload.put("body", msg);

                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

// Set the request body
                                                        JSONObject requestBody = new JSONObject();

                                                        try {
                                                            requestBody.put("to", Token);
                                                            requestBody.put("priority", "high");
                                                            requestBody.put("notification", payload);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }


// Send the POST request to the FCM API
                                                        RequestQueue queue = Volley.newRequestQueue(chatting.this);
                                                        JsonObjectRequest request = new JsonObjectRequest(
                                                                Request.Method.POST,
                                                                "https://fcm.googleapis.com/fcm/send",
                                                                requestBody,
                                                                new Response.Listener<JSONObject>() {
                                                                    @Override
                                                                    public void onResponse(JSONObject response) {
                                                                    }
                                                                },
                                                                new Response.ErrorListener() {
                                                                    @Override
                                                                    public void onErrorResponse(VolleyError error) {

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
            }
        });



    }


    private void updatePresence(String online) {
        HashMap map=new HashMap();
        map.put("Status",online);
        mPresenceRef.updateChildren(map);
    }
}