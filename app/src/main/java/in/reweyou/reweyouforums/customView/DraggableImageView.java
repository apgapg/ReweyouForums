package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by master on 25/6/17.
 */

public class DraggableImageView extends AppCompatImageView {
    private static final String TAG = DraggableImageView.class.getName();
    private float dX, dY;

    public DraggableImageView(Context context) {
        super(context);
    }

    public DraggableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                setX(event.getRawX() + dX);
                setY(event.getRawY() + dY);
                break;
            default:
                return false;
        }
        return true;
    }


}
