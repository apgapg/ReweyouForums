package in.reweyou.reweyouforums.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.CommentsAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.CommentModel;
import in.reweyou.reweyouforums.model.ReplyCommentModel;

/**
 * Created by master on 24/2/17.
 */

public class CommentFragment extends Fragment {


    private static final String TAG = CommentFragment.class.getName();
    private Activity mContext;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private ImageView send;
    private EditText editText;
    private TextView nocommenttxt;
    private UserSessionManager userSessionManager;
    private TextView replyheader;
    private CommentsAdapter adapterComment;
    private String threadid;
    private String tempcommentid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.content_comment, container, false);
        Log.d(TAG, "onCreateView: reeeeeeec");
        this.threadid = getArguments().getString("threadid");
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        recyclerView = (RecyclerView) layout.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        editText = (EditText) layout.findViewById(R.id.edittext);
        send = (ImageView) layout.findViewById(R.id.send);
        nocommenttxt = (TextView) layout.findViewById(R.id.commenttxt);

        userSessionManager = new UserSessionManager(mContext);
        replyheader = (TextView) layout.findViewById(R.id.t2);

        adapterComment = new CommentsAdapter(mContext);
        recyclerView.setAdapter(adapterComment);

        initSendButton();
        initTextWatcherEditText();

        getData();
        return layout;
    }

    public void getData() {
        swipeRefreshLayout.setRefreshing(true);
        replyheader.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.post("https://www.reweyou.in/google/list_comments.php")
                        .addBodyParameter("uid", userSessionManager.getUID())
                        .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                        .addBodyParameter("threadid", threadid)

                        .setTag("report")

                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                swipeRefreshLayout.setRefreshing(false);

                                Log.d(TAG, "onResponse: " + response);
                                Gson gson = new Gson();
                                List<Object> list = new ArrayList<>();

                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject json = response.getJSONObject(response.length() - 1 - i);
                                        CommentModel coModel = gson.fromJson(json.toString(), CommentModel.class);
                                        list.add(coModel);

                                        if (json.has("reply")) {
                                            JSONArray jsonReply = json.getJSONArray("reply");

                                            for (int j = 0; j < jsonReply.length(); j++) {
                                                JSONObject jsontemp = jsonReply.getJSONObject(jsonReply.length() - 1 - j);
                                                ReplyCommentModel temp = gson.fromJson(jsontemp.toString(), ReplyCommentModel.class);
                                                list.add(temp);
                                            }
                                        }

                                    }

                                    adapterComment.add(list);
                                    if (list.size() == 0) {
                                        nocommenttxt.setVisibility(View.VISIBLE);


                                    } else nocommenttxt.setVisibility(View.INVISIBLE);


                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            recyclerView.smoothScrollToPosition(0);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                                }


                            }

                            @Override
                            public void onError(ANError anError) {
                                swipeRefreshLayout.setRefreshing(false);

                                Log.d(TAG, "onError: " + anError);
                                Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }, 500);
    }

    private void initSendButton() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                if (editText.getText().toString().trim().length() > 0) {
                    editText.setEnabled(false);
                    send.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("uid", userSessionManager.getUID());
                    hashMap.put("username", userSessionManager.getUsername());
                    hashMap.put("imageurl", userSessionManager.getProfilePicture());
                    hashMap.put("threadid", threadid);
                    hashMap.put("image", "");
                    String url;
                    if (replyheader.getVisibility() == View.VISIBLE) {
                        url = "https://www.reweyou.in/google/create_reply.php";
                        // hashMap.put("commentid", tempcommentid);
                        hashMap.put("commentid", tempcommentid);
                        hashMap.put("reply", editText.getText().toString());

                        Log.d(TAG, "onClick: reply");
                    } else {
                        url = "https://www.reweyou.in/google/create_comments.php";
                        hashMap.put("comment", editText.getText().toString().trim());

                    }


                    AndroidNetworking.post(url)
                            .addBodyParameter(hashMap)
                            .setTag("comment")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    //   Toast.makeText(CommentActivity.this,response,Toast.LENGTH_SHORT).show();


                                    if (response.contains("Comment created")) {
                                        editText.setEnabled(true);
                                        editText.setText("");
                                        send.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        getData();
                                    } else if (response.contains("Reply created")) {
                                        editText.setEnabled(true);
                                        editText.setText("");
                                        send.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        getData();
                                    } else {

                                        send.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                        editText.setEnabled(true);

                                        Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d(TAG, "onError: anerror" + anError);

                                    send.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    editText.setEnabled(true);

                                    Toast.makeText(mContext, "couldn't connect", Toast.LENGTH_SHORT).show();

                                }
                            });
                }


            }
        });
        send.setClickable(false);
        send.setTag("0");
    }

    private void initTextWatcherEditText() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 0) {
                    send.setTag("0");
                    send.setClickable(false);
                    send.animate().scaleY(0.0f).scaleX(0.0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            send.setImageResource(R.drawable.button_send_disable);
                            send.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new DecelerateInterpolator()).setDuration(300);
                        }
                    });

                } else if (s.toString().trim().length() > 0) {

                    send.setClickable(true);

                    if (!send.getTag().toString().equals("1")) {
                        send.setTag("1");
                        send.animate().scaleY(0.0f).scaleX(0.0f).setDuration(200).setInterpolator(new AccelerateInterpolator()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                send.setImageResource(R.drawable.button_send_comments);

                                send.animate().scaleX(1.0f).scaleY(1.0f).setInterpolator(new DecelerateInterpolator()).setDuration(300);
                            }
                        });
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void passClicktoEditText(String s, String commentid) {
        this.tempcommentid = commentid;
        if (replyheader.getVisibility() == View.GONE) {
            replyheader.setVisibility(View.VISIBLE);
            editText.setHint("Write a reply...");

        } else {
            replyheader.setVisibility(View.GONE);
            editText.setHint("Write a comment...");

        }

        replyheader.setText("Reply to " + s);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.performClick();
        final InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
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
        }
    }


}
