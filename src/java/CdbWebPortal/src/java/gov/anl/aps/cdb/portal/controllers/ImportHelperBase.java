/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public abstract class ImportHelperBase {
    
    public class RowModel {

        private boolean isValid = false;
        private String validString = "";
        
        public RowModel(boolean v, String vs) {
            isValid = v;
            validString = vs;
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
    }

    static public class ColumnModel implements Serializable {

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
    
    protected static String isValidHeader = "Is Valid";
    protected static String isValidProperty = "isValid";
    protected static String validStringHeader = "Valid String";
    protected static String validStringProperty = "validString";
    
    protected List<RowModel> rows = new ArrayList<>();
    protected static List<ColumnModel> columns = new ArrayList<>();
    
    public abstract int getDataStartRow();

    public abstract void parseRow(Row row);
    
    public ImportHelperBase() {
        createColumnModels();
    }
    
    public List<RowModel> getRows() {
        return rows;
    }
    
    public static List<ColumnModel> getColumns() {
        return columns;
    }
    
    protected abstract void createColumnModels_();
    
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
    
}
