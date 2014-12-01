package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 *
 * @author sveseli
 */
public class HttpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "HTTP Link";

    public HttpLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }    
    
    public static String shortenLinkIfNeeded(String linkValue) {
        int length = linkValue.length();
        if (length > 32) {
            linkValue = linkValue.substring(0,15) + "..." + linkValue.substring(length-15);
        }
        return linkValue;
    }
    
    @Override
    public void setViewValue(PropertyValue propertyValue) {
        // Shorten display if link is too large
        String linkValue = shortenLinkIfNeeded(propertyValue.getValue());
        propertyValue.setViewValue(linkValue);
    } 
    
    @Override
    public void setViewValue(PropertyValueHistory propertyValueHistory) {
        String linkValue = shortenLinkIfNeeded(propertyValueHistory.getValue());
        propertyValueHistory.setViewValue(linkValue);
    }     
}
