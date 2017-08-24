package in.reweyou.reweyouforums.civil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import in.reweyou.reweyouforums.utils.Utils;

public class LevelIndicator extends View {
    Paint f0p = new Paint();


    public LevelIndicator(Context context) {
        super(context);
        init();
    }

    public LevelIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.f0p.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.f0p.setAntiAlias(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(300000, 100) {

                    public void onTick(long millisUntilFinished) {
                        //here you can have your logic to set text to edittext
                        invalidate();
                    }

                    public void onFinish() {
                    }

                }.start();
            }
        }, 1000);

    }


    protected void onDraw(Canvas c) {

        c.drawCircle((DigitalLevel.f4x * Utils.convertpxFromDp(50)) + ((float) (Utils.screenWidth / 2)), (((float) Utils.screenHeight) / 2.0f) + (DigitalLevel.f5y * Utils.convertpxFromDp(50)), (float) (Utils.screenWidth / 32), this.f0p);
    }


}
