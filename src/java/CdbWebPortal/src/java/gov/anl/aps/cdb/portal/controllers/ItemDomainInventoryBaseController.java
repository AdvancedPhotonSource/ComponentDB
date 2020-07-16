/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyAllowedValue;
import gov.anl.aps.cdb.portal.view.objects.InventoryStatusPropertyTypeInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemCoreMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author craig
 */
public abstract class ItemDomainInventoryBaseController<ItemInventoryBaseDomainEntity extends ItemDomainInventoryBase, ItemDomainInventoryEntityBaseFacade extends ItemFacadeBase<ItemInventoryBaseDomainEntity>, ItemInventoryEntityBaseSettingsObject extends ItemSettings> extends ItemController<ItemInventoryBaseDomainEntity, ItemDomainInventoryEntityBaseFacade, ItemInventoryEntityBaseSettingsObject>  {
    
    // Inventory status variables
    protected InventoryStatusPropertyTypeInfo inventoryStatusPropertyTypeInfo = null;
    private PropertyType inventoryStatusPropertyType; 

    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    
    protected abstract String getStatusPropertyTypeName();

    @Override
    protected void loadEJBResourcesManually() {
        super.loadEJBResourcesManually(); 
        propertyTypeFacade = PropertyTypeFacade.getInstance();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Inventory status implementation">
    
    protected abstract InventoryStatusPropertyTypeInfo initializeInventoryStatusPropertyTypeInfo();
    
    public InventoryStatusPropertyTypeInfo getInventoryStatusPropertyTypeInfo() {
        if (inventoryStatusPropertyTypeInfo == null) {
            inventoryStatusPropertyTypeInfo = initializeInventoryStatusPropertyTypeInfo();
        }
        return inventoryStatusPropertyTypeInfo;
    }
    
    private PropertyType prepareInventoryStatusPropertyType() {
        InventoryStatusPropertyTypeInfo propInfo = getInventoryStatusPropertyTypeInfo();
        PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
        
        PropertyType propertyType = propertyTypeController.createEntityInstance();
        propertyType.setIsInternal(true);
        propertyType.setName(getStatusPropertyTypeName());
        //propertyType.setDescription(propInfo.getDisplayName());
        
        List<Domain> allowedDomainList = new ArrayList<>();
        allowedDomainList.add(getDefaultDomain());
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

        propertyTypeController.setCurrent(propertyType);
        propertyTypeController.create(true, false); 
        return propertyType; 
    }
        
    public PropertyType getInventoryStatusPropertyType() {
        if (inventoryStatusPropertyType == null) {
            inventoryStatusPropertyType = 
                    propertyTypeFacade.findByName(getStatusPropertyTypeName()); 
        }
        if (inventoryStatusPropertyType == null) {
            inventoryStatusPropertyType = prepareInventoryStatusPropertyType();
        }
        return inventoryStatusPropertyType;
    }        

    public PropertyValue getCurrentStatusPropertyValue() {
        return getCurrent().getInventoryStatusPropertyValue();
    }
    
    public void prepareEditInventoryStatus() {
        if (getCurrentStatusPropertyValue() == null) {
            PropertyValue preparePropertyTypeValueAdd = preparePropertyTypeValueAdd(getInventoryStatusPropertyType()); 
            getCurrent().setInventoryStatusPropertyValue(preparePropertyTypeValueAdd);
        }
    }
    
    public synchronized void prepareEditInventoryStatusFromApi(ItemInventoryBaseDomainEntity item) {
        setCurrent(item);
        prepareEditInventoryStatus();
    }
    
    public boolean getRenderedHistoryButton() {
        return getCurrentStatusPropertyValue() != null; 
    }

    // </editor-fold>        

}
