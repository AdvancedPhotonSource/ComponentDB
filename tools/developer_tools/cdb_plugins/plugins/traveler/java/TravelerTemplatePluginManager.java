/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;

/**
 *
 * @author djarosz
 */
public class TravelerTemplatePluginManager extends TravelerPluginManager {

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new TravelerTemplatePropertyTypeHandler(); 
        }
        return propertyTypeHandler;
    }   
    
}
