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
public class UploadPhoto {

    private static String accessKeyId = "hauXgt6si5cgU39B";
    private static String accessKeySecret = "W8pEoUO4h2oIkeAAF1vHdvgdbJXvXp";
    private static String bucketName = "beauty-photo";
    private static String path, filterDir;
    // 初始化一个OSSClient
    private static OSSClient client = new OSSClient(accessKeyId, accessKeySecret);

    private static Logger logger = LogManager.getLogger(UploadPhoto.class.getName());

    public static void main(String[] args) throws IOException, UnirestException, JSONException {


        path = args[0];

        filterDir = args[1];

        //path = "c:\\photo";

        logger.info("path=" + path);

        logger.info("filterDir=" + filterDir);

        Start(new File(path));
        StartIndex startIndex = new StartIndex();

        startIndex.start(accessKeyId, accessKeySecret, bucketName);

        PushBroadcastMessage pushBroadcastMessage = new PushBroadcastMessage();
        pushBroadcastMessage.sendNotification("每日更新", "套图更新了,速来围观");
        pushBroadcastMessage.sendMessage("update");
    }

    private static void Start(File file) throws IOException {
        Random rd = new Random();

        File regions[] = file.listFiles();

        for (File region : regions) {
            logger.info("专区：" + region.getPath());

            if (filterDir.indexOf(region.getName())> -1 ) continue;
            File categories[] = region.listFiles();
            if (categories.length == 0) {
                logger.info("专区：" + region.getPath() + "没有图片");
                continue;
            }
            File category = categories[rd.nextInt(categories.length)];

            File folders[] = category.listFiles();

            int index = rd.nextInt(folders.length);
            StartEnum(folders[index]);
            FileUtils.forceDelete(folders[index]);

            folders = category.listFiles();

            if (folders.length == 0) {
                category.delete();
                logger.info(category.getAbsolutePath() + "--文件夹为空，已删除");
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

                String key = StringUtils.replace(f.getAbsolutePath(), path, "").toLowerCase();

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


}
