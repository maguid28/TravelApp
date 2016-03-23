package glen.dan.travelapp.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import glen.dan.travelapp.MapsActivity;


public class SubmitWarningBackgroundTask extends AsyncTask<String, String, String> {

    Context ctx;

    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private static final String TAG_SUCCESS = "success";

    public SubmitWarningBackgroundTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Loading, Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }


    protected String doInBackground(String... args) {

        String method = args[0];
        //create url string
        String url_create_entry = "http://student.computing.dcu.ie/~maguid28/create_entry.php";

        if(method.equals("entry")) {
            String username = args[1];
            String type_of_warning = args[2];
            String description = args[3];
            String longitude = args[4];
            String latitude = args[5];

            // Build Parameters in ArrayList
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("type_of_warning", type_of_warning));
            params.add(new BasicNameValuePair("description", description));
            params.add(new BasicNameValuePair("longitude", longitude));
            params.add(new BasicNameValuePair("latitude", latitude));

            // make request
            JSONObject json = jsonParser.makeHttpRequest(url_create_entry, "POST", params);

            // check log for response
            Log.d("Create Response", json.toString());

            // check for success
            try
            {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // successfully created database entry

                    Intent i = new Intent(ctx, MapsActivity.class);
                    ctx.startActivity(i);
                    ((Activity)ctx).runOnUiThread(new Runnable() {
                        public void run() {
                            String message = "Submission Success";
                            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return null;
                }
                else {
                    ((Activity)ctx).runOnUiThread(new Runnable() {
                        public void run() {
                            String message = "Failed, try again later";
                            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected void onPostExecute(String file_url) {
        // dismiss the dialog once done
        pDialog.dismiss();
    }
}



