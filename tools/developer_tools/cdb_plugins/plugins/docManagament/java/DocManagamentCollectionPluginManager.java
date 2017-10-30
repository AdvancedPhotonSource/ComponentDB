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
public class DocManagamentCollectionPluginManager extends PluginManagerBase{
    
    protected static final String COLLECTION_IFRAME_PATH = "/iframe.jsp#/create/";

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DocManagamentCollectionPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }

    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        DocManagamentBean.getInstance().updateCurrentIFrameDialog(COLLECTION_IFRAME_PATH, propertyValue);
        
        super.performInfoActionLoad(propertyValue); 
    }    
    
}
