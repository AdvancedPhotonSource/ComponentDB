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
        boolean isValid = false;
        String validString = "";
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
    
    protected List<RowModel> rows = new ArrayList<>();
    protected List<ColumnModel> columns;
    
    public abstract int getDataStartRow();

    public abstract void parseRow(Row row);
    
}
