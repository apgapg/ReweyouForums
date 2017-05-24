package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.EditActivity;
import in.reweyou.reweyouforums.GroupMembers;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupMemberModel;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 24/2/17.
 */

public class GroupInfoFragment extends Fragment {


    private static final String TAG = GroupInfoFragment.class.getName();
    private Activity mContext;
    private String groupid;
    private boolean isfollowed;
    private String groupdes = "";
    private String grouprules = "";
    private String adminuid = "";
    private ImageView shineeffect;
    private UserSessionManager userSessionManager;

    private RelativeLayout joincontainer;
    private TextView shortdes;
    private TextView tvRules;
    private ImageView img;
    private FlowLayout flowLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group_info, container, false);

        TextView edit = (TextView) layout.findViewById(R.id.edit);


        userSessionManager = new UserSessionManager(mContext);
        final TextView btnfollow = (TextView) layout.findViewById(R.id.btn_follow);
        joincontainer = (RelativeLayout) layout.findViewById(R.id.joincontainer);
        img = (ImageView) layout.findViewById(R.id.image);
        final TextView groupname = (TextView) layout.findViewById(R.id.groupname);
        final TextView textrules = (TextView) layout.findViewById(R.id.textrules);
        shortdes = (TextView) layout.findViewById(R.id.shortdescription);
        tvRules = (TextView) layout.findViewById(R.id.description);
        final TextView members = (TextView) layout.findViewById(R.id.members);
        TextView threads = (TextView) layout.findViewById(R.id.threads);
        shineeffect = (ImageView) layout.findViewById(R.id.img_shine);
        final ProgressBar pd = (ProgressBar) layout.findViewById(R.id.pd);
        TextView membersmore = (TextView) layout.findViewById(R.id.membersmore);
        membersmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, GroupMembers.class);
                i.putExtra("groupid", groupid);
                i.putExtra("admin", adminuid);
                mContext.startActivity(i);
            }
        });

        try {
            groupname.setText(getArguments().getString("groupname"));
            members.setText(getArguments().getString("members"));
            groupid = getArguments().getString("groupid");
            isfollowed = getArguments().getBoolean("follow");
            shortdes.setText(getArguments().getString("description"));
            groupdes = getArguments().getString("description");
            grouprules = getArguments().getString("rules");
            tvRules.setText(getArguments().getString("rules"));
            adminuid = getArguments().getString("admin");
            threads.setText(getArguments().getString("threads"));
            if (getArguments().getString("rules").isEmpty()) {
                if (userSessionManager.getUID().equals(adminuid)) {

                    tvRules.setText("*Edit to update rules*");

                } else {
                    textrules.setVisibility(View.GONE);
                    tvRules.setVisibility(View.GONE);
                }
            } else tvRules.setText(grouprules);

            if (adminuid.equals(userSessionManager.getUID())) {
                joincontainer.setVisibility(View.GONE);
            } else if (isfollowed) {
                btnfollow.setText("Leave");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

            } else {
                btnfollow.setText("Join");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
            }


            flowLayout = (FlowLayout) layout.findViewById(R.id.flowlayout);
            getMembersData();


            Glide.with(mContext).load(getArguments().getString("image")).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnfollow.setVisibility(View.INVISIBLE);
                    pd.setVisibility(View.VISIBLE);

                    AndroidNetworking.post("https://www.reweyou.in/google/follow_groups.php")
                            .addBodyParameter("groupid", groupid)
                            .addBodyParameter("groupname", getArguments().getString("groupname"))
                            .addBodyParameter("uid", userSessionManager.getUID())
                            .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                            .setTag("uploadpost")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.equals("Followed")) {

                                        btnfollow.setText("Leave");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        Toast.makeText(mContext, "You are now following '" + getArguments().getString("groupname") + "'", Toast.LENGTH_SHORT).show();
                                        mContext.setResult(Activity.RESULT_OK);
                                        // FirebaseMessaging.getInstance().subscribeToTopic(getArguments().getString("groupname"));

                                    } else if (response.equals("Unfollowed")) {

                                        btnfollow.setText("Join");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        mContext.setResult(Activity.RESULT_OK);
                                        //   FirebaseMessaging.getInstance().unsubscribeFromTopic(getArguments().getString("groupname"));

                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d(TAG, "onError: " + anError);
                                    Toast.makeText(mContext, "Connection problem", Toast.LENGTH_SHORT).show();
                                    btnfollow.setVisibility(View.VISIBLE);
                                    pd.setVisibility(View.GONE);
                                }
                            });

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!userSessionManager.getUID().equals(adminuid)) {
            edit.setVisibility(View.INVISIBLE);

        } else {
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, EditActivity.class);
                    i.putExtra("description", groupdes);
                    i.putExtra("image", getArguments().getString("image"));
                    i.putExtra("rules", grouprules);
                    i.putExtra("groupid", groupid);
                    mContext.startActivityForResult(i, Utils.REQ_CODE_EDIT_GROUP_ACTIVITY);
                }
            });
        }

        if (!isfollowed)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shineeffect.animate().translationXBy(Utils.convertpxFromDp(28 + 90 + 28)).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(750).start();
                }
            }, 700);
        return layout;
    }

    private void getMembersData() {
        Log.d(TAG, "getMembersData: " + groupid);
        AndroidNetworking.post("https://www.reweyou.in/google/list_members.php")
                .addBodyParameter("groupid", groupid)
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "onResponse: members " + response.length());
                            List<GroupMemberModel> list = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                GroupMemberModel groupMemberModel = gson.fromJson(response.getJSONObject(i).toString(), GroupMemberModel.class);
                                list.add(groupMemberModel);
                            }

                            populatedata(list);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                    }
                });
    }

    private void populatedata(final List<GroupMemberModel> groupModels) {
        for (int i = 0; i < groupModels.size(); i++) {
            View view = mContext.getLayoutInflater().inflate(R.layout.item_group_info_members, null);
            final ImageView image = (ImageView) view.findViewById(R.id.img);
            Glide.with(mContext).load(groupModels.get(i).getImageurl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

            flowLayout.addView(view);
        }
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


    public void refreshDetails(String description, String rules, String image) {
        try {
            if (description != null)
                shortdes.setText(description);
            if (rules != null)
                tvRules.setText(rules);
            if (image != null)
                Glide.with(mContext).load(image).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
