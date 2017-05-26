package in.reweyou.reweyouforums.model;

/**
 * Created by master on 24/2/17.
 */

public class CommentModel {

    private String username;
    private String comment;
    private String commentid;
    private String imageurl;
    private String timestamp;
    private String badge;
    private String points;
    private String uid;

    public String getUid() {
        return uid;
    }

    public String getBadge() {
        return badge;
    }

    public String getPoints() {
        return points;
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

    public String getComment() {
        return comment;
    }

    public String getCommentid() {
        return commentid;
    }
}
