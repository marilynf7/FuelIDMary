package com.example.fuelid;

import com.logixsoft.fuelid.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
 
    public class SplashScreen extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private Settings SettingsHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SettingsHelper = new Settings(this); 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
              	Cursor set = SettingsHelper.getSettings();
            	set.moveToFirst();
            	if(set.getCount()>0){
                Intent i = new Intent(SplashScreen.this, MainMenu.class);
                startActivity(i);}else{
                Intent i = new Intent(SplashScreen.this, FirstSettingsMod.class);
                startActivity(i);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}