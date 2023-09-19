package com.complaint.management.system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.complaint.management.system.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register extends AppCompatActivity {
    EditText remail,rpass,rcpass,rusername;
    Button rsubmit;
    FirebaseAuth mAuth;

    String pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rusername=findViewById(R.id.inputUsername);
        remail=findViewById(R.id.inputEmail);
        rpass=findViewById(R.id.inputPassword);
        rcpass=findViewById(R.id.inputConformPassword);
        rsubmit=findViewById(R.id.btnRegister);
        mAuth=FirebaseAuth.getInstance();
        TextView t=findViewById(R.id.alreadyHaveAccount);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(register.this,login.class);
                startActivity(i);
            }
        });
        ProgressDialog progressDialog = new ProgressDialog(this);
        rsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=remail.getText().toString();
                String username=rusername.getText().toString();
                String password=rpass.getText().toString();
                String confirmPassword=rcpass.getText().toString();
                if (email.isEmpty() || password.isEmpty()||confirmPassword.isEmpty() || username.isEmpty()){
                    Toast.makeText(register.this,"Fill all details",Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(confirmPassword)){
                    Toast.makeText(register.this,"Passwords not match",Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<8){
                    Toast.makeText(register.this,"Password must be 8 characters",Toast.LENGTH_SHORT).show();
                }
                else if (!email.trim().matches(pattern)){
                    Toast.makeText(register.this,"Enter valid email",Toast.LENGTH_SHORT).show();
                }
                else{

                    progressDialog.setMessage("Registering...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        FirebaseUser u=mAuth.getCurrentUser();
                                        u.sendEmailVerification();
                                        HashMap<String,Object> map=new HashMap<>();
                                        map.put("Username",username.toUpperCase());
                                        map.put("Email",email.trim());
                                        map.put("Role","user");
                                         map.put("Chat","false");
                                         map.put("State","true");
                                         map.put("Token","");
                                         map.put("Status","0");
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(u.getUid()).setValue(map);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
                                        builder.setTitle("Account created");
                                        builder.setMessage("Please verify email to login").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        if (u.isEmailVerified()){
                                                            Toast.makeText(register.this,"Login Successful",Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(register.this,MainActivity.class));
                                                        }

                                                        else{
                                                            Toast.makeText(register.this,"Please verify email",Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(register.this,login.class));

                                                        }

                                                    }
                                                }).show();







                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(register.this, "User already exists",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
            }

            private void insertUser() {
                String email=remail.getText().toString();
                String username=rusername.getText().toString();

            }
        });
    }
}