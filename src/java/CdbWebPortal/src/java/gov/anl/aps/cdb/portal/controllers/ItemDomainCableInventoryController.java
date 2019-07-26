/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCableInventorySettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableInventoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainCableInventoryController.CONTROLLER_NAMED)
@SessionScoped
public class ItemDomainCableInventoryController extends ItemController<ItemDomainCableInventory, ItemDomainCableInventoryFacade, ItemDomainCableInventorySettings> {
    
    public static final String CONTROLLER_NAMED = "itemDomainCableInventoryController";
    private final String DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME = "CableCatalog";                        
    private static final String DEFAULT_DOMAIN_NAME = ItemDomainName.cableInventory.getValue();
    private static ItemDomainCableInventoryController apiInstance;        
    
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

    public void setDefaultValuesForCurrentItem() {
        // get cable catalog item for this cable inventory
        ItemDomainCableInventory cableInventoryItem = getCurrent();
        if (cableInventoryItem != null) {
            ItemDomainCableCatalog cableCatalogItem = 
                    cableInventoryItem.getCableCatalogItem();
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
                    List<ItemDomainCableInventory> ItemInventoryItemList = 
                            cableCatalogItem.getCableInventoryItemList();
                    // Copy list to not update actual derived from item list. 
                    List<ItemDomainCableInventory> inventoryItemList = 
                            new ArrayList<>(ItemInventoryItemList);
                    if (isItemExistInDb(cableInventoryItem) == false) {
                        if (inventoryItemList.contains(cableInventoryItem)) {
                            // Remove since it is not yet existing. 
                            inventoryItemList.remove(cableInventoryItem);
                        }
                    }
                    DataModel cableInventoryDataModel = 
                            new ListDataModel(inventoryItemList);
                    cableInventoryItem.setName("Unit: " + 
                            (cableInventoryDataModel.getRowCount() + 1) + "");
                }
            }
        }
    }
    
    @EJB
    ItemDomainCableInventoryFacade itemDomainCableInventoryFacade; 

    @Override
    protected ItemDomainCableInventory instenciateNewItemDomainEntity() {
        return new ItemDomainCableInventory(); 
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
    public String getEntityTypeName() {
        return "cableInventory"; 
    } 

    @Override
    public String getDisplayEntityTypeName() {
        return "Cable Inventory";
    }

    @Override
    public String getDefaultDomainName() {
        return DEFAULT_DOMAIN_NAME; 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return true;
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
    public boolean getEntityDisplayItemSources() {
        return false;
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
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false; 
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        return "Cable Catalog Item";
    }

    @Override
    public String getStyleName() {
        return "inventory"; 
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        return DEFAULT_DOMAIN_DERIVED_FROM_ITEM_DOMAIN_NAME;
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        return null;
    } 

}
