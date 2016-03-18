
package glen.dan.travelapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;


public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super(GeofenceTransitionsIntentService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        if(event!=null) {

            if (event.hasError()) {
                int errorCode = event.getErrorCode();
                Log.e("Geofence feature", "Location Services error: " + errorCode);
            }
            else {

                //Accumulate a list of event geofences
                List<String> geofenceIds = new ArrayList<>();
                for (Geofence geofence : event.getTriggeringGeofences()) {
                    geofenceIds.add(geofence.getRequestId());
                }
                onEnteredGeofences(geofenceIds);

            }
        }

    }


    private void onEnteredGeofences(List<String> geofenceIds) {
          //for (String geofenceId : geofenceIds) {
          //  String geofenceName = "";

            String contextText = "You have entered a warning area";

            // 1. Create a NotificationManager
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            // 2. Create a PendingIntent for AllGeofencesActivity
            Intent intent = new Intent(this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // 3. Create and send a notification
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.plane)
                    .setContentTitle("Safe Travel")
                    .setContentText(contextText)
                    .setContentIntent(pendingNotificationIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contextText))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();
            notificationManager.notify(0, notification);
        //}
    }


}
