/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects;

import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;

/**
 *
 * @author craig
 */
public class CreateInfo {

    private CdbEntity entity;
    private ValidInfo validInfo;

    public CreateInfo(CdbEntity entity, ValidInfo validInfo) {
        this.entity = entity;
        this.validInfo = validInfo;
    }

    public CreateInfo(CdbEntity entity, boolean isValid, String validString) {
        this(entity, new ValidInfo(isValid, validString));
    }

    public CdbEntity getEntity() {
        return entity;
    }

    public ValidInfo getValidInfo() {
        return validInfo;
    }

}
