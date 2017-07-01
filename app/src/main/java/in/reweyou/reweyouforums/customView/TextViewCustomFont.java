package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by master on 1/7/17.
 */

public class TextViewCustomFont extends android.support.v7.widget.AppCompatTextView {
    public TextViewCustomFont(Context context) {
        super(context);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewCustomFont(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf) {
        Typeface type = Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Medium.ttf");
        super.setTypeface(type);
    }
}
