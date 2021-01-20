/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

/**
 *
 * @author craig
 */
public class ImportExportFormatInfo {
    
    private String formatName;
    private Class helperClass;
    
    public ImportExportFormatInfo(String name, Class helperClass) {
        formatName = name;
        this.helperClass = helperClass;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public Class getHelperClass() {
        return helperClass;
    }

    public void setHelperClass(Class helperClass) {
        this.helperClass = helperClass;
    }
    
    @Override
    public String toString() {
        return formatName;
    }
    
}
