package in.reweyou.reweyouforums;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.adapter.GroupMembersAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupMemberModel;

public class GroupMembers extends AppCompatActivity {
    private static final String TAG = GroupMembers.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private UserSessionManager userSessionManager;
    private GroupMembersAdapter groupMembersAdapter;
    private boolean isadmin;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Group Members");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMembersData(getIntent().getStringExtra("groupid"));
            }
        });
        userSessionManager = new UserSessionManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (userSessionManager.getUID().equals(getIntent().getStringExtra("admin")))
            isadmin = true;
        groupMembersAdapter = new GroupMembersAdapter(this, getIntent().getStringExtra("groupid"), isadmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupMembersAdapter);

        getMembersData(getIntent().getStringExtra("groupid"));

    }

    private void getMembersData(String groupid) {
        swipeRefreshLayout.setRefreshing(true);
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
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d(TAG, "onResponse: members " + response);
                            List<GroupMemberModel> list = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                GroupMemberModel groupMemberModel = gson.fromJson(response.getJSONObject(i).toString(), GroupMemberModel.class);
                                list.add(groupMemberModel);
                            }

                            groupMembersAdapter.add(list);

                        } catch (Exception e) {
                            swipeRefreshLayout.setRefreshing(false);

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                });
    }


}
