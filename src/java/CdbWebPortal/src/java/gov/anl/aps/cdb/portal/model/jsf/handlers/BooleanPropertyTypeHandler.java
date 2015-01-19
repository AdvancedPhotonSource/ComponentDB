package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 *
 * @author sveseli
 */
public class BooleanPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Boolean";

    public BooleanPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.BOOLEAN;
    }    
    
}
