package ossindex;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.commons.io.IOUtils;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ListObjectsRequest;
import com.aliyun.openservices.oss.model.OSSObjectSummary;
import com.aliyun.openservices.oss.model.ObjectListing;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;
import com.google.gson.Gson;

import ossindex.model.ImageInfo;
import ossindex.model.ImageInfos;
import ossindex.model.Index;
import ossindex.model.PhotoGallery;

public class StartIndex {
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    
    private static RestPhotoGallery restPhotoGallery = new RestPhotoGallery();
    
    public static void main(String[] args) throws UnirestException, IOException {


        String accessKeyId = "hauXgt6si5cgU39B";
        String accessKeySecret = "W8pEoUO4h2oIkeAAF1vHdvgdbJXvXp";
        String bucketName = "beauty-photo";



        StartIndex startIndex = new StartIndex();

        startIndex.start(accessKeyId, accessKeySecret, bucketName);


    }

    public void start(String accessKeyId, String accessKeySecret, String bucketName) throws UnirestException {
        String today = df.format(new Date());
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


        List<String> dailyUrls = new ArrayList<String>();

        for (String rootFolder : rootFolders) {
            listObjectsRequest.setPrefix(rootFolder);
            listing = client.listObjects(listObjectsRequest);
            System.err.println(rootFolder + ":");
            List<String> urls = new ArrayList<String>();


            for (String folder : listing.getCommonPrefixes()) {
                if (folder.contains(" ")) continue;

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
                    if (listFolder.contains(" ")) continue;

                    System.err.println(listFolder);

                    strs = listFolder.split("/");
                    if (strs.length > 1) {
                    	PhotoGallery photoGallery = new PhotoGallery(listFolder);                    	
                    	String objectId = restPhotoGallery.SavePhotoGallery(photoGallery);
                    	
                        String date = "2000-01-01";

                        listObjectsRequest.setPrefix(listFolder + "smallthumb/");
                        listing = client.listObjects(listObjectsRequest);
                        ImageInfos imageInfos = new ImageInfos();

                        imageInfos.setObjectId(objectId);
                        
                        for(OSSObjectSummary summary : listing.getObjectSummaries()) {
                            String key = summary.getKey();
                            if (!key.endsWith("jpg")) continue;

                            if (key.contains("cover")) continue;

                            ImageInfo imageInfo = new ImageInfo();
                            imageInfo.setKey(key.substring(key.lastIndexOf("/")+1, key.length()));
                            imageInfos.getResults().add(imageInfo);
                            date = df.format(summary.getLastModified());
                        }


                        String str = gson.toJson(imageInfos);

                        System.err.println(str);

                        uploadIndex(bucketName, client, str, listFolder + "index.json");

                        urls.add(listFolder + ":" + date + ":" + objectId);

                        if (today.equals(date)) dailyUrls.add(listFolder + ":" + date + ":" + objectId);
                    }
                }
            }

            Collections.sort(urls, new Comparator<String>() {
                public int compare(String o1, String o2) {
                    try {
                        Date d1 = df.parse(o1.split(":")[1]);
                        Date d2 = df.parse(o2.split(":")[1]);
                        return d2.compareTo(d1);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            index.roots.put(rootFolder.replace("/", ""), urls);
        }

        index.roots.put("daily", dailyUrls);

        String str = gson.toJson(index);

        System.err.println(str);

        uploadIndex(bucketName, client, str, "index.json");
    }

    private static void uploadIndex(String bucketName, OSSClient client, String str, String key) {
        InputStream content = IOUtils.toInputStream(str);

        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();

        // 必须设置ContentLength
        meta.setContentLength(str.getBytes().length);
        meta.setContentType("application/json");

        // 上传Object.
        PutObjectResult result = client.putObject(bucketName, key, content, meta);

        // 打印ETag
        System.out.println(result.getETag());
    }
}
