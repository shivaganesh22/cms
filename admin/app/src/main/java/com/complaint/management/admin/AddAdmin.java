package com.complaint.management.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddAdmin extends AppCompatActivity {
    String email,username,mobile,password,Role="admin",Chat="true";
    TextView Email,Name,Mobile,Password;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        Email=findViewById(R.id.aaddEmail);
        mAuth=FirebaseAuth.getInstance();
        String adminEmail=mAuth.getCurrentUser().getEmail();
        Name=findViewById(R.id.aaddName);
        Mobile=findViewById(R.id.aaddNumber);
        Password=findViewById(R.id.aaddPassword);
        ProgressDialog progressDialog=new ProgressDialog(this);
        findViewById(R.id.adminSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=Email.getText().toString();
                username=Name.getText().toString();
                mobile=Mobile.getText().toString();
                password=Password.getText().toString();
                if (email.isEmpty()||username.isEmpty()||mobile.isEmpty()||password.isEmpty()){
                    Toast.makeText(AddAdmin.this,"Fill all details",Toast.LENGTH_SHORT).show();
                }
                else{
                    final Dialog verifyDialog = new Dialog(AddAdmin.this);
                    verifyDialog.setContentView(R.layout.verify_account);
                    verifyDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    verifyDialog.show();



                    final EditText verifyPass=verifyDialog.findViewById(R.id.verifyPassword);
                    final Button verifyBtn=verifyDialog.findViewById(R.id.verifyBtn);
                    verifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progressDialog.setMessage("Verifying...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            String pass=verifyPass.getText().toString();

                            mAuth.signInWithEmailAndPassword(adminEmail, pass)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            progressDialog.dismiss();
                                            verifyDialog.dismiss();
                                            progressDialog.setMessage("Creating account");
                                            progressDialog.setCancelable(false);
                                            progressDialog.show();
                                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password)
                                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                // User account created successfully

                                                                HashMap<String,Object> map=new HashMap<>();
                                                                map.put("Username",username.toUpperCase());
                                                                map.put("Email",email.trim());
                                                                map.put("Role",Role);
                                                                map.put("Chat",Chat);
                                                                map.put("Token","");
                                                                map.put("Status","0");
                                                                map.put("State","true");
                                                                map.put("Mobile",mobile);
                                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);
                                                                Toast.makeText(AddAdmin.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                                                mAuth.getCurrentUser().sendEmailVerification();
                                                                mAuth.signOut();
                                                                mAuth.signInWithEmailAndPassword(adminEmail, pass)
                                                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                            @Override
                                                                            public void onSuccess(AuthResult authResult) {
                                                                                progressDialog.dismiss();
                                                                                startActivity(new Intent(AddAdmin.this,MainActivity.class));

                                                                            }
                                                                        });



                                                            } else {
                                                                progressDialog.dismiss();
                                                                Toast.makeText(AddAdmin.this, "Failed to create account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();

                                            Toast.makeText(AddAdmin.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });



                }

            }
        });


    }
}