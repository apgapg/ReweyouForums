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
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import in.reweyou.reweyouforums.CommentActivity;
import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage);

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

    private void handleDataMessage(JSONObject json) {
        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            JSONObject payload = data.getJSONObject("payload");

            Random random = new Random();

            int m = random.nextInt(9999 - 1000) + 1000;
            Intent i;
            if (payload.has("threadid")) {
                //create comment thread, comment/reply like

                i = new Intent(this, CommentActivity.class);
                i.putExtra("threadid", payload.getString("threadid"));
                i.putExtra("from", "n");
                shownoti(m, message, title, i);

            } else if (payload.has("id")) {
                //thread like

                i = new Intent(this, CommentActivity.class);
                i.putExtra("threadid", payload.getString("id"));
                i.putExtra("from", "n");
                shownoti(m, message, title, i);

            } else if (payload.has("ids")) {
                if (userSessionManager.getGroupsilentstatus(payload.getString("groupid"))) {
                    Log.d(TAG, "handleDataMessage: group is silent");
                } else {
                    Log.d(TAG, "handleDataMessage: hereeeee");
                    i = new Intent(this, CommentActivity.class);
                    i.putExtra("threadid", payload.getString("ids"));
                    i.putExtra("from", "n");
                    shownoti(m, message, title, i);

                }
            } else {
                i = new Intent(this, ForumMainActivity.class);
                shownoti(m, message, title, i);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shownoti(int m, String message, String title, Intent i) {
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

    }
}
