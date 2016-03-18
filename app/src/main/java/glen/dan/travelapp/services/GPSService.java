package glen.dan.travelapp.services;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class GPSService extends Service implements LocationListener {

    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean isLocationAvailable = false;
    Location mLocation;
    double mLatitude;
    double mLongitude;
    private static final long TIME = 30000;
    private static final long DISTANCE = 20;
    protected LocationManager mLocationManager;


    public GPSService(Context context) {
        this.mContext = context;
        mLocationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

    }








    public Location getLocation() {
        try {
            // Getting GPS status
            isGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // If gps enabled, get latitude and longitude using GPS Services
            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME, DISTANCE, this);
                if (mLocationManager != null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mLocation != null) {
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                        isLocationAvailable = true;
                        return mLocation;
                    }
                }
            }

            isNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME, DISTANCE, this);
                if (mLocationManager != null) {
                    mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (mLocation != null) {
                        mLatitude = mLocation.getLatitude();
                        mLongitude = mLocation.getLongitude();
                        isLocationAvailable = true; // setting a flag that
                        // location is available
                        return mLocation;
                    }
                }
            }
            // If reaching here means, we were not able to get location neither
            // from GPS not Network,
            if (!isGPSEnabled) {
                // so asking user to open GPS
                askUserToOpenGPS();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // if reaching here means, location was not available, so setting the
        // flag as false
        isLocationAvailable = false;
        return null;
    }

    public String getCountryName() {
        if (isLocationAvailable) {

            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // If it has returned an address
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String countryName = address.getCountryName();
                return countryName;
            } else {
                return "No address found";
            }
        } else {
            return "Location Not available";
        }
    }

    public double getLatitude() {
        if (mLocation != null) {
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }


    public double getLongitude() {
        if (mLocation != null) {
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }


    public void closeGPS() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(GPSService.this);
        }
    }






    public void askUserToOpenGPS() {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        mAlertDialog.setTitle("Location not available, Open GPS?")
                .setMessage("Activate GPS to use use location services?")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}

