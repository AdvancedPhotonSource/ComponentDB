/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum ItemElementRelationshipTypeNames {
    itemLocation("Location", ItemElementRelationshipTypeNames.LOCATION_ID),
    itemCableConnection("Cable Connection", ItemElementRelationshipTypeNames.CABLE_CONNECTION),
    maarc("MAARC Connection", ItemElementRelationshipTypeNames.MAARC_CONNECTION_ID),
    template("Created From Template", ItemElementRelationshipTypeNames.TEMPLATE_ID),
    control("Controlled By", ItemElementRelationshipTypeNames.CONTROLLED_BY_ID),
    power("Powered By", ItemElementRelationshipTypeNames.POWERED_BY_ID);

    public final static int LOCATION_ID = 1;
    public final static int MAARC_CONNECTION_ID = 2;
    public final static int TEMPLATE_ID = 3; 
    public final static int CABLE_CONNECTION = 4;
    public final static int CONTROLLED_BY_ID = 5;
    public final static int POWERED_BY_ID = 6; 

    private String value;
    private int dbId;

    private ItemElementRelationshipTypeNames(String value, int id) {
        this.value = value;
        this.dbId = id;
    }

    public String getValue() {
        return value;
    }

    public int getDbId() {
        return dbId;
    }
    
};
