/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemEnforcedPropertiesDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCatalogBaseControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * Allows subclasses to determine whether a new ItemConnector instance is valid. Default
     * implementation returns true.
     * @return 
     */
    protected ValidInfo validateNewItemConnector(ItemConnector itemConnector) {
        return new ValidInfo(true, "");
    }

    /**
     * Handles save button for itemConnectorListCreateDialog.
     */
    public void saveItemConnectorDialog() {
        
        // check if new connector name is duplicate to existing
        ItemConnectorController controller = ItemConnectorController.getInstance();
        ItemConnector newConnector = controller.getCurrent();
        Item item = newConnector.getItem();
        List<ItemConnector> connectorList = item.getItemConnectorList();   
        
        // check to see if new item is duplicate
        boolean isDuplicate = false;
        for (ItemConnector itemConnector : connectorList) {
            if ((itemConnector.getId() != null) 
                    && (itemConnector.getConnector().getName().equals(newConnector.getConnector().getName()))) {
                isDuplicate = true;
                break;
            }
        }        
        if (isDuplicate) {
            this.revertItemConnectorListForCurrent();
            SessionUtility.addErrorMessage("Error", "Unable to create connector. Please use unique name.");
            return;
        }
        
        // allow subclass to validate new item
        ValidInfo validateInfo = validateNewItemConnector(newConnector);
        if (!validateInfo.isValid()) {
            this.revertItemConnectorListForCurrent();
            SessionUtility.addErrorMessage("Error", "Unable to create connector. " + validateInfo.getValidString());
            return;
        }
        
        controller.createWithoutRedirect();
    }

}
