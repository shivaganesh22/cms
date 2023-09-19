package com.complaint.management.system;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    EditText lemail,lpass;
    Button lsubmit;
    FirebaseAuth mAuth;Boolean verify;
    private static final int RC_SIGN_IN = 123;
    GoogleSignInClient mGoogleSignInClient;







    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()){
            Intent i=new Intent(login.this,MainActivity.class);
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
        mAuth = FirebaseAuth.getInstance();
        lemail = findViewById(R.id.loginEmail);
        lpass = findViewById(R.id.loginPassword);
        lsubmit = findViewById(R.id.btnlogin);
        TextView t = findViewById(R.id.textViewSignUp);
        TextView forgot = findViewById(R.id.forgotPassword);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(login.this, register.class);
                startActivity(i);
            }
        });
        findViewById(R.id.donthave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(login.this, register.class);
                startActivity(i);
            }
        });
        ProgressDialog progressDialog = new ProgressDialog(this);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = lemail.getText().toString();
                if (email.isEmpty()) {
                    Toast.makeText(login.this, "Enter email address", Toast.LENGTH_SHORT).show();
                } else {


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
                    Toast.makeText(login.this, "Recovery mail sent", Toast.LENGTH_SHORT).show();

                }
            }
        });

        lsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = lemail.getText().toString();
                String password = lpass.getText().toString();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Fill all details", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Login...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    HashMap<String, Object> map = new HashMap<>();


                    mAuth.signInWithEmailAndPassword(email.trim(), password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        verify = mAuth.getCurrentUser().isEmailVerified();


                                        if (verify) {
                                            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    String state = dataSnapshot.child("State").getValue(String.class);
                                                    progressDialog.dismiss();
                                                    if (state != null && state.equals("true")) {
                                                        Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                        FirebaseMessaging.getInstance().getToken()
                                                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<String> task) {
                                                                        if (task.isSuccessful()) {
                                                                            HashMap<String, Object> map = new HashMap<>();
                                                                            map.put("Token", task.getResult());
                                                                            FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                                                                        } else {

                                                                        }
                                                                    }
                                                                });
                                                        startActivity(new Intent(login.this, MainActivity.class));

                                                    } else {
                                                        Toast.makeText(login.this, "Account removed", Toast.LENGTH_SHORT).show();
                                                        mAuth.signOut();

                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    progressDialog.dismiss();
                                                    mAuth.signOut();
                                                }
                                            });

                                        } else if (verify == null) {
                                            progressDialog.dismiss();
                                            Toast.makeText(login.this, "Please try again", Toast.LENGTH_SHORT).show();

                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(login.this, "Please verify email", Toast.LENGTH_SHORT).show();


                                        }

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        progressDialog.dismiss();
                                        Toast.makeText(login.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }


            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.btnGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);


                ProgressDialog progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("Signing...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);




                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()) {
                                                String state = snapshot.child("State").getValue(String.class);
                                                if (state != null && state.equals("true")) {
                                                    Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                    FirebaseMessaging.getInstance().getToken()
                                                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<String> task) {
                                                                    if (task.isSuccessful()) {
                                                                        HashMap<String, Object> map = new HashMap<>();
                                                                        map.put("Token", task.getResult());
                                                                        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).updateChildren(map);
                                                                    } else {

                                                                    }
                                                                }
                                                            });
                                                    startActivity(new Intent(login.this, MainActivity.class));

                                                }
                                                else{
                                                    mAuth.signOut();
                                                    Toast.makeText(login.this,"Account removed",Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                            else {
                                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();

                                                FirebaseMessaging.getInstance().getToken()
                                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<String> task) {
                                                                if (task.isSuccessful()) {
                                                                    HashMap<String,Object> map=new HashMap<>();
                                                                    map.put("Username",user.getDisplayName());
                                                                    map.put("Email",user.getEmail());
                                                                    map.put("Role","user");
                                                                    map.put("Chat","false");
                                                                    map.put("State","true");
                                                                    map.put("Token",task.getResult());
                                                                    map.put("Status",System.currentTimeMillis());
                                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).updateChildren(map);
                                                                    Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                                    startActivity(new Intent(login.this,MainActivity.class));
                                                                } else {
                                                                    HashMap<String,Object> map=new HashMap<>();
                                                                    map.put("Username",user.getDisplayName());
                                                                    map.put("Email",user.getEmail());
                                                                    map.put("Role","user");
                                                                    map.put("Chat","false");
                                                                    map.put("State","true");
                                                                    map.put("Token","");
                                                                    map.put("Status",System.currentTimeMillis());
                                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).updateChildren(map);
                                                                    Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                                                    startActivity(new Intent(login.this,MainActivity.class));

                                                                }
                                                            }
                                                        });


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle error
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                }
                            }
                        });

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }



}