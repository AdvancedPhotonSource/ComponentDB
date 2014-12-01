package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;

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
import org.primefaces.component.datatable.DataTable;

@Named("propertyValueController")
@SessionScoped
public class PropertyValueController extends CrudEntityController<PropertyValue, PropertyValueFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyValue.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyValue.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyValue.List.Display.Description";
    private static final String DisplayEnteredByUserSettingTypeKey = "PropertyValue.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "PropertyValue.List.Display.EnteredOnDateTime";
    private static final String DisplayTagSettingTypeKey = "PropertyValue.List.Display.Tag";
    private static final String DisplayTypeCategorySettingTypeKey = "PropertyValue.List.Display.TypeCategory";
    private static final String DisplayUnitsSettingTypeKey = "PropertyValue.List.Display.Units";

    private static final String FilterByDescriptionSettingTypeKey = "PropertyValue.List.FilterBy.Description";
    private static final String FilterByEnteredByUserSettingTypeKey = "PropertyValue.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "PropertyValue.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByNameSettingTypeKey = "PropertyValue.List.FilterBy.Name";
    private static final String FilterByTagSettingTypeKey = "PropertyValue.List.FilterBy.Tag";
    private static final String FilterByTypeSettingTypeKey = "PropertyValue.List.FilterBy.Type";
    private static final String FilterByTypeCategorySettingTypeKey = "PropertyValue.List.FilterBy.TypeCategory";
    private static final String FilterByValueSettingTypeKey = "PropertyValue.List.FilterBy.Value";
    private static final String FilterByUnitsSettingTypeKey = "PropertyValue.List.FilterBy.Units";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayTag = null;
    private Boolean displayTypeCategory = null;
    private Boolean displayUnits = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByTag = null;
    private String filterByType = null;
    private String filterByTypeCategory = null;
    private String filterByValue = null;
    private String filterByUnits = null;

    @EJB
    private PropertyValueFacade propertyValueFacade;

    private DataTable designPropertyValueListDataTable = null;
    private DataTable componentPropertyValueListDataTable = null;

    public PropertyValueController() {
        super();
    }

    @Override
    protected PropertyValueFacade getFacade() {
        return propertyValueFacade;
    }

    @Override
    protected PropertyValue createEntityInstance() {
        return new PropertyValue();
    }

    @Override
    public String getEntityTypeName() {
        return "propertyValue";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<PropertyValue> getAvailableItems() {
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
        displayTypeCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayTypeCategorySettingTypeKey).getDefaultValue());
        displayUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayUnitsSettingTypeKey).getDefaultValue());

        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByTag = settingTypeMap.get(FilterByTagSettingTypeKey).getDefaultValue();
        filterByType = settingTypeMap.get(FilterByTypeSettingTypeKey).getDefaultValue();
        filterByTypeCategory = settingTypeMap.get(FilterByTypeCategorySettingTypeKey).getDefaultValue();
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
        displayTypeCategory = sessionUser.getUserSettingValueAsBoolean(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        displayUnits = sessionUser.getUserSettingValueAsBoolean(DisplayUnitsSettingTypeKey, displayUnits);

        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByEnteredByUser = sessionUser.getUserSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = sessionUser.getUserSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByTag = sessionUser.getUserSettingValueAsString(FilterByTagSettingTypeKey, filterByTag);
        filterByType = sessionUser.getUserSettingValueAsString(FilterByTypeSettingTypeKey, filterByType);
        filterByTypeCategory = sessionUser.getUserSettingValueAsString(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        filterByUnits = sessionUser.getUserSettingValueAsString(FilterByUnitsSettingTypeKey, filterByUnits);
        filterByValue = sessionUser.getUserSettingValueAsString(FilterByValueSettingTypeKey, filterByValue);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, String> filters = dataTable.getFilters();
        filterByEnteredByUser = filters.get("enteredByUser");
        filterByEnteredOnDateTime = filters.get("enteredOnDateTime");
        filterByTag = filters.get("tag");
        filterByType = filters.get("type");
        filterByTypeCategory = filters.get("typeCategory");
        filterByUnits = filters.get("units");
        filterByValue = filters.get("value");
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
        sessionUser.setUserSettingValue(DisplayTypeCategorySettingTypeKey, displayTypeCategory);
        sessionUser.setUserSettingValue(DisplayUnitsSettingTypeKey, displayUnits);

        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        sessionUser.setUserSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        sessionUser.setUserSettingValue(FilterByTagSettingTypeKey, filterByTag);
        sessionUser.setUserSettingValue(FilterByTypeSettingTypeKey, filterByType);
        sessionUser.setUserSettingValue(FilterByTypeCategorySettingTypeKey, filterByTypeCategory);
        sessionUser.setUserSettingValue(FilterByUnitsSettingTypeKey, filterByUnits);
        sessionUser.setUserSettingValue(FilterByValueSettingTypeKey, filterByValue);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByTag = null;
        filterByType = null;
        filterByTypeCategory = null;
        filterByUnits = null;
        filterByValue = null;
    }

    public PropertyTypeHandlerInterface getPropertyTypeHandler(PropertyValue propertyValue) {
        return PropertyTypeHandlerFactory.getHandler(propertyValue);
    }

    public DisplayType configurePropertyValueDisplay(PropertyValue propertyValue) {
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
        propertyTypeHandler.setViewValue(propertyValue);
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

    public DisplayType getPropertyValueDisplayType(PropertyValue propertyValue) {
        DisplayType result = propertyValue.getPropertyType().getDisplayType();
        if (result == null) {
            result = configurePropertyValueDisplay(propertyValue);
        }
        return result;
    }

    public boolean displayFreeFormTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.FREE_FORM_TEXT);
    }

    public boolean displaySelectedTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.SELECTED_TEXT);
    }

    public boolean displayImageValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.IMAGE);
    }

    public boolean displayHttpLinkValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.HTTP_LINK);
    }

    @FacesConverter(forClass = PropertyValue.class)
    public static class PropertyValueControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                PropertyValueController controller = (PropertyValueController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyValueController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // We cannot get this entity from given key.
                return null;
            }
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PropertyValue) {
                PropertyValue o = (PropertyValue) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyValue.class.getName());
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

    public Boolean getDisplayTypeCategory() {
        return displayTypeCategory;
    }

    public void setDisplayTypeCategory(Boolean displayTypeCategory) {
        this.displayTypeCategory = displayTypeCategory;
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

    public Boolean getDisplayTag() {
        return displayTag;
    }

    public void setDisplayTag(Boolean displayTag) {
        this.displayTag = displayTag;
    }

    public String getFilterByTag() {
        return filterByTag;
    }

    public void setFilterByTag(String filterByTag) {
        this.filterByTag = filterByTag;
    }

    public String getFilterByType() {
        return filterByType;
    }

    public void setFilterByType(String filterByType) {
        this.filterByType = filterByType;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
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

    public DataTable getDesignPropertyValueListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            designPropertyValueListDataTable = new DataTable();
        }
        return designPropertyValueListDataTable;
    }

    public void setDesignPropertyValueListDataTable(DataTable designPropertyValueListDataTable) {
        this.designPropertyValueListDataTable = designPropertyValueListDataTable;
    }

    public DataTable getComponentPropertyValueListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            componentPropertyValueListDataTable = new DataTable();
        }
        return componentPropertyValueListDataTable;
    }

    public void setComponentPropertyValueListDataTable(DataTable componentPropertyValueListDataTable) {
        this.componentPropertyValueListDataTable = componentPropertyValueListDataTable;
    }

}
