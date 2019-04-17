package com.gymtrainer.gymuserapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    RadioGroup radioGroupGender;
    RadioButton radioGenderButton;

    TextView textViewLogin;
    FirebaseAuth auth;
    ProgressBar progressBar;
    DatabaseReference databaseReferenceUser;
    FirebaseUser firebaseUser;

    EditText ed_name,ed_email,ed_password,ed_confirmpassword,ed_phonenumber,ed_address,ed_city,ed_age;
    String name,email,password,confirmpassword,phonenumber,address,city,age;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initControls();
        setListeners();
    }


    private void initControls()
    {

        textViewLogin = (TextView)findViewById(R.id.goToLogin);
        progressBar = (ProgressBar)findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Users");
        ed_name = (EditText)findViewById(R.id.edName);
        ed_email = (EditText)findViewById(R.id.edEmail);
        ed_password = (EditText)findViewById(R.id.edPassword);
        ed_confirmpassword = (EditText)findViewById(R.id.edConfirmPassword);
        ed_phonenumber = (EditText)findViewById(R.id.edPhoneNumber);
        ed_address = (EditText)findViewById(R.id.edAddress);
        ed_city = (EditText)findViewById(R.id.edCity);
        radioGroupGender = (RadioGroup)findViewById(R.id.radioGroupGender);
        ed_age = (EditText)findViewById(R.id.edAge);
        buttonRegister = (Button)findViewById(R.id.registerButton);


    }

    private void setListeners()
    {
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();

            }
        });
    }

    private void registerUser()
    {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        radioGenderButton = (RadioButton) findViewById(selectedId);
        name = ed_name.getText().toString();
        email = ed_email.getText().toString();
        password= ed_password.getText().toString();
        confirmpassword = ed_confirmpassword.getText().toString();
        phonenumber = ed_phonenumber.getText().toString();
        address = ed_address.getText().toString();
        city = ed_city.getText().toString();
       age = ed_age.getText().toString();



        if (!password.equals(confirmpassword))
        {
            Toast.makeText(RegisterActivity.this, "Password must be equal to the confirm password", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(RegisterActivity.this, "Name cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValid(email))
        {
            Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals("") || confirmpassword.equals(""))
        {
            Toast.makeText(RegisterActivity.this, "Password cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.length()<6)
        {
            Toast.makeText(RegisterActivity.this, "Password must be 6 characters long.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(phonenumber.equals(""))
        {
            Toast.makeText(RegisterActivity.this, "Phone number cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }

            if(address.equals(""))
            {
                Toast.makeText(RegisterActivity.this, "Address cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if(city.equals(""))
            {
                Toast.makeText(RegisterActivity.this, "City cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if(age.equals(""))
            {
                Toast.makeText(RegisterActivity.this, "Age cannot be blank", Toast.LENGTH_SHORT).show();
                return;
        }

        else
        {
            registerFirebaseAuth();
        }
    }

    private void registerFirebaseAuth()
    {
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email,password)   // firebase create user with email/password
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            insertDatabase();   // inserting values in firebase database



                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Could not register user",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void insertDatabase()
    {
        firebaseUser = auth.getCurrentUser();
        User user = new User(name,email,phonenumber,address,city,radioGenderButton.getText().toString(),age,"defaultImage",firebaseUser.getUid());

        databaseReferenceUser.child(firebaseUser.getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            sendVerificationLink();

                            Intent i = new Intent(RegisterActivity.this,HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }


                    }
                });
    }

    private void sendVerificationLink()
    {
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"A verification email has been sent to your email address",Toast.LENGTH_LONG).show();
                        gotoHomeScreen();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Could not send email verification. Try again later",Toast.LENGTH_LONG).show();
                    }
            }
        });
    }

    private void gotoHomeScreen()
    {
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(RegisterActivity.this,SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    public static boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

}
