package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import java.util.Collections;
import java.util.Comparator;
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
    private static final int SORT_ALPHABETICALLY = 5;
    private static final int SORT_MEMBERS = 6;
    private static final int SORT_POSTS = 7;
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
    private EditText editText;
    private TextView sort;
    private int checkidposition = -1;

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
                        if (!explorelistsearch.get(i).getGroupname().toLowerCase().contains(s.toString().toLowerCase()) && !explorelistsearch.get(i).getDescription().toLowerCase().contains(s.toString().toLowerCase())) {
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
        sort = (TextView) layout.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showsortdialog();
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
                .setPriority(Priority.LOW)
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
                                editText.setVisibility(View.VISIBLE);
                                sort.setVisibility(View.VISIBLE);
                                checkidposition = -1;

                                editText.setText("");
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
            for (int i = 0; i < explorejsonarray.length(); i++) {
                JSONObject jsonObject = explorejsonarray.getJSONObject(i);
                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                explorelist.add(0, groupModel);
            }


            adapterExplore.add(explorelist);
            explorelistsearch.addAll(explorelist);

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

    private void showsortdialog() {
        final LayoutInflater li = LayoutInflater.from(mContext);
        View confirmDialog = li.inflate(R.layout.dialog_sort_groups, null);


        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        RadioGroup rGroup = (RadioGroup) confirmDialog.findViewById(R.id.radioGroup1);
        rGroup.check(checkidposition);
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioalpha:
                        checkidposition = R.id.radioalpha;

                        sortCollections(SORT_ALPHABETICALLY);
                        alertDialog.dismiss();
                        break;
                    case R.id.radiomembers:
                        checkidposition = R.id.radiomembers;

                        sortCollections(SORT_MEMBERS);
                        alertDialog.dismiss();

                        break;
                    case R.id.radioposts:
                        checkidposition = R.id.radioposts;

                        sortCollections(SORT_POSTS);
                        alertDialog.dismiss();

                        break;
                }
            }
        });
        alertDialog.show();


    }

    private void sortCollections(int code) {
        if (code == SORT_ALPHABETICALLY) {
            Collections.sort(explorelistsearch, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return o1.getGroupname().compareToIgnoreCase(o2.getGroupname());
                }
            });
            adapterExplore.add(explorelistsearch);
        } else if (code == SORT_MEMBERS) {
            Collections.sort(explorelistsearch, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return (Integer.parseInt(o2.getMembers()) - Integer.parseInt(o1.getMembers()));
                }
            });
            adapterExplore.add(explorelistsearch);


        } else if (code == SORT_POSTS) {
            Collections.sort(explorelistsearch, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return (Integer.parseInt(o2.getThreads()) - Integer.parseInt(o1.getThreads()));
                }
            });
            adapterExplore.add(explorelistsearch);

        }


    }

    public void scrolltotop() {
        try {
            recyclerViewExplore.scrollToPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
