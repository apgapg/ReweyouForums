package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

/**
 * Created by master on 25/6/17.
 */

public class DraggableEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = DraggableEditText.class.getName();
    private float dX, dY;
    private long startClickTime;
    private long MAX_CLICK_DURATION = 250;

    public DraggableEditText(Context context) {
        super(context);
    }

    public DraggableEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                Log.d(TAG, "onTouchEvent: " + clickDuration);
                if (clickDuration < MAX_CLICK_DURATION) {

                    Log.d(TAG, "onTouchEvent: fefw");
                    setFocusableInTouchMode(true);
                    requestFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
                    return true;
                } else {
                    return false;
                }
            case MotionEvent.ACTION_DOWN:
                startClickTime = Calendar.getInstance().getTimeInMillis();


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
