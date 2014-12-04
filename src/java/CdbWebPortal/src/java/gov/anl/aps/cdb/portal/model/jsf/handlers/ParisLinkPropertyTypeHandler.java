package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.CdbProperty;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;

/**
 *
 * @author sveseli
 */
public class ParisLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "PARIS Link";
    public static final String ParisUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.PARIS_URL_STRING_PROPERTY_NAME);

    public ParisLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }

    public static String formatParisLink(String poId) {
        // Property Handler:  For a purchase req number such as Fy-nnnnnn , create link ... https://apps.anl.gov/paris/req.jsp?reqNbr=Fy-nnnnnn
        // Example: F3-326054  https://apps.anl.gov/paris/req.jsp?reqNbr=F3-326054          
        if (poId == null) {
            return null;
        }
        
        String url = ParisUrl.replace("PO_ID", poId);
        return url;
    }

    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        String targetLink = formatParisLink(propertyValue.getValue());
        propertyValue.setTargetValue(targetLink);
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        String targetLink = formatParisLink(propertyValueHistory.getValue());
        propertyValueHistory.setTargetValue(targetLink);
    }
}
