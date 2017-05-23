package in.reweyou.reweyouforums.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.ImagePicker;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.LoginActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.InterestAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupModel;

/**
 * Created by master on 24/2/17.
 */

public class ProfileFragment extends Fragment {


    private static final String TAG = ProfileFragment.class.getName();
    private Activity mContext;
    private EditText username;
    private ImageView image;
    private TextView continuebutton;
    private ImagePicker imagePicker;
    private ProgressBar progressBar;
    private String idToken;
    private String realname;
    private Uri photoUrl;
    private String serverAuthCode;
    private String uid;
    private ProgressBar progressBarproceed;
    private InterestAdapter interestAdapter;
    private org.apmem.tools.layouts.FlowLayout flowLayout;
    private List<String> interestlist;
    private JSONObject jsonObject;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_profile_login, container, false);

        username = (EditText) layout.findViewById(R.id.username);
        image = (ImageView) layout.findViewById(R.id.image);
        continuebutton = (TextView) layout.findViewById(R.id.continu);
        progressBar = (ProgressBar) layout.findViewById(R.id.pd);
        progressBarproceed = (ProgressBar) layout.findViewById(R.id.pd1);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });
        layout.findViewById(R.id.editphoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGallery();
            }
        });
        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestlist.isEmpty())
                    Toast.makeText(mContext, "Please select an interest", Toast.LENGTH_SHORT).show();
                else
                    uploadDetails();

            }
        });

        flowLayout = (FlowLayout) layout.findViewById(R.id.flowlayout);
        flowLayout.removeAllViews();


        getData();

        return layout;
    }

    private void getData() {
        AndroidNetworking.get("https://www.reweyou.in/google/suggest_groups.php")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Gson gson = new Gson();
                            List<GroupModel> groupModels = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                if (i < 9) {
                                    GroupModel groupModel = gson.fromJson(response.getJSONObject(i).toString(), GroupModel.class);
                                    groupModels.add(groupModel);
                                }


                            }
                            populatedata(groupModels);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void populatedata(final List<GroupModel> groupModels) {
        interestlist = new ArrayList<>();
        Log.d(TAG, "populatedata: " + groupModels.size());
        for (int i = 0; i < groupModels.size(); i++) {
            View view = mContext.getLayoutInflater().inflate(R.layout.item_interest, null);
            final TextView textView = (TextView) view.findViewById(R.id.groupname);
            textView.setText(groupModels.get(i).getGroupname());
            textView.setTag("0");
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag().equals("0")) {
                        v.setTag("1");
                        textView.setTextColor(mContext.getResources().getColor(R.color.white));
                        textView.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_blue));
                        interestlist.add(groupModels.get(finalI).getGroupid());
                    } else {
                        v.setTag("0");
                        textView.setTextColor(mContext.getResources().getColor(R.color.bright_blue));
                        textView.setBackground(mContext.getResources().getDrawable(R.drawable.border_blue));
                        interestlist.remove(groupModels.get(finalI).getGroupid());

                    }
                }
            });
            flowLayout.addView(view);
        }
    }


    private void uploadDetails() {


        continuebutton.setVisibility(View.INVISIBLE);
        progressBarproceed.setVisibility(View.VISIBLE);


        if (interestlist.size() > 0) {
            jsonObject = new JSONObject();
            for (int i = 0; i < interestlist.size(); i++)
                try {
                    jsonObject.put("" + i, interestlist.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        UserSessionManager userSessionManager = new UserSessionManager(mContext);
        Log.d(TAG, "uploadDetails: " + jsonObject);
        AndroidNetworking.post("https://www.reweyou.in/google/signup.php")
                .addBodyParameter("profileurl", photoUrl.toString())
                .addBodyParameter("name", realname)
                .addBodyParameter("userid", idToken)
                .addBodyParameter("uid", uid)
                .addBodyParameter("fcmid", userSessionManager.getfcmid())
                .addBodyParameter("interest", jsonObject.toString())
                .addBodyParameter("username", username.getText().toString())
                .setTag("login")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "onResponse: " + response);

                            UserSessionManager userSessionManager = new UserSessionManager(mContext);
                            userSessionManager.createUserRegisterSession(uid, realname, username.getText().toString(), photoUrl.toString(), response.getString("token"), response.getString("shortinfo"));
                            mContext.startActivity(new Intent(mContext, ForumMainActivity.class));
                            mContext.finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
                            continuebutton.setVisibility(View.VISIBLE);
                            progressBarproceed.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        Toast.makeText(mContext, "Something went wrong! Please try again", Toast.LENGTH_SHORT).show();
                        continuebutton.setVisibility(View.VISIBLE);
                        progressBarproceed.setVisibility(View.INVISIBLE);
                    }
                });

    }

    private void showGallery() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkStoragePermission();

        } else ((LoginActivity) mContext).showPickImage();
    }


    private void checkStoragePermission() {
        Dexter.withActivity(mContext)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ((LoginActivity) mContext).showPickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(mContext, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            mContext = (Activity) context;
        else throw new IllegalArgumentException("Context should be an instance of Activity");
    }

    @Override
    public void onDestroy() {
        mContext = null;
        super.onDestroy();

    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {


        }
    }


    public void onsignin(String id, String idToken, String givenName, Uri photoUrl, String serverAuthCode) {
        this.uid = id;
        this.idToken = idToken;
        this.realname = givenName;
        this.photoUrl = photoUrl;
        this.serverAuthCode = serverAuthCode;
        Log.d("ProfileFragment", "onsignin: reached 1 postition");
        if (username != null) {
            username.setText(givenName);
            username.setSelection(givenName.length());
        }
        Glide.with(ProfileFragment.this).load(photoUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);


    }

    public void onImageChoosen(String s) {
        progressBar.setVisibility(View.VISIBLE);

    }

    public void onImageUpload() {
        //write glide code here


    }
}
