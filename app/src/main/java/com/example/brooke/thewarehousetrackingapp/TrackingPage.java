package com.example.brooke.thewarehousetrackingapp;


import android.content.Intent;

import android.os.AsyncTask;
import android.support.v7.app.*;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

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
    //todo: changing variables
    //boolean tick1,tick2,tick3 = false;
    TickBool tick1 = new TickBool();
    TickBool tick2 = new TickBool();
    TickBool tick3 = new TickBool();

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

        //TextView signature = (TextView) this.findViewById(R.id.Signature);
        //ImageView signatureImage = (ImageView) this.findViewById(R.id.signatureView);

        //signature.setEnabled(false);
        //signatureImage.setEnabled(false);

        tick1.setListener(new TickBool.ChangeListener() {
            @Override
            public void onChange() {
                if(tick1.getBool())
                    image1.setImageResource(R.drawable.tick);
            }
        });

        tick2.setListener(new TickBool.ChangeListener() {
            @Override
            public void onChange() {
                if(tick2.getBool())
                    image2.setImageResource(R.drawable.tick);
            }
        });

        tick3.setListener(new TickBool.ChangeListener() {
            @Override
            public void onChange() {
                if(tick3.getBool())
                    image3.setImageResource(R.drawable.tick);
            }
        });

        //send textviews to getTrackingDetails class
        new getTrackingDetails(status, status2, status3, event, event2, event3, dateTime, dateTime2, dateTime3).execute();

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
                JSONArray trackingEvents = obj.getJSONObject("results").getJSONArray("tracking_events") ;

                //establish the different states of parcel, parcel is picked up
                if(trackingEvents.length() == 2){
                    System.out.println("tracking events length "+trackingEvents.length());
                    JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);
                    getPickedUpEvent(pickedUpEvent);
                    //todo: changed value here
                    tick1.setBool(true);
                }
                //parcel is in transit
                if(trackingEvents.length() == 3) {
                    System.out.println("tracking events length "+trackingEvents.length());
                    JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 2);
                    getPickedUpEvent(pickedUpEvent);
                    JSONObject transitEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);
                    getTransitEvent(transitEvent);
                    //todo: changed value here
                    tick1.setBool(true);
                    tick2.setBool(true);
                }
                //parcel is delivered
                if(trackingEvents.length() == 4) {
                    System.out.println("tracking events length "+trackingEvents.length()+" "+trackingEvents);
                    JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 3);
                    getPickedUpEvent(pickedUpEvent);
                    JSONObject transitEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 2);
                    getTransitEvent(transitEvent);
                    JSONObject deliveredEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);
                    getDeliveredEvent(deliveredEvent);
                    //todo: changed value here
                    tick1.setBool(true);
                    tick2.setBool(true);
                    tick3.setBool(true);
                }

                //JSONObject eventsTest = (JSONObject) trackingEvents.get(trackingEvents.length());

                //String resultSignature = lastEvent.getString("signed_by");

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
                dateTime.setText(transitDateTime[0] + " " + transitDateTime[1]);
                dateTime2.setText(resultDateTime2[0] + " " + resultDateTime2[1]);
                status.setText(result[0]); status2.setText(result[3]);
                event.setText(result[1]); event2.setText(result[4]);
            }
            //
            if(result.length == 9) {
                String[] resultDateTime = result[2].split("T");
                String[] resultDateTime2 = result[5].split("T");
                String[] resultDateTime3 = result[8].split("T");
                dateTime.setText(resultDateTime[0] + " " + resultDateTime[1]);
                dateTime2.setText(resultDateTime2[0] + " " + resultDateTime2[1]);
                dateTime3.setText(resultDateTime3[0] + " " + resultDateTime3[1]);
                status.setText(result[0]); status2.setText(result[3]); status3.setText(result[6]);
                event.setText(result[1]); event2.setText(result[4]); event3.setText(result[7]);
            }

        }
    }

}


