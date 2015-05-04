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
 * AMOS property type handler.
 */
public class AmosLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "AMOS Link";

    private static final String AmosUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.AMOS_URL_STRING_PROPERTY_NAME);

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
        // https://apps.anl.gov/webcompadapter/viewrpt.cwr?id=10211&apsuser=USER&apspassword=PASSWORD&apsauthtype=secEnterprise&cmd=EXPORT&EXPORT_FMT=U2FPDF%3A0&promptex-ORDER_NO=nnnnnn
        // Example: MO352645   
        // https://apps.anl.gov/webcompadapter/viewrpt.cwr?id=10211&apsuser=USER&apspassword=PASSWORD&apsauthtype=secEnterprise&cmd=EXPORT&EXPORT_FMT=U2FPDF%3A0&promptex-ORDER_NO=352645
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
