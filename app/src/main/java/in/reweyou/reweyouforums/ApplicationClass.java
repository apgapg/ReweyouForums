package in.reweyou.reweyouforums;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.crash.FirebaseCrash;

import java.util.concurrent.TimeUnit;

import in.reweyou.reweyouforums.utils.Utils;
import okhttp3.OkHttpClient;

/**
 * Created by Reweyou on 2/25/2016.
 */
public class ApplicationClass extends MultiDexApplication {
    public static final String TAG = ApplicationClass.class.getSimpleName();

    private static ApplicationClass mInstance;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private GoogleAnalytics sAnalytics;
    private Tracker sTracker;

    public static synchronized ApplicationClass getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Log.d(TAG, "onCreate: calledapp");

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        Utils.setBackgroundColor(getApplicationContext());
        Utils.setpxFromDp(getApplicationContext());

        sAnalytics = GoogleAnalytics.getInstance(this);
        getDefaultTracker();


        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        if (BuildConfig.DEBUG)
            GoogleAnalytics.getInstance(this).setDryRun(true);

        if (BuildConfig.DEBUG)
            FirebaseCrash.setCrashCollectionEnabled(false);

        if (BuildConfig.DEBUG) { //disable for debug
        }
    }

    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);

    }


}
