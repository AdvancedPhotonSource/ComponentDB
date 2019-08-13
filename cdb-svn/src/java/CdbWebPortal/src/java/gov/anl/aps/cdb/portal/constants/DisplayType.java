/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/portal/constants/DisplayType.java $
 *   $Date: 2015-09-08 13:15:53 -0500 (Tue, 08 Sep 2015) $
 *   $Revision: 774 $
 *   $Author: djarosz $
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
