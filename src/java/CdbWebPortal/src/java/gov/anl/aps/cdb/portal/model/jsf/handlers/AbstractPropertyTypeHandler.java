package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.utilities.HttpLinkUtility;

/**
 *
 * @author sveseli
 */
public abstract class AbstractPropertyTypeHandler implements PropertyTypeHandlerInterface {

    protected String name;

    public static String shortenHttpLinkDisplayValueIfNeeded(String displayValue) {
        return HttpLinkUtility.prepareHttpLinkDisplayValue(displayValue);
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
