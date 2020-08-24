/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

/**
 *
 * @author craig
 */
public class ImportFormatInfo {
    
    private String formatName;
    private Class importHelperClass;
    
    public ImportFormatInfo(String name, Class helperClass) {
        formatName = name;
        importHelperClass = helperClass;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public Class getImportHelperClass() {
        return importHelperClass;
    }

    public void setImportHelperClass(Class importHelperClass) {
        this.importHelperClass = importHelperClass;
    }
    
    @Override
    public String toString() {
        return formatName;
    }
    
}
