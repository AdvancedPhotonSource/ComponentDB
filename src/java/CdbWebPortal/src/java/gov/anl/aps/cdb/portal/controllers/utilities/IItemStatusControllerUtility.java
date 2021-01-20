package gov.anl.aps.cdb.portal.controllers.utilities;

/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */

import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;

/**
 *
 * @author darek
 */
public interface IItemStatusControllerUtility {
        
    void prepareEditInventoryStatus(LocatableStatusItem item, UserInfo sessionUser);    
        
    String getStatusPropertyTypeName();
    PropertyValue getItemStatusPropertyValue(LocatableStatusItem item);    
    PropertyType getInventoryStatusPropertyType();
    
    InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo();
    InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo();
    
    
}


