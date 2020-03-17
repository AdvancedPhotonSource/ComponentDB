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
    
    protected String voltageFilter;
    protected String layingFilter;
    
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

    public String getVoltageFilter() {
        return voltageFilter;
    }

    public void setVoltageFilter(String voltageFilter) {
        this.voltageFilter = voltageFilter;
    }

    public String getLayingFilter() {
        return layingFilter;
    }

    public void setLayingFilter(String layingFilter) {
        this.layingFilter = layingFilter;
    }

}
