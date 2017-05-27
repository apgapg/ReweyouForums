package in.reweyou.reweyouforums.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import in.reweyou.reweyouforums.R;
import in.reweyou.reweyouforums.classes.UserSessionManager;
import in.reweyou.reweyouforums.model.GroupMemberModel;

/**
 * Created by master on 1/5/17.
 */

public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GroupMembersAdapter.class.getName();
    private final Context context;
    private final String groupid;
    private final UserSessionManager userSessionManager;
    private final boolean isadmin;
    List<GroupMemberModel> messagelist;

    public GroupMembersAdapter(Context context, String groupid, boolean isadmin) {
        this.context = context;
        this.groupid = groupid;
        this.isadmin = isadmin;
        this.messagelist = new ArrayList<>();
        userSessionManager = new UserSessionManager(context);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new YourGroupsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_info_members_activity, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        YourGroupsViewHolder forumViewHolder = (YourGroupsViewHolder) holder;
        ((YourGroupsViewHolder) holder).username.setText(messagelist.get(position).getUsername());
        Glide.with(context).load(messagelist.get(position).getImageurl()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(forumViewHolder.backgroundImage);
        if (isadmin) {
            ((YourGroupsViewHolder) holder).block.setVisibility(View.VISIBLE);
        } else ((YourGroupsViewHolder) holder).block.setVisibility(View.INVISIBLE);


    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    public void add(List<GroupMemberModel> list) {
        messagelist.clear();
        messagelist.addAll(list);
        notifyDataSetChanged();
    }

    private void editHeadline(final int adapterPosition) {
        //Creating a LayoutInflater object for the dialog box
        LayoutInflater li = LayoutInflater.from(context);
        //Creating a view to get the dialog box
        View confirmDialog = li.inflate(R.layout.dialog_ban, null);
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
                    buttonconfirm.setBackground(context.getResources().getDrawable(R.drawable.border_pink));
                    buttonconfirm.setTextColor(context.getResources().getColor(R.color.main_background_pink));
                } else {
                    buttonconfirm.setBackground(context.getResources().getDrawable(R.drawable.border_grey));
                    buttonconfirm.setTextColor(Color.parseColor("#9e9e9e"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

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

                    sendBanRequest(adapterPosition, link.getText().toString().trim());

                } else alertDialog.dismiss();
            }
        });

    }

    private void sendBanRequest(int adapterPosition, String trim) {
        Toast.makeText(context, "sending block request", Toast.LENGTH_SHORT).show();

        AndroidNetworking.post("https://www.reweyou.in/google/ban.php")
                .addBodyParameter("groupid", groupid)
                .addBodyParameter("banned_user", messagelist.get(adapterPosition).getUid())
                .addBodyParameter("banned_by", userSessionManager.getUID())
                .addBodyParameter("reason", trim)
                .addBodyParameter("authtoken", userSessionManager.getAuthToken())
                .setTag("uploadpost")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: " + response);
                        if (response.contains("Request sent"))
                            Toast.makeText(context, "request sent!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "block request failed!", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError: " + anError);
                        Toast.makeText(context, "block request failed!", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private class YourGroupsViewHolder extends RecyclerView.ViewHolder {
        private ImageView backgroundImage, block;
        private TextView username;


        public YourGroupsViewHolder(View inflate) {
            super(inflate);

            backgroundImage = (ImageView) inflate.findViewById(R.id.image);
            block = (ImageView) inflate.findViewById(R.id.block);

            username = (TextView) inflate.findViewById(R.id.message);

            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (messagelist.get(getAdapterPosition()).getUid().equals(userSessionManager.getUID())) {
                        Toast.makeText(context, "You can't block yourself", Toast.LENGTH_SHORT).show();
                    } else
                        editHeadline(getAdapterPosition());
                }
            });
        }
    }

}
