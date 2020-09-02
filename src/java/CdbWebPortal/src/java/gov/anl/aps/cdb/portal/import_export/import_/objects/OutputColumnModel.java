/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import java.io.Serializable;

/**
 *
 * @author craig
 */
public class OutputColumnModel implements Serializable {

    private int columnIndex;
    private String header = null;
    private String domainProperty = null;

    public OutputColumnModel(
            int columnIndex,
            String header,
            String domainProperty) {

        this.columnIndex = columnIndex;
        this.header = header;
        this.domainProperty = domainProperty; // must have getter method
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getHeader() {
        return header;
    }

    public String getDomainProperty() {
        return domainProperty;
    }

}    
