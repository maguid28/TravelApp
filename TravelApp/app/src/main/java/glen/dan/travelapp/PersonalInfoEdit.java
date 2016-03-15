package glen.dan.travelapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import glen.dan.travelapp.services.ServerLink;

public class PersonalInfoEdit extends AppCompatActivity {

    EditText name, surname, phone, kin, kinContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info_edit);
    }

    public void onButtonClick(View v){
        if(v.getId() == R.id.save_button) {
            EditText name = (EditText) findViewById(R.id.name_et);
            EditText surname = (EditText) findViewById(R.id.surname_et);
            EditText phone = (EditText) findViewById(R.id.phone_et);
            EditText kin = (EditText) findViewById(R.id.kin_et);
            EditText kinContact = (EditText) findViewById(R.id.kin_contact_et);

            String nameStr = name.getText().toString();
            String surnameStr = surname.getText().toString();
            String phoneStr = phone.getText().toString();
            String kinStr = kin.getText().toString();
            String kinContactStr = kinContact.getText().toString();

            //store details
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("firstname", nameStr);
            editor.putString("surname", surnameStr);
            editor.putString("phone", phoneStr);
            editor.putString("kin", kinStr);
            editor.putString("kinContact", kinContactStr);
            editor.commit();

           // Intent refresh = new Intent(this, PersonalInfo.class);
            this.finish();
            //startActivity(refresh);

            //finish();
        }
    }
}
