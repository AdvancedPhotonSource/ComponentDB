/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.common.utilities.HttpLinkUtility;

/**
 * Abstract property type handler.
 *
 * This class is used as a base for all property type handlers.
 */
public abstract class AbstractPropertyTypeHandler implements PropertyTypeHandlerInterface {

    private String name = null;
    private DisplayType displayType = null;

    public static String shortenHttpLinkDisplayValueIfNeeded(String displayValue) {
        return HttpLinkUtility.prepareHttpLinkDisplayValue(displayValue);
    }

    /**
     * Constructor with a property handler name. 
     * 
     * @param name - name of property type handler. 
     */
    public AbstractPropertyTypeHandler(String name) {
        this.name = name;
    }
    
    /**
     * Constructor with a property handler name and display type. 
     * 
     * @param name - name of property type handler. 
     * @param displayType - a constant that specifies how property value should be shown. 
     */
    public AbstractPropertyTypeHandler(String name, DisplayType displayType) {
        this.name = name;
        this.displayType = displayType;
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public DisplayType getValueDisplayType() {
        return displayType;
    }
    
    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public void setInfoActionCommand(PropertyValue propertyValue){
        propertyValue.setInfoActionCommand(null);
    }
    
    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public void setInfoActionCommand(PropertyValueHistory propertyValueHistory){
        propertyValueHistory.setInfoActionCommand(null);
    }
    
    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public String getEditActionOncomplete() {
        return null;
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public String getEditActionIcon() {
        return null;
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public Boolean getDisplayEditActionButton() {
        return getEditActionIcon() != null;
    }
    
    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        propertyValue.setDisplayValueToValue();
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setDisplayValueToValue();
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        propertyValue.setTargetValueToValue();
    }

    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setTargetValueToValue();
    }
    
    /**
     * See PropertyTypeHandlerInterface
     */
    @Override
    public String getPropertyEditPage(){
        return null; 
    }
}
