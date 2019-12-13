/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDefaultColumnReferences;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemMultiEditDomainInventoryController.controllerNamed)
@SessionScoped
public class ItemMultiEditDomainInventoryController extends ItemMultiEditController implements Serializable {
    
    public final static String controllerNamed = "itemMultiEditDomainInventoryController";
    
    private final String REL_PATH_ITEM_STATUS_INPUT = "../../itemDomainInventory/private/applyValuesTo/itemStatusInput.xhtml";
    
    Integer unitCount = null; 
    
    protected boolean updateLocation = false;
    protected boolean updateLocationDetails = false; 
    protected String toggledLocationEditViewUUID = null; 
    
    protected boolean updateInventoryStatus = false; 
    
    private ItemDomainInventoryController itemDomainInventoryController = null; 
    
    private List<PropertyType> propertyTypesRequiredForMultiCreate = null; 

    public ItemDomainInventoryController getItemDomainInventoryController() {
        if (itemDomainInventoryController == null) {
            itemDomainInventoryController = ItemDomainInventoryController.getInstance();
        }
        return itemDomainInventoryController; 
    }

    @Override
    protected ItemController getItemController() {
        return getItemDomainInventoryController(); 
    }

    @Override
    protected String getControllerNamedConstant() {
        return controllerNamed; 
    }
    
    public static ItemMultiEditDomainInventoryController getInstance() {
        return (ItemMultiEditDomainInventoryController) SessionUtility.findBean(controllerNamed);
    }
    
    @Override
    public void resetMultiEditVariables() {
        super.resetMultiEditVariables();
        unitCount = null; 
    }
            

    @Override
    public Item createItemEntity() {
        Item item = super.createItemEntity();
        // Create a bill of materials for later creating placeholder elements.         
        List<ItemElement> itemElementDisplayList = derivedFromItemForNewItems.getItemElementDisplayList();
        List<ItemElement> newItemItemElementList = item.getFullItemElementList();
        
        for (ItemElement catalogItemElement : itemElementDisplayList) {
            ItemElement newItemElement = new ItemElement();
            newItemElement.init(item, catalogItemElement);
            newItemItemElementList.add(newItemElement);            
        }
        
        // Auto-assign tag
        if (unitCount == null) {
            unitCount = derivedFromItemForNewItems.getDerivedFromItemList().size() + 1 ;
        }
        
        item.setName("Unit: " + unitCount);
        unitCount ++; 
        
        return item; 
    }

    @Override
    protected boolean checkCreateConfig() {
        if (derivedFromItemForNewItems == null) {
            SessionUtility.addErrorMessage("No Catalog Item Selected", "Please select a catalog item.");
            return false; 
        }
        return true; 
    }

    @Override
    protected boolean prepareSwitchToUpdateNewItemsActiveIndex() {
        if(super.prepareSwitchToUpdateNewItemsActiveIndex()) {
            List<PropertyType> propertyTypes = getPropertyTypesRequiredForMultiCreate();
            if (propertyTypes != null) {
                setSelectedPropertyTypesForEditing(propertyTypes);
                for (PropertyType propertyType : propertyTypes) {
                    addPropertyTypeToRestItems(propertyType); 
                }
            }
            return true; 
        }
        return false;         
    }
    
    private List<PropertyType> getPropertyTypesRequiredForMultiCreate() {
        if (propertyTypesRequiredForMultiCreate == null) {
            if (selectedPropertyTypesForEditing == null || selectedPropertyTypesForEditing.isEmpty()) {
                if (selectedItemsToEdit != null && !selectedItemsToEdit.isEmpty()) {
                    ItemEnforcedPropertiesController itemEnforcedPropertiesController = getItemEnforcedPropertiesController();
                    propertyTypesRequiredForMultiCreate = itemEnforcedPropertiesController.getRequiredPropertyTypeListForItem(selectedItemsToEdit.get(0));
                    
                    if (propertyTypesRequiredForMultiCreate != null) {
                        // Select appopriate categories for the property type filter view to prevent a automated clear. 
                        List<PropertyTypeCategory> propertyTypeCategories = new ArrayList<>(); 
                        for (PropertyType propertyType : propertyTypesRequiredForMultiCreate) {
                            PropertyTypeCategory propertyTypeCategory = propertyType.getPropertyTypeCategory();
                            if (!propertyTypeCategories.contains(propertyTypeCategory)) {
                                propertyTypeCategories.add(propertyTypeCategory); 
                            }
                        }
                        PropertyTypeController ptcontroller = PropertyTypeController.getInstance();

                        ptcontroller.setFitlerViewSelectedPropertyTypeCategories(propertyTypeCategories);
                        ptcontroller.setFitlerViewSelectedPropertyTypeHandlers(new ArrayList<>());
                    }
                }
            }
        }
        
        return propertyTypesRequiredForMultiCreate; 
    }

    public void setSelectedPropertyTypesForEditing(List<PropertyType> selectedPropertyTypesForEditing) {      
        this.selectedPropertyTypesForEditing = selectedPropertyTypesForEditing;
    }

    public boolean isUpdateLocation() {
        return updateLocation;
    }

    public void setUpdateLocation(boolean updateLocation) {
        this.toggledLocationEditViewUUID = null; 
        this.updateLocation = updateLocation;
    }

    public boolean isUpdateLocationDetails() {
        return updateLocationDetails;
    }

    public void setUpdateLocationDetails(boolean updateLocationDetails) {
        this.updateLocationDetails = updateLocationDetails;
    }

    public String getToggledLocationEditViewUUID() {
        return toggledLocationEditViewUUID;
    }

    public void setToggledLocationEditViewUUID(String toggledLocationEditViewUUID) {
        this.toggledLocationEditViewUUID = toggledLocationEditViewUUID;
    }

    public boolean isUpdateInventoryStatus() {
        return updateInventoryStatus;
    }

    public void setUpdateInventoryStatus(boolean updateInventoryStatus) {
        ItemDomainInventoryController itemController = (ItemDomainInventoryController) getItemController();
        for (Item item : selectedItemsToEdit) {
            itemController.setCurrent((ItemDomainInventory) item);
            itemController.prepareEditInventoryStatus();
        }
                
        this.updateInventoryStatus = updateInventoryStatus;
    }

    @Override
    public String getApplyValuesToEditLink() {
        if (getRenderSpecificInput(ItemDefaultColumnReferences.inventoryStatus)) {
            return REL_PATH_ITEM_STATUS_INPUT; 
        }
        
        return super.getApplyValuesToEditLink();
    }

    @Override
    protected void customApplyValuesForColumn(Item item, ItemDefaultColumnReferences columnReference) {
        if (columnReference == ItemDefaultColumnReferences.inventoryStatus) {
            ItemDomainInventory inventoryItem = (ItemDomainInventory) item;
            inventoryItem.setInventoryStatusValue((String) currentObjectValueToColumn);
        }
    }
    
}
