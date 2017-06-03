package in.reweyou.reweyouforums;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import in.reweyou.reweyouforums.utils.Utils;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class FullImage extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private String imagepath;
    private ImageViewTouch imageView;
    private Toolbar toolbar;
    private String tag;
    private TextView headline;
    private String headlinetext;
    private TextView txtwallpaper;
    private TextView txtsave;
    private ProgressBar pd;
    private ProgressBar pdsave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        initToolbar();
        headline = (TextView) findViewById(R.id.headline);

        txtwallpaper = (TextView) findViewById(R.id.txtwallpaper);
        txtsave = (TextView) findViewById(R.id.txtsave);
        pd = (ProgressBar) findViewById(R.id.pd);
        pdsave = (ProgressBar) findViewById(R.id.pdsave);

        Bundle bundle = getIntent().getExtras();
        imagepath = bundle.getString("image");
        imageView = (ImageViewTouch) findViewById(R.id.image);
        showimage(imagepath);

        txtwallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                pd.setVisibility(View.VISIBLE);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = (int) (1.5 * displayMetrics.widthPixels);
                Log.d("ddd", "onClick: " + height + "  " + width);
                Glide.with(FullImage.this).load(imagepath).asBitmap().override(width, height).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                // you can do something with loaded bitmap here
                                // .....
                                new WallpaperAsync(resource).execute();


                            }
                        });
            }
        });

        txtsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.INVISIBLE);
                pdsave.setVisibility(View.VISIBLE);

                savephoto();
            }
        });
    }

    private void savephoto() {


        Glide.with(FullImage.this).load(imagepath).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

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
                    final FileOutputStream outputStream = new FileOutputStream(imageFile);
                    resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Toast.makeText(FullImage.this, "Image saved!", Toast.LENGTH_SHORT).show();
                    txtsave.setVisibility(View.VISIBLE);
                    pdsave.setVisibility(View.INVISIBLE);
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{mPath}, new String[]{"image/jpeg"}, null);


                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void updateUI() {
        pd.setVisibility(View.INVISIBLE);
        txtwallpaper.setVisibility(View.VISIBLE);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showimage(String i) {
        Glide.with(FullImage.this).load(i).diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().into(imageView);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class WallpaperAsync extends AsyncTask<Void, Void, Void> {
        private final Bitmap bitmap;

        public WallpaperAsync(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "setting wallpaper...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "wallpaper set!", Toast.LENGTH_SHORT).show();
            try {
                updateUI();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... params) {

            WallpaperManager myWallpaperManager
                    = WallpaperManager.getInstance(getApplicationContext());
            try {
                Log.d("feeds", "doInBackground:ewlm " + bitmap.getWidth() + "  " + bitmap.getHeight());
                if (bitmap.getWidth() > (1.5 * Utils.screenWidth)) {
                    Bitmap finalBitmap = Bitmap.createBitmap(bitmap, (int) ((bitmap.getWidth() / 2) - ((1.5 * Utils.screenWidth) / 2)), 0, (int) (1.5 * Utils.screenWidth), bitmap.getHeight());
                    Log.d("feeds", "doInBackground: " + finalBitmap.getWidth() + "  " + finalBitmap.getHeight());
                    myWallpaperManager.setBitmap(finalBitmap);

                } else myWallpaperManager.setBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

}
