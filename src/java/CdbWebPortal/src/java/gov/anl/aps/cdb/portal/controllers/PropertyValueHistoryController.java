/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueHistoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("propertyValueHistoryController")
@SessionScoped
public class PropertyValueHistoryController extends CdbEntityController<PropertyValueHistory, PropertyValueHistoryFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyValueHistory.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyValueHistory.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyValueHistory.List.Display.Description";
    private static final String DisplayEnteredByUserSettingTypeKey = "PropertyValueHistory.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "PropertyValueHistory.List.Display.EnteredOnDateTime";
    private static final String DisplayTagSettingTypeKey = "PropertyValueHistory.List.Display.Tag";
    private static final String DisplayUnitsSettingTypeKey = "PropertyValueHistory.List.Display.Units";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyValueHistory.List.FilterBy.Description";
    private static final String FilterByEnteredByUserSettingTypeKey = "PropertyValueHistory.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "PropertyValueHistory.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByTagSettingTypeKey = "PropertyValueHistory.List.FilterBy.Tag";
    private static final String FilterByValueSettingTypeKey = "PropertyValueHistory.List.FilterBy.Value";
    private static final String FilterByUnitsSettingTypeKey = "PropertyValueHistory.List.FilterBy.Units";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayTag = null;
    private Boolean displayUnits = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByTag = null;
    private String filterByValue = null;
    private String filterByUnits = null;

    private List<PropertyValueHistory> selectedPropertyValueHistoryList;
    private String selectedPropertyValueTypeName = null;
    private PropertyValue selectedPropertyValue = null;
    private DisplayType displayType = null;

    private static final Logger logger = Logger.getLogger(PropertyValueHistoryController.class.getName());

    @EJB
    private PropertyValueHistoryFacade propertyValueHistoryFacade;

    public PropertyValueHistoryController() {
        super();
    }

    @Override
    protected PropertyValueHistoryFacade getEntityDbFacade() {
        return propertyValueHistoryFacade;
    }

    @Override
    protected PropertyValueHistory createEntityInstance() {
        return new PropertyValueHistory();
    }

    @Override
    public String getEntityTypeName() {
        return "propertyValueHistory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Property Value History";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<PropertyValueHistory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayTag = Boolean.parseBoolean(settingTypeMap.get(DisplayTagSettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();
        filterByUnits = settingTypeMap.get(FilterByUnitsSettingTypeKey).getDefaultValue();
        filterByValue = settingTypeMap.get(FilterByValueSettingTypeKey).getDefaultValue();

    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEnteredByUser = settingEntity.getSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayTag = settingEntity.getSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayUnits = settingEntity.getSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);

        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEnteredByUser = settingEntity.getSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = settingEntity.getSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByTag = settingEntity.getSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);
        filterByUnits = settingEntity.getSettingValueAsString(FilterByUnitsSettingTypeKey, filterByUnits);
        filterByValue = settingEntity.getSettingValueAsString(FilterByValueSettingTypeKey, filterByValue);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByEnteredByUser = (String) filters.get("enteredByUser");
        filterByEnteredOnDateTime = (String) filters.get("enteredOnDateTime");
        filterByTag = (String) filters.get("tag");
        filterByUnits = (String) filters.get("units");
        filterByValue = (String) filters.get("value");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        settingEntity.setSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        settingEntity.setSettingValue(DisplayTagSettingTypeKey, displayTag);
        settingEntity.setSettingValue(DisplayUnitsSettingTypeKey, displayUnits);

        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        settingEntity.setSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        settingEntity.setSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        settingEntity.setSettingValue(FilterByTagSettingTypeKey, filterByTag);
        settingEntity.setSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        settingEntity.setSettingValue(FilterByValueSettingTypeKey, filterByValue);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByTag = null;
        filterByUnits = null;
        filterByValue = null;
    }

    /**
     * Converter class for property value history objects.
     */
    @FacesConverter(forClass = PropertyValueHistory.class)
    public static class PropertyValueHistoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                PropertyValueHistoryController controller = (PropertyValueHistoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyValueHistoryController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property value history object.");
                return null;
            }
        }

        Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PropertyValueHistory) {
                PropertyValueHistory o = (PropertyValueHistory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyValueHistory.class.getName());
            }
        }

    }

    public Boolean getDisplayEnteredByUser() {
        return displayEnteredByUser;
    }

    public void setDisplayEnteredByUser(Boolean displayEnteredByUser) {
        this.displayEnteredByUser = displayEnteredByUser;
    }

    public Boolean getDisplayEnteredOnDateTime() {
        return displayEnteredOnDateTime;
    }

    public void setDisplayEnteredOnDateTime(Boolean displayEnteredOnDateTime) {
        this.displayEnteredOnDateTime = displayEnteredOnDateTime;
    }

    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }

    public Boolean getDisplayUnits() {
        return displayUnits;
    }

    public void setDisplayUnits(Boolean displayUnits) {
        this.displayUnits = displayUnits;
    }

    public String getFilterByEnteredByUser() {
        return filterByEnteredByUser;
    }

    public void setFilterByEnteredByUser(String filterByEnteredByUser) {
        this.filterByEnteredByUser = filterByEnteredByUser;
    }

    public String getFilterByEnteredOnDateTime() {
        return filterByEnteredOnDateTime;
    }

    public void setFilterByEnteredOnDateTime(String filterByEnteredOnDateTime) {
        this.filterByEnteredOnDateTime = filterByEnteredOnDateTime;
    }

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }

    public String getFilterByUnits() {
        return filterByUnits;
    }

    public void setFilterByUnits(String filterByUnits) {
        this.filterByUnits = filterByUnits;
    }

    public List<PropertyValueHistory> getSelectedPropertyValueHistoryList() {
        return selectedPropertyValueHistoryList;
    }

    public String getSelectedPropertyValueTypeName() {
        return selectedPropertyValueTypeName;
    }

    public PropertyValue getSelectedPropertyValue() {
        return selectedPropertyValue;
    }

    public void setSelectedPropertyValue(PropertyValue selectedPropertyValue) {
        this.selectedPropertyValue = selectedPropertyValue;

        // Reset history list adding the current entry and reversing the order, set property type name
        PropertyValueHistory currentEntry = new PropertyValueHistory();
        currentEntry.updateFromPropertyValue(selectedPropertyValue);
        selectedPropertyValueHistoryList = new ArrayList<>();
        selectedPropertyValueHistoryList.addAll(selectedPropertyValue.getPropertyValueHistoryList());
        selectedPropertyValueHistoryList.add(currentEntry);
        Collections.reverse(selectedPropertyValueHistoryList);
        selectedPropertyValueTypeName = selectedPropertyValue.getPropertyType().getName();
        displayType = selectedPropertyValue.getPropertyType().getDisplayType();
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(selectedPropertyValue);
        for (PropertyValueHistory propertyValueHistory : selectedPropertyValueHistoryList) {
            propertyTypeHandler.setDisplayValue(propertyValueHistory);
            propertyTypeHandler.setTargetValue(propertyValueHistory);
            propertyTypeHandler.setInfoActionCommand(propertyValueHistory);
            propertyTypeHandler.resetOneTimeUseVariables();
        }
    }

    public boolean displayTextValue() {
        return displayType.equals(DisplayType.FREE_FORM_TEXT) || displayType.equals(DisplayType.SELECTED_TEXT);
    }

    public boolean displayImageValue() {
        return displayType.equals(DisplayType.IMAGE);
    }

    public boolean displayHttpLinkValue() {
        return displayType.equals(DisplayType.HTTP_LINK);
    }
    
    public boolean displayInfoActionValue(){
        return displayType.equals(displayType.INFO_ACTION); 
    }
    
    public boolean displayTableRecordReferenceValue(){
        return displayType.equals(displayType.TABLE_RECORD_REFERENCE); 
    }

    public boolean displayDocumentValue() {
        return displayType.equals(DisplayType.DOCUMENT);
    }

    public boolean displayBooleanValue() {
        return displayType.equals(DisplayType.BOOLEAN);
    }

    public boolean displayDateValue() {
        return displayType.equals(DisplayType.DATE);
    }
        
    public String getOriginalImageApplicationPath(PropertyValueHistory propertyValueHistory) {
        return StorageUtility.getApplicationPropertyValueImagePath(propertyValueHistory.getValue() + CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION);   
    }

    public String getThumbnailImagePath(PropertyValueHistory propertyValueHistory) {
        return StorageUtility.getPropertyValueImagePath(propertyValueHistory.getValue(), CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);   
    }
    
}
