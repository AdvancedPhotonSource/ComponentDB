/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.edpLink;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;
import org.apache.log4j.Logger;

/**
 * EDP link property type handler.
 */
public class EdpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "EDP Link";
    
    private static final Logger logger = Logger.getLogger(EdpLinkPropertyTypeHandler.class.getName());

    private static final String EdpUrl = EdpLinkPluginManager.getUrlStringProperty(); 

    public EdpLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.GENERATED_HTTP_LINK);
    }

    public static String formatCollectionId(String collectionId) {
        if (collectionId == null) {
            return null;
        }
        String formattedId = collectionId.replace("EDP", "");
        if (formattedId.equals("") == false) {
            try {
                formattedId = formattedId.replace("_", "");
                formattedId = String.format("%06d", Integer.parseInt(formattedId));
                formattedId = "EDP_" + formattedId;
            } catch (NumberFormatException ex) {
                logger.debug(ex);
            }
        }
        return formattedId;
    }

    public static String formatEdpLink(String collectionId) {
        // Format: https://edp.aps.anl.gov/browse/index/collection_id/96  
        if (collectionId == null) {
            return null;
        }
        String formattedId = collectionId.replace("EDP", "");
        formattedId = formattedId.replace("_", "");
        String url = EdpUrl.replace("COLLECTION_ID", formattedId);
        return url;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = formatEdpLink(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = formatEdpLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String displayValue = formatCollectionId(propertyValue.getValue());
        propertyValue.setDisplayValue(displayValue);
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String displayValue = formatCollectionId(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(displayValue);
    }
}
