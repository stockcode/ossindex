package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.google.gson.Gson;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.BufferedImages;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13-7-11.
 */
public class Thumbnail {



//        URL url = new URL("http://nit-photo.oss.aliyuncs.com/china/%E4%B8%9D%E6%83%85/2001-01-01%2011/13%20%284%29.jpg");
//        Thumbnails.of(url)
//                .scale(0.05f).toFile(new File("c:\\thumbnail.jpg"));

    public static void main(String[] args) throws IOException {
        String accessKeyId = "tEPWqYKJGESwhRo5";
        String accessKeySecret = "oUkPZvE5HghfRbkX5wklu6qAiDnMrw";
        String bucketName = "nit-photo";
        // 初始化一个OSSClient
        OSSClient client = new OSSClient(accessKeyId, accessKeySecret);

        // 构造ListObjectsRequest请求
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);

        listObjectsRequest.setDelimiter("/");
        //listObjectsRequest.setPrefix("/");

        ObjectListing listing = client.listObjects(listObjectsRequest);


        List<String> rootFolders = listing.getCommonPrefixes();


        for (String rootFolder : rootFolders) {
            listObjectsRequest.setPrefix(rootFolder);
            listing = client.listObjects(listObjectsRequest);
            System.err.println(rootFolder + ":");
            List<String> urls = new ArrayList<String>();


            for (String folder : listing.getCommonPrefixes()) {

                listObjectsRequest.setPrefix(folder);
                listing = client.listObjects(listObjectsRequest);
                for (String listFolder : listing.getCommonPrefixes()) {
                    System.err.println(listFolder);


                        listObjectsRequest.setPrefix(listFolder);
                        listing = client.listObjects(listObjectsRequest);

                    for(String orgFolder : listing.getCommonPrefixes()) {
                        if (!orgFolder.contains("original")) continue;


                        listObjectsRequest.setPrefix(orgFolder);
                        listing = client.listObjects(listObjectsRequest);
                        List<String> picList = new ArrayList<String>();

                        for (OSSObjectSummary summary : listing.getObjectSummaries()) {
                            picList.add(summary.getKey());
                        }




                    for(String pic : picList) {
                        if (FilenameUtils.getExtension(pic).equals("")) continue;

                        String key = pic.replaceAll("original/", "thumb/");

                        try {
                            client.getObjectMetadata(bucketName, key);
                        } catch (OSSException e) {
                            URL url = new URL("http://nit-photo.oss.aliyuncs.com/" + pic);
                            ByteArrayOutputStream os = new ByteArrayOutputStream();

                            float scale = 0.5f;
                            if (pic.endsWith("cover.jpg")) scale = 0.2f;

                            Thumbnails.of(url).scale(scale).outputFormat("jpg").toOutputStream(os);
                            byte[] bytes = os.toByteArray();
                            InputStream is = new ByteArrayInputStream(bytes);
                            uploadIndex(bucketName, client, is, bytes.length, key);

                        }
                    }
                    }
                }
            }


        }

//        uploadIndex(bucketName, client, str);

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
        System.out.println(result.getETag());
    }

}
