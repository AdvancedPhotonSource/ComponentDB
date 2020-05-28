/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.pdmLink;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author djarosz
 */
public class PdmLinkPluginManager extends PluginManagerBase {
    
    private static final Logger logger = LogManager.getLogger(PdmLinkPluginManager.class.getName());

    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new PdmLinkPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }

    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        String value = propertyValue.getValue(); 
        logger.debug("Info action of type PDMLink, drawing #: " + value);
        PdmLinkDrawingBean pdmLinkDrawingBean = PdmLinkDrawingBean.getInstance();
        pdmLinkDrawingBean.resetDrawingInfo();
        pdmLinkDrawingBean.getRelatedDrawings(value, propertyValue.getInfoActionCommand());
    }
    
}
