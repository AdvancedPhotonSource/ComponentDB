/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.controllers.IItemStatusController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeCategoryController;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.controllers.utilities.IItemStatusControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.LocatableStatusItem;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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

    public static void prepareEditInventoryStatus(ItemControllerUtility itemControllerUtility, LocatableStatusItem item, UserInfo user) {
        IItemStatusControllerUtility statusController = (IItemStatusControllerUtility) itemControllerUtility;
        if (statusController.getItemStatusPropertyValue(item) == null) {
            PropertyType inventoryStatusPropertyType = statusController.getInventoryStatusPropertyType();
            PropertyValue preparePropertyTypeValueAdd = null;
            if (user == null) {
                preparePropertyTypeValueAdd = itemControllerUtility.preparePropertyTypeValueAdd(item, inventoryStatusPropertyType);
            } else {
                preparePropertyTypeValueAdd = itemControllerUtility.preparePropertyTypeValueAdd(item, inventoryStatusPropertyType, inventoryStatusPropertyType.getDefaultValue(), null, user);
            }
            item.setInventoryStatusPropertyValue(preparePropertyTypeValueAdd);
        }
    }

    public static PropertyValue getItemStatusPropertyValue(LocatableStatusItem item) {
        return item.getInventoryStatusPropertyValue();
    }

    public static PropertyType getInventoryStatusPropertyType(IItemStatusControllerUtility itemControllerUtility, PropertyTypeFacade propertyTypeFacade) {        

        String statusPropertyTypeName = itemControllerUtility.getStatusPropertyTypeName();
        PropertyType pt = propertyTypeFacade.findByName(statusPropertyTypeName);

        if (pt == null) {
            pt = prepareInventoryStatusPropertyType(itemControllerUtility);
        }
        return pt;
    }

    private static PropertyType prepareInventoryStatusPropertyType(IItemStatusControllerUtility statusController) {
        ItemControllerUtility itemControllerUtility = (ItemControllerUtility) statusController;
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
        allowedDomainList.add(itemControllerUtility.getDefaultDomain());
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
        propertyTypeController.create(true);
        return propertyType;
    }

    public static InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo(IItemStatusControllerUtility itemControllerUtility) {                
        return itemControllerUtility.initializeInventoryStatusPropertyTypeInfo();
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

    public static void updateDefaultStatusProperty(LocatableStatusItem item, UserInfo userInfo, IItemStatusControllerUtility itemControllerUtility) {
        // set default value for status property
        String defaultValue = itemControllerUtility.getInventoryStatusPropertyType().getDefaultValue();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            itemControllerUtility.prepareEditInventoryStatus(item, userInfo);
            item.setInventoryStatusValue(defaultValue);
        }
    }

    public static boolean getRenderedHistoryButton(IItemStatusController itemStatusController) {
        PropertyValue currentStatusPropertyValue = itemStatusController.getCurrentStatusPropertyValue();
        return currentStatusPropertyValue != null;
    }

}
