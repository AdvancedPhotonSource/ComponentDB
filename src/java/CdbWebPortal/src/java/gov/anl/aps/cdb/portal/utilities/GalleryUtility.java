/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.utilities;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.common.exceptions.ImageProcessingFailed;
import gov.anl.aps.cdb.common.utilities.FileUtility;
import gov.anl.aps.cdb.common.utilities.ImageUtility;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * This Utility is used to generate and update files for the gallery.
 */
public class GalleryUtility {

    private static final Logger logger = LogManager.getLogger(GalleryUtility.class.getName());

    protected static boolean utilMode = false;
    
    protected static String XLS_EXCEL_FORMAT = "xls";
    protected static String XLSX_EXCEL_FORMAT = "xlsx";

    public static String getImageFormat(String fileName) {
        if (fileName.endsWith(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION)) {
            fileName = fileName.replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, "");
        }
        String[] nameParts = fileName.split("\\.");
        return nameParts[nameParts.length - 1];
    }

    public static Boolean viewableFileName(String fileName) {
        if (fileName == null) {
            return false;
        }
        String imageFormat = getImageFormat(fileName);
        return viewableFormat(imageFormat);
    }

    public static Boolean viewableFormat(String imageFormat) {
        if (imageFormat.equalsIgnoreCase("pdf")) {
            return true;
        } else if (imageFormat.equalsIgnoreCase("png")) {
            return true;
        } else if (imageFormat.equalsIgnoreCase("jpg")) {
            return true;
        } else if (imageFormat.equalsIgnoreCase("jpeg")) {
            return true;
        } else if (imageFormat.equalsIgnoreCase("gif")) {
            return true;
        }

        return false;
    }

    public static void storeImagePreviews(File originalFile) {
        storeImagePreviews(originalFile, null);
    }

    public static void storeImagePreviews(File originalFile, String imageFormat) {
        String originalName = originalFile.getName();
        boolean viewable;
        if (imageFormat == null) {
            imageFormat = getImageFormat(originalName);
            viewable = viewableFormat(imageFormat);
        } else {
            viewable = viewableFormat(imageFormat);
        }
        if (originalName.endsWith(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION)) {
            originalName = originalName.replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, "");
        }

        if (viewable) {
            logger.debug("Generating previews for: " + originalName);
        } else {
            logger.debug("Cannot generate preview for: " + originalName + " Invalid Extension: " + imageFormat);
            return;
        }
        try {
            byte[] originalData;

            // Generate Preview to scale for pdf images.
            if (imageFormat.equalsIgnoreCase("pdf")) {
                byte[] pdfBytes = Files.readAllBytes(originalFile.toPath());
                imageFormat = "png";
                originalData = createPNGFromPDF(pdfBytes);

                if (originalData == null) {
                    return;
                }
            } else {
                originalData = Files.readAllBytes(originalFile.toPath());
            }

            String basePath = originalFile.getParentFile().getAbsolutePath();
            storePreviewsFromViewableData(originalData, imageFormat, basePath, originalName);
        } catch (IOException ex) {
            logger.error(ex);
            // Check allows this class to run as a utility without server running. 
            if (!utilMode) {
                FacesContext context = FacesContext.getCurrentInstance();
                if (context != null) {
                    SessionUtility.addErrorMessage("Error", ex.toString());
                }
            }
        }
    }

    public static void storePreviewsFromViewableData(byte[] data, String imageFormat, String basePath, String fileName) {
        try {
            byte[] thumbData = ImageUtility.resizeImage(data, StorageUtility.THUMBNAIL_IMAGE_SIZE, imageFormat);

            String thumbnailName = fileName + CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION;
            String thumbFilePath = basePath + "/" + thumbnailName;
            //String thumbFileName = originalFile.getAbsolutePath().replace(originalName, originalName + CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);
            thumbFilePath = thumbFilePath.replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, "");
            Path thumbPath = Paths.get(thumbFilePath);
            Files.write(thumbPath, thumbData);
            logger.debug("Saved File: " + thumbFilePath);

            byte[] scaledData;
            if (ImageUtility.verifyImageSizeBigger(data, StorageUtility.SCALED_IMAGE_SIZE)) {
                scaledData = ImageUtility.resizeImage(data, StorageUtility.SCALED_IMAGE_SIZE, imageFormat);
            } else {
                scaledData = data;
            }

            String scaledFileName = fileName + CdbPropertyValue.SCALED_IMAGE_EXTENSION;
            String scaledFilePath = basePath + "/" + scaledFileName;
            scaledFilePath = scaledFilePath.replace(CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION, "");
            Path scaledPath = Paths.get(scaledFilePath);
            Files.write(scaledPath, scaledData);
            logger.debug("Saved File: " + scaledFilePath);

        } catch (IOException | ImageProcessingFailed ex) {
            logger.error(ex);
            // Check allows this class to run as a utility without server running. 
            if (!utilMode) {
                FacesContext context = FacesContext.getCurrentInstance();
                if (context != null) {
                    SessionUtility.addErrorMessage("Error", ex.toString());
                }
            }
        }
    }

    public static byte[] createPNGFromPDF(byte[] pdfBytes) {
        byte[] originalData;
        try {
            BufferedImage image = null;
            try (PDDocument pdfDocument = PDDocument.load(pdfBytes)) {
                PDFRenderer renderer = new PDFRenderer(pdfDocument);
                image = renderer.renderImage(0);
                pdfDocument.close();
            }

            try (ByteArrayOutputStream imageBaos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "PNG", imageBaos);
                imageBaos.flush();
                originalData = imageBaos.toByteArray();
            }

            // It is not possible to catch certain errors during gerneration of a page preview. 
            // Avoid creating blank white previews. 
            if (originalData.length < 5000) {
                return null;
            }
        } catch (IOException ex) {
            logger.error(ex);
            return null;
        }

        return originalData;
    }
    
    public static boolean isFileNameExcel(String fileName) {
        if (fileName != null) {
            String fileType = FileUtility.getFileExtension(fileName);
            return fileType.equals(XLS_EXCEL_FORMAT) || fileType.equals(XLSX_EXCEL_FORMAT); 
            
        }
        return false;         
    }

    public static StreamedContent convertExcelToPDF(InputStream stream, String fileName) throws DocumentException, ExternalServiceError, IOException {
        String fileType = FileUtility.getFileExtension(fileName);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Document document = new Document(PageSize.ARCH_E, 0, 0, 0, 0);
        document.addTitle(fileName);

        PdfWriter writer = PdfWriter.getInstance(document, output);
        document.open();
        document.addDocListener(writer);

        Iterator<Sheet> sheetIterator = null;
        if (fileType.equals(XLS_EXCEL_FORMAT)) {
            HSSFWorkbook workbook = new HSSFWorkbook(stream);
            sheetIterator = workbook.iterator();
        }
        if (fileType.equals(XLSX_EXCEL_FORMAT)) {
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            sheetIterator = workbook.iterator();
        }

        if (sheetIterator != null) {
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Iterator<Row> rowIterator = sheet.iterator();

                Paragraph para = new Paragraph(sheet.getSheetName());
                para.setSpacingAfter(20);
                para.setSpacingBefore(20);
                document.add(para);

                fillInDataForSheet(rowIterator, document);
            }
        }

        document.close();
        logger.debug("Excel file converted to PDF successfully");

        InputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        
        DefaultStreamedContent.Builder builder = DefaultStreamedContent.builder();                
        builder.stream(() -> inputStream); 

        return builder.build(); 
    }

    private static void fillInDataForSheet(Iterator<Row> rowIterator, Document document) throws DocumentException {
        PdfPTable outerTable = new PdfPTable(1);

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            int columns = row.getLastCellNum();
            if (columns < 0) {
                outerTable.addCell(" ");
                continue;
            }

            PdfPTable my_table = new PdfPTable(columns);
            my_table.setHorizontalAlignment(Element.ALIGN_CENTER);
            my_table.setWidthPercentage(100);
            my_table.setSpacingBefore(0f);
            my_table.setSpacingAfter(0f);
            PdfPCell table_cell;

            Iterator<Cell> cellIterator = row.cellIterator();

            boolean blank = true;

            Cell cell = null;

            int cellNum = -1;

            while (cellIterator.hasNext()) {
                cellNum++;

                if (cell == null) {
                    cell = cellIterator.next();
                }

                CellAddress address = cell.getAddress();

                if (address.getColumn() != cellNum) {
                    my_table.addCell(" ");
                    continue;
                }

                switch (cell.getCellType()) {
                    case STRING:
                        //Push the data from Excel to PDF Cell
                        table_cell = new PdfPCell(new Phrase(cell.getStringCellValue()));
                        my_table.addCell(table_cell);
                        blank = false;
                        break;
                    case NUMERIC:
                    case FORMULA:
                        table_cell = new PdfPCell(new Phrase(cell.getNumericCellValue() + ""));
                        my_table.addCell(table_cell);
                        blank = false;
                        break;
                    default:
                        my_table.addCell(" ");
                        break;
                }

                cell = null;
            }

            if (!blank) {
                document.add(my_table);
            }
        }
    }

    /**
     * Provide: args[0] type (directory/document) args[1] path (path to document
     * or directory)
     *
     * or: no args (process defaults)
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        GalleryUtility.utilMode = true;
        Path imageUploadDirPath;
        Path documentUploadDirPath;

        String DIRECTORY_TYPE = "directory";
        String DOCUMENT_TYPE = "document";

        String dataDirectory = null;
        String filePath = null;

        if (args.length > 0) {
            if (args.length == 2) {
                String type = args[0];
                if (type.equalsIgnoreCase(DIRECTORY_TYPE)) {
                    dataDirectory = args[1];
                } else if (type.equalsIgnoreCase(DOCUMENT_TYPE)) {
                    filePath = args[1];
                } else {
                    throw new Exception("Invalid arguments provided 1 ");
                }
            } else {
                throw new Exception("Invalid arguments provided 2 ");
            }
        }

        if (dataDirectory != null) {
            imageUploadDirPath = Paths.get(dataDirectory + StorageUtility.getPropertyValueImagesDirectory());
            documentUploadDirPath = Paths.get(dataDirectory + StorageUtility.getPropertyValueDocumentsDirectory());
        } else if (filePath != null) {
            File file = new File(filePath);
            logger.debug("Generating Preview for image: " + filePath);
            storeImagePreviews(file);
            return;
        } else {
            imageUploadDirPath = Paths.get(StorageUtility.getFileSystemPropertyValueImagesDirectory());
            documentUploadDirPath = Paths.get(StorageUtility.getFileSystemPropertyValueDocumentsDirectory());
        }

        FilenameFilter originalFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !(name.endsWith(CdbPropertyValue.SCALED_IMAGE_EXTENSION) || name.endsWith(CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION));
            }
        };

        logger.debug("Generating Previews for images.");
        logger.debug("Using directory: " + imageUploadDirPath.toString());

        File imageUploadDir = imageUploadDirPath.toFile();
        File[] originalImageFiles = imageUploadDir.listFiles(originalFilter);

        if (originalImageFiles == null) {
            logger.debug("Could not find or open image directory: " + imageUploadDirPath.toString());
        } else {
            for (File originalFile : originalImageFiles) {
                storeImagePreviews(originalFile);
            }
        }

        logger.debug("Generating Previews for documents.");
        logger.debug("Using directory: " + documentUploadDirPath.toString());

        File documentUploadDir = documentUploadDirPath.toFile();
        File[] originalDocumentFiles = documentUploadDir.listFiles(originalFilter);

        if (originalDocumentFiles == null) {
            logger.debug("Could not find or open document directory: " + documentUploadDirPath.toString());
        } else {
            for (File originalFile : originalDocumentFiles) {
                storeImagePreviews(originalFile);
            }
        }
    }

}
