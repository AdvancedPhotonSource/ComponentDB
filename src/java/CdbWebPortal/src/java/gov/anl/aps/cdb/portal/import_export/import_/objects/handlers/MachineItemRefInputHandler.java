/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class MachineItemRefInputHandler extends RefInputHandler {
    
    private ItemDomainMachineDesign rootItem = null;
    
    public MachineItemRefInputHandler(
            int columnIndex,
            String columnName,
            String propertyName,
            String setterMethod,
            CdbEntityController controller,
            Class paramType,
            ItemDomainMachineDesign rootItem,
            boolean idOnly,
            boolean singleValue,
            boolean allowPaths) {
        
        super(
                columnIndex, 
                columnName, 
                propertyName, 
                setterMethod, 
                controller, 
                paramType, 
                null, 
                idOnly, 
                singleValue,
                allowPaths);
        
        this.rootItem = rootItem;
    }
    
    private ItemDomainMachineDesignController getController() {
        return ItemDomainMachineDesignController.getInstance();
    }
    
    /**
     * Finds machine item with specified name and checks that its top-level 
     * parent item is rootParentItem. 
     */
    private ParseInfo findItemByNameAndRootParent(
            String itemName, ItemDomainMachineDesign rootParentItem) {
        
        List<ItemDomainMachineDesign> matches = new ArrayList<>();
        
        List<ItemDomainMachineDesign> itemsByName
                = ItemDomainMachineDesignFacade.getInstance().findByName(itemName);
        for (ItemDomainMachineDesign item : itemsByName) {
            ItemDomainMachineDesign parentItem = item.getParentMachineDesign();
            while (parentItem != null) {
                // check root match
                if ((parentItem.getParentMachineDesign() == null)
                        && (parentItem.getId().equals(rootParentItem.getId()))) {
                    matches.add(item);
                    break;
                }

                parentItem = parentItem.getParentMachineDesign();
            }
        }
        
        // check for multiple matches
        CdbEntity entity = null;
        boolean isValid = true;
        String validString = "";
        if (matches.size() == 0) {
            isValid = false;
            validString = "no item found with name: " + itemName + " and root item: " + rootParentItem.getName();
        } else if (matches.size() == 1) {
            entity = matches.get(0);
            entity = super.getCacheObject(getController(), entity);
        }
        else if (matches.size() > 1) {
            // more than one match
            isValid = false;
            validString = "multiple items found with name: " + itemName + " and root item: " + rootParentItem.getName();
        }
        
        return new ParseInfo(entity, isValid, validString);
        
    }


    /**
     * Performs machine hierarchy constrained lookup if rootItem is specified. 
     */
    @Override
    protected ParseInfo getSingleObjectByName(String nameString) {
        
        if (rootItem == null) {
            return super.getSingleObjectByName(nameString);
            
        } else {
            
            CdbEntity objValue = null;
            ParseInfo result = findItemByNameAndRootParent(nameString, rootItem);
            
            if (!result.getValidInfo().isValid()) {
                return result;
            }
            
            objValue = (ItemDomainMachineDesign)(result.getValue());
            
            return new ParseInfo(objValue, true, "");
        }
    }    
}
