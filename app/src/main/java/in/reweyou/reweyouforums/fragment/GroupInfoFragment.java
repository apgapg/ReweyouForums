package in.reweyou.reweyouforums.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.reweyou.reweyouforums.EditActivity;
import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.GroupMembers;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupMemberModel;
import in.reweyou.reweyouforums.utils.Utils;

import static in.reweyou.reweyouforums.utils.Utils.convertpxFromDp;

/**
 * Created by master on 24/2/17.
 */

public class GroupInfoFragment extends Fragment {


    private static final String TAG = GroupInfoFragment.class.getName();
    private Activity mContext;
    private String groupid;
    private boolean isfollowed;
    private String groupdes = "";
    private String grouprules = "";
    private String adminuid = "";
    private ImageView shineeffect;
    private UserSessionManager userSessionManager;

    private RelativeLayout joincontainer;
    private TextView shortdes;
    private TextView tvRules;
    private ImageView img;
    private FlowLayout flowLayout;
    private ImageView dnd;
    private ImageView share;
    private Uri uri;
    private CardView cd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSessionManager = new UserSessionManager(mContext);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_group_info, container, false);

        TextView edit = (TextView) layout.findViewById(R.id.edit);
        cd = (CardView) layout.findViewById(R.id.cd);
        dnd = (ImageView) layout.findViewById(R.id.dnd);
        share = (ImageView) layout.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkStoragePermission();

                    } else
                        takeScreenshot(cd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (userSessionManager.getGroupsilentstatus(getArguments().getString("groupid")))
            dnd.setImageResource(R.drawable.ic_notifications_off_black_24px);
        else dnd.setImageResource(R.drawable.ic_notifications_none_black_24px);

        dnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!userSessionManager.getGroupsilentstatus(getArguments().getString("groupid"))) {
                    dnd.setImageResource(R.drawable.ic_notifications_off_black_24px);
                    userSessionManager.savegroupsilent(getArguments().getString("groupid"), true);
                    Toast.makeText(mContext, "Group notifications hide!", Toast.LENGTH_SHORT).show();
                } else {
                    dnd.setImageResource(R.drawable.ic_notifications_none_black_24px);
                    userSessionManager.savegroupsilent(getArguments().getString("groupid"), false);
                    Toast.makeText(mContext, "Group notifications shown!", Toast.LENGTH_SHORT).show();


                }

            }
        });
        final TextView btnfollow = (TextView) layout.findViewById(R.id.btn_follow);
        joincontainer = (RelativeLayout) layout.findViewById(R.id.joincontainer);
        img = (ImageView) layout.findViewById(R.id.image);
        final TextView groupname = (TextView) layout.findViewById(R.id.groupname);
        final TextView textrules = (TextView) layout.findViewById(R.id.textrules);
        shortdes = (TextView) layout.findViewById(R.id.shortdescription);
        tvRules = (TextView) layout.findViewById(R.id.description);
        final TextView members = (TextView) layout.findViewById(R.id.members);
        TextView threads = (TextView) layout.findViewById(R.id.threads);
        shineeffect = (ImageView) layout.findViewById(R.id.img_shine);
        final ProgressBar pd = (ProgressBar) layout.findViewById(R.id.pd);
        TextView membersmore = (TextView) layout.findViewById(R.id.membersmore);
        membersmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, GroupMembers.class);
                i.putExtra("groupid", groupid);
                i.putExtra("admin", adminuid);
                mContext.startActivity(i);
            }
        });

        try {
            groupname.setText(getArguments().getString("groupname"));
            members.setText(getArguments().getString("members"));
            groupid = getArguments().getString("groupid");
            isfollowed = getArguments().getBoolean("follow");
            shortdes.setText(getArguments().getString("description"));
            groupdes = getArguments().getString("description");
            grouprules = getArguments().getString("rules");
            tvRules.setText(getArguments().getString("rules"));
            adminuid = getArguments().getString("admin");
            threads.setText(getArguments().getString("threads"));
            if (getArguments().getString("rules").isEmpty()) {
                if (userSessionManager.getUID().equals(adminuid)) {

                    tvRules.setText("*Edit to update rules*");

                } else {
                    textrules.setVisibility(View.GONE);
                    tvRules.setVisibility(View.GONE);
                }
            } else tvRules.setText(grouprules);

            if (adminuid.equals(userSessionManager.getUID())) {
                joincontainer.setVisibility(View.GONE);
            } else if (isfollowed) {
                btnfollow.setText("Leave");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

            } else {
                btnfollow.setText("Join");
                btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
            }


            flowLayout = (FlowLayout) layout.findViewById(R.id.flowlayout);
            getMembersData();


            Glide.with(mContext).load(getArguments().getString("image")).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
            btnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnfollow.setVisibility(View.INVISIBLE);
                    pd.setVisibility(View.VISIBLE);

                    AndroidNetworking.post("https://www.reweyou.in/google/follow_groups.php")
                            .addBodyParameter("groupid", groupid)
                            .addBodyParameter("groupname", getArguments().getString("groupname"))
                            .addBodyParameter("uid", userSessionManager.getUID())
                            .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                            .setTag("uploadpost")
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsString(new StringRequestListener() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    if (response.equals("Followed")) {

                                        btnfollow.setText("Leave");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_border_pink));

                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        Toast.makeText(mContext, "You are now following '" + getArguments().getString("groupname") + "'", Toast.LENGTH_SHORT).show();
                                        mContext.setResult(Activity.RESULT_OK);
                                        isfollowed = true;
                                        ((GroupActivity) mContext).refreshfeeds(isfollowed);
                                        try {
                                            String paramgroupname = getArguments().getString("groupname");

                                            FirebaseMessaging.getInstance().subscribeToTopic(paramgroupname.replace(" ", "")
                                                    .replaceAll("[0-9]", "")
                                                    .toLowerCase());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((GroupActivity) mContext).showsecondpage();

                                            }
                                        }, 300);

                                    } else if (response.equals("Unfollowed")) {
                                        isfollowed = false;
                                        btnfollow.setText("Join");
                                        btnfollow.setTextColor(mContext.getResources().getColor(R.color.white));
                                        btnfollow.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_pink));
                                        btnfollow.setVisibility(View.VISIBLE);
                                        pd.setVisibility(View.GONE);
                                        mContext.setResult(Activity.RESULT_OK);
                                        // FirebaseMessaging.getInstance().unsubscribeFromTopic(getArguments().getString("groupname"));
                                        ((GroupActivity) mContext).refreshfeeds(isfollowed);

                                        try {
                                            String paramgroupname = getArguments().getString("groupname");
                                            FirebaseMessaging.getInstance().unsubscribeFromTopic(paramgroupname.replace(" ", "")
                                                    .replaceAll("[0-9]", "")
                                                    .toLowerCase());
                                            Log.d(TAG, "onResponse: string" + paramgroupname.replace(" ", "")
                                                    .replaceAll("[0-9]", "")
                                                    .toLowerCase());

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d(TAG, "onError: " + anError);
                                    Toast.makeText(mContext, "Connection problem", Toast.LENGTH_SHORT).show();
                                    btnfollow.setVisibility(View.VISIBLE);
                                    pd.setVisibility(View.GONE);
                                }
                            });

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!userSessionManager.getUID().equals(adminuid)) {
            edit.setVisibility(View.INVISIBLE);

        } else {
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, EditActivity.class);
                    i.putExtra("description", groupdes);
                    i.putExtra("image", getArguments().getString("image"));
                    i.putExtra("rules", grouprules);
                    i.putExtra("groupid", groupid);
                    mContext.startActivityForResult(i, Utils.REQ_CODE_EDIT_GROUP_ACTIVITY);
                }
            });
        }

        if (!isfollowed)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shineeffect.animate().translationXBy(convertpxFromDp(28 + 90 + 28)).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(750).start();
                }
            }, 700);
        return layout;
    }

    private void checkStoragePermission() {
        Dexter.withActivity(mContext)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        takeScreenshot(cd);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(mContext, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void getMembersData() {

        Log.d(TAG, "getMembersData: " + groupid);
        AndroidNetworking.post("https://www.reweyou.in/google/list_members.php")
                .addBodyParameter("groupid", groupid)
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "onResponse: members " + response.length());
                            List<GroupMemberModel> list = new ArrayList<>();
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                GroupMemberModel groupMemberModel = gson.fromJson(response.getJSONObject(i).toString(), GroupMemberModel.class);
                                list.add(groupMemberModel);
                            }

                            populatedata(list);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                    }
                });
    }

    private void populatedata(final List<GroupMemberModel> groupModels) {
        ((TextView) mContext.findViewById(R.id.membershead)).setText("MEMBERS (" + groupModels.size() + ")");
        for (int i = 0; i < groupModels.size(); i++) {
            View view = mContext.getLayoutInflater().inflate(R.layout.item_group_info_members, null);
            final ImageView image = (ImageView) view.findViewById(R.id.img);
            Glide.with(mContext).load(groupModels.get(i).getImageurl()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(image);

            flowLayout.addView(view);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            mContext = (Activity) context;
        else throw new IllegalArgumentException("Context should be an instance of Activity");
    }

    @Override
    public void onDestroy() {
        mContext = null;
        super.onDestroy();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isAdded()) {


        }
    }


    public void refreshDetails(String description, String rules, String image) {
        try {
            if (description != null)
                shortdes.setText(description);
            if (rules != null)
                tvRules.setText(rules);
            if (image != null)
                Glide.with(mContext).load(image).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shareIntent(String image, String groupname) {
        String tempimage = image;
        if (image == null)
            if (tempimage.isEmpty())
                tempimage = "";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Join '" + groupname + "' group in ReweyouForums app. Download now: https://play.google.com/store/apps/details?id=in.reweyou.reweyouforums");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        mContext.startActivity(Intent.createChooser(intent, "Share Group using"));
    }


    private void takeScreenshot(CardView cv) {
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
            uri = Uri.fromFile(imageFile);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 99;
            cv.setDrawingCacheEnabled(true);
            cv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            loadBitmapFromView(cv).compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            try {
                shareIntent(getArguments().getString("image"), getArguments().getString("groupname"));
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

        final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final Bitmap b2 = drawToBitmap(mContext, R.layout.share_reweyou_tag, metrics.widthPixels, metrics.heightPixels);

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
}
