package in.reweyou.reweyouforums.customView;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import in.reweyou.reweyouforums.R;

/**
 * Created by master on 5/7/17.
 */

public class Custom_upload_dialog extends DialogFragment {

    int[] imageArraysmartphone = {R.drawable.ic_smartphone, R.drawable.ic_smartphone_light};
    int[] imageArrayserver = {R.drawable.ic_server_light, R.drawable.ic_server};
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_custom_uploading, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextViewCustomFontBold buttonconfirm = (TextViewCustomFontBold) view.findViewById(R.id.buttonConfirm);
        final ImageView smartphone = (ImageView) view.findViewById(R.id.phone);
        final ImageView server = (ImageView) view.findViewById(R.id.server);

        handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0, j = 0;

            public void run() {
                try {
                    smartphone.setImageResource(imageArraysmartphone[i]);
                    i++;
                    if (i > imageArraysmartphone.length - 1) {
                        i = 0;
                    }

                    server.setImageResource(imageArrayserver[j]);
                    j++;
                    if (j > imageArrayserver.length - 1) {
                        j = 0;
                    }

                    handler.postDelayed(this, 500);
                } catch (Exception e) {

                }
            }
        };
        handler.postDelayed(runnable, 500);
        return view;
    }

}
