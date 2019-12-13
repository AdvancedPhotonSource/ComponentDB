/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDefaultColumnReferences;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.controllers.LoginController;
import gov.anl.aps.cdb.portal.controllers.PropertyValueController;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.ejb.EJB;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.ListDataModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.primefaces.event.ReorderEvent;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;

/**
 *
 * @author djarosz
 */
public abstract class ItemMultiEditController extends ItemControllerExtensionHelper {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Item.class.getName());

    @EJB
    protected PropertyValueFacade propertyValueFacade;

    @EJB
    protected ItemFacade itemFacade;

    protected abstract String getControllerNamedConstant();

    protected final String EDIT_MULTIPLE_REDIRECT = "editMultiple?faces-redirect=true";
    protected final String CREATE_MULTIPLE_REDIRECT = "createMultiple?faces-redirect=true";
    
    protected final String REL_PATH_EMPTY_PAGE = "../../common/private/commonEmptyPage.xhtml";

    protected ListDataModel editableListDataModel;

    protected List<Item> selectedItemsToEdit;

    protected DefaultMenuModel editStepsMenuModel;
    protected DefaultMenuModel createStepsMenuModel;

    protected int activeIndex = 0;

    protected int createNewItemCount = 0;

    protected Item derivedFromItemForNewItems = null;

    protected EntityInfo newItemEntityInfo = null;

    protected boolean updateItemIdentifier1 = false;
    protected boolean updateItemIdentifier2 = false;
    protected boolean updateProject = false;
    protected boolean updateDescription = false;
    protected boolean updateQrId = false;
    protected boolean updateItemType = false; 
    protected boolean updateItemCategory = false; 

    protected boolean updateOwnerUser = false;
    protected boolean updateOwnerGroup = false;
    protected boolean updateGroupWriteable = false;

    protected MultiEditMode multiEditMode = null;
    protected List<ItemProject> newItemAssignDefaultProject = null;

    protected List<PropertyType> selectedPropertyTypesForEditing = null;

    protected Map<String, Map<Integer, MultiEditPropertyRecord>> multiEditPropertyRecordMap = null;

    protected MultiEditPropertyRecord currentMultiEditPropertyRecord = null;
    protected PropertyValue storedPropertyValueBeforeSingleEditInformation = null;

    protected ItemDefaultColumnReferences currentApplyValuesToColumn = null;
    protected String currentPrefixValueToColumn = null;
    protected String currentPostfixValueToColumn = null;
    protected Integer currentSequenceStartValueToColumn = null;
    protected List<Object> currentObjectListValueToColumn = null;
    protected String currentObjectListStringRep = null;
    protected Object currentObjectValueToColumn = null;
    protected PropertyValue currentMockPropertyValueApplyValuesToColumn = null;
    protected boolean isInputValueDialogOpen;

    protected boolean renderMultiCreateConfigurationDialog = false;

    private Set<ItemControllerExtensionHelper> subscribedResetForMultiEditControllerHelpers;

    public ItemMultiEditController() {
        super();
        subscribedResetForMultiEditControllerHelpers = new HashSet<>();
    }

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

    protected enum MultipleCreateMenu {
        createConfig("Create Configuration"),
        updateNewItems("Update New Items");

        private String value;

        private MultipleCreateMenu(String value) {
            this.value = value;
        }

        public final String getValue() {
            return value;
        }
    }

    protected enum MultiEditMode {
        update(),
        create(),
        delete();

        private MultiEditMode() {
        }
    }

    public String getPageTitle() {
        return "Multiple Edit for " + getDisplayEntityTypeName() + "s.";
    }

    public DefaultMenuModel getEditStepsMenuModel() {
        if (editStepsMenuModel == null) {
            editStepsMenuModel = new DefaultMenuModel();

            for (MultipleEditMenu multiEditConstant : MultipleEditMenu.values()) {
                DefaultMenuItem menuItem = createMenuModelMenuItem(multiEditConstant.getValue(), multiEditConstant.ordinal());

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

    public DefaultMenuModel getCreateStepsMenuModel() {
        if (createStepsMenuModel == null) {
            createStepsMenuModel = new DefaultMenuModel();

            for (MultipleCreateMenu multipleCreateConstant : MultipleCreateMenu.values()) {
                DefaultMenuItem menuItem = createMenuModelMenuItem(multipleCreateConstant.getValue(), multipleCreateConstant.ordinal());

                if (multipleCreateConstant == MultipleCreateMenu.updateNewItems) {
                    menuItem.setIcon("fa fa-pencil-square-o");
                }
                createStepsMenuModel.addElement(menuItem);
            }
        }
        return createStepsMenuModel;
    }

    private DefaultMenuItem createMenuModelMenuItem(String constantValue, int ordinal) {
        DefaultMenuItem menuItem = new DefaultMenuItem(constantValue);
        menuItem.setCommand("#{" + getControllerNamedConstant() + ".setActiveIndex(" + ordinal + ")}");
        menuItem.setOnstart("PF('loadingDialog').show();");
        menuItem.setOncomplete("PF('loadingDialog').hide();");
        menuItem.setUpdate("@form");

        return menuItem;
    }

    public void performDeleteOperationsOnAllItems() {
        int beforeDeletionCount = selectedItemsToEdit.size();

        MultiEditMode ittrMode = MultiEditMode.delete;

        List<Integer> indexesToRemoveFromList = new ArrayList<>();

        for (int i = selectedItemsToEdit.size() - 1; i >= 0; i--) {
            Item item = selectedItemsToEdit.get(i);
            if (isItemExistInDb(item)) {
                try {
                    performDestroyOperations(item);
                    indexesToRemoveFromList.add(i);
                } catch (CdbException ex) {
                    processDatabaseOperationsException(ex, item, ittrMode);
                } catch (RuntimeException ex) {
                    processDatabaseOperationsException(ex, item, ittrMode);
                }
            } else {
                indexesToRemoveFromList.add(i);
            }
        }

        for (int indexToRemove : indexesToRemoveFromList) {
            selectedItemsToEdit.remove(indexToRemove);
        }

        // reset data model to select from
        editableListDataModel = null;

        List<Item> newSelectedItems = new ArrayList<>();
        // Update the references of selected items that are left over after deletion completion

        // TODO check if in multi-edit mode 
        for (Item oldSelectedItemToEdit : selectedItemsToEdit) {
            Iterator<Item> editableListIterator = getEditableListDataModel().iterator();
            Item item = null;
            while (editableListIterator.hasNext() && item == null) {
                Item newItem = editableListIterator.next();
                if (newItem.equals(oldSelectedItemToEdit)) {
                    newItem.setPersitanceErrorMessage(oldSelectedItemToEdit.getPersitanceErrorMessage());
                    item = newItem;
                }
            }
            if (item != null) {
                newSelectedItems.add(item);
            } else {
                newSelectedItems.add(oldSelectedItemToEdit);
            }
        }

        selectedItemsToEdit = newSelectedItems;

        int successDeleteCounter = indexesToRemoveFromList.size();
        if (successDeleteCounter == beforeDeletionCount) {
            String message = "";
            message += successDeleteCounter + " items were deleted. ";

            SessionUtility.addInfoMessage("Success", message);
        } else {
            SessionUtility.addWarningMessage("Warning", "Some Items were not deleted: "
                    + successDeleteCounter
                    + " out of "
                    + beforeDeletionCount + " items were deleted");
        }
    }

    public boolean performSaveOperationsOnItem(Item item) {
        MultiEditMode ittrMode = null;

        try {
            if (isItemExistInDb(item)) {
                ittrMode = MultiEditMode.update;
                performUpdateOperations(item);
                return true;
            } else {
                ittrMode = MultiEditMode.create;
                performCreateOperations(item);
                // Get refetched item with everything initalized. 
                item = getCurrent();
                return true;
            }
        } catch (CdbException ex) {
            processDatabaseOperationsException(ex, item, ittrMode);
        } catch (RuntimeException ex) {
            processDatabaseOperationsException(ex, item, ittrMode);
        }
        return false;
    }

    public void performSaveOperationsOnItems() {
        int successUpdateCounter = 0;
        int successCreateCounter = 0;

        for (int i = 0; i < selectedItemsToEdit.size(); i++) {
            Item item = selectedItemsToEdit.get(i);
            
            if (isItemExistInDb(item)) {
                if (performSaveOperationsOnItem(item)) {
                    successUpdateCounter++;
                }
            } else if (performSaveOperationsOnItem(item)) {
                successCreateCounter++;
            }
            
            // Reload the updated item. 
            Item updatedItem = getCurrent();
            selectedItemsToEdit.remove(i);
            selectedItemsToEdit.add(i, updatedItem); 
        }

        // Summary message
        int totalSaved = successCreateCounter + successUpdateCounter;

        if (totalSaved == selectedItemsToEdit.size()) {
            String message = "";
            if (successCreateCounter != 0) {
                message += successCreateCounter + " items were created. ";
            }
            if (successUpdateCounter != 0) {
                message += successUpdateCounter + " items were updated. ";
            }
            SessionUtility.addInfoMessage("Success", message);
        } else {
            SessionUtility.addWarningMessage("Warning", "Some Items were not saved: " + totalSaved + " out of " + selectedItemsToEdit.size() + " items were saved");
        }
    }

    private void processDatabaseOperationsException(Exception ex, Item item, MultiEditMode mode) {
        String actionWord = "";
        if (mode == MultiEditMode.create) {
            actionWord = "create";
        } else if (mode == MultiEditMode.update) {
            actionWord = "update";
        } else if (mode == MultiEditMode.delete) {
            actionWord = "delete";
        }
        String exceptionMessage;
        if (ex instanceof RuntimeException) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            if (t != null) {
                exceptionMessage = t.getMessage();
            } else {
                exceptionMessage = ex.getMessage();
            }
        } else {
            exceptionMessage = ex.getMessage();
        }

        logger.error("Error performing a " + actionWord + " on item: " + ex);
        addCdbEntityWarningSystemLog("Failed to " + actionWord, ex, item);
        SessionUtility.addErrorMessage("Error", "Could not " + actionWord + ": " + item.toString() + " - " + exceptionMessage);
    }

    public void removeSelection(Item item) {
        int index = getSelectedItemIndex(item);
        selectedItemsToEdit.remove(index);
    }

    public String prepareEditMultipleItems() {
        resetMultiEditVariables();
        multiEditMode = MultiEditMode.update;
        return EDIT_MULTIPLE_REDIRECT;
    }

    public String prepareCreateMultipleItems() {
        resetMultiEditVariables();
        multiEditMode = MultiEditMode.create;
        return CREATE_MULTIPLE_REDIRECT;
    }

    public void prepareCreateMultipleItemsFromDialog(Item derivedFromNewItems) {
        prepareCreateMultipleItemsFromDialog();
        this.derivedFromItemForNewItems = derivedFromNewItems;
    }

    public void prepareCreateMultipleItemsFromDialog() {
        prepareCreateMultipleItems();
        renderMultiCreateConfigurationDialog = true;
    }

    public void continueToCreateMultipleItemsFromDialog() {
        setActiveIndex(MultipleCreateMenu.updateNewItems.ordinal());
        String desiredPath = getEntityApplicationViewPath() + "/" + CREATE_MULTIPLE_REDIRECT;
        SessionUtility.navigateTo(desiredPath);
        renderMultiCreateConfigurationDialog = false;
    }
    
    public void editAllItemsDerivedFromItem(Item item) {
        resetMultiEditVariables();
        setActiveIndex(MultipleEditMenu.updateItems.ordinal());
        multiEditMode = MultiEditMode.update; 
               
        LoginController loginController = LoginController.getInstance();
        List<Item> derivedFromItemList = item.getDerivedFromItemList();
        selectedItemsToEdit = new ArrayList<>(); 
        for (Item derivedItem : derivedFromItemList) {
            if (loginController.isEntityWriteable(derivedItem.getEntityInfo())) {
                selectedItemsToEdit.add(derivedItem);
            }
        }
                
        String desiredPath = getEntityApplicationViewPath() + "/" + EDIT_MULTIPLE_REDIRECT; 
        SessionUtility.navigateTo(desiredPath);
    }

    protected void resetMultiEditVariables() {
        multiEditMode = null;
        selectedItemsToEdit = new ArrayList<>();
        selectedPropertyTypesForEditing = new ArrayList<>();
        editableListDataModel = null;
        activeIndex = 0;
        createNewItemCount = 0;
        derivedFromItemForNewItems = null;
        newItemEntityInfo = null;
        currentMultiEditPropertyRecord = null;
        storedPropertyValueBeforeSingleEditInformation = null;
        newItemAssignDefaultProject = null;
        isInputValueDialogOpen = false;
        renderMultiCreateConfigurationDialog = false;

        for (ItemControllerExtensionHelper helper : subscribedResetForMultiEditControllerHelpers) {
            helper.resetExtensionVariablesForMultiEdit();
        }
    }

    public void subscribeResetForMultiEdit(ItemControllerExtensionHelper helper) {
        subscribedResetForMultiEditControllerHelpers.add(helper);
    }

    public int getMinNewItemsToCreate() {
        return selectedItemsToEdit.size();
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

    public void createSingleItem(Item item) {
        setCurrent(item);
        create();
    }

    @Override
    public void savePropertyList() {
        PropertyValue current = PropertyValueController.getInstance().getCurrent();
        if (current != null) {
            Item item = null;

            List<ItemElement> itemElementList = current.getItemElementList();
            if (itemElementList != null) {
                if (itemElementList.size() > 0) {
                    item = itemElementList.get(0).getParentItem();
                    setCurrent(item);
                } else {
                    return;
                }
            }
            if (item != null) {
                super.savePropertyList();
            }
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
                multiEditPropertyRecordMap = new HashMap<String, Map<Integer, MultiEditPropertyRecord>>();
            }

            MultiEditPropertyRecord multiEditPropertyRecord = null;

            Map itemPropertyMap = null;
            if (!multiEditPropertyRecordMap.containsKey(item.getViewUUID())) {
                multiEditPropertyRecordMap.put(item.getViewUUID(), new HashMap<>());
            }

            itemPropertyMap = multiEditPropertyRecordMap.get(item.getViewUUID());

            if (itemPropertyMap.containsKey(propertyType.getId())) {
                multiEditPropertyRecord = (MultiEditPropertyRecord) itemPropertyMap.get(propertyType.getId());
            } else {
                multiEditPropertyRecord = new MultiEditPropertyRecord(propertyType, item);
                itemPropertyMap.put(propertyType.getId(), multiEditPropertyRecord);
            }
            return multiEditPropertyRecord;
        }
        return null;
    }

    public void addPropertyValueForItem(MultiEditPropertyRecord multiEditPropertyRecord) {
        Item item = multiEditPropertyRecord.getItem();
        if (item.getPropertyValueList() == null) {
            item.setPropertyValueList(new ArrayList<>());
        }
        setCurrent(item);
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

    @Override
    public Item createItemEntity() {
        Item item = super.createItemEntity();
        EntityInfo entityInfo = item.getEntityInfo();
        entityInfo.setOwnerUser(newItemEntityInfo.getOwnerUser());
        entityInfo.setOwnerUserGroup(newItemEntityInfo.getOwnerUserGroup());
        entityInfo.setIsGroupWriteable(newItemEntityInfo.getIsGroupWriteable());
        item.setEntityInfo(entityInfo);
        item.setItemProjectList(newItemAssignDefaultProject);
        item.setDerivedFromItem(derivedFromItemForNewItems);
        return item;
    }

    public void revertItemBackToOriginalState(Item item) {
        int itemIndex = getSelectedItemIndex(item);

        // Remove cached information for item 
        if (multiEditPropertyRecordMap != null) {
            if (multiEditPropertyRecordMap.containsKey(item.getViewUUID())) {
                multiEditPropertyRecordMap.remove(item.getViewUUID());
            }
        }

        Integer itemId = item.getId();
        if (itemId != null) {
            item = itemFacade.findById(itemId);
        } else {
            item = createItemEntity();
        }

        selectedItemsToEdit.remove(itemIndex);
        selectedItemsToEdit.add(itemIndex, item);
    }

    private int getSelectedItemIndex(Item item) {
        int itemIndex = -1;
        for (int i = 0; i < selectedItemsToEdit.size(); i++) {
            Item ittrItem = selectedItemsToEdit.get(i);
            // equals method is not valid when editing many items. Memory based equals is more accurate. 
            if (ittrItem == item) {
                itemIndex = i;
                break;
            }
        }
        return itemIndex;
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
        if (MultiEditMode.update == multiEditMode) {
            return activeIndex < MultipleEditMenu.values().length - 1;
        } else if (MultiEditMode.create == multiEditMode) {
            return activeIndex < MultipleCreateMenu.values().length - 1;
        }

        return false;
    }

    public Boolean getRenderSaveAllButton() {
        if (MultiEditMode.update == multiEditMode) {
            return activeIndex == MultipleEditMenu.values().length - 1
                    && selectedItemsToEdit.size() > 0;
        } else if (MultiEditMode.create == multiEditMode) {
            return activeIndex == MultipleCreateMenu.values().length - 1
                    && selectedItemsToEdit.size() > 0;
        }
        return false;
    }

    public void setActiveIndex(int activeIndex) {
        if (multiEditMode == MultiEditMode.create) {
            if (activeIndex == MultipleCreateMenu.updateNewItems.ordinal()) {
                if (!prepareSwitchToUpdateNewItemsActiveIndex()) {
                    return;
                }
            }
        }

        this.activeIndex = activeIndex;
    }

    /**
     * Performs checks and preparations to proceed to switch to update new
     * items.
     *
     * @return true to proceed with the switch
     */
    protected boolean prepareSwitchToUpdateNewItemsActiveIndex() {
        if (!checkCreateConfig()) {
            return false;
        }

        if (selectedItemsToEdit == null) {
            selectedItemsToEdit = new ArrayList<>();
        }
        int newItemsNeeded = createNewItemCount - selectedItemsToEdit.size();
        for (int i = 0; i < newItemsNeeded; i++) {
            Item newItem = createItemEntity();
            selectedItemsToEdit.add(newItem);
        }
        return true;
    }

    protected boolean checkCreateConfig() {
        return true;
    }

    public boolean getRenderDeleteAllButton() {
        return getRenderSaveAllButton();
    }

    public boolean getRenderResetAllButton() {
        return getRenderSaveAllButton();
    }

    public void goToNextStep() {
        setActiveIndex(activeIndex + 1);
    }

    public void goToPrevStep() {
        setActiveIndex(activeIndex - 1);
    }

    public List<Item> getSelectedItemsToEdit() {
        return selectedItemsToEdit;
    }

    public void setSelectedItemsToEdit(List<Item> selectedItemsToEdit) {
        this.selectedItemsToEdit = selectedItemsToEdit;
    }

    public void onRowReorder(ReorderEvent event) {
        int index = event.getFromIndex();
        Item item = this.selectedItemsToEdit.remove(index);
        int toIndex = event.getToIndex();
        this.selectedItemsToEdit.add(toIndex, item);
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

    public boolean isDefaultValuesForNewItemsEditable() {
        return selectedItemsToEdit != null && selectedItemsToEdit.size() > 0;
    }

    public boolean getRenderSelection() {
        return activeIndex == MultipleEditMenu.selection.ordinal();
    }

    public boolean getRenderUpdateItems() {
        return activeIndex == MultipleEditMenu.updateItems.ordinal();
    }

    public boolean getRenderCreateConfiguration() {
        return activeIndex == MultipleCreateMenu.createConfig.ordinal();
    }

    public boolean getRenderUpdateNewItems() {
        return activeIndex == MultipleCreateMenu.updateNewItems.ordinal();
    }

    public int getCreateNewItemCount() {
        return createNewItemCount;
    }

    public void setCreateNewItemCount(int createNewItemCount) {
        this.createNewItemCount = createNewItemCount;
    }

    public EntityInfo getNewItemEntityInfo() {
        if (newItemEntityInfo == null) {
            newItemEntityInfo = EntityInfoUtility.createEntityInfo();
        }
        return newItemEntityInfo;
    }

    public void setNewItemEntityInfo(EntityInfo newItemEntityInfo) {
        this.newItemEntityInfo = newItemEntityInfo;
    }

    public List<ItemProject> getNewItemAssignDefaultProject() {
        return newItemAssignDefaultProject;
    }

    public void setNewItemAssignDefaultProject(List<ItemProject> newItemAssignDefaultProject) {
        this.newItemAssignDefaultProject = newItemAssignDefaultProject;
    }

    public String getNewItemAssignedDefaultProjectLabel() {
        if (newItemAssignDefaultProject != null) {
            if (!newItemAssignDefaultProject.isEmpty()) {
                return StringUtility.getStringifyCdbList(newItemAssignDefaultProject);
            }
        }

        return "Please select default project for all new items.";
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

    public boolean isUpdateItemType() {
        return updateItemType;
    }

    public void setUpdateItemType(boolean updateItemType) {
        this.updateItemType = updateItemType;
    }

    public boolean isUpdateItemCategory() {
        return updateItemCategory;
    }

    public void setUpdateItemCategory(boolean updateItemCategory) {
        this.updateItemCategory = updateItemCategory;
    }

    public boolean isUpdateQrId() {
        return updateQrId;
    }

    public void setUpdateQrId(boolean updateQrId) {
        this.updateQrId = updateQrId;
    }

    public boolean isUpdateOwnerUser() {
        return updateOwnerUser;
    }

    public void setUpdateOwnerUser(boolean updateOwnerUser) {
        this.updateOwnerUser = updateOwnerUser;
    }

    public boolean isUpdateOwnerGroup() {
        return updateOwnerGroup;
    }

    public void setUpdateOwnerGroup(boolean updateOwnerGroup) {
        this.updateOwnerGroup = updateOwnerGroup;
    }

    public boolean isUpdateGroupWriteable() {
        return updateGroupWriteable;
    }

    public void setUpdateGroupWriteable(boolean updateGroupWriteable) {
        this.updateGroupWriteable = updateGroupWriteable;
    }

    public Item getDerivedFromItemForNewItems() {
        return derivedFromItemForNewItems;
    }

    public void setDerivedFromItemForNewItems(Item derivedFromItemForNewItems) {
        this.derivedFromItemForNewItems = derivedFromItemForNewItems;
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

    protected boolean getRenderSpecificInput(ItemDefaultColumnReferences idcr) {
        if (this.currentApplyValuesToColumn != null) {
            return this.currentApplyValuesToColumn == idcr;
        }
        return false;
    }

    public boolean getRenderGroupWriteableInput() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.groupWriteable);
    }

    public boolean getRenderOwnerGroupInput() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.ownerGroup);
    }

    public boolean getRenderOwnerUserInput() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.ownerUser);
    }

    public boolean getRenderNumberInput() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.qrId);
    }

    public boolean getRenderLargeTextInput() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.description);
    }

    public boolean getRenderItemProjectInputValue() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.itemProject);
    }

    public boolean getRenderPropertyInputValue() {
        return getRenderSpecificInput(ItemDefaultColumnReferences.property);
    }

    public boolean isRenderMultiCreateConfigurationDialog() {
        return renderMultiCreateConfigurationDialog;
    }

    public void setRenderMultiCreateConfigurationDialog(boolean renderMultiCreateConfigurationDialog) {
        this.renderMultiCreateConfigurationDialog = renderMultiCreateConfigurationDialog;
    }

    public boolean getRenderSimpleTextInputValue() {
        if (this.currentApplyValuesToColumn != null) {
            if (this.currentApplyValuesToColumn == ItemDefaultColumnReferences.name) {
                return true;
            } else if (this.currentApplyValuesToColumn == ItemDefaultColumnReferences.itemIdentifier1) {
                return true;
            } else if (this.currentApplyValuesToColumn == ItemDefaultColumnReferences.itemIdentifier2) {
                return true;
            }
        }

        return false;
    }

    public boolean getDisplayColumnApplyValuesConfiguration(ItemDefaultColumnReferences columnKey) {
        if (columnKey == ItemDefaultColumnReferences.derivedFromItem) {
            return false;
        }

        return true;
    }

    public void populateValuesForColumn() {
        isInputValueDialogOpen = false;
        for (Item item : selectedItemsToEdit) {
            switch (currentApplyValuesToColumn) {
                case name:
                    item.setName(getNextValueForCurrentSequence());
                    break;
                case itemIdentifier1:
                    item.setItemIdentifier1(getNextValueForCurrentSequence());
                    break;
                case itemIdentifier2:
                    item.setItemIdentifier2(getNextValueForCurrentSequence());
                    break;
                case itemProject:
                    item.setItemProjectList((List<ItemProject>) (List<?>) currentObjectListValueToColumn);
                    break;
                case description:
                    item.setDescription((String) currentObjectValueToColumn);
                    break;
                case qrId:
                    item.setQrId(currentSequenceStartValueToColumn);
                    currentSequenceStartValueToColumn++;
                    break;
                case ownerUser:
                    item.getEntityInfo().setOwnerUser((UserInfo) currentObjectValueToColumn);
                    break;
                case ownerGroup:
                    item.getEntityInfo().setOwnerUserGroup((UserGroup) currentObjectValueToColumn);
                    break;
                case groupWriteable:
                    item.getEntityInfo().setIsGroupWriteable((Boolean) currentObjectValueToColumn);
                    break;
                case property:
                    PropertyType currentPropertyType = currentMockPropertyValueApplyValuesToColumn.getPropertyType();
                    MultiEditPropertyRecord mepr = getMultiEditPropertyRecordForItem(currentPropertyType, item);
                    PropertyValue value = mepr.getPropertyValue();
                    if (value != null) {
                        value.setValue(currentMockPropertyValueApplyValuesToColumn.getValue());
                    }
                    break;
                default:
                    customApplyValuesForColumn(item, currentApplyValuesToColumn);
                    break;
            }
        }
    }
    
    protected void customApplyValuesForColumn(Item item, ItemDefaultColumnReferences columnReference) {
        // Override this for additional domain specific values. 
    }
    
    public String getApplyValuesToEditLink() {
        if (getRenderItemProjectInputValue()) {
            return "applyValuesTo/itemProjectInput.xhtml";
        } else if (getRenderSimpleTextInputValue()) {
            return "applyValuesTo/simpleTextInput.xhtml";
        } else if (getRenderLargeTextInput()) {
            return "applyValuesTo/itemLargeTextInput.xhtml"; 
        } else if (getRenderNumberInput()) {
            return "applyValuesTo/numberInput.xhtml"; 
        } else if (getRenderOwnerUserInput()) {
            return "applyValuesTo/entityInfoOwnerUserInput.xhtml";
        } else if (getRenderOwnerGroupInput()) {
            return "applyValuesTo/entityInfoOwnerGroupInput.xhtml";
        } else if (getRenderGroupWriteableInput()) {
            return "applyValuesTo/entityInfoGroupWriteableInput.xhtml"; 
        } else if (getRenderPropertyInputValue()) {
            return "applyValuesTo/propertyValueInput.xhtml";
        } else {
            return REL_PATH_EMPTY_PAGE; 
        }
    }

    private String getNextValueForCurrentSequence() {
        String newValue = currentPrefixValueToColumn;
        if (getHasSequenceValueToSet()) {
            newValue += currentSequenceStartValueToColumn;
            newValue += currentPostfixValueToColumn;
            currentSequenceStartValueToColumn++;
        }
        return newValue;
    }

    public void setCurrentApplyValuesToColumn(ItemDefaultColumnReferences currentApplyValuesToColumn,
            PropertyType propertyTypesApplyValuesTo) {
        currentMockPropertyValueApplyValuesToColumn = new PropertyValue();
        currentMockPropertyValueApplyValuesToColumn.setPropertyType(propertyTypesApplyValuesTo);
        setCurrentApplyValuesToColumn(currentApplyValuesToColumn);
        currentMockPropertyValueApplyValuesToColumn.setValue("");
    }

    public void setCurrentApplyValuesToColumn(ItemDefaultColumnReferences currentApplyValuesToColumn) {
        this.currentApplyValuesToColumn = currentApplyValuesToColumn;
        this.isInputValueDialogOpen = true;
        this.currentPrefixValueToColumn = null;
        this.currentPostfixValueToColumn = null;
        this.currentSequenceStartValueToColumn = null;
        this.currentObjectListValueToColumn = null;
        this.currentObjectListStringRep = null;
        this.currentObjectValueToColumn = null;
    }

    public void handleCloseInputValueDialog(AjaxBehaviorEvent event) {
        setIsInputValueDialogOpen(false);
    }

    public boolean isIsInputValueDialogOpen() {
        return isInputValueDialogOpen;
    }

    public void setIsInputValueDialogOpen(boolean isInputValueDialogOpen) {
        this.isInputValueDialogOpen = isInputValueDialogOpen;
    }

    public PropertyValue getCurrentMockPropertyValueApplyValuesToColumn() {
        return currentMockPropertyValueApplyValuesToColumn;
    }

    public void setCurrentMockPropertyValueApplyValuesToColumn(PropertyValue currentMockPropertyValueApplyValuesToColumn) {
        this.currentMockPropertyValueApplyValuesToColumn = currentMockPropertyValueApplyValuesToColumn;
    }

    public String getCurrentPrefixValueToColumn() {
        return currentPrefixValueToColumn;
    }

    public void setCurrentPrefixValueToColumn(String currentPrefixValueToColumn) {
        this.currentPrefixValueToColumn = currentPrefixValueToColumn;
    }

    public String getCurrentPostfixValueToColumn() {
        return currentPostfixValueToColumn;
    }

    public void setCurrentPostfixValueToColumn(String currentPostfixValueToColumn) {
        this.currentPostfixValueToColumn = currentPostfixValueToColumn;
    }

    public Integer getCurrentSequenceStartValueToColumn() {
        return currentSequenceStartValueToColumn;
    }

    public void setCurrentSequenceStartValueToColumn(Integer currentSequenceStartValueToColumn) {
        this.currentSequenceStartValueToColumn = currentSequenceStartValueToColumn;
    }

    public boolean getHasSequenceValueToSet() {
        return currentSequenceStartValueToColumn != null;
    }

    public List<Object> getCurrentObjectListValueToColumn() {
        return currentObjectListValueToColumn;
    }

    public void setCurrentObjectListValueToColumn(List<Object> currentObjectListValueToColumn) {
        currentObjectListStringRep = null;
        this.currentObjectListValueToColumn = currentObjectListValueToColumn;
    }

    public String getCurrentObjectListStringRep() {
        if (currentObjectListStringRep == null) {
            currentObjectListStringRep = StringUtility.getStringifyCdbList(currentObjectListValueToColumn);
        }
        return currentObjectListStringRep;
    }

    public Object getCurrentObjectValueToColumn() {
        return currentObjectValueToColumn;
    }

    public void setCurrentObjectValueToColumn(Object currentObjectValueToColumn) {
        this.currentObjectValueToColumn = currentObjectValueToColumn;
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
                if (item.getPropertyValueList() != null) {
                    for (PropertyValue propertyValue : item.getPropertyValueList()) {
                        if (propertyValue.getPropertyType().equals(propertyType)) {
                            relatedPropertyValueList.add(propertyValue);
                        }
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
