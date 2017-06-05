package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 13/11/16.
 */

public class CustomImageViewRectangular extends AppCompatImageView {

    private static final String TAG = CustomImageViewRectangular.class.getName();

    public CustomImageViewRectangular(Context context) {
        super(context);
    }

    public CustomImageViewRectangular(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageViewRectangular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width - Utils.convertpxFromDp(20));

    }
}
