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
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ConnectorControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogBaseControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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
public abstract class ItemDomainCatalogBaseController<ControllerUtility extends ItemDomainCatalogBaseControllerUtility<ItemCatalogBaseDomainEntity, ItemDomainCatalogEntityBaseFacade>, ItemCatalogBaseDomainEntity extends ItemDomainCatalogBase, ItemDomainCatalogEntityBaseFacade extends ItemFacadeBase<ItemCatalogBaseDomainEntity>, ItemCatalogEntityBaseSettingsObject extends ItemSettings> extends ItemController<ControllerUtility, ItemCatalogBaseDomainEntity, ItemDomainCatalogEntityBaseFacade, ItemCatalogEntityBaseSettingsObject>  {

    private final String DOMAIN_TYPE_NAME = ItemDomainName.catalog.getValue();
    private final String DERIVED_DOMAIN_NAME = "Inventory";        
    
    private static final Logger logger = LogManager.getLogger(ItemDomainCatalogBaseController.class.getName());                                       

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
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new CatalogItemElementConstraintInformation(itemElement);
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
    
    /**
     * Allows subclasses to perform custom validation of a new ItemConnector instance.
     * @param itemConnector
     * @return 
     */
    protected ValidInfo validateItemConnector_(boolean isUpdate, ItemConnector itemConnector) {
        return new ValidInfo(true, "");
    }
    
    public ValidInfo validateItemConnector(boolean isUpdate, ItemConnector itemConnector) {
        
        boolean isValid = true;
        String validStr = "";
        
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
                if (otherConnector.getConnector().getName().equals(itemConnector.getConnector().getName())){
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

    /**
     * Handles save button for itemConnectorListCreateDialog.
     */
    public void saveItemConnectorDialog() {
        
        ItemConnectorController controller = ItemConnectorController.getInstance();
        ItemConnector newConnector = controller.getCurrent();
        
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
        if (connectorControllerUtility.verifySafeRemovalOfConnector(connector)) {
            completeDeleteItemConnector(itemConnector);
        } else {
            // Generate a userfull message
            String message = "";
            List<ItemConnector> itemConnectorList = connector.getItemConnectorList();
            List<ItemConnector> connectorDeleteList = new ArrayList<>();
            for (ItemConnector ittrConnector : itemConnectorList) {
                Item ittrItem = ittrConnector.getItem();
                if (ittrItem.equals(item) == false) {
                    if (ittrItem.getDomain().getName().equals(ItemDomainName.machineDesign.getValue())) {
                        if (ittrConnector.getItemElementRelationshipList().size() == 0) {
                            connectorDeleteList.add(ittrConnector);
                        } else {
                            message = "Please check connections on machine design item: " + ittrItem.toString();
                            SessionUtility.addErrorMessage("Error", "Cannot remove connector, check if it is used for connections in machine design. " + message);
                        }
                    }
                } else {
                    connectorDeleteList.add(ittrConnector);
                }
            }

            if (itemConnectorList.size() == connectorDeleteList.size()) {
                // All save. 
                for (ItemConnector relatedConnector : connectorDeleteList) {
                    completeDeleteItemConnector(relatedConnector);
                }
            }
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
