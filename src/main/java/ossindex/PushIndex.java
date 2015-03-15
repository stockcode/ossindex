package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * Created by Administrator on 13-7-11.
 */
public class PushIndex {

    private static String accessKeyId = "hauXgt6si5cgU39B";
    private static String accessKeySecret = "W8pEoUO4h2oIkeAAF1vHdvgdbJXvXp";
    private static String bucketName = "beauty-photo";

    private static Logger logger = LogManager.getLogger(PushIndex.class.getName());

    public static void main(String[] args) throws IOException, UnirestException, JSONException {


        StartIndex startIndex = new StartIndex();

        startIndex.start(accessKeyId, accessKeySecret, bucketName);

        PushBroadcastMessage pushBroadcastMessage = new PushBroadcastMessage();
        pushBroadcastMessage.sendNotification("每日更新", "套图更新了,速来围观");
        pushBroadcastMessage.sendMessage("update");
    }
}
