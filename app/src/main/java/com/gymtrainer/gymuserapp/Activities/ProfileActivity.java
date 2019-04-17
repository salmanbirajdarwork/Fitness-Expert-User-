package com.gymtrainer.gymuserapp.Activities;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewName,textViewEmail,textViewCellNumber,textViewAddress,textViewCity,textViewGender,textViewAge;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceUsers;
    Toolbar toolbar;
    Button logOutButton;
    CircleImageView userImgView;
    Uri resultUri;
    StorageReference firebaseStorageReference;
    ProgressBar progressBar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        setListeners();
    }



    private void init()
    {

        // firebase initialization
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference().child("UserProfileImages");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users").child("Users").child(firebaseUser.getUid());


        // views initialization
        toolbar = (Toolbar)findViewById(R.id.toolbarProfile);
        logOutButton = (Button)findViewById(R.id.buttonLogout);
        progressBar = (ProgressBar)findViewById(R.id.progressBarProfile);
        userImgView = (CircleImageView) findViewById(R.id.trainerImgView);
        textViewName = (TextView)findViewById(R.id.nameUserTxt);
        textViewEmail = (TextView)findViewById(R.id.emailUserTxt);
        textViewCellNumber = (TextView)findViewById(R.id.cellUserTxt);
        textViewAddress = (TextView)findViewById(R.id.addressUserTxt);
        textViewCity = (TextView)findViewById(R.id.cityUserTxt);
        textViewGender = (TextView)findViewById(R.id.genderUserTxt);
        textViewAge = (TextView)findViewById(R.id.ageUserTxt);

        // toolbar
        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }






    }

    private void setListeners()
    {
        userImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // pick user image from gallery

                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, 14);
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();           // logout
            }
        });

        databaseReferenceUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    user = dataSnapshot.getValue(User.class);          // fetch current user detail from firebase

                    if(user!=null)
                    {
                        textViewName.setText(user.getName());
                        textViewEmail.setText(user.getEmail());
                        textViewCellNumber.setText(user.getPhonenumber());
                        textViewAddress.setText(user.getAddress());
                        textViewCity.setText(user.getCity());
                        textViewGender.setText(user.getGender());
                        textViewAge.setText(user.getAge());


                        // set all the values after getting from firebase

                        Picasso.get().load(user.getImageUrl()).placeholder(R.drawable.ic_launcher_man).into(userImgView);



                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Could not fetch user ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {

            finish();
        }
        else if(item.getItemId() == R.id.menuEdit)
        {
                Intent i = new Intent(ProfileActivity.this,EditProfileActivity.class);
                i.putExtra("userObj",user);
                startActivity(i);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout()
    {
        if(auth.getCurrentUser()!=null)
        {
            auth.signOut();
            Intent i = new Intent(ProfileActivity.this,SignInActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 14 && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            CropImage.activity(uri).start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Log.d("result_code",String.valueOf(resultCode));


            if (resultCode ==RESULT_OK) {
                resultUri = result.getUri();
                userImgView.setImageURI(resultUri);

                // now upload the image to firebase storage
                progressBar.setVisibility(View.VISIBLE);
                savePicture();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("error","error");
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void savePicture()  // saving the image in firebase storage

    {
        final StorageReference filePath = firebaseStorageReference.child(firebaseUser.getUid() + ".jpg");
        filePath.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                databaseReferenceUsers.child("imageUrl").setValue(uri.toString())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(),"Profile image updated successfully",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });



                    }
                });
    }
}
