/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerInstancePluginManager extends TravelerPluginManager {
    
    private static final Logger logger = Logger.getLogger(TravelerInstancePluginManager.class.getName());

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new TravelerInstancePropertyTypeHandler(); 
        }
        return propertyTypeHandler;
    }   

    /*
    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        TravelerBean travelerBean = TravelerBean.getInstance();        
        logger.debug("Info action of type Traveler Instance, id: " + propertyValue.getValue());        
        travelerBean.setPropertyValue(propertyValue);
        travelerBean.loadCurrentTravelerInstance(propertyValue.getInfoActionCommand());
    } 
    */
    
}
