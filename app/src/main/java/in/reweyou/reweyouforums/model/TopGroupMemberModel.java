package in.reweyou.reweyouforums.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.linkedin.android.spyglass.mentions.Mentionable;

/**
 * Created by master on 18/5/17.
 */

@SuppressLint("ParcelCreator")
public class TopGroupMemberModel implements Mentionable {
    private String imageurl;
    private String username;
    private String badge;
    private String points;
    private String uid;

    public String getPoints() {
        return points;
    }

    public String getBadge() {
        return badge;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public int getSuggestibleId() {
        return username.length();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        Log.d("sd", "getSuggestiblePrimaryText: " + username);
        return username;
    }

    @Override
    public String getMemberProfilePic() {
        return imageurl;
    }

    @Override
    public String getMemberUid() {
        return getUid();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return username;
            case PARTIAL:
            case NONE:
            default:
                return "";
        }
    }

    @Override
    public MentionDeleteStyle getDeleteStyle() {
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }
}
