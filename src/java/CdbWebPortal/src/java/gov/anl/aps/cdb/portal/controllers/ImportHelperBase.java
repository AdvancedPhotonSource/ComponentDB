/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;

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

    public class ColumnModel implements Serializable {

        private final String header;
        private final String property;

        public ColumnModel(String header, String property) {
            this.header = header;
            this.property = property;
        }

        public String getHeader() {
            return header;
        }

        public String getProperty() {
            return property;
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
    
    public ImportHelperBase() {
        createColumnModels();
    }
    
    public List<RowModel> getRows() {
        return rows;
    }
    
    public List<ColumnModel> getColumns() {
        return columns;
    }
    
    public String getCompletionUrl() {
        return getCompletionUrlValue();
    }
    
    protected void createColumnModels() {
        
        // allow subclass to create column models
        createColumnModels_();
        
        columns.add(new ColumnModel(isValidHeader, isValidProperty));
        columns.add(new ColumnModel(validStringHeader, validStringProperty));
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