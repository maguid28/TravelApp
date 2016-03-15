package glen.dan.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PersonalInfo extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  name = sharedPreferences.getString("firstname", null);
        String  surname = sharedPreferences.getString("surname", null);
        String  phone = sharedPreferences.getString("phone", null);
        String  kin = sharedPreferences.getString("kin", null);
        String  kinContact = sharedPreferences.getString("kinContact", null);

        TextView nameText = (TextView) findViewById(R.id.first_name_tv);
        TextView surnameText = (TextView) findViewById(R.id.last_name_tv);
        TextView phoneText = (TextView) findViewById(R.id.phone_tv);
        TextView kinText = (TextView) findViewById(R.id.kin_tv);
        TextView kinContactText = (TextView) findViewById(R.id.kin_contact_tv);

        nameText.setText(name);
        surnameText.setText(surname);
        phoneText.setText(phone);
        kinText.setText(kin);
        kinContactText.setText(kinContact);
    }


    //if user clicks edit
    public void onButtonClick(View v){
        if(v.getId() == R.id.edit_button) {
            //this is the activity we need to launch
            Intent i = new Intent(this, PersonalInfoEdit.class);
            startActivity(i);
        }
    }
}
