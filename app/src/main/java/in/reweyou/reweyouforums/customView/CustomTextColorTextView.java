package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 22/5/17.
 */

public class CustomTextColorTextView extends android.support.v7.widget.AppCompatTextView {
    public CustomTextColorTextView(Context context) {
        super(context);
        setmytextcolor();
    }


    public CustomTextColorTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextColorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void setmytextcolor() {
        switch (Utils.backgroundCode) {
            case 0:
                return;
            case 1:
                return;
            case 2:
                return;
            case 3:
                return;
        }
    }
}
