/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;

/**
 *
 * @author djarosz
 */
public class DocManagamentContainerPluginManager extends PluginManagerBase {                     

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DocManagamentContainerPropertyTypeHandler();
        }
        return propertyTypeHandler; 
    }
    
    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        super.performInfoActionLoad(propertyValue); 
    }
        
}
