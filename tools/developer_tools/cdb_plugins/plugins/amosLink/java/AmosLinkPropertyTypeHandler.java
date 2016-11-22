/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.amosLink;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;

/**
 * AMOS property type handler.
 */
public class AmosLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "AMOS Link";

    private static final String AmosUrl = AmosLinkPluginManager.getAmosUrlString(); 

    public AmosLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
    }

    public static String formatOrderId(String orderId) {
        if (orderId == null) {
            return null;
        }
        String formattedId = orderId.replace("MO", "");
        formattedId = formattedId.replace("_", "");
        formattedId = "MO_" + formattedId;
        return formattedId;
    }

    public static String formatAmosLink(String orderId) {
        // For an AMOS order # like MOnnnnnn , create link
        // https://amos....ORDER_NO=nnnnnn
        // Example: MO352645   
        // https://amos....ORDER_NO=352645
        if (orderId == null) {
            return null;
        }

        String docId = orderId.replace("MO", "");
        docId = docId.replace("_", "");
        String url = AmosUrl.replace("ORDER_ID", docId);
        return url;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = formatAmosLink(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = formatAmosLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String displayValue = formatOrderId(propertyValue.getValue());
        propertyValue.setDisplayValue(displayValue);
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String displayValue = formatOrderId(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(displayValue);
    }
}
