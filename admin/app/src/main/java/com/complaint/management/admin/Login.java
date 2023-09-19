package com.complaint.management.admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText lemail,lpass;
    Button lsubmit;

    FirebaseAuth mAuth;String role="",id="";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i=new Intent(Login.this,MainActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        lemail=findViewById(R.id.loginEmail);
        lpass=findViewById(R.id.loginPassword);
        ProgressDialog progressDialog=new ProgressDialog(this);
        lsubmit=findViewById(R.id.btnlogin);
        findViewById(R.id.forgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=lemail.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(Login.this,"Enter email address",Toast.LENGTH_SHORT).show();
                }
                else{


                    progressDialog.setMessage("Sending recovery mail...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 1000);
                    mAuth.sendPasswordResetEmail(email.trim());
                    Toast.makeText(Login.this,"Recovery mail sent",Toast.LENGTH_SHORT).show();

                }
            }
        });
        lsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=lemail.getText().toString();
                String password=lpass.getText().toString();
                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(Login.this,"Fill all details",Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setMessage("Login...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
//                    mAuth.signInWithEmailAndPassword(email.trim(), password)
//                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        id=FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                        getRole();
//
//                                        progressDialog.dismiss();
//
//                                        if (role.equalsIgnoreCase("admin")){
//
//                                            Toast.makeText(Login.this,"Login Successful",Toast.LENGTH_SHORT).show();
//                                            Intent i=new Intent(Login.this,MainActivity.class);
//                                            startActivity(i);
//
//                                        }
//                                        else if(role==""){
//                                            mAuth.signOut();
//                                            Toast.makeText(Login.this,"Please try again",Toast.LENGTH_SHORT).show();
//
//                                        }
//
//
//                                        else{
//
//                                            mAuth.signOut();
//
//
//                                            Toast.makeText(Login.this, "Access denied try again",
//                                                    Toast.LENGTH_SHORT).show();
//
//                                        }
//                                        role="";
//
//
//                                    } else {
//                                        // If sign in fails, display a message to the user.
//                                        progressDialog.dismiss();
//                                        Toast.makeText(Login.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            });


// ...

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is logged in successfully
                                    String userId = mAuth.getCurrentUser().getUid();
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String role = dataSnapshot.child("Role").getValue(String.class);
                                            String State=dataSnapshot.child("State").getValue(String.class);
                                            progressDialog.dismiss();

                                            if (role != null && role.equals("admin")&& State.equalsIgnoreCase("true")) {
                                                Toast.makeText(Login.this,"Login Successful",Toast.LENGTH_SHORT).show();
                                                FirebaseMessaging.getInstance().getToken()
                                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<String> task) {
                                                                if (task.isSuccessful()) {
                                                                    HashMap<String,Object> map=new HashMap<>();
                                                                    map.put("Token",task.getResult());
                                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                                                                } else {

                                                                }
                                                            }
                                                        });
                                                startActivity(new Intent(Login.this,MainActivity.class));

                                            } else {
                                                Toast.makeText(Login.this,"Access Denied",Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            mAuth.signOut();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();

                                    Toast.makeText(Login.this,"Invalid Credentials",Toast.LENGTH_SHORT).show();
                                }
                            });









                }

            }


        });



    }










    private void getRole() {
        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    Map map=(Map)snapshot.getValue();
                    if(snapshot.getKey().matches(id)){
                        role=map.get("Role").toString();

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}