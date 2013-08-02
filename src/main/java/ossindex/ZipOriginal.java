package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObject;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13-7-11.
 */
public class ZipOriginal {



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
            if (rootFolder.contains("asia")) continue;

            listObjectsRequest.setPrefix(rootFolder);
            listing = client.listObjects(listObjectsRequest);
            System.err.println(rootFolder + ":");
            List<String> urls = new ArrayList<String>();


            for (String folder : listing.getCommonPrefixes()) {

                listObjectsRequest.setPrefix(folder);
                listing = client.listObjects(listObjectsRequest);
                for (String listFolder : listing.getCommonPrefixes()) {
                    System.err.println(listFolder);

                    String dstFolder = listFolder;


                        listObjectsRequest.setPrefix(listFolder + "original/");
                        listing = client.listObjects(listObjectsRequest);


                        for (OSSObjectSummary summary : listing.getObjectSummaries()) {

                            OSSObject ossObject = client.getObject("nit-photo", summary.getKey());
                            File file = new File("c:\\test\\" + summary.getETag());

                            FileUtils.writeByteArrayToFile(file, IOUtils.toByteArray(ossObject.getObjectContent()));
                        }

                    ZipCompressorByAnt zipCompressorByAnt = new ZipCompressorByAnt("c:\\test\\original.zip");
                    zipCompressorByAnt.compress("c:\\test\\");

                    File zipFile = new File("c:\\test\\original.zip");

                    uploadIndex(bucketName, client, zipFile, listFolder + "original.zip");

                    FileUtils.cleanDirectory(new File("c:\\test"));

                }
            }


        }

//        uploadIndex(bucketName, client, str);

    }

    private static void uploadIndex(String bucketName, OSSClient client, File zipFile, String key) throws IOException {
        client.deleteObject(bucketName, key);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(zipFile.length());

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, FileUtils.openInputStream(zipFile), meta);

        // 打印ETag
        System.out.println(result.getETag());
    }

}
