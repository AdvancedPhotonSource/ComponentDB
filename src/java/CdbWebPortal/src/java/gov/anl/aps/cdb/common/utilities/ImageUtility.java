/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.common.utilities;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import gov.anl.aps.cdb.common.exceptions.ImageProcessingFailed;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.io.ByteArrayInputStream;
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

    public static Boolean verifyImageSizeBigger(byte[] imageData, int maxDim) {
        ImageIcon imageIcon = new ImageIcon(imageData);
        Image inImage = imageIcon.getImage();
        int origWidth = inImage.getWidth(null);
        int origHeight = inImage.getHeight(null);

        return ((origWidth > maxDim) || (origHeight > maxDim));
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
            imageData = rotateImageUsingMetadataIfNeeded(imageData, imageFormat);

            ByteArrayInputStream data = new ByteArrayInputStream(imageData);
            BufferedImage imageIo = ImageIO.read(data);

            ImageIO.createImageInputStream(data);

            int origWidth = imageIo.getWidth();
            int origHeight = imageIo.getHeight();

            double scale = (double) maxDim / (double) (origWidth);
            if (origHeight > origWidth) {
                scale = (double) maxDim / (double) (origHeight);
            }

            int scaledW = (int) (scale * origWidth);
            int scaledH = (int) (scale * origHeight);

            AffineTransform tx = new AffineTransform();

            if (scale < 1.0d) {
                tx.scale(scale, scale);
            }

            return transformImage(imageData, tx, scaledW, scaledH, imageFormat);

        } catch (IOException ex) {
            logger.error("Could not process image: " + ex.getMessage());
            throw new ImageProcessingFailed(ex);
        } catch (NullPointerException ex) {
            logger.error("Could not process image: " + ex.getMessage());
            throw new ImageProcessingFailed(ex);
        }
    }

    private static byte[] rotateImageUsingMetadataIfNeeded(byte[] imageData, String imageFormat) throws IOException {
        // Get orientation
        int orientation = -1;
        try {
            ByteArrayInputStream data = new ByteArrayInputStream(imageData);
            Metadata metadata = ImageMetadataReader.readMetadata(data);
            Directory exifD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            orientation = exifD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (Exception ex) {
            logger.error(ex);
            
            // Continue with original image data. 
            return imageData; 
        }

        ByteArrayInputStream data = new ByteArrayInputStream(imageData);
        BufferedImage imageIo = ImageIO.read(data);
        int width = imageIo.getWidth();
        int height = imageIo.getHeight();

        AffineTransform tx = new AffineTransform();

        boolean flipAxes = false;

        switch (orientation) {
            case 1:
                return imageData;
            case 2: // Flip X
                tx.scale(-1.0, 1.0);
                tx.translate(-height, 0);
                break;
            case 3: // PI rotation
                tx.translate(width, height);
                tx.rotate(Math.PI);
                break;
            case 4: // Flip Y
                tx.scale(1.0, -1.0);
                tx.translate(0, -width);
                break;
            case 5: // - PI/2 and Flip X
                tx.rotate(-Math.PI / 2);
                tx.scale(-1.0, 1.0);                
                break;
            case 6: // -PI/2 and -width
                tx.translate(height, 0);
                tx.rotate(Math.PI / 2);
                break;
            case 7: // PI/2 and Flip
                tx.scale(-1.0, 1.0);
                tx.translate(-height, 0);
                tx.translate(0, width);
                tx.rotate(3 * Math.PI / 2);
                break;
            case 8: // PI / 2
                tx.translate(0, width);
                tx.rotate(3 * Math.PI / 2);
                break;
            default:
                return imageData;
        }

        if (orientation > 4) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        return transformImage(imageData, tx, width, height, imageFormat);

    }

    private static byte[] transformImage(byte[] imageBytes, AffineTransform transformation, int width, int height, String imageFormat) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(imageBytes);
        BufferedImage imageIo = ImageIO.read(data);

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

        BufferedImage outImage = new BufferedImage(width, height, imageType);
        Graphics2D g2d = outImage.createGraphics();

        // Prepare output image based on incoming image format.
        switch (imageFormat.toLowerCase()) {
            case "png": {
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, width, height);
                g2d.setComposite(AlphaComposite.Src);
                break;
            }
            default: {
                // Do nothing
                break;
            }
        }

        g2d.drawImage(imageIo, transformation, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outImage, imageFormat, baos);
        byte[] bytesOut = baos.toByteArray();
        return bytesOut;
    }

}
