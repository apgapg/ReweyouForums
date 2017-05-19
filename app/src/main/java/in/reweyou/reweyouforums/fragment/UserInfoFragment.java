package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import in.reweyou.reweyouforums.EditProfileActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 24/2/17.
 */

public class UserInfoFragment extends Fragment {


    private static final String TAG = UserInfoFragment.class.getName();
    private Activity mContext;
    private ImageView image;
    private ProgressBar progressBar;

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

        layout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, EditProfileActivity.class);
                startActivityForResult(i, Utils.REQ_CODE_EDIT_PROFILE);
            }
        });
        TextView username = (TextView) layout.findViewById(R.id.username);
        TextView userstatus = (TextView) layout.findViewById(R.id.userStatus);

        UserSessionManager userSessionManager = new UserSessionManager(mContext);

        username.setText(userSessionManager.getUsername());
        userstatus.setText(userSessionManager.getShortinfo());

        Glide.with(mContext).load(userSessionManager.getProfilePicture()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

        return layout;
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
        Toast.makeText(mContext, "code profile refresh method", Toast.LENGTH_SHORT).show();
    }
}
