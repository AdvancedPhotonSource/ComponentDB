/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignPowerControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {
    
    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignPowerControllerUtility.class.getName());            
        
    @Override
    public List<ItemDomainMachineDesign> getItemList() {
        return itemFacade.getTopLevelMachineDesignPower();
    }    

    public void assignPowerAttributes(ItemDomainMachineDesign newInventory, UserInfo sessionUser) {
        String inventoryetn = EntityTypeName.power.getValue();
        EntityType inventoryet = entityTypeFacade.findByName(inventoryetn);
        if (newInventory.getEntityTypeList() == null) {
            try {
                newInventory.setEntityTypeList(new ArrayList());
            } catch (CdbException ex) {
                logger.error(ex);
            }
        }
        newInventory.getEntityTypeList().add(inventoryet);                
    } 

    @Override
    public ItemDomainMachineDesign createEntityInstance(UserInfo sessionUser) {
        ItemDomainMachineDesign newPower = super.createEntityInstance(sessionUser); 
        
        assignPowerAttributes(newPower, sessionUser);
        
        return newPower; 
    }
    
}
