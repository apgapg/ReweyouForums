package in.reweyou.reweyouforums.fcm;

/**
 * Created by Reweyou on 10/14/2016.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.ReviewActivityQR;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String NOTI_TYPE_CHAT = "chat";

    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage);
      /*  if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
*/
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
           /* NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();*/
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            JSONObject payload = data.getJSONObject("payload");

            Log.d(TAG, "handleDataMessage: " + payload.toString());

            Random random = new Random();

            int m = random.nextInt(9999 - 1000) + 1000;
            Intent i = new Intent(this, ReviewActivityQR.class);


            i.putExtra("id", String.valueOf(m));
            Log.d(TAG, "handleDataMessage: id " + m);
            i.putExtra("qrdata", "https://www.reweyou.in/qr/topicid=" + payload.getString("topicid"));


            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), m, i, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(message);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_stat_logo_plain)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setStyle(bigTextStyle)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(message);


            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour < 7 || hour > 23) {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

            } else {
                mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(m, mBuilder.build());
             /*else {
                Intent i = new Intent(Constants.SEND_NOTI_CHANGE_REQUEST);
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                if (postid.equals("0")) {
                    Intent resultIntent = new Intent(this, MyProfile.class);
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("postid", postid);
                    Intent resultIntent = new Intent(this, SinglePostActivity.class);
                    resultIntent.putExtras(bundle);
                    resultIntent.setAction(Long.toString(System.currentTimeMillis()));
                    // check for image attachment
                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    } else {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }

            }*/
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
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
