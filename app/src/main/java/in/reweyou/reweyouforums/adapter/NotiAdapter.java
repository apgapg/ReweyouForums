package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.NotiModel;

/**
 * Created by master on 1/5/17.
 */

public class NotiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    List<NotiModel> messagelist;

    public NotiAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NotiViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noti, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NotiViewHolder notiViewHolder = (NotiViewHolder) holder;
        // forumViewHolder.message.setText(messagelist.get(position).getGroupname());
        switch (messagelist.get(position).getNoti_type()) {
            case "Thread Like":
                notiViewHolder.message.setText(messagelist.get(position).getNotifier() + " liked your post.");
                break;
            case "Comment Like":
                notiViewHolder.message.setText(messagelist.get(position).getNotifier() + " liked your comment.");

                break;
            case "Reply Like":
                notiViewHolder.message.setText(messagelist.get(position).getNotifier() + " liked your reply.");

                break;
            case "Reply":
                notiViewHolder.message.setText(messagelist.get(position).getNotifier() + " replied to your comment.");

                break;
            case "Comment":
                notiViewHolder.message.setText(messagelist.get(position).getNotifier() + " commented on your post.");

                break;
        }
        notiViewHolder.time.setText(messagelist.get(position).getTimestamp().replace("about ", ""));
        Glide.with(context).load(messagelist.get(position).getNotifier_image()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(notiViewHolder.image);
        if (messagelist.get(position).getReadstatus().equals("0"))
            notiViewHolder.container.setBackgroundColor(context.getResources().getColor(R.color.red_alpha));
        else
            notiViewHolder.container.setBackgroundColor(context.getResources().getColor(R.color.transparent));

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<NotiModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private class NotiViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout container;
        private TextView message, time;
        private ImageView image;

        public NotiViewHolder(View inflate) {
            super(inflate);

            message = (TextView) inflate.findViewById(R.id.message);
            time = (TextView) inflate.findViewById(R.id.time);
            image = (ImageView) inflate.findViewById(R.id.image);
            message.setSelected(true);
            container = (RelativeLayout) inflate.findViewById(R.id.container);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
