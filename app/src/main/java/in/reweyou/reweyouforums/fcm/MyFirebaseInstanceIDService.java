package in.reweyou.reweyouforums.fcm;

/**
 * Created by Reweyou on 10/14/2016.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.reweyou.reweyouforums.classes.UserSessionManager;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";
    public static final String URL_UPDATE_TOKEN = "https://www.reweyou.in/reweyou/firebasetoken.php";
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "sendRegistrationToServer: " + refreshedToken);

        storeRegIdInPref(refreshedToken);


    }


    private void storeRegIdInPref(String token) {
        // FirebaseMessaging.getInstance().subscribeToTopic("news");
       /* SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();*/

        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.setfcmid(token);
    }
}