/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.controllers.PropertyValueController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.model.ListDataModel;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
public abstract class ItemMultiEditController extends ItemControllerExtensionHelper {
    
    @EJB
    protected PropertyValueFacade propertyValueFacade; 
    
    @EJB
    protected ItemFacade itemFacade; 

    protected abstract String getControllerNamedConstant();

    protected final String EDIT_MULTIPLE_REDIRECT = "editMultiple?faces-redirect=true";

    protected ListDataModel editableListDataModel;

    protected List<Item> selectedItemsToEdit;

    protected DefaultMenuModel editStepsMenuModel;

    protected int activeIndex = 0;

    protected boolean updateItemIdentifier1 = false;
    protected boolean updateItemIdentifier2 = false;
    protected boolean updateProject = false;
    protected boolean updateDescription = false;
    protected boolean updateQrId = false;

    protected List<PropertyType> selectedPropertyTypesForEditing = null;
    
    protected Map<Integer, Map<Integer, MultiEditPropertyRecord>> multiEditPropertyRecordMap = null;
    
    protected MultiEditPropertyRecord currentMultiEditPropertyRecord = null; 
    protected PropertyValue storedPropertyValueBeforeSingleEditInformation = null; 

    protected enum MultipleEditMenu {
        selection("Selection"),
        updateItems("Update Items");

        private String value;

        private MultipleEditMenu(String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }
    }

    public String getPageTitle() {
        return "Multiple Edit for " + getDisplayEntityTypeName() + "s.";
    }

    public DefaultMenuModel getEditStepsMenuModel() {
        if (editStepsMenuModel == null) {
            editStepsMenuModel = new DefaultMenuModel();

            for (MultipleEditMenu multiEditConstant : MultipleEditMenu.values()) {
                DefaultMenuItem menuItem = new DefaultMenuItem(multiEditConstant.getValue());
                menuItem.setCommand("#{" + getControllerNamedConstant() + ".setActiveIndex(" + multiEditConstant.ordinal() + ")}");
                menuItem.setOnstart("PF('loadingDialog').show();");
                menuItem.setOncomplete("PF('loadingDialog').hide();");
                menuItem.setUpdate("@form");
                
                if (multiEditConstant == MultipleEditMenu.selection) {
                    menuItem.setIcon("fa fa-check-circle-o");
                }
                if (multiEditConstant == MultipleEditMenu.updateItems) {
                    menuItem.setIcon("fa fa-pencil-square-o");
                }
                
                editStepsMenuModel.addElement(menuItem);
            }
        }

        return editStepsMenuModel;
    }

    public void updateSelectedItems() {
        Integer successCounter = 0; 

        for (Item item : selectedItemsToEdit) {            
            try {
                performUpdateOperations(item);
                successCounter ++; 
            } catch (CdbException ex) {
                Logger.getLogger(ItemMultiEditController.class.getName()).log(Level.SEVERE, null, ex);
                addCdbEntityWarningSystemLog("Failed to update", ex, item);
                SessionUtility.addErrorMessage("Error", "Could not update: " + item.toString() + " - " + ex.getErrorMessage());
            } catch (RuntimeException ex) {
                Logger.getLogger(ItemMultiEditController.class.getName()).log(Level.SEVERE, null, ex);
                addCdbEntityWarningSystemLog("Failed to update", ex, item);
                SessionUtility.addErrorMessage("Error", "Could not update: " + item.toString() + " - " + ex.getMessage());
            }
        }
        if(successCounter == selectedItemsToEdit.size()) {
            SessionUtility.addInfoMessage("Success", "Updated all: " + successCounter + " items.");
        } else if(successCounter > 0) {
            SessionUtility.addWarningMessage("Some Items Updated", "Updated " + successCounter + " out of " + selectedItemsToEdit.size() + " items.");
        }
    }

    public void removeSelection(Item item) {
        selectedItemsToEdit.remove(item);
    }

    public String prepareEditMultipleItems() {
        selectedItemsToEdit = new ArrayList<>();
        selectedPropertyTypesForEditing = new ArrayList<>();
        editableListDataModel = null;
        activeIndex = 0;
        currentMultiEditPropertyRecord = null; 
        storedPropertyValueBeforeSingleEditInformation = null;
        return EDIT_MULTIPLE_REDIRECT;
    }

    public ListDataModel getEditableListDataModel() {
        if (editableListDataModel == null) {

            LoginController loginController = LoginController.getInstance();
            if (loginController.isLoggedInAsAdmin()) {
                editableListDataModel = new ListDataModel(getItemList());
            } else {
                UserInfo userInfo = (UserInfo) SessionUtility.getUser();
                editableListDataModel = new ListDataModel(getItemDbFacade().findItemsWithPermissionsOfDomain(userInfo.getId(), getDomainId()));
            }
        }
        return editableListDataModel;
    }
    
    public void updateSingleItem(Item item) {
        setCurrent(item);
        update();
    }

    @Override
    public void savePropertyList() {
        PropertyValue current = PropertyValueController.getInstance().getCurrent();
        if (current != null) {
            List<ItemElement> itemElementList = current.getItemElementList();
            if (itemElementList != null) {
                if (itemElementList.size() > 0) {
                    Item item = itemElementList.get(0).getParentItem();
                    setCurrent(item);
                } else {
                    return;
                }
            }

            super.savePropertyList();
        }
    }
    
    public void addPropertyTypeToRestItems(PropertyType propertyType) {
        for (Item selectedItem : selectedItemsToEdit) {
            MultiEditPropertyRecord multiEditPropertyRecordForItem = getMultiEditPropertyRecordForItem(propertyType, selectedItem);
            if (multiEditPropertyRecordForItem.getPropertyValue() == null) {
                addPropertyValueForItem(multiEditPropertyRecordForItem);
            }
        }
    }

    public MultiEditPropertyRecord getMultiEditPropertyRecordForItem(PropertyType propertyType, Item item) {
        if (item != null && propertyType != null) {
            if (multiEditPropertyRecordMap == null) {            
                multiEditPropertyRecordMap = new HashMap<Integer, Map<Integer, MultiEditPropertyRecord>>();        
            }

            MultiEditPropertyRecord multiEditPropertyRecord = null;

            Map itemPropertyMap = null; 
            if (!multiEditPropertyRecordMap.containsKey(item.getId())) {
                multiEditPropertyRecordMap.put(item.getId(), new HashMap<>()); 
            }  
            
            itemPropertyMap = multiEditPropertyRecordMap.get(item.getId()); 

            if (itemPropertyMap.containsKey(propertyType.getId())) {
                multiEditPropertyRecord = (MultiEditPropertyRecord) itemPropertyMap.get(propertyType.getId()); 
            }
            else {
                multiEditPropertyRecord = new MultiEditPropertyRecord(propertyType, item);
                itemPropertyMap.put(propertyType.getId(), multiEditPropertyRecord); 
            }
            return multiEditPropertyRecord; 
        }
        return null;         
    }
    
    public void addPropertyValueForItem(MultiEditPropertyRecord multiEditPropertyRecord) {
        setCurrent(multiEditPropertyRecord.getItem());
        preparePropertyTypeValueAdd(multiEditPropertyRecord.getPropertyType()); 
        multiEditPropertyRecord.resetRelatedPropertyValueList();
    } 
    
    public void prepareEditSingleProperty(MultiEditPropertyRecord multiEditPropertyRecord) {
        PropertyValue propertyValue = multiEditPropertyRecord.getPropertyValue();        
        storedPropertyValueBeforeSingleEditInformation = new PropertyValue();         
        overridePropertyValues(propertyValue, storedPropertyValueBeforeSingleEditInformation);
        setCurrentMultiEditPropertyRecord(multiEditPropertyRecord);
    }

    @Override
    public void deleteCurrentEditPropertyValue() {
        Item item = currentMultiEditPropertyRecord.getItem();
        PropertyValue propertyValue = currentMultiEditPropertyRecord.getPropertyValue();        
        item.getPropertyValueList().remove(propertyValue);
        
        currentMultiEditPropertyRecord.resetRelatedPropertyValueList();
        currentMultiEditPropertyRecord = null;
    }

    @Override
    public void updateEditProperty() {
        // Perform nothing... When user decides to updated all items, changes will be saved. 
        currentMultiEditPropertyRecord = null;
    }

    @Override
    public void restoreCurrentEditPropertyValueToOriginalState() {
        if (currentMultiEditPropertyRecord != null) {
            PropertyValue propertyValue = currentMultiEditPropertyRecord.getPropertyValue();             
            overridePropertyValues(storedPropertyValueBeforeSingleEditInformation, propertyValue);
        }
        currentMultiEditPropertyRecord = null;
    }
    
    protected void overridePropertyValues(PropertyValue source, PropertyValue destination) {
        destination.setValue(source.getValue());
        destination.setTargetValue("");
        destination.setDisplayValue("");
        destination.setTag(source.getTag());
        destination.setDescription(source.getDescription());
        destination.setUnits(source.getUnits());
        destination.setIsDynamic(source.getIsDynamic());
        destination.setIsUserWriteable(source.getIsUserWriteable());
    }
    
    public void revertItemBackToOriginalState(Item item) {
        int itemIndex = selectedItemsToEdit.indexOf(item);
        int itemId = item.getId(); 
        
        // Remove cached information for item 
        if (multiEditPropertyRecordMap != null) {
            if (multiEditPropertyRecordMap.containsKey(itemId)) {
                multiEditPropertyRecordMap.remove(itemId);
            } 
        }
        
        item = itemFacade.findById(itemId); 
        selectedItemsToEdit.remove(itemIndex);
        selectedItemsToEdit.add(itemIndex, item);                
    }
    
    public void resetAllItemsBackToOriginalState() {
        for (int i = 0; i < selectedItemsToEdit.size(); i++) {
            revertItemBackToOriginalState(selectedItemsToEdit.get(i));
        }
    }
    
    public boolean getRenderPreviousButton() {
        return activeIndex > 0;
    }

    public boolean getRenderNextButton() {
        return activeIndex < MultipleEditMenu.values().length - 1;
    }

    public boolean getRenderUpdateAllButton() {
        return activeIndex == MultipleEditMenu.values().length - 1
                && selectedItemsToEdit.size() > 0;
    }
    
    public boolean getRenderResetAllButton() {
        return getRenderUpdateAllButton(); 
    }

    public void goToNextStep() {
        activeIndex++;
    }

    public void goToPrevStep() {
        activeIndex--;
    }

    public List<Item> getSelectedItemsToEdit() {
        return selectedItemsToEdit;
    }

    public void setSelectedItemsToEdit(List<Item> selectedItemsToEdit) {
        this.selectedItemsToEdit = selectedItemsToEdit;
    }

    public MultiEditPropertyRecord getCurrentMultiEditPropertyRecord() {
        return currentMultiEditPropertyRecord;
    }

    public void setCurrentMultiEditPropertyRecord(MultiEditPropertyRecord currentMultiEditPropertyRecord) {
        this.currentMultiEditPropertyRecord = currentMultiEditPropertyRecord;
    }
    
    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public boolean getRenderSelection() {
        return activeIndex == MultipleEditMenu.selection.ordinal();
    }

    public boolean getRenderUpdateItems() {
        return activeIndex == MultipleEditMenu.updateItems.ordinal();
    }

    public boolean isUpdateItemIdentifier1() {
        return updateItemIdentifier1;
    }

    public void setUpdateItemIdentifier1(boolean updateItemIdentifier1) {
        this.updateItemIdentifier1 = updateItemIdentifier1;
    }

    public boolean isUpdateItemIdentifier2() {
        return updateItemIdentifier2;
    }

    public void setUpdateItemIdentifier2(boolean updateItemIdentifier2) {
        this.updateItemIdentifier2 = updateItemIdentifier2;
    }

    public boolean isUpdateProject() {
        return updateProject;
    }

    public void setUpdateProject(boolean updateProject) {
        this.updateProject = updateProject;
    }

    public boolean isUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(boolean updateDescription) {
        this.updateDescription = updateDescription;
    }

    public boolean isUpdateQrId() {
        return updateQrId;
    }

    public void setUpdateQrId(boolean updateQrId) {
        this.updateQrId = updateQrId;
    }

    public List<PropertyType> getSelectedPropertyTypesForEditing() {
        return selectedPropertyTypesForEditing;
    }

    public void setSelectedPropertyTypesForEditing(List<PropertyType> selectedPropertyTypesForEditign) {
        this.selectedPropertyTypesForEditing = selectedPropertyTypesForEditign;
    }
    
    public void removePropertyTypeForEditing(PropertyType propertyType) {
        this.selectedPropertyTypesForEditing.remove(propertyType); 
    }        
    
    public class MultiEditPropertyRecord {
        
        private PropertyType propertyType = null; 
        private PropertyValue propertyValue = null; 
        private List<PropertyValue> relatedPropertyValueList = null; 
        private Item item = null; 
        private String viewUUID;
                
        public MultiEditPropertyRecord(PropertyType propertyType, Item item) {
            this.propertyType = propertyType; 
            this.item = item; 
        }         

        public List<PropertyValue> getRelatedPropertyValueList() {
            if (relatedPropertyValueList == null) {
                relatedPropertyValueList = new ArrayList<>(); 
                for (PropertyValue propertyValue : item.getPropertyValueList()) {
                    if (propertyValue.getPropertyType().equals(propertyType)) {
                        relatedPropertyValueList.add(propertyValue); 
                    }
                }
            }
            return relatedPropertyValueList;
        }
        
        public void resetRelatedPropertyValueList() {
            relatedPropertyValueList = null;
            propertyValue = null;
        }

        public PropertyValue getPropertyValue() {
            if (propertyValue == null) {
                // Attempt to get a value
                if (getRelatedPropertyValueList().size() > 0) {
                    propertyValue = relatedPropertyValueList.get(0); 
                }
            }
            return propertyValue;
        }

        public void setPropertyValue(PropertyValue propertyValue) {
            this.propertyValue = propertyValue;
        }
        
        public boolean isItemHasMoreThanOneOfSameType() {            
            return getRelatedPropertyValueList().size() > 1;                        
        }

        public PropertyType getPropertyType() {
            return propertyType;
        }

        public Item getItem() {
            return item;
        }
        
        public String getViewUUID() {
        if (viewUUID == null) {
            viewUUID = UUID.randomUUID().toString().replaceAll("[-]", "");
        }
        return viewUUID;
    }
        
    }
    
}
