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

import in.reweyou.reweyouforums.AddBackground;
import in.reweyou.reweyouforums.R;

/**
 * Created by master on 1/5/17.
 */

public class BackgroundImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BackgroundImagesAdapter.class.getName();
    private final Context context;
    List<String> messagelist;

    public BackgroundImagesAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BackgroundImagesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_background_text, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BackgroundImagesHolder forumViewHolder = (BackgroundImagesHolder) holder;
        Glide.with(context).load(messagelist.get(position)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);


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


    private class BackgroundImagesHolder extends RecyclerView.ViewHolder {
        private ImageView backgroundImage;


        public BackgroundImagesHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.image);
            backgroundImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AddBackground) context).onbackgrounditemclick(messagelist.get(getAdapterPosition()));
                }
            });
        }
    }

}
