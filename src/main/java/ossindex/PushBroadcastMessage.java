package ossindex;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umeng.android.AndroidBroadcast;

import java.util.Date;

public class PushBroadcastMessage {

    private String appkey = "548fe31dfd98c542620004d2";
    private String masterSecret = "ixr9fjeipp7vjm4giya2njngtrn1irr2";
    private String timestamp = null;
    private String validationToken = null;

    private static Logger logger = LogManager.getLogger(PushBroadcastMessage.class.getName());

	public static void main(String[] args) {
        PushBroadcastMessage pushBroadcastMessage = new PushBroadcastMessage();
        pushBroadcastMessage.sendMessage("update");
    }

    public PushBroadcastMessage() {
        timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
        // Generate MD5 of appkey, masterSecret and timestamp as validation_token
        validationToken = DigestUtils.md5Hex(appkey.toLowerCase() + masterSecret.toLowerCase() + timestamp);
    }

    public void sendNotification(String title, String content) {

        AndroidBroadcast broadcast = new AndroidBroadcast();
        try {
            broadcast.setPredefinedKeyValue("appkey", this.appkey);
            broadcast.setPredefinedKeyValue("timestamp", this.timestamp);
            broadcast.setPredefinedKeyValue("validation_token", this.validationToken);
            broadcast.setPredefinedKeyValue("ticker", "丽图：套图更新了");
            broadcast.setPredefinedKeyValue("title",  title);
            broadcast.setPredefinedKeyValue("text",   content);
            broadcast.setPredefinedKeyValue("after_open", "go_activity");
            broadcast.setPredefinedKeyValue("activity", "cn.nit.beauty.ui.SplashActivity");
            broadcast.setPredefinedKeyValue("display_type", "notification");
            // For how to register a test device, please see the developer doc.
            broadcast.setPredefinedKeyValue("production_mode", "true");
            // Set customized fields
            broadcast.setExtraField("isDaily", "true");
            Date date = new Date();
            broadcast.setPredefinedKeyValue("description", date.toString());
            broadcast.send();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) {

        AndroidBroadcast broadcast = new AndroidBroadcast();
        try {
            broadcast.setPredefinedKeyValue("appkey", this.appkey);
            broadcast.setPredefinedKeyValue("timestamp", this.timestamp);
            broadcast.setPredefinedKeyValue("validation_token", this.validationToken);
            broadcast.setPredefinedKeyValue("custom",  message);
            broadcast.setPredefinedKeyValue("after_open", "go_activity");
            broadcast.setPredefinedKeyValue("activity", "cn.nit.beauty.ui.SplashActivity");
            broadcast.setPredefinedKeyValue("display_type", "message");
            // For how to register a test device, please see the developer doc.
            broadcast.setPredefinedKeyValue("production_mode", "true");
            Date date = new Date();
            broadcast.setPredefinedKeyValue("description", message + ":" + date.toString());
            broadcast.send();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
