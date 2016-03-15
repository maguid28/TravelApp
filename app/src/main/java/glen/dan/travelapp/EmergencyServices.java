package glen.dan.travelapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import glen.dan.travelapp.services.GPSService;

public class EmergencyServices extends AppCompatActivity {

    TextView locationText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_services);

        locationText = (TextView) findViewById(R.id.location_text);

        GPSService gpsService = new GPSService(EmergencyServices.this);
        gpsService.getLocation();
        locationText.setText(gpsService.getCountryName());
        gpsService.closeGPS();


        Button callPoliceButton = (Button) findViewById(R.id.call_police_button);
        callPoliceButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:1231"));
                    if (ActivityCompat.checkSelfPermission(EmergencyServices.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EmergencyServices.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                        return;
                    }
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        Button callAmbulanceButton = (Button) findViewById(R.id.call_ambulance_button);
        callAmbulanceButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:1231"));
                    if (ActivityCompat.checkSelfPermission(EmergencyServices.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EmergencyServices.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                        return;
                    }
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });

        Button callFireButton = (Button) findViewById(R.id.call_fire_button);
        callFireButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:1231"));
                    if (ActivityCompat.checkSelfPermission(EmergencyServices.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EmergencyServices.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
                        return;
                    }
                    startActivity(callIntent);
                } catch (ActivityNotFoundException activityException) {
                    Log.e("Calling a Phone Number", "Call failed", activityException);
                }
            }
        });


    }
}
