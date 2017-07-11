package in.reweyou.reweyouforums.customView;


import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import in.reweyou.reweyouforums.R;

/**
 * Created by master on 5/7/17.
 */

public class CustomDialogEditShare extends DialogFragment {


    public boolean setPostByUser;
    private EditShareCallback editsharecallback;
    private String externalLink = "";
    private String youtubeLink = "";

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
        TextViewCustomFont copylink = (TextViewCustomFont) view.findViewById(R.id.copylink);


        Log.d("eee", "onCreateView: " + externalLink + "     " + youtubeLink);
        if (externalLink.isEmpty() && youtubeLink.isEmpty()) {
            copylink.setVisibility(View.GONE);
        } else if (!externalLink.isEmpty()) {
            copylink.setText("Copy link");
        } else {
            copylink.setText("Copy video link");
        }

        copylink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData;
                if (!externalLink.isEmpty())
                    clipData = ClipData.newPlainText(null, externalLink);
                else
                    clipData = ClipData.newPlainText(null, youtubeLink);

                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getActivity(), "link copied", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

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

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
        Log.d("eee", "setExternalLink: " + externalLink);
    }

    public void setYoutubeLink(String youtubeLink) {

        String temp = "https://www.youtube.com/watch?v=";

        this.youtubeLink = temp.concat(youtubeLink);
    }

    public interface EditShareCallback {
        void onEditPress();

        void onSharePress();

    }


}
