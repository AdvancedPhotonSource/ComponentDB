/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;

import static gov.anl.aps.cdb.portal.controllers.CdbEntityController.parseSettingValueAsInteger;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.beans.ItemElementFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemElementUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DragDropEvent;
import org.primefaces.model.TreeNode;

@Named("itemElementController")
@SessionScoped
public class ItemElementController extends CdbDomainEntityController<ItemElement, ItemElementFacade> implements Serializable {

    @EJB
    private ItemElementFacade itemElementFacade;

    /**
     * Controller specific settings
     */
    private static final String DisplayChildItemSettingTypeKey = "ItemElement.List.Display.ChildDesign";
    private static final String DisplayComponentSettingTypeKey = "ItemElement.List.Display.Component";
    private static final String DisplayComponentTypeSettingTypeKey = "ItemElement.List.Display.ComponentType";
    private static final String DisplayCreatedByUserSettingTypeKey = "ItemElement.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "ItemElement.List.Display.CreatedOnDateTime";
    private static final String DisplayDescriptionSettingTypeKey = "ItemElement.List.Display.Description";
    private static final String DisplayFlatTableViewSettingTypeKey = "ItemElement.List.Display.FlatTableView";
    private static final String DisplayIdSettingTypeKey = "ItemElement.List.Display.Id";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "ItemElement.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "ItemElement.List.Display.LastModifiedOnDateTime";
    private static final String DisplayLocationSettingTypeKey = "ItemElement.List.Display.Location";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ItemElement.List.Display.NumberOfItemsPerPage";
    private static final String DisplayOwnerUserSettingTypeKey = "ItemElement.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "ItemElement.List.Display.OwnerGroup";
    private static final String DisplaySortOrderSettingTypeKey = "ItemElement.List.Display.SortOrder";
    private static final String DisplayPropertyTypeId1SettingTypeKey = "ItemElement.List.Display.PropertyTypeId1";
    private static final String DisplayPropertyTypeId2SettingTypeKey = "ItemElement.List.Display.PropertyTypeId2";
    private static final String DisplayPropertyTypeId3SettingTypeKey = "ItemElement.List.Display.PropertyTypeId3";
    private static final String DisplayPropertyTypeId4SettingTypeKey = "ItemElement.List.Display.PropertyTypeId4";
    private static final String DisplayPropertyTypeId5SettingTypeKey = "ItemElement.List.Display.PropertyTypeId5";
    private static final String DisplayItemElementRowColorTypeKey = "ItemElement.List.Display.RowColor";
    private static final String DisplayBillOfMaterialsActionColumnTypeKey = "ItemElement.List.Display.BillOfMaterialsActionColumn";
    private static final String DisplayRowExpansionSettingTypeKey = "ItemElement.List.Display.RowExpansion";
    private static final String LoadRowExpansionPropertyValueSettingTypeKey = "ItemElement.List.Load.RowExpansionPropertyValue";
    private static final String SortByPropertyTypeIdSettingTypeKey = "ItemElement.List.SortBy.PropertyTypeId";
    private static final String FilterByChildItemSettingTypeKey = "ItemElement.List.FilterBy.ChildDesign";
    private static final String FilterByComponentSettingTypeKey = "ItemElement.List.FilterBy.Component";
    private static final String FilterByComponentTypeSettingTypeKey = "ItemElement.List.FilterBy.ComponentType";
    private static final String FilterByCreatedByUserSettingTypeKey = "ItemElement.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "ItemElement.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByDescriptionSettingTypeKey = "ItemElement.List.FilterBy.Description";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "ItemElement.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "ItemElement.List.FilterBy.LastModifiedOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ItemElement.List.FilterBy.Location";
    private static final String FilterByNameSettingTypeKey = "ItemElement.List.FilterBy.Name";
    private static final String FilterByOwnerUserSettingTypeKey = "ItemElement.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "ItemElement.List.FilterBy.OwnerGroup";
    private static final String FilterBySortOrderSettingTypeKey = "ItemElement.List.FilterBy.SortOrder";

    private static final Logger logger = Logger.getLogger(ItemElementController.class.getName());

    private static final String DESIGN_ELEMENT_ROW_COLOR_PROPERTY_NAME = "Item Element Row Color";

    private Boolean displayChildItem = null;
    private Boolean displayComponent = null;
    private Boolean displayComponentType = null;
    private Boolean displayFlatTableView = null;
    private Boolean displayLocation = null;
    private Boolean displaySortOrder = null;
    private Boolean displayItemElementRowColor = null;
    private Boolean displayBillOfMaterialsActionColumn = null;

    private Integer sortByPropertyTypeId = null;

    private String filterByChildItem = null;
    private String filterByComponent = null;
    private String filterByComponentType = null;
    private String filterByLocation = null;
    private String filterBySortOrder = null;

    private Item selectedParentItem = null;

    private List<PropertyValue> filteredPropertyValueList = null;

    private List<ItemElement> pendingChangesItemElementList = null;
    private List<ItemElement> sortableItemElementList = null;

    private List<Item> selectChildItemCandidateList = null;

    public ItemElementController() {
    }
    
    public static ItemElementController getInstance() {
        return (ItemElementController) SessionUtility.findBean("itemElementController"); 
    }
    
    @Override
    protected void prepareEntityUpdate(ItemElement itemElement) throws CdbException {
        super.prepareEntityUpdate(itemElement);
        
        // Basic checks for updating an element must be verified with domain of item element. 
        Item parentItem = itemElement.getParentItem();
        ItemController itemController = ItemController.findDomainControllerForItem(parentItem);
        itemController.checkItemElement(itemElement);
        
        if (itemElement.getId() != null) {
            ItemElement freshDbItemElement = findById(itemElement.getId()); 
            
            // Verify if contained item changed
            Item originalContainedItem = freshDbItemElement.getContainedItem();
            if (ObjectUtility.equals(originalContainedItem, itemElement.getContainedItem()) == false) {
                // Contained item has been updated.
                ItemElementConstraintInformation ieci = getItemElementConstraintInformation(freshDbItemElement); 
                if (ieci.isSafeToUpdateContainedItem() == false) {
                    itemElement.setContainedItem(originalContainedItem);
                    throw new CdbException("Cannot update item element " + itemElement + " due to constraints not met. Please reload the item details page and try again.");
                }
            }
            
            //Verify if isRequred changed
            Boolean originalIsRequired = freshDbItemElement.getIsRequired(); 
            if (ObjectUtility.equals(originalIsRequired, itemElement.getIsRequired()) == false) {                
                itemController.finalizeItemElementRequiredStatusChanged(itemElement); 
            }
        }        
    }

    @Override
    protected void completeEntityUpdate(ItemElement itemElement) {
        // Force update of constraint information. 
        itemElement.setConstraintInformation(null);
        
        Item parentItem = itemElement.getParentItem();
        ItemController itemController = ItemController.findDomainControllerForItem(parentItem);
        itemController.completeSuccessfulItemElementUpdate(itemElement); 
    }

    @Override
    protected void prepareEntityDestroy(ItemElement itemElement) throws CdbException {
        super.prepareEntityDestroy(itemElement);
        // Verify that item domain allows destroy of item element. 
        ItemElementConstraintInformation ieci = getFreshItemElementConstraintInformation(itemElement);
        if (ieci.isSafeToRemove() == false) {
            throw new CdbException("Cannot remove item element. Constrains not met.");
        }
    }    

    @Override
    protected void completeEntityDestroy(ItemElement itemElement) {
        // Verify that item domain allows destroy of item element. 
        Item parentItem = itemElement.getParentItem();
        ItemController itemController = ItemController.findDomainControllerForItem(parentItem);
        itemController.completeSuccessfulItemElementRemoval(itemElement);
    }

    public ItemElementConstraintInformation getItemElementConstraintInformation(ItemElement itemElement) {
        ItemElementConstraintInformation itemElementConstraintInformation;
        itemElementConstraintInformation = itemElement.getConstraintInformation();
        if (itemElementConstraintInformation == null) {
            Item parentItem = itemElement.getParentItem();
            if (parentItem != null) {
                ItemController itemDomainController = ItemController.findDomainControllerForItem(parentItem);
                itemElementConstraintInformation = itemDomainController.loadItemElementConstraintInformation(itemElement);
                itemElement.setConstraintInformation(itemElementConstraintInformation);
            }
        }
        return itemElementConstraintInformation;
    }

    public ItemElementConstraintInformation getFreshItemElementConstraintInformation(ItemElement itemElement) {
        itemElement = findById(itemElement.getId()); 
        return getItemElementConstraintInformation(itemElement);
    }

    public void setValidContainedItemForCurrent(Item containedItem) {
        if (current.getId() == null) {
            current.setContainedItem(containedItem);
            return;
        } else {
            ItemElementConstraintInformation ieci = getFreshItemElementConstraintInformation(current);
            if (ieci.isSafeToUpdateContainedItem()) {
                current.setContainedItem(containedItem);
                return;
            }
        }

        SessionUtility.addErrorMessage("Constraints not met", "Couldn't update contained item due to certain constraints. Refresh the detail page and try again.");
    }
    
    public void revertContainedItemForCurrent() {
        if (current != null) {
            ItemElement itemElement = findById(current.getId()); 
            Item containedItem = itemElement.getContainedItem();
            
            current.setContainedItem(containedItem);
        }
    }
    
    public void toggleIsRequiredForCurrent() {
        if (current != null) {
            Boolean isRequired = current.getTemporaryIsRequiredValue();            
            current.setTemporaryIsRequiredValue(!isRequired);
        }
    }
    
    public String getIsRequiredButtonValueForCurrent() {
        if (current != null) {
            Boolean isRequired = current.getTemporaryIsRequiredValue();
            if (isRequired) {
                return "Yes";
            } else {
                return "No"; 
            }
        }
        return null; 
    }
    
    public void revertIsRequiredItemForCurrent() {
        if (current != null) {
            current.setTemporaryIsRequiredValue(null);
        }
    }
    
    public void submitIsRequiredValueForCurrent() {
        current.setIsRequired(current.getTemporaryIsRequiredValue());
        updateWithoutRedirect();
    }

    @Override
    protected ItemElementFacade getEntityDbFacade() {
        return itemElementFacade;
    }

    private void resetSelectObjectLists() {
        selectChildItemCandidateList = null;
    }

    @Override
    protected ItemElement createEntityInstance() {
        ItemElement designElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        designElement.setEntityInfo(entityInfo);

        // clear selection lists
        resetSelectObjectLists();
        return designElement;
    }

    @Override
    public ItemElement findById(Integer id) {
        return itemElementFacade.findById(id);
    }

    @Override
    public EntityInfo getEntityInfo(ItemElement entity) {
        if (entity != null) {
            return entity.getEntityInfo();
        }
        return null;
    }

    public TreeNode getItemElementListTreeTableRootNode(ItemElement parent) {

        if (parent.getChildItemElementListTreeTableRootNode() == null) {
            try {
                if (parent.getContainedItem() != null) {
                    parent.setChildItemElementListTreeTableRootNode(ItemElementUtility.createItemElementRoot(parent.getContainedItem()));
                }
            } catch (CdbException ex) {
                logger.debug(ex);
            }
        }

        return parent.getChildItemElementListTreeTableRootNode();
    }

    @Override
    public String getEntityTypeName() {
        return "itemElement";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Item Element";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<ItemElement> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(ItemElement designElement) throws ObjectAlreadyExists {
    }

    /*
    @Override
    public void prepareEntityUpdate(ItemElement designElement) throws ObjectAlreadyExists {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);

        // Compare properties with what is in the db
        List<PropertyValue> originalPropertyValueList = designElementFacade.findById(designElement.getId()).getPropertyValueList();
        List<PropertyValue> newPropertyValueList = designElement.getPropertyValueList();
        logger.debug("Verifying properties for design element " + designElement);
        PropertyValueUtility.preparePropertyValueHistory(originalPropertyValueList, newPropertyValueList, entityInfo);
        prepareImageList(designElement);
        resetSelectObjectLists();
    }
     */

    // This listener is accessed either after selection made in dialog,
    // or from selection menu.    
    public void selectItemValueChangeListener(ValueChangeEvent valueChangeEvent) {
        ItemElement itemElement = getCurrent();
        if (itemElement == null || valueChangeEvent == null) {
            return;
        }

        Item existingItem = itemElement.getContainedItem();
        Item newEventItem = null;
        Item oldEventItem = null;

        Object newValue = valueChangeEvent.getNewValue();
        if (newValue != null) {
            newEventItem = (Item) newValue;
        }
        Object oldValue = valueChangeEvent.getOldValue();
        if (oldValue != null) {
            oldEventItem = (Item) oldValue;
        }

        if (ObjectUtility.equals(existingItem, oldEventItem)) {
            // change via menu
            itemElement.setContainedItem(newEventItem);
        } else {
            // change via dialog
            itemElement.setContainedItem(oldEventItem);
        }
    }

    @Override
    public void prepareEntityUpdateOnRemoval(ItemElement designElement) {
        EntityInfo entityInfo = designElement.getEntityInfo();
        EntityInfoUtility.updateEntityInfo(entityInfo);
        prepareImageList(designElement);
        resetSelectObjectLists();
    }

    @Override
    public String prepareView(ItemElement itemElement) {
        logger.debug("Preparing item element view");
        super.prepareView(itemElement);
        return "/views/itemElement/view.xhtml?faces-redirect=true";
    }

    public void prepareAddProperty() {
        ItemElement designElement = getCurrent();
        List<PropertyValue> propertyList = designElement.getPropertyValueList();
        PropertyValue property = new PropertyValue();
        propertyList.add(property);
    }

    public String destroyAndReturnItemView(ItemElement designElement) {
        Item parentItem = designElement.getParentItem();
        setCurrent(designElement);
        try {
            logger.debug("Destroying " + designElement.getName());
            getEntityDbFacade().remove(designElement);
            SessionUtility.addInfoMessage("Success", "Deleted design element id " + designElement.getId() + ".");
            return "/views/design/view.xhtml?faces-redirect=true&id=" + parentItem.getId();
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        super.updateSettingsFromSettingTypeDefaults(settingTypeMap);
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults");

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayFlatTableView = Boolean.parseBoolean(settingTypeMap.get(DisplayFlatTableViewSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        displayRowExpansion = Boolean.parseBoolean(settingTypeMap.get(DisplayRowExpansionSettingTypeKey).getDefaultValue());
        loadRowExpansionPropertyValues = Boolean.parseBoolean(settingTypeMap.get(LoadRowExpansionPropertyValueSettingTypeKey).getDefaultValue());

        displayPropertyTypeId1 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId1SettingTypeKey).getDefaultValue());
        displayPropertyTypeId2 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId2SettingTypeKey).getDefaultValue());
        displayPropertyTypeId3 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId3SettingTypeKey).getDefaultValue());
        displayPropertyTypeId4 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId4SettingTypeKey).getDefaultValue());
        displayPropertyTypeId5 = parseSettingValueAsInteger(settingTypeMap.get(DisplayPropertyTypeId5SettingTypeKey).getDefaultValue());

        sortByPropertyTypeId = parseSettingValueAsInteger(settingTypeMap.get(SortByPropertyTypeIdSettingTypeKey).getDefaultValue());

        displayChildItem = Boolean.parseBoolean(settingTypeMap.get(DisplayChildItemSettingTypeKey).getDefaultValue());
        displayComponent = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentSettingTypeKey).getDefaultValue());
        displayComponentType = Boolean.parseBoolean(settingTypeMap.get(DisplayComponentTypeSettingTypeKey).getDefaultValue());
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());
        displayItemElementRowColor = Boolean.parseBoolean(settingTypeMap.get(DisplayItemElementRowColorTypeKey).getDefaultValue());
        displayBillOfMaterialsActionColumn = Boolean.parseBoolean(settingTypeMap.get(DisplayBillOfMaterialsActionColumnTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();

        filterByChildItem = settingTypeMap.get(FilterByChildItemSettingTypeKey).getDefaultValue();
        filterByComponent = settingTypeMap.get(FilterByComponentSettingTypeKey).getDefaultValue();
        filterByComponentType = settingTypeMap.get(FilterByComponentTypeSettingTypeKey).getDefaultValue();
        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        super.updateSettingsFromSessionSettingEntity(settingEntity);
        if (settingEntity == null) {
            return;
        }

        logger.debug("Updating list settings from session user");

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayFlatTableView = settingEntity.getSettingValueAsBoolean(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        displayOwnerUser = settingEntity.getSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = settingEntity.getSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = settingEntity.getSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        displayRowExpansion = settingEntity.getSettingValueAsBoolean(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        loadRowExpansionPropertyValues = settingEntity.getSettingValueAsBoolean(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        displayPropertyTypeId1 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        displayPropertyTypeId2 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        displayPropertyTypeId3 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        displayPropertyTypeId4 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        displayPropertyTypeId5 = settingEntity.getSettingValueAsInteger(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        sortByPropertyTypeId = settingEntity.getSettingValueAsInteger(SortByPropertyTypeIdSettingTypeKey, sortByPropertyTypeId);

        displayChildItem = settingEntity.getSettingValueAsBoolean(DisplayChildItemSettingTypeKey, displayChildItem);
        displayComponent = settingEntity.getSettingValueAsBoolean(DisplayComponentSettingTypeKey, displayComponent);
        displayComponentType = settingEntity.getSettingValueAsBoolean(DisplayComponentTypeSettingTypeKey, displayComponentType);
        displayLocation = settingEntity.getSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displaySortOrder = settingEntity.getSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);
        displayItemElementRowColor = settingEntity.getSettingValueAsBoolean(DisplayItemElementRowColorTypeKey, displayItemElementRowColor);
        displayBillOfMaterialsActionColumn = settingEntity.getSettingValueAsBoolean(DisplayBillOfMaterialsActionColumnTypeKey, displayBillOfMaterialsActionColumn);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = settingEntity.getSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = settingEntity.getSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = settingEntity.getSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = settingEntity.getSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = settingEntity.getSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = settingEntity.getSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        filterByChildItem = settingEntity.getSettingValueAsString(FilterByChildItemSettingTypeKey, filterByChildItem);
        filterByComponent = settingEntity.getSettingValueAsString(FilterByComponentSettingTypeKey, filterByComponent);
        filterByComponentType = settingEntity.getSettingValueAsString(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        filterByLocation = settingEntity.getSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterBySortOrder = settingEntity.getSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);

        resetDomainEntityPropertyTypeIdIndexMappings();
    }

    @Override
    public void resetDomainEntityPropertyTypeIdIndexMappings() {
        super.resetDomainEntityPropertyTypeIdIndexMappings();
        ItemElement.setSortByPropertyTypeId(sortByPropertyTypeId);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, Object> filters = dataTable.getFilters();
        filterByChildItem = (String) filters.get("childItem");
        filterByComponent = (String) filters.get("component");
        filterByComponentType = (String) filters.get("componentType");
        filterByLocation = (String) filters.get("location");
        filterBySortOrder = (String) filters.get("sortOrder");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        super.saveSettingsForSessionSettingEntity(settingEntity);
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayFlatTableViewSettingTypeKey, displayFlatTableView);
        settingEntity.setSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        settingEntity.setSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        settingEntity.setSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        settingEntity.setSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        settingEntity.setSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        settingEntity.setSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        settingEntity.setSettingValue(DisplayRowExpansionSettingTypeKey, displayRowExpansion);
        settingEntity.setSettingValue(LoadRowExpansionPropertyValueSettingTypeKey, loadRowExpansionPropertyValues);

        settingEntity.setSettingValue(DisplayPropertyTypeId1SettingTypeKey, displayPropertyTypeId1);
        settingEntity.setSettingValue(DisplayPropertyTypeId2SettingTypeKey, displayPropertyTypeId2);
        settingEntity.setSettingValue(DisplayPropertyTypeId3SettingTypeKey, displayPropertyTypeId3);
        settingEntity.setSettingValue(DisplayPropertyTypeId4SettingTypeKey, displayPropertyTypeId4);
        settingEntity.setSettingValue(DisplayPropertyTypeId5SettingTypeKey, displayPropertyTypeId5);

        settingEntity.setSettingValue(SortByPropertyTypeIdSettingTypeKey, sortByPropertyTypeId);

        settingEntity.setSettingValue(DisplayChildItemSettingTypeKey, displayChildItem);
        settingEntity.setSettingValue(DisplayComponentSettingTypeKey, displayComponent);
        settingEntity.setSettingValue(DisplayComponentTypeSettingTypeKey, displayComponentType);
        settingEntity.setSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        settingEntity.setSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);
        settingEntity.setSettingValue(DisplayItemElementRowColorTypeKey, displayItemElementRowColor);
        settingEntity.setSettingValue(DisplayBillOfMaterialsActionColumnTypeKey, displayBillOfMaterialsActionColumn);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        settingEntity.setSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        settingEntity.setSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        settingEntity.setSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        settingEntity.setSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        settingEntity.setSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

        settingEntity.setSettingValue(FilterByChildItemSettingTypeKey, filterByChildItem);
        settingEntity.setSettingValue(FilterByComponentSettingTypeKey, filterByComponent);
        settingEntity.setSettingValue(FilterByComponentTypeSettingTypeKey, filterByComponentType);
        settingEntity.setSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        settingEntity.setSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByChildItem = null;
        filterByComponent = null;
        filterByComponentType = null;
        filterByLocation = null;
        filterBySortOrder = null;
    }

    public String getDisplayRowStyle(ItemElement itemElement) {
        List<String> rowStyles = new ArrayList<>(); 
        
        if (displayItemElementRowColor != null && displayItemElementRowColor) {
            List<PropertyValue> propertyValueList = itemElement.getPropertyValueList();
            if (propertyValueList != null && propertyValueList.size() > 0) {
                for (PropertyValue propertyValue : propertyValueList) {
                    if (propertyValue.getPropertyType().getName().equals(DESIGN_ELEMENT_ROW_COLOR_PROPERTY_NAME)) {
                        String value = propertyValue.getValue();
                        rowStyles.add(value + "Row");
                        break;  
                    }
                }
            }
        }
        
        Boolean isRequired = itemElement.getIsRequired(); 
        if (isRequired != null && isRequired == false) {
            rowStyles.add("optionalElement"); 
        }
        
        String style = ""; 
        for (String currentStyle : rowStyles) {
            style += currentStyle + " "; 
        }
        
        return style;
    }

    public Boolean getDisplayItemElementRowColor() {
        return displayItemElementRowColor;
    }

    public void setDisplayItemElementRowColor(Boolean displayItemElementRowColor) {
        this.displayItemElementRowColor = displayItemElementRowColor;
    }

    public Integer getSortByPropertyTypeId() {
        return sortByPropertyTypeId;
    }

    public void setSortByPropertyTypeId(Integer sortByPropertyTypeId) {
        this.sortByPropertyTypeId = sortByPropertyTypeId;
    }

    public Boolean getDisplayBillOfMaterialsActionColumn() {
        return displayBillOfMaterialsActionColumn;
    }

    public void setDisplayBillOfMaterialsActionColumn(Boolean displayBillOfMaterialsActionColumn) {
        this.displayBillOfMaterialsActionColumn = displayBillOfMaterialsActionColumn;
    }

    public Boolean getDisplayChildItem() {
        return displayChildItem;
    }

    public void setDisplayChildItem(Boolean displayChildItem) {
        this.displayChildItem = displayChildItem;
    }

    public Boolean getDisplayComponent() {
        return displayComponent;
    }

    public void setDisplayComponent(Boolean displayComponent) {
        this.displayComponent = displayComponent;
    }

    public Boolean getDisplayComponentType() {
        return displayComponentType;
    }

    public void setDisplayComponentType(Boolean displayComponentType) {
        this.displayComponentType = displayComponentType;
    }

    public Boolean getDisplayFlatTableView() {
        return displayFlatTableView;
    }

    public void setDisplayFlatTableView(Boolean displayFlatTableView) {
        this.displayFlatTableView = displayFlatTableView;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    public String getFilterByChildItem() {
        return filterByChildItem;
    }

    public void setFilterByChildItem(String filterByChildItem) {
        this.filterByChildItem = filterByChildItem;
    }

    public String getFilterByComponent() {
        return filterByComponent;
    }

    public void setFilterByComponent(String filterByComponent) {
        this.filterByComponent = filterByComponent;
    }

    public String getFilterByComponentType() {
        return filterByComponentType;
    }

    public void setFilterByComponentType(String filterByComponentType) {
        this.filterByComponentType = filterByComponentType;
    }

    public String getFilterByLocation() {
        return filterByLocation;
    }

    public void setFilterByLocation(String filterByLocation) {
        this.filterByLocation = filterByLocation;
    }

    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    public Item getSelectedParentItem() {
        return selectedParentItem;
    }

    /**
     * Function handles the sorting of dropped designElement based on where it
     * was dropped.
     *
     * @param ddEvent
     */
    public void onItemElementDrop(DragDropEvent ddEvent) {
        String[] draggedId = ddEvent.getDragId().split(":");
        String[] droppedId = ddEvent.getDropId().split(":");
        int dragIndex = Integer.parseInt(draggedId[draggedId.length - 2]);
        int dropIndex = Integer.parseInt(droppedId[droppedId.length - 2]);

        String dropDescription = droppedId[droppedId.length - 1];

        if (dragIndex > dropIndex) { //Dragged from bottom to top
            if (dropDescription.equals("designElementRearrangeDataGridBottomPanel")) {
                dropIndex++;
            }
        } else if (dragIndex < dropIndex) { //Dragged from top to bottom
            if (dropDescription.equals("designElementRearrangeDataGridTopPanel")) {
                dropIndex--;
            }
        }

        if (dragIndex == dropIndex) {
            return;
        }
        ItemElement designElementDroppped = (ItemElement) ddEvent.getData();

        List<ItemElement> designElementList = sortableItemElementList;

        List<ItemElement> newItemElementList = new ArrayList<>();

        float sortOrder = 1;
        for (int i = 0; i < designElementList.size(); i++) {
            if (dragIndex > dropIndex) {
                if (dragIndex == i) {
                    continue;
                }
                if (dropIndex == i) {
                    newItemElementList.add(designElementDroppped);
                    updateItemElementSortOrder(designElementDroppped, sortOrder++);
                }
                newItemElementList.add(designElementList.get(i));
                updateItemElementSortOrder(designElementList.get(i), sortOrder++);
            } else if (dragIndex < dropIndex) {
                if (dragIndex == i) {
                    continue;
                }
                newItemElementList.add(designElementList.get(i));
                updateItemElementSortOrder(designElementList.get(i), sortOrder++);
                if (dropIndex == i) {
                    newItemElementList.add(designElementDroppped);
                    updateItemElementSortOrder(designElementDroppped, sortOrder++);
                }
            }
        }

        sortableItemElementList = newItemElementList;
    }

    public List<ItemElement> getSortableItemElementList() {
        return sortableItemElementList;
    }

    /**
     * Creates a sorted list in preparation for sorting.
     *
     * @param parentItem
     * @param onSuccessCommand
     */
    public void configureSortableItemElementList(Item parentItem, String onSuccessCommand) {
        if (pendingChangesItemElementList != null) {
            pendingChangesItemElementList.clear();
        }

        List<ItemElement> parentItemElementList = parentItem.getItemElementDisplayList();

        if (parentItemElementList != null && parentItemElementList.size() > 0) {
            sortableItemElementList = new ArrayList<>();

            for (ItemElement designElement : parentItemElementList) {
                if (designElement.getId() == null) {
                    SessionUtility.addWarningMessage("Unsaved Item Element", "Please save design element list and try again.");
                    return;
                }
                sortableItemElementList.add(designElement);
            }
            // Apply a sort in case the user applied filters to the data table
            Collections.sort(sortableItemElementList, new Comparator<ItemElement>() {
                @Override
                public int compare(ItemElement e1, ItemElement e2) {
                    if (e1.getSortOrder() != null && e2.getSortOrder() != null) {
                        return e1.getSortOrder().compareTo(e2.getSortOrder());
                    }
                    return 1;
                }
            });

            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        } else {
            SessionUtility.addInfoMessage("Info", "No design elements to sort.");
        }

    }

    /**
     * Updates the design element with new sort order number if needed adds to
     * list that will be saved once sorting is complete.
     *
     * @param designElement object to have the sort order updated
     * @param sortOrder sort order to set on the designElement object
     */
    private void updateItemElementSortOrder(ItemElement designElement, float sortOrder) {
        if (designElement.getSortOrder() != null && designElement.getSortOrder() == sortOrder) {
            return;
        }
        designElement.setSortOrder(sortOrder);

        if (pendingChangesItemElementList == null) {
            pendingChangesItemElementList = new ArrayList<>();
        }
        if (pendingChangesItemElementList.contains(designElement) == false) {
            pendingChangesItemElementList.add(designElement);
        }
    }

    @Override
    public Boolean getDisplayUpdateSortOrderButton() {
        return true;
    }

    /**
     * Determines if pending changes need to be saved by checking the list.
     *
     * @return boolean
     */
    public Boolean getItemElementPendingChanges() {
        return pendingChangesItemElementList != null && pendingChangesItemElementList.size() > 0;
    }

    /**
     * Updates each design element object that had the sort order modified.
     */
    public void saveItemElementPendingChanges() {
        if (getItemElementPendingChanges()) {
            for (int i = 0; i < pendingChangesItemElementList.size(); i++) {
                this.setCurrent(pendingChangesItemElementList.get(i));
                this.update();
            }

            // Need the server to refresh database information for the particular entity.
            int parentItemId = pendingChangesItemElementList.get(0).getParentItem().getId();
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("?id=" + parentItemId);
            } catch (IOException ex) {
                logger.debug(ex);
                SessionUtility.addErrorMessage("Error", ex.getMessage());
            }

            // Clear the pending changes list since everythign was saved
            pendingChangesItemElementList.clear();
            // Clear the sortable list pointer since sorting is complete. 
            sortableItemElementList = null;

        }
    }

    public void prepareComponentInstancePropertyValueDisplay(ItemElement designElement) {
        List<PropertyValue> propertyValueList = designElement.getPropertyValueList();
        for (PropertyValue propertyValue : propertyValueList) {
            PropertyValueUtility.configurePropertyValueDisplay(propertyValue);
        }
    }

}
