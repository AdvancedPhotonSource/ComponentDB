/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.constants;

public enum EntityTypeName {
    template("Template", EntityTypeName.TEMPLATE_ID),
    inventory("Inventory", EntityTypeName.INVENTORY_ID),
    deleted("Deleted", EntityTypeName.DELETED_ID),
    power("Power", EntityTypeName.POWER_ID),
    control("Control", EntityTypeName.CONTROL_ID),
    ioc("IOC", EntityTypeName.IOC_ID);
    
    public final static int TEMPLATE_ID = 5;
    public final static int INVENTORY_ID = 7;
    public final static int DELETED_ID = 8;
    public final static int POWER_ID = 10;
    public final static int CONTROL_ID = 9;         
    public final static int IOC_ID = 11;

    private String value;
    private int dbId; 

    private EntityTypeName(String value, int dbId) {
        this.value = value;
        this.dbId = dbId;
    }   

    public String getValue() {
        return value;
    }

    public int getDbId() {
        return dbId;
    }
};
