package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13-7-11.
 */
public class DeleteThumb {



//        URL url = new URL("http://nit-photo.oss.aliyuncs.com/china/%E4%B8%9D%E6%83%85/2001-01-01%2011/13%20%284%29.jpg");
//        Thumbnails.of(url)
//                .scale(0.05f).toFile(new File("c:\\thumbnail.jpg"));

    public static void main(String[] args) throws IOException {
        String accessKeyId = "hauXgt6si5cgU39B";
        String accessKeySecret = "W8pEoUO4h2oIkeAAF1vHdvgdbJXvXp";
        String bucketName = "beauty-photo";
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
                if (!folder.contains("东京")) continue;;
                //if (!folder.contains("only")) continue;;

                listObjectsRequest.setPrefix(folder);
                listing = client.listObjects(listObjectsRequest);


                for (String listFolder : listing.getCommonPrefixes()) {
                    System.err.println(listFolder);


                        listObjectsRequest.setPrefix(listFolder);
                        listing = client.listObjects(listObjectsRequest);


                    for (OSSObjectSummary summary : listing.getObjectSummaries()) {
                        System.err.println(summary.getKey());


                        if (summary.getKey().endsWith("jpg")) {
                            client.deleteObject("nit-photo", summary.getKey());
                        }
                    }

                    for (String thumbFolder : listing.getCommonPrefixes()) {
                        System.err.println(thumbFolder);
//
//                        listObjectsRequest.setPrefix(thumbFolder);
//                        listing = client.listObjects(listObjectsRequest);
//
//                        for(OSSObjectSummary summary : listing.getObjectSummaries()) {
//                            if (summary.getKey().contains("cover_thumb")) {
//                                System.err.println(summary.getKey());
//                                client.deleteObject("nit-photo", summary.getKey());
//                            }
                        }
                    }
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
        System.out.println(result.getETag());
    }

}
