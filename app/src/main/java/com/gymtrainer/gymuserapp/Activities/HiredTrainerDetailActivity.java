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
import com.google.firebase.database.ValueEventListener;
import com.gymtrainer.gymuserapp.Model.Hire;
import com.gymtrainer.gymuserapp.Model.Trainer;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;
import com.gymtrainer.gymuserapp.Utils.GmailSender;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HiredTrainerDetailActivity extends AppCompatActivity {

    CircleImageView hiredTrainerImgView;
    TextView hiredTrainerName,hiredTrainerEmail,hiredTrainerGender,hiredTrainerPhoneNumber,
            hiredTrainerAddress,hiredTrainerHours,hiredTrainerDate,hiredTrainerRate,hiredTrainerCategory;
    ArrayList<String> hoursList;
    DatabaseReference databaseReferenceTrainer,databaseReferenceHire,databaseReferenceUser;
    String trainerId,categoryName,userId,date;
    ArrayList<String> newWorkingHrsList;
    Toolbar toolbar;
    Handler handler = new Handler();
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    Trainer myTrainer;
    User user;
    Hire hire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hired_trainer_detail);
        init();
        getData();   // getting data from previous activity
        setTrainerData();   // getting the trainer data and setting it

    }
    private void init()
    {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        toolbar = (Toolbar)findViewById(R.id.toolbarHiredTrainerDetail);
        hiredTrainerImgView = (CircleImageView)findViewById(R.id.hiredTrainerImg);
        hiredTrainerName = (TextView)findViewById(R.id.hiredTrainerName);
        hiredTrainerEmail = (TextView)findViewById(R.id.hiredTrainerEmail);
        hiredTrainerGender = (TextView)findViewById(R.id.hiredTrainerGender);
        hiredTrainerPhoneNumber = (TextView)findViewById(R.id.hiredTrainerPhoneNumber);
        hiredTrainerAddress = (TextView)findViewById(R.id.hiredTrainerAddress);
        hiredTrainerHours = (TextView)findViewById(R.id.hiredTrainerHours);
        hiredTrainerDate = (TextView)findViewById(R.id.hiredTrainerDate);
        hiredTrainerRate = (TextView)findViewById(R.id.hiredTrainerRate);
        hiredTrainerCategory = (TextView)findViewById(R.id.hiredTrainerCategory);


        newWorkingHrsList = new ArrayList<>();
        hoursList = new ArrayList<>();



        databaseReferenceTrainer = FirebaseDatabase.getInstance().getReference().child("Users").child("Trainers");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Users").child(firebaseUser.getUid());
        databaseReferenceHire = FirebaseDatabase.getInstance().getReference().child("Hire");

        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    user = dataSnapshot.getValue(User.class);   // fetching the user (current)
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



    }



    private void setTrainerData()
    {
        databaseReferenceTrainer.child(trainerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                     myTrainer = dataSnapshot.getValue(Trainer.class);
                    if(myTrainer!=null)
                    {
                        hiredTrainerName.setText(myTrainer.getName());
                        hiredTrainerEmail.setText(myTrainer.getEmail());
                        hiredTrainerGender.setText(myTrainer.getGender());
                        hiredTrainerPhoneNumber.setText(myTrainer.getPhonenumber());
                        hiredTrainerAddress.setText(myTrainer.getAddress());
                        hiredTrainerDate.setText(date);
                        hiredTrainerCategory.setText(categoryName);
                        hiredTrainerRate.setText(myTrainer.getRate()+ "$");
                        toolbar.setTitle(myTrainer.getName());
                        setSupportActionBar(toolbar);
                        if (getSupportActionBar() != null){

                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                        }
                        Picasso.get().load(myTrainer.getImageUrl()).placeholder(R.drawable.ic_launcher_man).into(hiredTrainerImgView);
                        StringBuilder stringBuilder = new StringBuilder();


                        for(int i=0;i<hoursList.size();i++)
                        {
                            stringBuilder.append(hoursList.get(i) + ",");
                        }

                        if(stringBuilder.length()>0)
                        {
                            hiredTrainerHours.setText(stringBuilder.toString());
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }



    private void getData()    // getting data from previous activity
    {
        trainerId = getIntent().getStringExtra("trainerId");
        userId = getIntent().getStringExtra("userId");
        date = getIntent().getStringExtra("date");
        categoryName = getIntent().getStringExtra("categoryName");
        hoursList = getIntent().getStringArrayListExtra("hoursList");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.unhiremenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            finish();
        }

        if(item.getItemId() == R.id.menuUnHire)
        {
            displayDialog();

        }
        return super.onOptionsItemSelected(item);
    }

    private void displayDialog()  // display unhire dialog using alert dialog
    {
        AlertDialog alertDialog = new AlertDialog.Builder(HiredTrainerDetailActivity.this).create();
        alertDialog.setTitle("Unhire Trainer?");
        alertDialog.setMessage("Are you sure you want to unhire the trainer?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "UNHIRE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        unhireTrainer();

                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void unhireTrainer()   // unhire the trainer
    {
        databaseReferenceHire.child(userId+trainerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {


                   hire = dataSnapshot.getValue(Hire.class);

                    dataSnapshot.getRef().setValue(null);


                    // get the list from firebase and append it


                    addBackWorkingHrs();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });





    }

    private void addBackWorkingHrs()
    {

        databaseReferenceTrainer.child(trainerId).child("WorkingHrs")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            newWorkingHrsList.clear();

                            for(DataSnapshot dss:dataSnapshot.getChildren())
                            {
                                newWorkingHrsList.add(dss.getValue(String.class));
                            }

                            Log.d("before1","size = "+newWorkingHrsList.size());

                            newWorkingHrsList.addAll(hoursList);


                            Log.d("before2","size = "+newWorkingHrsList.size());


                            if(newWorkingHrsList.size()>0)
                            {
                                databaseReferenceTrainer.child(trainerId).child("WorkingHrs")
                                        .setValue(newWorkingHrsList)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(getApplicationContext(),"Trainer unhired successfully",Toast.LENGTH_LONG).show();
                                                    String price = null;
                                                    try {
                                                        price = findHirePrice();
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                    sendMailToUser(price);
                                                   sendMailToTrainer(price);


                                                    Intent i = new Intent(HiredTrainerDetailActivity.this,HomeActivity.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(i);
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(),"Could not unhire trainer, Try again later",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Could not update trainer working hours,Try again",Toast.LENGTH_LONG).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Could not unhire trainer,Try again",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });


    }

    private void sendMailToTrainer(String price)
    {
        sendMail("You have been unhired for Gym Training.","Dear Trainer. You have been unhired by the user. The details are given below:\n\nName:"+user.getName()+"\n\nEmail:"+user.getEmail()+"\n\n" +
                "Address: "+user.getAddress()+ "\n\nNumber: "+user.getPhonenumber() + "\n\n" +
                "User Age:"+user.getAge() + "\n\nTOTAL FEE = "+price,myTrainer.getEmail());


    }

    private void sendMailToUser(String price)
    {
        sendMail("Trainer unhired Successfully","Dear User. You have successfully unhired the trainer. The details are given below:\n\nName:"+myTrainer.getName()+"\n\nEmail:"+myTrainer.getEmail()+"\n\n" + "\n\nNumber: "+myTrainer.getPhonenumber() + "\n\n" +
                "City:"+myTrainer.getCity() + "\n\nPRICE = "+price,user.getEmail());
    }

    private String findHirePrice() throws ParseException {
        Date date = Calendar.getInstance().getTime();

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        String formattedDate = df.format(date);

        Date currentDate = df.parse(formattedDate);


        String hiredDateStr = hire.getDate();

        Date hiredDate = df.parse(hiredDateStr);

        long difference = currentDate.getTime() - hiredDate.getTime();
        float daysBetween = (difference / (1000*60*60*24));

        int days = (int)Math.round(daysBetween);
        int rate = Integer.parseInt(hire.getRate());

        int price = days * rate;


        String priceStr = String.valueOf(price);
        return priceStr;
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
}
