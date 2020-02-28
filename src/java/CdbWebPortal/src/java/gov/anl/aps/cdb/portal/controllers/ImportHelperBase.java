/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author craig
 */
public abstract class ImportHelperBase {
    
    public class RowModel <EntityType extends CdbEntity> {

        private boolean isValid = false;
        private String validString = "";
        private EntityType entity;
        
        public RowModel(EntityType e) {
            entity = e;
        }
        
        public boolean isIsValid() {
            return isValid;
        }

        public void setIsValid(boolean isValid) {
            this.isValid = isValid;
        }

        public String getValidString() {
            return validString;
        }

        public void setValidString(String validString) {
            this.validString = validString;
        }
        
        public EntityType getEntity() {
            return entity;
        }
        
        public void setEntity(EntityType e) {
            entity = e;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RowModel) {
                return this.entity.equals(((RowModel)obj).getEntity());
            } else {
                return false;
            }
        }
        
    }

    public enum ColType {
        STRING,
        NUMERIC
    }
        
    public class ColumnModel implements Serializable {

        private final String header;
        private final String property;
        private final ColType colType;
        private final String sampleValue;
        
        public ColumnModel(String h, String p, ColType t, String v) {
            this.header = h;
            this.property = p;
            this.colType = t;
            this.sampleValue = v;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
        }
        
        public ColType getColType() {
            return colType;
        }
        
        public String getSampleValue() {
            return sampleValue;
        }
    }
    
    static public class ImportInfo {
        
        protected boolean importSuccessful = true;
        protected String message = "";
        
        public ImportInfo(boolean s, String m) {
            importSuccessful = s;
            message = m;
        }
        
        public boolean isImportSuccessful() {
            return importSuccessful;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    protected static String isValidHeader = "Is Valid";
    protected static String isValidProperty = "isValid";
    protected static String validStringHeader = "Valid String";
    protected static String validStringProperty = "validString";
    
    protected List<RowModel> rows = new ArrayList<>();
    protected List<ColumnModel> columns = new ArrayList<>();
    protected byte[] templateExcelFile = null;
    
    public ImportHelperBase() {
        createColumnModels();
    }
    
    public List<RowModel> getRows() {
        return rows;
    }
    
    public List<ColumnModel> getColumns() {
        return columns;
    }
    
    public StreamedContent getTemplateExcelFile() {
        if (templateExcelFile == null) {
            buildTemplateExcelFile();
        }
        InputStream inStream = new ByteArrayInputStream(templateExcelFile);
        return new DefaultStreamedContent(inStream, "xls", "cableCatalogTemplate.xls");
    }
    
    private void buildTemplateExcelFile() {
        
        Workbook wb = new XSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("cable type specs");
        Row headerRow = sheet.createRow(0);
        Row dataRow = sheet.createRow(1);
        for (int i = 0 ; i < columns.size() - 2 ; i++) {
            ColumnModel col = columns.get(i);
            
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(col.getHeader());
            
            Cell dataCell = dataRow.createCell(i);
            
            if (col.getColType() == ColType.STRING) {
                dataCell.setCellType(CellType.STRING);
                dataCell.setCellValue(col.getSampleValue());
            } else if (col.getColType() == ColType.NUMERIC) {
                dataCell.setCellType(CellType.NUMERIC);
                dataCell.setCellValue(Double.valueOf(col.getSampleValue()));
            }
        }
        
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            wb.write(outStream);
            templateExcelFile = outStream.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(ImportHelperBase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getCompletionUrl() {
        return getCompletionUrlValue();
    }
    
    protected void createColumnModels() {
        
        // allow subclass to create column models
        createColumnModels_();
        
        columns.add(new ColumnModel(isValidHeader, isValidProperty, ColType.STRING, ""));
        columns.add(new ColumnModel(validStringHeader, validStringProperty, ColType.STRING, ""));
    }
    
    protected void reset_() {
        // allow subclass to reset, by default do nothing
    }
    
    public void reset() {
        rows.clear();
        
        // allow subclass to reset
        reset_();
    }
    
    protected abstract void createColumnModels_();
    
    public abstract int getDataStartRow();

    public abstract boolean parseRow(Row row);
    
    protected abstract String getCompletionUrlValue();
    
    protected abstract boolean isValidationOnly();
    
    public abstract ImportInfo importData();
    
}
