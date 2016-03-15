package glen.dan.travelapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;


public class AttractionsActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mName;
    private TextView mAddress;
    private TextView mWebsite;
    private TextView mPhone;
    private TextView tapForDirections;
    private TextView mAttributions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        mName = (TextView) findViewById(R.id.attraction_name);
        mAddress = (TextView) findViewById(R.id.attraction_address);
        mWebsite = (TextView) findViewById(R.id.attractions_website);
        mPhone = (TextView) findViewById(R.id.attraction_phone);
        tapForDirections = (TextView) findViewById(R.id.tap_for_directions);
        mAttributions = (TextView) findViewById(R.id.attributions);

        Button pickerButton = (Button) findViewById(R.id.pickerButton);

        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    startActivityForResult(intentBuilder.build(AttractionsActivity.this), PLACE_PICKER_REQUEST);



                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(this, data);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final CharSequence phone = place.getPhoneNumber();


            final LatLng placeLocation = place.getLatLng();
            final String lat = String.valueOf(placeLocation.latitude);
            final String lon = String.valueOf(placeLocation.longitude);

            String attributions = (String) place.getAttributions();
            if (attributions == null) {
                attributions = "";
            }
            String website = String.valueOf(place.getWebsiteUri());
            if(website == null){
                website = "";
            }
            mName.setText(name);
            mAddress.setText(address);
            mWebsite.setText(website);
            mPhone.setText(phone);
            tapForDirections.setText("TAP FOR DIRECTIONS");
            mAttributions.setText(Html.fromHtml(attributions));


            tapForDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?f=d&daddr="+ lat +"," + lon));
                    intent.setComponent(new ComponentName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity"));
                    startActivity(intent);
                }
            });


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
