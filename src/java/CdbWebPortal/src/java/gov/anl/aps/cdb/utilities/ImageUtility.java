package gov.anl.aps.cdb.utilities;

import gov.anl.aps.cdb.exceptions.ImageProcessingFailed;
import java.awt.AlphaComposite;
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

    public static final String DefaultImageFormat = "jpg";

    private static final Logger logger = Logger.getLogger(ImageUtility.class.getName());

    public static byte[] resizeImage(byte[] imageData, int maxDim) throws ImageProcessingFailed {
        return resizeImage(imageData, maxDim, DefaultImageFormat);
    }

    public static byte[] resizeImage(byte[] imageData, int maxDim, String imageFormat) throws ImageProcessingFailed {
        try {
            ImageIcon imageIcon = new ImageIcon(imageData);
            Image inImage = imageIcon.getImage();
            int origWidth = inImage.getWidth(null);
            int origHeight = inImage.getHeight(null);

            double scale = (double) maxDim / (double) (origWidth);
            if (origHeight > origWidth) {
                scale = (double) maxDim / (double) (origHeight);
            }

            int scaledW = (int) (scale * inImage.getWidth(null));
            int scaledH = (int) (scale * inImage.getHeight(null));

            // Chose image type based on incoming image format.
            int imageType;
            switch (imageFormat.toLowerCase()) {
                case "png": {
                    imageType = BufferedImage.TYPE_INT_ARGB;
                    break;
                }
                default: {
                    imageType = BufferedImage.TYPE_INT_RGB;
                    break;
                }
            }
            BufferedImage outImage = new BufferedImage(scaledW, scaledH, imageType);
            AffineTransform tx = new AffineTransform();

            if (scale < 1.0d) {
                tx.scale(scale, scale);
            }

            Graphics2D g2d = outImage.createGraphics();

            // Prepare output image based on incoming image format.
            switch (imageFormat.toLowerCase()) {
                case "png": {
                    g2d.setComposite(AlphaComposite.Clear);
                    g2d.fillRect(0, 0, scaledW, scaledH);
                    g2d.setComposite(AlphaComposite.Src);
                    break;
                }
                default: {
                    // Do nothing
                    break;
                }
            }

            g2d.drawImage(inImage, tx, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outImage, imageFormat, baos);
            byte[] bytesOut = baos.toByteArray();
            return bytesOut;
        } catch (IOException ex) {
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
