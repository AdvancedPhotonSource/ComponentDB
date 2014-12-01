package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 *
 * @author sveseli
 */
public abstract class AbstractPropertyTypeHandler implements PropertyTypeHandlerInterface {

    protected String name;

    public AbstractPropertyTypeHandler(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getEditActionOncomplete() {
        return null;
    }

    @Override
    public String getEditActionIcon() {
        return null;
    }

    @Override
    public String getEditActionBean() {
        return null;
    }

    @Override
    public Boolean getDisplayEditActionButton() {
        return getEditActionIcon() != null;
    }

    @Override
    public DisplayType getValueDisplayType() {
        return null;
    }
    
    @Override
    public void setViewValue(PropertyValue propertyValue) {
        propertyValue.setViewValueToValue();
    }    
    
    @Override
    public void setViewValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setViewValueToValue();
    }        
}
