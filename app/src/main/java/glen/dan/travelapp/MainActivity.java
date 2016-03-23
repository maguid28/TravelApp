package glen.dan.travelapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import glen.dan.travelapp.services.GPSService;

public class MainActivity extends Activity {

    private ImageView flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView locationText = (TextView) findViewById(R.id.location_text);
        flag = (ImageView) findViewById(R.id.flag);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loggedIn", getString(R.string.loggedin));
        editor.commit();

        //get country name and display it
        GPSService gpsService = new GPSService(MainActivity.this);
        gpsService.getLocation();
        locationText.setText(gpsService.getCountryName());
        gpsService.closeGPS();

        getCountryFlag(locationText);
    }





    public void onButtonClick(View v){


        if(v.getId() == R.id.smart_travel_button){
            //this is the activity we need to launch
            Intent i = new Intent(this, MapsActivity.class);
            startActivity(i);
        }

        if(v.getId() == R.id.personalinfo_button){
            //this is the activity we need to launch
            Intent i = new Intent(this, PersonalInfo.class);
            startActivity(i);
        }

        if(v.getId() == R.id.weather_button) {
            //this is the activity we need to launch
            Intent i = new Intent(this, WeatherActivity.class);
            startActivity(i);
        }

        if(v.getId() == R.id.emergency_button){
            //this is the activity we need to launch
            Intent i = new Intent(this, EmergencyServices.class);
            startActivity(i);
        }

        if(v.getId() == R.id.attractions_button){
            //this is the activity we need to launch
            Intent i = new Intent(this, AttractionsActivity.class);
            startActivity(i);
        }

        //log user out and return to log in screen
        if(v.getId() == R.id.log_out_button){

            //change loggedIn preference to no
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("loggedIn", getString(R.string.loggedout));
            editor.remove("firstname");
            editor.remove("surname");
            editor.remove("phone");
            editor.remove("kin");
            editor.remove("kinContact");

            editor.commit();

            //remove username from preferences
            editor.putString("username", "username");
            editor.commit();
            //start Login activity
            Intent i = new Intent(this, LoginActivity.class);
            finish();
            startActivity(i);
        }
    }

    //get the flag for the country you are in and display it
    public void getCountryFlag(TextView locationText) {
        String country = locationText.getText().toString();

        switch (country)
        {
            case "Ireland":
                flag.setImageResource(R.drawable.ireland);
                break;
            case "Irlande":
                flag.setImageResource(R.drawable.ireland);
                break;
            case "United Kingdom":
                flag.setImageResource(R.drawable.uk);
                break;
            case "France":
                flag.setImageResource(R.drawable.france);
                break;
            case "Netherlands":
                flag.setImageResource(R.drawable.netherlands);
                break;
            case "Canada":
                flag.setImageResource(R.drawable.canada);
                break;
            case "United States":
                flag.setImageResource(R.drawable.usa);
                break;
            case "Italy":
                flag.setImageResource(R.drawable.italy);
                break;
            case "Spain":
                flag.setImageResource(R.drawable.spain);
                break;
            case "China":
                flag.setImageResource(R.drawable.china);
                break;
            case "Turkey":
                flag.setImageResource(R.drawable.turkey);
                break;
            case "Germany":
                flag.setImageResource(R.drawable.germany);
                break;
            case "Russia":
                flag.setImageResource(R.drawable.russia);
                break;
            case "Mexico":
                flag.setImageResource(R.drawable.mexico);
                break;
            default:
                flag.setImageResource(R.drawable.world);
        }
    }
}


