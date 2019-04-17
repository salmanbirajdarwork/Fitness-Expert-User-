package com.gymtrainer.gymuserapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gymtrainer.gymuserapp.Model.Hire;
import com.gymtrainer.gymuserapp.Model.Trainer;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;
import com.gymtrainer.gymuserapp.Utils.GmailSender;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainersDetailActivity extends AppCompatActivity {

    TextView textViewName,textViewCity,textViewGender,textViewCategory,textViewExperience,textViewAvailibilityHrs,textViewTrail;
    Toolbar toolbar;
    User user;
    ArrayList<String> arrayListCategories,arrayListWorkingHrs;
    ArrayList<String> selectedHoursList;
    Trainer trainer;
    DatabaseReference databaseReferenceCategories,databaseReferenceWorkingHrs,databaseReferenceUser,databaseReferenceHire;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    ArrayList<Integer> mHourItem = new ArrayList<>();
    String categoryName;

    Handler handler = new Handler();
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers_detail);
        getIntentData();
        init();


        setData();



        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    user = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


            fetchCategories();
            fetchWorkingHrs();




    }

    private void getIntentData()
    {
        if(getIntent()!=null)
        {
            trainer=(Trainer)getIntent().getSerializableExtra("trainerObj");
            categoryName = getIntent().getStringExtra("categoryName");
        }
    }

    private void fetchWorkingHrs()
    {
        databaseReferenceWorkingHrs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    arrayListWorkingHrs.clear();
                    if(dataSnapshot.exists())
                    {
                        for(DataSnapshot dss:dataSnapshot.getChildren())
                        {
                            arrayListWorkingHrs.add(dss.getValue(String.class));
                        }


                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i=0;i<arrayListWorkingHrs.size();i++)
                        {
                            stringBuilder.append(arrayListWorkingHrs.get(i)+ ",");
                        }


                        if(stringBuilder.length()>0)
                        {
                            textViewAvailibilityHrs.setText(stringBuilder.toString());
                        }


                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchCategories()
    {
        databaseReferenceCategories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListCategories.clear();
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss:dataSnapshot.getChildren())
                    {
                        arrayListCategories.add(dss.getValue(String.class));
                    }

                    StringBuilder builder = new StringBuilder();

                    for(int i=0;i<arrayListCategories.size();i++)
                    {
                        builder.append(arrayListCategories.get(i)+ ",");
                    }


                    if(builder.length()>0)
                    {
                        textViewCategory.setText(builder.toString());
                    }


                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Could not fetch profile properly.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init()
    {
        selectedHoursList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        circleImageView = (CircleImageView)findViewById(R.id.trainerImgView);
        toolbar = (Toolbar)findViewById(R.id.toolbarTrainerDetail);
        textViewName = (TextView)findViewById(R.id.nameTrainerTxt);
        textViewCity = (TextView)findViewById(R.id.cityTrainerTxt);
        textViewGender = (TextView)findViewById(R.id.genderTrainerTxt);
        textViewCategory = (TextView)findViewById(R.id.categoriesTrainerTxt);
        textViewExperience = (TextView)findViewById(R.id.experienceTrainerTxt);
        textViewAvailibilityHrs = (TextView)findViewById(R.id.workingHrsTrainerTxt);
        textViewTrail = (TextView) findViewById(R.id.trailTrainerTxt);


        arrayListCategories = new ArrayList<>();
        arrayListWorkingHrs = new ArrayList<>();

        databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Trainers").child(trainer.getTrainerid()).child("Categories");

        databaseReferenceWorkingHrs = FirebaseDatabase.getInstance().getReference().child("Users")
                .child("Trainers").child(trainer.getTrainerid()).child("WorkingHrs");

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Users").child(firebaseUser.getUid());
        databaseReferenceHire = FirebaseDatabase.getInstance().getReference().child("Hire");
    }

    private void setData()
    {
        if(trainer!=null)
        {
            textViewName.setText(trainer.getName());
            textViewCity.setText(trainer.getCity());
            textViewGender.setText(trainer.getGender());
            textViewExperience.setText(trainer.getExperience());
            textViewTrail.setText(trainer.getTrailtraining());
            Picasso.get().load(trainer.getImageUrl()).placeholder(R.drawable.ic_launcher_man).into(circleImageView);

            if(trainer.getTrailtraining().equals("true"))
            {
                textViewTrail.setText("3 Days Trial");
            }
            else
            {
                textViewTrail.setText("No trail");
            }

            toolbar.setTitle(trainer.getName());
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null){

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hiremenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            finish();
        }

        if(item.getItemId() == R.id.menuHire)
        {
            hireTrainer();

        }
        return super.onOptionsItemSelected(item);
    }

    private void hireTrainer()
    {
        String[] availableHrs = new String[arrayListWorkingHrs.size()];
        availableHrs = arrayListWorkingHrs.toArray(availableHrs);

        //  final String[] availableHrs = (String[]) arrayListCategories.toArray();
        final boolean[] checkedHourItem = new boolean[availableHrs.length];


        AlertDialog.Builder builder = new AlertDialog.Builder(TrainersDetailActivity.this);
        builder.setTitle("Choose hiring hours");
        builder.setMultiChoiceItems(availableHrs, checkedHourItem, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked) {
                    if (!mHourItem.contains(which)) {
                        mHourItem.add(which);
                    }
                }
                else {
                    mHourItem.remove((Integer)which);
                }
            }
        });

        builder.setCancelable(false);
        final String[] finalAvailableHrs = availableHrs;
        builder.setPositiveButton("Hire", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item= "";
                for(int i=0;i<mHourItem.size();i++)
                {
                    item = item + finalAvailableHrs[mHourItem.get(i)];

                    if(i!=mHourItem.size() -1)
                    {
                        item = item + ", ";
                    }
                }

                if(item.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Please choose atleast one hour",Toast.LENGTH_LONG).show();
                }
                else
                {
                    selectedHoursList.clear();
                    for(int i=0;i<mHourItem.size();i++)
                    {
                        selectedHoursList.add(finalAvailableHrs[mHourItem.get(i)]);
                    }

                }

                Date date = Calendar.getInstance().getTime();

                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(date);

                final Hire hire = new Hire(user.getUserid(),trainer.getTrainerid(),categoryName,trainer.getName(),user.getName(),trainer.getImageUrl(),trainer.getRate(),selectedHoursList,formattedDate);


                databaseReferenceHire.child(user.getUserid()+trainer.getTrainerid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            Toast.makeText(getApplicationContext(),"You have already hired this trainer.",Toast.LENGTH_LONG).show();
                        }

                        else
                        {
                            databaseReferenceHire.child(user.getUserid()+trainer.getTrainerid()).setValue(hire)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                updateWorkingHrs();


                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Could not hire the trainer. Try again later",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0;i<checkedHourItem.length;i++)
                {
                    checkedHourItem[i] = false;
                }
                mHourItem.clear();
                selectedHoursList.clear();
            }
        });

        AlertDialog mDialog = builder.create();
        mDialog.show();
    }

    private void updateWorkingHrs()
    {
        final DatabaseReference databaseReferenceWorkingHrs = FirebaseDatabase.getInstance().getReference().child("Users").child("Trainers")
                .child(trainer.getTrainerid()).child("WorkingHrs");

        for(int i=0;i<selectedHoursList.size();i++)
        {
            Query query = databaseReferenceWorkingHrs.orderByValue().equalTo(selectedHoursList.get(i));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                        dataSnapshot.getChildren().iterator().next().getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {

                                            Toast.makeText(getApplicationContext(),"You have successfully hired this trainer.",Toast.LENGTH_LONG).show();
                                            sendEmailToTrainer();
                                            sendEmailToUser();

                                            Intent i = new Intent(TrainersDetailActivity.this,HomeActivity.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Could not hire the trainer. Try again later",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void sendEmailToTrainer()
    {
        sendMail("GOOD NEWS!!! You have been hired for Gym Training.","Dear Trainer. You have been hired by a user. The details are given below:\n\nName:"+user.getName()+"\n\nUser email address:"+user.getEmail()+"\n\n" +
                "User address: "+user.getAddress()+ "\n\nUser Number: "+user.getPhonenumber() + "\n\n" +
                "User Age:"+user.getAge(),trainer.getEmail());


    }

    private void sendMail(final String subject, final String body, final String receipient)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.

                Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        new Thread(new Runnable() {

            public void run() {
                GmailSender sender = new GmailSender("gymtrainerapp2019@gmail.com", "Gym-1234");
                sender.sendMail(subject, body, "gymtrainerapp2019@gmail.com", receipient);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"An email has been sent to your email address.",Toast.LENGTH_LONG).show();
                    }
                });
            }

        }).start();

    }

    private void sendEmailToUser()
    {
        sendMail("Trainer Hired Successfully","Dear User. You have successfully hired the trainer. The details are given below:\n\nName:"+trainer.getName()+"\n\nEmail:"+trainer.getEmail()+"\n\n" +
                "Address: "+trainer.getAddress()+ "\n\nNumber: "+trainer.getPhonenumber() + "\n\n" +
                "City:"+trainer.getCity(),user.getEmail());
    }



}
