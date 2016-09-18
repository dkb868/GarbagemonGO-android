package com.example.mitrikyle.test;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RedeemRewardsActivity extends AppCompatActivity {

    ListView listView ;
    Button mRedeemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem_rewards);

        mRedeemButton = (Button) findViewById(R.id.btnRedeem);
        mRedeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new doOtherRequest().execute();
            }
        });
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // Defined Array values to show in ListView
        String[] values = new String[] {
                "An apple: 20 points",
                "Recycled Cookies: 50 points",
                "Indestructable Garbage Bag: 100 points",
                "Harrambe Hat: 200 points",
                "Harrambe Hoodie : 500 points",
                "Life sized Harrambe plush: 1000 points",
                "Case of Soylent: 10,000 points",
        };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third parameter - ID of the TextView to which the data is written
        // Forth - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.listview, R.id.rewardText, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition  = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);


            }

        });
    }


    private class doOtherRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doOtherRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                getPublic();
                return "HI";
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                Log.d("Error: ", e.getMessage());
                this.e = null;
            } else {
                Toast.makeText(RedeemRewardsActivity.this, "You have successfully claimed the Harrambe Hat!", Toast.LENGTH_LONG).show();
                if(data != null) {
                    Log.d("YES", data.toString());

                }
            }

            Intent i = new Intent(RedeemRewardsActivity.this, ProfileActivity.class);
            startActivity(i);
        }




    }

    public void getPublic() throws Exception {
        //Your server URL
        //Request Parameters you want to send
        String urlParameters = "team=100002022265315&price=200&description=Harrambe+Hat";;
        String url = MapsActivity.host + "reward/?" + urlParameters;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //Request Parameters you want to send

        // Send post request
        con.setDoOutput(true);// Should be part of code only for .Net web-services else no need for PHP
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        Log.d("RES", response.toString());

    }

}