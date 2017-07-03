package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.YourGroupsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;
import io.paperdb.Paper;

/**
 * Created by master on 1/5/17.
 */

public class YourGroupsFragment extends Fragment {
    private static final String TAG = YourGroupsFragment.class.getName();
    private static final int SORT_ALPHABETICALLY = 4;
    private static final int SORT_MEMBERS = 5;
    private static final int SORT_POSTS = 6;
    private Activity mContext;
    private RecyclerView recyclerViewYourGroups;
    private YourGroupsAdapter adapterYourGroups;
    private UserSessionManager userSessionManager;
    private TextView txtgroups;
    private SwipeRefreshLayout swiperefresh;
    private JSONArray jsonresponse;
    private EditText editText;
    private List<GroupModel> followlist = new ArrayList<>();
    private List<GroupModel> explorelistsearch = new ArrayList<>();
    private int checkidposition = -1;
    private TextView sort;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);
        Paper.init(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_your_groups, container, false);
        recyclerViewYourGroups = (RecyclerView) layout.findViewById(R.id.explore_recycler_view_your_groups);
        swiperefresh = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });
        txtgroups = (TextView) layout.findViewById(R.id.txtgroups);
        editText = (EditText) layout.findViewById(R.id.search);
        sort = (TextView) layout.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showsortdialog();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtgroups.setVisibility(View.GONE);

                if (s.toString().trim().length() > 0) {

                    explorelistsearch.clear();
                    explorelistsearch.addAll(followlist);
                    for (int i = explorelistsearch.size() - 1; i >= 0; i--) {
                        if (!explorelistsearch.get(i).getGroupname().toLowerCase().contains(s.toString().toLowerCase()) && !explorelistsearch.get(i).getDescription().toLowerCase().contains(s.toString().toLowerCase())) {
                            explorelistsearch.remove(i);
                        }
                    }
                    adapterYourGroups.add(explorelistsearch);

                    if (explorelistsearch.size() == 0) {
                        txtgroups.setVisibility(View.VISIBLE);
                        txtgroups.setText("No matches found. Try searching something different");
                    }

                } else {
                    adapterYourGroups.add(followlist);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


       /* DividerItemDecoration divider = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(mContext, R.drawable.my_custom_divider));
        recyclerViewYourGroups.addItemDecoration(divider);
*/
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, LinearLayoutManager.VERTICAL, false);

        recyclerViewYourGroups.setLayoutManager(gridLayoutManager);
        //recyclerViewYourGroups.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerViewYourGroups.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
        adapterYourGroups = new YourGroupsAdapter(mContext, this);
        recyclerViewYourGroups.setAdapter(adapterYourGroups);
        ImageView imageView = (ImageView) layout.findViewById(R.id.backarrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ForumMainActivity) mContext).setFeedPage();
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
        txtgroups.setVisibility(View.GONE);


        AndroidNetworking.post("https://www.reweyou.in/google/discover_groups.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .setTag("fetchgroups")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, jsonarray)) {

                            try {
                                checkidposition = userSessionManager.getvaluefromsharedpref("checkyourgroup");
                                editText.setText("");
                                editText.setVisibility(View.VISIBLE);
                                sort.setVisibility(View.VISIBLE);
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
            followlist.clear();
            JSONArray followjsonarray = jsonobject.getJSONArray("followed");
            Log.d(TAG, "parsejsonresponse: " + followjsonarray);

            for (int i = 0; i < followjsonarray.length(); i++) {
                JSONObject jsonObject = followjsonarray.getJSONObject(i);
                GroupModel groupModel = gson.fromJson(jsonObject.toString(), GroupModel.class);
                followlist.add(0, groupModel);
            }

            Log.d(TAG, "parsejsonresponse: " + checkidposition);
            sortCollections2(checkidposition);


            adapterYourGroups.add(followlist);
            Paper.book().write("yourgroups", followlist);

            if (followlist.size() == 0) {
                txtgroups.setVisibility(View.VISIBLE);
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
                        userSessionManager.putinsharedpref("checkyourgroup", checkidposition);
                        sortCollections(SORT_ALPHABETICALLY);
                        alertDialog.dismiss();
                        break;
                    case R.id.radiomembers:
                        checkidposition = R.id.radiomembers;
                        userSessionManager.putinsharedpref("checkyourgroup", checkidposition);
                        sortCollections(SORT_MEMBERS);
                        alertDialog.dismiss();

                        break;
                    case R.id.radioposts:
                        checkidposition = R.id.radioposts;
                        userSessionManager.putinsharedpref("checkyourgroup", checkidposition);
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
            Collections.sort(followlist, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return o1.getGroupname().compareToIgnoreCase(o2.getGroupname());
                }
            });
            adapterYourGroups.add(followlist);
        } else if (code == SORT_MEMBERS) {
            Collections.sort(followlist, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return (Integer.parseInt(o2.getMembers()) - Integer.parseInt(o1.getMembers()));
                }
            });
            adapterYourGroups.add(followlist);


        } else if (code == SORT_POSTS) {
            Collections.sort(followlist, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return (Integer.parseInt(o2.getThreads()) - Integer.parseInt(o1.getThreads()));
                }
            });
            adapterYourGroups.add(followlist);

        }


    }

    private void sortCollections2(int code) {
        if (code == R.id.radioalpha) {
            Collections.sort(followlist, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return o1.getGroupname().compareToIgnoreCase(o2.getGroupname());
                }
            });
        } else if (code == R.id.radiomembers) {
            Collections.sort(followlist, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return (Integer.parseInt(o2.getMembers()) - Integer.parseInt(o1.getMembers()));
                }
            });


        } else if (code == R.id.radioposts) {
            Collections.sort(followlist, new Comparator<GroupModel>() {
                @Override
                public int compare(GroupModel o1, GroupModel o2) {
                    return (Integer.parseInt(o2.getThreads()) - Integer.parseInt(o1.getThreads()));
                }
            });

        }


    }

    public void scrolltotop() {
        try {
            recyclerViewYourGroups.scrollToPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
