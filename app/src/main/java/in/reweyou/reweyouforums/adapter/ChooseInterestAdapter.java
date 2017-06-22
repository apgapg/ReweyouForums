package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.fragment.ChooseInterestFragment;
import in.reweyou.reweyouforums.model.GroupModel;

/**
 * Created by master on 1/5/17.
 */

public class ChooseInterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final FirebaseAnalytics mFirebaseAnalytics;
    private final ChooseInterestFragment fragmentcontext;
    List<GroupModel> messagelist;

    public ChooseInterestAdapter(Context context, ChooseInterestFragment chooseInterestFragment) {
        this.context = context;
        this.messagelist = new ArrayList<>();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.fragmentcontext = chooseInterestFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_interest, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        Glide.with(fragmentcontext).load(messagelist.get(position).getImage()).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);
        forumViewHolder.groupName.setText(messagelist.get(position).getGroupname());
        forumViewHolder.groupName.setSelected(true);
        if (messagelist.get(position).getTempselect().equals("select")) {
            forumViewHolder.rl.setVisibility(View.VISIBLE);
        } else if (messagelist.get(position).getTempselect().equals("deselect")) {
            forumViewHolder.rl.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.contains("select")) {
                YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
                if (forumViewHolder.rl.getVisibility() == View.VISIBLE) {
                    forumViewHolder.rl.setVisibility(View.INVISIBLE);
                    messagelist.get(position).setTempselect("deselect");
                    fragmentcontext.removefromselectlist(messagelist.get(position).getGroupId());

                } else {
                    forumViewHolder.rl.setVisibility(View.VISIBLE);
                    messagelist.get(position).setTempselect("select");
                    fragmentcontext.updateselectlist(messagelist.get(position).getGroupId());

                }
            }
        } else
            super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<GroupModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        Log.d("dhwh", "add: list" + list.size());
        notifyDataSetChanged();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                fragmentcontext.scrolltotop();
            }
        });
    }

    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout container;
        private ImageView backgroundImage;
        private TextView groupName;
        private RelativeLayout rl;


        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.img);
            groupName = (TextView) inflate.findViewById(R.id.groupname);

            container = (LinearLayout) inflate.findViewById(R.id.container);
            rl = (RelativeLayout) inflate.findViewById(R.id.rl);
            groupName.setSelected(true);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyItemChanged(getAdapterPosition(), "select");
                }
            });


        }
    }


}
