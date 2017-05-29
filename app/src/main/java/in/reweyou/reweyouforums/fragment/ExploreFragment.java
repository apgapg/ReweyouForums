package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import in.reweyou.reweyouforums.adapter.ForumAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupModel;

/**
 * Created by master on 1/5/17.
 */

public class ExploreFragment extends Fragment {
    private static final String TAG = ExploreFragment.class.getName();
    private Activity mContext;
    private ForumAdapter adapterExplore;
    private RecyclerView recyclerViewExplore;
    private TextView exploretextview;

    private UserSessionManager userSessionManager;
    private TextView txtgroups;
    private TextView txtexplore;
    private SwipeRefreshLayout swiperefresh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerViewExplore = (RecyclerView) layout.findViewById(R.id.explore_recycler_view);
        swiperefresh = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });
        txtexplore = (TextView) layout.findViewById(R.id.txtexplore);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, LinearLayoutManager.VERTICAL, false);

        recyclerViewExplore.setLayoutManager(gridLayoutManager);
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
            getDataFromServer();

        }
    }

    private void getDataFromServer() {
        swiperefresh.setRefreshing(true);
        txtexplore.setVisibility(View.GONE);
        adapterExplore = new ForumAdapter(mContext);
        recyclerViewExplore.setAdapter(adapterExplore);

        AndroidNetworking.post("https://www.reweyou.in/google/discover_groups.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .setTag("fetchgroups")
                .setPriority(Priority.MEDIUM)
                .build()
                /*.getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: "+response);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });*/
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        try {
                            swiperefresh.setRefreshing(false);
                            Gson gson = new Gson();
                            JSONObject jsonobject = jsonarray.getJSONObject(0);
                            Log.d(TAG, "onResponse: " + jsonobject);

                            JSONArray explorejsonarray = jsonobject.getJSONArray("explore");

                            List<GroupModel> explorelist = new ArrayList<GroupModel>();
                            for (int i = 0; i < explorejsonarray.length(); i++) {
                                JSONObject jsonObject = explorejsonarray.getJSONObject(i);
                                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                                explorelist.add(0, groupModel);
                            }


                            adapterExplore.add(explorelist);

                            if (explorelist.size() == 0) {
                                txtexplore.setVisibility(View.VISIBLE);
                            }


                        } catch (Exception e) {
                            swiperefresh.setRefreshing(false);

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        swiperefresh.setRefreshing(false);
                        Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();

                    }
                });


    }

    public void refreshlist() {
        Log.d(TAG, "refreshlist: reached");
        getDataFromServer();
    }
}
