package com.example.mitrikyle.test;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String host = "http://45.55.89.29:8080/";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Button btnReportTrash;
    private Button btnDiscoverBin;
    private Button btnDisposeTrash;
    public static LatLng userLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        setContentView(R.layout.activity_maps2);
        btnReportTrash = (Button) findViewById(R.id.btnReportTrash);
        btnReportTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SubmitHotspot.class);
                startActivity(i);
            }
        });
        btnDiscoverBin = (Button) findViewById(R.id.btnDiscoverBin);
        btnDiscoverBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SubmitBin.class);
                startActivity(i);
            }
        });
        btnDisposeTrash = (Button) findViewById(R.id.btnDisposeTrash);
        btnDisposeTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DisposeTrashActivity.class);
                startActivity(i);
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                } else {
                    return;
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        3);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            CameraUpdate center = CameraUpdateFactory.newLatLng(userLoc);
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
            mMap.setBuildingsEnabled(true);


            LatLngBounds curScreen = mMap.getProjection()
                    .getVisibleRegion().latLngBounds;
            new RetrieveHotspotsTask().execute(curScreen);
            new RetrieveBinsTask().execute(curScreen);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public class RetrieveHotspotsTask extends AsyncTask<LatLngBounds, Void, String> {
        Exception exception;

        protected void onPreExecute() {
            Log.i("INFO", "hi");
        }

        protected String doInBackground(LatLngBounds... locs) {
            LatLngBounds loc = locs[0];
//        String email = emailText.getText().toString();
            // Do some validation here
            LatLng northeast = loc.northeast;
            LatLng southwest = loc.southwest;
            String NElat = String.valueOf(northeast.latitude);
            String NElong = String.valueOf(northeast.longitude);
            String SWlat = String.valueOf(southwest.latitude);
            String SWlong = String.valueOf(southwest.longitude);
            try {
                URL url = new URL(MapsActivity.host + "hotspots/" + NElat + '/' + NElong + '/' + SWlat + '/' + SWlong +'/');
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

            JSONArray locs = null;
            try {
                List<LatLng> latLngData = new ArrayList<LatLng>();
                locs = new JSONArray(response);
                Log.i("Info", locs.toString());
                for (int i=0; i < locs.length(); i++) {
                    JSONObject loc = locs.getJSONObject(i);
                    Double lat = loc.getDouble("latitude");
                    Double lon = loc.getDouble("longitude");
                    latLngData.add(new LatLng(lat, lon));
                }

                Log.i("INFO", "LatLngData: " + latLngData.toString());

                for (LatLng loc : latLngData) {
                    mMap.addCircle(new CircleOptions()
                            .center(loc)
                            .radius(20)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        Log.i("INFO", response);
        }
    }

    public class RetrieveBinsTask extends AsyncTask<LatLngBounds, Void, String> {
        Exception exception;

        protected void onPreExecute() {
            Log.i("INFO", "hi");
        }

        protected String doInBackground(LatLngBounds... locs) {
            LatLngBounds loc = locs[0];
//        String email = emailText.getText().toString();
            // Do some validation here
            LatLng northeast = loc.northeast;
            LatLng southwest = loc.southwest;
            String NElat = String.valueOf(northeast.latitude);
            String NElong = String.valueOf(northeast.longitude);
            String SWlat = String.valueOf(southwest.latitude);
            String SWlong = String.valueOf(southwest.longitude);
            try {
                URL url = new URL(MapsActivity.host + "bins/" + NElat + '/' + NElong + '/' + SWlat + '/' + SWlong +'/');
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

            JSONArray locs = null;
            try {
                List<Bin> bins = new ArrayList<Bin>();
                locs = new JSONArray(response);
                Log.i("Info", locs.toString());
                for (int i=0; i < locs.length(); i++) {
                    List<Integer> l = new ArrayList<Integer>();
                    JSONObject loc = locs.getJSONObject(i);
                    Double lat = loc.getDouble("latitude");
                    Double lon = loc.getDouble("longitude");
                    JSONArray arr = loc.getJSONArray("frequencies");
                    for (int j=0; j < arr.length(); j++) {
                        l.add(arr.getInt(j));
                    }
                    bins.add(new Bin(new LatLng(lat, lon), l));
                }

                Log.i("INFO", "LatLngData: " + bins.toString());

                for (Bin bin : bins) {
                    String red = Integer.toString(bin.frequencies.get(0));
                    String blue = Integer.toString(bin.frequencies.get(1));
                    String yellow = Integer.toString(bin.frequencies.get(2));
                    String display = "Red: " + red  + " Blue: " + blue + " Yellow: " + yellow;
                    mMap.addMarker(new MarkerOptions()
                        .position(bin.latLng)
                        .title("Garbage Bin")
                        .snippet(display)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        Log.i("INFO", response);
        }
    }


}
