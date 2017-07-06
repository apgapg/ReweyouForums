package in.reweyou.reweyouforums.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.CommentActivity;
import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.NotificationActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.customView.ColorTextView;
import in.reweyou.reweyouforums.model.CommentModel;
import in.reweyou.reweyouforums.model.ReplyCommentModel;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 1/5/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_COMMENT = 1;
    private static final int VIEWTYPE_REPLY = 2;
    private static final String TAG = CommentsAdapter.class.getName();
    private final Context mContext;
    private final UserSessionManager userSessionManager;
    List<Object> messagelist;
    private String threadId;


    public CommentsAdapter(Context context) {
        this.mContext = context;
        this.messagelist = new ArrayList<>();
        userSessionManager = new UserSessionManager(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_COMMENT)
            return new CommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_main, parent, false));
        else if (viewType == VIEWTYPE_REPLY)
            return new ReplyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_reply, parent, false));
        else return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEWTYPE_COMMENT) {
            CommentModel commentModel = (CommentModel) messagelist.get(position);
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            commentViewHolder.username.setText(commentModel.getUsername());


            commentViewHolder.comment.setText(commentModel.getComment());

            try {
                JSONObject jsonObject = new JSONObject(commentModel.getTags().replace("\\", ""));
                Log.d(TAG, "bbd: " + position + "   " + jsonObject.toString());
                if (jsonObject.length() > 0) {
                    for (int i = 0; i < jsonObject.length(); i++) {

                        commentViewHolder.comment.findAndSetStrColor(jsonObject.getString("" + i), "#2962FF");
                    }

                }

            } catch (Exception e) {
            }


            commentViewHolder.time.setText(commentModel.getTimestamp().replace("about ", ""));

            if (((CommentModel) messagelist.get(position)).getUid().equals(userSessionManager.getUID()))
                commentViewHolder.edit.setVisibility(View.VISIBLE);
            else commentViewHolder.edit.setVisibility(View.INVISIBLE);
            Glide.with(mContext).load(((CommentModel) messagelist.get(position)).getImageurl()).error(R.drawable.download).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(commentViewHolder.image);
            commentViewHolder.userlevel.setText(((CommentModel) messagelist.get(position)).getBadge());
            commentViewHolder.likenumber.setText(((CommentModel) messagelist.get(position)).getUpvotes());
            if (((CommentModel) messagelist.get(position)).getStatus().equals("true")) {
                commentViewHolder.like.setImageResource(R.drawable.ic_heart_like);
            } else if (((CommentModel) messagelist.get(position)).getStatus().equals("false")) {

                commentViewHolder.like.setImageResource(R.drawable.ic_heart_like_grey);

            }
            Drawable background = commentViewHolder.userlevel.getBackground();
            if (background instanceof GradientDrawable) {
                // cast to 'ShapeDrawable'
                GradientDrawable shapeDrawable = (GradientDrawable) background;
                if (((CommentModel) messagelist.get(position)).getBadge().equals("Noob")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_noob));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Follower")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_follower));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Pro")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_pro));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Star")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_rising_star));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Expert")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_expert));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Leader")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_leader));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("King")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_king));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Legend")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_legend));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Editor")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_editor));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Writer")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_writer));
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("GOAT")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_GOAT));

                }
            }

        } else if (getItemViewType(position) == VIEWTYPE_REPLY) {
            ReplyCommentModel replyCommentModel = (ReplyCommentModel) messagelist.get(position);
            ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
            replyViewHolder.username.setText(replyCommentModel.getUsername());

            replyViewHolder.comment.setText(replyCommentModel.getReply());

            try {
                JSONObject jsonObject = new JSONObject(replyCommentModel.getTags().replace("\\", ""));
                Log.d(TAG, "bbd: " + position + "   " + jsonObject.toString());
                if (jsonObject.length() > 0) {
                    for (int i = 0; i < jsonObject.length(); i++) {

                        replyViewHolder.comment.findAndSetStrColor(jsonObject.getString("" + i), "#2962FF");
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            replyViewHolder.time.setText(replyCommentModel.getTimestamp().replace("about ", ""));
            Glide.with(mContext).load(((ReplyCommentModel) messagelist.get(position)).getImageurl()).error(R.drawable.download).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(replyViewHolder.image);
            replyViewHolder.userlevel.setText(((ReplyCommentModel) messagelist.get(position)).getBadge());
            if (((ReplyCommentModel) messagelist.get(position)).getUid().equals(userSessionManager.getUID()))
                replyViewHolder.edit.setVisibility(View.VISIBLE);
            else replyViewHolder.edit.setVisibility(View.INVISIBLE);
            Drawable background = replyViewHolder.userlevel.getBackground();
            replyViewHolder.likenumber.setText(((ReplyCommentModel) messagelist.get(position)).getUpvotes());
            if (((ReplyCommentModel) messagelist.get(position)).getStatus().equals("true")) {
                replyViewHolder.like.setImageResource(R.drawable.ic_heart_like);
            } else if (((ReplyCommentModel) messagelist.get(position)).getStatus().equals("false")) {

                replyViewHolder.like.setImageResource(R.drawable.ic_heart_like_grey);

            }
            if (background instanceof GradientDrawable) {
                // cast to 'ShapeDrawable'
                GradientDrawable shapeDrawable = (GradientDrawable) background;
                if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Noob")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_noob));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Follower")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_follower));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Pro")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_pro));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Rising Star")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_rising_star));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Expert")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_expert));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Leader")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_leader));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("King")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_king));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Legend")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_legend));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Editor")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_editor));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("Writer")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_writer));
                } else if (((ReplyCommentModel) messagelist.get(position)).getBadge().equals("GOAT")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_GOAT));

                }
            }

        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position, List<Object> payloads) {

        if (payloads.contains("like")) {
            if (getItemViewType(position) == VIEWTYPE_COMMENT) {
                CommentViewHolder holder = (CommentViewHolder) holder1;
                ((CommentModel) messagelist.get(position)).setStatus("true");
                holder.like.setImageResource(R.drawable.ic_heart_like);
                holder.liketemp.animate().rotation(-80).setDuration(650).alpha(0.0f).translationYBy(-Utils.convertpxFromDp(16)).translationXBy(-Utils.convertpxFromDp(70)).setInterpolator(new DecelerateInterpolator()).start();
                holder.likenumber.setText(String.valueOf(Integer.parseInt(((CommentModel) messagelist.get(position)).getUpvotes()) + 1));
                ((CommentModel) messagelist.get(position)).setUpvotes(String.valueOf(Integer.parseInt(((CommentModel) messagelist.get(position)).getUpvotes()) + 1));

            } else {
                ReplyViewHolder holder = (ReplyViewHolder) holder1;
                holder.like.setImageResource(R.drawable.ic_heart_like);
                holder.liketemp.animate().rotation(-80).setDuration(650).alpha(0.0f).translationYBy(-Utils.convertpxFromDp(16)).translationXBy(-Utils.convertpxFromDp(70)).setInterpolator(new DecelerateInterpolator()).start();
                holder.likenumber.setText(String.valueOf(Integer.parseInt(((ReplyCommentModel) messagelist.get(position)).getUpvotes()) + 1));
                ((ReplyCommentModel) messagelist.get(position)).setUpvotes(String.valueOf(Integer.parseInt(((ReplyCommentModel) messagelist.get(position)).getUpvotes()) + 1));

            }
        } else if (payloads.contains("unlike")) {
            if (getItemViewType(position) == VIEWTYPE_COMMENT) {
                CommentViewHolder holder = (CommentViewHolder) holder1;

                ((CommentModel) messagelist.get(position)).setStatus("false");
                holder.like.setImageResource(R.drawable.ic_heart_like_grey);
                if (Integer.parseInt(((CommentModel) messagelist.get(position)).getUpvotes()) != 0) {
                    holder.likenumber.setText(String.valueOf(Integer.parseInt(((CommentModel) messagelist.get(position)).getUpvotes()) - 1));
                    ((CommentModel) messagelist.get(position)).setUpvotes(String.valueOf(Integer.parseInt(((CommentModel) messagelist.get(position)).getUpvotes()) - 1));

                }
            } else {
                ReplyViewHolder holder = (ReplyViewHolder) holder1;

                ((ReplyCommentModel) messagelist.get(position)).setStatus("false");
                holder.like.setImageResource(R.drawable.ic_heart_like_grey);
                if (Integer.parseInt(((ReplyCommentModel) messagelist.get(position)).getUpvotes()) != 0) {
                    holder.likenumber.setText(String.valueOf(Integer.parseInt(((ReplyCommentModel) messagelist.get(position)).getUpvotes()) - 1));
                    ((ReplyCommentModel) messagelist.get(position)).setUpvotes(String.valueOf(Integer.parseInt(((ReplyCommentModel) messagelist.get(position)).getUpvotes()) - 1));

                }

            }
        } else
            super.onBindViewHolder(holder1, position, payloads);
    }


    private void settextbackground() {

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messagelist.get(position) instanceof CommentModel)
            return VIEWTYPE_COMMENT;
        else if (messagelist.get(position) instanceof ReplyCommentModel)
            return VIEWTYPE_REPLY;
        else
            return super.getItemViewType(position);
    }

    public void add(List<Object> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();

    }

    private void editcomment(final int adapterPosition) {
        //Creating a LayoutInflater object for the dialog box
        final LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_edit_comment, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText edittextdesc = (EditText) confirmDialog.findViewById(R.id.edittext);
        edittextdesc.setText(((CommentModel) messagelist.get(adapterPosition)).getComment());
        edittextdesc.setSelection(((CommentModel) messagelist.get(adapterPosition)).getComment().length());
        edittextdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    buttonconfirm.setBackground(mContext.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(mContext.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (edittextdesc.getText().toString().trim().length() > 0) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittextdesc.getWindowToken(), 0);
                    alertDialog.dismiss();
                    Toast.makeText(mContext, "updating comment!", Toast.LENGTH_SHORT).show();
                    sendeditcommenttoserver(adapterPosition, edittextdesc.getText().toString().trim());

                } else alertDialog.dismiss();
            }
        });

    }

    private void editreply(final int adapterPosition) {
        //Creating a LayoutInflater object for the dialog box
        final LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_edit_reply, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText edittextdesc = (EditText) confirmDialog.findViewById(R.id.edittext);
        edittextdesc.setText(((ReplyCommentModel) messagelist.get(adapterPosition)).getReply());
        edittextdesc.setSelection(((ReplyCommentModel) messagelist.get(adapterPosition)).getReply().length());
        edittextdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    buttonconfirm.setBackground(mContext.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(mContext.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (edittextdesc.getText().toString().trim().length() > 0) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittextdesc.getWindowToken(), 0);
                    alertDialog.dismiss();
                    Toast.makeText(mContext, "updating reply!", Toast.LENGTH_SHORT).show();
                    sendeditreplytoserver(adapterPosition, edittextdesc.getText().toString().trim());

                } else alertDialog.dismiss();
            }
        });

    }

    private void sendeditcommenttoserver(int adapterPosition, String desc) {
        AndroidNetworking.post("https://www.reweyou.in/google/edit_comment.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .addBodyParameter("comment", desc)
                .addBodyParameter("commentid", ((CommentModel) messagelist.get(adapterPosition)).getCommentid())
                .setTag("reporst")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: edit: " + response);
                        if (response.contains("Edited")) {
                            Toast.makeText(mContext, "comment updated!", Toast.LENGTH_SHORT).show();
                            ((CommentActivity) mContext).refreshlist();
                        } else
                            Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                        Toast.makeText(mContext, "couldn't connect!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void sendeditreplytoserver(int adapterPosition, String desc) {
        AndroidNetworking.post("https://www.reweyou.in/google/edit_reply.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .addBodyParameter("reply", desc)
                .addBodyParameter("replyid", ((ReplyCommentModel) messagelist.get(adapterPosition)).getReplyid())
                .setTag("reporst")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: edit: " + response);
                        if (response.contains("Edited")) {
                            Toast.makeText(mContext, "reply updated!", Toast.LENGTH_SHORT).show();
                            if (mContext instanceof ForumMainActivity) {
                                ((ForumMainActivity) mContext).refreshfeeds();
                            } else if (mContext instanceof GroupActivity) {
                                ((GroupActivity) mContext).refreshfeeds(true);
                            } else
                                ((CommentActivity) mContext).refreshlist();

                        } else
                            Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                        Toast.makeText(mContext, "couldn't connect!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void sendrequestforlikecomment(final int adapterPosition) {
        AndroidNetworking.post("https://www.reweyou.in/google/like.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("imageurl", userSessionManager.getProfilePicture())
                .addBodyParameter("username", userSessionManager.getUsername())
                .addBodyParameter("commentid", ((CommentModel) messagelist.get(adapterPosition)).getCommentid())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("reportlike")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        notifyItemChanged(adapterPosition, "unlike");
                    }
                });
    }

    private void sendrequestforlikereply(final int adapterPosition) {
        AndroidNetworking.post("https://www.reweyou.in/google/like.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("imageurl", userSessionManager.getProfilePicture())
                .addBodyParameter("username", userSessionManager.getUsername())
                .addBodyParameter("replyid", ((ReplyCommentModel) messagelist.get(adapterPosition)).getReplyid())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("reportlike")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        notifyItemChanged(adapterPosition, "unlike");
                    }
                });
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private ColorTextView comment;
        private TextView reply;
        private ImageView image, like, liketemp;
        private TextView username, userlevel, time, edit, likenumber;


        public CommentViewHolder(View inflate) {
            super(inflate);

            image = (ImageView) inflate.findViewById(R.id.image);
            edit = (TextView) inflate.findViewById(R.id.edit);
            username = (TextView) inflate.findViewById(R.id.message);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);
            likenumber = (TextView) inflate.findViewById(R.id.likenumber);
            like = (ImageView) inflate.findViewById(R.id.like);
            liketemp = (ImageView) inflate.findViewById(R.id.templike);
            comment = (ColorTextView) inflate.findViewById(R.id.comment);
            time = (TextView) inflate.findViewById(R.id.time);
            reply = (TextView) inflate.findViewById(R.id.reply);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editcomment(getAdapterPosition());
                }
            });

            if (mContext instanceof ForumMainActivity || mContext instanceof GroupActivity) {

                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext, CommentActivity.class);
                        i.putExtra("threadid", threadId);
                        if (mContext instanceof ForumMainActivity)
                            i.putExtra("from", "f");
                        else if (mContext instanceof GroupActivity)
                            i.putExtra("from", "g");
                        else if (mContext instanceof NotificationActivity)
                            i.putExtra("from", "nb");

                        if (mContext instanceof ForumMainActivity) {
                            ((ForumMainActivity) mContext).startActivityForResult(i, Utils.MAINACTIVITY_COMMENT);
                        } else mContext.startActivity(i);
                    }
                });
            } else
                reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CommentActivity) mContext).passClicktoEditText(((CommentModel) messagelist.get(getAdapterPosition())).getUsername(), (((CommentModel) messagelist.get((getAdapterPosition()))).getCommentid()), getAdapterPosition());

                    }
                });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CommentModel) messagelist.get(getAdapterPosition())).getStatus().equals("false"))
                        notifyItemChanged(getAdapterPosition(), "like");
                    else notifyItemChanged(getAdapterPosition(), "unlike");
                    sendrequestforlikecomment(getAdapterPosition());


                }
            });

        }
    }

    private class ReplyViewHolder extends RecyclerView.ViewHolder {
        private TextView likenumber;
        private ImageView liketemp;
        private ImageView like;
        private TextView edit;
        private TextView userlevel;
        private ImageView image;
        private TextView username, reply, time;
        private ColorTextView comment;


        public ReplyViewHolder(View inflate) {
            super(inflate);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);
            likenumber = (TextView) inflate.findViewById(R.id.likenumber);

            image = (ImageView) inflate.findViewById(R.id.image);
            edit = (TextView) inflate.findViewById(R.id.edit);
            like = (ImageView) inflate.findViewById(R.id.like);
            liketemp = (ImageView) inflate.findViewById(R.id.templike);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editreply(getAdapterPosition());
                }
            });
            username = (TextView) inflate.findViewById(R.id.message);
            comment = (ColorTextView) inflate.findViewById(R.id.comment);
            time = (TextView) inflate.findViewById(R.id.time);

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ReplyCommentModel) messagelist.get(getAdapterPosition())).getStatus().equals("false"))
                        notifyItemChanged(getAdapterPosition(), "like");
                    else notifyItemChanged(getAdapterPosition(), "unlike");
                    sendrequestforlikereply(getAdapterPosition());

                }
            });
        }
    }


}
