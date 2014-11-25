package gov.anl.aps.cdb.portal.utilities;

import gov.anl.aps.cdb.portal.exceptions.ImageProcessingFailed;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import org.apache.log4j.Logger;

//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.file.Path;
public class ImageUtility {

    private static final Logger logger = Logger.getLogger(ImageUtility.class.getName());

    public static byte[] resizeImage(byte[] orig, int maxDim) throws ImageProcessingFailed {
        try {
            ImageIcon imageIcon = new ImageIcon(orig);
            Image inImage = imageIcon.getImage();
            double scale = (double) maxDim / (double) inImage.getWidth(null);

            int scaledW = (int) (scale * inImage.getWidth(null));
            int scaledH = (int) (scale * inImage.getHeight(null));

            BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_RGB);
            AffineTransform tx = new AffineTransform();

            if (scale < 1.0d) {
                tx.scale(scale, scale);
            }

            Graphics2D g2d = outImage.createGraphics();
            g2d.drawImage(inImage, tx, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outImage, "JPG", baos);
            byte[] bytesOut = baos.toByteArray();
            return bytesOut;
        } 
        catch (IOException ex) {
            logger.error("Could not process image: " + ex.getMessage());
            throw new ImageProcessingFailed(ex);
        }
    }
//
//    public static void main(String[] args) throws Exception {
//        Path path = Paths.get("hard_drive.jpg");
//        byte[] data = Files.readAllBytes(path);
//        byte[] outImage = ImageUtility.resizeImage(data, 100);
//        Path path2 = Paths.get("hard_drive_thumb.jpg");
//        Files.write(path2, outImage); 
//    }
}
