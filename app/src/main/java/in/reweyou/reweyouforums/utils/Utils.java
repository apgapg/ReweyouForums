package in.reweyou.reweyouforums.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import java.util.Random;

/**
 * Created by master on 2/5/17.
 */

public class Utils {
    public static final int REQ_CODE_GROP_ACITIVTY = 25;
    public static final int REQ_CODE_EDIT_GROUP_ACTIVITY = 23;
    public static final int REQ_CODE_EDIT_PROFILE = 28;
    public static final int REQ_CODE_CREATE_FROM_FORUMACTVITY = 29;
    public static final int REQ_CODE_NOTI = 30;
    public static final String YOUTUBE_API_KEY = "AIzaSyBBnhu7Kx-4QAEmoSddzU9zFYutex-p5l4";
    public static final int RECOVERY_DIALOG_YOUTUBE = 111;

    public static boolean isNight;
    public static int backgroundCode;
    public static int screenWidth;
    public static String youtubeUrl;
    private static int scalefactor;
    private static int screenHeight;

    public static void setBackgroundColor(Context applicationContext) {
        Random rand = new Random();
        backgroundCode = rand.nextInt(4);

        WindowManager wm = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    public static void setpxFromDp(final Context context) {
        int scalefacto = (int) (context.getResources().getDisplayMetrics().density);
        scalefactor = scalefacto;
    }

    public static int convertpxFromDp(int dp) {
        return scalefactor * dp;
    }

}
