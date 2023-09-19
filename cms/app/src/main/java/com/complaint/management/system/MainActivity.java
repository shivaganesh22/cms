package com.complaint.management.system;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.complaint.management.system.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.nio.charset.CharacterCodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String Chat="";
    ActivityMainBinding binding;
    DatabaseReference mPresenceRef;
    private ValueEventListener userValueEventListener;



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
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();

        if (mUser==null){
            Intent i=new Intent(MainActivity.this,login.class);
            startActivity(i);

        }
        //data
         mPresenceRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminGetter userdata = dataSnapshot.getValue(adminGetter.class);
                if(userdata.getState().equalsIgnoreCase("false")){
                    Toast.makeText(MainActivity.this,"Your account is removed",Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this,login.class));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).addValueEventListener(userValueEventListener);





        findViewById(R.id.plusbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new complaint());
            }
        });
        replaceFragment(new fragment_home());
        binding.bottomNavigationView.setBackground(null);
       binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new fragment_home());
                    break;
                case R.id.shorts:
                    FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String chat = dataSnapshot.child("Chat").getValue(String.class);

                            if (chat != null && chat.equals("true")) {
                                replaceFragment(new fragment_chat());

                            } else {
                                Toast.makeText(MainActivity.this,"Chat is disable by admin",Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    break;



                case R.id.subscriptions:
                    replaceFragment(new profile());
                    break;
                case R.id.library:
                    replaceFragment(new user_complaints());
                    break;
            }
            return true;
        });
       findViewById(R.id.plusbtn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               replaceFragment(new complaint());
           }
       });


    }


    private void updatePresence(String online) {
        HashMap map=new HashMap();
        map.put("Status",online);
        mPresenceRef.updateChildren(map);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}