package glen.dan.travelapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import glen.dan.travelapp.services.MarkerJSONParser;
import glen.dan.travelapp.services.MyGeofence;

public class GeoFencingActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "App";

    // Stores the PendingIntent used to request geofence monitoring.
    private PendingIntent mGeofenceRequestIntent;
    List<Geofence> mGeofenceList;
    private GoogleApiClient mApiClient;

    public MyGeofence myGeofence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);

        new RetrieveTask().execute();
        try {
            String str_result= new RetrieveTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mApiClient.connect();

        Intent i = new Intent(GeoFencingActivity.this, MainActivity.class);
        startActivity(i);

    }

    // Adding marker on the GoogleMaps
    private void addMarker(LatLng latLng) {

        String id = String.valueOf(latLng.latitude) + String.valueOf(latLng.longitude);
        float radius = 200f;
        long expiration_duration = Geofence.NEVER_EXPIRE;
        int transitionType = Geofence.GEOFENCE_TRANSITION_ENTER;

        myGeofence = new MyGeofence(id, latLng.latitude, latLng.longitude, radius, expiration_duration, transitionType);

        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<>();
        mGeofenceList.add(myGeofence.toGeofence());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
         return builder.build();
    }

    private PendingIntent getGeofenceTransitionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofenceRequestIntent != null) {
            return mGeofenceRequestIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Get the PendingIntent for the geofence monitoring request.
        // Send a request to add the current geofences.
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        //catch geofences are already added.
        try {
            LocationServices.GeofencingApi.addGeofences(mApiClient, getGeofencingRequest(), mGeofenceRequestIntent);
        }catch (IllegalArgumentException e){
            Log.e("Geofence feature", "Geofences already added");
        }
        finish();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // If the error has a resolution, start a Google Play services activity to resolve it.
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, 9000);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while resolving connection error.", e);
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (null != mGeofenceRequestIntent) {
            LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
        }
    }



    // Background task to retrieve locations from remote mysql server
    private class RetrieveTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String strUrl = "http://student.computing.dcu.ie/~maguid28/get_all_data.php";
            URL url;
            StringBuilder sb = new StringBuilder();
            try {
                url = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream iStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
                String line;
                while( (line = reader.readLine()) != null){
                    sb.append(line);
                }

                reader.close();
                iStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    // Background thread to parse the JSON data retrieved from MySQL server
    private class ParserTask extends AsyncTask<String, Void, List<HashMap<String, String>>> {
        @Override
        protected List<HashMap<String,String>> doInBackground(String... params) {
            MarkerJSONParser markerParser = new MarkerJSONParser();
            JSONObject json = null;
            try {
                json = new JSONObject(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return markerParser.parse(json);
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            for(int i=0; i<result.size();i++){
                HashMap<String, String> marker = result.get(i);
                LatLng latlng = new LatLng(Double.parseDouble(marker.get("latitude")), Double.parseDouble(marker.get("longitude")));
                addMarker(latlng);
            }
        }
    }
}
