package in.reweyou.reweyouforums.customView;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.util.List;

import in.reweyou.reweyouforums.CreatePostActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.adapter.ChooseGroupAdapter;
import in.reweyou.reweyouforums.model.GroupModel;

/**
 * Created by master on 5/7/17.
 */

public class CustomGroupChooseDialog extends DialogFragment {

    private List<GroupModel> list;

    public CustomGroupChooseDialog() {

    }

    @SuppressLint("ValidFragment")
    public CustomGroupChooseDialog(List<GroupModel> list) {
        this.list = list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_choose_group, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ChooseGroupAdapter chooseGroupAdapter = new ChooseGroupAdapter(getActivity(), list);
        chooseGroupAdapter.addonGroupSelectListener(new ChooseGroupAdapter.ChooseGroup() {
            @Override
            public void onGroupChoosen(GroupModel groupModel) {
                dismiss();
                ((CreatePostActivity) getActivity()).onGroupSelectByUser(groupModel);
            }
        });
        recyclerView.setAdapter(chooseGroupAdapter);
        return view;
    }


}
