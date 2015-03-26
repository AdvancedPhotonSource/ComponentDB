package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;

/**
 *
 * @author sveseli
 */
public class PdmLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "PDMLink";
    public static final String PdmLinkUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.PDMLINK_URL_STRING_PROPERTY_NAME);

    public PdmLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }

    public static String formatPdmLink(String pdmLinkId) {       
        if (pdmLinkId == null) {
            return null;
        }
        
        String idValue = pdmLinkId.trim();
        String url = PdmLinkUrl.replace("PDMLINK_ID", idValue);
        return url;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = formatPdmLink(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = formatPdmLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }
}
