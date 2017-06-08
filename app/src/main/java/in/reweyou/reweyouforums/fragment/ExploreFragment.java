package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
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
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.ForumAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;

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
    private JSONArray jsonresponse;
    private List<GroupModel> explorelist = new ArrayList<>();
    private List<GroupModel> explorelistsearch = new ArrayList<>();
    private List<String> groupnamelist = new ArrayList<>();
    private EditText editText;

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
        editText = (EditText) layout.findViewById(R.id.search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtexplore.setVisibility(View.GONE);

                if (s.toString().trim().length() > 0) {

                    explorelistsearch.clear();
                    explorelistsearch.addAll(explorelist);
                    for (int i = explorelistsearch.size() - 1; i >= 0; i--) {
                        if (!explorelistsearch.get(i).getGroupname().contains(s.toString())) {
                            explorelistsearch.remove(i);
                        }
                    }
                    adapterExplore.add(explorelistsearch);

                    if (explorelistsearch.size() == 0) {
                        txtexplore.setVisibility(View.VISIBLE);
                        txtexplore.setText("No matches found. Try searching something different");
                    }

                } else {
                    adapterExplore.add(explorelist);
                    Log.d(TAG, "onTextChanged: " + explorelist.size());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        DividerItemDecoration divider = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.my_custom_divider));
        recyclerViewExplore.addItemDecoration(divider);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, LinearLayoutManager.VERTICAL, false);

        recyclerViewExplore.setLayoutManager(new LinearLayoutManager(mContext));
        adapterExplore = new ForumAdapter(mContext, this);
        recyclerViewExplore.setAdapter(adapterExplore);
        recyclerViewExplore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
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
                getDataFromServer();
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

    private void getDataFromServer() {
        swiperefresh.setRefreshing(true);
        txtexplore.setVisibility(View.GONE);


        AndroidNetworking.post("https://www.reweyou.in/google/discover_groups.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
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
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, jsonarray)) {

                            try {
                                swiperefresh.setRefreshing(false);
                                jsonresponse = jsonarray;
                                parsejsonresponse();

                            } catch (Exception e) {
                                swiperefresh.setRefreshing(false);

                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {

                            Log.d(TAG, "onError: " + anError);
                            swiperefresh.setRefreshing(false);
                            Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void parsejsonresponse() {
        try {
            Gson gson = new Gson();
            JSONObject jsonobject = jsonresponse.getJSONObject(0);

            JSONArray explorejsonarray = jsonobject.getJSONArray("explore");
            explorelist.clear();
            groupnamelist.clear();
            for (int i = 0; i < explorejsonarray.length(); i++) {
                JSONObject jsonObject = explorejsonarray.getJSONObject(i);
                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                groupnamelist.add(groupModel.getGroupname());
                explorelist.add(0, groupModel);
            }


            adapterExplore.add(explorelist);

            if (explorelist.size() == 0) {
                txtexplore.setVisibility(View.VISIBLE);
                txtexplore.setText("No groups followed yet");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshlist() {
        Log.d(TAG, "refreshlist: reached");
        getDataFromServer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (jsonresponse != null)
            outState.putString("response", jsonresponse.toString());
        super.onSaveInstanceState(outState);
    }
}
