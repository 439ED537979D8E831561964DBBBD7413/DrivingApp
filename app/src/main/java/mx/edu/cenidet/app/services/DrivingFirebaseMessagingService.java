
package mx.edu.cenidet.app.services;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Random;

import mx.edu.cenidet.app.activities.AlertMapDetailActivity;
import mx.edu.cenidet.app.utils.Config;
import mx.edu.cenidet.app.utils.NotificationUtils;
import mx.edu.cenidet.cenidetsdk.httpmethods.Response;
import mx.edu.cenidet.app.R;

public class DrivingFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = DrivingFirebaseMessagingService.class.getSimpleName();

    private Response response = new Response();
    private String severity = "";
    private String location = "";
    private String subcategory = "";
    private String description = "";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null) {
            Log.d(TAG, "REMOTE NULL");
            return;
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();
    }




    private void handleNotification(String title, String message) {

        Intent resultIntent = new Intent(getApplicationContext(), AlertMapDetailActivity.class);
        showNotificationMessage(getApplicationContext(), title, message, new Date().toString(), resultIntent);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();

    }

    private void handleDataMessage(JSONObject json) {
        try {

            JSONObject alert = json.getJSONObject("alert");
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("subcategory", alert.getString("subCategory"));
            pushNotification.putExtra("description", alert.getString("description"));
            pushNotification.putExtra("location", alert.getString("location"));
            pushNotification.putExtra("severity", alert.getString("severity"));
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            Intent resultIntent = new Intent(getApplicationContext(), AlertMapDetailActivity.class);
            resultIntent.putExtra("subcategory", alert.getString("subCategory"));
            resultIntent.putExtra("description", alert.getString("description"));
            resultIntent.putExtra("location", alert.getString("location"));
            resultIntent.putExtra("severity", alert.getString("severity"));

            showNotificationMessage(
                    getApplicationContext(),
                    alert.getString("category"),
                    alert.getString("subCategory"),
                    new Date().toString(), resultIntent);

            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void showNotification(String messageTitle ,String messageBody, String severity) {
        Intent intent = new Intent(this, AlertMapDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
        //       PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = null;

        int notificationId = new Random().nextInt(60000);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setColorized(true)
                .setSmallIcon(R.drawable.ic_car)
                .setVibrate(new long[] {100, 250, 100, 500})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        switch (severity){
            case "informational":
                notificationBuilder
                        .setColor(Color.parseColor("#3498db"));
                break;
            case "low":
                notificationBuilder
                        .setColor(Color.parseColor("#2c3e50"));
                break;
            case "medium":
                notificationBuilder
                        .setColor(Color.parseColor("#f1c40f"));
                break;
            case "high":
                notificationBuilder
                        .setColor(Color.parseColor("#e67e22"));
                break;
            case "critical":
                notificationBuilder
                        .setColor(Color.parseColor("#c0392b"));
                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId , notificationBuilder.build());
    }

}