/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueHistoryDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
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

/**
 * Controller class for property value history objects.
 */
@Named("propertyValueHistoryController")
@SessionScoped
public class PropertyValueHistoryController extends CdbEntityController<PropertyValueHistory, PropertyValueHistoryDbFacade> implements Serializable {

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
    private PropertyValueHistoryDbFacade propertyValueHistoryFacade;

    public PropertyValueHistoryController() {
        super();
    }

    @Override
    protected PropertyValueHistoryDbFacade getEntityDbFacade() {
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
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayEnteredByUser = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayTag = sessionUser.getUserSettingValueAsBoolean(DisplayTagSettingTypeKey, displayTag);
        displayUnits = sessionUser.getUserSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);

        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEnteredByUser = sessionUser.getUserSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = sessionUser.getUserSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByTag = sessionUser.getUserSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);
        filterByUnits = sessionUser.getUserSettingValueAsString(FilterByUnitsSettingTypeKey, filterByUnits);
        filterByValue = sessionUser.getUserSettingValueAsString(FilterByValueSettingTypeKey, filterByValue);
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
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        sessionUser.setUserSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        sessionUser.setUserSettingValue(DisplayTagSettingTypeKey, displayTag);
        sessionUser.setUserSettingValue(DisplayUnitsSettingTypeKey, displayUnits);

        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        sessionUser.setUserSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        sessionUser.setUserSettingValue(FilterByTagSettingTypeKey, filterByTag);
        sessionUser.setUserSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        sessionUser.setUserSettingValue(FilterByValueSettingTypeKey, filterByValue);

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
        return StorageUtility.getPropertyValueImagePath(propertyValueHistory.getValue() + CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);   
    }
 
}
