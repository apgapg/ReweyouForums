package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.FeeedsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
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
    private Button createpost;
    private CardView joingroupcard;
    private FloatingActionButton fab;
    private boolean isfollow;

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

        createpost = (Button) layout.findViewById(R.id.createpost);

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
                fab.setVisibility(View.INVISIBLE);

            }


        }
    }

    private void getData() {

        recyclerView.setVisibility(View.VISIBLE);
        nopostcard.setVisibility(View.GONE);
        joingroupcard.setVisibility(View.GONE);

        AndroidNetworking.post("https://www.reweyou.in/google/list_threads.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .addBodyParameter("groupid", getArguments().getString("groupid"))
                .setTag("report")
                .setPriority(Priority.HIGH)
                .build()
                .getAsParsed(new TypeToken<List<ThreadModel>>() {
                }, new ParsedRequestListener<List<ThreadModel>>() {

                    @Override
                    public void onResponse(final List<ThreadModel> list) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, list)) {

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (list.size() == 0) {
                                        nopostcard.setVisibility(View.VISIBLE);
                                    } else
                                        feeedsAdapter.add(list);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, mContext, anError)) {

                            Log.e(TAG, "run: error: " + anError.getErrorDetail());

                        }
                    }
                });
    }


    public void refreshList() {
        if (getArguments().getBoolean("follow"))
            getData();
        else {
            joingroupcard.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);

        }


    }

    public void refreshList1(boolean isfollowed) {
        this.isfollow = isfollowed;
        if (isfollowed)
            getData();
        else {
            nopostcard.setVisibility(View.INVISIBLE);
            joingroupcard.setVisibility(View.VISIBLE);
            fab.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }
}
