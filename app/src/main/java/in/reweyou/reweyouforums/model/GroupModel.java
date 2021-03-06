package in.reweyou.reweyouforums.model;

/**
 * Created by master on 8/5/17.
 */

public class GroupModel {
    private String groupname;
    private String description;
    private String image;
    private String threads;
    private String members;
    private String groupid;
    private String admin;
    private String adminname;
    private String rules;
    private String tempselect = "deselect";

    public String getTempselect() {
        return tempselect;
    }

    public void setTempselect(String tempselect) {
        this.tempselect = tempselect;
    }

    public String getAdminname() {
        return adminname;
    }

    public String getAdmin() {
        return admin;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getThreads() {
        return threads;
    }

    public String getRules() {
        return rules;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getGroupname() {
        return groupname;
    }

    public String getMembers() {
        return members;
    }

    public String getGroupId() {
        return groupid;
    }
}
