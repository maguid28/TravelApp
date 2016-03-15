package glen.dan.travelapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import glen.dan.travelapp.services.SubmitWarningBackgroundTask;

public class SubmitWarningActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION = 1;

    Spinner dropdown;
    EditText descriptionText;
    Address address;

    Geocoder geocoder;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_warning);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //create search button
        Button search_button = (Button) findViewById(R.id.report_search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText location_tf = (EditText) findViewById(R.id.report_search_box);
                String location = location_tf.getText().toString();
                List<Address> addressList = null;
                if (!location.equals("")) {
                    geocoder = new Geocoder(SubmitWarningActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {
                        address = addressList.get(0);
                        latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                    }
                    else Toast.makeText(SubmitWarningActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();

                }
            }
        });



        //create dropdown menu
        dropdown = (Spinner)findViewById(R.id.dropdownmenu);
        String[] items = new String[]{"--", "Theft", "Assault", "Burglary", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        //text field
        descriptionText = (EditText) findViewById(R.id.description_field);





        // Create button
        Button submit = (Button) findViewById(R.id.submit_button);
        // button click event
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String  username = sharedPreferences.getString("username", "username") ;
                String method = "entry";



                EditText location_tf = (EditText) findViewById(R.id.report_search_box);
                String location = location_tf.getText().toString();
                List<Address> addressList = null;
                if (!location.equals("")) {
                    geocoder = new Geocoder(SubmitWarningActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null) {
                        address = addressList.get(0);
                    }
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    String warning = dropdown.getSelectedItem().toString();
                    String description = descriptionText.getText().toString();
                    String lon = String.valueOf(address.getLongitude());
                    String lat = String.valueOf(address.getLatitude());

                    SubmitWarningBackgroundTask submitWarningBackgroundTask = new SubmitWarningBackgroundTask(SubmitWarningActivity.this);
                    submitWarningBackgroundTask.execute(method, username, warning, description, lon, lat);
                    finish();

                }
                else Toast.makeText(SubmitWarningActivity.this, "No Location Selected", Toast.LENGTH_SHORT).show();
            }
        });





    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        //set my location
        mMap.setMyLocationEnabled(true);
        //Zoom camera in to current location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 11));
        }
    }
}
