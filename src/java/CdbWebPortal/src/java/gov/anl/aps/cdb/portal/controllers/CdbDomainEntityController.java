/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.portal.controllers.settings.CdbDomainEntitySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.CdbDomainEntityControllerUtility;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeCategoryFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueBase;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author djarosz
 * @param <EntityType>
 * @param <FacadeType>
 */
public abstract class CdbDomainEntityController<ControllerUtility extends CdbDomainEntityControllerUtility<EntityType, FacadeType>, EntityType extends CdbDomainEntity, FacadeType extends CdbEntityFacade<EntityType>, DomainEntitySettingsObject extends CdbDomainEntitySettings> extends CdbEntityController<ControllerUtility, EntityType, FacadeType, DomainEntitySettingsObject> implements Serializable, ICdbDomainEntityController<EntityType> {

    private PropertyValue currentEditPropertyValue;
    
    private PropertyValueController propertyValueController; 

    @EJB
    private PropertyValueFacade propertyValueDbFacade;
    
    @EJB
    private PropertyTypeFacade propertyTypeFacade;   
    
    @EJB
    protected PropertyTypeCategoryFacade propertyTypeCategoryFacade;

    protected Log newLogEdit;

    private static final Logger logger = LogManager.getLogger(CdbDomainEntity.class.getName());    

    private List<PropertyValue> filteredPropertyValueList;    
    
    protected List<PropertyTypeCategory> relevantPropertyTypeCategories = null;

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        for (PropertyType propertyType : propertyTypeList) {
            preparePropertyTypeValueAdd(propertyType);
        }
    }

    public void selectPropertyType(PropertyType propertyType, String onSuccessCommand) {
        if (propertyType != null) {
            PropertyValue propertyValue = preparePropertyTypeValueAdd(propertyType);
            setCurrentEditPropertyValue(propertyValue);
            SessionUtility.executeRemoteCommand(onSuccessCommand);            
        } else {
            SessionUtility.addWarningMessage("No property type selected", "Please select a property type.");
            currentEditPropertyValue = null;
        }
    }
    
    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType) {
        return preparePropertyTypeValueAdd(propertyType, propertyType.getDefaultValue());
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString) {
        return preparePropertyTypeValueAdd(propertyType, propertyValueString, null);
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString, String tag) {
        EntityType cdbDomainEntity = getCurrent();
        ControllerUtility controllerUtility = getControllerUtility();
        return controllerUtility.preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyValueString, tag); 
    }
    
    /**
     * Override this value for controller that manages a property with download-able viewable files. 
     * 
     * @param propertyValue
     * @return 
     */
    public String getThumbnailImageForDownloadablePropertyValue(PropertyValue propertyValue) {
        return StorageUtility.getMissingImageDefaultPreview(propertyValue.getValue(), null, CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION); 
    }
    
    public List<PropertyValue> prepareImageListForCurrent() {
        return prepareImageList(getCurrent()); 
    }

    public List<PropertyValue> prepareImageList(EntityType cdbDomainEntity) {
        if (cdbDomainEntity == null) {
            return null;
        }
        List<PropertyValue> imageList = PropertyValueUtility.prepareImagePropertyValueList(cdbDomainEntity.getPropertyValueList(), settingObject.getDisplayGalleryViewableDocuments());
        cdbDomainEntity.setImagePropertyList(imageList);
        return imageList;
    }

    public List<PropertyValue> getImageList() {
        EntityType cdbDomainEntity = getCurrent();
        if (cdbDomainEntity == null) {
            return null;
        }
        List<PropertyValue> cdbDomainEntityImageList = cdbDomainEntity.getImagePropertyList();
        if (cdbDomainEntityImageList == null) {
            cdbDomainEntityImageList = prepareImageList(cdbDomainEntity);
        }
        return cdbDomainEntityImageList;
    }
    
    public Boolean getDisplayPropertyMetadata(PropertyValueBase propertyValue) {
        // Maybe future property types will have internal property metadata. 
        // Temporarly done for disabling the metadata for specific entitiees. 
        return true; 
    }

    public Boolean getDisplayImages() {
        List<PropertyValue> cdbDomainEntityImageList = getImageList();
        return (cdbDomainEntityImageList != null && !cdbDomainEntityImageList.isEmpty());
    }

    public PropertyValue getCurrentEditPropertyValue() {
        return currentEditPropertyValue;
    }

    public void setCurrentEditPropertyValue(PropertyValue currentEditPropertyValue) {
        this.currentEditPropertyValue = currentEditPropertyValue;
        // Update current or property value controller 
        if (propertyValueController == null) {
            propertyValueController = PropertyValueController.getInstance();
        }
        propertyValueController.setCurrent(currentEditPropertyValue);
    }

    public void restoreCurrentEditPropertyValueToOriginalState() {
        if (currentEditPropertyValue != null) {
            EntityType cdbDomainEntity = getCurrent();
            if (currentEditPropertyValue.getId() == null) {
                // Never saved so it should be removed from the property value list
                cdbDomainEntity.getPropertyValueList().remove(currentEditPropertyValue);
                cdbDomainEntity.resetPropertyValueLists();
            } else {
                // Update the current Edit value pointer to db info.
                PropertyValue originalValue = propertyValueDbFacade.find(currentEditPropertyValue.getId());
                currentEditPropertyValue.setValue(originalValue.getValue());
                currentEditPropertyValue.setTag(originalValue.getTag());
                currentEditPropertyValue.setDescription(originalValue.getDescription());
                currentEditPropertyValue.setUnits(originalValue.getUnits());
                currentEditPropertyValue.setIsDynamic(originalValue.getIsDynamic());
                currentEditPropertyValue.setIsUserWriteable(originalValue.getIsUserWriteable());
                if (currentEditPropertyValue.getIsHasPropertyMetadata()) {
                    for (PropertyMetadata m : currentEditPropertyValue.getPropertyMetadataList()) {
                        m.setMetadataValue(originalValue.getPropertyMetadataValueForKey(m.getMetadataKey()));
                    }
                }
            }
            currentEditPropertyValue = null;
        }

    }

    public void updateEditProperty() {        
        this.update();        
    }

    public void deleteCurrentEditPropertyValue() {
        this.deleteProperty(currentEditPropertyValue);
        currentEditPropertyValue = null;
    }

    public void deleteProperty(PropertyValue cdbDomainEntityProperty) {
        EntityType entity = getCurrent();
        List<PropertyValue> cdbDomainEntityPropertyList = entity.getPropertyValueList();
        cdbDomainEntityPropertyList.remove(cdbDomainEntityProperty);
        updateOnRemoval();
    }

    public void savePropertyList() {
        update();        
    }   
    
    @Override
    public void customizeListDisplay() {
        resetDomainEntityPropertyTypeIdIndexMappings();
    }   

    @Override
    public DataModel getListDataModel() {
        DataModel cdbDomainEntityDataModel = super.getListDataModel();

        return cdbDomainEntityDataModel;
    }
    
    public void resetDomainEntityPropertyTypeIdIndexMappings() {
        ItemElement.setPropertyTypeIdIndex(1, settingObject.getDisplayPropertyTypeId1());
        ItemElement.setPropertyTypeIdIndex(2, settingObject.getDisplayPropertyTypeId2());
        ItemElement.setPropertyTypeIdIndex(3, settingObject.getDisplayPropertyTypeId3());
        ItemElement.setPropertyTypeIdIndex(4, settingObject.getDisplayPropertyTypeId4());
        ItemElement.setPropertyTypeIdIndex(5, settingObject.getDisplayPropertyTypeId5());
    }

    public String getDisplayPropertyTypeName(Integer propertyTypeId) {
        if (propertyTypeId != null) {

            try {
                PropertyType propertyType = propertyTypeFacade.find(propertyTypeId);
                return propertyType.getName();
            } catch (Exception ex) {
                return "Unknown Property";
            }
        }
        return null;
    }

    public boolean isDisplayRowExpansionLogs(CdbDomainEntity domainEntity) {
        if (domainEntity != null) {
            List<Log> itemLog = domainEntity.getLogList();
            return itemLog != null && !itemLog.isEmpty();
        }
        return false; 
    }

    public boolean isDisplayRowExpansionProperties(CdbDomainEntity domainEntity) {
        if (domainEntity != null) {
            List<PropertyValue> itemProperties = domainEntity.getPropertyValueDisplayList();
            return itemProperties != null && !itemProperties.isEmpty();
        }
        return false;
    }   

    public Boolean getDisplayLogList() {
        EntityType cdbDomainEntity = getCurrent();
        if (cdbDomainEntity != null) {
            List<Log> logList = cdbDomainEntity.getLogList();
            return logList != null && !logList.isEmpty();
        }
        return false;
    }

    public Boolean getDisplayPropertyList() {
        EntityType cdbDomainEntity = getCurrent();
        if (cdbDomainEntity != null) {
            List<PropertyValue> propertyValueList = cdbDomainEntity.getPropertyValueDisplayList();
            return propertyValueList != null && !propertyValueList.isEmpty();
        }
        return false;
    }

    public void logObjectEditRowEvent(RowEditEvent event) {
        this.saveLogList();
    }

    public Log getNewLogEdit() {
        return newLogEdit;
    }

    public void setNewLogEdit(Log newLogEdit) {
        this.newLogEdit = newLogEdit;
    }

    public void removeNewLog() {
        if (newLogEdit != null) {
            EntityType cdbDomainEntity = getCurrent();
            cdbDomainEntity.getLogList().remove(newLogEdit);
            newLogEdit = null;
        }
    }

    public void saveLogList() {
        newLogEdit = null;
        update();        
    }
    
    public Log prepareAddLog(EntityType cdbDomainEntity) {
        UserInfo user = SessionUtility.getUser();
        Log logEntry = getControllerUtility().prepareAddLog(cdbDomainEntity, user);         
        
        setNewLogEdit(logEntry);
        
        return logEntry; 
    }   

    public List<Log> getLogList() {
        EntityType cdbDomainEntity = getCurrent();
        List<Log> domainEntityLogList = cdbDomainEntity.getLogList();
        return domainEntityLogList;
    }

    public void deleteLog(Log cdbDomainEntityLog) {
        EntityType cdbDomainEntity = getCurrent();
        List<Log> cdbDomainEntityLogList = cdbDomainEntity.getLogList();
        cdbDomainEntityLogList.remove(cdbDomainEntityLog);
        updateOnRemoval();
    }

    public List<PropertyValue> getFilteredPropertyValueList() {
        return filteredPropertyValueList;
    }

    public void setFilteredPropertyValueList(List<PropertyValue> filteredPropertyValueList) {
        this.filteredPropertyValueList = filteredPropertyValueList;
    }
    
    public List<PropertyTypeCategory> getRelevantPropertyTypeCategories() {
        if (relevantPropertyTypeCategories == null) {
            if (getDefaultDomain() != null) {
                String domainName = getDefaultDomainName();
                relevantPropertyTypeCategories = propertyTypeCategoryFacade.findRelevantCategoriesByDomainId(domainName);
            } else {
                relevantPropertyTypeCategories = propertyTypeCategoryFacade.findAll();
            }
        }
        return relevantPropertyTypeCategories;
    }
    
    public Domain getDefaultDomain() {
        return null; 
    }
    
    public String getDefaultDomainName() {
        if (getDefaultDomain() != null) {
            return getDefaultDomain().getName(); 
        }
        return null; 
    }
}
