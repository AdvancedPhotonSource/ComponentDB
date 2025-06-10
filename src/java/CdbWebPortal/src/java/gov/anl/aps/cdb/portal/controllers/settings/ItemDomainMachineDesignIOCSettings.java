/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignBaseController;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignIOCSettings extends ItemDomainMachineDesignSettings {

    public ItemDomainMachineDesignIOCSettings(ItemDomainMachineDesignBaseController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
    }

}
