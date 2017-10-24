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
    
    protected static final String CONTAINER_IFRAME_PATH = "/iframe.jsp#/container/";

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DocManagamentContainerPropertyTypeHandler();
        }
        return propertyTypeHandler; 
    }
    
    public String getDocumentManagamentIFrameContainerURL(Integer containerId) {
        return DocManagerPlugin.getContextRootUrlProperty() + CONTAINER_IFRAME_PATH + containerId; 
    }
    
    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        DocManagamentBean.getInstance().updateCurrentIFrameSrc(CONTAINER_IFRAME_PATH, propertyValue.getValue());
        
        super.performInfoActionLoad(propertyValue); 
    }   
        
}
