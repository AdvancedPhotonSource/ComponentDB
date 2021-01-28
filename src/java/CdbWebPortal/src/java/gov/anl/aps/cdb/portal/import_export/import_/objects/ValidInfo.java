/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ValidInfo {

    protected boolean isValid = false;
    protected String validString = "";

    public ValidInfo(boolean iv, String s) {
        isValid = iv;
        validString = s;
    }

    public boolean isValid() {
        return isValid;
    }

    public void isValid(boolean b) {
        isValid = b;
    }

    public String getValidString() {
        return validString;
    }

    public void setValidString(String s) {
        validString = s;
    }
}
