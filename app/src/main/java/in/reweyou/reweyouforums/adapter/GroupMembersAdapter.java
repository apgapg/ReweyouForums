package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.GroupMemberModel;

/**
 * Created by master on 1/5/17.
 */

public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<GroupMemberModel> messagelist;

    public GroupMembersAdapter(Context context, int members) {
        this.context = context;
        this.messagelist = new ArrayList<>();
        for (int i = 0; i < members; i++) {
            messagelist.add(new GroupMemberModel());
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_info_members, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        Glide.with(context).load(messagelist.get(position).getImageurl()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<GroupMemberModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private ImageView backgroundImage;


        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.img);

        }
    }
}
