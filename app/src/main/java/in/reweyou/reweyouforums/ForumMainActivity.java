package in.reweyou.reweyouforums;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.fragment.CreateFragment;
import in.reweyou.reweyouforums.fragment.ExploreFragment;
import in.reweyou.reweyouforums.fragment.MainThreadsFragment;
import in.reweyou.reweyouforums.fragment.UserInfoFragment;
import in.reweyou.reweyouforums.fragment.YourGroupsFragment;
import in.reweyou.reweyouforums.utils.Utils;

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
                startActivityForResult(new Intent(ForumMainActivity.this, NotiActivity.class), Utils.REQ_CODE_NOTI);
            }
        });
        getnoticount();


        ImageView back = (ImageView) findViewById(R.id.backgroundimageview);
        final TextView tabnametoolbar = (TextView) toolbar.findViewById(R.id.tabnametoolbar);
        Typeface type = Typeface.createFromAsset(getAssets(), "cr.ttf");
        tabnametoolbar.setTypeface(type);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(4);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0)
                    tabnametoolbar.setText("Feeds");
                else if (position == 1)
                    tabnametoolbar.setText("Explore Groups");
                else if (position == 2)
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
        });
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

        switch (Utils.backgroundCode) {
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
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAppVersion();
            }
        }, 7000);

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
                    CreateFragment createFragment = (CreateFragment) pagerAdapter.getRegisteredFragment(3);
                    createFragment.onImageChoosen(result.getUri().toString());
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


    public void showExploreGroupFragment() {

        viewPager.setCurrentItem(1);
        ((YourGroupsFragment) pagerAdapter.getRegisteredFragment(2)).refreshlist();
    }

    @Override
    public void onBackPressed() {

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

    public void startCreateActivity() {
        Intent i = new Intent(this, CreateActivity.class);
        i.putExtra("frommain", true);
        startActivityForResult(i, Utils.REQ_CODE_CREATE_FROM_FORUMACTVITY);
    }

    public void refreshfeeds() {
        Log.d(TAG, "onResponse: dkwmdkwkkkkk11swsws11");

        ((MainThreadsFragment) pagerAdapter.getRegisteredFragment(0)).refreshList();
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
            else if (position == 1)
                return new ExploreFragment();
            else if (position == 3)
                return new CreateFragment();
            if (position == 2)
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
