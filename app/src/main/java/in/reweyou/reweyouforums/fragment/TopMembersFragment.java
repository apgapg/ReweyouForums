package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.TopGroupMembersAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.TopGroupMemberModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;

/**
 * Created by master on 24/2/17.
 */

public class TopMembersFragment extends Fragment {


    private static final String TAG = TopMembersFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private UserSessionManager userSessionManager;

    private LinearLayoutManager linearLayoutManager;
    private String groupid;
    private TopGroupMembersAdapter topGroupMembersAdapter;
    private JSONArray jsonresponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.content_top_members, container, false);
        Log.d(TAG, "onCreateView: called");
        this.groupid = getArguments().getString("groupid");
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });


        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);

        userSessionManager = new UserSessionManager(mContext);
        topGroupMembersAdapter = new TopGroupMembersAdapter(mContext);
        recyclerView.setAdapter(topGroupMembersAdapter);

        return layout;
    }

    public void getData() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("https://www.reweyou.in/google/top_list.php")
                        .addBodyParameter("uid", userSessionManager.getUID())
                        .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                        .addBodyParameter("groupid", groupid)

                        .setTag("commenttop1")

                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                if (new NetworkHandler().isActivityAlive(TAG, mContext, response)) {

                                    try {
                                        if (!mContext.isFinishing()) {

                                            jsonresponse = response;
                                            swipeRefreshLayout.setRefreshing(false);

                                            parsejsonresponse();

                                        } else Log.e(TAG, "onResponse: activity finishing");
                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {

                                    swipeRefreshLayout.setRefreshing(false);

                                    Log.d(TAG, "onError: " + anError);
                                    Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }, 500);
    }

    private void parsejsonresponse() {
        Gson gson = new Gson();
        List<TopGroupMemberModel> list = new ArrayList<>();

        try {
            for (int i = 0; i < jsonresponse.length(); i++) {
                JSONObject json = jsonresponse.getJSONObject(i);
                TopGroupMemberModel coModel = gson.fromJson(json.toString(), TopGroupMemberModel.class);
                list.add(coModel);

            }

            topGroupMembersAdapter.add(list);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @Override
    public void onAttach(Context context) {
        Log.d(TAG, "onAttach: called");
        super.onAttach(context);
        if (context instanceof Activity)
            mContext = (Activity) context;
        else throw new IllegalArgumentException("Context should be an instance of Activity");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: called");
        mContext = null;
        super.onDestroy();


    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: called");
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: called");
        if (jsonresponse != null)
            outState.putString("response", jsonresponse.toString());
        super.onSaveInstanceState(outState);
    }


}
