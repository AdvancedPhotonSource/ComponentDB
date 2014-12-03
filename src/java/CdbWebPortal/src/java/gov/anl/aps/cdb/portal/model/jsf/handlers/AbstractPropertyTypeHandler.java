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

    public static String shortenDisplayValueIfNeeded(String displayValue) {
        int length = displayValue.length();
        if (length > 32) {
            displayValue = displayValue.substring(0, 15) + "..." + displayValue.substring(length - 15);
        }
        return displayValue;
    }

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
    public void setDisplayValue(PropertyValue propertyValue) {
        propertyValue.setDisplayValueToValue();
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setDisplayValueToValue();
    }
    
    @Override
    public void setTargetValue(PropertyValue propertyValue) {
        propertyValue.setTargetValueToValue();
    }

    @Override
    public void setTargetValue(PropertyValueHistory propertyValueHistory) {
        propertyValueHistory.setTargetValueToValue();
    }    
}
