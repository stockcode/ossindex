package ossindex;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.OSSException;
import com.aliyun.openservices.oss.model.*;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
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
    static double K5 = K1 * 10;
    static DecimalFormat df = new DecimalFormat("#0.00");

    public static void main(String[] args) throws IOException {
        String path = args[0];

        System.err.println("path=" + path);

        ChangeName(new File(path));

        thumbOrignial(path);

        thumbBig(path);

        thumbSmall(path);
}

    private static void ChangeName(File file) {
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return;
        }
        for (File f : flist) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
                System.out.println("Dir==>" + f.getAbsolutePath());
                if (f.getPath().contains(" ")) {
                    File dst = new File(f.getPath().replaceAll(" ", "_"));
                    f.renameTo(dst) ;
                    f = dst;
                }
                ChangeName(f);
            } else {
                //这里将列出所有的文件
                System.out.println("file==>" + f.getAbsolutePath());
                if (f.getPath().contains(" ")) {
                    File dst = new File(f.getPath().replaceAll(" ", "_"));
                    f.renameTo(dst) ;
                }

                if (f.getPath().contains("vip")) {
                    File dst = new File(f.getPath().replaceAll("vip", ""));
                    f.renameTo(dst) ;
                }
            }
        }
    }

    private static void thumbBig(String path) throws IOException {
        System.err.println("start bigthumb:" + path);


        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {
            double scale = 0.5f;
            file = iter.next();
            if (!file.getPath().contains("original")) continue;

            int i = 0;
            do {
                i++;

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                if (bytes.length - K100 > 0) scale -= 0.01;
                else scale += 0.01;

            } while (Math.abs(bytes.length - K100) > K1 * 10  && i < 10);

            System.err.println("bigthumb:" + file.getPath() + ":" + df.format(scale) + "");

            if (file.getPath().contains("bigthumb"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent().replace("original", "bigthumb" + File.separator);
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
            }
    }
    }

    private static void thumbSmall(String path) throws IOException {
        System.err.println("start smallthumb:" + path);

        double scale = 0.1f;

        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {

            file = iter.next();
            if (!file.getPath().contains("original")) continue;

            int i = 0;
            do {
                i++;

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                if (bytes.length - K5 > 0) scale -= 0.01;
                else scale += 0.01;

            } while (Math.abs(bytes.length - K5) > K1 * 2  && i < 10);

            System.err.println("smallthumb" + file.getPath() + ":" + df.format(scale) + "");

            if (file.getPath().contains("smallthumb"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent().replace("original", "smallthumb" + File.separator);
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
            }
        }
    }


    private static void thumbOrignial(String path) throws IOException {
        System.err.println("start originalthumb:" + path);

        double scale = 0.5f;

        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {
            file = iter.next();

            if (file.getPath().contains("thumb")) continue;


            if (file.length() < (MB1 + 150 * K1)) {

                if (!file.getPath().contains("original")) {

                    String filepath = file.getParent() + File.separator + "original" + File.separator;
                    FileUtils.forceMkdir(new File(filepath));
                    filepath += file.getName().toLowerCase();
                    file.renameTo(new File(filepath));
                }

                continue;
            }

            scale = MB1 / file.length() * 2;

            int i = 0;
            do {

                i++;

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                int len = bytes.length;

                System.err.println(file.getPath() + ":" + df.format(scale) + ":" + len);

                if (len - MB1 > 0) scale -= 0.09;
                else scale += 0.09;

            } while (Math.abs(bytes.length - MB1) > K1 * 100 && i < 10);

            if (file.getPath().contains("original"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent() + File.separator + "original" + File.separator;
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
                FileUtils.forceDelete(file);
            }

        }
    }


}
