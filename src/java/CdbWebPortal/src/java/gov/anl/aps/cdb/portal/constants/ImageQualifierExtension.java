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
 * Image qualifier extension.
 */
public enum ImageQualifierExtension {
    ORIGINAL(".original"),
    SCALED(".scaled"),
    THUMBNAIL(".thumbnail");
    
    private final String type;
    private ImageQualifierExtension(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
