/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignBaseController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignIOCController;

/**
 *
 * @author djarosz
 */
public class ItemDomainMachineDesignIOCSettings extends ItemSettings<ItemDomainMachineDesignIOCController> {

    public ItemDomainMachineDesignIOCSettings(ItemDomainMachineDesignBaseController parentController) {
        super(parentController);
        displayNumberOfItemsPerPage = 25;
    }

}
