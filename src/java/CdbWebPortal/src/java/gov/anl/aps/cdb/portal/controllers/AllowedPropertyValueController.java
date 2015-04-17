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

import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.beans.AllowedPropertyValueDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

/**
 * Controller class for allowed property values.
 */
@Named("allowedPropertyValueController")
@SessionScoped
public class AllowedPropertyValueController extends CdbEntityController<AllowedPropertyValue, AllowedPropertyValueDbFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "AllowedPropertyValue.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "AllowedPropertyValue.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "AllowedPropertyValue.List.Display.Description";
    private static final String DisplaySortOrderSettingTypeKey = "AllowedPropertyValue.List.Display.SortOrder";
    private static final String DisplayUnitsSettingTypeKey = "AllowedPropertyValue.List.Display.Units";
    private static final String FilterByDescriptionSettingTypeKey = "AllowedPropertyValue.List.FilterBy.Description";
    private static final String FilterBySortOrderSettingTypeKey = "AllowedPropertyValue.List.FilterBy.SortOrder";
    private static final String FilterByUnitsSettingTypeKey = "AllowedPropertyValue.List.FilterBy.Units";
    private static final String FilterByValueSettingTypeKey = "AllowedPropertyValue.List.FilterBy.Value";

    @EJB
    private AllowedPropertyValueDbFacade allowedPropertyValueFacade;
    private static final Logger logger = Logger.getLogger(AllowedPropertyValueController.class.getName());

    private Boolean displayUnits = null;
    private Boolean displaySortOrder = null;

    private String filterBySortOrder = null;
    private String filterByUnits = null;
    private String filterByValue = null;

    public AllowedPropertyValueController() {
    }

    @Override
    protected AllowedPropertyValueDbFacade getEntityDbFacade() {
        return allowedPropertyValueFacade;
    }

    @Override
    protected AllowedPropertyValue createEntityInstance() {
        AllowedPropertyValue allowedPropertyValue = new AllowedPropertyValue();
        return allowedPropertyValue;
    }

    @Override
    public String getEntityTypeName() {
        return "allowedPropertyValue";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "allowed property value";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<AllowedPropertyValue> getAvailableItems() {
        return super.getAvailableItems();
    }

    /**
     * Retrieve data model with all allowed property value objects that
     * correspond to the given property type.
     *
     * @param propertyTypeId property type id
     * @return data model with allowed property values
     */
    public DataModel getListDataModelByPropertyTypeId(Integer propertyTypeId) {
        return new ListDataModel(allowedPropertyValueFacade.findAllByPropertyTypeId(propertyTypeId));
    }

    /**
     * Find list of all allowed property value objects that correspond to the
     * given property type.
     *
     * @param propertyTypeId property type id
     * @return list of allowed property values
     */
    public List<AllowedPropertyValue> findAllByPropertyTypeId(Integer propertyTypeId) {
        return allowedPropertyValueFacade.findAllByPropertyTypeId(propertyTypeId);
    }

    /**
     * Remove specified allowed property value from the database
     *
     * @param allowedPropertyValue object to be deleted from the database
     */
    @Override
    public void destroy(AllowedPropertyValue allowedPropertyValue) {
        if (allowedPropertyValue.getId() != null) {
            super.destroy(allowedPropertyValue);
        }
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displaySortOrder = Boolean.parseBoolean(settingTypeMap.get(DisplaySortOrderSettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterBySortOrder = settingTypeMap.get(FilterBySortOrderSettingTypeKey).getDefaultValue();
        filterByUnits = settingTypeMap.get(FilterByUnitsSettingTypeKey).getDefaultValue();
        filterByValue = settingTypeMap.get(FilterByValueSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        displaySortOrder = sessionUser.getUserSettingValueAsBoolean(DisplaySortOrderSettingTypeKey, displaySortOrder);
        displayUnits = sessionUser.getUserSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterBySortOrder = sessionUser.getUserSettingValueAsString(FilterBySortOrderSettingTypeKey, filterBySortOrder);
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
        filterBySortOrder = (String) filters.get("sortOrder");
        filterByUnits = (String) filters.get("units");
        filterByValue = (String) filters.get("value");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplaySortOrderSettingTypeKey, displaySortOrder);
        sessionUser.setUserSettingValue(DisplayUnitsSettingTypeKey, displayUnits);

        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterBySortOrderSettingTypeKey, filterBySortOrder);
        sessionUser.setUserSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        sessionUser.setUserSettingValue(FilterByValueSettingTypeKey, filterByValue);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterBySortOrder = null;
        filterByUnits = null;
        filterByValue = null;
    }

    public Boolean getDisplayUnits() {
        return displayUnits;
    }

    public void setDisplayUnits(Boolean displayUnits) {
        this.displayUnits = displayUnits;
    }

    public Boolean getDisplaySortOrder() {
        return displaySortOrder;
    }

    public void setDisplaySortOrder(Boolean displaySortOrder) {
        this.displaySortOrder = displaySortOrder;
    }

    public String getFilterBySortOrder() {
        return filterBySortOrder;
    }

    public void setFilterBySortOrder(String filterBySortOrder) {
        this.filterBySortOrder = filterBySortOrder;
    }

    public String getFilterByUnits() {
        return filterByUnits;
    }

    public void setFilterByUnits(String filterByUnits) {
        this.filterByUnits = filterByUnits;
    }

    public String getFilterByValue() {
        return filterByValue;
    }

    public void setFilterByValue(String filterByValue) {
        this.filterByValue = filterByValue;
    }

    /**
     * Converter class for allowed property value objects.
     */
    @FacesConverter(forClass = AllowedPropertyValue.class)
    public static class AllowedPropertyValueControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                AllowedPropertyValueController controller = (AllowedPropertyValueController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "allowedPropertyValueController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to allowed property valueS object.");
                return null;
            }
        }

        private Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        private String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AllowedPropertyValue) {
                AllowedPropertyValue o = (AllowedPropertyValue) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AllowedPropertyValue.class.getName());
            }
        }

    }

}
