package com.example.brooke.thewarehousetrackingapp;

import android.content.Intent;
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


public class TrackingPage extends AppCompatActivity {

    protected String trackingNumber = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_page);
        Intent intent = getIntent();
        trackingNumber = intent.getExtras().getString("tracking_number");
    }

    public void trackingItem(View view) {
        new getTrackingDetails().execute();
    }
    class getTrackingDetails extends AsyncTask<String, String, String>{

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
                String line = "";
                String json = "";
                while((line = in.readLine()) != null){
                    System.err.println(line);
                    json += line;
                }
                in.close();

                //put returned data in jsonobj
                JSONObject obj = new JSONObject(json);
                //extract the necessary access_token for interaction with nzpost api
                String accessToken = obj.getString("access_token");
                System.err.println(accessToken);
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

                //return the actually json data or the error json data
                if(responseCode != 200) in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                else in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while((line = in.readLine()) !=null){
                    responseMessage += line;
                }

                //parsing the returned data
                obj = new JSONObject(responseMessage);
                JSONArray trackingEvents = obj.getJSONObject("results").getJSONArray("tracking_events") ;
                JSONObject lastEvent  = (JSONObject) trackingEvents.get(trackingEvents.length() - 1);

                //extracting the last result from tracking events to display
                String resultEvent = lastEvent.getString("event_description");
                String resultStatus = lastEvent.getString("status_description");

                System.err.println(trackingEvents);
                System.err.println(resultEvent);
                System.err.println(resultStatus);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}


