package glen.dan.travelapp;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import glen.dan.travelapp.services.ServerLink;

public class LoginActivity extends Activity implements OnClickListener {


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if user is not logged in
        if(!already_logged_in()) {
            setContentView(R.layout.activity_login);
        }
        //if user is already logged in
        else {
            // Close the activity and start main activity
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    private boolean already_logged_in() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  data = sharedPreferences.getString("loggedIn", "Logged Out") ;
        Toast.makeText(this,data, Toast.LENGTH_LONG).show();
        //if user has not logged in before
        return (!data.equals("Logged Out"));
    }




    public void onButtonClick(View v){
        //launch login activity
        if(v.getId() == R.id.Blogin){
            EditText name = (EditText) findViewById(R.id.TFuname);
            EditText password = (EditText) findViewById(R.id.TFpassword);

            String nameStr = name.getText().toString();
            String passwordStr = password.getText().toString();
            String method = "login";

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //store username
            editor.putString("username", nameStr);
            editor.apply();

            if(nameStr.equals("")) {
                String message = "Please enter your username";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            else {
                ServerLink serverLink = new ServerLink(this);
                serverLink.execute(method, nameStr, passwordStr);
            }
        }
        //launch sign up activity
        if(v.getId() == R.id.Bsignup){
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
    }

}

