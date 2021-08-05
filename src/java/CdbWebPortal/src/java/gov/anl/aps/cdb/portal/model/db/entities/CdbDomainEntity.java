/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author djarosz
 */
public abstract class CdbDomainEntity extends CdbEntity {

    @JsonIgnore
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
    
    public abstract void setPropertyValueList(List<PropertyValue> propertyValueList);
    
    public List<PropertyValue> getPropertyValueDisplayList() {
        return getPropertyValueList();
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
            propertyValueInfo = new PropertyValueInformation(); 
            PropertyTypeHandlerInterface propertyHandler = null;
            List<PropertyValue> propertyValueList = getPropertyValueList();
            for (PropertyValue propertyValue : propertyValueList) {
                if (propertyValue.getPropertyType().getId().equals(propertyTypeId)) {
                    propertyHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);

                    propertyHandler.setDisplayValue(propertyValue);
                    propertyHandler.setTargetValue(propertyValue);
                    propertyHandler.setInfoActionCommand(propertyValue);
               
                    String value = propertyValue.getDisplayValue();
                    if (value != null && !value.isEmpty()) {
                        propertyValueInfo.appendFilterValue(value);
                    }
                    propertyValueInfo.addPropertyValueObject(propertyValue);
                }
            }
            propertyValueCacheMap.put(propertyTypeId, propertyValueInfo);
        }
        return propertyValueInfo;
    }

    public String getPropertyValueByIndex(Integer index) {
        Integer propertyTypeId = propertyTypeIdIndexMap.get(index);
        if (propertyTypeId != null) {
            PropertyValueInformation propertyInfo = propertyValueCacheMap.get(propertyTypeId);
            if (propertyInfo != null) {
                return propertyInfo.getFilterValue();
            }
        }
        return null;
    }

    public String getPropertyValue(Integer propertyTypeId) {
        return getPropertyValueInformation(propertyTypeId).getFilterValue();
    }

    public void setPropertyValueByIndex(Integer index, String propertyValue) {
        if (index == null) {
            return;
        }
        Integer propertyTypeId = propertyTypeIdIndexMap.get(index);
        if (propertyTypeId != null) {
            PropertyValueInformation propertyValueInformation = new PropertyValueInformation();
            propertyValueInformation.setFilterValue(propertyValue);
            propertyValueCacheMap.put(propertyTypeId, propertyValueInformation);
        }
    }

    @JsonIgnore
    public String getPropertyValue1() {
        return getPropertyValueByIndex(1);
    }

    public void setPropertyValue1(String propertyValue1) {
        setPropertyValueByIndex(1, propertyValue1);
    }

    @JsonIgnore
    public String getPropertyValue2() {
        return getPropertyValueByIndex(2);
    }

    public void setPropertyValue2(String propertyValue2) {
        setPropertyValueByIndex(2, propertyValue2);
    }

    @JsonIgnore
    public String getPropertyValue3() {
        return getPropertyValueByIndex(3);
    }

    public void setPropertyValue3(String propertyValue3) {
        setPropertyValueByIndex(3, propertyValue3);
    }

    @JsonIgnore
    public String getPropertyValue4() {
        return getPropertyValueByIndex(4);
    }

    public void setPropertyValue4(String propertyValue4) {
        setPropertyValueByIndex(4, propertyValue4);
    }

    @JsonIgnore
    public String getPropertyValue5() {
        return getPropertyValueByIndex(5);
    }

    public void setPropertyValue5(String propertyValue5) {
        setPropertyValueByIndex(5, propertyValue5);
    }

    public class PropertyValueInformation {

        private String filterValue;
        private List<PropertyValue> propertyValueList; 
        private final String DELIMITER = "\n"; 

        public PropertyValueInformation() {
            this.propertyValueList = new ArrayList<>(); 
            this.filterValue = ""; 
        }
         
        @JsonIgnore
        public List<PropertyValue> getPropertyValueList() {
            return propertyValueList;
        }
        
        public void addPropertyValueObject(PropertyValue propertyValue) {
            propertyValueList.add(propertyValue); 
        }
        
        public void appendFilterValue(String value) {
            filterValue += value + DELIMITER;    
        }

        public String getFilterValue() {
            return filterValue;
        }

        public void setFilterValue(String filterValue) {
            this.filterValue = filterValue;
        }
    }
}
