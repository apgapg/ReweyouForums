package in.reweyou.reweyouforums;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.customView.ColorTextView;
import in.reweyou.reweyouforums.fragment.CreateGroupFragment;
import in.reweyou.reweyouforums.fragment.ExploreFragment;
import in.reweyou.reweyouforums.fragment.MainThreadsFragment;
import in.reweyou.reweyouforums.fragment.UserInfoFragment;
import in.reweyou.reweyouforums.fragment.YourGroupsFragment;
import in.reweyou.reweyouforums.model.BadgeModel;
import in.reweyou.reweyouforums.model.TopGroupMemberModel;
import in.reweyou.reweyouforums.utils.NetworkHandler;
import in.reweyou.reweyouforums.utils.Utils;
import io.paperdb.Paper;

public class ForumMainActivity extends AppCompatActivity {

    private static final String TAG = ForumMainActivity.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ImagePicker imagePicker;
    private PagerAdapter pagerAdapter;
    private int positionFragment = -1;
    private ViewPager viewPager;
    private UserSessionManager userSessionManager;
    private boolean doubleBackToExitPressedOnce = false;
    private ImageView noti;
    private TextView notinum;
    private TextView notiback;
    private boolean firstload;
    private Uri uri;
    private boolean isBadgeDialogShown;
    private View confirmDialog;
    private BadgeModel badgeModel;
    private TextView tabnametoolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userSessionManager = new UserSessionManager(this);

        notiback = (TextView) toolbar.findViewById(R.id.notiback);
        notinum = (TextView) toolbar.findViewById(R.id.notinum);
        noti = (ImageView) toolbar.findViewById(R.id.noti);

        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ForumMainActivity.this, NotificationActivity.class), Utils.REQ_CODE_NOTI);
            }
        });
        getnoticount();


        ImageView back = (ImageView) findViewById(R.id.backgroundimageview);
        tabnametoolbar = (TextView) toolbar.findViewById(R.id.tabnametoolbar);
        Typeface type = Typeface.createFromAsset(getAssets(), "cr.ttf");
        tabnametoolbar.setTypeface(type);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(1);
      /*  viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0)
                    tabnametoolbar.setText("Feeds");
                else if (position == 1) {
                    tabnametoolbar.setText("Explore Groups");
                } else if (position == 2)
                    tabnametoolbar.setText("Your Groups");
                else if (position == 3)
                    tabnametoolbar.setText("Create Group");
                else if (position == 4)
                    tabnametoolbar.setText("My Profile");
                try {
                    InputMethodManager imm = (InputMethodManager) ForumMainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ForumMainActivity.this.getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab tabCall1 = tabLayout.getTabAt(0);
        tabCall1.setIcon(R.drawable.tab1_selector);
        TabLayout.Tab tabCall2 = tabLayout.getTabAt(1);
        tabCall2.setIcon(R.drawable.tab2_selector);
        TabLayout.Tab tabCall3 = tabLayout.getTabAt(2);
        tabCall3.setIcon(R.drawable.tab5_selector);
        TabLayout.Tab tabCall4 = tabLayout.getTabAt(3);
        tabCall4.setIcon(R.drawable.tab3_selector);
        TabLayout.Tab tabCall5 = tabLayout.getTabAt(4);
        tabCall5.setIcon(R.drawable.tab4_selector);

       /* switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_blue_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_blue));
                break;
            case 2:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_green_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_green));
                break;
            case 3:
                back.setColorFilter(ForumMainActivity.this.getResources().getColor(R.color.main_background_pink_alpha));
                tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_background_pink));
                break;
        }*/
        tabLayout.setSelectedTabIndicatorColor(ForumMainActivity.this.getResources().getColor(R.color.main_back_purple_neutral));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAppVersion();
            }
        }, 7000);


        getMembersData();

    }

    private void getMembersData() {


        AndroidNetworking.post("https://www.reweyou.in/google/tag_user.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("memberdata")

                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (new NetworkHandler().isActivityAlive(TAG, ForumMainActivity.this, response)) {

                            Gson gson = new Gson();
                            List<TopGroupMemberModel> list = new ArrayList<>();

                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject json = response.getJSONObject(i);
                                    TopGroupMemberModel coModel = gson.fromJson(json.toString(), TopGroupMemberModel.class);
                                    if (!coModel.getUid().equals(userSessionManager.getUID()))
                                        list.add(coModel);

                                }

                                Paper.init(ForumMainActivity.this);
                                Paper.book().write("member", list);

                            } catch (Exception e) {
                                e.printStackTrace();

                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (new NetworkHandler().isActivityAlive(TAG, ForumMainActivity.this, anError)) {


                            Log.d(TAG, "onError: " + anError);
                        }
                    }
                });
    }

    private void getnoticount() {

        AndroidNetworking.post("https://www.reweyou.in/google/notification_count.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("report")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: noticount: " + response);
                        if (!response.equals("0")) {
                            notinum.setVisibility(View.VISIBLE);
                            notiback.setVisibility(View.VISIBLE);

                            notinum.setText(response);
                        } else {
                            notinum.setVisibility(View.INVISIBLE);
                            notiback.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);

                    }
                });
    }

    private void checkAppVersion() {
        AndroidNetworking.get("https://www.reweyou.in/google/version.php")
                .setTag("report")
                .setPriority(Priority.LOW)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        int versionCode = BuildConfig.VERSION_CODE;

                        if (Integer.parseInt(response) > versionCode) {
                            AlertDialogBox alertDialogBox = new AlertDialogBox(ForumMainActivity.this, "Update Available", "A new update is available. Please update the app in order to have rich user experience", "UPDATE", "LATER") {
                                @Override
                                public void onNegativeButtonClick(DialogInterface dialog) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onPositiveButtonClick(DialogInterface dialog) {
                                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                    try {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                    }
                                }
                            };
                            alertDialogBox.show();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                    }
                });
    }

    public void showPickImage(int i) {
        this.positionFragment = i;
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                               @Override
                                               public void onImagesChosen(List<ChosenImage> images) {

                                                   onImageChoosenbyUser(images);

                                               }

                                               @Override
                                               public void onError(String message) {
                                                   // Do error handling
                                                   Log.e(TAG, "onError: " + message);
                                               }
                                           }

        );

        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(false);
        imagePicker.pickImage();

    }

    private void onImageChoosenbyUser(List<ChosenImage> images) {
        if (images != null) {

            try {

                Log.d(TAG, "onImagesChosen: size" + images.size());
                if (images.size() > 0) {
                    Log.d(TAG, "onImagesChosen: path" + images.get(0).getOriginalPath() + "  %%%   " + images.get(0).getThumbnailSmallPath());

                    if (images.get(0).getOriginalPath() != null) {
                        Log.d(TAG, "onImagesChosen: " + images.get(0).getFileExtensionFromMimeTypeWithoutDot());
                        if (images.get(0).getFileExtensionFromMimeTypeWithoutDot().equals("gif")) {
                            // handleGif(images.get(0).getOriginalPath());
                            Toast.makeText(this, "Only image can be uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            startImageCropActivity(Uri.parse(images.get(0).getQueryUri()));
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong. ErrorCode: 19", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startImageCropActivity(Uri data) {
        CropImage.activity(data)
                .setActivityTitle("Crop Image")
                .setBackgroundColor(Color.parseColor("#90000000"))
                .setMinCropResultSize(200, 200)
                .setFixAspectRatio(true)
                .setAspectRatio(1, 1)
                .setBorderCornerColor(getResources().getColor(R.color.colorPrimaryDark))
                .setBorderLineColor(getResources().getColor(R.color.colorPrimary))
                .setGuidelinesColor(getResources().getColor(R.color.divider))
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + "   " + resultCode);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                if (positionFragment == 3) {
                    CreateGroupFragment createGroupFragment = (CreateGroupFragment) pagerAdapter.getRegisteredFragment(3);
                    createGroupFragment.onImageChoosen(result.getUri().toString());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                imagePicker.submit(data);
            } else if (requestCode == Utils.REQ_CODE_GROP_ACITIVTY) {
                ((ExploreFragment) pagerAdapter.getRegisteredFragment(1)).refreshlist();
                ((YourGroupsFragment) pagerAdapter.getRegisteredFragment(2)).refreshlist();
            } else if (requestCode == Utils.REQ_CODE_EDIT_PROFILE) {
                ((UserInfoFragment) pagerAdapter.getRegisteredFragment(4)).refreshprofile();

            } else if (requestCode == Utils.REQ_CODE_CREATE_FROM_FORUMACTVITY) {
                ((MainThreadsFragment) pagerAdapter.getRegisteredFragment(0)).refreshList();

            } else if (requestCode == Utils.REQ_CODE_NOTI) {
                Log.d(TAG, "onActivityResult: dwjdnwndwdwnoti");
                getnoticount();

            }
        }


    }


    public void showYourGroupsFragment() {

        viewPager.setCurrentItem(2);
        ((YourGroupsFragment) pagerAdapter.getRegisteredFragment(2)).refreshlist();
    }

    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() != 1) {
            viewPager.setCurrentItem(1);
        } else {
            if (doubleBackToExitPressedOnce) {

                finishAffinity();
            }
            if (!doubleBackToExitPressedOnce)
                Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();

            this.doubleBackToExitPressedOnce = true;

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 3000);
        }
    }

    public void startCreateActivity() {
        Intent i = new Intent(this, CreatePostActivity.class);
        i.putExtra("frommain", true);
        startActivityForResult(i, Utils.REQ_CODE_CREATE_FROM_FORUMACTVITY);
    }

    public void refreshfeeds() {
        Log.d(TAG, "onResponse: dkwmdkwkkkkk11swsws11");

        ((MainThreadsFragment) pagerAdapter.getRegisteredFragment(0)).refreshList();
    }

    private void showBadgeUpdate() {
        //Creating a LayoutInflater object for the dialog box
        final LayoutInflater li = LayoutInflater.from(ForumMainActivity.this);
        //Creating a view to get the dialog box
        confirmDialog = li.inflate(R.layout.dialog_badge_update, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final TextView buttonconfirm = (TextView) confirmDialog.findViewById(R.id.buttonConfirm);
        final TextView share = (TextView) confirmDialog.findViewById(R.id.share);
        final ColorTextView des = (ColorTextView) confirmDialog.findViewById(R.id.des);
        final ImageView groupimage = (ImageView) confirmDialog.findViewById(R.id.groupimage);


        Paper.init(this);
        badgeModel = Paper.book().read("notibadge");
        des.setText("You have earned " + badgeModel.getBadge() + " badge in " + badgeModel.getGroupname() + ". Share your achievement with friends.");
        des.findAndSetStrColor(badgeModel.getBadge(), "#C51162");
        des.findAndSetStrColor(badgeModel.getGroupname(), "#C51162");


        Glide.with(ForumMainActivity.this).load(badgeModel.getImage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(groupimage);

        AlertDialog.Builder alert = new AlertDialog.Builder(ForumMainActivity.this);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.setCancelable(false);
        alertDialog.show();


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   takeScreenshot(confirmDialog.findViewById(R.id.rootlayout), badgeModel.getGroupname());
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else
                    takeScreenshot(confirmDialog.findViewById(R.id.rootlayout), badgeModel.getGroupname());
            }
        });

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                alertDialog.dismiss();
                userSessionManager.putinsharedpref("notibadge", 0);

            }
        });

    }

    private void checkStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        try {
                            takeScreenshot(confirmDialog.findViewById(R.id.rootlayout), badgeModel.getGroupname());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ForumMainActivity.this, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (firstload) {
            try {
                getnoticount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        firstload = true;

        if (userSessionManager.getvaluefromsharedpref("notibadge") == 1 && !isBadgeDialogShown) {
            isBadgeDialogShown = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    showBadgeUpdate();
                }
            }, 2000);
        }
    }

    private void takeScreenshot(View cv, String groupname) {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Pictures/ReweyouForums/Screenshot");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Reweyou", "failed to create directory");
                }
            }

            String mPath = mediaStorageDir.toString() + "/" + now + ".jpg";
            File imageFile = new File(mPath);
            Uri uri = Uri.fromFile(imageFile);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 99;
            cv.setDrawingCacheEnabled(true);
            cv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            loadBitmapFromView(cv).compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            try {
                shareIntent(uri, groupname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight() - Utils.convertpxFromDp(8), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
        v.draw(c);

        final DisplayMetrics metrics = ForumMainActivity.this.getResources().getDisplayMetrics();
        final Bitmap b2 = drawToBitmap(ForumMainActivity.this, R.layout.share_reweyou_tag, metrics.widthPixels, metrics.heightPixels);

        return combineImages(b, b2);
    }

    private Bitmap drawToBitmap(Context context, final int layoutResId, final int width, final int height) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(layoutResId, null);
        layout.setDrawingCacheEnabled(true);
        layout.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        final Bitmap bmp = Bitmap.createBitmap(layout.getMeasuredWidth(), layout.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        final Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(layout.getDrawingCache(), 0, 0, new Paint());
        return bmp;
    }

    private Bitmap combineImages(Bitmap c, Bitmap s) {
        Bitmap cs = null;

        int width, height = 0;

        width = c.getWidth();
        height = c.getHeight() + s.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas comboImage = new Canvas(cs);
        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);
        Bitmap resizedbitmap1 = Bitmap.createBitmap(cs, Utils.convertpxFromDp(10), Utils.convertpxFromDp(10), cs.getWidth() - Utils.convertpxFromDp(16), cs.getHeight() - Utils.convertpxFromDp(10));

        return resizedbitmap1;
    }

    private void shareIntent(Uri uri, String groupname) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Join '" + groupname + "' group in ReweyouForums app. Download now: https://play.google.com/store/apps/details?id=in.reweyou.reweyouforums");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        startActivity(Intent.createChooser(intent, "Share Group using"));
    }

    public void onFeedCardChange(String s) {
        tabnametoolbar.setText(s);
    }

    public void setFeedPage() {
        viewPager.setCurrentItem(1);
    }

    public void movetonextcard() {
        ((MainThreadsFragment) pagerAdapter.getRegisteredFragment(1)).changenextcard();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.tabs);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 4)
                return new UserInfoFragment();
            else if (position == 2)
                return new ExploreFragment();
            else if (position == 3)
                return new CreateGroupFragment();
            if (position == 0)
                return new YourGroupsFragment();
            else
                return new MainThreadsFragment();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


        @Override
        public int getCount() {
            return tabs.length;
        }


    }

}
