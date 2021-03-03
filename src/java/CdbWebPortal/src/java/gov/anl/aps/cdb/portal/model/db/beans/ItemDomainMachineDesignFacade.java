/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
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
    public String getDomainName() {
        return ItemDomainName.machineDesign.getValue();
    }
    
    public ItemDomainMachineDesignFacade() {
        super(ItemDomainMachineDesign.class);
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
    
}
