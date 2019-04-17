package com.gymtrainer.gymuserapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gymtrainer.gymuserapp.Adapters.HomeAdapter;
import com.gymtrainer.gymuserapp.Model.Category;
import com.gymtrainer.gymuserapp.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Category> arrayListCategories;
    HomeAdapter homeAdapter;
    DatabaseReference databaseReferenceCategories;
    ProgressBar progressBarHome;
    Toolbar toolbar;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

    }

    private void init() {
        toolbar = (Toolbar)findViewById(R.id.toolbarHome);
        toolbar.setTitle("Home");
        auth = FirebaseAuth.getInstance();

        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewHome);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBarHome = (ProgressBar)findViewById(R.id.progressBarHome);
        arrayListCategories = new ArrayList<>();
        setRecyclerView();
        databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("Categories");



        databaseReferenceCategories.addValueEventListener(new ValueEventListener() {  // fetching all the categories data from firebase

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListCategories.clear();
                for(DataSnapshot dss: dataSnapshot.getChildren())
                {
                    Category category = dss.getValue(Category.class);
                    arrayListCategories.add(category);
                }

                progressBarHome.setVisibility(View.GONE);
                homeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



    }

    private void setRecyclerView()
    {
        homeAdapter = new HomeAdapter(this,arrayListCategories);
        recyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();
    }

    public void openRelevantTrainers(int adapterPosition, ArrayList<Category> arrayListCategories)
    {
        Category category = arrayListCategories.get(adapterPosition);
        Intent i = new Intent(HomeActivity.this,TrainersActivity.class);
        i.putExtra("categoryId",category.getCategoryId());
        i.putExtra("categoryName",category.getCategoryName());
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.menuProfile)
        {
            Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
            startActivity(intent);

        }

        if(item.getItemId() == R.id.menuHired)
        {
                        Intent intent = new Intent(HomeActivity.this,HiredTrainersActivity.class);
                        startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



}
