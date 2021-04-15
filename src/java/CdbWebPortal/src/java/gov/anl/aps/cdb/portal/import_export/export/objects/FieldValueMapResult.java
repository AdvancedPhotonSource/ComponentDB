/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects;

import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;

/**
 *
 * @author craig
 */
public class FieldValueMapResult {
    
    private final ValidInfo validInfo;
    private final FieldValueMap valueMap;
    
    public FieldValueMapResult(ValidInfo validInfo, FieldValueMap valueMap) {
        this.validInfo = validInfo;
        this.valueMap = valueMap;
    }

    public ValidInfo getValidInfo() {
        return validInfo;
    }

    public FieldValueMap getValueMap() {
        return valueMap;
    }
    
}
