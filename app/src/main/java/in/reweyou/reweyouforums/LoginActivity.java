package in.reweyou.reweyouforums;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kbeanie.multipicker.api.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.customView.NonSwipeableViewPager;
import in.reweyou.reweyouforums.fragment.ChooseInterestFragment;
import in.reweyou.reweyouforums.fragment.LoginFragment;
import in.reweyou.reweyouforums.service.LoginNotiService;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 4;
    private static final String TAG = LoginActivity.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private GoogleApiClient mGoogleApiClient;
    private NonSwipeableViewPager nonSwipeableViewPager;
    private PagerAdapter pagerAdapter;
    private ImagePicker imagePicker;
    private UserSessionManager userSessionManager;
    private ProgressDialog progressDialog;
    private GoogleSignInAccount acct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserSessionManager userSessionManager = new UserSessionManager(this);
        if (userSessionManager.isUserLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, ForumMainActivity.class));
            finish();
        }


        setContentView(R.layout.content_login);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))

                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        nonSwipeableViewPager = (NonSwipeableViewPager) findViewById(R.id.swipeViewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        nonSwipeableViewPager.setAdapter(pagerAdapter);

        if (!userSessionManager.isUserLoggedIn()) {
            startService(new Intent(this, LoginNotiService.class));

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: failed");
    }


    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
            Log.d(TAG, "handleSignInResult: LoginName: " + acct.getGivenName());
            nonSwipeableViewPager.setCurrentItem(1);
            // uploadsignin(acct);
        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "handleSignInResult: signed out");
        }
    }

    private void uploadsignin(final GoogleSignInAccount acct, List<String> selectlist) {
        JSONObject jsonObject = new JSONObject();
        for (int i = 0; i < selectlist.size(); i++) {
            try {
                jsonObject.put("" + i, selectlist.get(i));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Signing in. Please wait!");
        progressDialog.show();
        userSessionManager = new UserSessionManager(this);

        AndroidNetworking.post("https://www.reweyou.in/google/signup.php")
                .addBodyParameter("profileurl", acct.getPhotoUrl().toString())
                .addBodyParameter("name", acct.getDisplayName())
                .addBodyParameter("email", acct.getEmail())
                .addBodyParameter("userid", acct.getIdToken())
                .addBodyParameter("uid", acct.getId())
                .addBodyParameter("fcmid", userSessionManager.getfcmid())
                .addBodyParameter("interest", jsonObject.toString())
                .addBodyParameter("username", acct.getDisplayName())
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        try {
                            Log.d(TAG, "onResponse: " + response);
                            userSessionManager.createUserRegisterSession(acct.getId(), acct.getDisplayName(), response.getString("username"), response.getString("profileurl"), response.getString("token"), response.getString("shortinfo"));
                            stopService(new Intent(LoginActivity.this, LoginNotiService.class));
                            startActivity(new Intent(LoginActivity.this, ForumMainActivity.class));

                            finish();


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();

                        Log.d(TAG, "onError: " + anError);
                        Toast.makeText(LoginActivity.this, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();

                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty");
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }

    public void onproceedclick(List<String> selectlist) {
        uploadsignin(acct, selectlist);
    }


    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {


            if (position == 0)
                return new LoginFragment();
            else if (position == 1)
                return new ChooseInterestFragment();
            else return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {
            return 2;
        }


    }


}
