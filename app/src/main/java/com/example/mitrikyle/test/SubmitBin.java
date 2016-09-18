package com.example.mitrikyle.test;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SubmitBin extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private Handler mHandler;
    private GoogleApiClient client;
    private String userChoosenTask;
    private ImageView ivImage;
    private Button mButton;
    private VisionServiceClient msoftClient;
    private Bitmap img;
    private String cap;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_hotspot);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        ivImage = (ImageView) findViewById(R.id.ivImage);
        if (msoftClient == null) {
            msoftClient = new VisionServiceRestClient(getString(R.string.subscription_key));
        }

        mTextView = (TextView) findViewById(R.id.imageText);

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
            }
        };

        cameraIntent();

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SubmitHotspot Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.mitrikyle.test/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "SubmitHotspot Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.mitrikyle.test/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }

    private void onCaptureImageResult(Intent data) {
        img = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(img);
        new doRequest().execute();
        mTextView.setText("PROCESING...");
    }


    private Boolean process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"Description", "Categories", "Faces"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = msoftClient.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        boolean binFound = false;

        try {
            JSONObject jObj = new JSONObject(result);
            JSONObject desc = jObj.getJSONObject("description");
            JSONArray captions = desc.getJSONArray("captions");
            JSONObject omg = captions.getJSONObject(0);
            cap = omg.getString("text");
            JSONArray tags = desc.getJSONArray("tags");
            Log.d("TAGS", tags.toString());
            for (int i =0; i < tags.length(); i++){
                if (tags.get(i).toString().equals("bin") || tags.get(i).toString().equals("cup") || tags.get(i).toString().equals("toilet") || tags.get(i).toString().equals("cat")){
                    Log.d("SUCCES", "BIN FOUND");
                    binFound = true;
                    break;
                }
            }

            Log.d("WTF", tags.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("result", result);

        return  binFound;
    }

    private class doRequest extends AsyncTask<String, String, Boolean> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected Boolean doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean data) {
            super.onPostExecute(data);
            // Display based on error existence

            if (e != null) {
                Log.d("Error: ", e.getMessage());
                this.e = null;
            } else {
                mTextView.setText("DONE");
               if (data) {
                   try {
                       new doOtherRequest().execute();
                   } catch (Exception e1) {
                       e1.printStackTrace();
                   }
               } else {
                   Toast.makeText(SubmitBin.this, "That's not a bin, that's a " + cap, Toast.LENGTH_LONG).show();
                   Intent i = new Intent(SubmitBin.this, MapsActivity.class);
                   startActivity(i);
               }
            }
        }




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
                Toast.makeText(SubmitBin.this, "Your bin has been submitted!", Toast.LENGTH_LONG).show();
                if(data != null) {
                    Log.d("YES", data.toString());

                }
            }

            Intent i = new Intent(SubmitBin.this, MapsActivity.class);
            startActivity(i);
        }




    }

    public void getPublic() throws Exception {
        LatLng userloc = MapsActivity.userLoc;

        //Your server URL
        //Request Parameters you want to send
        String urlParameters = "team=100002022265315&latitude=" + userloc.latitude + "&longitude=" + userloc.longitude;;
        String url = MapsActivity.host + "bins/?" + urlParameters;
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
