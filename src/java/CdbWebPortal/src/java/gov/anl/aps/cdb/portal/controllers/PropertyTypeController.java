package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.entities.Component;
import gov.anl.aps.cdb.portal.model.entities.ComponentType;

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
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("propertyTypeController")
@SessionScoped
public class PropertyTypeController extends CrudEntityController<PropertyType, PropertyTypeFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyType.List.Display.Description";

    private static final String DisplayCategorySettingTypeKey = "PropertyType.List.Display.Category";
    private static final String DisplayDefaultUnitsSettingTypeKey = "PropertyType.List.Display.DefaultUnits";
    private static final String DisplayDefaultValueSettingTypeKey = "PropertyType.List.Display.DefaultValue";
    private static final String DisplayHandlerNameSettingTypeKey = "PropertyType.List.Display.HandlerName";
    private static final String DisplayIsDynamicSettingTypeKey = "PropertyType.List.Display.IsDynamic";
    private static final String DisplayIsUserWriteableSettingTypeKey = "PropertyType.List.Display.IsUserWriteable";

    private static final String FilterByNameSettingTypeKey = "PropertyType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyType.List.FilterBy.Description";

    private static final String FilterByCategorySettingTypeKey = "PropertyType.List.FilterBy.Category";
    private static final String FilterByDefaultUnitsSettingTypeKey = "PropertyType.List.FilterBy.DefaultUnits";
    private static final String FilterByDefaultValueSettingTypeKey = "PropertyType.List.FilterBy.DefaultValue";
    private static final String FilterByHandlerNameSettingTypeKey = "PropertyType.List.FilterBy.HandlerName";
    private static final String FilterByIsDynamicSettingTypeKey = "PropertyType.List.FilterBy.IsDynamic";
    private static final String FilterByIsUserWriteableSettingTypeKey = "PropertyType.List.FilterBy.IsUserWriteable";

    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());

    @EJB
    private PropertyTypeFacade propertyTypeFacade;

    private Boolean displayCategory = null;
    private Boolean displayDefaultUnits = null;
    private Boolean displayDefaultValue = null;
    private Boolean displayHandlerName = null;
    private Boolean displayIsDynamic = null;
    private Boolean displayIsUserWriteable = null;

    private String filterByCategory = null;
    private String filterByDefaultUnits = null;
    private String filterByDefaultValue = null;
    private String filterByHandlerName = null;
    private String filterByIsDynamic = null;
    private String filterByIsUserWriteable = null;

    private Boolean selectDisplayCategory = true;
    private Boolean selectDisplayDefaultUnits = true;
    private Boolean selectDisplayDefaultValue = true;
    private Boolean selectDisplayHandlerName = true;
    private Boolean selectDisplayIsDynamic = false;
    private Boolean selectDisplayIsUserWriteable = false;

    private String selectFilterByCategory = null;
    private String selectFilterByDefaultUnits = null;
    private String selectFilterByDefaultValue = null;
    private String selectFilterByHandlerName = null;
    private String selectFilterByIsDynamic = null;
    private String selectFilterByIsUserWriteable = null;

    public PropertyTypeController() {
    }

    @Override
    protected PropertyTypeFacade getFacade() {
        return propertyTypeFacade;
    }

    @Override
    protected PropertyType createEntityInstance() {
        PropertyType propertyType = new PropertyType();
        return propertyType;
    }

    @Override
    public String getEntityTypeName() {
        return "propertyType";
    }

    @Override
    public String getEntityTypeCategoryName() {
        return "propertyTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "property type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<PropertyType> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(PropertyType propertyType) throws ObjectAlreadyExists {
        PropertyType existingPropertyType = propertyTypeFacade.findByName(propertyType.getName());
        if (existingPropertyType != null) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyType.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyType propertyType) throws ObjectAlreadyExists {
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        displayCategory = Boolean.parseBoolean(settingTypeMap.get(DisplayCategorySettingTypeKey).getDefaultValue());
        displayDefaultUnits = Boolean.parseBoolean(settingTypeMap.get(DisplayDefaultUnitsSettingTypeKey).getDefaultValue());
        displayDefaultValue = Boolean.parseBoolean(settingTypeMap.get(DisplayDefaultValueSettingTypeKey).getDefaultValue());
        displayHandlerName = Boolean.parseBoolean(settingTypeMap.get(DisplayHandlerNameSettingTypeKey).getDefaultValue());
        displayIsDynamic = Boolean.parseBoolean(settingTypeMap.get(DisplayIsDynamicSettingTypeKey).getDefaultValue());
        displayIsUserWriteable = Boolean.parseBoolean(settingTypeMap.get(DisplayIsUserWriteableSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByDefaultUnits = settingTypeMap.get(FilterByDefaultUnitsSettingTypeKey).getDefaultValue();
        filterByDefaultValue = settingTypeMap.get(FilterByDefaultValueSettingTypeKey).getDefaultValue();
        filterByHandlerName = settingTypeMap.get(FilterByHandlerNameSettingTypeKey).getDefaultValue();
        filterByIsDynamic = settingTypeMap.get(FilterByIsDynamicSettingTypeKey).getDefaultValue();
        filterByIsUserWriteable = settingTypeMap.get(FilterByIsUserWriteableSettingTypeKey).getDefaultValue();

    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        displayCategory = sessionUser.getUserSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayCategory);
        displayDefaultUnits = sessionUser.getUserSettingValueAsBoolean(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        displayDefaultValue = sessionUser.getUserSettingValueAsBoolean(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        displayHandlerName = sessionUser.getUserSettingValueAsBoolean(DisplayHandlerNameSettingTypeKey, displayHandlerName);
        displayIsDynamic = sessionUser.getUserSettingValueAsBoolean(DisplayIsDynamicSettingTypeKey, displayIsDynamic);
        displayIsUserWriteable = sessionUser.getUserSettingValueAsBoolean(DisplayIsUserWriteableSettingTypeKey, displayIsUserWriteable);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByCategory = sessionUser.getUserSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByDefaultUnits = sessionUser.getUserSettingValueAsString(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        filterByDefaultValue = sessionUser.getUserSettingValueAsString(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        filterByHandlerName = sessionUser.getUserSettingValueAsString(FilterByHandlerNameSettingTypeKey, filterByHandlerName);
        filterByIsDynamic = sessionUser.getUserSettingValueAsString(FilterByIsDynamicSettingTypeKey, filterByIsDynamic);
        filterByIsUserWriteable = sessionUser.getUserSettingValueAsString(FilterByIsUserWriteableSettingTypeKey, filterByIsUserWriteable);

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, String> filters = dataTable.getFilters();
        filterByCategory = filters.get("propertyTypeCategory.name");
        filterByDefaultUnits = filters.get("defaultUnits");
        filterByDefaultValue = filters.get("defaultValue");
        filterByHandlerName = filters.get("handlerName");
        filterByIsDynamic = filters.get("isDynamic");
        filterByIsUserWriteable = filters.get("isUserWriteable");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        sessionUser.setUserSettingValue(DisplayCategorySettingTypeKey, displayCategory);
        sessionUser.setUserSettingValue(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        sessionUser.setUserSettingValue(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        sessionUser.setUserSettingValue(DisplayHandlerNameSettingTypeKey, displayHandlerName);
        sessionUser.setUserSettingValue(DisplayIsDynamicSettingTypeKey, displayIsDynamic);
        sessionUser.setUserSettingValue(DisplayIsUserWriteableSettingTypeKey, displayIsUserWriteable);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        sessionUser.setUserSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        sessionUser.setUserSettingValue(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        sessionUser.setUserSettingValue(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        sessionUser.setUserSettingValue(FilterByHandlerNameSettingTypeKey, filterByHandlerName);
        sessionUser.setUserSettingValue(FilterByIsDynamicSettingTypeKey, filterByIsDynamic);
        sessionUser.setUserSettingValue(FilterByIsUserWriteableSettingTypeKey, filterByIsUserWriteable);

    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCategory = null;
        filterByDefaultUnits = null;
        filterByDefaultValue = null;
        filterByHandlerName = null;
        filterByIsDynamic = null;
        filterByIsUserWriteable = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByCategory = null;
        selectFilterByDefaultUnits = null;
        selectFilterByDefaultValue = null;
        selectFilterByHandlerName = null;
        selectFilterByIsDynamic = null;
        selectFilterByIsUserWriteable = null;
    }

    @Override
    public boolean entityHasCategories() {
        return true;
    }

    public void prepareAddAllowedPropertyValue(String value) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        AllowedPropertyValue allowedPropertyValue = new AllowedPropertyValue();
        allowedPropertyValue.setValue(value);
        allowedPropertyValue.setPropertyType(propertyType);
        allowedPropertyValueList.add(allowedPropertyValue);
    }

    public void saveAllowedPropertyValueList() {
        update();
    }

    public void deleteAllowedPropertyValue(AllowedPropertyValue allowedPropertyValue) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        allowedPropertyValueList.remove(allowedPropertyValue);
    }


    public void prepareSelectPropertyTypesForComponentType(ComponentType componentType) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getFacade().findAll();
        List<PropertyType> componentTypePropertyList = componentType.getPropertyTypeList();
        selectPropertyTypeList.removeAll(componentTypePropertyList);
        createSelectDataModel(selectPropertyTypeList);
    }

    public void prepareSelectPropertyTypesForComponent(Component component) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
    }
    
    @FacesConverter(forClass = PropertyType.class)
    public static class PropertyTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PropertyTypeController controller = (PropertyTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "propertyTypeController");
            return controller.getEntity(getKey(value));
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
            if (object instanceof PropertyType) {
                PropertyType o = (PropertyType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyType.class.getName());
            }
        }

    }

    public Boolean getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(Boolean displayCategory) {
        this.displayCategory = displayCategory;
    }

    public Boolean getDisplayDefaultUnits() {
        return displayDefaultUnits;
    }

    public void setDisplayDefaultUnits(Boolean displayDefaultUnits) {
        this.displayDefaultUnits = displayDefaultUnits;
    }

    public Boolean getDisplayDefaultValue() {
        return displayDefaultValue;
    }

    public void setDisplayDefaultValue(Boolean displayDefaultValue) {
        this.displayDefaultValue = displayDefaultValue;
    }

    public Boolean getDisplayHandlerName() {
        return displayHandlerName;
    }

    public void setDisplayHandlerName(Boolean displayHandlerName) {
        this.displayHandlerName = displayHandlerName;
    }

    public Boolean getDisplayIsDynamic() {
        return displayIsDynamic;
    }

    public void setDisplayIsDynamic(Boolean displayIsDynamic) {
        this.displayIsDynamic = displayIsDynamic;
    }

    public Boolean getDisplayIsUserWriteable() {
        return displayIsUserWriteable;
    }

    public void setDisplayIsUserWriteable(Boolean displayIsUserWriteable) {
        this.displayIsUserWriteable = displayIsUserWriteable;
    }

    public String getFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(String filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public String getFilterByDefaultUnits() {
        return filterByDefaultUnits;
    }

    public void setFilterByDefaultUnits(String filterByDefaultUnits) {
        this.filterByDefaultUnits = filterByDefaultUnits;
    }

    public String getFilterByDefaultValue() {
        return filterByDefaultValue;
    }

    public void setFilterByDefaultValue(String filterByDefaultValue) {
        this.filterByDefaultValue = filterByDefaultValue;
    }

    public String getFilterByHandlerName() {
        return filterByHandlerName;
    }

    public void setFilterByHandlerName(String filterByHandlerName) {
        this.filterByHandlerName = filterByHandlerName;
    }

    public String getFilterByIsDynamic() {
        return filterByIsDynamic;
    }

    public void setFilterByIsDynamic(String filterByIsDynamic) {
        this.filterByIsDynamic = filterByIsDynamic;
    }

    public String getFilterByIsUserWriteable() {
        return filterByIsUserWriteable;
    }

    public void setFilterByIsUserWriteable(String filterByIsUserWriteable) {
        this.filterByIsUserWriteable = filterByIsUserWriteable;
    }

    public Boolean getSelectDisplayCategory() {
        return selectDisplayCategory;
    }

    public void setSelectDisplayCategory(Boolean selectDisplayCategory) {
        this.selectDisplayCategory = selectDisplayCategory;
    }

    public Boolean getSelectDisplayDefaultUnits() {
        return selectDisplayDefaultUnits;
    }

    public void setSelectDisplayDefaultUnits(Boolean selectDisplayDefaultUnits) {
        this.selectDisplayDefaultUnits = selectDisplayDefaultUnits;
    }

    public Boolean getSelectDisplayDefaultValue() {
        return selectDisplayDefaultValue;
    }

    public void setSelectDisplayDefaultValue(Boolean selectDisplayDefaultValue) {
        this.selectDisplayDefaultValue = selectDisplayDefaultValue;
    }

    public Boolean getSelectDisplayHandlerName() {
        return selectDisplayHandlerName;
    }

    public void setSelectDisplayHandlerName(Boolean selectDisplayHandlerName) {
        this.selectDisplayHandlerName = selectDisplayHandlerName;
    }

    public Boolean getSelectDisplayIsDynamic() {
        return selectDisplayIsDynamic;
    }

    public void setSelectDisplayIsDynamic(Boolean selectDisplayIsDynamic) {
        this.selectDisplayIsDynamic = selectDisplayIsDynamic;
    }

    public Boolean getSelectDisplayIsUserWriteable() {
        return selectDisplayIsUserWriteable;
    }

    public void setSelectDisplayIsUserWriteable(Boolean selectDisplayIsUserWriteable) {
        this.selectDisplayIsUserWriteable = selectDisplayIsUserWriteable;
    }

    public String getSelectFilterByCategory() {
        return selectFilterByCategory;
    }

    public void setSelectFilterByCategory(String selectFilterByCategory) {
        this.selectFilterByCategory = selectFilterByCategory;
    }

    public String getSelectFilterByDefaultUnits() {
        return selectFilterByDefaultUnits;
    }

    public void setSelectFilterByDefaultUnits(String selectFilterByDefaultUnits) {
        this.selectFilterByDefaultUnits = selectFilterByDefaultUnits;
    }

    public String getSelectFilterByDefaultValue() {
        return selectFilterByDefaultValue;
    }

    public void setSelectFilterByDefaultValue(String selectFilterByDefaultValue) {
        this.selectFilterByDefaultValue = selectFilterByDefaultValue;
    }

    public String getSelectFilterByHandlerName() {
        return selectFilterByHandlerName;
    }

    public void setSelectFilterByHandlerName(String selectFilterByHandlerName) {
        this.selectFilterByHandlerName = selectFilterByHandlerName;
    }

    public String getSelectFilterByIsDynamic() {
        return selectFilterByIsDynamic;
    }

    public void setSelectFilterByIsDynamic(String selectFilterByIsDynamic) {
        this.selectFilterByIsDynamic = selectFilterByIsDynamic;
    }

    public String getSelectFilterByIsUserWriteable() {
        return selectFilterByIsUserWriteable;
    }

    public void setSelectFilterByIsUserWriteable(String selectFilterByIsUserWriteable) {
        this.selectFilterByIsUserWriteable = selectFilterByIsUserWriteable;
    }

}
