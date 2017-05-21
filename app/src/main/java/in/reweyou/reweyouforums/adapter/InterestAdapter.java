package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.GroupModel;

/**
 * Created by master on 1/5/17.
 */

public class InterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<GroupModel> messagelist;

    public InterestAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        forumViewHolder.groupName.setText(messagelist.get(position).getGroupname());

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<GroupModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout container;
        private TextView groupName;

        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            groupName = (TextView) inflate.findViewById(R.id.groupname);

            container = (RelativeLayout) inflate.findViewById(R.id.container);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
