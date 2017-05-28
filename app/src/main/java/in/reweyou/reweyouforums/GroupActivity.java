package in.reweyou.reweyouforums;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.fragment.GroupInfoFragment;
import in.reweyou.reweyouforums.fragment.GroupThreadsFragment;
import in.reweyou.reweyouforums.utils.Utils;

public class GroupActivity extends AppCompatActivity {
    private static final String TAG = GroupActivity.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ImageView back;
    private int positionFragment = -1;
    private ImagePicker imagePicker;
    private PagerAdapter pagerAdapter;
    private LinearLayout uploadingContainer;
    private UserSessionManager userSessionManager;
    private TextView okbbutton;
    private ProgressBar uploadingpd;
    private TextView uploadingtext;
    private Intent intentData;
    private String image1encoded = "";
    private String image2encoded = "";
    private String image3encoded = "";
    private String image4encoded = "";
    private String groupid;
    private String groupname;
    private String groupmembers;
    private String groupimage = "";
    private boolean isfollowed;
    private String groupadmin = "";
    private String groupdescription = "";
    private String grouprules = "";
    private String groupthreads = "";
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        try {

            groupname = getIntent().getStringExtra("groupname");
            groupid = getIntent().getStringExtra("groupid");
            groupmembers = getIntent().getStringExtra("members");
            groupthreads = getIntent().getStringExtra("threads");
            groupimage = getIntent().getStringExtra("image");
            groupadmin = getIntent().getStringExtra("admin");
            groupdescription = getIntent().getStringExtra("description");
            grouprules = getIntent().getStringExtra("rules");
            isfollowed = getIntent().getBooleanExtra("follow", false);

            getSupportActionBar().setTitle(groupname);


        } catch (Exception e) {
            e.printStackTrace();
        }

        userSessionManager = new UserSessionManager(this);
        initUploadingContainer();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        back = (ImageView) findViewById(R.id.backgroundimageview);
        setBackgroundtint();

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(1);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        if (isfollowed)
            viewPager.setCurrentItem(1);
    }

    private void initUploadingContainer() {
        uploadingContainer = (LinearLayout) findViewById(R.id.uploadingContainer);
        uploadingtext = (TextView) findViewById(R.id.uploadingtext);
        uploadingpd = (ProgressBar) findViewById(R.id.uploadingprogressbar);
        okbbutton = (TextView) findViewById(R.id.okbutton);
        okbbutton.setTag("1");
        okbbutton.setVisibility(View.GONE);
        okbbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okbbutton.getTag().equals("1")) {
                    uploadingContainer.setVisibility(View.GONE);
                } else if (okbbutton.getTag().equals("0")) {
                    showUploading();
                    uploadGroup();

                }
            }
        });

    }

    private void setBackgroundtint() {
        switch (Utils.backgroundCode) {
            case 0:
                break;
            case 1:
                back.setColorFilter(this.getResources().getColor(R.color.main_background_blue_alpha));
                break;
            case 2:
                back.setColorFilter(this.getResources().getColor(R.color.main_background_green_alpha));
                break;
            case 3:
                back.setColorFilter(this.getResources().getColor(R.color.main_background_pink_alpha));
                break;
        }
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
        Log.d(TAG, "onActivityResult: " + requestCode + "    " + resultCode);


        if (requestCode == 19) {
            if (resultCode == RESULT_OK) {
                this.intentData = data;
                compressImages();
            }
        } else if (resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: eff333333333");
            if (requestCode == Utils.REQ_CODE_EDIT_GROUP_ACTIVITY) {
                Log.d(TAG, "onActivityResult: ewnjdnedejwndnjwendjkwe ncjx");

                ((GroupInfoFragment) pagerAdapter.getRegisteredFragment(0)).refreshDetails(data.getStringExtra("description"), data.getStringExtra("rules"), data.getStringExtra("image"));
                setResult(RESULT_OK);

            }
        }

    }

    private void compressImages() {
        showUploading();
        final int count = intentData.getIntExtra("counter", 0);
        if (count > 0) {
            Glide.with(this).load(intentData.getStringExtra("image1")).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                @Override
                public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                    image1encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                    if (count > 1)
                        Glide.with(GroupActivity.this).load(intentData.getStringExtra("image2")).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                            @Override
                            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                image2encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                if (count > 2)
                                    Glide.with(GroupActivity.this).load(intentData.getStringExtra("image3")).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                                        @Override
                                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                            image3encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                            if (count > 3)
                                                Glide.with(GroupActivity.this).load(intentData.getStringExtra("image4")).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                                                    @Override
                                                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                                        image4encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                                        uploadGroup();
                                                    }
                                                });
                                            else uploadGroup();

                                        }
                                    });
                                else uploadGroup();

                            }
                        });
                    else uploadGroup();

                }
            });
        } else uploadGroup();
    }

    private void uploadGroup() {

        Intent data = intentData;
        //showUploading();
        AndroidNetworking.post("https://www.reweyou.in/google/create_threads.php")
                .addBodyParameter("groupname", groupname)
                .addBodyParameter("groupid", groupid)
                .addBodyParameter("description", data.getStringExtra("description"))
                .addBodyParameter("link", data.getStringExtra("link"))
                .addBodyParameter("linkdesc", data.getStringExtra("linkdesc"))
                .addBodyParameter("linkhead", data.getStringExtra("linkhead"))
                .addBodyParameter("linkimage", data.getStringExtra("linkimage"))
                .addBodyParameter("image1", image1encoded)
                .addBodyParameter("image2", image2encoded)
                .addBodyParameter("image3", image3encoded)
                .addBodyParameter("image4", image4encoded)
                .addBodyParameter("type", data.getStringExtra("type"))
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("name", userSessionManager.getUsername())
                .addBodyParameter("profilepic", userSessionManager.getProfilePicture())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.contains("Thread created")) {
                            showUploadSuccessful();
                            userSessionManager.setBadge(response.replace(" Thread created", ""));

                        } else {
                            showFailedUpload();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        showFailedUpload();
                    }
                });

    }

    private void showUploading() {
        uploadingpd.setVisibility(View.VISIBLE);
        uploadingtext.setText("Uploading Post");
        okbbutton.setText("OK");
        okbbutton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_layer, 0, 0, 0);
        okbbutton.setVisibility(View.GONE);
        uploadingContainer.setVisibility(View.VISIBLE);
    }

    private void showUploadSuccessful() {

        uploadingpd.setVisibility(View.INVISIBLE);
        uploadingtext.setText("Upload successful.");
        okbbutton.setTag("1");
        okbbutton.setText("OK");
        okbbutton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_layer, 0, 0, 0);
        okbbutton.setVisibility(View.VISIBLE);

        ((GroupThreadsFragment) pagerAdapter.getRegisteredFragment(1)).refreshList();


    }

    private void showFailedUpload() {
        uploadingpd.setVisibility(View.INVISIBLE);
        uploadingtext.setText("Upload failed.");
        okbbutton.setText("Retry");

        okbbutton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_refresh_layer, 0, 0, 0);
        okbbutton.setTag("0");
        okbbutton.setVisibility(View.VISIBLE);

    }


    public void startCreateActivity() {
        uploadingContainer.setVisibility(View.GONE);
        startActivityForResult(new Intent(GroupActivity.this, CreateActivity.class), 19);
    }

    public void showfirstpage() {
        viewPager.setCurrentItem(0);
    }

    public void refreshfeeds(boolean isfollowed) {
        ((GroupThreadsFragment) pagerAdapter.getRegisteredFragment(1)).refreshList1(isfollowed);
    }

    public void showsecondpage() {
        viewPager.setCurrentItem(1);
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private String[] tabs = getResources().getStringArray(R.array.group_tab);

        private PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {


            if (position == 0) {
                Bundle bundle = new Bundle();
                bundle.putString("groupname", groupname);
                bundle.putString("groupid", groupid);
                bundle.putString("members", groupmembers);
                bundle.putString("admin", groupadmin);
                bundle.putString("description", groupdescription);
                bundle.putString("rules", grouprules);
                bundle.putString("threads", groupthreads);
                bundle.putString("image", groupimage);
                bundle.putBoolean("follow", isfollowed);

                GroupInfoFragment fragment = new GroupInfoFragment();
                fragment.setArguments(bundle);

                return fragment;
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("groupid", groupid);
                bundle.putBoolean("follow", isfollowed);
                GroupThreadsFragment groupThreadsFragment = new GroupThreadsFragment();
                groupThreadsFragment.setArguments(bundle);

                return groupThreadsFragment;
            }
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

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

    }


}
