package glen.dan.travelapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import glen.dan.travelapp.services.GPSService;
import glen.dan.travelapp.services.WeatherService;

public class WeatherActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {



    private TextView cityField, detailsField, currentTemperatureField, humidity_field, weatherIcon;
    private AutoCompleteTextView autoCompView;

    Typeface weatherFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = (TextView)findViewById(R.id.city_field);
        detailsField = (TextView)findViewById(R.id.details_field);
        currentTemperatureField = (TextView)findViewById(R.id.current_temperature_field);
        humidity_field = (TextView)findViewById(R.id.humidity_field);
        weatherIcon = (TextView)findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        WeatherService.placeIdTask asyncTask =new WeatherService.placeIdTask(new WeatherService.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {

                cityField.setText(weather_city);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature + "C");
                humidity_field.setText("Humidity: "+weather_humidity);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });

        GPSService gpsService = new GPSService(WeatherActivity.this);
        gpsService.getLocation();
        String latitude = Double.toString(gpsService.getLatitude());
        String longitude = Double.toString(gpsService.getLongitude());
        gpsService.closeGPS();

        asyncTask.execute(latitude, longitude);



        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.autocomplete_list_item));
        autoCompView.setOnItemClickListener(this);

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
                String location = autoCompView.getText().toString();
                List<Address> addressList = null;

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(autoCompView.getWindowToken(),
                        InputMethodManager.RESULT_UNCHANGED_SHOWN);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(WeatherActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        assert addressList != null;
                        Address address = addressList.get(0);

                        WeatherService.placeIdTask asyncTask = new WeatherService.placeIdTask(new WeatherService.AsyncResponse() {
                            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                                cityField.setText(weather_city);
                                detailsField.setText(weather_description);
                                currentTemperatureField.setText(weather_temperature + "C");
                                humidity_field.setText("Humidity: " + weather_humidity);
                                weatherIcon.setText(Html.fromHtml(weather_iconText));
                            }
                        });

                        String latitude = Double.toString(address.getLatitude());
                        String longitude = Double.toString(address.getLongitude());
                        asyncTask.execute(latitude, longitude);
                    }
                    catch (IndexOutOfBoundsException e){
                        cityField.setText("No Weather Available Here");
                        detailsField.setText("");
                        currentTemperatureField.setText("");
                        humidity_field.setText("");
                        weatherIcon.setText("");
                    }

                }
            }
        });

        autoCompView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
                    String location = autoCompView.getText().toString();
                    List<Address> addressList = null;

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompView.getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);

                    if (!location.equals("")) {
                        Geocoder geocoder = new Geocoder(WeatherActivity.this);
                        try {
                            addressList = geocoder.getFromLocationName(location, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert addressList != null;
                        Address address = addressList.get(0);

                        WeatherService.placeIdTask asyncTask =new WeatherService.placeIdTask(new WeatherService.AsyncResponse() {
                            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_pressure, String weather_updatedOn, String weather_iconText, String sun_rise) {
                                cityField.setText(weather_city);
                                detailsField.setText(weather_description);
                                currentTemperatureField.setText(weather_temperature + "C");
                                humidity_field.setText("Humidity: "+weather_humidity);
                                weatherIcon.setText(Html.fromHtml(weather_iconText));
                            }
                        });

                        String latitude = Double.toString(address.getLatitude());
                        String longitude = Double.toString(address.getLongitude());
                        asyncTask.execute(latitude, longitude);

                    }
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
}
