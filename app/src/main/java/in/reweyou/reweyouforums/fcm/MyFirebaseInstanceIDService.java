package in.reweyou.reweyouforums.fcm;

/**
 * Created by Reweyou on 10/14/2016.
 */

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import in.reweyou.reweyouforums.classes.UserSessionManager;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "fcm: " + refreshedToken);

        storeRegIdInPref(refreshedToken);


    }


    private void storeRegIdInPref(String token) {

        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.setfcmid(token);
    }
}