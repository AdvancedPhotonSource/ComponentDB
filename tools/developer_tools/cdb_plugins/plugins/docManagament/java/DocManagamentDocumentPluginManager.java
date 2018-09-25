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
public class DocManagamentDocumentPluginManager extends PluginManagerBase{        

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DocManagamentDocumentPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }

    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        if (DocManagamentBean.getInstance().loadSelectedDocumentObjectByPropertyValue(propertyValue)) {
            super.performInfoActionLoad(propertyValue); 
        }
    }    
    
}
