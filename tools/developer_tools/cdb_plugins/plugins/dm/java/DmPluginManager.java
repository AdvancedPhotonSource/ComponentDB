/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.dm;

import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.plugins.PluginManagerBase;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author djarosz
 */
public class DmPluginManager extends PluginManagerBase {
    
    private static final Properties DM_PROPERTIES = getDefaultPropertiesForPlugin("dm"); 
    
    private static final Logger LOGGER = Logger.getLogger(DmPluginManager.class.getName());
    
    private static final String DM_SERVICE_URL_KEY = "serviceUrl"; 
    private static final String DM_SERVICE_USER_KEY = "serviceUser";     
    private static final String DM_SERVICE_PASS_KEY = "servicePass"; 
    private static final String DM_PORTAL_URL_KEY = "dmPortalUrl";
    
    private String infoActionText = null;
    private String infoActionDialogHeader = null; 
    
    public static String getServiceUrlProperty() {
        return DM_PROPERTIES.getProperty(DM_SERVICE_URL_KEY, ""); 
    }
    
    public static String getServiceUserProperty() {
        return DM_PROPERTIES.getProperty(DM_SERVICE_USER_KEY, ""); 
    }
    
    public static String getServicePassProperty() {
        return DM_PROPERTIES.getProperty(DM_SERVICE_PASS_KEY, ""); 
    }
    
    public String getDmPortalUrl() {
        return DM_PROPERTIES.getProperty(DM_PORTAL_URL_KEY, ""); 
    } 

    @Override
    public void performInfoActionLoad(PropertyValue propertyValue) {
        DmPropertyTypeHandler handler = (DmPropertyTypeHandler) propertyTypeHandler;
        infoActionText = null; 
        try { 
            ByteArrayOutputStream byteArrayOutputStream = handler.getByteArrayOutputStream(propertyValue);
            infoActionDialogHeader = "Showing text for: " + propertyValue.getValue(); 
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            infoActionText = new String(toByteArray);
            super.performInfoActionLoad(propertyValue);
        } catch (ExternalServiceError ex) {
            LOGGER.error(ex);            
            SessionUtility.addErrorMessage("Error", "Error fetching property data.");
        }
    }

    public String getInfoActionText() {
        return infoActionText;
    }

    public String getInfoActionDialogHeader() {
        return infoActionDialogHeader;
    }
    
    @Override
    public PropertyTypeHandlerInterface getPluginPropertyTypeHandler() {
        if (propertyTypeHandler == null) {
            propertyTypeHandler = new DmPropertyTypeHandler();
        }
        return propertyTypeHandler;
    }
    
}
