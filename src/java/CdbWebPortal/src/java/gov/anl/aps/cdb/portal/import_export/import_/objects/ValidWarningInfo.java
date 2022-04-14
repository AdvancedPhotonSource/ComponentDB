/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ValidWarningInfo {
    
    private ValidInfo validInfo;
    private WarningInfo warningInfo;
    
    public ValidWarningInfo(ValidInfo validInfo, WarningInfo warningInfo) {
        this.validInfo = validInfo;
        this.warningInfo = warningInfo;
    }
    
    public boolean isValid() {
        if (validInfo != null) {
            return validInfo.isValid();
        } else {
            return false;
        }
    }
    
    public String validString() {
        if (validInfo != null) {
            return validInfo.getValidString();
        } else {
            return "";
        }
    }
    
    public boolean isWarning() {
        if (warningInfo != null) {
            return warningInfo.isWarning();
        } else {
            return false;
        }
    }

    public String warningString() {
        if (warningInfo != null) {
            return warningInfo.getWarningString();
        } else {
            return "";
        }
    }

}
