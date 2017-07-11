package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by master on 25/6/17.
 */

public class OnlyDraggableTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = OnlyDraggableTextView.class.getName();
    private float dX, dY;

    public OnlyDraggableTextView(Context context) {
        super(context);
    }

    public OnlyDraggableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OnlyDraggableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {


            case MotionEvent.ACTION_DOWN:


                dX = getX() - event.getRawX();
                dY = getY() - event.getRawY();

                return true;

            case MotionEvent.ACTION_MOVE:

                setX(event.getRawX() + dX);
                setY(event.getRawY() + dY);
                return true;

            default:
                Log.d(TAG, "onTouchEvent: wfewefwefwefw");
                return false;
        }

    }


}
