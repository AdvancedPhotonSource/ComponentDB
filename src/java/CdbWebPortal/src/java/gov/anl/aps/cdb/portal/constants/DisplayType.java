/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
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
    INFO_ACTION(7);
    
    private final int type;
    private DisplayType(int type) {
        this.type = type;
    }
}
