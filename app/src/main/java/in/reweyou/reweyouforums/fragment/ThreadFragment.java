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
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.FeeedsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.ThreadModel;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

/**
 * Created by master on 24/2/17.
 */

public class ThreadFragment extends Fragment {


    private static final String TAG = ThreadFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerView;
    private FeeedsAdapter feeedsAdapter;
    private UserSessionManager userSessionManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomTabsHelperFragment mCustomTabsHelperFragment;
    private FloatingActionButton fab;
    private String threadid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);
        mCustomTabsHelperFragment = CustomTabsHelperFragment.attachTo(this);

        this.threadid = getArguments().getString("threadid");
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

        fab.setVisibility(View.GONE);
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

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {
            feeedsAdapter = new FeeedsAdapter(mContext, this);
            recyclerView.setAdapter(feeedsAdapter);

            getData();


        }
    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.post("https://www.reweyou.in/google/single_thread.php")
                .addBodyParameter("threadid", threadid)
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("comment3")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse: resp: " + response);
                        swipeRefreshLayout.setRefreshing(false);
                        List<ThreadModel> list = new ArrayList<>();
                        try {
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                ThreadModel threadModel = gson.fromJson(response.getJSONObject(i).toString(), ThreadModel.class);
                                list.add(threadModel);
                                feeedsAdapter.add(list);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                            swipeRefreshLayout.setRefreshing(false);

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "run: error: " + anError.getErrorDetail());
                        Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);


                    }
                });
    }


    public void refreshList(String threadid) {
        this.threadid = threadid;
        getData();

    }
}
