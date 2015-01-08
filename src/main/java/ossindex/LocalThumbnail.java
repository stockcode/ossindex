package ossindex;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Administrator on 13-7-11.
 */
public class LocalThumbnail {

    static double orgSize = 1024*1024 / 2;
    static long K1 = 1024;
    static double bigSize = K1 * 100 / 2;
    static double littleSize = K1 * 10;
    static DecimalFormat df = new DecimalFormat("#0.00");

    private static Logger logger = LogManager.getLogger(LocalThumbnail.class.getName());

    public static void main(String[] args) throws IOException {
        String path = args[0];

        logger.info("path=" + path);

        ChangeName(new File(path));

        selectCover(path);

        thumbOrignial(path);

        thumbBig(path);

        thumbSmall(path);
}

    private static void selectCover(String path) throws IOException {
        logger.info("start selectCover:" + path);

        Map<String, List<String>> map = new HashMap<String, List<String>>();

        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {

            file = iter.next();

            if (file.getPath().contains("thumb")) continue;

            if (map.containsKey(file.getParent())) {
                map.get(file.getParent()).add(file.getPath());
            } else {
                List<String> list = new ArrayList<String>();
                list.add(file.getPath());
                map.put(file.getParent(), list);
            }
        }

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            Boolean skiped = false;

            List<String> files = entry.getValue();
            for(String filename : files) {
                filename = filename.toLowerCase();
                if (filename.endsWith("cover.jpg")) {
                    skiped = true;
                    continue;
                }
            }

            if (skiped ){
                logger.info(entry.getKey() + " has cover, skipped");
                continue;
            }

            String orgFilename = files.get(0);
            String dstFilename = entry.getKey() + File.separator + "cover.jpg";
            FileUtils.copyFile(new File(orgFilename), new File(dstFilename));
            FileUtils.forceDelete(new File(orgFilename));

            logger.info("rename " + orgFilename + " to " + dstFilename);
        }
    }

    private static void ChangeName(File file) {
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return;
        }
        for (File f : flist) {
            if (f.isDirectory()) {
                //这里将列出所有的文件夹
                //logger.info("Dir==>" + f.getAbsolutePath());

                if (f.getPath().contains(" ")) {
                    File dst = new File(f.getPath().replaceAll(" ", "_"));
                    f.renameTo(dst) ;
                    f = dst;
                }
                ChangeName(f);
            } else {

                //这里将列出所有的文件


                if (f.getAbsolutePath().toLowerCase().endsWith("cover.jpg")) {
                    continue;
                }

                if (f.getAbsolutePath().contains("thumb") || f.getAbsolutePath().contains("original")) {
                    logger.info(f.getAbsolutePath() + " has exists, skipped");
                    continue;
                }



                String dst = f.getParent() + File.separator + generateShortUuid() + ".jpg";
                //dst = dst.replaceAll(" ", "_");

                logger.info(f.getAbsolutePath() + "==>" + dst);

                Path source = f.toPath();
                try {
                    Files.move(source, source.resolveSibling(dst));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static void thumbBig(String path) throws IOException {
        logger.info("start bigthumb:" + path);


        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {
            file = iter.next();
            if (!file.getPath().contains("original")) continue;

            String filename = file.getParent().replace("original", "bigthumb" + File.separator) + file.getName();

            if ((new File(filename)).exists()) {
                logger.info("bigthumb:" + filename + " exist,skipped");
                continue;
            }

            if (file.length() == 0) {
                logger.info("filelength=:" + file.length() + ",deleting");
                file.delete();
                continue;
            }

            double scale = bigSize / file.length() * 3.5;
            int i = 0;
            do {
                i++;

                ByteArrayOutputStream os = new ByteArrayOutputStream();

                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                if (bytes.length - bigSize > 0) scale -= 0.01;
                else scale += 0.01;

            } while (Math.abs(bytes.length - bigSize) > (bigSize * 0.1)  && i < 10);

            logger.info("bigthumb:" + file.getPath() + ":" + df.format(scale) + ":" + i);

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
        logger.info("start smallthumb:" + path);

        FilterImage filterImage = new FilterImage();


        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {


            file = iter.next();
            if (!file.getPath().contains("original")) continue;

            String filename = file.getParent().replace("original", "smallthumb" + File.separator) + file.getName();

            if ((new File(filename)).exists()) {
                logger.info("smallthumb:" + filename + " exist,skipped");
                filterImage.Filter(filename);
                continue;
            }

            double scale = littleSize / file.length() * 6;

            int i = 0;
            do {
                i++;

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                if (bytes.length - littleSize > 0) scale -= 0.01;
                else scale += 0.01;

            } while (Math.abs(bytes.length - littleSize) > (littleSize * 0.1)  && i < 10);

            logger.info("smallthumb" + file.getPath() + ":" + df.format(scale) + ":" + i);

            if (file.getPath().contains("smallthumb"))
                FileUtils.writeByteArrayToFile(file, bytes);
            else {
                String filepath = file.getParent().replace("original", "smallthumb" + File.separator);
                FileUtils.forceMkdir(new File(filepath));
                FileUtils.writeByteArrayToFile(new File(filepath + file.getName()), bytes);
            }

            filterImage.Filter(filename);
        }
    }


    private static void thumbOrignial(String path) throws IOException {
        logger.info("start originalthumb:" + path);



        byte[] bytes;


        Iterator<File> iter = FileUtils.iterateFiles(new File(path), new String[]{"jpg", "JPG"}, true);
        File file;
        while(iter.hasNext()) {
            double scale = 0.5f;

            file = iter.next();

            if (file.getPath().contains("thumb")) continue;


            if (file.length() < (orgSize + 100 * K1)) {

                if (!file.getPath().contains("original")) {

                    String filepath = file.getParent() + File.separator + "original" + File.separator;
                    FileUtils.forceMkdir(new File(filepath));
                    filepath += file.getName().toLowerCase();
                    file.renameTo(new File(filepath));
                }

                continue;
            }

            scale = orgSize / file.length() * 2;

            int i = 0;
            do {

                i++;

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                Thumbnails.of(file).scale(scale).outputFormat("jpg").toOutputStream(os);
                bytes = os.toByteArray();

                int len = bytes.length;

                logger.info(file.getPath() + ":" + df.format(scale) + ":" + len);

                if (len - orgSize > 0) scale -= 0.09;
                else scale += 0.09;

            } while (Math.abs(bytes.length - orgSize) > (orgSize * 0.1) && i < 10);

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
