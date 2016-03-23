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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class SubmitWarningActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener {

    AutoCompleteTextView autoCompView;
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

        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete1);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_list_item));
        autoCompView.setOnItemClickListener(this);

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete1);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                String location = autoCompView.getText().toString();
                List<Address> addressList = null;

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(SubmitWarningActivity.this);
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
                    autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete1);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompView.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    String location = autoCompView.getText().toString();
                    List<Address> addressList = null;

                    if (!location.equals("")) {
                        Geocoder geocoder = new Geocoder(SubmitWarningActivity.this);
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


        //create dropdown menu
        dropdown = (Spinner)findViewById(R.id.dropdownmenu);
        String[] items = new String[]{"--", getString(R.string.theft), getString(R.string.assault),getString(R.string.burglary), getString(R.string.other)};
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

                autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete1);

                String location = autoCompView.getText().toString();
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
                else Toast.makeText(SubmitWarningActivity.this, getString(R.string.nolocationselected), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onButtonClick(View v) {
        if (v.getId() == R.id.clearButton) {
            autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete1);
            autoCompView.setText("");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {}

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
