package gov.anl.aps.cdb.portal.model.jsf.handlers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;

/**
 *
 * @author sveseli
 */
public class CurrencyPropertyTypeHandler extends AbstractPropertyTypeHandler {

    public static final String HANDLER_NAME = "Currency";

    public static String roundDisplayValue(String displayValue) {
        try {
            if (displayValue != null && !displayValue.isEmpty())
            return String.format("%.2f", Double.parseDouble(displayValue));
        } catch (NumberFormatException ex) {
            // ignore this, simply return existing value
        }
        return displayValue;
    }

    public CurrencyPropertyTypeHandler() {
        super(HANDLER_NAME);
    }

    @Override
    public DisplayType getValueDisplayType() {
        return DisplayType.FREE_FORM_TEXT;
    }

    @Override
    public void setDisplayValue(PropertyValue propertyValue) {
        String currencyValue = roundDisplayValue(propertyValue.getValue());
        propertyValue.setDisplayValue(currencyValue);
    }

    @Override
    public void setDisplayValue(PropertyValueHistory propertyValueHistory) {
        String currencyValue = roundDisplayValue(propertyValueHistory.getValue());
        propertyValueHistory.setDisplayValue(currencyValue);
    }
}
