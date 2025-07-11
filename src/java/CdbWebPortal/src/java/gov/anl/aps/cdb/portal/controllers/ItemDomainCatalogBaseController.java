/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCatalogBaseSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ConnectorControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogBaseControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.ItemLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.beans.ItemConnectorFacade;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainCatalogBaseController<ControllerUtility extends ItemDomainCatalogBaseControllerUtility<ItemCatalogBaseDomainEntity, ItemDomainCatalogEntityBaseFacade>, ItemCatalogBaseDomainEntity extends ItemDomainCatalogBase, ItemDomainCatalogEntityBaseFacade extends ItemFacadeBase<ItemCatalogBaseDomainEntity>, ItemCatalogEntityBaseSettingsObject extends ItemDomainCatalogBaseSettings, ItemDomainCatalogBaseLazyDataModel extends ItemLazyDataModel>
        extends ItemController<ControllerUtility, ItemCatalogBaseDomainEntity, ItemDomainCatalogEntityBaseFacade, ItemCatalogEntityBaseSettingsObject, ItemLazyDataModel> {

    private final String DOMAIN_TYPE_NAME = ItemDomainName.catalog.getValue();
    private final String DERIVED_DOMAIN_NAME = "Inventory";

    private static final Logger logger = LogManager.getLogger(ItemDomainCatalogBaseController.class.getName());
    
    private String cableEndDesignationEditValue = null;
    
    public abstract String getControllerName();

    @Override
    protected ItemCreateWizardController getItemCreateWizardController() {
        return ItemCreateWizardDomainCatalogController.getInstance();
    }

    @Override
    public ItemEnforcedPropertiesController getItemEnforcedPropertiesController() {
        return ItemEnforcedPropertiesDomainCatalogController.getInstance();
    }

    @Override
    protected ItemCatalogBaseDomainEntity cloneCreateItemElements(ItemCatalogBaseDomainEntity clonedItem, ItemCatalogBaseDomainEntity cloningFrom) {
        return cloneCreateItemElements(clonedItem, cloningFrom, true);
    }  

    @Override
    public ItemCatalogBaseDomainEntity createEntityInstance() {
        ItemCatalogBaseDomainEntity newItem = super.createEntityInstance();
        if (getCurrentItemProject() != null) {
            List<ItemProject> itemProjectList = new ArrayList<>();
            itemProjectList.add(getCurrentItemProject());
            newItem.setItemProjectList(itemProjectList);
        }
        return newItem;
    }

    @Override
    public boolean getEntityHasSortableElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Inventory";
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true;
    }

    @Override
    public boolean getEntityDisplayTemplates() {
        return true;
    }

    @Override
    public String getStyleName() {
        return "catalog";
    }

    @Override
    public String getDefaultDomainName() {
        return DOMAIN_TYPE_NAME;
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return null;
    }

    @Override
    public boolean entityCanBeCreatedByUsers() {
        return true;
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return DERIVED_DOMAIN_NAME;
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true;
    }

    @Override
    public boolean isDisplayRowExpansionItemsDerivedFromItem(Item item) {
        ItemCatalogEntityBaseSettingsObject settingObject = getSettingObject();
        Boolean displayInventorySetting = settingObject.getDisplayComponentInstanceRowExpansion();

        if (displayInventorySetting) {
            return super.isDisplayRowExpansionItemsDerivedFromItem(item);
        }

        return displayInventorySetting;
    }

    public Boolean getDisplayInventorySpares() {
        ItemCatalogBaseDomainEntity current = getCurrent();
        Boolean displayInventorySpares = current.getDisplayInventorySpares();
        if (displayInventorySpares == null) {
            displayInventorySpares = SparePartsBean.isItemContainSparePartConfiguration(getCurrent());
            current.setDisplayInventorySpares(displayInventorySpares);
        }
        return displayInventorySpares;
    }

    public List<ItemDomainInventoryBase> getInventorySparesList() {
        ItemDomainCatalogBase current = getCurrent();
        List<ItemDomainInventoryBase> inventorySparesList = current.getInventorySparesList();
        if (inventorySparesList == null) {
            inventorySparesList = new ArrayList<>();
            for (Object item : current.getInventoryItemList()) {
                ItemDomainInventoryBase inventoryItem = (ItemDomainInventoryBase) item;
                if (inventoryItem.getSparePartIndicator()) {
                    inventorySparesList.add(inventoryItem);
                }
            }
            current.setInventorySparesList(inventorySparesList);
        }
        return inventorySparesList;
    }

    public List<ItemDomainInventoryBase> getInventoryNonSparesList() {
        ItemDomainCatalogBase current = getCurrent();
        List<ItemDomainInventoryBase> inventoryNonSparesList = current.getInventoryNonSparesList();
        if (inventoryNonSparesList == null) {
            ItemDomainCatalogBase currentItem = getCurrent();
            if (currentItem != null) {
                List<ItemDomainInventoryBase> spareItems = getInventorySparesList();
                List<ItemDomainInventoryBase> allInventoryItems = getCurrent().getInventoryItemList();
                inventoryNonSparesList = new ArrayList<>(allInventoryItems);
                inventoryNonSparesList.removeAll(spareItems);
            }
            current.setInventoryNonSparesList(inventoryNonSparesList);
        }
        return inventoryNonSparesList;
    }

    public int getInventorySparesCount() {
        List<ItemDomainInventoryBase> sparesList = getInventorySparesList();
        if (sparesList != null) {
            return sparesList.size();
        }
        return 0;
    }

    public void notifyUserIfMinimumSparesReachedForCurrent() {
        int sparesMin = SparePartsBean.getSparePartsMinimumForItem(getCurrent());
        if (sparesMin == -1) {
            // Either an error occured or no spare parts configuration was found.
            return;
        } else {
            int sparesCount = getInventorySparesCount();
            if (sparesCount < sparesMin) {
                String sparesMessage;
                sparesMessage = "You now have " + sparesCount;
                if (sparesCount == 1) {
                    sparesMessage += " spare";
                } else {
                    sparesMessage += " spares";
                }

                sparesMessage += " but require a minumum of " + sparesMin;

                SessionUtility.addWarningMessage("Spares Warning", sparesMessage);
            }
        }

    }

    public int getInventoryNonSparesCount() {
        List<ItemDomainInventoryBase> nonSparesList = getInventoryNonSparesList();
        if (nonSparesList != null) {
            return nonSparesList.size();
        }
        return 0;
    }

    @Override
    public Boolean getDisplayMembershipByData() {
        return true; 
    }   

    /**
     * Allows subclasses to perform custom validation of a new ItemConnector
     * instance.
     *
     * @param itemConnector
     * @return
     */
    protected ValidInfo validateItemConnector_(boolean isUpdate, ItemConnector itemConnector) {
        return new ValidInfo(true, "");
    }

    public ValidInfo validateItemConnector(boolean isUpdate, ItemConnector itemConnector) {

        boolean isValid = true;
        String validStr = "";

        if (isUpdate) {

            // Retrieve original object from database for comparison.
            ItemConnector origItemConnector = ItemConnectorFacade.getInstance().find(itemConnector.getId());

            // get list of inheriting items
            List<Item> sharingItems = itemConnector.otherItemsUsingConnector();

            // only allow changing cable end if there are no design items using this connector
            boolean changedCableEnd
                    = (!itemConnector.getCableEndDesignation().equals(origItemConnector.getCableEndDesignation()));
            if (changedCableEnd) {
                if (!sharingItems.isEmpty()) {
                    isValid = false;
                    validStr = "Can't change cable end for " + getDisplayItemConnectorName()
                            + " because it is shared with design items using it for cable connections.";
                }
            }

            // only allow changing connector type if there are no design items using this connector
            boolean changedConnectorType
                    = (!itemConnector.getConnectorType().equals(origItemConnector.getConnectorType()));
            if (changedConnectorType) {
                if (!sharingItems.isEmpty()) {
                    isValid = false;
                    validStr = validStr + "Can't change connector type for " + getDisplayItemConnectorName()
                            + " because it is shared with design items using it for cable connections.";
                    return new ValidInfo(isValid, validStr);
                }
            }

        }

        // validate that child connector is not null
        if (itemConnector.getConnector() == null) {
            isValid = false;
            validStr = getDisplayItemConnectorLabel() + " must contain child Connector object";
            return new ValidInfo(isValid, validStr);
        }

        // validate that name is specified and unique
        if (itemConnector.getConnectorName() == null || (itemConnector.getConnectorName().isBlank())) {
            // name must be specified
            isValid = false;
            validStr = getDisplayItemConnectorLabel() + " name must be specified";
            return new ValidInfo(isValid, validStr);

        } else {
            // specified name must be unique
            Item item = itemConnector.getItem();
            if (item == null) {
                isValid = false;
                validStr = getDisplayItemConnectorLabel() + " does not have parent catalog item";
                return new ValidInfo(isValid, validStr);
            }
            List<ItemConnector> connectorList = item.getItemConnectorList();
            boolean isDuplicate = false;
            for (ItemConnector otherConnector : connectorList) {
                if (isUpdate && otherConnector.equals(itemConnector)) {
                    // in edit mode, don't compare updated item to itself
                    continue;
                }
                if (!isUpdate && otherConnector == itemConnector) {
                    // in create mode, don't compare updated item to itself
                    continue;
                }
                if (otherConnector.getConnector().getName().equals(itemConnector.getConnector().getName())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (isDuplicate) {
                isValid = false;
                validStr = getDisplayItemConnectorLabel() + " name is not unique for item";
                return new ValidInfo(isValid, validStr);
            }
        }

        // allow subclass to perform custom validation
        ValidInfo validateInfo = validateItemConnector_(isUpdate, itemConnector);
        if (!validateInfo.isValid()) {
            return validateInfo;
        }

        return new ValidInfo(isValid, validStr);
    }
    
    public void setCableEndDesignationEditValue(String value) {
        cableEndDesignationEditValue = value;
    }
    
    public String getCableEndDesignationEditValue() {
        return cableEndDesignationEditValue;
    }

    @Override
    public void prepareAddItemConnector(Item item) {
        cableEndDesignationEditValue = CdbEntity.DEFAULT_CABLE_END_DESIGNATION;
        super.prepareAddItemConnector(item);
    }
    
    /**
     * Handles save button for itemConnectorListCreateDialog.
     */
    public void saveItemConnectorDialog() {

        ItemConnectorController controller = ItemConnectorController.getInstance();
        ItemConnector newConnector = controller.getCurrent();
        
        String cableEndDesignation = getCableEndDesignationEditValue();
        if (cableEndDesignation != null) {
            newConnector.setCableEndDesignation(cableEndDesignation, newConnector.getItem().getOwnerUser());
        }

        // validate new connector
        ValidInfo validateInfo = validateItemConnector(false, newConnector);
        if (!validateInfo.isValid()) {
            this.revertItemConnectorListForCurrent();
            SessionUtility.addErrorMessage("Error", "Unable to create " + getDisplayItemConnectorName() + ". "
                    + validateInfo.getValidString() + ".");
            return;
        }

        controller.createWithoutRedirect();

        reloadCurrent();
    }

    public void deleteItemConnector(ItemConnector itemConnector) {

        Item item = getCurrent();
        ConnectorControllerUtility connectorControllerUtility = new ConnectorControllerUtility();
        itemConnector = itemConnectorFacade.find(itemConnector.getId());
        Connector connector = itemConnector.getConnector();

        // check if it is safe to delete the connector
        ValidInfo deleteValidInfo = itemConnector.isDeleteAllowed();

        if (deleteValidInfo.isValid()) {
            // underlying Connector is not inherited by design items and can be removed
            completeDeleteItemConnector(itemConnector);

        } else {
            // display error message identifying items sharing inherited connector
            String message
                    = getDisplayItemConnectorLabel()
                    + " cannot be removed. " + deleteValidInfo.getValidString();
            SessionUtility.addErrorMessage("Error", message);
        }

        reloadCurrent();
    }

    private void completeDeleteItemConnector(ItemConnector itemConnector) {
        ItemDomainCatalogBase item = getCurrent();
        removeCatalogItemConnector(item, itemConnector);
        ItemConnectorController.getInstance().destroy(itemConnector);
    }

    public void removeCatalogItemConnector(ItemDomainCatalogBase item, ItemConnector itemConnector) {
        List<ItemConnector> itemConnectorList = item.getItemConnectorList();
        itemConnectorList.remove(itemConnector);

        Connector connector = itemConnector.getConnector();
        connector.getItemConnectorList().remove(itemConnector);

        itemConnector.addConnectorToRemove(connector);
    }

    public void connectorEditRowEvent(RowEditEvent event) {
        ConnectorControllerUtility utility = new ConnectorControllerUtility();

        UserInfo user = SessionUtility.getUser();
        ItemConnector object = (ItemConnector) event.getObject();
        Connector connector = object.getConnector();

        // validate udpated connector
        ValidInfo validateInfo = validateItemConnector(true, object);
        if (!validateInfo.isValid()) {
            SessionUtility.addErrorMessage("Error", "Unable to update " + getDisplayItemConnectorName() + ". "
                    + validateInfo.getValidString() + ".");
            reloadCurrent();
            return;
        }

        try {
            utility.update(connector, user);
            SessionUtility.addInfoMessage(
                    "Updated " + getDisplayItemConnectorLabel(),
                    "Updated " + getDisplayItemConnectorName() + ": " + connector,
                    true);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage(), true);
            reloadCurrent();
        } catch (RuntimeException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage(), true);
            reloadCurrent();
        }
    }

    public void connectorEditCancelRowEvent(RowEditEvent event) {
        reloadCurrent();
    }

}
