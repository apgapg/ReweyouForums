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

import in.reweyou.reweyouforums.CreatePostActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;

/**
 * Created by master on 1/5/17.
 */

public class GalleryImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GalleryImagesAdapter.class.getName();
    private final Context context;
    private final UserSessionManager userSessionManager;
    List<String> messagelist;

    public GalleryImagesAdapter(Context context) {
        this.context = context;

        this.messagelist = new ArrayList<>();
        userSessionManager = new UserSessionManager(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        Glide.with(context).load(messagelist.get(position)).dontAnimate().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);


    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<String> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }


    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private ImageView backgroundImage;


        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.image);
            backgroundImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CreatePostActivity) context).onimageselected(messagelist.get(getAdapterPosition()));
                }
            });
        }
    }

}
