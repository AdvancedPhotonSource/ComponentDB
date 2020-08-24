package gov.anl.aps.cdb.portal.controllers;

/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;

/**
 *
 * @author darek
 */
public interface IItemStatusController {
    
    void prepareEditInventoryStatus(LocatableStatusItem item);
    void prepareEditInventoryStatus();
    
    boolean getRenderedHistoryButton();
    String getStatusPropertyTypeName();
    PropertyValue getItemStatusPropertyValue(LocatableStatusItem item);
    PropertyValue getCurrentStatusPropertyValue();
    PropertyType getInventoryStatusPropertyType();
    
    InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo();
    InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo();
    
    
}
