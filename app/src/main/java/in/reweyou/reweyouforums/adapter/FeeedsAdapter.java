package in.reweyou.reweyouforums.adapter;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.linkedin.android.spyglass.ui.RichEditorViewInvert;

import org.chromium.customtabsclient.CustomTabsActivityHelper;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import in.reweyou.reweyouforums.CommentActivity;
import in.reweyou.reweyouforums.ForumMainActivity;
import in.reweyou.reweyouforums.FullImageActivity;
import in.reweyou.reweyouforums.GroupActivity;
import in.reweyou.reweyouforums.NotificationActivity;
import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.YoutubeActivity;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.customView.ColorTextView;
import in.reweyou.reweyouforums.customView.CustomDialogEditShare;
import in.reweyou.reweyouforums.model.ThreadModel;
import in.reweyou.reweyouforums.utils.Utils;
import io.supercharge.shimmerlayout.ShimmerLayout;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;

/**
 * Created by master on 1/5/17.
 */

public class FeeedsAdapter extends RecyclerView.Adapter<FeeedsAdapter.BaseViewHolder> {

    private static final String BUCKET = "cities-memory";

    private static final int VIEW_TYPE_IMAGE_1 = 21;
    private static final int VIEW_TYPE_IMAGE_2 = 22;
    private static final int VIEW_TYPE_IMAGE_3 = 23;
    private static final int VIEW_TYPE_IMAGE_4 = 24;
    private static final int VIEW_TYPE_TEXT = 25;
    private static final int VIEW_TYPE_LINK = 26;
    private static final int VIEW_TYPE_YOUTUBE_LINK = 27;
    private static final String TAG = FeeedsAdapter.class.getName();
    private static final int VIEW_TYPE_EMPTY = 28;

    private final Context mContext;
    private final UserSessionManager userSessionManager;
    private final CustomTabsIntent mCustomTabsIntent;
    private final FirebaseAnalytics mFirebaseAnalytics;
    private final Fragment fragmentContext;
    List<ThreadModel> messagelist;
    private String tempcommentid;

    public FeeedsAdapter(Context context, Fragment mainThreadsFragment) {
        this.mContext = context;
        this.messagelist = new ArrayList<>();

        ThreadModel threadModel = new ThreadModel();
        threadModel.setType("empty");
        threadModel.setGroupname("");
        messagelist.add(threadModel);

        userSessionManager = new UserSessionManager(mContext);
        mCustomTabsIntent = new CustomTabsIntent.Builder()

                .enableUrlBarHiding()
                .setToolbarColor(mContext.getResources().getColor(R.color.black))
                .setShowTitle(true)
                .build();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.fragmentContext = mainThreadsFragment;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE_1:
                return new Image1ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image1, parent, false));
            case VIEW_TYPE_IMAGE_2:
                return new Image2ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image2, parent, false));
            case VIEW_TYPE_IMAGE_3:
                return new Image3ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image3, parent, false));
            case VIEW_TYPE_IMAGE_4:
                return new Image4ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_image4, parent, false));
            case VIEW_TYPE_TEXT:
                return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_text, parent, false));
            case VIEW_TYPE_LINK:
                return new LinkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_link, parent, false));
            case VIEW_TYPE_YOUTUBE_LINK:
                return new YoutubeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_youtube_link, parent, false));
            case VIEW_TYPE_EMPTY:
                return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_empty, parent, false));

            default:
                return new TextViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feeds_text, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        if (!messagelist.get(position).getType().equals("empty")) {

            Log.d(TAG, "onBindViewHolder: callledddd");
            holder.nested.scrollTo(0, 0);
            holder.description.setText(messagelist.get(position).getDescription().trim());
            holder.likenumber.setText(messagelist.get(position).getUpvotes());
            holder.commentnum.setText(messagelist.get(position).getComments());
            holder.userlevel.setText(messagelist.get(position).getBadge());

            try {
                JSONObject jsonObject = new JSONObject(messagelist.get(position).getTags().replace("\\", ""));
                if (jsonObject.length() > 0) {
                    for (int i = 0; i < jsonObject.length(); i++) {

                        holder.description.findAndSetStrColor(jsonObject.getString("" + i), "#2962FF");
                    }

                }

            } catch (Exception e) {
            }
            holder.username.setText(messagelist.get(position).getUsername());
            Glide.with(fragmentContext).load(messagelist.get(position).getProfilepic()).diskCacheStrategy(DiskCacheStrategy.SOURCE).error(R.drawable.download).into(holder.profileimage);
            holder.date.setText(messagelist.get(position).getTimestamp().replace("about ", "").replace(" ago", ""));

            if (mContext instanceof ForumMainActivity || mContext instanceof GroupActivity) {
                holder.adapterComment.setThreadId(messagelist.get(position).getThreadid());
                holder.adapterComment.add(messagelist.get(position).getCommentlistshow());
            }


            if (messagelist.get(position).getStatus().equals("true")) {
                holder.likebuttn.setImageResource(R.drawable.ic_heart_like);
            } else if (messagelist.get(position).getStatus().equals("false")) {

                holder.likebuttn.setImageResource(R.drawable.ic_heart_like_grey);

            }

            Drawable background = holder.userlevel.getBackground();
            if (background instanceof GradientDrawable) {
                // cast to 'ShapeDrawable'
                GradientDrawable shapeDrawable = (GradientDrawable) background;
                if (messagelist.get(position).getBadge().equals("Noob")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_noob));
                } else if (messagelist.get(position).getBadge().equals("Follower")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_follower));
                } else if (messagelist.get(position).getBadge().equals("Pro")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_pro));
                } else if (messagelist.get(position).getBadge().equals("Rising Star")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_rising_star));
                } else if (messagelist.get(position).getBadge().equals("Star")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_star));
                } else if (messagelist.get(position).getBadge().equals("Expert")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_expert));
                } else if (messagelist.get(position).getBadge().equals("Leader")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_leader));
                } else if (messagelist.get(position).getBadge().equals("King")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_king));
                } else if (messagelist.get(position).getBadge().equals("Legend")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_legend));
                } else if (messagelist.get(position).getBadge().equals("Editor")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_editor));
                } else if (messagelist.get(position).getBadge().equals("Writer")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_writer));
                } else if (messagelist.get(position).getBadge().equals("GOAT")) {
                    shapeDrawable.setColor(ContextCompat.getColor(mContext, R.color.user_level_GOAT));

                }
            }
            switch (getItemViewType(position))

            {
                case VIEW_TYPE_IMAGE_1:
                    Image1ViewHolder image1ViewHolder = (Image1ViewHolder) holder;
                    onbindimage1(image1ViewHolder, position);
                    return;
                case VIEW_TYPE_IMAGE_2:
                    Image2ViewHolder image2ViewHolder = (Image2ViewHolder) holder;
                    onbindimage2(image2ViewHolder, position);
                    return;
                case VIEW_TYPE_IMAGE_3:
                    Image3ViewHolder image3ViewHolder = (Image3ViewHolder) holder;
                    onbindimage3(image3ViewHolder, position);
                    return;
                case VIEW_TYPE_IMAGE_4:
                    Image4ViewHolder image4ViewHolder = (Image4ViewHolder) holder;
                    onbindimage4(image4ViewHolder, position);
                    return;
                case VIEW_TYPE_TEXT:
                    return;
                case VIEW_TYPE_LINK:
                    LinkViewHolder linkViewHolder = (LinkViewHolder) holder;
                    onbindlink(linkViewHolder, position);
                    return;
                case VIEW_TYPE_YOUTUBE_LINK:
                    YoutubeViewHolder youtubeViewHolder = (YoutubeViewHolder) holder;
                    onbindyoutubelink(youtubeViewHolder, position);
                    return;

            }


       /* if (messagelist.get(position).getImage().isEmpty())
            forumViewHolder.image.setVisibility(View.GONE);
        else {
            forumViewHolder.image.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(messagelist.get(position).getImage()).into(forumViewHolder.image);
        }*/

        } else {

        }
    }

    private void onbindlink(final LinkViewHolder linkViewHolder, int position) {

        linkViewHolder.linkheadline.setText(messagelist.get(position).getLinkhead());


        Glide.with(mContext).load(messagelist.get(position).getLinkimage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(Utils.screenWidth - Utils.convertpxFromDp(8), Target.SIZE_ORIGINAL).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                linkViewHolder.linkimage.invalidate();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(linkViewHolder.linkimage);


    }

    private void onbindyoutubelink(YoutubeViewHolder youtubeViewHolder, int position) {

        youtubeViewHolder.linkheadline.setText(messagelist.get(position).getLinkhead());
        youtubeViewHolder.linkheadline.setSelected(true);
        Glide.with(mContext).load(messagelist.get(position).getLinkimage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(youtubeViewHolder.linkimage);


    }

    private void onbindimage1(final Image1ViewHolder image1ViewHolder, final int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).asBitmap().format(DecodeFormat.PREFER_ARGB_8888).diskCacheStrategy(DiskCacheStrategy.SOURCE).override(Utils.screenWidth - Utils.convertpxFromDp(8), Target.SIZE_ORIGINAL).into(image1ViewHolder.image1);

    }

    private void onbindimage2(Image2ViewHolder image2ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image2ViewHolder.image1);
        Glide.with(mContext).load(messagelist.get(position).getImage2()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image2ViewHolder.image2);


    }

    private void onbindimage3(Image3ViewHolder image3ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image3ViewHolder.image1);
        Glide.with(mContext).load(messagelist.get(position).getImage2()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image3ViewHolder.image2);
        Glide.with(mContext).load(messagelist.get(position).getImage3()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image3ViewHolder.image3);

    }

    private void onbindimage4(Image4ViewHolder image4ViewHolder, int position) {
        Glide.with(mContext).load(messagelist.get(position).getImage1()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image4ViewHolder.image1);
        Glide.with(mContext).load(messagelist.get(position).getImage2()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image4ViewHolder.image2);
        Glide.with(mContext).load(messagelist.get(position).getImage3()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image4ViewHolder.image3);
        Glide.with(mContext).load(messagelist.get(position).getImage4()).diskCacheStrategy(DiskCacheStrategy.SOURCE).skipMemoryCache(true).into(image4ViewHolder.image4);


    }

    private void onbindtext() {

    }


    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position, List<Object> payloads) {
        Log.d(TAG, "onBindViewHolder: called");
        if (payloads.contains("like")) {
            messagelist.get(position).setStatus("true");
            holder.like.setImageResource(R.drawable.ic_heart_like);
            holder.likebuttn.setImageResource(R.drawable.ic_heart_like);

            Log.d(TAG, "onBindViewHolder: wfejqfwjkefkjwfwebfkwebqkfweqjfw");

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.like.setScaleX((Float) animation.getAnimatedValue());
                    holder.like.setScaleY((Float) animation.getAnimatedValue());
                    holder.like.setAlpha(1.0f - ((Float) animation.getAnimatedValue()));
                }


            });

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    holder.like.setScaleX(0.0f);
                    holder.like.setScaleY(0.0f);
                    holder.like.setAlpha(1.0f);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.start();

            holder.likenumber.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getUpvotes()) + 1));
            messagelist.get(position).setUpvotes(String.valueOf(Integer.parseInt(messagelist.get(position).getUpvotes()) + 1));
        } else if (payloads.contains("unlike")) {
            messagelist.get(position).setStatus("false");
            holder.like.setImageResource(R.drawable.ic_heart_like_grey);
            holder.likebuttn.setImageResource(R.drawable.ic_heart_like_grey);


            holder.like.animate().setDuration(600).alpha(0.0f).scaleX(1).scaleY(1).withEndAction(new Runnable() {
                @Override
                public void run() {
                    holder.like.setScaleX(0.0f);
                    holder.like.setScaleY(0.0f);
                    holder.like.setAlpha(1.0f);
                }
            }).setInterpolator(new DecelerateInterpolator()).start();

            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimator.setDuration(500);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    holder.like.setScaleX((Float) animation.getAnimatedValue());
                    holder.like.setScaleY((Float) animation.getAnimatedValue());
                    holder.like.setAlpha(1.0f - ((Float) animation.getAnimatedValue()));
                }


            });

            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    holder.like.setScaleX(0.0f);
                    holder.like.setScaleY(0.0f);
                    holder.like.setAlpha(1.0f);
                }
            });
            valueAnimator.start();

            if (Integer.parseInt(messagelist.get(position).getUpvotes()) != 0) {
                holder.likenumber.setText(String.valueOf(Integer.parseInt(messagelist.get(position).getUpvotes()) - 1));
                messagelist.get(position).setUpvotes(String.valueOf(Integer.parseInt(messagelist.get(position).getUpvotes()) - 1));

            }
        } else super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemViewType(int position) {
        String viewty = messagelist.get(position).getType();
        switch (viewty) {
            case "image1":
                return VIEW_TYPE_IMAGE_1;
            case "image2":
                return VIEW_TYPE_IMAGE_2;
            case "image3":
                return VIEW_TYPE_IMAGE_3;
            case "image4":
                return VIEW_TYPE_IMAGE_4;
            case "text":
                return VIEW_TYPE_TEXT;
            case "link":
                return VIEW_TYPE_LINK;
            case "youtubelink":
                return VIEW_TYPE_YOUTUBE_LINK;
            case "empty":
                return VIEW_TYPE_EMPTY;
            default:
                return super.getItemViewType(position);

        }
    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<ThreadModel> list) {

        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();

    }

    private void sendrequestforlike(final int adapterPosition) {
        AndroidNetworking.post("https://www.reweyou.in/google/like.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("imageurl", userSessionManager.getProfilePicture())
                .addBodyParameter("username", userSessionManager.getUsername())
                .addBodyParameter("threadid", messagelist.get(adapterPosition).getThreadid())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("reportlike")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponsep: " + response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        notifyItemChanged(adapterPosition, "unlike");
                    }
                });
    }

    private void editdescription(final int adapterPosition) {
        //Creating a LayoutInflater object for the dialog box
        final LayoutInflater li = LayoutInflater.from(mContext);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_edit_description, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText edittextdesc = (EditText) confirmDialog.findViewById(R.id.edittext);
        edittextdesc.setText(messagelist.get(adapterPosition).getDescription());
        edittextdesc.setSelection(messagelist.get(adapterPosition).getDescription().length());
        edittextdesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    buttonconfirm.setBackground(mContext.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(mContext.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(mContext.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (edittextdesc.getText().toString().trim().length() > 0) {
                    InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edittextdesc.getWindowToken(), 0);
                    alertDialog.dismiss();
                    Toast.makeText(mContext, "updating post!", Toast.LENGTH_SHORT).show();

                    sendeditrequesttoserver(adapterPosition, edittextdesc.getText().toString().trim());

                } else alertDialog.dismiss();
            }
        });

    }

    private void sendeditrequesttoserver(int adapterPosition, String desc) {
        AndroidNetworking.post("https://www.reweyou.in/google/edit_thread.php")
                .addBodyParameter("uid", userSessionManager.getUID())
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .addBodyParameter("threadid", messagelist.get(adapterPosition).getThreadid())
                .addBodyParameter("description", desc)
                .setTag("reporst")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: edit: " + response);
                        if (response.contains("Edited")) {
                            Toast.makeText(mContext, "post updated!", Toast.LENGTH_SHORT).show();
                            if (mContext instanceof GroupActivity)
                                ((GroupActivity) mContext).refreshfeeds(true);
                            else if (mContext instanceof ForumMainActivity) {
                                Log.d(TAG, "onResponse: dkwmdkwkkkkk1111");
                                ((ForumMainActivity) mContext).refreshfeeds();
                            } else if (mContext instanceof CommentActivity) {
                                Log.d(TAG, "onResponse: dkwmdkwkkkkk1111");
                                ((CommentActivity) mContext).refreshthread();
                            }

                        } else

                            Toast.makeText(mContext, "something went wrong!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(TAG, "onError: " + anError);
                        Toast.makeText(mContext, "couldn't connect!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void shareIntent(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Download ReweyouForums app: https://play.google.com/store/apps/details?id=in.reweyou.reweyouforums");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/jpeg");
        mContext.startActivity(Intent.createChooser(intent, "Share Post using"));
    }

    private void takeScreenshot(CardView cv) {

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
            int quality = 99;
            cv.setDrawingCacheEnabled(true);
            cv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
            loadBitmapFromView(cv).compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


            MediaScannerConnection.scanFile(mContext.getApplicationContext(), new String[]{mPath}, new String[]{"image/jpeg"}, null);


            try {
                shareIntent(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    private Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight() - Utils.convertpxFromDp(4), Bitmap.Config.ARGB_4444);
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
        Bitmap resizedbitmap1 = Bitmap.createBitmap(cs, Utils.convertpxFromDp(6), Utils.convertpxFromDp(6), cs.getWidth() - Utils.convertpxFromDp(12), cs.getHeight() - Utils.convertpxFromDp(6));

        return resizedbitmap1;
    }

    private void checkStoragePermission(final CardView cv) {
        Dexter.withActivity((Activity) mContext)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        takeScreenshot(cv);
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


    public class BaseViewHolder extends RecyclerView.ViewHolder {
        private ShimmerLayout shimmerText;
        private NestedScrollView nested;
        private RelativeLayout commentcont;
        private TextView replyheader;
        private ProgressBar progressBar;
        private ImageView send;
        private RichEditorViewInvert editText;
        private CommentsAdapter adapterComment;
        private RecyclerView recyclerView;
        private CardView cv;
        private ImageView profileimage, liketemp, comment, like, share, likebuttn;
        private TextView username, likenum, commentnum, likenumber;
        private TextView date, userlevel;
        private ImageView edit;
        private TextView commenttxt;
        private ColorTextView description;
        private LinearLayout commentcontainer;

        public BaseViewHolder(View inflate) {
            super(inflate);

            shimmerText = (ShimmerLayout) inflate.findViewById(R.id.shimmer_text);

            profileimage = (ImageView) inflate.findViewById(R.id.profilepic);
            comment = (ImageView) inflate.findViewById(R.id.comment);
            share = (ImageView) inflate.findViewById(R.id.share);
            like = (ImageView) inflate.findViewById(R.id.like);
            likebuttn = (ImageView) inflate.findViewById(R.id.likebutton);
            edit = (ImageView) inflate.findViewById(R.id.edit);
            likenumber = (TextView) inflate.findViewById(R.id.numlikes);
            userlevel = (TextView) inflate.findViewById(R.id.userlevel);
            description = (ColorTextView) inflate.findViewById(R.id.description);
            cv = (CardView) inflate.findViewById(R.id.cv);
            commentnum = (TextView) inflate.findViewById(R.id.numcomments);
            nested = (NestedScrollView) inflate.findViewById(R.id.nested);

            commentcontainer = (LinearLayout) inflate.findViewById(R.id.commentcontainer);
            username = (TextView) inflate.findViewById(R.id.usernamee);
            date = (TextView) inflate.findViewById(R.id.date);
            commentcont = (RelativeLayout) inflate.findViewById(R.id.commmentcont);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Utils.screenWidth - Utils.convertpxFromDp(8), ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            cv.setLayoutParams(layoutParams);


            likebuttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!messagelist.get(getAdapterPosition()).getType().equals("empty")) {

                        if (messagelist.get(getAdapterPosition()).getStatus().equals("false")) {
                            FeeedsAdapter.this.notifyItemChanged(getAdapterPosition(), "like");
                        } else FeeedsAdapter.this.notifyItemChanged(getAdapterPosition(), "unlike");

                        sendrequestforlike(getAdapterPosition());

                    }
                }

            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!messagelist.get(getAdapterPosition()).getType().equals("empty")) {
                        CustomDialogEditShare customDialogEditShare = new CustomDialogEditShare();
                        customDialogEditShare.setPostByUser = messagelist.get(getAdapterPosition()).getUid().equals(userSessionManager.getUID());

                        customDialogEditShare.setonEditShareOptions(new CustomDialogEditShare.EditShareCallback() {
                            @Override
                            public void onEditPress() {
                                editdescription(getAdapterPosition());
                            }

                            @Override
                            public void onSharePress() {
                                takeScreenshot(cv);

                            }
                        });
                        customDialogEditShare.show(((Activity) mContext).getFragmentManager(), "");

                    }
                }
            });


            cv.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {

                }

                @Override
                public void onDoubleClick(View v) {
                    if (!messagelist.get(getAdapterPosition()).getType().equals("empty")) {


                        if (messagelist.get(getAdapterPosition()).getStatus().equals("false")) {
                            FeeedsAdapter.this.notifyItemChanged(getAdapterPosition(), "like");
                        } else FeeedsAdapter.this.notifyItemChanged(getAdapterPosition(), "unlike");

                        sendrequestforlike(getAdapterPosition());
                    }

                }
            });


            commentcont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!messagelist.get(getAdapterPosition()).getType().equals("empty")) {

                        if (mContext instanceof CommentActivity) {
                            ((CommentActivity) mContext).showCommentPage();
                        } else {
                            Intent i = new Intent(mContext, CommentActivity.class);
                            i.putExtra("threadid", messagelist.get(getAdapterPosition()).getThreadid());
                            if (mContext instanceof ForumMainActivity)
                                i.putExtra("from", "f");
                            else if (mContext instanceof GroupActivity)
                                i.putExtra("from", "g");
                            else if (mContext instanceof NotificationActivity)
                                i.putExtra("from", "nb");

                            if (mContext instanceof ForumMainActivity) {
                                ((ForumMainActivity) mContext).startActivityForResult(i, Utils.MAINACTIVITY_COMMENT);
                            } else mContext.startActivity(i);
                        }
                    }

                }
            });


            if (mContext instanceof ForumMainActivity || mContext instanceof GroupActivity) {

                recyclerView = (RecyclerView) inflate.findViewById(R.id.recycler_view);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                recyclerView.setLayoutManager(linearLayoutManager);

                adapterComment = new CommentsAdapter(mContext);

                recyclerView.setNestedScrollingEnabled(false);
                recyclerView.setAdapter(adapterComment);

            }


            /*share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkStoragePermission(cv);

                        } else
                            takeScreenshot(cv);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/


        }

    }

    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds
        long lastClickTime = 0;
        private Timer timer = null;  //at class level;
        private int DELAY = 320;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                processDoubleClickEvent(v);
            } else {
                processSingleClickEvent(v);
            }
            lastClickTime = clickTime;
        }


        public void processSingleClickEvent(final View v) {

            final Handler handler = new Handler();
            final Runnable mRunnable = new Runnable() {
                public void run() {
                    onSingleClick(v); //Do what ever u want on single click

                }
            };

            TimerTask timertask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(mRunnable);
                }
            };
            timer = new Timer();
            timer.schedule(timertask, DELAY);

        }


        public void processDoubleClickEvent(View v) {
            if (timer != null) {
                timer.cancel(); //Cancels Running Tasks or Waiting Tasks.
                timer.purge();  //Frees Memory by erasing cancelled Tasks.
            }
            onDoubleClick(v);//Do what ever u want on Double Click
        }

        public abstract void onSingleClick(View v);

        public abstract void onDoubleClick(View v);
    }

    private class Image1ViewHolder extends BaseViewHolder {
        private NestedScrollView nested;
        private CardView cv;
        private ImageView image1;

        public Image1ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);

            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage1());
                    mContext.startActivity(i);
                }
            });

        }
    }

    private class TextViewHolder extends BaseViewHolder {

        private CardView cv;

        public TextViewHolder(View inflate) {
            super(inflate);
            cv = (CardView) inflate.findViewById(R.id.cv);


        }
    }

    private class Image2ViewHolder extends BaseViewHolder {
        private ImageView image1, image2;

        public Image2ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            image2 = (ImageView) inflate.findViewById(R.id.image2);
            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage1());
                    mContext.startActivity(i);
                }
            });
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage2());
                    mContext.startActivity(i);
                }
            });
        }
    }

    private class Image3ViewHolder extends BaseViewHolder {
        private ImageView image1, image2, image3;

        public Image3ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            image2 = (ImageView) inflate.findViewById(R.id.image2);
            image3 = (ImageView) inflate.findViewById(R.id.image3);
            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage1());
                    mContext.startActivity(i);
                }
            });
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage2());
                    mContext.startActivity(i);
                }
            });
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage3());
                    mContext.startActivity(i);
                }
            });
        }
    }

    private class Image4ViewHolder extends BaseViewHolder {
        private ImageView image1, image2, image3, image4;

        public Image4ViewHolder(View inflate) {
            super(inflate);
            image1 = (ImageView) inflate.findViewById(R.id.image1);
            image2 = (ImageView) inflate.findViewById(R.id.image2);
            image3 = (ImageView) inflate.findViewById(R.id.image3);
            image4 = (ImageView) inflate.findViewById(R.id.image4);

            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage1());

                    mContext.startActivity(i);
                }
            });
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage2());
                    mContext.startActivity(i);
                }
            });
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage3());
                    mContext.startActivity(i);
                }
            });
            image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mContext, FullImageActivity.class);
                    i.putExtra("image", messagelist.get(getAdapterPosition()).getImage4());
                    mContext.startActivity(i);
                }
            });
        }
    }


    private class LinkViewHolder extends BaseViewHolder {
        private TextView openlink;
        private CardView cv;
        private TextView linkheadline, linkdescription, link;
        private RelativeLayout container;
        private ImageView linkimage;

        public LinkViewHolder(View inflate) {
            super(inflate);
            link = (TextView) inflate.findViewById(R.id.linklink);
            linkheadline = (TextView) inflate.findViewById(R.id.headlinelink);
            linkdescription = (TextView) inflate.findViewById(R.id.descriptionlink);

            linkimage = (ImageView) inflate.findViewById(R.id.imagelink);
            Typeface type = Typeface.createFromAsset(mContext.getAssets(), "Quicksand-Medium.ttf");
            linkheadline.setTypeface(type);

            openlink = (TextView) inflate.findViewById(R.id.openlink);
            openlink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagelist.get(getAdapterPosition()).getLink() != null) {

                        Log.d(TAG, "onClick: " + messagelist.get(getAdapterPosition()).getLink());
                        CustomTabsHelperFragment.open((Activity) mContext, mCustomTabsIntent, Uri.parse(messagelist.get(getAdapterPosition()).getLink()),
                                new CustomTabsActivityHelper.CustomTabsFallback() {
                                    @Override
                                    public void openUri(Activity activity, Uri uri) {
                                        Log.d(TAG, "openUri: here");
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(messagelist.get(getAdapterPosition()).getLink()));
                                        mContext.startActivity(browserIntent);
                                    }
                                });
                    }

                }
            });

        }
    }

    private class YoutubeViewHolder extends BaseViewHolder {
        private TextView linkheadline;
        private ImageView linkimage;

        public YoutubeViewHolder(View inflate) {
            super(inflate);
            linkheadline = (TextView) inflate.findViewById(R.id.headlinelink);
            linkimage = (ImageView) inflate.findViewById(R.id.imagelink);
            linkheadline.setSelected(true);

            linkimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(mContext, YoutubeActivity.class);
                        Log.d(TAG, "onClick: " + messagelist.get(getAdapterPosition()).getLink());
                        i.putExtra("url", messagelist.get(getAdapterPosition()).getLink());
                        mContext.startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
