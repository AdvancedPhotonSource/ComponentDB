/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 * Property type handler interface.
 */
public interface PropertyTypeHandlerInterface 
{   
    
    public abstract String getName();
    
    public abstract String getEditActionOncomplete();
 
    public abstract String getEditActionIcon();

    public abstract String getEditActionBean();
    
    public abstract Boolean getDisplayEditActionButton();
    
    public abstract DisplayType getValueDisplayType();
    
    public abstract void setInfoActionCommand(PropertyValue propertyValue); 
    
    public abstract void setInfoActionCommand(PropertyValueHistory propertyValueHistory); 
    
    public abstract void setDisplayValue(PropertyValue propertyValue);
    
    public abstract void setDisplayValue(PropertyValueHistory propertyValueHistory);

    public abstract void setTargetValue(PropertyValue propertyValue);
    
    public abstract void setTargetValue(PropertyValueHistory propertyValueHistory);    
    
    public abstract void resetOneTimeUseVariables(); 
    
    public abstract String getPropertyEditPage(); 
}
