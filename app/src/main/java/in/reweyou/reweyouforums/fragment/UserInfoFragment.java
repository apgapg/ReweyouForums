package in.reweyou.reweyouforums.fragment;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.EditProfileActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.GroupBadegsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupBadgeModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 24/2/17.
 */

public class UserInfoFragment extends Fragment {


    private static final String TAG = UserInfoFragment.class.getName();
    private Activity mContext;
    private ImageView image;
    private ProgressBar progressBar;
    private UserSessionManager userSessionManager;
    private TextView username;
    private TextView userstatus;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GroupBadegsAdapter groupBadegsAdapter;
    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_info, container, false);
        image = (ImageView) layout.findViewById(R.id.image);
        progressBar = (ProgressBar) layout.findViewById(R.id.pd);

        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        layout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EditProfileActivity.class);
                mContext.startActivityForResult(i, Utils.REQ_CODE_EDIT_PROFILE);
            }
        });
        username = (TextView) layout.findViewById(R.id.message);
        userstatus = (TextView) layout.findViewById(R.id.userStatus);
        imageView = (ImageView) layout.findViewById(R.id.dashimg);

        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        rotate.setDuration(5000);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        imageView.setAnimation(rotate);
        userSessionManager = new UserSessionManager(mContext);

        username.setText(userSessionManager.getUsername());
        userstatus.setText(userSessionManager.getShortinfo());

        Glide.with(mContext).load(userSessionManager.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        groupBadegsAdapter = new GroupBadegsAdapter(mContext);
        recyclerView.setAdapter(groupBadegsAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        getMembersData();


        layout.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showbadgedialog();
            }
        });

        //  setcoloranimation();
        return layout;
    }

    private void setcoloranimation() {
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(R.color.main_background_pink, R.color.bright_blue);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Drawable background = imageView.getBackground();
                if (background instanceof GradientDrawable) {
                    // cast to 'ShapeDrawable'
                    GradientDrawable shapeDrawable = (GradientDrawable) background;
                    shapeDrawable.setColor((Integer) valueAnimator.getAnimatedValue());

                }
            }
        });

        anim.setDuration(4000);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.start();

    }

    private void getMembersData() {
        AndroidNetworking.post("https://www.reweyou.in/google/show_profile.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, response)) {

                            try {
                                Log.d(TAG, "onResponse: members " + response);
                                List<GroupBadgeModel> list = new ArrayList<>();
                                Gson gson = new Gson();
                                for (int i = 0; i < response.length(); i++) {
                                    GroupBadgeModel groupMemberModel = gson.fromJson(response.getJSONObject(i).toString(), GroupBadgeModel.class);
                                    list.add(groupMemberModel);
                                }

                                groupBadegsAdapter.add(list);
                                //  populatedata(list);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {
                        }
                    }
                });
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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {


        }
    }


    public void refreshprofile() {
        username.setText(userSessionManager.getUsername());
        userstatus.setText(userSessionManager.getShortinfo());

        Glide.with(mContext).load(userSessionManager.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);


    }

    private void showbadgedialog() {
        //Creating a LayoutInflater object for the dialog box
        final LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_badges, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);


        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

}
