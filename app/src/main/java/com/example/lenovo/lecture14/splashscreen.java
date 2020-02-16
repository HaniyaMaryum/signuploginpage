package com.example.lenovo.lecture14;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class splashscreen extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        auth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (auth.getCurrentUser()!=null)
                {
                    startActivity(new Intent(splashscreen.this,MainActivity.class));

                    finish();
                }
                else {
                    startActivity(new Intent(splashscreen.this, loginpage.class));

                    finish();
                }
            }
        },5000);
    }
}
