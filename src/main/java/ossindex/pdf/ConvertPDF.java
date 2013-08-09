package ossindex.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 13-8-8.
 */
public class ConvertPDF {

    /** The new document to which we've added a border rectangle. */
    public static final String RESULT = "\\Img%s.%s";

    MyImageRenderListener listener;

    /**
     * Parses a PDF and extracts all the images.
     */
    public void extractImages(File file)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(file.getAbsolutePath());
        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        File imgDir = new File(file.getParent() + "\\image");
        FileUtils.forceMkdir(imgDir);

        listener = new MyImageRenderListener(imgDir.getPath() + RESULT);
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            parser.processContent(i, listener);
        }
        reader.close();
    }

    /**
     * Creates a PDF document.
     * @param filename the path to the new PDF document
     * @throws    DocumentException
     * @throws    IOException
     */
    public void createPdfwithiText(String filename) throws IOException, DocumentException {
        Image img0 = Image.getInstance(listener.files.get(0));
        Image img1 = Image.getInstance(listener.files.get(1));
        Rectangle rectangle = new Rectangle(img0.getWidth() + img1.getWidth(), Math.max(img0.getHeight(), img1.getHeight()));
        // step 1
        Document document = new Document(rectangle, 0, 0, 0, 0);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        writer.setStrictImageSequence(true);
        // step 3
        document.open();
        // step 4
        // Adding a series of images


        img0.setAlignment(Image.ALIGN_LEFT);
        img1.setAlignment(Image.ALIGN_RIGHT);
        document.add(img0);
        document.add(img1);

        for (int i = 2; i < listener.files.size(); i++) {
            img0 = Image.getInstance(listener.files.get(i++));
            if (i < listener.files.size()) {
                img1 = Image.getInstance(listener.files.get(i));
                img1.setAlignment(Image.ALIGN_RIGHT);
            }

            img0.setAlignment(Image.ALIGN_LEFT);


            document.add(img0);
            if (i < listener.files.size()) {
                document.add(img1);
                rectangle = new Rectangle(img0.getWidth() + img1.getWidth(), Math.max(img0.getHeight(), img1.getHeight()));
            } else {
                rectangle = new Rectangle(img0.getWidth(), img0.getHeight());
            }
            document.setPageSize(rectangle);
            document.newPage();
        }

        // step 5
        document.close();
    }


    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws DocumentException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, DocumentException {
        ConvertPDF convertPDF = new ConvertPDF();
        String folder = "c:\\convertPDF";
        for(Object pdfFile : FileUtils.listFiles(new File(folder), new String[]{"pdf"}, false)) {
            File file = (File) pdfFile;
        convertPDF.extractImages(file);
        convertPDF.createPdfwithiText(file.getParent() + "\\bak_" + file.getName());
        }
    }
}
