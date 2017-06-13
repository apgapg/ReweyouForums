package in.reweyou.reweyouforums.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import in.reweyou.reweyouforums.LoginActivity;


/**
 * Created by Reweyou on 12/17/2015.
 */
public class UserSessionManager {

    public static final String KEY_NAME = "username";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PIC = "pic";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_MOBILE_NUMBER = "mobilenumber";
    public static final String KEY_LOGIN_LOCATION = "loginlocation";
    public static final String KEY_CUSTOM_LOCATION = "customlocation";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_LOGIN_FULLNAME = "fullname";
    // Sharedpref file name
    private static final String PREFER_NAME = "ReweyouPref";
    // All Shared Preferences Keys
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    private static final String KEY_AUTH_TOKEN = "authtoken";
    private static final String KEY_DEVICE_ID = "deviceid";
    private static final String TAG = UserSessionManager.class.getName();
    private static final String KEY_REALNAME = "realname";
    private static final String KEY_UID = "uid";
    private static final String KEY_SHORT_INFO = "info";
    private static final String KEY_FCM_ID = "fcmid";
    private static final String KEY_BADGE = "badge";
    // Shared Preferences reference
    SharedPreferences pref;
    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    private Gson gson = new Gson();
    private List<String> likesList;
    private String FIRST_LOAD_TUT = "firsttimeload";
    private boolean mobileNumber;
    private String deviceid;
    private String shortinfo;


    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }


    public String getUsername() {
        return pref.getString(KEY_NAME, "");
    }

    public void setUsername(String fullname) {
        editor.putString(KEY_NAME, fullname);
        editor.commit();
    }

    public void createUserRegisterSession(String uid, String real, String username, String photoUrl, String authtoken, String shortinfo) {

        editor.putBoolean(IS_USER_LOGIN, true);

        editor.putString(KEY_REALNAME, real);
        editor.putString(KEY_PIC, photoUrl);
        editor.putString(KEY_NAME, username);
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_AUTH_TOKEN, authtoken);
        editor.putString(KEY_SHORT_INFO, shortinfo);

        // commit changes
        editor.commit();
    }

    public String getUID() {
        return pref.getString(KEY_UID, "");
    }

    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, "");
    }

    public void setAuthToken(String token) {
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.commit();
    }

    public String getProfilePicture() {
        return pref.getString(KEY_PIC, "");
    }

    public void setProfilePicture(String image) {
        editor.putString(KEY_PIC, image);
        editor.commit();
    }

    public boolean checkLoginSplash() {
        // Check login status
        return this.isUserLoggedIn();
    }

    public void logoutUser() {
        Toast.makeText(_context, "invalid session! please login again", Toast.LENGTH_SHORT).show();
        logoutUser1();
    }

    public void logoutUser1() {

        // Clearing all user data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    // Check for login
    public boolean isUserLoggedIn() {
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public String getKeyAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, "");
    }


    public void setFirstLoad() {
        editor.putBoolean(FIRST_LOAD_TUT, true);
        editor.commit();
    }

    public boolean getFirstLoad() {
        return pref.getBoolean(FIRST_LOAD_TUT, false);
    }


    public boolean getMobileNumber() {
        return mobileNumber;
    }

    public void setFirstLoadReview() {
    }

    public boolean getFirstLoadReview() {

        return false;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public String getShortinfo() {
        return pref.getString(KEY_SHORT_INFO, "");
    }

    public void setShortInfo(String shortInfo) {
        editor.putString(KEY_SHORT_INFO, shortInfo);
        editor.commit();
    }

    public void setfcmid(String token) {
        editor.putString(KEY_FCM_ID, token);
        editor.commit();
    }

    public String getfcmid() {
        return pref.getString(KEY_FCM_ID, "");
    }

    public String getBadge() {
        return pref.getString(KEY_BADGE, "");
    }

    public void setBadge(String badge) {
        editor.putString(KEY_BADGE, badge);
        editor.commit();
    }

    public boolean getGroupsilentstatus(String groupid) {
        return pref.getBoolean("groupid" + groupid, false);
    }

    public void savegroupsilent(String groupid, boolean b) {
        editor.putBoolean("groupid" + groupid, b);
        editor.commit();
    }

    public void putinsharedpref(String key, int value) {

        editor.putInt(key, value);
        editor.commit();
    }

    public int getvaluefromsharedpref(String key) {
        return pref.getInt(key, -1);
    }
}