package com.gymtrainer.gymuserapp.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.gymtrainer.gymuserapp.R;

public class SplashActivity extends AppCompatActivity {
    protected boolean _active = true;
    protected int _splashTime = 1000;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();

        runThread();
    }
    private void runThread()
    {
        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {

                } finally
                {

                    if(auth.getCurrentUser()==null)
                    {
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                        finish();
                    }
                    else
                    {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                        finish();
                    }

                }
            };
        };
        splashTread.start();
    }

}
