package ossindex.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 13-8-8.
 */
public class ExtractPDF {

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
     * @throws    com.itextpdf.text.DocumentException
     * @throws    java.io.IOException
     */
    public void createPdfwithiText(String filename) throws IOException, DocumentException {
        Image img0 = Image.getInstance(listener.files.get(0));
        Image img1 = Image.getInstance(listener.files.get(1));


        Rectangle rectangle = new Rectangle(img0.getWidth(), img0.getHeight());
        // step 1
        Document document = new Document(rectangle, 0, 0, 0, 0);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        //writer.setStrictImageSequence(true);
        // step 3
        document.open();
        // step 4
        // Adding a series of images

        document.add(img0);

        Paragraph paragraph = new Paragraph();
        paragraph.add(new Chunk(img1, 0, 0, true));
        document.setPageSize(new Rectangle(img1.getWidth(), img1.getHeight()));
        document.add(paragraph);


        for (int i = 2; i < listener.files.size(); i++) {
            paragraph = new Paragraph();

            img0 = Image.getInstance(listener.files.get(i++));
            if (i < listener.files.size()) {
                img1 = Image.getInstance(listener.files.get(i));
            }



            paragraph.add(new Chunk(img0, 0, 0, true));
            if (i < listener.files.size()) {

                if (img0.getHeight() < img1.getHeight())
                    img0.scaleAbsolute(img0.getWidth(), img1.getHeight());
                else
                    img1.scaleAbsolute(img1.getWidth(), img0.getHeight());

                paragraph.add(new Chunk(img1, 0, 0, true));
                rectangle = new Rectangle(img0.getWidth() + img1.getWidth(), Math.max(img0.getHeight(), img1.getHeight()));
            } else {
                rectangle = new Rectangle(img0.getWidth(), img0.getHeight());
            }
            document.setPageSize(rectangle);
            document.add(paragraph);
        }

        // step 5
        document.close();
    }
    public static final int BLANK_THRESHOLD = 50;

    public static void removeBlankPdfPages(String source, String destination)
            throws IOException, DocumentException
    {
        PdfReader r = null;
        RandomAccessSourceFactory rasf = null;
        RandomAccessFileOrArray raf = null;
        Document document = null;
        PdfCopy writer = null;

        try {
            r = new PdfReader(source);
            // deprecated
            //    RandomAccessFileOrArray raf
            //           = new RandomAccessFileOrArray(pdfSourceFile);
            // itext 5.4.1
            rasf = new RandomAccessSourceFactory();
            raf = new RandomAccessFileOrArray(rasf.createBestSource(source));
            document = new Document(r.getPageSizeWithRotation(1));
            writer = new PdfCopy(document, new FileOutputStream(destination));
            document.open();
            PdfImportedPage page = null;

            for (int i=1; i<=r.getNumberOfPages(); i++) {
                // first check, examine the resource dictionary for /Font or
                // /XObject keys.  If either are present -> not blank.
                PdfDictionary pageDict = r.getPageN(i);
                PdfDictionary resDict = (PdfDictionary) pageDict.get( PdfName.RESOURCES );
                boolean noFontsOrImages = true;
                if (resDict != null) {
                    noFontsOrImages = resDict.get( PdfName.FONT ) == null &&
                            resDict.get( PdfName.XOBJECT ) == null;
                }
                System.out.println(i + " noFontsOrImages " + noFontsOrImages);

                if (!noFontsOrImages) {
                    byte bContent [] = r.getPageContent(i,raf);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bs.write(bContent);
                    System.out.println
                            (i + bs.size() + " > BLANK_THRESHOLD " +  (bs.size() > BLANK_THRESHOLD));
                    if (bs.size() > BLANK_THRESHOLD) {
                        page = writer.getImportedPage(r, i);
                        writer.addPage(page);
                    }
                }
            }
        }
        finally {
            if (document != null) document.close();
            if (writer != null) writer.close();
            if (raf != null) raf.close();
            if (r != null) r.close();
        }
    }

    /**
     * Main method.
     * @param    args    no arguments needed
     * @throws com.itextpdf.text.DocumentException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException, DocumentException {
        ExtractPDF convertPDF = new ExtractPDF();
        String folder = "c:\\convertPDF";



        //String filename = args[0];

        String filename = "c:\\Temp\\pobing.pdf";

            File file = new File(filename);
            File origFile = new File(folder + "\\original\\" + file.getName());
            file.renameTo(origFile);
            convertPDF.extractImages(origFile);

    }
}
