/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.controllers.IItemStatusController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeCategoryController;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyAllowedValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * Perform standard functions to help implement the IItemStatusController
 * interface
 *
 * @author darek
 */
public class ItemStatusUtility {

    public static void prepareEditInventoryStatus(ItemController itemController, LocatableStatusItem item) {
        IItemStatusController statusController = (IItemStatusController) itemController;
        if (statusController.getItemStatusPropertyValue(item) == null) {
            PropertyType inventoryStatusPropertyType = statusController.getInventoryStatusPropertyType();
            PropertyValue preparePropertyTypeValueAdd = itemController.preparePropertyTypeValueAdd(item, inventoryStatusPropertyType);
            item.setInventoryStatusPropertyValue(preparePropertyTypeValueAdd);
        }
    }

    public static void prepareEditInventoryStatus(ItemController itemController) {
        IItemStatusController statusController = (IItemStatusController) itemController;
        LocatableStatusItem current = (LocatableStatusItem) itemController.getCurrent();
        statusController.prepareEditInventoryStatus(current);
    }

    public static PropertyValue getCurrentStatusPropertyValue(ItemController itemController) {
        IItemStatusController controller = (IItemStatusController) itemController;
        LocatableStatusItem current = (LocatableStatusItem) itemController.getCurrent();
        return controller.getItemStatusPropertyValue(current); 
    }
    
    public static PropertyValue getItemStatusPropertyValue(LocatableStatusItem item) {
        return item.getInventoryStatusPropertyValue();
    }

    public static PropertyType getInventoryStatusPropertyType(ItemController itemController, PropertyTypeFacade propertyTypeFacade, PropertyType cachedInventoryStatusPropertyType) { 
        IItemStatusController statusController = (IItemStatusController) itemController;
        if (cachedInventoryStatusPropertyType == null) {
            String statusPropertyTypeName = statusController.getStatusPropertyTypeName();
            cachedInventoryStatusPropertyType = propertyTypeFacade.findByName(statusPropertyTypeName); 
        }
        if (cachedInventoryStatusPropertyType == null) {
            cachedInventoryStatusPropertyType = prepareInventoryStatusPropertyType(itemController);
        }
        return cachedInventoryStatusPropertyType;
    }
    
    private static PropertyType prepareInventoryStatusPropertyType(ItemController itemController) {  
        IItemStatusController statusController = (IItemStatusController) itemController;
        InventoryStatusPropertyTypeInfo propInfo = statusController.getInventoryStatusPropertyTypeInfo();
        
        PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
        propertyTypeController.prepareCreate();
        PropertyType propertyType = propertyTypeController.getCurrent(); 
        propertyType.setIsInternal(true);
        propertyType.setName(statusController.getStatusPropertyTypeName());
        
        PropertyTypeCategory category = PropertyTypeCategoryController.getInstance().findByName("Status");
        if (category != null) {
            propertyType.setPropertyTypeCategory(category);
        }
        
        List<Domain> allowedDomainList = new ArrayList<>();
        allowedDomainList.add(itemController.getDefaultDomain());
        propertyType.setAllowedDomainList(allowedDomainList);
        
        List<AllowedPropertyValue> apvList = new ArrayList<>();
        for (InventoryStatusPropertyAllowedValue valInfo : propInfo.getValues()) {
            AllowedPropertyValue apv = new AllowedPropertyValue();
            apv.setValue(valInfo.getValue());
            apv.setSortOrder(valInfo.getSortOrder());
            apv.setPropertyType(propertyType);
            apvList.add(apv);
        }
        propertyType.setAllowedPropertyValueList(apvList);
        
        propertyType.setDefaultValue(propInfo.getDefaultValue());

        propertyTypeController.setCurrent(propertyType);
        propertyTypeController.create(true, false); 
        return propertyType; 
    }
    
    public static InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo(IItemStatusController itemController, InventoryStatusPropertyTypeInfo cachedInventoryStatusPropertyTypeInfo) {
        if (cachedInventoryStatusPropertyTypeInfo == null) {
            cachedInventoryStatusPropertyTypeInfo = itemController.initializeInventoryStatusPropertyTypeInfo();
        }
        return cachedInventoryStatusPropertyTypeInfo;        
    }
    
    public static InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo() {
        InventoryStatusPropertyTypeInfo info = new InventoryStatusPropertyTypeInfo();
        info.addValue("Unknown", new Float(1.0));
        info.addValue("Planned", new Float(1.1));
        info.addValue("Requisition Submitted", new Float(2.0));
        info.addValue("Delivered", new Float(3.0));
        info.addValue("Acceptance In Progress", new Float(4.0));
        info.addValue("Accepted", new Float(5.0));
        info.addValue("Rejected", new Float(6.0));
        info.addValue("Post-Acceptance/Test/Certification in Progress", new Float(7.0));
        info.addValue("Ready For Use", new Float(8.0));
        info.addValue("Installed", new Float(9.0));
        info.addValue("Spare", new Float(10.0));
        info.addValue("Spare - Critical", new Float(11.0));
        info.addValue("Failed", new Float(12.0));
        info.addValue("Returned", new Float(13.0));
        info.addValue("Discarded", new Float(14.0));
        
        info.setDefaultValue("Unknown");
        
        return info;
    }
    
    public static void updateDefaultStatusProperty(LocatableStatusItem item, IItemStatusController itemController){
        // set default value for status property
        String defaultValue = itemController.getInventoryStatusPropertyType().getDefaultValue();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            itemController.prepareEditInventoryStatus();
            item.setInventoryStatusValue(defaultValue);
        }
    }
    
    public static boolean getRenderedHistoryButton(IItemStatusController itemStatusController) {
        PropertyValue currentStatusPropertyValue = itemStatusController.getCurrentStatusPropertyValue();
        return currentStatusPropertyValue != null; 
    }

}
