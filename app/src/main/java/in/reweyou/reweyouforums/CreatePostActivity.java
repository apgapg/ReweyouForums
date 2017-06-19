package in.reweyou.reweyouforums;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.reweyou.reweyouforums.adapter.GalleryImagesAdapter;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupModel;
import in.reweyou.reweyouforums.utils.Utils;

public class CreatePostActivity extends AppCompatActivity {
    private static final String TAG = CreatePostActivity.class.getName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private ProgressBar linkpd;
    private CardView cd;
    private TextView headlinelink;
    private TextView descriptionlink;
    private RelativeLayout bottomAttachContainer;
    private RelativeLayout camerabtn;
    private RelativeLayout linkbtn;
    private TextView linklink;
    private TextView postbutton;
    private EditText edittextdescription;
    private ImagePicker imagePicker;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView addmore;
    private int counter = 1;
    private RelativeLayout addmorecont;
    private String link = "";
    private String type = "text";
    private UserSessionManager sessionManager;
    private LinearLayout uploadingContainer;
    private String image1url = "";
    private String image2url = "";
    private String image3url = "";
    private String image4url = "";
    private LinearLayout ll, l2;
    private String linkhead = "";
    private String linkdesc = "";
    private ImageView imageviewlink;
    private String linkimage = "";
    private FlowLayout flowLayout;
    private String groupid;
    private int temppos = -1;
    private String groupname;
    private ProgressDialog progressDialog;
    private String image1encoded = "";
    private String image2encoded = "";
    private String image3encoded = "";
    private String image4encoded = "";
    private Uri messageimageUri;
    private RecyclerView recyclerView;
    private GalleryImagesAdapter galleryImagesAdapter;
    private LinearLayout image1edit;
    private LinearLayout image2edit;
    private LinearLayout image3edit;
    private LinearLayout image4edit;
    private ImageView image1writeedit;
    private ImageView image2writeedit;
    private ImageView image3writeedit;
    private ImageView image4writeedit;
    private ImageView image1cropeedit;
    private ImageView image2cropeedit;
    private ImageView image3cropeedit;
    private ImageView image4cropeedit;
    private String fromimageview = "image1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_create);


        sessionManager = new UserSessionManager(this);


        if (!sessionManager.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        linkpd = (ProgressBar) findViewById(R.id.linkpd);
        cd = (CardView) findViewById(R.id.cd);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);

        image1writeedit = (ImageView) findViewById(R.id.image1writeedit);
        image2writeedit = (ImageView) findViewById(R.id.image2writeedit);
        image3writeedit = (ImageView) findViewById(R.id.image3writeedit);
        image4writeedit = (ImageView) findViewById(R.id.image4writeedit);


        image1cropeedit = (ImageView) findViewById(R.id.image1cropedit);
        image2cropeedit = (ImageView) findViewById(R.id.image2cropedit);
        image3cropeedit = (ImageView) findViewById(R.id.image3cropedit);
        image4cropeedit = (ImageView) findViewById(R.id.image4cropedit);


        image1edit = (LinearLayout) findViewById(R.id.image1edit);
        image2edit = (LinearLayout) findViewById(R.id.image2edit);
        image3edit = (LinearLayout) findViewById(R.id.image3edit);
        image4edit = (LinearLayout) findViewById(R.id.image4edit);


        imageviewlink = (ImageView) findViewById(R.id.imagelink);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();

            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();

            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();

            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();

            }
        });


        ll = (LinearLayout) findViewById(R.id.ll);
        l2 = (LinearLayout) findViewById(R.id.l2);

        edittextdescription = (EditText) findViewById(R.id.groupname);
        headlinelink = (TextView) findViewById(R.id.headlinelink);
        descriptionlink = (TextView) findViewById(R.id.descriptionlink);
        linklink = (TextView) findViewById(R.id.linklink);
        bottomAttachContainer = (RelativeLayout) findViewById(R.id.rl);
        linkbtn = (RelativeLayout) findViewById(R.id.link);
        camerabtn = (RelativeLayout) findViewById(R.id.camera);
        linkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editHeadline();
            }
        });
        postbutton = (TextView) findViewById(R.id.create);
        postbutton.setEnabled(false);
        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPost();
            }
        });
        initTextWatchers();
        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkStoragePermission();

                } else showPickImage();
            }
        });

        initPreviewGalleryImages();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkStoragePermissionforGallery();
            }
        } else recyclerView.setVisibility(View.GONE);

        try {
            if (getIntent().getBooleanExtra("frommain", false)) {

                flowLayout = (FlowLayout) findViewById(R.id.flowlayout);
                flowLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.selectgroup).setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 200);
                postbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (groupid != null && groupname != null)
                            compressImages();
                        else
                            Toast.makeText(CreatePostActivity.this, "Please select a group!", Toast.LENGTH_SHORT).show();

                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getIntent().getAction() != null)
            if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
                flowLayout = (FlowLayout) findViewById(R.id.flowlayout);
                flowLayout.removeAllViews();
                flowLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.selectgroup).setVisibility(View.VISIBLE);
                getData();
                postbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (groupid != null && groupname != null)
                            compressImages();
                        else
                            Toast.makeText(CreatePostActivity.this, "Please select a group!", Toast.LENGTH_SHORT).show();

                    }
                });

                Log.d(TAG, "init1: " + getIntent().getType());
                String message = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                messageimageUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                Log.d(TAG, "init2: " + messageimageUri);
                Log.d(TAG, "init3: " + message);

                if ("text/plain".equals(getIntent().getType())) {
                    if (!URLUtil.isValidUrl(message)) {
                        Toast.makeText(this, "Please check link", Toast.LENGTH_SHORT).show();
                    } else onLinkPasted(message);
                } else if (getIntent().getType().startsWith("image/")) {
                    if (message != null)
                        edittextdescription.setText(message);
                    try {
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkStoragePermissionforshare();

                        } else handleImage(messageimageUri.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }

                }


            }


    }

    private void checkStoragePermissionforGallery() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        try {
                            getAllShownImagesPath();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(CreatePostActivity.this, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void initPreviewGalleryImages() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        galleryImagesAdapter = new GalleryImagesAdapter(this);

        recyclerView.setAdapter(galleryImagesAdapter);
        try {
            getAllShownImagesPath();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showPickImage();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(CreatePostActivity.this, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void checkStoragePermissionforshare() {
        Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        handleImage(messageimageUri.toString());
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(CreatePostActivity.this, "Storage Permission denied by user", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onPermissionGranted: " + response.isPermanentlyDenied());

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }

    private void getData() {
        AndroidNetworking.post("https://www.reweyou.in/google/suggest_groups.php")
                .addBodyParameter("uid", sessionManager.getUID())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Gson gson = new Gson();
                            List<GroupModel> groupModels = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {

                                GroupModel groupModel = gson.fromJson(response.getJSONObject(i).toString(), GroupModel.class);
                                groupModels.add(groupModel);

                            }

                            Collections.sort(groupModels, new Comparator<GroupModel>() {
                                @Override
                                public int compare(GroupModel o1, GroupModel o2) {
                                    return o1.getGroupname().length() - o2.getGroupname().length();
                                }
                            });
                            populatedata(groupModels);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void populatedata(final List<GroupModel> groupModels) {
        final Context mContext = CreatePostActivity.this;
        Log.d(TAG, "populatedata: " + groupModels.size());
        for (int i = 0; i < groupModels.size(); i++) {
            View view = CreatePostActivity.this.getLayoutInflater().inflate(R.layout.item_interest, null);
            final TextView textView = (TextView) view.findViewById(R.id.groupname);
            textView.setText(groupModels.get(i).getGroupname());
            textView.setTag("0");
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag().equals("0")) {
                        v.setTag("1");
                        textView.setTextColor(mContext.getResources().getColor(R.color.white));
                        textView.setBackground(mContext.getResources().getDrawable(R.drawable.rectangular_solid_blue));
                        groupid = (groupModels.get(finalI).getGroupid());
                        groupname = (groupModels.get(finalI).getGroupname());

                        if (temppos != -1) {
                            ((TextView) flowLayout.getChildAt(temppos).findViewById(R.id.groupname)).setTextColor(mContext.getResources().getColor(R.color.bright_blue));
                            flowLayout.getChildAt(temppos).findViewById(R.id.groupname).setBackground(mContext.getResources().getDrawable(R.drawable.border_blue));
                            flowLayout.getChildAt(temppos).setTag("0");
                        }
                        temppos = finalI;

                    } else {
                        v.setTag("0");
                        textView.setTextColor(mContext.getResources().getColor(R.color.bright_blue));
                        textView.setBackground(mContext.getResources().getDrawable(R.drawable.border_blue));
                        groupid = null;
                    }
                }
            });
            flowLayout.addView(view);
        }
    }


    private void uploadPost() {


        String edittextdestext = "";
        if (edittextdescription.getText().toString().trim().length() != 0) {
            edittextdestext = edittextdescription.getText().toString();
        }
        Intent intent = new Intent();
        intent.putExtra("description", edittextdestext);

        intent.putExtra("link", link);
        intent.putExtra("linkhead", linkhead);
        intent.putExtra("linkdesc", linkdesc);
        intent.putExtra("counter", counter - 1);
        intent.putExtra("image1", image1url);
        intent.putExtra("image2", image2url);
        intent.putExtra("image3", image3url);
        intent.putExtra("image4", image4url);
        intent.putExtra("linkimage", linkimage);


        intent.putExtra("type", type);
        setResult(RESULT_OK, intent);
        finish();
            /*AndroidNetworking.post("https://www.reweyou.in/google/create_threads.php")
                    .addBodyParameter("groupname", "Photography")
                    .addBodyParameter("description", edittextdescription.getText().toString())
                    .addBodyParameter("link", link)
                    .addBodyParameter("type", type)
                    .addBodyParameter("uid", sessionManager.getUID())
                    .addBodyParameter("authtoken", sessionManager.getAuthToken())
                    .setTag("uploadpost")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "onResponse: " + response);
                            if (response.equals("Thread created")) {
                                Toast.makeText(CreatePostActivity.this, "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(CreatePostActivity.this, "Couldn't post. connectivity error", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d(TAG, "onError: " + anError);
                            Toast.makeText(CreatePostActivity.this, "Couldn't post. connectivity error", Toast.LENGTH_SHORT).show();

                        }
                    });*/


    }

    private void uploadPostShare() {
        String edittextdestext = "";
        if (edittextdescription.getText().toString().trim().length() != 0) {
            edittextdestext = edittextdescription.getText().toString();
        }
        AndroidNetworking.post("https://www.reweyou.in/google/create_threads.php")
                .addBodyParameter("groupname", groupname)
                .addBodyParameter("groupid", groupid)
                .addBodyParameter("description", edittextdestext)
                .addBodyParameter("link", link)
                .addBodyParameter("linkdesc", linkdesc)
                .addBodyParameter("linkhead", linkhead)
                .addBodyParameter("linkimage", linkimage)
                .addBodyParameter("image1", image1encoded)
                .addBodyParameter("image2", image2encoded)
                .addBodyParameter("image3", image3encoded)
                .addBodyParameter("image4", image4encoded)
                .addBodyParameter("type", type)
                .addBodyParameter("uid", sessionManager.getUID())
                .addBodyParameter("name", sessionManager.getUsername())
                .addBodyParameter("profilepic", sessionManager.getProfilePicture())
                .addBodyParameter("authtoken", sessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.contains("Thread created")) {
                            progressDialog.dismiss();
                            Toast.makeText(CreatePostActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                            if (!getIntent().getBooleanExtra("frommain", false)) {
                                Intent i = new Intent(CreatePostActivity.this, ForumMainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(i);
                            } else setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(CreatePostActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        Toast.makeText(CreatePostActivity.this, "Upload failed!", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                });


    }

    private void showUploading() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Post");
        progressDialog.setMessage("Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    private void initTextWatchers() {

        edittextdescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edittextdescription.getText().toString().trim().length() > 0 || !type.equals("text")) {
                    updateCreateTextUI(true);
                } else updateCreateTextUI(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateCreateTextUI(boolean b) {
        if (b) {
            postbutton.setEnabled(true);
            postbutton.setTextColor(this.getResources().getColor(R.color.main_background_pink));
            postbutton.setBackground(this.getResources().getDrawable(R.drawable.border_pink));
        } else {
            postbutton.setEnabled(false);
            postbutton.setTextColor(this.getResources().getColor(R.color.grey_create));
            postbutton.setBackground(this.getResources().getDrawable(R.drawable.border_grey));
        }
    }

    private void editHeadline() {
        //Creating a LayoutInflater object for the dialog box
        final LayoutInflater li = LayoutInflater.from(this);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_insert_link, null);
        //  number=session.getMobileNumber();
        //Initizliaing confirm button fo dialog box and edittext of dialog box
        final Button buttonconfirm = (Button) confirmDialog.findViewById(R.id.buttonConfirm);
        final EditText link = (EditText) confirmDialog.findViewById(R.id.editTextlink);
        link.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    buttonconfirm.setBackground(CreatePostActivity.this.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(CreatePostActivity.this.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(CreatePostActivity.this.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(confirmDialog);

        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        alertDialog.show();

        //On the click of the confirm button from alert dialog
        buttonconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (link.getText().toString().trim().length() > 0) {
                    alertDialog.dismiss();
                    if (URLUtil.isValidUrl(link.getText().toString()))
                        CreatePostActivity.this.onLinkPasted(link.getText().toString());
                    else
                        Toast.makeText(CreatePostActivity.this, "Please check your link", Toast.LENGTH_SHORT).show();


                } else alertDialog.dismiss();
            }
        });

    }

    private void onLinkPasted(String s) {
        cd.setVisibility(View.VISIBLE);
        bottomAttachContainer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                linkpd.setVisibility(View.VISIBLE);

            }
        });


        AndroidNetworking.post("https://damp-beyond-15607.herokuapp.com/previewlinknew.php")
                .addBodyParameter("url", s)
                .setTag("agr")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);

                        try {

                            linkpd.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = new JSONObject(response);
                            String reallink = jsonObject.getString("reallink");
                            String link = jsonObject.getString("link");
                            String imagelink = jsonObject.getString("image");
                            Log.d(TAG, "onResponse: " + response + "   " + link);
                            String title = jsonObject.getString("title");
                            String description = jsonObject.getString("description");
                            String parser = jsonObject.getString("parser");
                            String videoid = jsonObject.getString("videoid");


                            if (title != null) {
                                linkhead = title;
                                headlinelink.setText(title);
                            }
                            if (description != null) {
                                linkdesc = description;
                                descriptionlink.setText(description);
                            }
                            if (link != null)
                                linklink.setText(reallink);
                            if (imagelink != null) {
                                linkimage = imagelink.replace("\n", "");
                                Glide.with(CreatePostActivity.this).load(linkimage).into(imageviewlink);
                            }
                            edittextdescription.setHint("Describe this link...");
                            if (parser.contains("youtube"))
                                CreatePostActivity.this.link = videoid;
                            else
                                CreatePostActivity.this.link = link;

                            if (parser.contains("youtube"))
                                type = "youtubelink";
                            else
                                type = "link";
                        } catch (Exception e) {
                            bottomAttachContainer.setVisibility(View.VISIBLE);
                            edittextdescription.setHint("Describe this link...");
                            cd.setVisibility(View.GONE);
                            Toast.makeText(CreatePostActivity.this, "Error in fetching data from link", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        cd.setVisibility(View.GONE);
                        bottomAttachContainer.setVisibility(View.VISIBLE);


                        Toast.makeText(CreatePostActivity.this, "Error in fetching data from link", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void showPickImage() {
        imagePicker = new ImagePicker(this);
        imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                               @Override
                                               public void onImagesChosen(List<ChosenImage> images) {
                                                   if (images != null)
                                                       onImageChoosenbyUser(images);
                                                   else
                                                       Log.e(TAG, "onError: onimagechoosen: ");

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
                            // startImageCropActivity(Uri.parse(images.get(0).getQueryUri()));
                            handleImage(images.get(0).getQueryUri());
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
                .setMinCropResultSize(600, 600)
                .setBorderCornerColor(getResources().getColor(R.color.colorPrimaryDark))
                .setBorderLineColor(getResources().getColor(R.color.colorPrimary))
                .setGuidelinesColor(getResources().getColor(R.color.divider))
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(100)
                .start(this);
        messageimageUri = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("reached", "activigty: " + data);
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                updateimage(fromimageview, result.getUri().toString());

               /* File compressedImage = new Compressor.Builder(this)
                        .setMaxWidth(1000)
                        .setMaxHeight(1000)
                        .setQuality(90)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .build()
                        .compressToFile(result.getUri());*/
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(new ImagePickerCallback() {
                                                           @Override
                                                           public void onImagesChosen(List<ChosenImage> images) {
                                                               if (images != null)
                                                                   onImageChoosenbyUser(images);
                                                               else
                                                                   Log.e(TAG, "onError: onimagechoosen: ");

                                                           }

                                                           @Override
                                                           public void onError(String message) {
                                                               // Do error handling
                                                               Log.e(TAG, "onError: " + message);
                                                           }
                                                       }

                    );
                }
                imagePicker.submit(data);
            } else if (requestCode == Utils.REQ_CODE_EDIT_IMAGE) {
                Log.d(TAG, "onActivityResult: fejfnejnfe called: " + data.getStringExtra("from"));
                Log.d(TAG, "onActivityResult: intent: " + data.getStringExtra("uri"));
                updateimage(data.getStringExtra("from"), data.getStringExtra("uri"));
            }
        }

    }

    private void updateimage(String from, String uri) {
        if (from.equals("image1")) {
            updateimageview1(uri);
        } else if (from.equals("image2")) {
            updateimageview2(uri);
        } else if (from.equals("image3")) {
            updateimageview3(uri);

        } else if (from.equals("image4")) {
            updateimageview4(uri);

        }
    }

    private void updateimageview1(final String uri) {
        image1url = uri;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Glide.with(CreatePostActivity.this).load(uri).into(image1);

            }
        });
    }

    private void updateimageview2(final String uri) {
        image2url = uri;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Glide.with(CreatePostActivity.this).load(uri).into(image2);

            }
        });
    }

    private void updateimageview3(final String uri) {
        image3url = uri;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Glide.with(CreatePostActivity.this).load(uri).into(image3);

            }
        });
    }

    private void updateimageview4(final String uri) {
        image4url = uri;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Glide.with(CreatePostActivity.this).load(uri).into(image4);

            }
        });
    }

    private void handleImage(final String s) {

        if (counter == 1) {
            recyclerView.setVisibility(View.GONE);
            bottomAttachContainer.setVisibility(View.GONE);
            image1url = s;
            updateCreateTextUI(true);
            ll.setVisibility(View.VISIBLE);
            image1edit.setVisibility(View.VISIBLE);
            image1.setOnClickListener(null);

            image1writeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreatePostActivity.this, EditImageActivity.class);
                    i.putExtra("uri", image1url);
                    i.putExtra("from", "image1");
                    startActivityForResult(i, Utils.REQ_CODE_EDIT_IMAGE);
                }
            });
            image1cropeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromimageview = "image1";
                    startImageCropActivity(Uri.parse(image1url));
                }
            });
            type = "image1";

            edittextdescription.setHint("Describe about this image...");
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(CreatePostActivity.this).load(s).into(image1);

                }
            });
            counter++;
        } else if (counter == 2) {
            type = "image2";
            edittextdescription.setHint("Describe about these images...");
            image2edit.setVisibility(View.VISIBLE);

            image2url = s;
            image2.setOnClickListener(null);
            image2writeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreatePostActivity.this, EditImageActivity.class);
                    i.putExtra("uri", image2url);
                    i.putExtra("from", "image2");
                    startActivityForResult(i, Utils.REQ_CODE_EDIT_IMAGE);
                }
            });
            image2cropeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromimageview = "image2";
                    startImageCropActivity(Uri.parse(image2url));
                }
            });
            l2.setVisibility(View.VISIBLE);
            image4.setVisibility(View.INVISIBLE);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(CreatePostActivity.this).load(s).into(image2);
                }
            });
            counter++;

        } else if (counter == 3) {
            type = "image3";
            image3edit.setVisibility(View.VISIBLE);
            image3.setOnClickListener(null);

            image3url = s;
            image3writeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreatePostActivity.this, EditImageActivity.class);
                    i.putExtra("uri", image3url);
                    i.putExtra("from", "image3");
                    startActivityForResult(i, Utils.REQ_CODE_EDIT_IMAGE);
                }
            });
            image3cropeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromimageview = "image3";
                    startImageCropActivity(Uri.parse(image3url));
                }
            });
            image4.setVisibility(View.VISIBLE);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(CreatePostActivity.this).load(s).into(image3);
                }
            });
            counter++;

        } else if (counter == 4) {
            image4url = s;
            type = "image4";
            image4.setOnClickListener(null);

            image4writeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CreatePostActivity.this, EditImageActivity.class);
                    i.putExtra("uri", image4url);
                    i.putExtra("from", "image4");
                    startActivityForResult(i, Utils.REQ_CODE_EDIT_IMAGE);
                }
            });
            image4cropeedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromimageview = "image4";
                    startImageCropActivity(Uri.parse(image4url));
                }
            });
            image4edit.setVisibility(View.VISIBLE);


            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Glide.with(CreatePostActivity.this).load(s).into(image4);
                }
            });
            counter++;


        }


      /*  Glide.with(this).load(s).asBitmap().toBytes().into(new SimpleTarget<byte[]>(150, 150) {
            @Override
            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                String encodedImage = Base64.encodeToString(resource, Base64.DEFAULT);
               // uploadImage(encodedImage);
            }
        });*/

    }

    private void compressImages() {
        showUploading();
        final int count = counter - 1;
        if (count > 0) {
            Glide.with(this).load(image1url).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                @Override
                public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                    image1encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                    if (count > 1)
                        Glide.with(CreatePostActivity.this).load(image2url).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                            @Override
                            public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                image2encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                if (count > 2)
                                    Glide.with(CreatePostActivity.this).load(image3url).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                                        @Override
                                        public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                            image3encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                            if (count > 3)
                                                Glide.with(CreatePostActivity.this).load(image4url).asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 90).atMost().override(1200, 1200).into(new SimpleTarget<byte[]>() {
                                                    @Override
                                                    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
                                                        image4encoded = Base64.encodeToString(resource, Base64.DEFAULT);
                                                        uploadPostShare();
                                                    }
                                                });
                                            else uploadPostShare();

                                        }
                                    });
                                else uploadPostShare();
                            }
                        });
                    else uploadPostShare();
                }
            });
        } else uploadPostShare();


    }

    public void getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";


        String[] projection = {MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns._ID};

        cursor = getContentResolver().query(uri, projection, null,
                null, orderBy);

        int tempcount;

        if (cursor.getCount() < 20) {
            tempcount = cursor.getCount();
        } else {
            tempcount = 20;
        }

        if (tempcount != 0) {

            for (int i = 0; i < tempcount; i++) {
                cursor.moveToNext();
                Uri imageUri =
                        ContentUris
                                .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                Log.d(TAG, "getAllShownImagesPath: " + imageUri.toString());

                listOfAllImages.add(imageUri.toString());
            }
            cursor.close();

            galleryImagesAdapter.add(listOfAllImages);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void onimageselected(String s) {
        handleImage(s);
    }
}
