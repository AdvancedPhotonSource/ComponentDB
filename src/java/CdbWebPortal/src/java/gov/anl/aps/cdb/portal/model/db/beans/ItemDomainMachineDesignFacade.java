/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ListTbl;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author djarosz
 */
@Stateless
public class ItemDomainMachineDesignFacade extends ItemFacadeBase<ItemDomainMachineDesign> {
    
    @Override
    public ItemDomainName getDomain() {
        return ItemDomainName.machineDesign;
    }
    
    public ItemDomainMachineDesignFacade() {
        super(ItemDomainMachineDesign.class);
    }
    
    /**
     * Updates machine design item.  Overridden here because, if we move an item from
     * the root level of the machine hierarchy to a new parent item, we need to create
     * the new ItemElement in the database in the course of updating the existing machine
     * item.
     */
    @Override
    public ItemDomainMachineDesign edit(ItemDomainMachineDesign entity) {
        
        ItemDomainMachineDesign result = super.edit(entity);
        
        for (ItemElement element : entity.getElementsToCreate()) {
            ItemElementFacade.getInstance().create(element);
        }
        entity.clearElementsToCreate();
        
        for (ItemDomainMachineDesign item : entity.getItemsToUpdate()) {
            this.edit(item);
        }
        entity.clearItemsToUpdate();
        
        return result;
    } 
    
    public List<ItemDomainMachineDesign> getIOCItems() {
        return findByDomainAndEntityType(
                ItemDomainName.machineDesign.getValue(),
                EntityTypeName.ioc.getValue()
        ); 
    }

    public List<ItemDomainMachineDesign> getMachineDesignTemplates() {
        return findByDomainAndEntityType(
                ItemDomainName.machineDesign.getValue(),
                EntityTypeName.template.getValue()
        ); 
    }
    
    public List<ItemDomainMachineDesign> getDeletedItems() {
        return findByDomainAndEntityType(
                ItemDomainName.machineDesign.getValue(),
                EntityTypeName.deleted.getValue()
        ); 
    }
    
    public List<ItemDomainMachineDesign> getTopLevelMachineDesignControl() {
        return findByDomainAndEntityTypeAndTopLevelExcludeEntityType(
                ItemDomainName.machineDesign.getValue(),
                EntityTypeName.control.getValue(),
                EntityTypeName.deleted.getValue()
        ); 
    }
    
    public List<ItemDomainMachineDesign> getTopLevelMachineDesignPower() {
        return findByDomainAndEntityTypeAndTopLevelExcludeEntityType(
                ItemDomainName.machineDesign.getValue(),
                EntityTypeName.power.getValue(),
                EntityTypeName.deleted.getValue()
        ); 
    }
    
    public List<ItemDomainMachineDesign> getTopLevelMachineDesignInventory() {        
        return findByDomainAndEntityTypeAndTopLevelOrderByDerivedFromItemExcludeEntityType(
                ItemDomainName.machineDesign.getValue(),
                EntityTypeName.inventory.getValue(),
                EntityTypeName.deleted.getValue()
        ); 
    }
    
    public List<ItemDomainMachineDesign> getMachineDesignInventoryInList(ListTbl list) {
        return getItemListContainedInListWithEntityType(
                ItemDomainName.machineDesign.getValue(),
                list, 
                EntityTypeName.inventory.getValue()); 
    }
    
    public static ItemDomainMachineDesignFacade getInstance() {
        return (ItemDomainMachineDesignFacade) SessionUtility.findFacade(ItemDomainMachineDesignFacade.class.getSimpleName()); 
    }
    
    public List<ItemDomainMachineDesign> findByAlternateName(String value) {
        return findByItemIdentifier1(value);
    }
    
    public List<ItemDomainMachineDesign> findByName(String name) {
        return findByDomainAndName(getDomainName(), name);
    }  
    
    /**
     * Finds unique template item by name, excludes deleted items. 
     */
    public ItemDomainMachineDesign findUniqueTemplateByName(String templateName) throws CdbException {
        
        if (templateName == null) {
            return null;
        }
        
        String domainName = getDomainName();
        if ((domainName == null) || domainName.isEmpty()) {
            throw new CdbException("findUniqueTemplateByName() error getting domain name");
        }

        List<ItemDomainMachineDesign> items = 
                findByDomainAndEntityTypeAndNameExcludeEntityType(
                        domainName, 
                        EntityTypeName.template.getValue(), 
                        templateName,
                        EntityTypeName.deleted.getValue());
        if (items.size() > 1) {
            // ambiguous result, throw exception
            throw new CdbException("findUniqueByEntityTypeAndName() returns multiple instances");
        } else if (items.size() == 0) {
            // no items found
            return null;
        } else {
            // return single item returned by query
            return items.get(0);
        }
    }
}
