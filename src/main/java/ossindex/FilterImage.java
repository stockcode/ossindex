package ossindex;

import com.jhlabs.image.BoxBlurFilter;
import com.jhlabs.image.HighPassFilter;
import com.jhlabs.image.LensBlurFilter;
import net.coobird.thumbnailator.util.BufferedImages;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by vicky on 2014/12/9.
 */
public class FilterImage {

    public static void main(String[] args) throws IOException {

    }

    public void Filter(String srcFile) throws IOException {

        File inFile = new File(srcFile);

        File outFile = new File(srcFile.replaceAll("smallthumb", "filterthumb"));

        if (outFile.exists()) return;

        if (!outFile.getParentFile().exists()) FileUtils.forceMkdir(outFile.getParentFile());

        BufferedImage in = ImageIO.read(inFile);

        BufferedImage dst = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);


        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();

        boxBlurFilter.filter(in, dst);

        ImageIO.write(dst, "jpg", outFile);
    }
}
