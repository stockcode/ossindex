package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.*;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Administrator on 13-7-11.
 */
public class UploadPhoto {

    private static String accessKeyId = "hauXgt6si5cgU39B";
    private static String accessKeySecret = "W8pEoUO4h2oIkeAAF1vHdvgdbJXvXp";
    private static String bucketName = "beauty-photo";
    private static String path;
    // 初始化一个OSSClient
    private static OSSClient client = new OSSClient(accessKeyId, accessKeySecret);

    private static Logger logger = LogManager.getLogger(UploadPhoto.class.getName());

    public static void main(String[] args) throws IOException, UnirestException, JSONException {
        path = args[0];

        //path = "c:\\photo";

        logger.info("path=" + path);

        Start(new File(path));
        StartIndex startIndex = new StartIndex();

        startIndex.start(accessKeyId, accessKeySecret, bucketName);

        PushBroadcastMessage pushBroadcastMessage = new PushBroadcastMessage();
        pushBroadcastMessage.send("每日更新", "套图更新了,速来围观");
}

    private static void Start(File file) throws IOException {
        File regions[] = file.listFiles();

        for(File region : regions) {
            File categories[] = region.listFiles();

            for(File category : categories) {
                File folders[] = category.listFiles();
                if (folders != null && folders.length > 0) {
                    StartEnum(folders[0]);
                    FileUtils.forceDelete(folders[0]);
                    break;
                }
            }


        }

    }

    private static void StartEnum(File file) throws IOException {
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return;
        }
        for (File f : flist) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
                logger.info("Dir==>" + f.getAbsolutePath());

                StartEnum(f);
            } else {
                //这里将列出所有的文件
                logger.info("file==>" + f.getAbsolutePath());


                byte[] bytes = FileUtils.readFileToByteArray(f);
                InputStream is = new ByteArrayInputStream(bytes);

                String key = StringUtils.replace(f.getParent(), path, "").toLowerCase() + "/" + generateShortUuid() + ".jpg";

                key = StringUtils.replace(key, "\\", "/");

                key = StringUtils.removeStart(key, "/");

                uploadIndex(bucketName, client, is, bytes.length, key);
            }
        }
    }

    private static void uploadIndex(String bucketName, OSSClient client, InputStream is, int length, String key) {
        client.deleteObject(bucketName, key);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(length);

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, is, meta);

        // 打印ETag
        logger.info(key + ":" + result.getETag());
    }

    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }
}
