package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.LoginActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.ChooseInterestAdapter;
import in.reweyou.reweyouforums.model.GroupModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;

/**
 * Created by master on 24/2/17.
 */

public class ChooseInterestFragment extends Fragment {


    private static final String TAG = ChooseInterestFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerview;
    private JSONArray jsonresponse;
    private SwipeRefreshLayout swiperefresh;
    private EditText editText;
    private List<GroupModel> followlist = new ArrayList<>();
    private List<GroupModel> explorelistsearch = new ArrayList<>();
    private ChooseInterestAdapter chooseInterestAdapter;
    private TextView txtexplore;
    private TextView btnproceed;
    private List<String> selectlist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_choose_interest, container, false);
        recyclerview = (RecyclerView) layout.findViewById(R.id.recycler_view);
        swiperefresh = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        txtexplore = (TextView) layout.findViewById(R.id.txtexplore);
        btnproceed = (TextView) layout.findViewById(R.id.proceed);
        btnproceed.setEnabled(false);
        btnproceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) mContext).onproceedclick(selectlist);
            }
        });
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });
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
                    explorelistsearch.addAll(followlist);
                    for (int i = explorelistsearch.size() - 1; i >= 0; i--) {
                        if (!explorelistsearch.get(i).getGroupname().toLowerCase().contains(s.toString().toLowerCase())) {
                            explorelistsearch.remove(i);
                        }
                    }
                    chooseInterestAdapter.add(explorelistsearch);

                    if (explorelistsearch.size() == 0) {
                        txtexplore.setVisibility(View.VISIBLE);
                        txtexplore.setText("No matches found. Try searching something different");
                    }

                } else {
                    chooseInterestAdapter.add(followlist);
                    Log.d(TAG, "onTextChanged: " + followlist.size());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(gridLayoutManager);

        recyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        chooseInterestAdapter = new ChooseInterestAdapter(mContext, ChooseInterestFragment.this);
        recyclerview.setAdapter(chooseInterestAdapter);


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

    private void parsejsonresponse() {
        try {
            txtexplore.setVisibility(View.GONE);

            Gson gson = new Gson();
            followlist.clear();

            for (int i = 0; i < jsonresponse.length(); i++) {
                JSONObject jsonObject = jsonresponse.getJSONObject(i);
                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                followlist.add(0, groupModel);
            }


            chooseInterestAdapter.add(followlist);
            explorelistsearch.addAll(followlist);
            swiperefresh.setEnabled(false);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (jsonresponse != null)
            outState.putString("response", jsonresponse.toString());
        super.onSaveInstanceState(outState);
    }

    private void getDataFromServer() {
        swiperefresh.setRefreshing(true);


        AndroidNetworking.post("https://www.reweyou.in/google/suggest_groups.php")
                .setTag("fetchgoups")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, jsonarray)) {

                            try {
                                editText.setText("");
                                editText.setVisibility(View.VISIBLE);
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
                            txtexplore.setVisibility(View.VISIBLE);
                            txtexplore.setText("Please check your network. Swipe to refresh list.");
                            swiperefresh.setRefreshing(false);
                            Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }

    public void updateselectlist(String groupId) {
        selectlist.add(groupId);
        updateUIproceedbtn(true);
    }

    private void updateUIproceedbtn(boolean b) {
        if (b) {
            btnproceed.setEnabled(true);
            btnproceed.setBackgroundColor(mContext.getResources().getColor(R.color.main_background_pink));
        } else {
            btnproceed.setEnabled(false);
            btnproceed.setBackgroundColor(Color.parseColor("#e0e0e0"));
        }

    }

    public void removefromselectlist(String groupId) {
        selectlist.remove(groupId);
        if (selectlist.size() == 0)
            updateUIproceedbtn(false);
    }
}
