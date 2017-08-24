package in.reweyou.reweyouforums.civil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import in.reweyou.reweyouforums.utils.Utils;

public class LevelCustombackground extends View {
    Paint f1p = new Paint();
    Paint p1 = new Paint();
    Paint p2 = new Paint();
    Paint p3 = new Paint();
    private int width;
    private int height;

    public LevelCustombackground(Context context) {
        super(context);
        init();
    }

    public LevelCustombackground(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LevelCustombackground(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        width = Utils.screenWidth;
        height = Utils.screenHeight;
    }

    protected void onDraw(Canvas c) {
        this.f1p.setColor(Color.parseColor("#000000"));
        this.f1p.setAntiAlias(true);
        this.p1.setColor(Color.parseColor("#000000"));
        this.p1.setAntiAlias(true);
        this.p1.setStyle(Style.STROKE);
        this.p1.setStrokeWidth(3.0f);
        this.p2.setColor(Color.parseColor("#000000"));
        this.p2.setAntiAlias(true);
        this.p2.setStyle(Style.STROKE);
        this.p2.setStrokeWidth(1.5f);
        this.p3.setColor(-1);
        this.p3.setAntiAlias(true);
        this.p3.setStyle(Style.STROKE);
        this.p3.setStrokeWidth(2.0f);
        c.drawCircle((float) (width / 2), (float) (height / 2), (float) (width / 10), this.p1);
        c.drawCircle((float) (width / 2), (float) (height / 2), (float) (width / 20), this.p1);
        c.drawCircle((float) (width / 2), (float) (height / 2), (float) (width / 30), this.p1);
        c.drawCircle((float) (width / 2), (float) (height / 2), (float) ((width / 2) - (width / 10)), this.p1);
        c.drawLine(0.0f, (float) (height / 2), (float) width, (float) (height / 2), this.p2);
        c.drawLine((float) (width / 2), 0.0f, (float) (width / 2), (float) height, this.p2);
    }
}
