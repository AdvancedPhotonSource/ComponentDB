/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ImportInfo {

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
