package glen.dan.travelapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import glen.dan.travelapp.services.LoginSignUpBackgroundTask;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    public void onSignupClick(View v) {
        if(v.getId() == R.id.Bsignupbutton){

            EditText name = (EditText) findViewById(R.id.TFname);
            EditText email = (EditText) findViewById(R.id.TFemail);
            EditText pass1 = (EditText) findViewById(R.id.TFpass1);
            EditText pass2 = (EditText) findViewById(R.id.TFpass2);

            String namestr = name.getText().toString();
            String emailstr = email.getText().toString();
            String pass1str = pass1.getText().toString();
            String pass2str = pass2.getText().toString();

            //Check if email and password is valid
            if(!isValidEmail(emailstr) || !pass1str.equals(pass2str) || pass1str.equals("") || namestr.equals("")){

                if(namestr.equals("")){
                    Toast pass = Toast.makeText(SignUpActivity.this, getString(R.string.entername), Toast.LENGTH_SHORT);
                    pass.show();
                }
                else if(!isValidEmail(emailstr)) {
                    Toast pass = Toast.makeText(SignUpActivity.this, getString(R.string.notavalidemail), Toast.LENGTH_SHORT);
                    pass.show();
                }
                else if(pass1str.equals("")) {
                    Toast pass = Toast.makeText(SignUpActivity.this, getString(R.string.enterapassword), Toast.LENGTH_SHORT);
                    pass.show();
                }
                else {
                    //popup message if passwords do not match
                    Toast pass = Toast.makeText(SignUpActivity.this, getString(R.string.passwordsdontmatch), Toast.LENGTH_SHORT);
                    pass.show();
                }

            }
            else{
                String method = "register";
                LoginSignUpBackgroundTask loginSignUpBackgroundTask = new LoginSignUpBackgroundTask(this);
                loginSignUpBackgroundTask.execute(method, namestr, emailstr, pass1str);
                finish();
            }
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
