package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.CommentActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.CommentModel;
import in.reweyou.reweyouforums.model.ReplyCommentModel;

/**
 * Created by master on 1/5/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEWTYPE_COMMENT = 1;
    private static final int VIEWTYPE_REPLY = 2;
    private final Context mContext;
    List<Object> messagelist;

    public CommentsAdapter(Context context) {
        this.mContext = context;
        this.messagelist = new ArrayList<>();

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
            commentViewHolder.time.setText(commentModel.getTimestamp().replace("about ", ""));
            Glide.with(mContext).load(((CommentModel) messagelist.get(position)).getImageurl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(commentViewHolder.image);
            commentViewHolder.userlevel.setText(((CommentModel) messagelist.get(position)).getBadge());

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
                } else if (((CommentModel) messagelist.get(position)).getBadge().equals("Rising Star")) {
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
            replyViewHolder.time.setText(replyCommentModel.getTimestamp().replace("about ", ""));
            Glide.with(mContext).load(((ReplyCommentModel) messagelist.get(position)).getImageurl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(replyViewHolder.image);
            replyViewHolder.userlevel.setText(((ReplyCommentModel) messagelist.get(position)).getBadge());

            Drawable background = replyViewHolder.userlevel.getBackground();

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

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView reply;
        private ImageView image;
        private TextView username, userlevel, comment, time;


        public CommentViewHolder(View inflate) {
            super(inflate);

            image = (ImageView) inflate.findViewById(R.id.image);
            username = (TextView) inflate.findViewById(R.id.username);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);
            comment = (TextView) inflate.findViewById(R.id.comment);
            time = (TextView) inflate.findViewById(R.id.time);
            reply = (TextView) inflate.findViewById(R.id.reply);

            reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CommentActivity) mContext).passClicktoEditText(((CommentModel) messagelist.get(getAdapterPosition())).getUsername(), (((CommentModel) messagelist.get((getAdapterPosition()))).getCommentid()));
                }
            });


        }
    }

    private class ReplyViewHolder extends RecyclerView.ViewHolder {
        private TextView userlevel;
        private ImageView image;
        private TextView username, comment, reply, time;


        public ReplyViewHolder(View inflate) {
            super(inflate);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);

            image = (ImageView) inflate.findViewById(R.id.image);
            username = (TextView) inflate.findViewById(R.id.username);
            comment = (TextView) inflate.findViewById(R.id.comment);
            time = (TextView) inflate.findViewById(R.id.time);


        }
    }
}
