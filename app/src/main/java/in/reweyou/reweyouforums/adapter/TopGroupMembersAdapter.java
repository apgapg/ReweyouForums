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

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.TopGroupMemberModel;

/**
 * Created by master on 1/5/17.
 */

public class TopGroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = TopGroupMembersAdapter.class.getName();
    private final Context mContext;
    List<TopGroupMemberModel> messagelist;

    public TopGroupMembersAdapter(Context context) {
        this.mContext = context;

        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_group_info_members_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        ((YourGroupsViewHolder) holder).username.setText(messagelist.get(position).getUsername());
        Glide.with(mContext).load(messagelist.get(position).getImageurl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.image);

        forumViewHolder.userlevel.setText(messagelist.get(position).getBadge());

        Drawable background = forumViewHolder.userlevel.getBackground();
        if (background instanceof GradientDrawable) {
            // cast to 'ShapeDrawable'
            GradientDrawable shapeDrawable = (GradientDrawable) background;
            if (messagelist.get(position).getBadge().equals("Noob")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_noob));
            } else if (messagelist.get(position).getBadge().equals("Follower")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_follower));
            } else if (messagelist.get(position).getBadge().equals("Pro")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_pro));
            } else if (messagelist.get(position).getBadge().equals("Rising Star")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_rising_star));
            } else if (messagelist.get(position).getBadge().equals("Expert")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_expert));
            } else if (messagelist.get(position).getBadge().equals("Leader")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_leader));
            } else if (messagelist.get(position).getBadge().equals("King")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_king));
            } else if (messagelist.get(position).getBadge().equals("Legend")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_legend));
            } else if (messagelist.get(position).getBadge().equals("Editor")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_editor));
            } else if (messagelist.get(position).getBadge().equals("Writer")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_writer));
            } else if (messagelist.get(position).getBadge().equals("GOAT")) {
                shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_GOAT));

            }
        }
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<TopGroupMemberModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }


    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView username, userlevel;


        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            image = (ImageView) inflate.findViewById(R.id.image);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);

            username = (TextView) inflate.findViewById(R.id.username);


        }
    }

}
