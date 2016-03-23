package glen.dan.travelapp;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import glen.dan.travelapp.services.LoginSignUpBackgroundTask;

public class LoginActivity extends Activity implements OnClickListener {


    Spinner dropdown;

    @Override
    public void onClick(View v) {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.titlescreen);
        new CountDownTimer(2000,1000){
            @Override
            public void onTick(long millisUntilFinished){}

            @Override
            public void onFinish(){
                //if user is not logged in
                if(!already_logged_in()) {
                    setContentView(R.layout.activity_login);
                }
                //if user is already logged in
                else {
                    // Close the activity and start main activity
                    Intent i = new Intent(LoginActivity.this, GeoFencingActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }.start();
    }

    private boolean already_logged_in() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  data = sharedPreferences.getString("loggedIn", getString(R.string.loggedout)) ;
        Toast.makeText(this,data, Toast.LENGTH_LONG).show();
        //if user has not logged in before
        return (!data.equals(getString(R.string.loggedout)));
    }




    public void onButtonClick(View v){
        //launch login activity
        if(v.getId() == R.id.Blogin){
            EditText name = (EditText) findViewById(R.id.TFuname);
            EditText password = (EditText) findViewById(R.id.TFpassword);

            String nameStr = name.getText().toString();
            String passwordStr = password.getText().toString();
            String method = "login";


            if(nameStr.equals("")) {
                String message = getString(R.string.pleaseenteremailaddress);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
            else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                //store username
                editor.putString("username", nameStr);
                editor.apply();
                LoginSignUpBackgroundTask loginSignUpBackgroundTask = new LoginSignUpBackgroundTask(this);
                loginSignUpBackgroundTask.execute(method, nameStr, passwordStr);
            }
        }
        //launch sign up activity
        if(v.getId() == R.id.Bsignup){
            Intent i = new Intent(this, SignUpActivity.class);
            startActivity(i);
        }
    }

}

