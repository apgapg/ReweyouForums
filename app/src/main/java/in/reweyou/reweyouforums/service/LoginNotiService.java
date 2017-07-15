package in.reweyou.reweyouforums.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import in.reweyou.reweyouforums.LoginActivity;
import in.reweyou.reweyouforums.R;

/**
 * Created by master on 15/7/17.
 */

public class LoginNotiService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shownoti();

            }
        }, 30 * 60 * 1000);
    }

    private void shownoti() {
        Intent i = new Intent(this, LoginActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 323, i, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText("Start exploring your interest. Login to join different Groups. See what others are sharing...");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_stat_logo_plain)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(bigTextStyle)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentTitle("ReweyouForums")
                .setContentText("Start exploring your interest. Login to join different Groups. See what others are sharing...");


        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(323, mBuilder.build());

    }
}
