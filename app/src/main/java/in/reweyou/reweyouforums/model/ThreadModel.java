package in.reweyou.reweyouforums.model;

import java.util.List;

/**
 * Created by master on 9/5/17.
 */

public class ThreadModel {
    private String threadid;
    private String groupname;
    private String link;
    private String youtubelink;
    private String uid;
    private String username;
    private String timestamp;
    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String imageurl;
    private String description;
    private String likenumber;
    private String type;
    private String upvotes;
    private String badge;
    private String points;
    private String comments;
    private String liketype = "";
    private String linkimage = "";
    private String linkdesc = "";
    private String linkhead = "";
    private String status = "false";
    private String tags = "";
    private List<Object> commentlistshow;



    public String getTags() {
        return tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLinkimage() {
        return linkimage;
    }

    public String getLinkdesc() {
        return linkdesc;
    }

    public String getLinkhead() {
        return linkhead;
    }

    public String getProfilepic() {
        return imageurl;
    }

    public String getBadge() {
        return badge;
    }

    public String getPoints() {
        return points;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getUsername() {
        return username;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }

    public String getLink() {
        return link;
    }

    public String getThreadid() {
        return threadid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(String upvotes) {
        this.upvotes = upvotes;
    }

    public String getYoutubelink() {
        return youtubelink;
    }

    public String getLiketype() {
        if (liketype != null)
            return liketype;
        else return "";
    }

    public void setLiketype(String liketype) {
        this.liketype = liketype;
    }

    public void setcommentlistshow(List<Object> commentlistshow) {
        this.commentlistshow = commentlistshow;
    }

    public List<Object> getCommentlistshow() {
        return commentlistshow;
    }
}
