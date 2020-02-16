package com.example.lenovo.lecture14;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.lecture14.jnhnhj.PersonModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class signuppage extends AppCompatActivity {
    EditText user,email, password , phone;
    EditText conf;
    Button Register;
    RadioGroup gender;
    RadioButton genderButton;
    TextView gotoLogin;
    ImageButton choose_button;

    Uri fileData= null;
    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signuppage);

        choose_button=findViewById(R.id.per_image);
        Register = findViewById(R.id.reg_button);
        user = findViewById(R.id.reg_username);
        email= findViewById(R.id.reg_Email);
        password = findViewById(R.id.reg_Password);
        conf = findViewById(R.id.regcon_Password);
        phone = findViewById(R.id.reg_Phone);
        gender = findViewById(R.id.gender_group);
        gotoLogin = findViewById(R.id.goto_login);

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();



        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( signuppage.this,loginpage.class));
                finish();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckAllValidation();
            }

        });

        choose_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Choose_Image();
            }
        });
    }

    private void Choose_Image() {
        Intent ImagePick = new Intent();
        ImagePick.setType("image/*");
        ImagePick.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(ImagePick,01);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 01 && resultCode == RESULT_OK)
        {
            fileData = data.getData();
        }
    }



    private void CheckAllValidation() {
        String userName = user.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPass = password.getText().toString().trim();
        String userCon = conf.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();

        if(TextUtils.isEmpty(userName))
        {
            user.setError("Please Enter username here");
        }
        else if (TextUtils.isEmpty(userEmail))
        {
            email.setError("Please enter email");
        }
        else if (!(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()))
        {
            email.setError("Please enter valid email");
        }
        else if(TextUtils.isEmpty(userPass))
        {
            password.setError("Please Enter password here");
        }
        else if(TextUtils.isEmpty(userCon))
        {
            conf.setError("Please Enter password here");
        }
        else if(!(userPass.equals(userCon)))
        {
            Toast.makeText(this,"Password not matched",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPhone))
        {
            phone.setError("Please Enter Phone here");
        }
        else if(fileData == null)
        {
            Toast.makeText(this,"Please choose an Image",Toast.LENGTH_SHORT).show();
        }
        else
        {
            int selectedID = gender.getCheckedRadioButtonId();
            genderButton = findViewById(selectedID);
            String userGender = genderButton.getText().toString();
            InsertInDataBase(userName,userEmail,userPass,userCon,userPhone, userGender,fileData);
        }

    }

    private void InsertInDataBase(final String userName, final String userEmail, final String userPass, final String userCon, final String userPhone, final String userGender, final Uri fileData)
    {

        progressDialog.setMessage("Please Wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        auth.createUserWithEmailAndPassword(userEmail,userPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            SendImageinStorage(userName, userEmail, userPass, userCon, userPhone, userGender, fileData);
                        }
                        else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(signuppage.this,"Authentication not complete",Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

    private void SendImageinStorage(final String userName, final String userEmail, final String userPass, final String userCon, final String userPhone, final String userGender, final Uri fileData)
    {
        final StorageReference ref = FirebaseStorage.getInstance().getReference("PersonImages/"+auth.getCurrentUser().getUid());
        ref.putFile(fileData).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful())
                {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                {
                    Uri DownloadedURL = task.getResult();
                    InsertInRealTimeDatabase(userName, userEmail, userPass, userCon, userPhone, userGender, DownloadedURL);

                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(signuppage.this,"URL no generated",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InsertInRealTimeDatabase(String userName, String userEmail, String userPass, String userCon, String userPhone, String userGender, Uri downloadedURL)
    {
        PersonModel value= new PersonModel(userName,userEmail,userPass,userPhone,userGender,downloadedURL.toString());
        FirebaseDatabase.getInstance().getReference("PersonTable").child(auth.getCurrentUser().getUid()).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    Toast.makeText(signuppage.this,"User created",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(signuppage.this,loginpage.class));
                    finish();
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(signuppage.this,"User not created",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
