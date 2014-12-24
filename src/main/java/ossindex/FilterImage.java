package ossindex;

import com.jhlabs.image.*;
import net.coobird.thumbnailator.util.BufferedImages;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;

/**
 * Created by vicky on 2014/12/9.
 */
public class FilterImage {

    public static void main(String[] args) throws IOException {

        File inFile = new File("c:\\Temp\\1.jpg");
        File outFile = new File("c:\\temp\\2.jpg");


        BufferedImage in = ImageIO.read(inFile);

        BufferedImage dst = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);


        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();

        boxBlurFilter.setRadius(10);
        boxBlurFilter.setIterations(3);

        boxBlurFilter.filter(in, dst);

        ImageIO.write(dst, "jpg", outFile);
    }

    public void Filter(String srcFile) throws IOException {

        File inFile = new File(srcFile);

        File outFile = new File(srcFile.replaceAll("smallthumb", "filterthumb"));

        //if (outFile.exists()) return;

        if (!outFile.getParentFile().exists()) FileUtils.forceMkdir(outFile.getParentFile());

        BufferedImage in = ImageIO.read(inFile);

        BufferedImage dst = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);


        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(10);
        boxBlurFilter.setIterations(3);

        boxBlurFilter.filter(in, dst);

        ImageIO.write(dst, "jpg", outFile);
    }
}
