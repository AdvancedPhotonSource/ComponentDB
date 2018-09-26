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
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainCatalogSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCatalogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.beans.SparePartsBean;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named("itemDomainCatalogController")
@SessionScoped
public class ItemDomainCatalogController extends ItemController<ItemDomainCatalog, ItemDomainCatalogFacade, ItemDomainCatalogSettings>  {

    private final String DOMAIN_TYPE_NAME = ItemDomainName.catalog.getValue();
    private final String DERIVED_DOMAIN_NAME = "Inventory";    
    
    private static final Logger logger = Logger.getLogger(ItemDomainCatalogController.class.getName());          

    private List<ItemDomainInventory> inventorySparesList = null;
    private List<ItemDomainInventory> inventoryNonSparesList = null;
    private Boolean displayInventorySpares = null;        
    
    @EJB
    ItemDomainCatalogFacade itemDomainCatalogFacade;    

    @Override
    public List<ItemDomainCatalog> getItemList() {
        List<ItemDomainCatalog> itemList = super.getItemList();
        return itemList; 
    }

    public ItemDomainCatalogController() {
        super();
    } 
    
    public static ItemDomainCatalogController getInstance() {
        return (ItemDomainCatalogController) SessionUtility.findBean("itemDomainCatalogController");
    }

    @Override
    protected ItemCreateWizardController getItemCreateWizardController() {
        return ItemCreateWizardDomainCatalogController.getInstance(); 
    }

    @Override
    public ItemMultiEditController getItemMultiEditController() {
        return ItemMultiEditDomainCatalogController.getInstance();
    } 

    @Override
    public ItemEnforcedPropertiesController getItemEnforcedPropertiesController() {
        return ItemEnforcedPropertiesDomainCatalogController.getInstance();        
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Catalog Item";
    }    

    @Override
    protected ItemDomainCatalog cloneCreateItemElements(ItemDomainCatalog clonedItem, ItemDomainCatalog cloningFrom) {
        return cloneCreateItemElements(clonedItem, cloningFrom, true);
    } 

    @Override
    public ItemElement createItemElement(ItemDomainCatalog item) {
        ItemElement itemElement = super.createItemElement(item);
        itemElement.setIsRequired(true);
        itemElement.setDerivedFromItemElementList(new ArrayList<>());
               
        return itemElement; 
    }
    
    protected void addInventoryElementsForEachInventoryItem(ItemElement catalogItemElement) {
        // Get fresh db representation of parent item. 
        ItemDomainCatalog parentItem = findById(catalogItemElement.getParentItem().getId()); 
        
        
        List<ItemDomainInventory> inventoryItems = parentItem.getInventoryItemList();
        for (ItemDomainInventory inventoryItem : inventoryItems) {
            // verify item element for the particular element needs to be created. 
            if (inventoryItemContainsItemElementForCatalogElement(catalogItemElement, inventoryItem)) {
                continue; 
            } 
            
            ItemController inventoryItemController = ItemController.findDomainControllerForItem(inventoryItem); 
            ItemElement inventoryItemElement = inventoryItemController.createItemElement(inventoryItem); 
            
            inventoryItemElement.setDerivedFromItemElement(catalogItemElement);
            EntityInfo inventoryItemEntityInfo = inventoryItem.getEntityInfo(); 
            UserInfo inventoryItemOwner = inventoryItemEntityInfo.getOwnerUser(); 
            UserGroup inventoryOwnerGroup = inventoryItemEntityInfo.getOwnerUserGroup(); 
            inventoryItemElement.getEntityInfo().setOwnerUser(inventoryItemOwner);
            inventoryItemElement.getEntityInfo().setOwnerUserGroup(inventoryOwnerGroup);
            catalogItemElement.getDerivedFromItemElementList().add(inventoryItemElement); 
        }       
    }
    
    private boolean inventoryItemContainsItemElementForCatalogElement(ItemElement catalogElement, ItemDomainInventory inventoryItem) {
        for (ItemElement inventoryElement : inventoryItem.getItemElementDisplayList()) {
            if (inventoryElement.getDerivedFromItemElement().getId().equals(catalogElement.getId())) {
                return true; 
            }
        }
        return false; 
    }

    @Override
    public ItemElementConstraintInformation loadItemElementConstraintInformation(ItemElement itemElement) {
        return new CatalogItemElementConstraintInformation(itemElement);
    } 

    @Override
    public ItemElement finalizeItemElementRequiredStatusChanged(ItemElement itemElement) throws CdbException {
        itemElement = super.finalizeItemElementRequiredStatusChanged(itemElement); 
        
        if (itemElement.getIsRequired() == false) {
            ItemElementController itemElementController = ItemElementController.getInstance(); 
            
            List<ItemElement> derivedItemElementList = new ArrayList<>(); 
            derivedItemElementList.addAll(itemElement.getDerivedFromItemElementList());
            
            for (ItemElement inventoryItemElement : derivedItemElementList) {
                // Verify safe to remove
                ItemElementConstraintInformation ieci; 
                ieci = itemElementController.getItemElementConstraintInformation(inventoryItemElement); 
                if (ieci.isSafeToRemove()) {
                    itemElementController.destroy(inventoryItemElement);
                    // Will need to perform destroy on each inventory element. 
                    itemElement.getDerivedFromItemElementList().remove(inventoryItemElement); 
                }
            }
            itemElementController.setCurrent(itemElement);
        } else {
            // Item is required therefore should be part of each inventory item. 
            addInventoryElementsForEachInventoryItem(itemElement);
        }
        return itemElement;
    }
    
    @Override
    protected void checkItem(ItemDomainCatalog catalogItem) throws CdbException {
        super.checkItem(catalogItem);
        
    } 

    @Override
    protected void checkItemElementsForItem(ItemDomainCatalog item) throws CdbException {
        super.checkItemElementsForItem(item);
        
        // Item element name check occurs prior to this check. 
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getContainedItem() == null) {
                throw new CdbException("No item specified for element: " + itemElement.getName());
            }
        }
    }

    @Override
    public void prepareEntityUpdate(ItemDomainCatalog item) throws CdbException {
        super.prepareEntityUpdate(item); 
        
        for (ItemElement itemElement : item.getFullItemElementList()) {
            if (itemElement.getId() == null) {
                // New item
                if (itemElement.getIsRequired()) {
                    addInventoryElementsForEachInventoryItem(itemElement);
                    // Force reload constraint info
                    itemElement.setConstraintInformation(null);
                }
            }
        }
    }

    @Override
    protected ItemDomainCatalog createEntityInstance() {
        ItemDomainCatalog newItem = super.createEntityInstance();
        if (getCurrentItemProject() != null) {
            List<ItemProject> itemProjectList = new ArrayList<>();
            itemProjectList.add(getCurrentItemProject());
            newItem.setItemProjectList(itemProjectList);
        }
        return newItem;
    }   

    @Override
    protected void resetVariablesForCurrent() {
        super.resetVariablesForCurrent();
        inventoryNonSparesList = null;
        inventorySparesList = null;
        displayInventorySpares = null;
    }

    public List<ItemDomainInventory> getInventorySparesList() {
        if (inventorySparesList == null) {
            ItemDomainCatalog currentItem = getCurrent();
            if (current != null) {
                inventorySparesList = new ArrayList<>();
                for (ItemDomainInventory inventoryItem : currentItem.getInventoryItemList()) {
                    if (inventoryItem.getSparePartIndicator()) {
                        inventorySparesList.add(inventoryItem);
                    }
                }
            }
        }
        return inventorySparesList;
    }

    public List<ItemDomainInventory> getInventoryNonSparesList() {
        if (inventoryNonSparesList == null) {
            ItemDomainCatalog currentItem = getCurrent();
            if (currentItem != null) {
                List<ItemDomainInventory> spareItems = getInventorySparesList();
                List<ItemDomainInventory> allInventoryItems = getCurrent().getInventoryItemList();
                inventoryNonSparesList = new ArrayList<>(allInventoryItems);
                inventoryNonSparesList.removeAll(spareItems);
            }
        }
        return inventoryNonSparesList;
    }

    public int getInventorySparesCount() {
        List<ItemDomainInventory> sparesList = getInventorySparesList();
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
        List<ItemDomainInventory> nonSparesList = getInventoryNonSparesList();
        if (nonSparesList != null) {
            return nonSparesList.size();
        }
        return 0;
    }

    public Boolean getDisplayInventorySpares() {
        if (displayInventorySpares == null) {
            displayInventorySpares = SparePartsBean.isItemContainSparePartConfiguration(getCurrent());
        }
        return displayInventorySpares;
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
    public boolean getEntityDisplayItemName() {
        return true;
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false;
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false;
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        return "Inventory";
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public boolean getEntityDisplayItemProject() {
        return true;
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
    protected ItemDomainCatalog instenciateNewItemDomainEntity() {
        return new ItemDomainCatalog();
    }

    @Override
    protected ItemDomainCatalogFacade getEntityDbFacade() {
        return itemDomainCatalogFacade;         
    }
    
    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true; 
    }     

    @Override
    protected ItemDomainCatalogSettings createNewSettingObject() {
        return new ItemDomainCatalogSettings(this);
    }   

}
