package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.FeeedsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.ThreadModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

/**
 * Created by master on 24/2/17.
 */

public class MainThreadsFragment extends Fragment {


    private static final String TAG = MainThreadsFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerView;
    private FeeedsAdapter feeedsAdapter;
    private UserSessionManager userSessionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomTabsHelperFragment mCustomTabsHelperFragment;
    private FloatingActionButton fab;
    private JSONArray jsonresponse;

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

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ForumMainActivity) mContext).startCreateActivity();

            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        feeedsAdapter = new FeeedsAdapter(mContext, this);
        recyclerView.setAdapter(feeedsAdapter);


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
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.post("https://www.reweyou.in/google/list_threads.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, jsonarray)) {

                            try {
                                swipeRefreshLayout.setRefreshing(false);
                                jsonresponse = jsonarray;
                                parsejsonresponse();


                            } catch (Exception e) {
                                swipeRefreshLayout.setRefreshing(false);

                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {

                            Log.d(TAG, "onError: " + anError);
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

    private void parsejsonresponse() {
        try {
            Gson gson = new Gson();
            List<ThreadModel> threadlist = new ArrayList<>();
            for (int i = 0; i < jsonresponse.length(); i++) {
                JSONObject jsonObject = jsonresponse.getJSONObject(i);
                ThreadModel groupModel = gson.fromJson(jsonObject.toString(), ThreadModel.class);
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
}
