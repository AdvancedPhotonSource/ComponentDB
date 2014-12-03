package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 *
 * @author sveseli
 */
public class EdpLinkPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "EDP Link";

    public EdpLinkPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.HTTP_LINK;
    }    
    
    public static String formatEdpLink(String edpCollectionId) {
        // Format: https://edp.aps.anl.gov/browse/index/collection_id/96  
        return "https://edp.aps.anl.gov/browse/index/collection_id/" + edpCollectionId;
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
