/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import java.util.List;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueBase;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

/**
 * The extension helper exposes item controller functionality and allows for lightweight controllers to add additional features for items. 
 * 
 * @author djarosz
 */
public abstract class ItemControllerExtensionHelper implements IItemController<Item, ItemSettings> {
    
    protected abstract ItemController getItemController(); 
    
    protected ItemFacadeBase getItemDbFacade() {
        return (ItemFacadeBase) getItemController().getEntityDbFacade(); 
    }
    
    /**
     * Subscribe to cdbEntityController (subscribeResetVariablesForCurrent) and override the method to reset appropriate variables. 
     */
    public void resetExtensionVariablesForCurrent() {
        
    }
    
    /**
     * Subscribe to itemMultiEditExtensionController (subscribeResetVariablesForMultiEdit) and override method to reset appropriate variables. 
     */
    public void resetExtensionVariablesForMultiEdit() {
        
    }
    
    
    /**
     * Subscribe to cdbEntityController (subscribePrepareInsertForCurrent) and override the method to reset appropriate variables. 
     */
    public void prepareInsertForCurrent() {
        
    }
    
    @Override
    public void itemProjectChanged() {
        
    }

    @Override
    public String getDefaultDomainName() {
        return getItemController().getDefaultDomainName(); 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return getItemController().getEntityDisplayItemConnectors(); 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return getItemController().getEntityDisplayItemIdentifier1(); 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return getItemController().getEntityDisplayItemIdentifier2(); 
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return getItemController().getEntityDisplayItemName(); 
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return getItemController().getEntityDisplayItemType();
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return getItemController().getEntityDisplayItemCategory(); 
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return getItemController().getEntityDisplayDerivedFromItem(); 
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return getItemController().getEntityDisplayQrId();
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return getItemController().getEntityDisplayItemGallery(); 
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return getItemController().getEntityDisplayItemLogs();
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return getItemController().getEntityDisplayItemSources(); 
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return getItemController().getEntityDisplayItemProperties(); 
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return getItemController().getEntityDisplayItemElements();
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return getItemController().getEntityDisplayItemsDerivedFromItem();
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return getItemController().getEntityDisplayItemMemberships();
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return getItemController().getEntityDisplayItemProject();
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return getItemController().getEntityDisplayItemEntityTypes();
    }
    
    @Override
    public boolean getEntityDisplayDeletedItems() {
        return getItemController().getEntityDisplayDeletedItems();
    }

    @Override
    public boolean getEntityDisplayConnectorCableEndDesignation() {
        return getItemController().getEntityDisplayConnectorCableEndDesignation();
    }

    @Override
    public String getItemIdentifier1Title() {
        return getItemController().getItemIdentifier1Title();
    }

    @Override
    public String getItemIdentifier2Title() {
        return getItemController().getItemIdentifier2Title(); 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return getItemController().getItemsDerivedFromItemTitle();
    }

    @Override
    public String getDerivedFromItemTitle() {
        return getItemController().getDerivedFromItemTitle();
    }

    @Override
    public String getStyleName() {
        return getItemController().getStyleName(); 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return getItemController().getDefaultDomainDerivedFromDomainName(); 
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return getItemController().getDefaultDomainDerivedToDomainName();
    }
    
    @Override
    public String getNameTitle() {
        return getItemController().getNameTitle();
    }

    @Override
    public String getItemItemTypeTitle() {
        return getItemController().getItemItemTypeTitle();
    }

    @Override
    public String getItemItemCategoryTitle() {
        return getItemController().getItemItemCategoryTitle();
    }

    @Override
    public String getEntityTypeName() {
        return getItemController().getEntityTypeName();
    }
    
    @Override
    public String getListStyleName() {
        return getItemController().getListStyleName();
    }
    
    @Override
    public List<ItemCategory> getDomainItemCategoryList() {
        return getItemController().getDomainItemCategoryList();
    }
    
    @Override
    public boolean itemHasPrimaryImage(Item item) {
        return getItemController().itemHasPrimaryImage(item);
    }

    @Override
    public String getPrimaryImageThumbnailForItem(Item item) {
        return getItemController().getPrimaryImageThumbnailForItem(item);
    }

    @Override
    public String getPrimaryImageValueForItem(Item item) {
        return getItemController().getPrimaryImageValueForItem(item);
    }
    
    public Integer getDomainId() {
        return getItemController().getDomainId(); 
    }
    
    @Override
    public String getItemDisplayString(Item item) {
        return getItemController().getItemDisplayString(item); 
    }
    
    @Override
    public String getItemFavoritesIconStyle(Item item) {
        return getItemController().getItemFavoritesIconStyle(item);
    }
    
    @Override
    public void toggleItemInFavoritesList(Item item) {
        getItemController().toggleItemInFavoritesList(item);
    }
    
    @Override
    public String getDomainControllerName() {
        return getItemController().getDomainControllerName(); 
    }
    
    @Override
    public void checkItemUniqueness(Item entity) throws CdbException {
        getItemController().checkItemUniqueness(entity);
    } 
    
    @Override
    public void setCurrent(Item current) {
        getItemController().setCurrent(current);
    }
    
    @Override
    public Item getCurrent() {
        return (Item) getItemController().getCurrent();
    }
    
    @Override
    public Boolean isItemProjectRequired() {
        return getItemController().isItemProjectRequired();
    }
    
    @Override
    public void checkItemProject(Item item) throws CdbException {
        getItemController().checkItemProject(item);
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return getItemController().getDisplayEntityTypeName(); 
    }
    
    @Override
    public boolean isEntityTypeEditable() {
        return getItemController().isEntityTypeEditable();
    }
    
    @Override
    public String getCurrentItemStyleName() {
        return getItemController().getCurrentItemStyleName(); 
    }
    
    @Override
    public ItemController getDefaultDomainDerivedFromDomainController() {
        return getItemController().getDefaultDomainDerivedFromDomainController();
    }

    @Override
    public ItemController getDefaultDomainDerivedToDomainController() {
        return getItemController().getDefaultDomainDerivedToDomainController(); 
    }
    
    @Override
    public void setCurrentDerivedFromItem(Item derivedFromItem) {
        getItemController().setCurrentDerivedFromItem(derivedFromItem);
    }

    @Override
    public String create() {
        return getItemController().create();
    }    
    
    public final ItemController getSelectionController() {
        return getItemController().getSelectionController(); 
    }

    @Override
    public List getAvailableItemTypesForCurrentItem() {
        return getItemController().getAvailableItemTypesForCurrentItem(); 
    }

    @Override
    public boolean isDisabledItemItemType(Item item) {
        return getItemController().isDisabledItemItemType(item);
    } 
    
    @Override
    public String getItemItemTypeEditString(Item item) {
        return getItemController().getItemItemTypeEditString(item);
    }

    @Override
    public List<ItemType> getAvailableItemTypes(Item item) {
        return getItemController().getAvailableItemTypes(item); 
    }
    
    @Override
    public ItemSettings getSettingObject() {
        return (ItemSettings) getItemController().getSettingObject(); 
    }
    
    @Override
    public DataModel getListDataModel() {
        return getItemController().getListDataModel(); 
    }
    
    @Override
    public List<Item> getFilteredObjectList() {
        return getItemController().getFilteredObjectList(); 
    }
    
    @Override
    public void setFilteredObjectList(List itemList) {
        getItemController().setFilteredObjectList(itemList);
    }
    
    @Override
    public List<PropertyValue> getFilteredPropertyValueList() {
        return getItemController().getFilteredPropertyValueList(); 
    }
    
    @Override
    public void setFilteredPropertyValueList(List propertyValueList) {
        getItemController().setFilteredPropertyValueList(propertyValueList);
    }
    
    @Override
    public SelectItem[] getDomainItemCategoryListForSelectOne() {
        return getItemController().getDomainItemCategoryListForSelectOne(); 
    }
    
    @Override
    public boolean isDisplayRowExpansionForItem(Item item) {
        return getItemController().isDisplayRowExpansionForItem(item);
    }
    
    @Override
    public boolean isDisplayRowExpansionAssembly(Item item) {
        return getItemController().isDisplayRowExpansionAssembly(item);
    }

    @Override
    public boolean isDisplayRowExpansionItemsDerivedFromItem(Item item) {
        return getItemController().isDisplayRowExpansionItemsDerivedFromItem(item);
    }

    @Override
    public boolean isDisplayRowExpansionProperties(Item item) {
        return getItemController().isDisplayRowExpansionProperties(item);
    }

    @Override
    public boolean isDisplayRowExpansionLogs(Item item) {
        return getItemController().isDisplayRowExpansionLogs(item);
    }
    
    public boolean renderRowExpansionContents(Item item) {
        return getItemController().renderRowExpansionContents(item); 
    }
    
    @Override
    public String update() {
        return getItemController().update();
    } 
        
    public void updateWithoutRedirect() {
        getItemController().updateWithoutRedirect();
    }
    
    @Override
    public Boolean getFilterablePropertyValue1() {
        return getItemController().getFilterablePropertyValue1();
    }

    @Override
    public Boolean getFilterablePropertyValue2() {
        return getItemController().getFilterablePropertyValue2();
    }

    @Override
    public Boolean getFilterablePropertyValue3() {
        return getItemController().getFilterablePropertyValue3();
    }

    @Override
    public Boolean getFilterablePropertyValue4() {
        return getItemController().getFilterablePropertyValue4();
    }

    @Override
    public Boolean getFilterablePropertyValue5() {
        return getItemController().getFilterablePropertyValue5();
    }
    
    @Override
    public String getDisplayPropertyTypeName(Integer propertyTypeId) {
        return getItemController().getDisplayPropertyTypeName(propertyTypeId);
    }
    
    @Override
    public String prepareList() {
        return getItemController().prepareList(); 
    }
    
    @Override
    public List<Item> getItemList() {
        return getItemController().getItemList(); 
    }
    
    @Override
    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType) {
        return getItemController().preparePropertyTypeValueAdd(propertyType);
    }
    
    @Override
    public void restoreCurrentEditPropertyValueToOriginalState() {
        getItemController().restoreCurrentEditPropertyValueToOriginalState(); 
    }
    
    @Override
    public void updateEditProperty() {
        getItemController().updateEditProperty(); 
    }
    
    @Override
    public void deleteCurrentEditPropertyValue() {
        getItemController().deleteCurrentEditPropertyValue();
    }
    
    @Override
    public PropertyValue getCurrentEditPropertyValue() {
        return getItemController().getCurrentEditPropertyValue(); 
    }
    
    @Override
    public void setCurrentEditPropertyValue(PropertyValue currentEditPropertyValue) {
        getItemController().setCurrentEditPropertyValue(currentEditPropertyValue);
    }

    @Override
    public void performUpdateOperations(Item entity) throws CdbException, RuntimeException {
        getItemController().performUpdateOperations(entity);
    } 

    @Override
    public void performDestroyOperations(Item entity) throws CdbException, RuntimeException {
        getItemController().performDestroyOperations(entity);
    }
    
    @Override
    public void performCreateOperations(Item item) throws CdbException, RuntimeException {
        getItemController().performCreateOperations(item);
    }
    
    @Override
    public void savePropertyList() {
        getItemController().savePropertyList();
    }
    
    @Override
    public Item createItemEntity() {
        return getItemController().createItemEntity(); 
    }
    
    @Override
    public String getEntityEditRowStyle(Item entity) {
        return getItemController().getEntityEditRowStyle(entity); 
    }
    
    @Override
    public Boolean isItemExistInDb(Item item) {
        return getItemController().isItemExistInDb(item); 
    }
    
    @Override
    public ItemEnforcedPropertiesController getItemEnforcedPropertiesController(){
        return getItemController().getItemEnforcedPropertiesController(); 
    }

    @Override
    public String getEntityApplicationViewPath() {
        return getItemController().getEntityApplicationViewPath(); 
    } 
  
    @Override
    public Boolean getDisplayPropertyMetadata(PropertyValueBase propertyValue) {
        return getItemController().getDisplayPropertyMetadata(propertyValue); 
    }
    
    public List<PropertyTypeCategory> getRelevantPropertyTypeCategories() {
        return getItemController().getRelevantPropertyTypeCategories(); 
    }
    
    public DataModel getTemplateItemsListDataModel() {
        return getItemController().getTemplateItemsListDataModel(); 
    }
    
     public Item getTemplateToCreateNewItem() {
        return getItemController().getTemplateToCreateNewItem();
    }

    public void setTemplateToCreateNewItem(Item templateToCreateNewItem) {
        getItemController().setTemplateToCreateNewItem(templateToCreateNewItem);
    }
    
    public void completeSelectionOfTemplate() {
        getItemController().completeSelectionOfTemplate();
    }
    
    public boolean getDisplayCreatedFromTemplateForCurrent() {
        return getItemController().getDisplayCreatedFromTemplateForCurrent();
    }
    
    public boolean getDisplayCreatedFromCurrentItemList() {
        return getItemController().getDisplayCreatedFromCurrentItemList(); 
    }
    
    public Item getCreatedFromTemplateForCurrentItem() {
        return getItemController().getCreatedFromTemplateForCurrentItem();
    }
    
    public List<Item> getItemsCreatedFromCurrentTemplateItem() {
        return getItemController().getItemsCreatedFromCurrentTemplateItem(); 
    } 
    
    public void deleteProperty(PropertyValue cdbDomainEntityProperty) {
        getItemController().deleteProperty(cdbDomainEntityProperty);
    }
}
