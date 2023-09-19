package com.complaint.management.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.complaint.management.admin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String Chat;
    ActivityMainBinding binding;
    DatabaseReference mPresenceRef;

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
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        if (mUser==null){
            Intent i=new Intent(MainActivity.this,Login.class);
            startActivity(i);

        }
        ValueEventListener userValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminGetter userdata = dataSnapshot.getValue(adminGetter.class);
                if (userdata.getState().equalsIgnoreCase("false")||!userdata.getRole().equalsIgnoreCase("admin")) {
                    Toast.makeText(MainActivity.this, "Your account is removed", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, Login.class));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).addValueEventListener(userValueEventListener);


        mPresenceRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new Home());
        binding.bottomNavigationView.setBackground(null);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new Home());
                    break;
                case R.id.shorts:

                    replaceFragment(new Users());
                    break;
                case R.id.subscriptions:
                    replaceFragment(new Manage());
                    break;
                case R.id.library:
                    replaceFragment(new Faculty());
                    break;
            }
            return true;
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