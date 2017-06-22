package in.reweyou.reweyouforums.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.fragment.ExploreFragment;
import in.reweyou.reweyouforums.model.GroupModel;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 1/5/17.
 */

public class ForumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final FirebaseAnalytics mFirebaseAnalytics;
    private final ExploreFragment fragmentcontext;
    List<GroupModel> messagelist;

    public ForumAdapter(Context context, ExploreFragment exploreFragment) {
        this.context = context;
        this.messagelist = new ArrayList<>();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.fragmentcontext = exploreFragment;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ForumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explore_your_groups, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ForumViewHolder forumViewHolder = (ForumViewHolder) holder;
        Glide.with(fragmentcontext).load(messagelist.get(position).getImage()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);
        forumViewHolder.groupName.setText(messagelist.get(position).getGroupname());
        forumViewHolder.members.setText(messagelist.get(position).getMembers());
        forumViewHolder.threads.setText(messagelist.get(position).getThreads());
        forumViewHolder.description.setText(messagelist.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<GroupModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                fragmentcontext.scrolltotop();
            }
        });
    }

    private class ForumViewHolder extends RecyclerView.ViewHolder {
        private ImageView backgroundImage;
        private TextView groupName;
        private TextView members;
        private TextView threads, description;
        private LinearLayout container;

        public ForumViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.img);
            groupName = (TextView) inflate.findViewById(R.id.groupname);
            members = (TextView) inflate.findViewById(R.id.members);
            threads = (TextView) inflate.findViewById(R.id.threads);
            description = (TextView) inflate.findViewById(R.id.description);
            container = (LinearLayout) inflate.findViewById(R.id.container);
            groupName.setSelected(true);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, GroupActivity.class);
                    i.putExtra("groupname", messagelist.get(getAdapterPosition()).getGroupname());
                    i.putExtra("groupid", messagelist.get(getAdapterPosition()).getGroupId());
                    i.putExtra("description", messagelist.get(getAdapterPosition()).getDescription());
                    i.putExtra("rules", messagelist.get(getAdapterPosition()).getRules());
                    i.putExtra("admin", messagelist.get(getAdapterPosition()).getAdmin());
                    i.putExtra("adminname", messagelist.get(getAdapterPosition()).getAdminname());
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage());
                    i.putExtra("members", messagelist.get(getAdapterPosition()).getMembers());
                    i.putExtra("threads", messagelist.get(getAdapterPosition()).getThreads());

                    i.putExtra("follow", false);
                    ((Activity) context).startActivityForResult(i, Utils.REQ_CODE_GROP_ACITIVTY);
                    //  i.putExtra("threads",messagelist.get(getAdapterPosition()).ge);

                    Bundle params = new Bundle();

                    mFirebaseAnalytics.logEvent(messagelist.get(getAdapterPosition()).getGroupname(), params);
                }
            });
        }
    }
}
