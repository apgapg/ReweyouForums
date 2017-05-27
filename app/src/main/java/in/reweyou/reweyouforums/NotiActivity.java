package in.reweyou.reweyouforums;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.adapter.NotiAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.NotiModel;

public class NotiActivity extends AppCompatActivity {

    private static final String TAG = NotiActivity.class.getName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NotiAdapter notiAdapter;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        userSessionManager = new UserSessionManager(this);
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
                getData();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        notiAdapter = new NotiAdapter(this);
        recyclerView.setAdapter(notiAdapter);
        getData();

    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("https://www.reweyou.in/google/notification.php")
                        .addBodyParameter("uid", userSessionManager.getUID())
                        .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                        .setTag("reportnoti")
                        .setPriority(Priority.HIGH)
                        .build()
                        /*.getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "onResponse: " + response);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "onError: " + anError);
                            }
                        });*/
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d(TAG, "onResponse: noti: " + response);
                                swipeRefreshLayout.setRefreshing(false);
                                try {
                                    List<NotiModel> notiModelList = new ArrayList<>();
                                    Gson gson = new Gson();
                                    for (int i = 0; i < response.length(); i++) {
                                        NotiModel notiModel = gson.fromJson(response.getJSONObject(i).toString(), NotiModel.class);
                                        notiModelList.add(notiModel);
                                    }
                                    notiAdapter.add(notiModelList);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(NotiActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();


                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "onError: " + anError);
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(NotiActivity.this, "couldn't connect", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

    }
}
