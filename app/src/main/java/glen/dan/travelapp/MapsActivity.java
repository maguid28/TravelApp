package glen.dan.travelapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import glen.dan.travelapp.services.MarkerJSONParser;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    private AutoCompleteTextView autoCompView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_list_item));
        autoCompView.setOnItemClickListener(this);

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                String location = autoCompView.getText().toString();
                List<Address> addressList = null;

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    assert addressList != null;
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });


        autoCompView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompView.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    String location = autoCompView.getText().toString();
                    List<Address> addressList = null;

                    if (!location.equals("")) {
                        Geocoder geocoder = new Geocoder(MapsActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert addressList != null;
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {}

    public void onButtonClick(View v) {
        //if user clicks submit warning button launch that activity
        if (v.getId() == R.id.submit_warning_button) {
            Intent i = new Intent(this, SubmitWarningActivity.class);
            startActivity(i);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Starting locations retrieve task
        new RetrieveTask().execute();

        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int REQUEST_LOCATION = 1;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        //set my location
        mMap.setMyLocationEnabled(true);
        //Zoom camera in to current location
        Location location = getLastKnownLocation();
        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
        }
    }



    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }




    // Adding marker on the GoogleMaps
    private void addMarker(LatLng latlng, String warning, String description) {

        if(warning.equals("Burglary")) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title(warning);
            markerOptions.snippet(description);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.caution_marker_blue));
            mMap.addMarker(markerOptions);
        }
        if(warning.equals("Assault")) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title(warning);
            markerOptions.snippet(description);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.caution_marker_pink));
            mMap.addMarker(markerOptions);
        }
        if(warning.equals("Theft")) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title(warning);
            markerOptions.snippet(description);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.caution_marker_green));
            mMap.addMarker(markerOptions);
        }
        if(warning.equals("Other")) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title(warning);
            markerOptions.snippet(description);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.caution_marker));
            mMap.addMarker(markerOptions);
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
                String warning = marker.get("warning");
                String description = marker.get("description");

                addMarker(latlng, warning, description);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
