package in.reweyou.reweyouforums.utils;

import android.app.Activity;
import android.util.Log;

/**
 * Created by master on 4/6/17.
 */

public class NetworkHandler {

    private static final String TAG = NetworkHandler.class.getName();


    public NetworkHandler() {

    }


    private static void logwarning(String tag, String message) {
        Log.w(tag, message);
    }

    private static void logresponse(String tag, String s) {
        Log.d(tag, s);
    }

    public boolean isActivityAlive(String tag, Activity activity, Object s) {
        if (s == null)
            s = "Response is Empty";
        if (activity != null) {
            if (!activity.isFinishing()) {
                logresponse(tag, s.toString());
                return true;
            } else {
                logwarning(tag, "Activity is finishing");
                return false;
            }
        } else {
            logwarning(tag, "Activity context is null");
            return false;
        }
    }
}
