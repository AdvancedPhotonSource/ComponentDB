/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.irmisLocation;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.AbstractPropertyTypeHandler;

/**
 *
 * @author iusmani
 */
public class irmisLocationPropertyTypeHandler extends AbstractPropertyTypeHandler {
   public static final String HANDLER_NAME = "IRMIS Location";

    private static final String irmisLocationUrl = irmisLocationPluginManager.getIrmisLocationUrlString(); 

    public irmisLocationPropertyTypeHandler() {
        super(HANDLER_NAME, DisplayType.GENERATED_HTTP_LINK);
    }

    public static String formatOrderId(String componentId) {
        if (componentId == null) {
            return null;
        }
        return "IRMIS Location";
    }

    public static String formatAmosLink(String componentId) {
        if (componentId == null) {
            return null;
        }

        String url = irmisLocationUrl.replace("COMPONENT_ID", componentId);
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
