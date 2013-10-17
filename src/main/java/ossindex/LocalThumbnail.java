package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.*;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 13-7-11.
 */
public class LocalThumbnail {

    static double MB1 = 1024*1024;
    static long K1 = 1024;
    static double K100 = K1 * 100;
    static double K5 = K1 * 3;

    public static void main(String[] args) throws IOException {
        String path = args[0];
        System.err.println("path=" + path);

        thumbOrginial(path);

        thumbBig(path);

        thumbSmall(path);
}

    private static void thumbBig(String path) throws IOException {
        double scale = 0.5f;

        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "bmp"}, true);
        File file;
        while(iter.hasNext()) {

            file = iter.next();
            if (!file.getPath().contains("original")) continue;


            do {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                if (bytes.length - K100 > 0) scale -= 0.01;
                else scale += 0.01;

            } while (Math.abs(bytes.length - K100) > K1 * 10);

            System.err.println(file.getPath() + ":" + scale + "");

            if (file.getPath().contains("bigthumb"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent().replace("original", "bigthumb\\");
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
            }
    }
    }

    private static void thumbSmall(String path) throws IOException {
        double scale = 0.1f;

        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "bmp"}, true);
        File file;
        while(iter.hasNext()) {

            file = iter.next();
            if (!file.getPath().contains("original")) continue;


            do {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                if (bytes.length - K5 > 0) scale -= 0.01;
                else scale += 0.01;

            } while (Math.abs(bytes.length - K5) > K1 * 2);

            System.err.println(file.getPath() + ":" + scale + "");

            if (file.getPath().contains("smallthumb"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent().replace("original", "smallthumb\\");
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
            }
        }
    }


    private static void thumbOrginial(String path) throws IOException {
        double scale = 0.5f;

        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "bmp"}, true);
        File file;
        while(iter.hasNext()) {

            file = iter.next();
            if (file.length() < (MB1 + 100 * K1)) continue;

            scale = MB1 / file.length() * 2;

            do {

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                int len = bytes.length;

                System.err.println(file.getPath() + ":" + scale + ":" + len);

                if (len - MB1 > 0) scale -= 0.09;
                else scale += 0.09;

            } while (Math.abs(bytes.length - MB1) > K1 * 100);

            if (file.getPath().contains("original"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent() + "\\original\\";
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
                FileUtils.forceDelete(file);
            }

        }
    }


}
