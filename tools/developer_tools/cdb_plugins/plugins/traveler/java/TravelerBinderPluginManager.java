/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class TravelerBinderPluginManager extends TravelerPluginManager {
    
    private static final Logger logger = LogManager.getLogger(TravelerBinderPluginManager.class.getName());

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new TravelerBinderPropertyTypeHandler(); 
        }
        return propertyTypeHandler;
    }
    
}
