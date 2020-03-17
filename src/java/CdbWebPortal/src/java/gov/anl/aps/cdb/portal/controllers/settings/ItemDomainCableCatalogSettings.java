/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.settings;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;

/**
 *
 * @author djarosz
 */
public class ItemDomainCableCatalogSettings extends ItemSettings<ItemDomainCableCatalogController> {
    
    protected boolean heatLimitDisplay;
    protected boolean insulationDisplay;
    protected boolean jacketColorDisplay;
    protected boolean weightDisplay;
    protected boolean diameterDisplay;
    protected boolean urlDisplay;
    protected boolean bendRadiusDisplay;
    protected boolean radToleranceDisplay;
    protected boolean conductorsDisplay;
    protected boolean imageUrlDisplay;
    protected boolean fireLoadDisplay;
    protected boolean voltageRatingDisplay;
    
    public ItemDomainCableCatalogSettings(ItemDomainCableCatalogController parentController) {
        super(parentController);
    }
    
    public boolean isHeatLimitDisplay() {
        return heatLimitDisplay;
    }

    public void setHeatLimitDisplay(boolean heatLimitDisplay) {
        this.heatLimitDisplay = heatLimitDisplay;
    }

    public boolean isInsulationDisplay() {
        return insulationDisplay;
    }

    public void setInsulationDisplay(boolean insulationDisplay) {
        this.insulationDisplay = insulationDisplay;
    }

    public boolean isJacketColorDisplay() {
        return jacketColorDisplay;
    }

    public void setJacketColorDisplay(boolean jacketColorDisplay) {
        this.jacketColorDisplay = jacketColorDisplay;
    }

    public boolean isWeightDisplay() {
        return weightDisplay;
    }

    public void setWeightDisplay(boolean weightDisplay) {
        this.weightDisplay = weightDisplay;
    }

    public boolean isDiameterDisplay() {
        return diameterDisplay;
    }

    public void setDiameterDisplay(boolean diameterDisplay) {
        this.diameterDisplay = diameterDisplay;
    }

    public boolean isUrlDisplay() {
        return urlDisplay;
    }

    public void setUrlDisplay(boolean urlDisplay) {
        this.urlDisplay = urlDisplay;
    }

    public boolean isBendRadiusDisplay() {
        return bendRadiusDisplay;
    }

    public void setBendRadiusDisplay(boolean bendRadiusDisplay) {
        this.bendRadiusDisplay = bendRadiusDisplay;
    }

    public boolean isRadToleranceDisplay() {
        return radToleranceDisplay;
    }

    public void setRadToleranceDisplay(boolean radToleranceDisplay) {
        this.radToleranceDisplay = radToleranceDisplay;
    }

    public boolean isConductorsDisplay() {
        return conductorsDisplay;
    }

    public void setConductorsDisplay(boolean conductorsDisplay) {
        this.conductorsDisplay = conductorsDisplay;
    }

    public boolean isImageUrlDisplay() {
        return imageUrlDisplay;
    }

    public void setImageUrlDisplay(boolean imageUrlDisplay) {
        this.imageUrlDisplay = imageUrlDisplay;
    }

    public boolean isFireLoadDisplay() {
        return fireLoadDisplay;
    }

    public void setFireLoadDisplay(boolean fireLoadDisplay) {
        this.fireLoadDisplay = fireLoadDisplay;
    }

    public boolean isVoltageRatingDisplay() {
        return voltageRatingDisplay;
    }

    public void setVoltageRatingDisplay(boolean voltageRatingDisplay) {
        this.voltageRatingDisplay = voltageRatingDisplay;
    }
     
}
