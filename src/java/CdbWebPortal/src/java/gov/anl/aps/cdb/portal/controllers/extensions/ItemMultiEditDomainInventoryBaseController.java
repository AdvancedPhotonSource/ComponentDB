/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDefaultColumnReferences;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryBaseController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainInventoryController;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class ItemMultiEditDomainInventoryBaseController<InventoryDomainEntity extends ItemDomainInventoryBase, InventoryController extends ItemDomainInventoryBaseController> extends ItemMultiEditLocatableItemController {

    private final String REL_PATH_ITEM_STATUS_INPUT = "../../itemStatus/private/applyValuesTo/itemStatusInput.xhtml";

    Integer unitCount = null;

    protected boolean updateInventoryStatus = false;

    private InventoryController inventoryController = null;

    private List<PropertyType> propertyTypesRequiredForMultiCreate = null;
    
    protected abstract InventoryController getInventoryControllerInstance();

    public InventoryController getInventoryController() {
        if (inventoryController == null) {
            inventoryController = getInventoryControllerInstance();
        }
        return inventoryController;
    }

    @Override
    protected ItemController getItemController() {
        return getInventoryController();
    }

    @Override
    public void resetMultiEditVariables() {
        super.resetMultiEditVariables();
        unitCount = null;
    }
    
    protected abstract String generateUnitName(int unitCount);

    @Override
    public Item createItemEntity() {
        Item item = super.createItemEntity();
        // Create a bill of materials for later creating placeholder elements.         
        List<ItemElement> itemElementDisplayList = derivedFromItemForNewItems.getItemElementDisplayList();
        List<ItemElement> newItemItemElementList = item.getFullItemElementList();
        
        UserInfo user = SessionUtility.getUser();

        for (ItemElement catalogItemElement : itemElementDisplayList) {
            ItemElement newItemElement = new ItemElement();
            newItemElement.init(item, catalogItemElement, user);
            newItemItemElementList.add(newItemElement);
        }

        // Auto-assign tag
        if (unitCount == null) {
            unitCount = derivedFromItemForNewItems.getDerivedFromItemList().size() + 1;
        }

        item.setName(generateUnitName(unitCount));
        unitCount++;

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
        if (super.prepareSwitchToUpdateNewItemsActiveIndex()) {
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
                    ItemEnforcedPropertiesController itemEnforcedPropertiesController = 
                            getItemEnforcedPropertiesController();
                    propertyTypesRequiredForMultiCreate = 
                            itemEnforcedPropertiesController.getRequiredPropertyTypeListForItem(selectedItemsToEdit.get(0));

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

    public boolean isUpdateInventoryStatus() {
        return updateInventoryStatus;
    }

    public void setUpdateInventoryStatus(boolean updateInventoryStatus) {
        if (updateInventoryStatus) {
            InventoryController itemController = getInventoryController();
            for (Item item : selectedItemsToEdit) {
                itemController.prepareEditInventoryStatus((ItemDomainInventory) item);
            }
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
            InventoryDomainEntity inventoryItem = (InventoryDomainEntity) item;

            PropertyValue inventoryStatusPropertyValue = inventoryItem.getInventoryStatusPropertyValue();
            PropertyValue mockStatusPV = (PropertyValue) currentObjectValueToColumn;
            inventoryStatusPropertyValue.setValue(mockStatusPV.getValue());
            inventoryStatusPropertyValue.setEffectiveFromDateTime(mockStatusPV.getEffectiveFromDateTime());
        }
    }

    @Override
    public void setCurrentApplyValuesToColumn(ItemDefaultColumnReferences currentApplyValuesToColumn) {
        super.setCurrentApplyValuesToColumn(currentApplyValuesToColumn);

        if (currentApplyValuesToColumn == currentApplyValuesToColumn.inventoryStatus) {
            // Mock property value will hold value and effective date.
            this.currentObjectValueToColumn = new PropertyValue();
            PropertyType statusType = getInventoryController().getInventoryStatusPropertyType();
            ((PropertyValue) this.currentObjectValueToColumn).setPropertyType(statusType);
        }
    }

}
