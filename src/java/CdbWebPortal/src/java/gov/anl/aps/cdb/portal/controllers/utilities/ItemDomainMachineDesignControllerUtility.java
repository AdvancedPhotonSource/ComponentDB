/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignControllerUtility extends ItemDomainMachineDesignBaseControllerUtility {

    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignControllerUtility.class.getName());

    public ItemDomainMachineDesignControllerUtility() {
        super();        
    }

    @Override
    public ItemElement performMachineMove(ItemDomainMachineDesign newParent, ItemDomainMachineDesign child, UserInfo sessionUser) throws CdbException {
        ItemElement representsCatalogElement = child.getRepresentsCatalogElement();
        
        if (representsCatalogElement != null) {
            throw new CdbException("Cannot move assembly representing machine element.");
        }        
        
        return super.performMachineMove(newParent, child, sessionUser); 
    }
    
}
