/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

/**
 * Display type enum.
 */
public enum DisplayType {
    FREE_FORM_TEXT(0),
    SELECTED_TEXT(1),
    HTTP_LINK(2),
    IMAGE(3),
    DOCUMENT(4),
    BOOLEAN(5),
    DATE(6),
    INFO_ACTION(7),
    TABLE_RECORD_REFERENCE(8);
    
    private final int type;
    private DisplayType(int type) {
        this.type = type;
    }
}
