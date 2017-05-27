package in.reweyou.reweyouforums.model;

/**
 * Created by master on 24/2/17.
 */

public class NotiModel {

    private String notifier_image;
    private String timestamp;
    private String notifier;
    private String nid;
    private String noti_type;
    private String readstatus;

    public String getReadstatus() {
        return readstatus;
    }

    public String getNoti_type() {
        return noti_type;
    }

    public String getNid() {
        return nid;
    }

    public String getNotifier() {
        return notifier;
    }

    public String getNotifier_image() {
        return notifier_image;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
