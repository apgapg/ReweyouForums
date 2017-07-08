package in.reweyou.reweyouforums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import in.reweyou.reweyouforums.utils.Utils;

public class AddBackground extends AppCompatActivity {

    private String inputText;
    private TextView textview;
    private ImageView imageview;
    private String TAG = AddBackground.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_background);

        inputText = getIntent().getStringExtra("text");
        textview = (TextView) findViewById(R.id.textview);
        imageview = (ImageView) findViewById(R.id.image);

        textview.setText(inputText);
        textview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout: " + textview.getHeight());
                textview.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                imageview.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, textview.getHeight()));
                Glide.with(AddBackground.this).load(R.drawable.add_background_2).into(imageview);
            }
        });

        findViewById(R.id.b1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(AddBackground.this).load(R.drawable.b4).into(imageview);
            }
        });
        findViewById(R.id.b4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(AddBackground.this).load(R.drawable.add_background_2).into(imageview);
            }
        });
        findViewById(R.id.b7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(AddBackground.this).load(R.drawable.b7).into(imageview);
            }
        });
        findViewById(R.id.b2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(AddBackground.this).load(R.drawable.add_background_3).into(imageview);
            }
        });
        findViewById(R.id.b5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(AddBackground.this).load(R.drawable.b5).into(imageview);
            }
        });

        Glide.with(AddBackground.this).load(R.drawable.b4).into((ImageView) findViewById(R.id.b1));
        Glide.with(AddBackground.this).load(R.drawable.add_background_2).into((ImageView) findViewById(R.id.b4));
        Glide.with(AddBackground.this).load(R.drawable.b7).into((ImageView) findViewById(R.id.b7));
        Glide.with(AddBackground.this).load(R.drawable.add_background_3).into((ImageView) findViewById(R.id.b2));
        Glide.with(AddBackground.this).load(R.drawable.b5).into((ImageView) findViewById(R.id.b5));

        findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + (int) (Utils.convertdpFromPx(textview.getTextSize())));
                textview.setScaleX((float) (textview.getScaleX() + 0.1));
                textview.setScaleY((float) (textview.getScaleY() + 0.1));

            }
        });

        findViewById(R.id.subtract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textview.setScaleX((float) (textview.getScaleX() - 0.1));
                textview.setScaleY((float) (textview.getScaleY() - 0.1));

            }
        });

        findViewById(R.id.okbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = findViewById(R.id.container);
                takeScreenshot(loadBitmapFromView(view, view.getWidth(), view.getHeight()));

            }
        });
    }

    private void takeScreenshot(Bitmap bitmap) {

        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures/ReweyouForums");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Reweyou", "failed to create directory");
                }
            }
            Random random = new Random();
            int m = random.nextInt(999999 - 100000) + 100000;

            String mPath = mediaStorageDir.toString() + "/" + m + ".jpg";
            File imageFile = new File(mPath);
            Uri uri = Uri.fromFile(imageFile);

            FileOutputStream outputStream = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent i = new Intent();
            i.putExtra("uri", uri.toString());
            setResult(RESULT_OK, i);
            finish();

            if (BuildConfig.DEBUG)
                MediaScannerConnection.scanFile(AddBackground.this, new String[]{mPath}, new String[]{"image/jpeg"}, null);

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.invalidate();
        v.draw(c);
        return b;
    }
}
