package com.gymtrainer.gymuserapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText editTextName,editTextEmail,editTextCellNumber,editTextAddress,editTextCity,editTextAge;
    Toolbar toolbar;
    Button updateButton;
    DatabaseReference databaseReferenceUsers;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    RadioGroup radioGroupGender;
    RadioButton radioGenderButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        init();
        getIntentData();
        setListeners();
        setData();
    }




    private void init()
    {
        toolbar = (Toolbar)findViewById(R.id.toolbarEditProfile);
        toolbar.setTitle("Edit Profile");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users").child("Users").child(firebaseUser.getUid());

        editTextName = findViewById(R.id.edName);
        editTextEmail = findViewById(R.id.edEmail);
        editTextCellNumber = findViewById(R.id.edPhoneNumber);
        editTextAddress = findViewById(R.id.edAddress);
        editTextCity = findViewById(R.id.edCity);
        editTextAge = findViewById(R.id.edAge);
        progressBar = findViewById(R.id.progressBarEditProfile);
        updateButton = (Button)findViewById(R.id.updateButton);
        radioGroupGender = (RadioGroup)findViewById(R.id.radioGroupGender);
    }

    private void getIntentData()
    {
        if(getIntent()!=null)
        {
            user = (User)getIntent().getSerializableExtra("userObj");
        }
    }
    private void setListeners()
    {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();

            }
        });

    }



    private void updateProfile()
    {
            if(editTextName.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Name cannot be blank",Toast.LENGTH_LONG).show();
            }

        if(editTextCellNumber.getText().toString().equals(""))
        {
            Toast.makeText(EditProfileActivity.this, "Phone number cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        if(editTextAddress.getText().toString().equals(""))
        {
            Toast.makeText(EditProfileActivity.this, "Address cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        if(editTextCity.getText().toString().equals(""))
        {
            Toast.makeText(EditProfileActivity.this, "City cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        if(editTextAge.getText().toString().equals(""))
        {
            Toast.makeText(EditProfileActivity.this, "Age cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            updateUser();

        }


    }

    private void updateUser()
    {
        progressBar.setVisibility(View.VISIBLE);
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        radioGenderButton = (RadioButton) findViewById(selectedId);
        databaseReferenceUsers.child("name").setValue(editTextName.getText().toString());
        databaseReferenceUsers.child("phonenumber").setValue(editTextCellNumber.getText().toString());
        databaseReferenceUsers.child("address").setValue(editTextAddress.getText().toString());
        databaseReferenceUsers.child("city").setValue(editTextCity.getText().toString());
        databaseReferenceUsers.child("gender").setValue(radioGenderButton.getText().toString());
        databaseReferenceUsers.child("age").setValue(editTextAge.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"User profile updated successfully",Toast.LENGTH_LONG).show();
                                finish();
                                Intent i = new Intent(EditProfileActivity.this,ProfileActivity.class);
                                startActivity(i);

                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Could not update profile.",Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                    }
                });

    }

    private void setData()
    {
        editTextName.setText(user.getName());
        editTextEmail.setText(user.getEmail());
        editTextCellNumber.setText(user.getPhonenumber());
        editTextAddress.setText(user.getAddress());
        editTextCity.setText(user.getCity());
        editTextAge.setText(user.getAge());
        editTextEmail.setEnabled(false);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            finish();
            Intent i = new Intent(EditProfileActivity.this,ProfileActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }



}
