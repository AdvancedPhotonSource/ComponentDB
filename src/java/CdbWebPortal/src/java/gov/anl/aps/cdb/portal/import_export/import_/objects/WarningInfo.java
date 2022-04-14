/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class WarningInfo {
    
    protected boolean isWarning = false;
    protected String warningString = "";

    public WarningInfo(boolean isWarning, String warningString) {
        this.isWarning  = isWarning;
        this.warningString = warningString;
    }

    public boolean isWarning() {
        return isWarning;
    }

    public void isWarning(boolean b) {
        isWarning = b;
    }

    public String getWarningString() {
        return warningString;
    }

    public void setWarningString(String s) {
        warningString = s;
    }
}
