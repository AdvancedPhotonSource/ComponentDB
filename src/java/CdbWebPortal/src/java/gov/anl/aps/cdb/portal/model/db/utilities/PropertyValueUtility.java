/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * DB utility class for property values.
 */
public class PropertyValueUtility {

    private static final Logger logger = Logger.getLogger(PropertyValueUtility.class.getName());

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

    public static void preparePropertyValueHistory(List<PropertyValue> originalPropertyValueList,
            List<PropertyValue> newPropertyValueList, EntityInfo entityInfo) {
        for (PropertyValue newPropertyValue : newPropertyValueList) {
            int index = originalPropertyValueList.indexOf(newPropertyValue);
            if (index >= 0) {
                // Original property was there.
                PropertyValue originalPropertyValue = originalPropertyValueList.get(index);
                if (!newPropertyValue.equalsByTagAndValueAndUnitsAndDescription(originalPropertyValue)) {
                    // Property value was modified.
                    logger.debug("Property value for type " + originalPropertyValue.getPropertyType()
                            + " was modified (original value: " + originalPropertyValue + "; new value: " + newPropertyValue + ")");
                    newPropertyValue.setEnteredByUser(entityInfo.getLastModifiedByUser());
                    newPropertyValue.setEnteredOnDateTime(entityInfo.getLastModifiedOnDateTime());
                    newPropertyValue.setDisplayValue(null);
                    newPropertyValue.setTargetValue(null);

                    // Save history
                    List<PropertyValueHistory> propertyValueHistoryList = newPropertyValue.getPropertyValueHistoryList();
                    PropertyValueHistory propertyValueHistory = new PropertyValueHistory();
                    propertyValueHistory.updateFromPropertyValue(originalPropertyValue);
                    propertyValueHistoryList.add(propertyValueHistory);
                }
            } else {
                // New property value.
                logger.debug("Adding new property value for type " + newPropertyValue.getPropertyType()
                        + ": " + newPropertyValue);
                newPropertyValue.setEnteredByUser(entityInfo.getLastModifiedByUser());
                newPropertyValue.setEnteredOnDateTime(entityInfo.getLastModifiedOnDateTime());
            }
        }

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

    public static void searchPropertyValueList(List<PropertyValue> propertyValueList, Pattern searchPattern, SearchResult searchResult) {
        for (PropertyValue propertyValue : propertyValueList) {
            String baseKey = "propertyValue/id:" + propertyValue.getId();
            String propertyValueKey = baseKey + "/value";
            searchResult.doesValueContainPattern(propertyValueKey, propertyValue.getValue(), searchPattern);
            propertyValueKey = baseKey + "/description";
            searchResult.doesValueContainPattern(propertyValueKey, propertyValue.getDescription(), searchPattern);
        }
    }
}
