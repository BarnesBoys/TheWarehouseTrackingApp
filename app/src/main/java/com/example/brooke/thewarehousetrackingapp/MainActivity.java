package com.example.brooke.thewarehousetrackingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() { super.onStart();}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //https://api.vworkapp.com/v4/jobs.xml?api_key=oo9E3tbGBEeEGqqUS2Nk
    //What we need to implement the api key, but where?

    //Todo: create the basic workflow for the main activity assuming already logged in, i.e. onClose(), onCreate() methods.

    //Todo: West - Firebase Push notifications logic


}
