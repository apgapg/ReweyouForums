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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import app.minimize.com.seek_bar_compat.SeekBarCompat;
import in.reweyou.reweyouforums.customView.CircularImageView;
import in.reweyou.reweyouforums.customView.DraggableImageView;
import in.reweyou.reweyouforums.customView.DraggableTextView;
import in.reweyou.reweyouforums.freedrawview.FreeDrawView;
import in.reweyou.reweyouforums.freedrawview.PathDrawnListener;
import in.reweyou.reweyouforums.freedrawview.PathRedoUndoCountChangeListener;
import in.reweyou.reweyouforums.freedrawview.ResizeBehaviour;
import in.reweyou.reweyouforums.utils.Utils;

public class EditImageActivity extends AppCompatActivity implements View.OnClickListener {

    private FreeDrawView mSignatureView;
    private ImageView demoview;
    private String TAG = EditImageActivity.class.getName();
    private String imageuri;
    private boolean isEdited;
    private Uri finalimageuri;
    private String fromimageview;

    private Toolbar toolbar;
    private RelativeLayout drawcontainer;
    private RelativeLayout emojicustomizecontainer;
    private SeekBarCompat seekbar_emoji_scale;
    private SeekBarCompat seekbar_emoji_rotate;
    private View tempview;
    private ImageView deleteemoji;


    public Bitmap mergeImages(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, 0, 0, null);
        canvas.drawBitmap(bmp2, 0, 0, null);

        return bmOverlay;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        drawcontainer = (RelativeLayout) findViewById(R.id.drawcontainer);
        imageuri = getIntent().getStringExtra("uri");
        fromimageview = getIntent().getStringExtra("from");

        initColorsBar();
        initEmojiBar();
        initTextviewBar();
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
        mSignatureView.addcontainerview(emojicustomizecontainer);
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

        Glide.with(EditImageActivity.this).load(imageuri).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                ViewGroup.LayoutParams params = mSignatureView.getLayoutParams();
                params.height = resource.getHeight();
                params.width = resource.getWidth();
                mSignatureView.setLayoutParams(params);

                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(resource.getWidth(), resource.getHeight());
                param.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                drawcontainer.setLayoutParams(param);
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


    private void initTextviewBar() {
        final TextView textTextview = (TextView) findViewById(R.id.text);

        textTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EditImageActivity.this, InsertTextActivity.class);
                i.putExtra("text", "");
                startActivityForResult(i, Utils.REQ_CODE_INSERT_TEXT);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Utils.REQ_CODE_INSERT_TEXT) {
                if (tempview != null) {
                    if (tempview instanceof DraggableTextView) {
                        ((DraggableTextView) tempview).setText(data.getStringExtra("text"));
                    } else insertDraggableTextView(data.getStringExtra("text"));
                } else insertDraggableTextView(data.getStringExtra("text"));

            }
        }
    }

    private void insertDraggableTextView(String text) {
        DraggableTextView textview = new DraggableTextView(EditImageActivity.this);
        textview.setText(text);
        textview.setOnSingleClickListener(new DraggableTextView.SingleClickcallback() {
            @Override
            public void onSingleClick(DraggableTextView draggableTextView) {
                EditImageActivity.this.tempview = draggableTextView;
                Intent i = new Intent(EditImageActivity.this, InsertTextActivity.class);
                i.putExtra("text", draggableTextView.getText().toString());
                startActivityForResult(i, Utils.REQ_CODE_INSERT_TEXT);
            }
        });

        textview.setonActionDownListener(new DraggableTextView.ActionDowncallback() {
            @Override
            public void onActionDown(DraggableTextView draggableTextView) {
                tempview = draggableTextView;
                emojicustomizecontainer.setVisibility(View.VISIBLE);
                seekbar_emoji_scale.setProgress((int) ((draggableTextView.getScaleX() - 0.5) / 0.015));
                seekbar_emoji_rotate.setProgress((int) ((100.0 / 360.0) * draggableTextView.getRotation()));
            }
        });


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        drawcontainer.addView(textview, params);
    }

    private void initEmojiBar() {
        CircularImageView e1 = (CircularImageView) findViewById(R.id.e1);
        CircularImageView e2 = (CircularImageView) findViewById(R.id.e2);
        CircularImageView e3 = (CircularImageView) findViewById(R.id.e3);
        CircularImageView e4 = (CircularImageView) findViewById(R.id.e4);
        CircularImageView e5 = (CircularImageView) findViewById(R.id.e5);
        e1.setOnClickListener(this);
        e2.setOnClickListener(this);
        e3.setOnClickListener(this);
        e4.setOnClickListener(this);
        e5.setOnClickListener(this);

        emojicustomizecontainer = (RelativeLayout) findViewById(R.id.emojicustomizecontainer);
        seekbar_emoji_scale = (SeekBarCompat) findViewById(R.id.seekbar_scale);
        seekbar_emoji_rotate = (SeekBarCompat) findViewById(R.id.seekbar_rotate);

        seekbar_emoji_scale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tempview.setScaleX((float) ((progress * 0.015) + 0.50));
                    tempview.setScaleY((float) ((progress * 0.015) + 0.50));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekbar_emoji_rotate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    tempview.setRotation((float) (3.6 * progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        deleteemoji = (ImageView) findViewById(R.id.deleteemoji);
        deleteemoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawcontainer.removeView(tempview);
                emojicustomizecontainer.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void initColorsBar() {

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
        CircularImageView c12 = (CircularImageView) findViewById(R.id.c12);
        CircularImageView c13 = (CircularImageView) findViewById(R.id.c13);

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
        c12.setOnClickListener(this);
        c13.setOnClickListener(this);
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
            case R.id.c12:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c12));

                break;
            case R.id.c13:
                mSignatureView.setPaintColor(getResources().getColor(R.color.c13));

                break;


            case R.id.e1:
                emojicustomizecontainer.setVisibility(View.VISIBLE);

                addemoji(1);
                break;
            case R.id.e2:
                emojicustomizecontainer.setVisibility(View.VISIBLE);

                addemoji(2);
                break;
            case R.id.e3:
                emojicustomizecontainer.setVisibility(View.VISIBLE);

                addemoji(3);
                break;
            case R.id.e4:
                emojicustomizecontainer.setVisibility(View.VISIBLE);

                addemoji(4);
                break;
            case R.id.e5:
                emojicustomizecontainer.setVisibility(View.VISIBLE);

                addemoji(5);
                break;
        }


    }

    private void addemoji(int i) {
        seekbar_emoji_scale.setProgress(33);
        seekbar_emoji_rotate.setProgress(0);
        DraggableImageView draggableImageView = new DraggableImageView(EditImageActivity.this);
        draggableImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showCustomizeEmojiBar(v);
                    EditImageActivity.this.tempview = v;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                }
                return false;
            }
        });
        EditImageActivity.this.tempview = draggableImageView;

        switch (i) {
            case 1:
                draggableImageView.setImageResource(R.drawable.emoji_laugh);
                break;
            case 2:
                draggableImageView.setImageResource(R.drawable.emoji_love);
                break;
            case 3:
                draggableImageView.setImageResource(R.drawable.emoji_cry);
                break;
            case 4:
                draggableImageView.setImageResource(R.drawable.emoji_sad);
                break;
            case 5:
                draggableImageView.setImageResource(R.drawable.emoji_surprise);
                break;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        drawcontainer.addView(draggableImageView, params);

    }

    private void showCustomizeEmojiBar(View v) {
        Log.d(TAG, "showCustomizeEmojiBar: ewfnwejfnwejfwfw");
        emojicustomizecontainer.setVisibility(View.VISIBLE);
        seekbar_emoji_scale.setProgress((int) ((v.getScaleX() - 0.5) / 0.015));
        seekbar_emoji_rotate.setProgress((int) ((100.0 / 360.0) * v.getRotation()));
        // seekbar_emoji_scale.setProgress(v.getScaleX());
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
              /*  Bitmap mBitmap = Bitmap.createBitmap(
                        mSignatureView.getWidth(), mSignatureView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas mCanvas = new Canvas(mBitmap);
                mSignatureView.draw(mCanvas);


                takeScreenshot(((GlideBitmapDrawable) demoview.getDrawable()).getBitmap(), mBitmap);

*/
            takeScreenshot(loadBitmapFromView(drawcontainer, drawcontainer.getWidth(), drawcontainer.getHeight()));

        }
        return super.onOptionsItemSelected(item);
    }


    public Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.invalidate();
        v.draw(c);
        return b;
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
            i.putExtra("from", fromimageview);
            setResult(RESULT_OK, i);
            finish();

            if (BuildConfig.DEBUG)
                MediaScannerConnection.scanFile(EditImageActivity.this, new String[]{mPath}, new String[]{"image/jpeg"}, null);

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
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


    }

}
