package in.reweyou.reweyouforums;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import in.reweyou.reweyouforums.customView.CircularImageView;
import in.reweyou.reweyouforums.freedrawview.FreeDrawView;
import in.reweyou.reweyouforums.freedrawview.PathDrawnListener;
import in.reweyou.reweyouforums.freedrawview.PathRedoUndoCountChangeListener;
import in.reweyou.reweyouforums.freedrawview.ResizeBehaviour;

public class EditImageActivity extends AppCompatActivity implements View.OnClickListener {

    private FreeDrawView mSignatureView;
    private ImageView demoview;
    private String TAG = EditImageActivity.class.getName();
    private String imageuri;
    private boolean isEdited;
    private Uri finalimageuri;
    private String fromimageview;

    public static Bitmap mergeImages(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);
        return bmOverlay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageuri = getIntent().getStringExtra("uri");
        fromimageview = getIntent().getStringExtra("from");

        CircularImageView c1 = (CircularImageView) findViewById(R.id.c1);
        CircularImageView c2 = (CircularImageView) findViewById(R.id.c2);
        CircularImageView c3 = (CircularImageView) findViewById(R.id.c3);
        CircularImageView c4 = (CircularImageView) findViewById(R.id.c4);
        CircularImageView c5 = (CircularImageView) findViewById(R.id.c5);
        CircularImageView c6 = (CircularImageView) findViewById(R.id.c6);
        CircularImageView c7 = (CircularImageView) findViewById(R.id.c7);
        CircularImageView c8 = (CircularImageView) findViewById(R.id.c8);
        CircularImageView c9 = (CircularImageView) findViewById(R.id.c9);
        CircularImageView c10 = (CircularImageView) findViewById(R.id.c10);
        CircularImageView c11 = (CircularImageView) findViewById(R.id.c11);

        c1.setOnClickListener(this);
        c2.setOnClickListener(this);
        c3.setOnClickListener(this);
        c4.setOnClickListener(this);
        c5.setOnClickListener(this);
        c6.setOnClickListener(this);
        c7.setOnClickListener(this);
        c8.setOnClickListener(this);
        c9.setOnClickListener(this);
        c10.setOnClickListener(this);
        c11.setOnClickListener(this);

        demoview = (ImageView) findViewById(R.id.image);

        SeekBarCompat seekBarCompat = (SeekBarCompat) findViewById(R.id.materialSeekBar);
        seekBarCompat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.percent)).setText("" + (100 - ((int) (0.7 * progress))) + "%");
                mSignatureView.setPaintAlpha((255 / 100) * (100 - ((int) (0.7 * progress))));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBarCompat seekBarCompatsize = (SeekBarCompat) findViewById(R.id.materialSeekBarSize);
        seekBarCompatsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mSignatureView.setPaintWidthDp((int) (0.32 * progress) + 4);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSignatureView = (FreeDrawView) findViewById(R.id.your_id);

        // Setup the View
        mSignatureView.setPaintColor(Color.BLACK);
        mSignatureView.setPaintWidthPx(12);
        mSignatureView.setPaintWidthDp(6);
        mSignatureView.setPaintAlpha(255);// from 0 to 255
        mSignatureView.setResizeBehaviour(ResizeBehaviour.CROP);// Must be one of ResizeBehaviour
        // values;

        // This listener will be notified every time the path done and undone count changes
        mSignatureView.setPathRedoUndoCountChangeListener(new PathRedoUndoCountChangeListener() {
            @Override
            public void onUndoCountChanged(int undoCount) {
                // The undoCount is the number of the paths that can be undone
            }

            @Override
            public void onRedoCountChanged(int redoCount) {
                // The redoCount is the number of path removed that can be redrawn
            }
        });
        // This listener will be notified every time a new path has been drawn
        mSignatureView.setOnPathDrawnListener(new PathDrawnListener() {
            @Override
            public void onNewPathDrawn() {
                // The user has finished drawing a path
            }

            @Override
            public void onPathStart() {
                // The user has started drawing a path
            }
        });

        // This will take a screenshot of the current drawn content of the view
/*
        mSignatureView.getDrawScreenshot(new FreeDrawView.DrawCreatorListener() {
            @Override
            public void onDrawCreated(Bitmap draw) {
                // The draw Bitmap is the drawn content of the View
            }

            @Override
            public void onDrawCreationError() {
                // Something went wrong creating the bitmap, should never
                // happen unless the async task has been canceled
            }
        });
*/


        Glide.with(EditImageActivity.this).load(imageuri).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.d(TAG, "onResourceReady:height " + resource.getIntrinsicHeight());
                Log.d(TAG, "onResourceReady:width " + resource.getIntrinsicWidth());
                ViewGroup.LayoutParams params = mSignatureView.getLayoutParams();
                params.height = resource.getIntrinsicHeight();
                params.width = resource.getIntrinsicWidth();
                mSignatureView.setLayoutParams(params);

                return false;
            }
        }).into(demoview);


        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignatureView.undoLast();
            }
        });
        findViewById(R.id.redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignatureView.redoLast();
            }
        });

    }

    private void takeScreenshot(Bitmap b1, Bitmap b2) {

        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures/ReweyouForums/temp");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Reweyou", "failed to create directory");
                }
            }
            Random random = new Random();
            int m = random.nextInt(999999 - 100000) + 100000;

            String mPath = mediaStorageDir.toString() + "/" + m + ".jpg";
            File imageFile = new File(mPath);
            finalimageuri = Uri.fromFile(imageFile);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 99;

            mergeImages(b1, b2).compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent i = new Intent();
            i.putExtra("uri", finalimageuri.toString());
            i.putExtra("from", fromimageview);
            setResult(RESULT_OK, i);
            finish();

            if (BuildConfig.DEBUG)
                MediaScannerConnection.scanFile(EditImageActivity.this.getApplicationContext(), new String[]{mPath}, new String[]{"image/jpeg"}, null);

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.c1:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c1));
                break;
            case R.id.c2:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c2));

                break;
            case R.id.c3:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c3));

                break;
            case R.id.c4:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c4));

                break;
            case R.id.c5:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c5));

                break;
            case R.id.c6:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c6));

                break;
            case R.id.c7:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c7));

                break;
            case R.id.c8:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c8));

                break;
            case R.id.c9:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c9));

                break;
            case R.id.c10:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c10));

                break;
            case R.id.c11:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c11));

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_image, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            // do something here
            if (mSignatureView.isEdited()) {
                Bitmap mBitmap = Bitmap.createBitmap(
                        mSignatureView.getWidth(), mSignatureView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas mCanvas = new Canvas(mBitmap);
                mSignatureView.draw(mCanvas);
                takeScreenshot(((GlideBitmapDrawable) demoview.getDrawable()).getBitmap(), mBitmap);


            } else finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSignatureView.isEdited()) {
            AlertDialogBox alertDialogBox = new AlertDialogBox(EditImageActivity.this, "Discard Changes?", "Your changes would be lost! Proceed back?", "No", "Yes") {
                @Override
                public void onNegativeButtonClick(DialogInterface dialog) {
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onPositiveButtonClick(DialogInterface dialog) {
                    dialog.dismiss();
                }
            };
            alertDialogBox.show();
        } else finish();
    }
}
