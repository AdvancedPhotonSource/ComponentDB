/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignInventoryControllerUtility extends ItemDomainMachineDesignControllerUtility {
        
    @Override
    public String getDerivedFromItemTitle() {
        return "Machine Template";
    }
    
}
