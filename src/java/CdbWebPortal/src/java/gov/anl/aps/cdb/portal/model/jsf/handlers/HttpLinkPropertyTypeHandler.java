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
    
    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String linkValue = shortenDisplayValueIfNeeded(propertyValue.getValue());
        propertyValue.setDisplayValue(linkValue);
    } 
    
    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String linkValue = shortenDisplayValueIfNeeded(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(linkValue);
    }     
}
