/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import java.io.Serializable;
import javax.inject.Named;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.plugins.CdbPluginManager;
import javax.enterprise.context.SessionScoped;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named("propertyValueInfoActionBean")
@SessionScoped
public class PropertyValueInfoActionBean implements Serializable {

    private PropertyValue propertyValue = null;
    private String value;

    private static final Logger logger = Logger.getLogger(PropertyValueInfoActionBean.class.getName());

    private CdbPluginManager cdbPluginManager; 

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    /**
     * Sets the propertyValue and value used by specific beans.
     *
     * @param propertyValue property value of current row. Current property value. 
     * @param value History property value. For infoAction display type handlers, only use this value for the bean. 
     */
    public void setPropertyValue(PropertyValue propertyValue, String value) {
        // Get value for optional parameter
        if (value == null) {
            value = propertyValue.getValue();
        }
        this.value = value;

        this.propertyValue = propertyValue;
    }
    
    /**
     * Executes the appropriate function for the currently loaded property value into the bean.
     */
    public void loadInfoActionForLoadedPropertyValue(){        
        if (cdbPluginManager == null) {
            cdbPluginManager = CdbPluginManager.getInstance();
        }
        cdbPluginManager.loadInfoActionForPropertyValue(propertyValue);        
    }      

    /**
     * Executes setPropertyValue function. 
     * 
     * @param propertyValue propertyValue passed from the entity DataTable upon user infoAction request. 
     */
    public void setPropertyValue(PropertyValue propertyValue) {
        setPropertyValue(propertyValue, null);
    }

    /**
     * Executes setPropertyValue function.
     * 
     * @param propertyValueHistory stores current property value as well as selected history value. 
     */
    public void setPropertyValueHistory(PropertyValueHistory propertyValueHistory) {
        setPropertyValue(propertyValueHistory.getPropertyValue(), propertyValueHistory.getValue());
    }

    public String getValue() {
        return value;
    }
}
