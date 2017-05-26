package in.reweyou.reweyouforums.model;

/**
 * Created by master on 24/2/17.
 */

public class ReplyCommentModel {

    private String username;
    private String reply;
    private String imageurl;
    private String timestamp;
    private String badge;
    private String points;
    private String replyid;
    private String uid;

    public String getUid() {
        return uid;
    }

    public String getPoints() {
        return points;
    }

    public String getBadge() {
        return badge;
    }

    public String getReplyid() {
        return replyid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public String getUsername() {
        return username;
    }

    public String getReply() {
        return reply;
    }
}
