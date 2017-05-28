package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupBadgeModel;

/**
 * Created by master on 1/5/17.
 */

public class GroupBadegsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GroupBadegsAdapter.class.getName();
    private final Context mContext;
    private final UserSessionManager userSessionManager;
    List<GroupBadgeModel> messagelist;

    public GroupBadegsAdapter(Context context) {
        this.mContext = context;

        this.messagelist = new ArrayList<>();
        userSessionManager = new UserSessionManager(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_badges, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        forumViewHolder.groupname.setText("#" + messagelist.get(position).getGroupname());
        forumViewHolder.userlevel.setText(messagelist.get(position).getBadge());
        forumViewHolder.grouppoints.setText("- " + messagelist.get(position).getPoints() + " pts");
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

    public void add(List<GroupBadgeModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }


    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {

        private TextView groupname, grouppoints;
        private TextView userlevel;

        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            groupname = (TextView) inflate.findViewById(R.id.groupname);
            grouppoints = (TextView) inflate.findViewById(R.id.grouppoints);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);

        }
    }

}
