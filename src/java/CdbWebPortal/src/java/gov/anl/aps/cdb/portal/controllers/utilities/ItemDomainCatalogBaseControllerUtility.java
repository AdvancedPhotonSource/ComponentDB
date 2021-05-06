/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventoryBase;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.ItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darek
 * @param <ItemDomainEntity>
 * @param <ItemDomainEntityFacade>
 */
public abstract class ItemDomainCatalogBaseControllerUtility<ItemCatalogBaseDomainEntity extends ItemDomainCatalogBase, ItemDomainEntityFacade extends ItemFacadeBase<ItemCatalogBaseDomainEntity>> 
        extends ItemControllerUtility<ItemCatalogBaseDomainEntity, ItemDomainEntityFacade> {   

    @Override
    public void prepareEntityUpdate(ItemCatalogBaseDomainEntity item,  UserInfo updatedByUser) throws CdbException {
        super.prepareEntityUpdate(item, updatedByUser); 
        
        for (ItemElement itemElement : item.getFullItemElementList()) {
            if (itemElement.getId() == null) {
                // New item
                if (itemElement.getIsRequired()) {
                    addInventoryElementsForEachInventoryItem(itemElement, updatedByUser);
                    // Force reload constraint info
                    itemElement.setConstraintInformation(null);
                }
            }
        }
    }
    
    @Override
    public ItemElement createItemElement(ItemCatalogBaseDomainEntity item, UserInfo userInfo) {
        ItemElement itemElement = super.createItemElement(item, userInfo);
        itemElement.setIsRequired(true);
        itemElement.setDerivedFromItemElementList(new ArrayList<>());
               
        return itemElement; 
    }      
    
    protected void addInventoryElementsForEachInventoryItem(ItemElement catalogItemElement, UserInfo userInfo) {
        // Get fresh db representation of parent item. 
        ItemCatalogBaseDomainEntity parentItem = findById(catalogItemElement.getParentItem().getId()); 
        
        
        List<ItemDomainInventoryBase> inventoryItems = parentItem.getInventoryItemList();
        for (ItemDomainInventoryBase inventoryItem : inventoryItems) {
            // verify item element for the particular element needs to be created. 
            if (inventoryItemContainsItemElementForCatalogElement(catalogItemElement, inventoryItem)) {
                continue; 
            } 
            
            ItemControllerUtility itemControllerUtility = inventoryItem.getItemControllerUtility();                        
            
            ItemElement inventoryItemElement = itemControllerUtility.createItemElement(inventoryItem, userInfo); 
            
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
    public ItemElement finalizeItemElementRequiredStatusChanged(ItemElement itemElement, UserInfo userInfo) throws CdbException {
        itemElement = super.finalizeItemElementRequiredStatusChanged(itemElement, userInfo); 
        
        if (itemElement.getIsRequired() == false) {            
            ItemElementControllerUtility itemElementCUtility = new ItemElementControllerUtility(); 
            
            List<ItemElement> derivedItemElementList = new ArrayList<>(); 
            derivedItemElementList.addAll(itemElement.getDerivedFromItemElementList());
            
            for (ItemElement inventoryItemElement : derivedItemElementList) {
                // Verify safe to remove
                ItemElementConstraintInformation ieci; 
                ieci = itemElementCUtility.getItemElementConstraintInformation(inventoryItemElement); 
                if (ieci.isSafeToRemove()) {
                    itemElementCUtility.destroy(inventoryItemElement, userInfo);
                    // Will need to perform destroy on each inventory element. 
                    itemElement.getDerivedFromItemElementList().remove(inventoryItemElement); 
                }
            }            
        } else {
            // Item is required therefore should be part of each inventory item. 
            addInventoryElementsForEachInventoryItem(itemElement, userInfo);
        }
        return itemElement;
    }      
    
    @Override
    public void checkItemElementsForItem(ItemCatalogBaseDomainEntity item) throws CdbException {
        super.checkItemElementsForItem(item);
        
        // Item element name check occurs prior to this check. 
        for (ItemElement itemElement : item.getItemElementDisplayList()) {
            if (itemElement.getContainedItem() == null) {
                throw new CdbException("No item specified for element: " + itemElement.getName());
            }
        }
    }    

    @Override
    public boolean isEntityHasName() {
        return true;
    }

    @Override
    public boolean isEntityHasQrId() {
        return false;
    }

    @Override
    public boolean isEntityHasProject() {
        return true; 
    }    
    
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getEntityTypeName() {
        return "component";
    }

}
