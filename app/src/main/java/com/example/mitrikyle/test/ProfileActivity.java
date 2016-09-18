package com.example.mitrikyle.test;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameTextView;
    private TextView pointsTextView;
    private Button mRedeemBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        nameTextView = (TextView) findViewById(R.id.username);
        pointsTextView = (TextView) findViewById(R.id.points);
        mRedeemBtn = (Button) findViewById(R.id.btnRedeemRewards);
        mRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, RedeemRewardsActivity.class);
                startActivity(i);
            }
        });
        new RetrieveUserTask().execute("100002022265315");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileId:
                Intent i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                break;
            case R.id.homeId:
                Intent j = new Intent(this, MapsActivity.class);
                startActivity(j);
                break;
        }
        return true;

    }

    public class RetrieveUserTask extends AsyncTask<String, Void, String> {
        Exception exception;

        protected void onPreExecute() {
        }

        protected String doInBackground(String... users) {
            String userId = users[0];
//        String email = emailText.getText().toString();
            // Do some validation here
            try {
                URL url = new URL(MapsActivity.host + "users/" + userId + '/');
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);

            try {
                List<LatLng> latLngData = new ArrayList<LatLng>();
                JSONObject user = new JSONObject(response);
                Log.i("Info", user.toString());
                String username = user.getString("user_name");
                int points = user.getInt("points");

                Log.i("INFO", "Points: " + points);

                nameTextView.setText(username);
                pointsTextView.setText(Integer.toString(points));
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        Log.i("INFO", response);
        }
    }

}
