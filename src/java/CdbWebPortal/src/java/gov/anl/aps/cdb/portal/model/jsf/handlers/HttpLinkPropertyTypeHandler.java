/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 * HTTP link property type handler.
 */
public class HttpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "HTTP Link";

    public HttpLinkPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.HTTP_LINK);
    }
    
    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String linkValue = shortenHttpLinkDisplayValueIfNeeded(propertyValue.getValue());
        propertyValue.setDisplayValue(linkValue);
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String linkValue = shortenHttpLinkDisplayValueIfNeeded(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(linkValue);
    }
}
