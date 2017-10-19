package com.example.brooke.thewarehousetrackingapp;


import android.content.Intent;

import android.os.AsyncTask;
import android.support.v7.app.*;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class TrackingPage extends AppCompatActivity {

    protected String trackingNumber = null;
    boolean tick1,tick2,tick3 = false;
    ImageView image1, image2, image3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_page);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent == null) {
            trackingNumber = "";
        } else {
            Bundle extras = intent.getExtras();
            if (extras == null) trackingNumber = "";
            else trackingNumber = intent.getExtras().getString("tracking_reference");
        }

        //to test tracking number has been set
        //System.err.println("tracking_ref is:  " + trackingNumber);

        //Establish textviews
        TextView status = (TextView) this.findViewById(R.id.status);
        TextView status2 = (TextView) this.findViewById(R.id.status2);
        TextView status3 = (TextView) this.findViewById(R.id.status3);
        TextView event = (TextView) this.findViewById(R.id.event);
        TextView event2 = (TextView) this.findViewById(R.id.event2);
        TextView event3 = (TextView) this.findViewById(R.id.event3);
        TextView dateTime = (TextView) this.findViewById(R.id.dateTime);
        TextView dateTime2 = (TextView) this.findViewById(R.id.dateTime2);
        TextView dateTime3 = (TextView) this.findViewById(R.id.dateTime3);

        image1 = (ImageView) findViewById(R.id.imageView1);
        image2 = (ImageView) findViewById(R.id.imageView2);
        image3 = (ImageView) findViewById(R.id.imageView3);


        //send textviews to getTrackingDetails class
        new getTrackingDetails(status, status2, status3, event, event2, event3, dateTime, dateTime2, dateTime3).execute();

        //This small delay is so the images update correctly
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //If stages have been complete put a tick on screen
        if (tick1) {
            image1.setImageResource(R.drawable.tick);
            image1.setVisibility(View.VISIBLE);
        }
        if (tick2) {
            image2.setImageResource(R.drawable.tick);
            image2.setVisibility(View.VISIBLE);
        }
        if (tick3) {
            image3.setImageResource(R.drawable.tick);
            image3.setVisibility(View.VISIBLE);
        }
    }

    private class getTrackingDetails extends AsyncTask<String, String, String>{

        String result = "";
        TextView status,status2,status3,event,event2,event3,dateTime,dateTime2,dateTime3,signature;

        //ImageView signatureImage;

        //class constructor
        getTrackingDetails(TextView status, TextView status2, TextView status3, TextView event, TextView event2, TextView event3, TextView dateTime,
                           TextView dateTime2, TextView dateTime3){
            this.status = status; this.status2 = status2; this.status3=status3;
            this.event = event; this.event2=event2; this.event3=event3;
            this.dateTime = dateTime; this.dateTime2=dateTime2; this.dateTime3=dateTime3;
        }

        protected String doInBackground(String... params){

            try {
                //establish connection nzpost
                URL url = new URL("https://oauth.nzpost.co.nz/as/token.oauth2");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, null, new java.security.SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());

                //set request properties
                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);

                //transfer the authorization details as needed
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes("grant_type=client_credentials&client_id=ccc7bf438e5f440dba9b0194b75a9fca&client_secret=dD021693C7f0410AbdAb48E63fD884c3");
                out.flush();
                out.close();

                /* this code is to see if the response is alright
                int responseCode = conn.getResponseCode();
                System.err.println("code is: --------> " + responseCode);*/

                //collect returned data
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                String json = "";
                while((line = in.readLine()) != null){
                    System.err.println(line);
                    json += line;
                }
                in.close();

                //put returned data in json object
                JSONObject obj = new JSONObject(json);
                //extract the necessary access_token for interaction with nzpost api
                String accessToken = obj.getString("access_token");
                System.err.println(accessToken);
                System.err.println("<------" + trackingNumber + "------>");
                String holdParcelURL = "https://api.nzpost.co.nz/parceltrack/3.0/parcels/" + trackingNumber;

                url = new URL(holdParcelURL);
                conn = (HttpsURLConnection) url.openConnection();
                sc = SSLContext.getInstance("TLS");
                sc.init(null, null, new java.security.SecureRandom());
                conn.setSSLSocketFactory(sc.getSocketFactory());

                conn.setReadTimeout(7000);
                conn.setConnectTimeout(7000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setRequestProperty("client_id", "ccc7bf438e5f440dba9b0194b75a9fca");

                int responseCode = conn.getResponseCode();
                String responseMessage = "";

                //printing out the header to see what is being set
                Map<String, List<String>> map = conn.getHeaderFields();

                for(Map.Entry<String, List<String>> entry : map.entrySet()){
                    System.err.println(entry.getKey() + " : " + entry.getValue());
                }

                //return the actual json data or the error json data
                if(responseCode != 200) in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                else in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((line = in.readLine()) !=null){
                    responseMessage += line;
                }

                //parsing the returned data
                obj = new JSONObject(responseMessage);
                System.out.println("tracking obj "+obj);
                JSONArray trackingEvents = obj.getJSONObject("results").getJSONArray("tracking_events") ;
                JSONObject lastEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);

                System.out.println("tracking "+trackingEvents);

                //establish the different states of parcel, parcel is picked up
                if(lastEvent.getString("status_description").equals("Picked up")){
                    System.out.println("tracking events length "+trackingEvents.length());
                    JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);
                    getPickedUpEvent(pickedUpEvent);
                    tick1 = true;
                }
                //parcel is in transit
                if(lastEvent.getString("status_description").equals("With courier for delivery")) {
                    System.out.println("tracking events length "+trackingEvents.length());
                    JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 2);
                    getPickedUpEvent(pickedUpEvent);
                    JSONObject transitEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);
                    getTransitEvent(transitEvent);
                    tick1 = true; tick2 = true;
                }
                //parcel is delivered
                if(lastEvent.getString("status_description").equals("Delivered")) {
                    System.out.println("tracking events length "+trackingEvents.length()+" "+trackingEvents);
                    JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 3);
                    getPickedUpEvent(pickedUpEvent);
                    JSONObject transitEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 2);
                    getTransitEvent(transitEvent);
                    JSONObject deliveredEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);
                    getDeliveredEvent(deliveredEvent);
                    tick1 = true; tick2 = true; tick3 = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        //---------------------------------------------------------------------
        //Methods for extracting the results from tracking events to display
        //---------------------------------------------------------------------

        //use jason to get info about transit status
        void getPickedUpEvent(JSONObject pickedUpEvent){
            try {
                String resultPickedUpEvent = pickedUpEvent.getString("event_description");
                String resultPickedUpStatus = pickedUpEvent.getString("status_description");
                String resultPickedUpDateTime = pickedUpEvent.getString("event_datetime");
                result += resultPickedUpStatus + "///" + resultPickedUpEvent + "///" + resultPickedUpDateTime + "///";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //use jason to get info about transit status
        void getTransitEvent(JSONObject transitEvent){
            try {
                String resultTransitEvent = transitEvent.getString("event_description");
                String resultTransitStatus = transitEvent.getString("status_description");
                String resultTransitDateTime = transitEvent.getString("event_datetime");
                result += resultTransitStatus + "///" + resultTransitEvent + "///" + resultTransitDateTime + "///";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //use jason to get info about delivered status
        void getDeliveredEvent(JSONObject deliveredEvent){
            try {
                String resultDeliveredEvent = deliveredEvent.getString("event_description");
                String resultDeliveredStatus = deliveredEvent.getString("status_description");
                String resultDeliveredDateTime = deliveredEvent.getString("event_datetime");
                result += resultDeliveredEvent + "///" + resultDeliveredStatus + "///" + resultDeliveredDateTime;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //On post execute method
        protected void onPostExecute(String st){

            String[] result = st.split("///");
            //
            if(result.length == 3) {
                String[] pickedUpDateTime = result[2].split("T");
                dateTime.setText(pickedUpDateTime[0] + " " + pickedUpDateTime[1]);
                status.setText(result[0]);
                event.setText(result[1]);
            }
            //
            if(result.length == 6) {
                String[] transitDateTime = result[2].split("T");
                String[] resultDateTime2 = result[5].split("T");
                dateTime2.setText(transitDateTime[0] + " " + transitDateTime[1]); dateTime2.setVisibility(View.VISIBLE);
                dateTime.setText(resultDateTime2[0] + " " + resultDateTime2[1]);
                status2.setText(result[0]); status.setText(result[3]); status2.setVisibility(View.VISIBLE);
                event2.setText(result[1]); event.setText(result[4]); event2.setVisibility(View.VISIBLE);
            }
            //
            if(result.length == 9) {
                String[] resultDateTime = result[2].split("T");
                String[] resultDateTime2 = result[5].split("T");
                String[] resultDateTime3 = result[8].split("T");
                dateTime3.setText(resultDateTime[0] + " " + resultDateTime[1]);
                dateTime2.setText(resultDateTime2[0] + " " + resultDateTime2[1]);
                dateTime.setText(resultDateTime3[0] + " " + resultDateTime3[1]); dateTime2.setVisibility(View.VISIBLE); dateTime3.setVisibility(View.VISIBLE);
                status3.setText(result[0]); status2.setText(result[3]); status.setText(result[7]); status2.setVisibility(View.VISIBLE); status3.setVisibility(View.VISIBLE);
                event3.setText(result[1]); event2.setText(result[4]); event.setText(result[6]); event2.setVisibility(View.VISIBLE); event3.setVisibility(View.VISIBLE);
            }
        }
    }
}


