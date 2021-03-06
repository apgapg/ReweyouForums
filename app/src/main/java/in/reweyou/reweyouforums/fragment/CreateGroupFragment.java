package in.reweyou.reweyouforums.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;

import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;

/**
 * Created by master on 24/2/17.
 */

public class CreateGroupFragment extends Fragment {


    private static final String TAG = CreateGroupFragment.class.getName();
    private Activity mContext;
    private EditText description;
    private EditText groupname;
    private TextView create;
    private ImageView img;
    private TextView imgtext;
    private String imgUrl;
    private UserSessionManager userSessionManager;
    private ProgressBar pd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSessionManager = new UserSessionManager(mContext);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_create, container, false);
        if (mContext != null) {
            keyboardListener();
            img = (ImageView) layout.findViewById(R.id.image);
            imgtext = (TextView) layout.findViewById(R.id.imgtext);
            groupname = (EditText) layout.findViewById(R.id.groupname);
            description = (EditText) layout.findViewById(R.id.description);
            pd = (ProgressBar) layout.findViewById(R.id.progressBar);

            create = (TextView) layout.findViewById(R.id.create);
            create.setEnabled(false);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = mContext.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    compressImage();
                }
            });

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkStoragePermission();

                    } else ((ForumMainActivity) mContext).showPickImage(3);
                }
            });

            initTextWatchers();
        }
        return layout;
    }

    private void compressImage() {
        if (imgUrl != null) {
            Glide.with(this).load(imgUrl).asBitmap().toBytes().into(new SimpleTarget<byte[]>(200, 200) {
                @Override
                public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                    String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
                    uploadGroup(encodedImage);
                }
            });
        } else uploadGroup(null);
    }

    private void uploadGroup(String encodedImage) {

        create.setVisibility(View.INVISIBLE);
        pd.setVisibility(View.VISIBLE);
        groupname.setEnabled(false);
        description.setEnabled(false);


        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupname", groupname.getText().toString().trim().trim());
        hashMap.put("description", description.getText().toString().trim());
        hashMap.put("admin", userSessionManager.getUID());
        hashMap.put("authtoken", userSessionManager.getAuthToken());
        if (imgUrl != null)
            hashMap.put("image", encodedImage);

        AndroidNetworking.post("https://www.reweyou.in/google/create_groups.php")
                .addBodyParameter(hashMap)
                .setTag("groupcreate")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: group upload: " + response);
                        if (response.equals("group created")) {
                            imgtext.setVisibility(View.VISIBLE);
                            img.setImageResource(0);

                            description.setText("");
                            groupname.setText("");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((ForumMainActivity) mContext).showYourGroupsFragment();
                                }
                            }, 500);

                        } else
                            Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                        create.setVisibility(View.VISIBLE);
                        pd.setVisibility(View.INVISIBLE);
                        groupname.setEnabled(true);
                        description.setEnabled(true);


                    }


                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                        Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                        create.setVisibility(View.VISIBLE);
                        pd.setVisibility(View.INVISIBLE);
                        groupname.setEnabled(true);
                        groupname.setFocusable(true);
                        description.setEnabled(true);
                        description.setFocusable(true);

                    }
                });
    }

    private void initTextWatchers() {
        groupname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (description.getText().toString().trim().length() > 0 && s.toString().trim().length() > 0) {
                    updateCreateTextUI(true);
                } else updateCreateTextUI(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (groupname.getText().toString().trim().length() > 0 && s.toString().trim().length() > 0) {
                    updateCreateTextUI(true);
                } else updateCreateTextUI(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCreateTextUI(boolean b) {
        if (b) {
            create.setEnabled(true);
            create.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
            create.setBackground(mContext.getResources().getDrawable(R.drawable.border_pink));
        } else {
            create.setEnabled(false);
            create.setTextColor(mContext.getResources().getColor(R.color.grey_create));
            create.setBackground(mContext.getResources().getDrawable(R.drawable.border_grey));
        }
    }

    private void keyboardListener() {
        mContext.findViewById(R.id.rootlayout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mContext != null) {
                    Rect r = new Rect();
                    mContext.findViewById(R.id.rootlayout).getWindowVisibleDisplayFrame(r);
                    int heightDiff = mContext.findViewById(R.id.rootlayout).getRootView().getHeight() - (r.bottom - r.top);

                    // int heightDiff = findViewById(R.id.main_content).getRootView().getHeight() - findViewById(R.id.main_content).getHeight();

                    //Log.d(TAG, "onGlobalLayout: height"+heightDiff+"   "+findViewById(R.id.main_content).getRootView().getHeight()+    "    "+(r.bottom - r.top));
                    if (heightDiff > pxFromDp(mContext, 150)) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        mContext.findViewById(R.id.tabLayout).setVisibility(View.GONE);
                        mContext.findViewById(R.id.line).setVisibility(View.GONE);
                        mContext.findViewById(R.id.tabLayout).setAlpha(0);


                    } else {
                        //ok now we know the keyboard is down...
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (mContext != null) {
                                    mContext.findViewById(R.id.tabLayout).setVisibility(View.VISIBLE);
                                    mContext.findViewById(R.id.line).setVisibility(View.VISIBLE);
                                    mContext.findViewById(R.id.tabLayout).animate().alpha(1).setDuration(150).start();
                                }
                            }
                        }, 150);


                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: called");
        if (context instanceof Activity)
            mContext = (Activity) context;
        else throw new IllegalArgumentException("Context should be an instance of Activity");
    }

    @Override
    public void onDestroy() {
        mContext = null;
        Log.d(TAG, "onDestroy: called");
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

    private void checkStoragePermission() {
        Dexter.withActivity(mContext)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        ((ForumMainActivity) mContext).showPickImage(3);
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

    public void onImageChoosen(String s) {
        this.imgUrl = s;
        Glide.with(mContext).load(s).into(img);
        imgtext.setVisibility(View.GONE);
    }


}
