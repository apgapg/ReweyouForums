package in.reweyou.reweyouforums.customView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;

import java.util.Calendar;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.utils.Utils;

/**
 * Created by master on 25/6/17.
 */

public class DraggableTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String TAG = DraggableTextView.class.getName();
    private float dX, dY;
    private long startClickTime;
    private long MAX_CLICK_DURATION = 250;
    private SingleClickcallback onSingleClickListener;
    private ActionDowncallback onActionDownListener;

    public DraggableTextView(Context context) {
        super(context);
        init();
    }

    public void setOnSingleClickListener(SingleClickcallback onSingleClickListener) {
        this.onSingleClickListener = onSingleClickListener;
    }

    public void setonActionDownListener(ActionDowncallback onActionDownListener) {
        this.onActionDownListener = onActionDownListener;
    }

    private void init() {
        setTextSize(20);
        setTextColor(getResources().getColor(R.color.white));
        setTypeface(null, Typeface.BOLD);
        setMinimumHeight(Utils.convertpxFromDp(32));
        setMinimumWidth(Utils.convertpxFromDp(56));
        setGravity(Gravity.CENTER);
        int paddingtopbottom = Utils.convertpxFromDp(4);
        int paddingleftright = Utils.convertpxFromDp(8);
        setPadding(paddingleftright, paddingtopbottom, paddingleftright, paddingtopbottom);
        setBackground(getResources().getDrawable(R.drawable.solid_pink));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION) {
                    onSingleClickListener.onSingleClick(this);
                    return true;
                }
            case MotionEvent.ACTION_DOWN:

                onActionDownListener.onActionDown(this);
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

    public interface SingleClickcallback {
        void onSingleClick(DraggableTextView draggableTextView);
    }


    public interface ActionDowncallback {
        void onActionDown(DraggableTextView draggableTextView);
    }


}
