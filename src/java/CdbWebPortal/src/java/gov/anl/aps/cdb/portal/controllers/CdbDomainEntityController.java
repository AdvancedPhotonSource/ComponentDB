/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author djarosz
 * @param <EntityType>
 * @param <FacadeType>
 */
public abstract class CdbDomainEntityController<EntityType extends CdbDomainEntity, FacadeType extends CdbEntityFacade<EntityType>> extends CdbEntityController<EntityType, FacadeType> implements Serializable {

    private PropertyValue currentEditPropertyValue;

    @EJB
    private PropertyValueFacade propertyValueDbFacade;
    @EJB
    private PropertyTypeFacade propertyTypeFacade;

    protected Integer displayPropertyTypeId1 = null;
    protected Integer displayPropertyTypeId2 = null;
    protected Integer displayPropertyTypeId3 = null;
    protected Integer displayPropertyTypeId4 = null;
    protected Integer displayPropertyTypeId5 = null;

    protected Boolean displayRowExpansion = null;
    protected Boolean loadRowExpansionPropertyValues = null;
    protected Boolean displayGalleryViewableDocuments = null;

    protected String filterByPropertyValue1 = null;
    protected String filterByPropertyValue2 = null;
    protected String filterByPropertyValue3 = null;
    protected String filterByPropertyValue4 = null;
    protected String filterByPropertyValue5 = null;
    protected String filterByItemIdentifier1 = null;
    protected String filterByItemIdentifier2 = null;
    protected Boolean filterByPropertiesAutoLoad = null;

    protected List<Integer> loadedDisplayPropertyTypes = null;

    protected Log newLogEdit;

    private static final Logger logger = Logger.getLogger(CdbDomainEntity.class.getName());

    private static final String DisplayGalleryViewableDocumentsSettingTypeKey = "DomainEntity.Detail.Display.GalleryViewableDocuments";

    private List<PropertyValue> filteredPropertyValueList;

    protected Integer preProcessLoadedListDataModelHashCode = null;
    protected DataModel preProcessDomainEntityListDataModel = null;

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        logger.debug("Updating list settings from setting type defaults for: " + this.getEntityTypeName());

        displayGalleryViewableDocuments = Boolean.parseBoolean(settingTypeMap.get(DisplayGalleryViewableDocumentsSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        logger.debug("Updating list settings from session user for: " + this.getEntityTypeName());

        displayGalleryViewableDocuments = settingEntity.getSettingValueAsBoolean(DisplayGalleryViewableDocumentsSettingTypeKey, displayGalleryViewableDocuments);

        prepareImageList(getCurrent());
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayGalleryViewableDocumentsSettingTypeKey, displayGalleryViewableDocuments);
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        for (PropertyType propertyType : propertyTypeList) {
            preparePropertyTypeValueAdd(propertyType);
        }
    }

    public void selectPropertyType(PropertyType propertyType, String onSuccessCommand) {
        if (propertyType != null) {
            PropertyValue propertyValue = preparePropertyTypeValueAdd(propertyType);
            setCurrentEditPropertyValue(propertyValue);
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
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
        List<PropertyValue> propertyValueList = cdbDomainEntity.getPropertyValueList();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyType(propertyType);
        propertyValue.setValue(propertyValueString);
        propertyValue.setUnits(propertyType.getDefaultUnits());
        propertyValueList.add(0, propertyValue);
        propertyValue.setEnteredByUser(lastModifiedByUser);
        propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        if (tag != null) {
            propertyValue.setTag(tag);
        }

        cdbDomainEntity.resetPropertyValueLists();

        return propertyValue;
    }

    public List<PropertyValue> prepareImageList(EntityType cdbDomainEntity) {
        if (cdbDomainEntity == null) {
            return null;
        }
        List<PropertyValue> imageList = PropertyValueUtility.prepareImagePropertyValueList(cdbDomainEntity.getPropertyValueList(), displayGalleryViewableDocuments);
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

    public Boolean getDisplayImages() {
        List<PropertyValue> cdbDomainEntityImageList = getImageList();
        return (cdbDomainEntityImageList != null && !cdbDomainEntityImageList.isEmpty());
    }

    public PropertyValue getCurrentEditPropertyValue() {
        return currentEditPropertyValue;
    }

    public void setCurrentEditPropertyValue(PropertyValue currentEditPropertyValue) {
        this.currentEditPropertyValue = currentEditPropertyValue;
    }

    public void removeCurrentEditPropertyValue() {
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
                currentEditPropertyValue.setTargetValue("");
                currentEditPropertyValue.setDisplayValue("");
                currentEditPropertyValue.setTag(originalValue.getTag());
                currentEditPropertyValue.setDescription(originalValue.getDescription());
                currentEditPropertyValue.setUnits(originalValue.getUnits());
                currentEditPropertyValue.setIsDynamic(originalValue.getIsDynamic());
                currentEditPropertyValue.setIsUserWriteable(originalValue.getIsUserWriteable());
            }
            currentEditPropertyValue = null;
        }

    }

    public void updateEditProperty() {
        // Will cause refetching of display value.
        currentEditPropertyValue.setDisplayValue("");
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

    protected void resetDomainEntityPropertyTypeIdIndexMappings() {
        ItemElement.setPropertyTypeIdIndex(1, displayPropertyTypeId1);
        ItemElement.setPropertyTypeIdIndex(2, displayPropertyTypeId2);
        ItemElement.setPropertyTypeIdIndex(3, displayPropertyTypeId3);
        ItemElement.setPropertyTypeIdIndex(4, displayPropertyTypeId4);
        ItemElement.setPropertyTypeIdIndex(5, displayPropertyTypeId5);
    }

    @Override
    public String customizeListDisplay() {
        resetDomainEntityPropertyTypeIdIndexMappings();
        forceLoadPreProcessListDataModel();

        return super.customizeListDisplay();
    }

    @Override
    public void saveListSettingsForSessionSettingEntityActionListener(ActionEvent actionEvent) {
        super.saveListSettingsForSessionSettingEntityActionListener(actionEvent);

        forceLoadPreProcessListDataModel();
    }

    @Override
    public void saveListSettingsForSessionSettingEntitySetCurrentActionListener(ActionEvent actionEvent) {
        super.saveListSettingsForSessionSettingEntitySetCurrentActionListener(actionEvent);

        forceLoadPreProcessListDataModel();
    }

    @Override
    public DataModel getListDataModel() {
        DataModel cdbDomainEntityDataModel = super.getListDataModel();

        loadPreProcessListDataModelIfNeeded(listDataModel);

        return cdbDomainEntityDataModel;
    }

    /**
     * Get hash code of the currently loaded data model.
     *
     * @return hash code integer.
     */
    private int getPreProcessDomainEntityListDataModelHashCode() {
        return preProcessDomainEntityListDataModel.hashCode();
    }

    /**
     * Should be executed whenever a relevant list datamodel is fetched.
     *
     * @param preProcessListDataModel dataModel that will be used for pre
     * process actions.
     */
    protected void loadPreProcessListDataModelIfNeeded(DataModel preProcessListDataModel) {
        if (preProcessListDataModel != null) {
            preProcessDomainEntityListDataModel = preProcessListDataModel;

            // Check if requested list dataMode was loaded.
            if (preProcessLoadedListDataModelHashCode == null
                    || getPreProcessDomainEntityListDataModelHashCode() != preProcessLoadedListDataModelHashCode) {
                logger.debug("Desired list data model changed. Starting data model pre process.");
                resetPreProcessVariables();
                processPreProcessDomainEntityListDataModel();
            }

            preProcessLoadedListDataModelHashCode = getPreProcessDomainEntityListDataModelHashCode();
        }
    }
    
    /**
     * Variables that need to be reset when list data model changes. 
     * 
     */
    protected void resetPreProcessVariables() {
        loadedDisplayPropertyTypes = null; 
    }

    /**
     * Check to determine if based on the current settings, a pre process is
     * required.
     *
     * @return preProcess iterate should be executed.
     */
    protected boolean isPreProcessListDataModelIterateNeeded() {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            return isPreProcessPropertyValueInformationSettingsPresent();
        }
        return false;
    }

    /**
     * preProcess iterate was execute. Update any necessary variables prior to
     * processing.
     */
    protected void preparePreProcessListDataModelIterate() {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            loadedDisplayPropertyTypes = new ArrayList<>();
        }
    }

    /**
     * Iterate over preprocess datamodel if required by current settings.
     */
    private void processPreProcessDomainEntityListDataModel() {
        if (isPreProcessListDataModelIterateNeeded()) {            
            iteratePreProcessListDataModel();
        }
    }

    /**
     * Performs pre process skipping list data model hash check.
     */
    private void forceLoadPreProcessListDataModel() {
        processPreProcessDomainEntityListDataModel();
    }
    
    public void loadPreProcessPropertyValueInformationColumns() {
        logger.debug("User requested loading of property values for current data model.");
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            forceLoadPreProcessListDataModel();
        } else {
            filterByPropertiesAutoLoad = true;
            forceLoadPreProcessListDataModel();
            filterByPropertiesAutoLoad = false;
        }
    }

    /**
     * Execute on each iteration of pre process iterateListDataModel.
     *
     * @param entity
     */
    protected void processPreProcessIteratedDomainEntity(CdbDomainEntity entity) {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            setPreProcessPropertyValueInformation(entity);
        }
    }

    /**
     * PreProcess iterate execution is completed; update any necessary
     * variables.
     */
    protected void updatePreProcessListProcessedVariables() {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            updatePreProcessCurrentPropertyValueSettingsLoaded();
        }
    }

    protected boolean isPreProcessPropertyValueInformationSettingsPresent() {
        return displayPropertyTypeId1 != null
                || displayPropertyTypeId2 != null
                || displayPropertyTypeId3 != null
                || displayPropertyTypeId4 != null
                || displayPropertyTypeId5 != null;
    }

    /**
     * Loads all of the necessary property types for the entity passed.
     *
     * @param entity
     */
    protected void setPreProcessPropertyValueInformation(CdbDomainEntity entity) {
        loadPropertyValueInformation(displayPropertyTypeId1, entity);
        loadPropertyValueInformation(displayPropertyTypeId2, entity);
        loadPropertyValueInformation(displayPropertyTypeId3, entity);
        loadPropertyValueInformation(displayPropertyTypeId4, entity);
        loadPropertyValueInformation(displayPropertyTypeId5, entity);
    }

    /**
     * Marks all of the currently set property types
     */
    protected void updatePreProcessCurrentPropertyValueSettingsLoaded() {
        addPropertyTypeToLoadedDisplayPropertyTypes(displayPropertyTypeId1);
        addPropertyTypeToLoadedDisplayPropertyTypes(displayPropertyTypeId2);
        addPropertyTypeToLoadedDisplayPropertyTypes(displayPropertyTypeId3);
        addPropertyTypeToLoadedDisplayPropertyTypes(displayPropertyTypeId4);
        addPropertyTypeToLoadedDisplayPropertyTypes(displayPropertyTypeId5);
    }

    /**
     * Execute iterate over preProcess List data model.
     */
    private void iteratePreProcessListDataModel() {
        logger.debug("Data model pre process iterate starting.");
        preparePreProcessListDataModelIterate();
        DataModel<CdbDomainEntity> cdbDomainEntityList = preProcessDomainEntityListDataModel;
        Iterator<CdbDomainEntity> cdbDomainEntityIterator = cdbDomainEntityList.iterator();
        while (cdbDomainEntityIterator.hasNext()) {
            CdbDomainEntity cdbDomainEntity = cdbDomainEntityIterator.next();
            processPreProcessIteratedDomainEntity(cdbDomainEntity);
        }
        updatePreProcessListProcessedVariables();
    }

    protected void addPropertyTypeToLoadedDisplayPropertyTypes(Integer propertyTypeId) {
        if (propertyTypeId != null) {
            if (!loadedDisplayPropertyTypes.contains(propertyTypeId)) {
                loadedDisplayPropertyTypes.add(propertyTypeId);
            }
        }
    }

    protected void loadPropertyValueInformation(Integer propertyTypeId, CdbDomainEntity entity) {
        if (propertyTypeId != null) {
            entity.getPropertyValueInformation(propertyTypeId);
        }
    }

    protected Boolean fetchFilterablePropertyValue(Integer propertyTypeId) {
        if (propertyTypeId != null) {
            if (loadedDisplayPropertyTypes != null) {
                if (preProcessLoadedListDataModelHashCode != null && getPreProcessDomainEntityListDataModelHashCode() != preProcessLoadedListDataModelHashCode) {
                    loadedDisplayPropertyTypes = null;
                    return false;
                }
                for (Integer loadedPropertyTypeId : loadedDisplayPropertyTypes) {
                    if (Objects.equals(propertyTypeId, loadedPropertyTypeId)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Boolean getDisplayGalleryViewableDocuments() {
        return displayGalleryViewableDocuments;
    }

    public void setDisplayGalleryViewableDocuments(Boolean displayGalleryViewableDocuments) {
        this.displayGalleryViewableDocuments = displayGalleryViewableDocuments;
    }

    public Boolean getFilterablePropertyValue1() {
        return fetchFilterablePropertyValue(displayPropertyTypeId1);
    }

    public Boolean getFilterablePropertyValue2() {
        return fetchFilterablePropertyValue(displayPropertyTypeId2);
    }

    public Boolean getFilterablePropertyValue3() {
        return fetchFilterablePropertyValue(displayPropertyTypeId3);
    }

    public Boolean getFilterablePropertyValue4() {
        return fetchFilterablePropertyValue(displayPropertyTypeId4);
    }

    public Boolean getFilterablePropertyValue5() {
        return fetchFilterablePropertyValue(displayPropertyTypeId5);
    }

    public Boolean getFilterByPropertiesAutoLoad() {
        return filterByPropertiesAutoLoad;
    }

    public void setFilterByPropertiesAutoLoad(Boolean filterByPropertiesAutoLoad) {
        this.filterByPropertiesAutoLoad = filterByPropertiesAutoLoad;
    }

    protected Boolean checkDisplayLoadPropertyValueButtonByProperty(Integer propertyTypeId) {
        if (propertyTypeId == null) {
            return false;
        } else {
            return !fetchFilterablePropertyValue(propertyTypeId);
        }
    }

    @Override
    public Boolean getDisplayLoadPropertyValuesButton() {
        if (filterByPropertiesAutoLoad != null && filterByPropertiesAutoLoad) {
            return false;
        }

        return (checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId1)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId2)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId3)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId4)
                || checkDisplayLoadPropertyValueButtonByProperty(displayPropertyTypeId5));
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

    public Integer getDisplayPropertyTypeId1() {
        return displayPropertyTypeId1;
    }

    public void setDisplayPropertyTypeId1(Integer displayPropertyTypeId1) {
        this.displayPropertyTypeId1 = displayPropertyTypeId1;
    }

    public Integer getDisplayPropertyTypeId2() {
        return displayPropertyTypeId2;
    }

    public void setDisplayPropertyTypeId2(Integer displayPropertyTypeId2) {
        this.displayPropertyTypeId2 = displayPropertyTypeId2;
    }

    public Integer getDisplayPropertyTypeId3() {
        return displayPropertyTypeId3;
    }

    public void setDisplayPropertyTypeId3(Integer displayPropertyTypeId3) {
        this.displayPropertyTypeId3 = displayPropertyTypeId3;
    }

    public Integer getDisplayPropertyTypeId4() {
        return displayPropertyTypeId4;
    }

    public void setDisplayPropertyTypeId4(Integer displayPropertyTypeId4) {
        this.displayPropertyTypeId4 = displayPropertyTypeId4;
    }

    public Integer getDisplayPropertyTypeId5() {
        return displayPropertyTypeId5;
    }

    public void setDisplayPropertyTypeId5(Integer displayPropertyTypeId5) {
        this.displayPropertyTypeId5 = displayPropertyTypeId5;
    }

    public Boolean getDisplayRowExpansion() {
        return displayRowExpansion;
    }

    public void setDisplayRowExpansion(Boolean displayRowExpansion) {
        this.displayRowExpansion = displayRowExpansion;
    }

    public Boolean getLoadRowExpansionPropertyValues() {
        return loadRowExpansionPropertyValues;
    }

    public void setLoadRowExpansionPropertyValues(Boolean loadRowExpansionPropertyValues) {
        this.loadRowExpansionPropertyValues = loadRowExpansionPropertyValues;
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

    public String getFilterByPropertyValue1() {
        return filterByPropertyValue1;
    }

    public void setFilterByPropertyValue1(String filterByPropertyValue1) {
        this.filterByPropertyValue1 = filterByPropertyValue1;
    }

    public String getFilterByPropertyValue2() {
        return filterByPropertyValue2;
    }

    public void setFilterByPropertyValue2(String filterByPropertyValue2) {
        this.filterByPropertyValue2 = filterByPropertyValue2;
    }

    public String getFilterByPropertyValue3() {
        return filterByPropertyValue3;
    }

    public void setFilterByPropertyValue3(String filterByPropertyValue3) {
        this.filterByPropertyValue3 = filterByPropertyValue3;
    }

    public String getFilterByPropertyValue4() {
        return filterByPropertyValue4;
    }

    public void setFilterByPropertyValue4(String filterByPropertyValue4) {
        this.filterByPropertyValue4 = filterByPropertyValue4;
    }

    public String getFilterByPropertyValue5() {
        return filterByPropertyValue5;
    }

    public void setFilterByPropertyValue5(String filterByPropertyValue5) {
        this.filterByPropertyValue5 = filterByPropertyValue5;
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
            EntityType cdbDomainEntity = this.current;
            cdbDomainEntity.getLogList().remove(newLogEdit);
            newLogEdit = null;
        }
    }

    public void saveLogList() {
        newLogEdit = null;
        update();
    }

    public void prepareAddLog(EntityType cdbDomainEntity) {
        Log logEntry = LogUtility.createLogEntry();
        setNewLogEdit(logEntry);
        List<Log> cdbDomainEntityLogList = cdbDomainEntity.getLogList();
        cdbDomainEntityLogList.add(0, logEntry);
    }

    public List<Log> getLogList() {
        EntityType cdbDomainEntity = getCurrent();
        List<Log> componentInstanceLogList = cdbDomainEntity.getLogList();
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser != null) {
            if (settingsTimestamp == null || sessionUser.areSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionSettingEntity(sessionUser);
                settingsTimestamp = new Date();
            }
        }
        return componentInstanceLogList;
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
}
