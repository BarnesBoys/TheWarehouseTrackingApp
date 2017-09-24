package com.example.brooke.thewarehousetrackingapp;


import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TrackingPage extends AppCompatActivity {

    protected String trackingNumber = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_page);
        Intent intent = getIntent();

        //System.err.println("intent: " + intent);

        if(intent == null) {
            trackingNumber = "";
        }
        else{
            Bundle extras = intent.getExtras();
            if(extras == null) trackingNumber = "";
            else trackingNumber = intent.getExtras().getString("tracking_reference");
        }

        //to test tracking number has been set
        //System.err.println("tracking_ref is:  " + trackingNumber);

        Toast.makeText(TrackingPage.this , "Track your Parcel here", Toast.LENGTH_LONG).show();

        TextView status = (TextView) this.findViewById(R.id.status);
        TextView status2 = (TextView) this.findViewById(R.id.status2);
        TextView status3 = (TextView) this.findViewById(R.id.status3);
        TextView event = (TextView) this.findViewById(R.id.event);
        TextView event2 = (TextView) this.findViewById(R.id.event2);
        TextView event3 = (TextView) this.findViewById(R.id.event3);
        TextView dateTime = (TextView) this.findViewById(R.id.dateTime);
        TextView dateTime2 = (TextView) this.findViewById(R.id.dateTime2);
        TextView dateTime3 = (TextView) this.findViewById(R.id.dateTime3);
        //TextView signature = (TextView) this.findViewById(R.id.Signature);
        //ImageView signatureImage = (ImageView) this.findViewById(R.id.signatureView);

        status.setEnabled(false); status2.setEnabled(false); status3.setEnabled(false);
        event.setEnabled(false); event2.setEnabled(false); event3.setEnabled(false);
        dateTime.setEnabled(false); dateTime2.setEnabled(false); dateTime3.setEnabled(false);
        //signature.setEnabled(false);
        //signatureImage.setEnabled(false);


        new getTrackingDetails(status, status2, status3, event, event2, event3, dateTime, dateTime2, dateTime3).execute();

    }
    private class getTrackingDetails extends AsyncTask<String, String, String>{

        String result;
        TextView status,status2,status3,event,event2,event3,dateTime,dateTime2,dateTime3,signature;

        ImageView signatureImage;

        getTrackingDetails(TextView status, TextView status2, TextView status3, TextView event, TextView event2, TextView event3, TextView dateTime,
                           TextView dateTime2, TextView dateTime3){
            this.status = status; this.status2 = status2; this.status3=status3;
            this.event = event; this.event2=event2; this.event3=event3;
            this.dateTime = dateTime; this.dateTime2=dateTime2; this.dateTime3=dateTime3;
        }

        protected String doInBackground(String... params){

            try {
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

                //transfer the authoriztion details as needed
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

                //return the actuall json data or the error json data
                if(responseCode != 200) in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                else in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((line = in.readLine()) !=null){
                    responseMessage += line;
                }

                //parsing the returned data
                obj = new JSONObject(responseMessage);
                JSONArray trackingEvents = obj.getJSONObject("results").getJSONArray("tracking_events") ;
                JSONObject pickedUpEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 4);
                JSONObject transitEvent = (JSONObject) trackingEvents.get(trackingEvents.length() - 2);
                JSONObject lastEvent  = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);

                //JSONObject eventsTest = (JSONObject) trackingEvents.get(trackingEvents.length());

                //extracting the results from tracking events to display
                String resultPickedUpEvent = pickedUpEvent.getString("event_description");
                String resultPickedUpStatus = pickedUpEvent.getString("status_description");
                String resultPickedUpDateTime = pickedUpEvent.getString("event_datetime");
                String resultTransitEvent = transitEvent.getString("event_description");
                String resultTransitStatus = transitEvent.getString("status_description");
                String resultTransitDateTime = transitEvent.getString("event_datetime");
                String resultEvent = lastEvent.getString("event_description");
                String resultStatus = lastEvent.getString("status_description");
                String resultDateTime = lastEvent.getString("event_datetime");
                //String resultSignature = lastEvent.getString("signed_by");

                System.err.println(trackingEvents);
                System.err.println(resultEvent);
                System.err.println(resultStatus);
                System.err.println(resultDateTime);
                //System.err.println(resultSignature);
                System.err.println(pickedUpEvent);
                System.err.println(transitEvent);

                result = resultPickedUpStatus + "///" + resultPickedUpEvent + "///" + resultPickedUpDateTime + "///" +
                        resultTransitStatus + "///" + resultTransitEvent + "///" + resultTransitDateTime + "///" +
                        resultStatus + "///" + resultEvent + "///" + resultDateTime;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String st){

            String[] result = st.split("///");
            String[] pickedUpDateTime = result[2].split("T");
            String[] transitDateTime = result[5].split("T");
            String[] resultDateTime = result[8].split("T");
            //String[] signatureSplit = result[3].split(":");

            // ------Trying to convert binary data to image----------
            // int sigLength = signatureSplit[2].length();
            //-------gets binary data out of signed_by event---------
            //String sigSplit2 = signatureSplit[2].substring(2, sigLength-2);
            //System.out.println(sigSplit2);
            //-------convert to bitmap? or buffered image? or?-------
            //Bitmap image = BitmapFactory.decodeFile(sigSplit2);
            //signatureImage.setImageBitmap(image);


            status.setText(result[0]); status2.setText(result[3]); status3.setText(result[6]);
            event.setText(result[1]); event2.setText(result[4]); event3.setText(result[7]);
            dateTime.setText(pickedUpDateTime[0] + " " + pickedUpDateTime[1]);
            dateTime2.setText(resultDateTime[0] + " " + resultDateTime[1]);
            dateTime3.setText(resultDateTime[0] + " " + resultDateTime[1]);
            //signature.setText(signatureSplit[1]);


        }
    }

}


