package ossindex;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.google.gson.Gson;

public class StartIndex {

    public static void main(String[] args) {
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

        Gson gson = new Gson();

        Index index = new Index();

        index.categories = new ArrayList<Category>();



        for (String rootFolder : rootFolders) {
            listObjectsRequest.setPrefix(rootFolder);
            listing = client.listObjects(listObjectsRequest);
            System.err.println(rootFolder + ":");
            List<String> urls = new ArrayList<String>();


            for (String folder : listing.getCommonPrefixes()) {
                String[] strs = folder.split("/");
                if (strs.length > 1) {
                    Category category = new Category();
                    category.setURL(folder);
                    category.setCATEGORY(strs[0]);
                    category.setTITLE(strs[1]);
                    category.setCATEGORY_ICON(1);
                    category.setICON(2);
                    index.categories.add(category);
                }

                listObjectsRequest.setPrefix(folder);
                listing = client.listObjects(listObjectsRequest);
                for (String listFolder : listing.getCommonPrefixes()) {
                    System.err.println(listFolder);

                    strs = listFolder.split("/");
                    if (strs.length > 1) {
                        urls.add(listFolder);
                        Category category = new Category();
                        category.setURL(listFolder);
                        category.setCATEGORY(folder);
                        category.setTITLE(strs[2]);
                        category.setCATEGORY_ICON(1);
                        category.setICON(2);
                        //index.categories.add(category);
                    }
                }
            }

            index.roots.put(rootFolder.replace("/", ""), urls);
        }
        String str = gson.toJson(index);

        System.err.println(str);

        uploadIndex(bucketName, client, str);

    }

    private static void uploadIndex(String bucketName, OSSClient client, String str) {
        InputStream content = IOUtils.toInputStream(str);

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(str.getBytes().length);

        String key = "index.ini";
        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, content, meta);

        // 打印ETag
        System.out.println(result.getETag());
    }
}
