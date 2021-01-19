/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignControllerUtility extends ItemControllerUtility<ItemDomainMachineDesign, ItemDomainMachineDesignFacade> {
    
    @Override    
    protected boolean verifyItemNameCombinationUniqueness(Item item) {
        boolean unique = super.verifyItemNameCombinationUniqueness(item);

        // Ensure all machine designs are unique
        if (!unique) {
            String viewUUID = item.getViewUUID();
            item.setItemIdentifier2(viewUUID);
            unique = true;
        }

        return unique;
    }
    
    @Override
    public void checkItem(ItemDomainMachineDesign item) throws CdbException {
        super.checkItem(item);

        if (item.getIsItemTemplate()) {
            List<ItemElement> itemElementMemberList = item.getItemElementMemberList();
            if (itemElementMemberList == null || itemElementMemberList.isEmpty()) {
                // Item is not a child of another item. 
                if (!verifyValidTemplateName(item.getName())) {
                    throw new CdbException("Place parements within {} in template name. Example: 'templateName {paramName}'");
                }
            }
        }

        Item newAssignedItem = item.getAssignedItem();
        if (newAssignedItem != null) {
            if ((newAssignedItem instanceof ItemDomainCatalog || newAssignedItem instanceof ItemDomainInventory) == false) {
                throw new CdbException("The new assigned item must be either catalog or inventory item.");
            }

            Integer itemId = item.getId();
            if (itemId != null) {                
                ItemDomainMachineDesign originalItem = findById(itemId);

                Item origAssignedItem = originalItem.getAssignedItem();

                if (origAssignedItem != null) {
                    ItemDomainCatalog catItem = null;
                    if (origAssignedItem instanceof ItemDomainInventory) {
                        catItem = ((ItemDomainInventory) origAssignedItem).getCatalogItem();
                    } else if (origAssignedItem instanceof ItemDomainCatalog) {
                        catItem = (ItemDomainCatalog) origAssignedItem;
                    }

                    if (newAssignedItem instanceof ItemDomainInventory) {
                        List<ItemDomainInventory> inventoryItemList = catItem.getInventoryItemList();
                        if (inventoryItemList.contains(newAssignedItem) == false) {
                            throw new CdbException("The new assigned inventory item must be of catalog item: " + catItem.getName() + ".");
                        }
                    }
                }
            }
        }
    }   
    
    private boolean verifyValidTemplateName(String templateName) {
        boolean validTitle = false;
        if (templateName.contains("{")) {
            int openBraceIndex = templateName.indexOf("{");
            int closeBraceIndex = templateName.indexOf("}");
            if (openBraceIndex < closeBraceIndex) {
                validTitle = true;
            }
        }
        
        return validTitle;
    }

    @Override
    public boolean isEntityHasItemIdentifier2() {
        return false;
    }

    @Override
    public boolean isEntityHasQrId() {
        //TODO add a machine design template and inventory and override with false; 
        return true; 
    }

    @Override
    public boolean isEntityHasName() {
        return true; 
    }

    @Override
    public boolean isEntityHasProject() {
        return true; 
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.machineDesign.getValue(); 
    }

    @Override
    protected ItemDomainMachineDesignFacade getItemFacadeInstance() {
        return ItemDomainMachineDesignFacade.getInstance(); 
    }       

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public String getEntityTypeName() {
        return "itemMachineDesign";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Machine Design Item";
    }
    
}
