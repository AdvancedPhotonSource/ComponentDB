/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.List;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignDeletedControllerUtility extends ItemDomainMachineDesignControllerUtility {
            
    @Override
    public List<ItemDomainMachineDesign> getItemList() {
        return itemFacade.getDeletedItems();
    }
    
}
