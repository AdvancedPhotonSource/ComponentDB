/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.portal.controllers.utilities.CdbEntityControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all CDB entities.
 */
public class CdbEntity implements Serializable, Cloneable {
    
    private static final Logger LOGGER = LogManager.getLogger(CdbEntity.class.getName());
    
    @JsonIgnore
    private transient String viewUUID;
    
    @JsonIgnore
    private transient String persitanceErrorMessage = null; 
    
    @JsonIgnore
    protected transient List<PropertyValue> propertyValueDisplayList = null; 
    @JsonIgnore
    protected transient List<PropertyValue> propertyValueInternalList = null; 
    
    private transient Map<String, String> apiProperties;
    
    // persistence management for associated ItemConnectors, deleted on call to edit this item in facade 
    private transient List<ItemConnector> deletedConnectorList = null;
    
    // import wizard variables
    private transient boolean isValidImport = true;
    private transient String validStringImport;
    private transient Integer importExistingItemId;
    private transient Boolean importDeleteExistingItem;
    private transient String importDiffs;
    private transient String importUnchanged;
    private transient Boolean hasImportUpdates;
    
    // shared code for managing cable end designation property type and value 
    // so that it is not duplicated in Connector, ItemElementRelationship
    private static PropertyType cableEndDesignationPropertyType = null;
    public static final String CABLE_END_DESIGNATION_PROPERTY_TYPE = "cable_end_designation_property_type";
    private transient String cableEndDesignation = null;
    private transient PropertyValue cableEndDesignationPropertyValue = null;
    public static final String CABLE_END_DESIGNATION_PROPERTY_DESCRIPTION = "cable end designation";
    public final static String VALUE_CABLE_END_1 = "1";
    public final static String VALUE_CABLE_END_2 = "2";
    public final static String LABEL_CABLE_END_1 = "End1";
    public final static String LABEL_CABLE_END_2 = "End2";
    public static final String DEFAULT_CABLE_END_DESIGNATION = VALUE_CABLE_END_1;
    

    protected static final long serialVersionUID = 1L;
    
    public void resetPropertyValueLists() {
        propertyValueDisplayList = null; 
        propertyValueInternalList = null; 
    }
    
    public Object clone(UserInfo currentUser) throws CloneNotSupportedException {
        return super.clone();
    }
    
    public Object getId() {
        return null;
    }
    
    @JsonIgnore
    public Object getEntityInfo() {
        return null;
    }
    
    public SearchResult search(Pattern searchPattern) {
        return null;
    }        
    
    @JsonIgnore
    public String getViewUUID() {
        if (viewUUID == null) {
            viewUUID = UUID.randomUUID().toString().replaceAll("[-]", "");
        }
        return viewUUID;
    }

    public String getPersitanceErrorMessage() {
        return persitanceErrorMessage;
    }

    public void setPersitanceErrorMessage(String persitanceErrorMessage) {
        this.persitanceErrorMessage = persitanceErrorMessage;
    }

    @JsonAnySetter
    public void addJsonProperty(String key, String value) {
        if (apiProperties == null) {
            apiProperties = new HashMap<>(); 
        }
        apiProperties.put(key, value);
    }
    
    @JsonIgnore
    public String getSystemLogString() {
        return toString(); 
    }
    
    @JsonIgnore
    public boolean getIsValidImport() {
        return isValidImport;
    }
    
    @JsonIgnore
    public List<ItemConnector> getDeletedConnectorList() {
        if (deletedConnectorList == null) {
            deletedConnectorList = new ArrayList<>();
        }
        return deletedConnectorList;
    }
    
    public void clearDeletedConnectorList() {
        if (deletedConnectorList != null) {
            deletedConnectorList.clear();
        }
    }
    
    @JsonIgnore
    public String getIsValidImportString() {
        if (isValidImport) {
            return "yes";
        } else {
            return "no";
        }
    }
    
    public void setIsValidImport(boolean b) {
        isValidImport = b;
    }
        
    @JsonIgnore
    public String getValidStringImport() {
        return validStringImport;
    }
    
    public void setValidStringImport(String s) {
        validStringImport = s;
    }
    
    public void setImportExistingItemId(Integer id) {
        importExistingItemId = id;
    }
    
    @JsonIgnore
    public Integer getImportExistingItemId() {
        return importExistingItemId;
    }
    
    public void setImportDeleteExistingItem(Boolean b) {
        importDeleteExistingItem = b;
    }
    
    @JsonIgnore
    public Boolean getImportDeleteExistingItem() {
        return importDeleteExistingItem;
    }
    
    @JsonIgnore
    public String getImportDiffs() {
        return importDiffs;
    }
    
    public void setImportDiffs(String diffString) {
        importDiffs = diffString;
    }

    @JsonIgnore
    public String getImportUnchanged() {
        return importUnchanged;
    }
    
    public void setImportUnchanged(String diffString) {
        importUnchanged = diffString;
    }

    @JsonIgnore
    public Boolean hasImportUpdates() {
        return hasImportUpdates;
    }
    
    public void hasImportUpdates(Boolean hasUpdates) {
        hasImportUpdates = hasUpdates;
    }

    @JsonIgnore
    public Boolean getIsItemDeleted() {
        return false;
    }
    
    /**
     * Allows subclasses to determine whether an instance can be deleted.
     * @return 
     */
    @JsonIgnore
    public ValidInfo isDeleteAllowed() {
        return new ValidInfo(true, "");
    }

    // default implementation is to do nothing
    public void addPropertyValueToPropertyValueList(PropertyValue propertyValue) {
        LOGGER.error("Override expected for addPropertyValueToPropertyValueList()? " + getClass().getName());
    }
    
    // default implementation returns null, subclasses override to customize
    protected List<PropertyValue> getPropertyValueList() {
        LOGGER.error("Override expected for getPropertyValueList()? " + getClass().getName());
        return null;
    }
    
    // default implementation returns null, subclasses override to customize
    @JsonIgnore
    public CdbEntityControllerUtility getControllerUtility() {
        LOGGER.error("Override expected for getControllerUtility()? " + getClass().getName());
        return null;
    }

    @JsonIgnore
    public PropertyType getCableEndDesignationPropertyType() {
        
        if (cableEndDesignationPropertyType == null) {
            
            cableEndDesignationPropertyType =
                    PropertyTypeFacade.getInstance().findByName(CABLE_END_DESIGNATION_PROPERTY_TYPE);
            
            if (cableEndDesignationPropertyType == null) {
                cableEndDesignationPropertyType = getControllerUtility().prepareCableEndDesignationPropertyType();
            }
        }
        return cableEndDesignationPropertyType;
    }
    
    public PropertyValue prepareCableEndDesignationPropertyValue() {
        PropertyType propertyType = getCableEndDesignationPropertyType();
        return getControllerUtility().preparePropertyTypeValueAdd(
                this, propertyType, propertyType.getDefaultValue(), null);
    }
    
    @JsonIgnore
    public PropertyValue getCableEndDesignationPropertyValue() {

        if (cableEndDesignationPropertyValue == null) {
            List<PropertyValue> propertyValueList = getPropertyValueList();
            if (propertyValueList != null) {
                for (PropertyValue propertyValue : propertyValueList) {
                    if (propertyValue.getPropertyType().getName().equals(CABLE_END_DESIGNATION_PROPERTY_TYPE)) {
                        cableEndDesignationPropertyValue = propertyValue;
                    }
                }
            }
        }
        
        if (cableEndDesignationPropertyValue == null) {
            cableEndDesignationPropertyValue = prepareCableEndDesignationPropertyValue();
        }

        return cableEndDesignationPropertyValue;
    }

    public void setCableEndDesignation(String endDesignation) {
        PropertyValue propertyValue = getCableEndDesignationPropertyValue();
        if (propertyValue != null) {
            cableEndDesignation = endDesignation;
            propertyValue.setValue(endDesignation);
        }
    }
    
    @JsonIgnore
    public String getCableEndDesignation() {        
        if (cableEndDesignation == null) {
            cableEndDesignation = getCableEndDesignationPropertyValue().getValue();
        }
        if (cableEndDesignation != null) {
            return cableEndDesignation;
        } else {
            return "";
        }
    }
    
    public static boolean isValidCableEndDesignation(String designation) {
        List<String> list = Arrays.asList(VALUE_CABLE_END_1, VALUE_CABLE_END_2);
        return list.contains(designation);
    }

}
