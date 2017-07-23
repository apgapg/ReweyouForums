package in.reweyou.reweyouforums.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.model.GroupModel;

/**
 * Created by master on 1/5/17.
 */

public class ChooseGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ChooseGroupAdapter.class.getName();
    private final Context context;
    List<GroupModel> messagelist;
    private ChooseGroup chooseGroup;

    public ChooseGroupAdapter(Context context, List<GroupModel> list) {
        this.context = context;
        this.messagelist = list;
    }

    public void addonGroupSelectListener(ChooseGroup chooseGroup) {
        this.chooseGroup = chooseGroup;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BackgroundImagesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_choose_group, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BackgroundImagesHolder forumViewHolder = (BackgroundImagesHolder) holder;
        forumViewHolder.textView.setText(messagelist.get(position).getGroupname());

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public interface ChooseGroup {
        void onGroupChoosen(GroupModel groupModel);
    }

    private class BackgroundImagesHolder extends RecyclerView.ViewHolder {
        private TextView textView;


        public BackgroundImagesHolder(View inflate) {
            super(inflate);

            textView = (TextView) inflate.findViewById(R.id.textview);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chooseGroup.onGroupChoosen(messagelist.get(getAdapterPosition()));
                    // ((AddBackground) context).onbackgrounditemclick(messagelist.get(getAdapterPosition()));
                }
            });
        }
    }

}
