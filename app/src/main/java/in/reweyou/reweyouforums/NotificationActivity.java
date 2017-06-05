package in.reweyou.reweyouforums;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.adapter.NotiAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.NotiModel;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = NotificationActivity.class.getName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NotiAdapter notiAdapter;
    private UserSessionManager userSessionManager;
    private AlertDialogBox alertDialogBox;
    private boolean dataloaded;

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
        alertDialogBox = new AlertDialogBox(this, "Mark all as read?", "Do you want to mark all notifications as read?", "yes", "no") {
            @Override
            public void onNegativeButtonClick(DialogInterface dialog) {
                dialog.dismiss();
            }

            @Override
            public void onPositiveButtonClick(DialogInterface dialog) {
                dialog.dismiss();
                sendrequestforseenchange();
            }
        };

    }

    public void getData() {

        Log.d(TAG, "getData: dddd");
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
                                    if (notiModelList.size() > 0) {
                                        dataloaded = true;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(NotificationActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();


                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e(TAG, "onError: " + anError);
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(NotificationActivity.this, "couldn't connect", Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forum, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_noti) {
            // do something here
            if (dataloaded) {
                alertDialogBox.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendrequestforseenchange() {
        AndroidNetworking.post("https://www.reweyou.in/google/notification_read.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("nid", "")
                .addBodyParameter("type", "All")
                .setTag("groupcreate")
                .setPriority(Priority.HIGH)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: noti: " + response);
                getData();
                setResult(RESULT_OK);

            }

            @Override
            public void onError(ANError anError) {
                Log.d(TAG, "onError: " + anError);

            }
        });
    }


}
