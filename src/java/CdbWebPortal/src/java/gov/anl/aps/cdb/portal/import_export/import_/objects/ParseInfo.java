/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

/**
 *
 * @author craig
 */
public class ParseInfo<ValueType extends Object> {

    protected ValueType value = null;
    protected ValidInfo validInfo = null;

    public ParseInfo(ValueType v, boolean iv, String s) {
        value = v;
        validInfo = new ValidInfo(iv, s);
    }

    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public ValueType getValue() {
        return value;
    }

}
