package com.example.brooke.thewarehousetrackingapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //declaring the buttons and setting their text-fields to transparent
        Button deliverBtn = (Button)findViewById(R.id.deliveredBtn);
        deliverBtn.setTextColor(Color.TRANSPARENT);
        Button transitBtn = (Button)findViewById(R.id.inTransitBtn);
        transitBtn.setTextColor(Color.TRANSPARENT);
        Button undefBtn = (Button)findViewById(R.id.tbcBtn);
        undefBtn.setTextColor(Color.TRANSPARENT);


        //setting listeners for all three buttons to go to the tracking page when clicked

        //undefined button is to be our third button that opens up the tracking page
        undefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingNumber = "8963062900191401HAM003DN";
                startActivity(new Intent(MainActivity.this, TrackingPage.class).putExtra("tracking_reference", trackingNumber));
            }
        });

        //transit button is to display the parcel "in transit/out for delivery"
        transitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingNumber = "8963062900186301HAM003DS";
                startActivity(new Intent(MainActivity.this, TrackingPage.class).putExtra("tracking_reference", trackingNumber));
            }
        });

        //deliver button is to display the parcel as delivered
        deliverBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String trackingNumber = "2809330617685110HAM001DS";

                startActivity(new Intent(MainActivity.this, TrackingPage.class).putExtra("tracking_reference", trackingNumber));
            }
        });
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

    //just some more comments

    //Todo: create the basic workflow for the main activity assuming already logged in, i.e. onClose(), onCreate() methods.

    //Todo: West - Firebase Push notifications logic


}
