/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;

/**
 *
 * @author djarosz
 */
public interface ICdbDomainEntityController<DomainEntity extends CdbDomainEntity> extends ICdbEntityController<DomainEntity> {
    /**
     * Functionality allows for adding a certain property type for current item.
     * 
     * @param propertyType
     * @return 
     */
    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType);
    
    /**
     * Functionality restored the item to a state before the dialog was opened (dialog). 
     */
    public void restoreCurrentEditPropertyValueToOriginalState(); 
    
    /**
     * Functionality saves the edits made to the currently edit property.
     */
    public void updateEditProperty(); 
    
    /**
     * Functionality deletes the single property currently being edited.
     */
    public void deleteCurrentEditPropertyValue();
    
    /**
     * Get current edit property value for application single property edits. 
     */
    public PropertyValue getCurrentEditPropertyValue();

    /**
     * Set current edit property value for application single property edits. 
     */
    public void setCurrentEditPropertyValue(PropertyValue currentEditPropertyValue);
    
    /**
     * True when property metadata should be shown for the entity for a certain property value. 
     * 
     * @param propertyValue
     * @return 
     */
    public Boolean getDisplayPropertyMetadata(PropertyValue propertyValue);
    
    /**
     * Save the list of properties for current item.
     */
    public void savePropertyList();
}
