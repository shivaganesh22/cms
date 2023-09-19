package com.complaint.management.system;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.window.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;

public class splashscreen extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent intent = new Intent(splashscreen.this, login.class);
                    startActivity(intent);


                finish();
            }
        }, 500);

    }
}