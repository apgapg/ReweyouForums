package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by master on 1/7/17.
 */

public class TextViewCustomFontBold extends android.support.v7.widget.AppCompatTextView {
    public TextViewCustomFontBold(Context context) {
        super(context);
    }

    public TextViewCustomFontBold(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewCustomFontBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(Typeface tf) {
        Typeface type = Typeface.createFromAsset(getContext().getAssets(), "Quicksand-Bold.ttf");
        super.setTypeface(type);
    }
}
