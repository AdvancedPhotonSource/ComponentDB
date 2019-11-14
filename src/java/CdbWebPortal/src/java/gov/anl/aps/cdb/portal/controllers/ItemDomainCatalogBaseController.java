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
import gov.anl.aps.cdb.portal.controllers.settings.ItemSettings;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public abstract class ItemDomainCatalogBaseController<ItemCatalogBaseDomainEntity extends ItemDomainCatalogBase, ItemDomainCatalogEntityBaseFacade extends ItemFacadeBase<ItemCatalogBaseDomainEntity>, ItemCatalogEntityBaseSettingsObject extends ItemSettings> extends ItemController<ItemCatalogBaseDomainEntity, ItemDomainCatalogEntityBaseFacade, ItemCatalogEntityBaseSettingsObject>  {

    private final String DOMAIN_TYPE_NAME = ItemDomainName.catalog.getValue();
    private final String DERIVED_DOMAIN_NAME = "Inventory";        
    
    private static final Logger logger = Logger.getLogger(ItemDomainCatalogBaseController.class.getName());                                

    @Override
    public List<ItemCatalogBaseDomainEntity> getItemList() {
        List<ItemCatalogBaseDomainEntity> itemList = super.getItemList();
        return itemList; 
    }            

    @Override
    protected ItemCreateWizardController getItemCreateWizardController() {
        return ItemCreateWizardDomainCatalogController.getInstance(); 
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
    protected ItemCatalogBaseDomainEntity cloneCreateItemElements(ItemCatalogBaseDomainEntity clonedItem, ItemCatalogBaseDomainEntity cloningFrom) {
        return cloneCreateItemElements(clonedItem, cloningFrom, true);
    } 

    @Override
    public ItemElement createItemElement(ItemCatalogBaseDomainEntity item) {
        ItemElement itemElement = super.createItemElement(item);
        itemElement.setIsRequired(true);
        itemElement.setDerivedFromItemElementList(new ArrayList<>());
               
        return itemElement; 
    }
    
    protected void addInventoryElementsForEachInventoryItem(ItemElement catalogItemElement) {
        // Get fresh db representation of parent item. 
        ItemCatalogBaseDomainEntity parentItem = findById(catalogItemElement.getParentItem().getId()); 
        
        
        List<ItemDomainInventoryBase> inventoryItems = parentItem.getInventoryItemList();
        for (ItemDomainInventoryBase inventoryItem : inventoryItems) {
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
    
    private boolean inventoryItemContainsItemElementForCatalogElement(ItemElement catalogElement, ItemDomainInventoryBase inventoryItem) {
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
    protected void checkItem(ItemCatalogBaseDomainEntity catalogItem) throws CdbException {
        super.checkItem(catalogItem);
        
    } 

    @Override
    protected void checkItemElementsForItem(ItemCatalogBaseDomainEntity item) throws CdbException {
        super.checkItemElementsForItem(item);
        
        // Item element name check occurs prior to this check. 
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getContainedItem() == null) {
                throw new CdbException("No item specified for element: " + itemElement.getName());
            }
        }
    }

    @Override
    public void prepareEntityUpdate(ItemCatalogBaseDomainEntity item) throws CdbException {
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
    protected ItemCatalogBaseDomainEntity createEntityInstance() {
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
    public boolean getEntityDisplayItemConnectors() {
        return true; 
    }    

}
