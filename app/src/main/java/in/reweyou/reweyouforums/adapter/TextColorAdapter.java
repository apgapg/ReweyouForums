package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.AddBackground;
import in.reweyou.reweyouforums.R;

/**
 * Created by master on 1/5/17.
 */

public class TextColorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = TextColorAdapter.class.getName();
    private final Context context;
    List<String> messagelist;
    private int[] colorarray = {R.color.ct1, R.color.ct2, R.color.ct22, R.color.ct3, R.color.ct4, R.color.ct5, R.color.ct6, R.color.c1, R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6, R.color.c7, R.color.c8, R.color.c9, R.color.c10};

    public TextColorAdapter(Context context) {
        this.context = context;
        this.messagelist = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BackgroundImagesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_bar, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BackgroundImagesHolder forumViewHolder = (BackgroundImagesHolder) holder;
        forumViewHolder.backgroundImage.setImageResource(colorarray[position]);

    }

    @Override
    public int getItemCount() {
        return colorarray.length;
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
                    ((AddBackground) context).ontextcolor(colorarray[getAdapterPosition()]);
                }
            });
        }
    }

}
