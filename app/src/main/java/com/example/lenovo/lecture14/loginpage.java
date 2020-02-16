package com.example.lenovo.lecture14;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class loginpage extends AppCompatActivity {
    Button login;
    EditText email , password ;
    TextView goto_Register;

    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        email = findViewById(R.id.login_Email);
        password = findViewById(R.id.login_Password);
        login = findViewById(R.id.login_button);
        goto_Register = findViewById(R.id.goto_register);

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        goto_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginpage.this,signuppage.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation();
            }

            private void CheckValidation() {
                String Email= email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(Email))
                {
                    email.setError("Please Enter Email");
                }
                else if(Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    email.setError("Please Enter valid email");
                }
                else if (TextUtils.isEmpty(Password))
                {
                    password.setError("Please Enter Password");
                }
                else
                {
                    PerformAuthentication(Email,Password);
                }
            }

            private void PerformAuthentication(String email, String password) {
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                auth.signInWithEmailAndPassword(email, password)

                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(loginpage.this,MainActivity.class));
                                    finish();
                                }
                                else
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(loginpage.this,"Not authenticated", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
