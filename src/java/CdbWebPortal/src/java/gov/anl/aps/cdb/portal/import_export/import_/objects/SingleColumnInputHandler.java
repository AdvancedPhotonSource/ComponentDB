/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public abstract class SingleColumnInputHandler extends InputHandler {

    protected int columnIndex;

    public SingleColumnInputHandler(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }
}
