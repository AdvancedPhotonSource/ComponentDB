package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import java.util.ArrayList;
import java.util.List;

public class PropertyValueUtility
{

    public static List<PropertyValue> prepareImagePropertyValueList(List<PropertyValue> propertyValueList) {
        List<PropertyValue> imagePropertyValueList = new ArrayList<>();
        if (propertyValueList != null) {
            for (PropertyValue propertyValue : propertyValueList) {
                PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
                DisplayType valueDisplayType = propertyTypeHandler.getValueDisplayType();
                if (valueDisplayType == DisplayType.IMAGE) {
                    String value = propertyValue.getValue();
                    if (value != null && !value.isEmpty()) {
                        imagePropertyValueList.add(propertyValue);
                    }
                }
            }
        }
        return imagePropertyValueList;
    }

    public static PropertyTypeHandlerInterface getPropertyTypeHandler(PropertyValue propertyValue) {
        return PropertyTypeHandlerFactory.getHandler(propertyValue);
    }

    public static DisplayType configurePropertyValueDisplay(PropertyValue propertyValue) {
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
        propertyTypeHandler.setDisplayValue(propertyValue);
        propertyTypeHandler.setTargetValue(propertyValue);
        PropertyType propertyType = propertyValue.getPropertyType();
        DisplayType displayType = propertyTypeHandler.getValueDisplayType();
        if (displayType == null) {
            displayType = DisplayType.FREE_FORM_TEXT;
            if (propertyType.hasAllowedPropertyValues()) {
                displayType = DisplayType.SELECTED_TEXT;
            }
        }
        propertyType.setDisplayType(displayType);
        return displayType;
    }

    public static DisplayType getPropertyValueDisplayType(PropertyValue propertyValue) {
        if (propertyValue == null) {
            return DisplayType.FREE_FORM_TEXT;
        }
        DisplayType result = propertyValue.getPropertyType().getDisplayType();
        if (result == null) {
            result = configurePropertyValueDisplay(propertyValue);
        }
        return result;
    }

    public static boolean displayFreeFormTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.FREE_FORM_TEXT);
    }

    public static boolean displaySelectedTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.SELECTED_TEXT);
    }

    public static boolean displayImageValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.IMAGE);
    }

    public static boolean displayHttpLinkValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.HTTP_LINK);
    }

    public static boolean displayDocumentValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.DOCUMENT);
    }    
}
