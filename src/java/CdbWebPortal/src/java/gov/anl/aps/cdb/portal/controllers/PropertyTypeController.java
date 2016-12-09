/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

@Named("propertyTypeController")
@SessionScoped
public class PropertyTypeController extends CdbEntityController<PropertyType, PropertyTypeFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyType.List.Display.Description";
    private static final String DisplayCategorySettingTypeKey = "PropertyType.List.Display.Category";
    private static final String DisplayDefaultUnitsSettingTypeKey = "PropertyType.List.Display.DefaultUnits";
    private static final String DisplayDefaultValueSettingTypeKey = "PropertyType.List.Display.DefaultValue";
    private static final String DisplayHandlerSettingTypeKey = "PropertyType.List.Display.Handler";
    private static final String FilterByNameSettingTypeKey = "PropertyType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyType.List.FilterBy.Description";
    private static final String FilterByCategorySettingTypeKey = "PropertyType.List.FilterBy.Category";
    private static final String FilterByDefaultUnitsSettingTypeKey = "PropertyType.List.FilterBy.DefaultUnits";
    private static final String FilterByDefaultValueSettingTypeKey = "PropertyType.List.FilterBy.DefaultValue";
    private static final String FilterByHandlerSettingTypeKey = "PropertyType.List.FilterBy.Handler";

    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());

    @EJB
    private PropertyTypeFacade propertyTypeFacade;

    private Boolean displayCategory = null;
    private Boolean displayDefaultUnits = null;
    private Boolean displayDefaultValue = null;
    private Boolean displayHandler = null;

    private String filterByCategory = null;
    private String filterByDefaultUnits = null;
    private String filterByDefaultValue = null;
    private String filterByHandler = null;

    private Boolean selectFilterViewDisplayCategory = null;
    private Boolean selectFilterViewDisplayHandler = null;
    private Boolean selectDisplayDefaultUnits = true;
    private Boolean selectDisplayDefaultValue = true;

    private String selectFilterByCategory = null;
    private String selectFilterByDefaultUnits = null;
    private String selectFilterByDefaultValue = null;
    private String selectFilterByHandler = null;

    private final Boolean FILTER_VIEW_IS_INTERNAL = false; 
    private List<PropertyTypeCategory> fitlerViewSelectedPropertyTypeCategories = null;
    private List<PropertyTypeHandler> fitlerViewSelectedPropertyTypeHandlers = null;

    private DataModel filterViewDataModel;

    public PropertyTypeController() {
        selectDisplayDescription = true;
    }

    @Override
    protected PropertyTypeFacade getEntityDbFacade() {
        return propertyTypeFacade;
    }

    @Override
    protected PropertyType createEntityInstance() {
        PropertyType propertyType = new PropertyType();
        propertyType.setIsInternal(false);
        propertyType.setIsActive(true);
        propertyType.setIsUserWriteable(false);
        propertyType.setIsDynamic(false);
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
    public PropertyType findById(Integer id) {
        return propertyTypeFacade.findById(id);
    }

    public PropertyType findByName(String name) {
        return propertyTypeFacade.findByName(name);
    }

    @Override
    public List<PropertyType> getAvailableItems() {
        return super.getAvailableItems();
    }
    
    public List<PropertyType> getAvailableExternalItems() { 
        return propertyTypeFacade.findByPropertyInternalStatus(false); 
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
        PropertyType existingPropertyType = propertyTypeFacade.findByName(propertyType.getName());
        if (existingPropertyType != null && !existingPropertyType.getId().equals(propertyType.getId())) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Updating property type " + propertyType.getName());
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
        displayHandler = Boolean.parseBoolean(settingTypeMap.get(DisplayHandlerSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
        filterByDefaultUnits = settingTypeMap.get(FilterByDefaultUnitsSettingTypeKey).getDefaultValue();
        filterByDefaultValue = settingTypeMap.get(FilterByDefaultValueSettingTypeKey).getDefaultValue();
        filterByHandler = settingTypeMap.get(FilterByHandlerSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = settingEntity.getSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        displayCategory = settingEntity.getSettingValueAsBoolean(DisplayCategorySettingTypeKey, displayCategory);
        displayDefaultUnits = settingEntity.getSettingValueAsBoolean(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        displayDefaultValue = settingEntity.getSettingValueAsBoolean(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        displayHandler = settingEntity.getSettingValueAsBoolean(DisplayHandlerSettingTypeKey, displayHandler);

        filterByName = settingEntity.getSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = settingEntity.getSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByCategory = settingEntity.getSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);
        filterByDefaultUnits = settingEntity.getSettingValueAsString(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        filterByDefaultValue = settingEntity.getSettingValueAsString(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        filterByHandler = settingEntity.getSettingValueAsString(FilterByHandlerSettingTypeKey, filterByHandler);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByCategory = (String) filters.get("propertyTypeCategory.name");
        filterByDefaultUnits = (String) filters.get("defaultUnits");
        filterByDefaultValue = (String) filters.get("defaultValue");
        filterByHandler = (String) filters.get("handlerName");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        settingEntity.setSettingValue(DisplayCategorySettingTypeKey, displayCategory);
        settingEntity.setSettingValue(DisplayDefaultUnitsSettingTypeKey, displayDefaultUnits);
        settingEntity.setSettingValue(DisplayDefaultValueSettingTypeKey, displayDefaultValue);
        settingEntity.setSettingValue(DisplayHandlerSettingTypeKey, displayHandler);

        settingEntity.setSettingValue(FilterByNameSettingTypeKey, filterByName);
        settingEntity.setSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        settingEntity.setSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
        settingEntity.setSettingValue(FilterByDefaultUnitsSettingTypeKey, filterByDefaultUnits);
        settingEntity.setSettingValue(FilterByDefaultValueSettingTypeKey, filterByDefaultValue);
        settingEntity.setSettingValue(FilterByHandlerSettingTypeKey, filterByHandler);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCategory = null;
        filterByDefaultUnits = null;
        filterByDefaultValue = null;
        filterByHandler = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByCategory = null;
        selectFilterByDefaultUnits = null;
        selectFilterByDefaultValue = null;
        selectFilterByHandler = null;
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
        allowedPropertyValueList.add(0, allowedPropertyValue);
    }

    public void saveAllowedPropertyValueList() {
        update();
    }

    public void deleteAllowedPropertyValue(AllowedPropertyValue allowedPropertyValue) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        allowedPropertyValueList.remove(allowedPropertyValue);
    }

    public void prepareSelectPropertyTypesForDomainEntity(CdbDomainEntity domainEntity) {
        clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
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

    public Boolean getDisplayHandler() {
        return displayHandler;
    }

    public void setDisplayHandler(Boolean displayHandler) {
        this.displayHandler = displayHandler;
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

    public String getFilterByHandler() {
        return filterByHandler;
    }

    public void setFilterByHandler(String filterByHandler) {
        this.filterByHandler = filterByHandler;
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

    public String getSelectFilterByHandler() {
        return selectFilterByHandler;
    }

    public void setSelectFilterByHandler(String selectFilterByHandler) {
        this.selectFilterByHandler = selectFilterByHandler;
    }

    public Boolean getSelectFilterViewDisplayCategory() {
        if (selectFilterViewDisplayCategory == null) {
            if (fitlerViewSelectedPropertyTypeCategories != null) {
                int size = fitlerViewSelectedPropertyTypeCategories.size();
                selectFilterViewDisplayCategory = size == 0 || size > 1;
            } else {
                selectFilterViewDisplayCategory = true;
            }
        }
        return selectFilterViewDisplayCategory;
    }

    public Boolean getSelectFilterViewDisplayHandler() {
        if (selectFilterViewDisplayHandler == null) {
            if (fitlerViewSelectedPropertyTypeHandlers != null) {
                int size = fitlerViewSelectedPropertyTypeHandlers.size();
                selectFilterViewDisplayHandler = size == 0 || size > 1;
            } else {
                selectFilterViewDisplayHandler = true;
            }
        }
        return selectFilterViewDisplayHandler;
    }

    public List<PropertyTypeCategory> getFitlerViewSelectedPropertyTypeCategories() {
        return fitlerViewSelectedPropertyTypeCategories;
    }

    public void setFitlerViewSelectedPropertyTypeCategories(List<PropertyTypeCategory> fitlerViewSelectedPropertyTypeCategories) {
        if (!Objects.equals(this.fitlerViewSelectedPropertyTypeCategories, fitlerViewSelectedPropertyTypeCategories)) {
            this.fitlerViewSelectedPropertyTypeCategories = fitlerViewSelectedPropertyTypeCategories;
            resetFilterViewDataModel();
        }
    }

    public List<PropertyTypeHandler> getFitlerViewSelectedPropertyTypeHandlers() {
        return fitlerViewSelectedPropertyTypeHandlers;
    }

    public void setFitlerViewSelectedPropertyTypeHandlers(List<PropertyTypeHandler> fitlerViewSelectedPropertyTypeHandlers) {
        if (!Objects.equals(this.fitlerViewSelectedPropertyTypeHandlers, fitlerViewSelectedPropertyTypeHandlers)) {
            this.fitlerViewSelectedPropertyTypeHandlers = fitlerViewSelectedPropertyTypeHandlers;
            resetFilterViewDataModel();
        }
    }

    public void resetFilterViewDataModel() {
        filterViewDataModel = null;
        selectFilterViewDisplayCategory = null;
        selectFilterViewDisplayHandler = null;
    }

    public DataModel getFilterViewDataModel() {
        if (filterViewDataModel == null) {
            List<PropertyType> results;
            results = propertyTypeFacade.findByFilterViewAttributes(
                    fitlerViewSelectedPropertyTypeCategories, 
                    fitlerViewSelectedPropertyTypeHandlers,
                    FILTER_VIEW_IS_INTERNAL);
            if (results != null) {
                filterViewDataModel = new ListDataModel(results);
            }
        }
        return filterViewDataModel;

    }

    /**
     * Converter class for property type objects.
     */
    @FacesConverter(forClass = PropertyType.class)
    public static class PropertyTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                PropertyTypeController controller = (PropertyTypeController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyTypeController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + "cannot be converted to property type object.");
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
            if (object instanceof PropertyType) {
                PropertyType o = (PropertyType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyType.class.getName());
            }
        }

    }
}
