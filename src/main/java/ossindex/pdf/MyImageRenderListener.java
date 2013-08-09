package ossindex.pdf;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 13-8-9.
 */
public class MyImageRenderListener implements RenderListener {

    public List<String> files = new ArrayList<String>();

    /** The new document to which we've added a border rectangle. */
    protected String path = "";

    /**
     * Creates a RenderListener that will look for images.
     */
    public MyImageRenderListener(String path) {
        this.path = path;
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#beginTextBlock()
     */
    public void beginTextBlock() {
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#endTextBlock()
     */
    public void endTextBlock() {
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderImage(
     *     com.itextpdf.text.pdf.parser.ImageRenderInfo)
     */
    public void renderImage(ImageRenderInfo renderInfo) {
        try {
            String filename;
            FileOutputStream os;
            PdfImageObject image = renderInfo.getImage();
            if (image == null) return;
            filename = String.format(path, renderInfo.getRef().getNumber(), image.getFileType());
            if (!files.contains(filename))
                files.add(filename);

            os = new FileOutputStream(filename);
            os.write(image.getImageAsBytes());
            os.flush();
            os.close();


        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @see com.itextpdf.text.pdf.parser.RenderListener#renderText(
     *     com.itextpdf.text.pdf.parser.TextRenderInfo)
     */
    public void renderText(TextRenderInfo renderInfo) {
    }
}
