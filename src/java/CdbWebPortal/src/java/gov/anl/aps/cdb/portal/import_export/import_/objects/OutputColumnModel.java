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

    private String header = null;
    private String domainProperty = null;
    private boolean displayed = false;

    public OutputColumnModel(
            String header, String domainProperty) {

        this.header = header;
        this.domainProperty = domainProperty; // must have getter method
    }

    public OutputColumnModel(
            String header, String domainProperty, boolean displayed) {

        this(header, domainProperty);
        this.displayed = displayed;
    }

    public String getHeader() {
        return header;
    }

    public String getDomainProperty() {
        return domainProperty;
    }
    
    public boolean isDisplayed() {
        return displayed;
    }

}    
