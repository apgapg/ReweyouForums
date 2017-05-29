package in.reweyou.reweyouforums.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.CommentActivity;
import in.reweyou.reweyouforums.NotiActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.NotiModel;

/**
 * Created by master on 1/5/17.
 */

public class NotiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = NotiAdapter.class.getName();
    private final Context context;
    private final UserSessionManager userSessionManager;
    List<NotiModel> messagelist;

    public NotiAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();
        userSessionManager = new UserSessionManager(context);

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.contains("change")) {
            NotiViewHolder notiViewHolder = (NotiViewHolder) holder;
            notiViewHolder.container.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            messagelist.get(position).setReadstatus("1");

        } else
            super.onBindViewHolder(holder, position, payloads);
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

    private void sendrequestforseenchange(int adapterPosition) {
        AndroidNetworking.post("https://www.reweyou.in/google/notification_read.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("nid", messagelist.get(adapterPosition).getNid())
                .addBodyParameter("type", "")
                .setTag("groupcreate")
                .setPriority(Priority.HIGH)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: noti: " + response);

                ((NotiActivity) context).setResult(Activity.RESULT_OK);
            }

            @Override
            public void onError(ANError anError) {
                Log.d(TAG, "onError: " + anError);

            }
        });
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

                    Intent i = new Intent(context, CommentActivity.class);
                    i.putExtra("from", "nb");
                    i.putExtra("threadid", messagelist.get(getAdapterPosition()).getId());
                    context.startActivity(i);
                    notifyItemChanged(getAdapterPosition(), "change");
                    sendrequestforseenchange(getAdapterPosition());
                }
            });
        }
    }
}
