package com.gymtrainer.gymuserapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gymtrainer.gymuserapp.Adapters.TrainerAdapter;
import com.gymtrainer.gymuserapp.Model.Trainer;
import com.gymtrainer.gymuserapp.Model.TrainerId;
import com.gymtrainer.gymuserapp.Model.User;
import com.gymtrainer.gymuserapp.R;
import com.gymtrainer.gymuserapp.Utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainersActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {


    GoogleMap mMap;
    ArrayList<Trainer> arrayListTrainers;
    DatabaseReference referenceTrainerId,referenceTrainers,databaseReferenceUsers;
    String categoryId,categoryName;
    ArrayList<String> arrayListTrainersId;
    Toolbar toolbarTrainers;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    View mCustomMarkerView;
    CircleImageView mMarkerImageView;

    private final int REQUEST_CHECK_SETTINGS =100;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLngCurrent;
    HashMap<String,Marker> hashMap;
    HashMap<Marker,Trainer> hashMapTrainers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainers);
        getIntentData();
        init();



    }



    private void init()
    {
        auth = FirebaseAuth.getInstance();
            hashMapTrainers = new HashMap<>();

        firebaseUser = auth.getCurrentUser();
        hashMap = new HashMap<>();
        toolbarTrainers = (Toolbar)findViewById(R.id.toolbarTrainers);
        toolbarTrainers.setTitle(categoryName);

        mCustomMarkerView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        mMarkerImageView = (CircleImageView) mCustomMarkerView.findViewById(R.id.profile_image);

        setSupportActionBar(toolbarTrainers);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        arrayListTrainers = new ArrayList<>();
        arrayListTrainersId = new ArrayList<>();

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(TrainersActivity.this)
                .addOnConnectionFailedListener(TrainersActivity.this)
                .build();
        client.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        referenceTrainerId = FirebaseDatabase.getInstance().getReference().child("Categories").child(categoryId).child("TrainerId");
        referenceTrainers = FirebaseDatabase.getInstance().getReference().child("Users").child("Trainers");
        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference().child("Users").child("Users").child(firebaseUser.getUid());


    }

    private void getIntentData()
    {
        Intent i = getIntent();
        if(i!=null) {
            categoryId = i.getStringExtra("categoryId");
            categoryName = i.getStringExtra("categoryName");

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void openTrainerDetail(ArrayList<Trainer> arrayListTrainers, int adapterPosition)
    {
        Trainer trainer = arrayListTrainers.get(adapterPosition);

            Intent i = new Intent(TrainersActivity.this,TrainersDetailActivity.class);
            i.putExtra("trainerObj",trainer);
            i.putExtra("categoryName",categoryName);
            startActivity(i);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;

        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);
        builder.setAlwaysShow(true);
        PendingResult result =
                LocationServices.SettingsApi.checkLocationSettings(
                        client,
                        builder.build()
                );
        result.setResultCallback(this);


        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  GPS turned off, Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(TrainersActivity.this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show dialog
                }
                break;



            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
        LocationServices.FusedLocationApi.removeLocationUpdates(client,this);


        Toast.makeText(getApplicationContext(),"Showing nearby trainers",Toast.LENGTH_LONG).show();

        displayNearbyTrainers();


    }


    private void displayNearbyTrainers()
    {
        referenceTrainerId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayListTrainersId.clear();
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dss:dataSnapshot.getChildren())
                    {
                        TrainerId trainerId = dss.getValue(TrainerId.class);
                        arrayListTrainersId.add(trainerId.getTrainerid());
                    }

                    referenceTrainers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayListTrainers.clear();
                            //   Toast.makeText(getApplicationContext(),"Trainer id size = "+arrayListTrainersId.size(),Toast.LENGTH_LONG).show();

                            for(int i=0;i<arrayListTrainersId.size();i++)
                            {
                                final Trainer trainer=  dataSnapshot.child(arrayListTrainersId.get(i)).getValue(Trainer.class);
                               final LatLng latLngTrainer = new LatLng(Double.parseDouble(trainer.getLat()),Double.parseDouble(trainer.getLng()));

                               if(Utils.isTrainerNearby(latLngCurrent,latLngTrainer))
                               {
                                   arrayListTrainers.add(trainer);

                                   Glide.with(getApplicationContext())
                                           .load(trainer.getImageUrl())
                                           .asBitmap()
                                           .error(R.mipmap.ic_launcher)
                                          .placeholder(R.mipmap.ic_launcher)
                                           .into(new SimpleTarget<Bitmap>() {
                                                     @Override
                                                     public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                                                            Log.d("resource123","In onResource Ready");
                                                          Marker myMarker =    mMap.addMarker(new MarkerOptions()
                                                             .position(latLngTrainer)
                                                             .title(trainer.getName())
                                                             .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView,resource))));

                                                                  hashMap.put(myMarker.getTitle(),myMarker);
                                                          hashMapTrainers.put(myMarker,trainer);

                                                     }
                                                 });

//                                                   MarkerOptions markerOptions = new MarkerOptions();
//                                   markerOptions.title(arrayListTrainers.get(i).getName());
//                                   markerOptions.position(latLngTrainer);
//                                   markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//                                   Marker myMarker = mMap.addMarker(markerOptions);
//                                   hashMap.put(myMarker.getTitle(),myMarker);
//
//                                   hashMapTrainers.put(myMarker,arrayListTrainers.get(i));
                               }



                            }



                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLngCurrent.latitude, latLngCurrent.longitude), 13.0f));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });



                }
                else
                {
                        Toast.makeText(getApplicationContext(),"No nearby trainer found",Toast.LENGTH_LONG).show();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();

             //   progressBarTrainers.setVisibility(View.GONE);

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (client.isConnected()) {
            client.disconnect();
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }

        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        Trainer trainer = hashMapTrainers.get(marker);

        Intent i = new Intent(TrainersActivity.this,TrainersDetailActivity.class);
        i.putExtra("trainerObj",trainer);
        i.putExtra("categoryName",categoryName);
        startActivity(i);

        return false;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

    }

    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {

        mMarkerImageView.setImageBitmap(bitmap);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }


}
