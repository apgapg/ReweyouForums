package in.reweyou.reweyouforums.customView;


import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import in.reweyou.reweyouforums.R;

/**
 * Created by master on 5/7/17.
 */

public class CustomDialogEditShare extends DialogFragment {


    public boolean setPostByUser;
    private EditShareCallback editsharecallback;

    public void setonEditShareOptions(EditShareCallback editShareCallback) {
        this.editsharecallback = editShareCallback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_edit_share, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextViewCustomFont edit = (TextViewCustomFont) view.findViewById(R.id.edit);
        TextViewCustomFont share = (TextViewCustomFont) view.findViewById(R.id.share);

        if (!setPostByUser) {
            edit.setVisibility(View.GONE);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                editsharecallback.onEditPress();
                dismiss();


            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editsharecallback.onSharePress();

                dismiss();

            }
        });


        return view;
    }

    public interface EditShareCallback {
        void onEditPress();

        void onSharePress();
    }


}
