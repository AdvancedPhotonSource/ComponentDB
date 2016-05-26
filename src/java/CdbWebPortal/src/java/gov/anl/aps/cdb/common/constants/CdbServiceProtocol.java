/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: https://svn.aps.anl.gov/cdb/trunk/src/java/CdbWebPortal/src/java/gov/anl/aps/cdb/common/constants/CdbServiceProtocol.java $
 *   $Date: 2015-04-17 12:25:03 -0500 (Fri, 17 Apr 2015) $
 *   $Revision: 594 $
 *   $Author: sveseli $
 */
package gov.anl.aps.cdb.common.constants;

/**
 * CDB service protocol enum.
 */
public enum CdbServiceProtocol {

    HTTP("http"),
    HTTPS("https");

    private final String type;

    private CdbServiceProtocol(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static CdbServiceProtocol fromString(String type) {
        CdbServiceProtocol protocol = null;
        switch (type) {
            case "http":
                protocol = HTTP;
                break;
            case "https":
                protocol = HTTPS;
                break;
        }
        return protocol;
    }
}
