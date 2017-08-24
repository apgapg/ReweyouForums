package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.FeeedsAdapter;
import in.reweyou.reweyouforums.civil.MainActivity;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.CommentModel;
import in.reweyou.reweyouforums.model.ReplyCommentModel;
import in.reweyou.reweyouforums.model.ThreadModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;

/**
 * Created by master on 24/2/17.
 */

public class GroupThreadsFragment extends Fragment {


    private static final String TAG = GroupThreadsFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerView;
    private FeeedsAdapter feeedsAdapter;
    private UserSessionManager userSessionManager;
    private CardView nopostcard;
    private TextView createpost;
    private CardView joingroupcard;
    private boolean isfollow;
    private RelativeLayout fetchingdatacont;
    private JSONArray jsonresponse;
    private List<ThreadModel> threadlist;
    private String groupname;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: djwjnjwendjwenjdwjdn");
        View layout = inflater.inflate(R.layout.fragment_group_threads, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        nopostcard = (CardView) layout.findViewById(R.id.nopostcard);
        joingroupcard = (CardView) layout.findViewById(R.id.joincard);
        isfollow = getArguments().getBoolean("follow");
        groupname = getArguments().getString("groupname");
        fetchingdatacont = (RelativeLayout) layout.findViewById(R.id.fetchingdatacontainer);

        createpost = (TextView) layout.findViewById(R.id.create);

        if (groupname != null) {
            if (groupname.equals("Civil Engineers")) {
                layout.findViewById(R.id.design).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.design_line).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.design).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, MainActivity.class));
                    }
                });
            }
        }

        createpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        ((GroupActivity) mContext).startCreateActivity();
                    }
                });
            }
        });
        layout.findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((GroupActivity) mContext).startCreateActivity();

            }
        });

        layout.findViewById(R.id.joinbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((GroupActivity) mContext).showfirstpage();

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

            if (getArguments().getBoolean("follow"))
                getData();
            else {
                joingroupcard.setVisibility(View.VISIBLE);
                fetchingdatacont.setVisibility(View.GONE);
                recyclerView.setVisibility(View.INVISIBLE);
                createpost.setVisibility(View.INVISIBLE);
            }


        }
    }

    private void getData() {

        recyclerView.setVisibility(View.VISIBLE);
        nopostcard.setVisibility(View.GONE);
        joingroupcard.setVisibility(View.GONE);

        AndroidNetworking.post("https://www.reweyou.in/google/list_thread_new.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .addBodyParameter("groupid", getArguments().getString("groupid"))
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonarray) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, jsonarray)) {
                            createpost.setVisibility(View.VISIBLE);

                            fetchingdatacont.setVisibility(View.GONE);
                            try {
                                jsonresponse = jsonarray;
                                parsejsonresponse();


                            } catch (Exception e) {

                                e.printStackTrace();

                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {
                            fetchingdatacont.setVisibility(View.GONE);
                            createpost.setVisibility(View.INVISIBLE);

                            Log.d(TAG, "onError: " + anError);
                            //Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                            ((GroupActivity) mContext).refreshfeed();
                        }
                    }

                });

    }




    public void refreshList1(boolean isfollowed) {
        this.isfollow = isfollowed;
        if (isfollowed)
            getData();
        else {
            nopostcard.setVisibility(View.INVISIBLE);
            joingroupcard.setVisibility(View.VISIBLE);
            fetchingdatacont.setVisibility(View.GONE);

            recyclerView.setVisibility(View.INVISIBLE);
            createpost.setVisibility(View.INVISIBLE);
        }

    }

    private void parsejsonresponse() {
        try {
            Gson gson = new Gson();
            threadlist = new ArrayList<>();
            for (int i = 0; i < jsonresponse.length(); i++) {
                JSONObject jsonObject = jsonresponse.getJSONObject(i);
                ThreadModel groupModel = gson.fromJson(jsonObject.toString(), ThreadModel.class);


                List<Object> list = new ArrayList<>();

                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("commentlist");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject json = jsonArray.getJSONObject(jsonArray.length() - 1 - j);
                        CommentModel coModel = gson.fromJson(json.toString(), CommentModel.class);
                        list.add(coModel);

                        if (json.has("reply")) {
                            JSONArray jsonReply = json.getJSONArray("reply");

                            for (int k = 0; k < jsonReply.length(); k++) {
                                JSONObject jsontemp = jsonReply.getJSONObject(jsonReply.length() - 1 - k);
                                ReplyCommentModel temp = gson.fromJson(jsontemp.toString(), ReplyCommentModel.class);
                                list.add(temp);
                            }
                        }

                    }

                    groupModel.setcommentlistshow(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                threadlist.add(0, groupModel);
            }
            Collections.reverse(threadlist);

            if (threadlist.size() == 0) {
                nopostcard.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            } else
                feeedsAdapter.add(threadlist);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
