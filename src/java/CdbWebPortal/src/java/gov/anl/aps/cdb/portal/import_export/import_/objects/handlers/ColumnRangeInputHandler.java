/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

/**
 *
 * @author craig
 */
public abstract class ColumnRangeInputHandler extends InputHandler {

    private int firstColumnIndex;
    private int lastColumnIndex;

    public ColumnRangeInputHandler(int firstColumnIndex, int lastColumnIndex) {
        this.firstColumnIndex = firstColumnIndex;
        this.lastColumnIndex = lastColumnIndex;
    }

    public int getFirstColumnIndex() {
        return firstColumnIndex;
    }

    public int getLastColumnIndex() {
        return lastColumnIndex;
    }

}
