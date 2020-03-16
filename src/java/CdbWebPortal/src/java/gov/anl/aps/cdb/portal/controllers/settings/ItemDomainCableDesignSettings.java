/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;

/**
 *
 * @author djarosz
 */
public class ItemDomainCableDesignSettings extends ItemSettings<ItemDomainCableDesignController> {
    
    protected boolean voltageDisplay;
    protected boolean layingDisplay;
    
    public ItemDomainCableDesignSettings(ItemDomainCableDesignController parentController) {
        super(parentController);
    }

    public boolean isVoltageDisplay() {
        return voltageDisplay;
    }

    public void setVoltageDisplay(boolean voltageDisplay) {
        this.voltageDisplay = voltageDisplay;
    }

    public boolean isLayingDisplay() {
        return layingDisplay;
    }

    public void setLayingDisplay(boolean layingDisplay) {
        this.layingDisplay = layingDisplay;
    }

}
