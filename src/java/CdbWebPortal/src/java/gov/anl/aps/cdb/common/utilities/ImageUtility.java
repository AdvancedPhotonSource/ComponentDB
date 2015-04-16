/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.common.utilities;

import gov.anl.aps.cdb.common.exceptions.ImageProcessingFailed;
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

/**
 * Utility class for manipulating images.
 */
public class ImageUtility {

    /**
     * Default image format.
     */
    public static final String DEFAULT_IMAGE_FORMAT = "jpg";

    private static final Logger logger = Logger.getLogger(ImageUtility.class.getName());

    /**
     * Resize image using default format.
     *
     * @param imageData image data
     * @param maxDim largest dimension of the resized image
     * @return resized image
     * @throws ImageProcessingFailed if processing fails for any reason
     */
    public static byte[] resizeImage(byte[] imageData, int maxDim) throws ImageProcessingFailed {
        return resizeImage(imageData, maxDim, DEFAULT_IMAGE_FORMAT);
    }

    /**
     * Resize image using provided format.
     *
     * @param imageData image data
     * @param maxDim largest dimension of the resized image
     * @param imageFormat image format
     * @return resized image
     * @throws ImageProcessingFailed if processing fails for any reason
     */
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

}
