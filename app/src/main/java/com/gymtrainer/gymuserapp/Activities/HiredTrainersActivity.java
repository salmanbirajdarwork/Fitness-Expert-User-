package com.gymtrainer.gymuserapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gymtrainer.gymuserapp.Adapters.HireAdapter;
import com.gymtrainer.gymuserapp.Model.Hire;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;

import java.util.ArrayList;
import java.util.List;

public class HiredTrainersActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Hire> hireList;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReferenceHire,databaseReferenceUser;
    HireAdapter hireAdapter;
    Toolbar toolbar;
    ProgressBar progressBar;
    TextView notFoundTxt;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hired_trainers);
        init();
        setListeners();

    }

    private void init()
    {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewHiredTrainers);
        recyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar)findViewById(R.id.progressBarHiredTrainers);
        notFoundTxt = (TextView)findViewById(R.id.notfoundTxt);
        hireList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hireAdapter = new HireAdapter(hireList,HiredTrainersActivity.this);
        recyclerView.setAdapter(hireAdapter);

        toolbar = (Toolbar)findViewById(R.id.hiredTrainerToolbar);
        toolbar.setTitle("Hired Trainers");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        databaseReferenceHire = FirebaseDatabase.getInstance().getReference().child("Hire");
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Users").child("Users").child(firebaseUser.getUid());

    }


    private void setListeners()
    {
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
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



        databaseReferenceHire.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    hireList.clear();
                    for(DataSnapshot dss:dataSnapshot.getChildren()) {
                        String key = dss.getKey();
                        Log.d("KeyChild", key);
                        if (user != null) {
                            if (key.contains(user.getUserid())) {
                                Hire hire = dss.getValue(Hire.class);
                                hireList.add(hire);

                            }
                        }
                        else {
                            notFoundTxt.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    hireAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    notFoundTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                notFoundTxt.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });


    }



    public void openNewActivity(String trainerId, ArrayList<String> hourList,String categoryName,String userId,String date)
    {
        Intent i = new Intent(HiredTrainersActivity.this,HiredTrainerDetailActivity.class);
        i.putExtra("trainerId",trainerId);
        i.putExtra("categoryName",categoryName);
        i.putExtra("userId",userId);
        i.putExtra("date",date);
        i.putStringArrayListExtra("hoursList",hourList);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
