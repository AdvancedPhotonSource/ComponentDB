/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;

/**
 * EDP link property type handler.
 */
public class EdpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "EDP Link";

    private static final String EdpUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.EDP_URL_STRING_PROPERTY_NAME);

    public EdpLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
    }

    public static String formatCollectionId(String collectionId) {
        if (collectionId == null) {
            return null;
        }
        String formattedId = collectionId.replace("EDP", "");
        formattedId = formattedId.replace("_", "");
        formattedId = String.format("%06d", Integer.parseInt(formattedId));
        formattedId = "EDP_" + formattedId;
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
