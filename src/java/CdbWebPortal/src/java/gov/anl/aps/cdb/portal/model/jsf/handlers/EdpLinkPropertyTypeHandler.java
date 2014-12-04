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
public class EdpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "EDP Link";
    public static final String EdpUrl = ConfigurationUtility.getPortalProperty(
            CdbProperty.EDP_URL_STRING_PROPERTY_NAME);
    
    public EdpLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }    
    
    public static String formatEdpLink(String collectionId) {
        // Format: https://edp.aps.anl.gov/browse/index/collection_id/96  
        if (collectionId == null) {
            return null;
        }
        String url = EdpUrl.replace("COLLECTION_ID", collectionId);
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
}
