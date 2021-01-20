/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperCableInventory;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemCreateWizardDomainCableInventoryController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableInventorySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableInventoryControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import gov.anl.aps.cdb.portal.view.objects.ImportExportFormatInfo;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCableInventoryController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableInventoryController extends ItemDomainInventoryBaseController<ItemDomainCableInventoryControllerUtility, ItemDomainCableInventory, ItemDomainCableInventoryFacade, ItemDomainCableInventorySettings> {
    
    public static final String ITEM_DOMAIN_CABLE_INVENTORY_STATUS_PROPERTY_TYPE_NAME = "Cable Instance Status";
    public static final String CABLE_INVENTORY_INTERNAL_PROPERTY_TYPE = "cable_inventory_internal_property_type";
    public static final String CONTROLLER_NAMED = "itemDomainCableInventoryController";
    private final String DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME = "CableCatalog";                        
    private static final String DEFAULT_DOMAIN_NAME = ItemDomainName.cableInventory.getValue();
    private static ItemDomainCableInventoryController apiInstance;        
    
    @EJB
    ItemDomainCableInventoryFacade itemDomainCableInventoryFacade; 

    public static synchronized ItemDomainCableInventoryController getApiInstance() {
        if (apiInstance == null) {
            apiInstance = new ItemDomainCableInventoryController();            
            apiInstance.prepareApiInstance(); 
        }
        return apiInstance;
    }
    
    public static ItemDomainCableInventoryController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (ItemDomainCableInventoryController) findDomainController(DEFAULT_DOMAIN_NAME);
        } else {
            return getApiInstance(); 
        }
    }

    @Override
    protected ItemDomainCableInventorySettings createNewSettingObject() {
        return new ItemDomainCableInventorySettings(this);
    }

    @Override
    protected ItemDomainCableInventoryFacade getEntityDbFacade() {
        return itemDomainCableInventoryFacade; 
    }   
            
    @Override
    protected String generatePaddedUnitName(int itemNumber) {
        return ItemDomainCableInventory.generatePaddedUnitName(itemNumber);
    }

    @Override
    protected ItemCreateWizardController getItemCreateWizardController() {
        return ItemCreateWizardDomainCableInventoryController.getInstance();
    }

    public void setDefaultValuesForCurrentItem() {
        
        // get cable catalog item for this cable inventory
        ItemDomainCableInventory cableInventoryItem = getCurrent();
        if (cableInventoryItem != null) {
            ItemDomainCableCatalog cableCatalogItem = 
                    cableInventoryItem.getCatalogItem();
            if (cableCatalogItem != null) {
                
                // set the project list for inventory to that for catalog item
                if (cableCatalogItem.getItemProjectList() != null
                        & !cableCatalogItem.getItemProjectList().isEmpty()) {
                    List<ItemProject> cableCatalogItemProjectList =
                            cableCatalogItem.getItemProjectList();
                    cableInventoryItem.setItemProjectList(
                            new ArrayList<>(cableCatalogItemProjectList));
                }
                
                // derive name of inventory from catalog item
                if (cableInventoryItem.getName() == null || 
                        cableInventoryItem.getName().isEmpty()) {
                    
                    String generatedName = generateItemName(
                            cableInventoryItem, cableCatalogItem);
                    
                    cableInventoryItem.setName(generatedName);
                }
            }
        }
    }        

    @Override
    public String getDefaultDomainName() {
        return DEFAULT_DOMAIN_NAME; 
    }   

    @Override
    public boolean getEntityDisplayItemElements() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return false; 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME;
    }
    
    @Override
    protected DomainImportExportInfo initializeDomainImportInfo() {
        
        List<ImportExportFormatInfo> formatInfo = new ArrayList<>();
        formatInfo.add(new ImportExportFormatInfo("Basic Cable Inventory Format", ImportHelperCableInventory.class));
        
        String completionUrl = "/views/itemDomainCableInventory/list?faces-redirect=true";
        
        return new DomainImportExportInfo(formatInfo, completionUrl);
    }

    @Override
    protected ItemDomainCableInventoryControllerUtility createControllerUtilityInstance() {
        return new ItemDomainCableInventoryControllerUtility(); 
    }
}
