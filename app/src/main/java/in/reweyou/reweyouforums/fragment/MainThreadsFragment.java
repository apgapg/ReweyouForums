package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.reweyou.reweyouforums.CreatePostActivity;
import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.FeeedsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.customView.RecyclerViewPager;
import in.reweyou.reweyouforums.model.CommentModel;
import in.reweyou.reweyouforums.model.ReplyCommentModel;
import in.reweyou.reweyouforums.model.ThreadModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;
import in.reweyou.reweyouforums.utils.Utils;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

/**
 * Created by master on 24/2/17.
 */

public class MainThreadsFragment extends Fragment {


    private static final String TAG = MainThreadsFragment.class.getName();
    private Activity mContext;
    private RecyclerViewPager recyclerView;
    private FeeedsAdapter feeedsAdapter;
    private UserSessionManager userSessionManager;
    private CustomTabsHelperFragment mCustomTabsHelperFragment;
    private FloatingActionButton fab;
    private JSONArray jsonresponse;
    private List<ThreadModel> threadlist;
    private RelativeLayout fetchingdatacont;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);
        try {
            mCustomTabsHelperFragment = CustomTabsHelperFragment.attachTo(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main_threads, container, false);

        recyclerView = (RecyclerViewPager) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int i, int i1) {
                try {
                    ((ForumMainActivity) mContext).onFeedCardChange(threadlist.get(i1).getGroupname());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        feeedsAdapter = new FeeedsAdapter(mContext, this);
        recyclerView.setAdapter(feeedsAdapter);

        layout.findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CreatePostActivity.class);
                i.putExtra("frommain", true);
                mContext.startActivityForResult(i, Utils.REQ_CODE_CREATE_FROM_FORUMACTVITY);
            }
        });

        fetchingdatacont = (RelativeLayout) layout.findViewById(R.id.fetchingdatacontainer);

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
        Glide.get(this.getContext()).clearMemory();

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
            if (savedInstanceState == null)
                getData();
            else {
                Log.d(TAG, "onActivityCreated: reacheed here");
                try {
                    jsonresponse = new JSONArray(savedInstanceState.getString("response"));
                    parsejsonresponse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void getData() {
        fetchingdatacont.setVisibility(View.VISIBLE);

        AndroidNetworking.post("https://www.reweyou.in/google/list_thread_new.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, jsonarray)) {
                            fetchingdatacont.setVisibility(View.GONE);
                            try {
                                jsonresponse = jsonarray;
                                parsejsonresponse();


                            } catch (Exception e) {

                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {
                            fetchingdatacont.setVisibility(View.GONE);

                            Log.d(TAG, "onError: " + anError);
                            //Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                            ((ForumMainActivity) mContext).showNetworkErrorSnackBar();
                        }
                    }

                });

    }

    private void parsejsonresponse() {
        try {
            Gson gson = new Gson();
            threadlist = new ArrayList<>();
            for (int i = 0; i < jsonresponse.length(); i++) {
                JSONObject jsonObject = jsonresponse.getJSONObject(i);
                ThreadModel groupModel = gson.fromJson(jsonObject.toString(), ThreadModel.class);


                List<Object> list = new ArrayList<>();

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("commentlist");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject json = jsonArray.getJSONObject(jsonArray.length() - 1 - j);
                        CommentModel coModel = gson.fromJson(json.toString(), CommentModel.class);
                        list.add(coModel);

                        if (json.has("reply")) {
                            JSONArray jsonReply = json.getJSONArray("reply");

                            for (int k = 0; k < jsonReply.length(); k++) {
                                JSONObject jsontemp = jsonReply.getJSONObject(jsonReply.length() - 1 - k);
                                ReplyCommentModel temp = gson.fromJson(jsontemp.toString(), ReplyCommentModel.class);
                                list.add(temp);
                            }
                        }

                    }

                    groupModel.setcommentlistshow(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                threadlist.add(0, groupModel);
            }
            Collections.reverse(threadlist);

            feeedsAdapter.add(threadlist);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void refreshList() {
        getData();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (jsonresponse != null)
            outState.putString("response", jsonresponse.toString());
        super.onSaveInstanceState(outState);
    }

    public void changenextcard() {
        if (feeedsAdapter.getItemCount() == recyclerView.getCurrentPosition() + 1) {

        } else
            recyclerView.smoothScrollToPosition(recyclerView.getCurrentPosition() + 1);
    }
}
