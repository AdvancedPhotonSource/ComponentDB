/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ColumnHandler {
    
    private int firstColumnIndex = -1;
    private int lastColumnIndex = -1;
    
    public ColumnHandler() {
    }
    
    public ColumnHandler(int firstColumnIndex) {
        this.firstColumnIndex = firstColumnIndex;
    }
    
    public ColumnHandler(int firstColumnIndex, int lastColumnIndex) {
        this(firstColumnIndex);
        this.lastColumnIndex = lastColumnIndex;
    }
    
    public int getFirstColumnIndex() {
        return firstColumnIndex;
    }
    
    public void setFirstColumnIndex(int firstIndex) {
        this.firstColumnIndex = firstIndex;
    }

    public int getLastColumnIndex() {
        return lastColumnIndex;
    }
    
    public void setLastColumnIndex(int lastIndex) {
        this.lastColumnIndex = lastIndex;
    }

}
