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
    TABLE_RECORD_REFERENCE(8),
    FILE_DOWNLOAD(9),
    // To be used in handlers that generate the http link
    GENERATED_HTTP_LINK(10),
    GENERATED_HTTP_LINK_FILE_DOWNLOAD(11),
    MARKDOWN(12);
    
    private final int type;
    private DisplayType(int type) {
        this.type = type;
    }
}
