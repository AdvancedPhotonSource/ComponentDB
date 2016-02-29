/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class CdbDomainEntity extends CdbEntity {

    private transient List<PropertyValue> imagePropertyList = null;
    private transient final HashMap<Integer, PropertyValueInformation> propertyValueCacheMap = new HashMap<>();
    // Used to map property type id to property value number
    private static transient HashMap<Integer, Integer> propertyTypeIdIndexMap = new HashMap<>();

    public static void setPropertyTypeIdIndex(Integer index, Integer propertyTypeId) {
        if (propertyTypeId != null) {
            propertyTypeIdIndexMap.put(index, propertyTypeId);
        }
    }

    public List<PropertyValue> getPropertyValueList() {
        return null;
    }

    public List<Log> getLogList() {
        return null;
    }

    public List<PropertyValue> getImagePropertyList() {
        return imagePropertyList;
    }

    public void setImagePropertyList(List<PropertyValue> imagePropertyList) {
        this.imagePropertyList = imagePropertyList;
    }

    public void resetImagePropertyList() {
        this.imagePropertyList = null;
    }

    public void clearPropertyValueCache() {
        propertyValueCacheMap.clear();
    }

    public PropertyValueInformation getPropertyValueInformation(Integer propertyTypeId) {
        if (propertyTypeId == null) {
            return null;
        }
        PropertyValueInformation propertyValueInfo = propertyValueCacheMap.get(propertyTypeId);
        if (propertyValueInfo == null) {
            String delimiter = "";
            String cachedValue = "";
            PropertyTypeHandlerInterface propertyHandler = null;
            boolean setTargetValue = false;
            PropertyValue lastValue = null;
            List<PropertyValue> propertyValueList = getPropertyValueList();
            for (PropertyValue propertyValue : propertyValueList) {
                if (propertyValue.getPropertyType().getId().equals(propertyTypeId)) {
                    if (propertyHandler == null) {
                        propertyHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
                    }
                    propertyHandler.setDisplayValue(propertyValue);
                    String value = propertyValue.getDisplayValue();
                    if (value != null && !value.isEmpty()) {
                        cachedValue += delimiter + value;
                        delimiter = "|";
                    }
                    setTargetValue = lastValue == null;
                    lastValue = propertyValue;
                }
            }
            propertyValueInfo = new PropertyValueInformation(cachedValue, null);
            if (setTargetValue) {
                propertyValueInfo = attemptSetTargetValue(propertyValueInfo, lastValue, propertyHandler);
            }
            propertyValueCacheMap.put(propertyTypeId, propertyValueInfo);
        }
        return propertyValueInfo;
    }

    public PropertyValueInformation attemptSetTargetValue(PropertyValueInformation propertyValueInfo, PropertyValue propertyValue, PropertyTypeHandlerInterface propertyHandler) {
        DisplayType displayType = propertyHandler.getValueDisplayType();
        if (displayType != null) {
            if (displayType.equals(DisplayType.HTTP_LINK) || displayType.equals(DisplayType.TABLE_RECORD_REFERENCE)) {
                propertyHandler.setTargetValue(propertyValue);
                propertyValueInfo.setTargetValue(propertyValue.getTargetValue());
            }
        }
        return propertyValueInfo;
    }

    public String getPropertyValueByIndex(Integer index) {
        Integer propertyTypeId = propertyTypeIdIndexMap.get(index);
        if (propertyTypeId != null) {
            PropertyValueInformation propertyInfo = propertyValueCacheMap.get(propertyTypeId);
            if (propertyInfo != null) {
                return propertyInfo.getValue();
            }
        }
        return null;
    }

    public String getPropertyValue(Integer propertyTypeId) {
        return getPropertyValueInformation(propertyTypeId).getValue();
    }

    public void setPropertyValueByIndex(Integer index, String propertyValue) {
        if (index == null) {
            return;
        }
        Integer propertyTypeId = propertyTypeIdIndexMap.get(index);
        if (propertyTypeId != null) {
            propertyValueCacheMap.put(propertyTypeId, new PropertyValueInformation(propertyValue, null));
        }
    }

    public String getPropertyValue1() {
        return getPropertyValueByIndex(1);
    }

    public void setPropertyValue1(String propertyValue1) {
        setPropertyValueByIndex(1, propertyValue1);
    }

    public String getPropertyValue2() {
        return getPropertyValueByIndex(2);
    }

    public void setPropertyValue2(String propertyValue2) {
        setPropertyValueByIndex(2, propertyValue2);
    }

    public String getPropertyValue3() {
        return getPropertyValueByIndex(3);
    }

    public void setPropertyValue3(String propertyValue3) {
        setPropertyValueByIndex(3, propertyValue3);
    }

    public String getPropertyValue4() {
        return getPropertyValueByIndex(4);
    }

    public void setPropertyValue4(String propertyValue4) {
        setPropertyValueByIndex(4, propertyValue4);
    }

    public String getPropertyValue5() {
        return getPropertyValueByIndex(5);
    }

    public void setPropertyValue5(String propertyValue5) {
        setPropertyValueByIndex(5, propertyValue5);
    }

    public class PropertyValueInformation {

        private String value;
        private String targetValue;

        public PropertyValueInformation(String value, String targetValue) {
            this.value = value;
            this.targetValue = targetValue;
        }

        public String getValue() {
            return value;
        }

        public String getTargetValue() {
            return targetValue;
        }

        public void setTargetValue(String targetValue) {
            this.targetValue = targetValue;
        }
    }
}
