package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import gov.anl.aps.cdb.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
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
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.selectonemenu.SelectOneMenu;

@Named("componentTypeController")
@SessionScoped
public class ComponentTypeController extends CrudEntityController<ComponentType, ComponentTypeFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "ComponentType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentType.List.Display.Description";
    private static final String DisplayCategorySettingTypeKey = "ComponentType.List.Display.Category";

    private static final String FilterByNameSettingTypeKey = "ComponentType.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "ComponentType.List.FilterBy.Description";
    private static final String FilterByCategorySettingTypeKey = "ComponentType.List.FilterBy.Category";

    private static final Logger logger = Logger.getLogger(ComponentTypeController.class.getName());

    @EJB
    private ComponentTypeFacade componentTypeFacade;

    private Boolean displayCategory = null;
    
    private Boolean selectDisplayCategory = true;
    
    private String filterByCategory = null;
    
    private String selectFilterByCategory = null;

    private ComponentType selectedComponentType = null;

    public ComponentTypeFacade getComponentTypeFacade() {
        return componentTypeFacade;
    }

    public void setComponentTypeFacade(ComponentTypeFacade componentTypeFacade) {
        this.componentTypeFacade = componentTypeFacade;
    }

    public ComponentTypeController() {
        super();
    }

    @Override
    protected ComponentTypeFacade getFacade() {
        return componentTypeFacade;
    }

    public ComponentType findById(Integer id) {
        return componentTypeFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            ComponentType componentType = findById(idViewParam);
            setCurrent(componentType);
            idViewParam = null;
        }
    }

    @Override
    protected ComponentType createEntityInstance() {
        ComponentType componentType = new ComponentType();
        return componentType;
    }

    @Override
    public String getEntityTypeName() {
        return "componentType";
    }

    @Override
    public String getEntityTypeCategoryName() {
        return "componentTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "component type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<ComponentType> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(ComponentType componentType) throws ObjectAlreadyExists {
        ComponentType existingComponentType = componentTypeFacade.findByName(componentType.getName());
        if (existingComponentType != null) {
            throw new ObjectAlreadyExists("Component type " + componentType.getName() + " already exists.");
        }
        logger.debug("Inserting new component type " + componentType.getName());
    }

    @Override
    public void prepareEntityUpdate(ComponentType componentType) throws ObjectAlreadyExists {
        ComponentType existingComponentType = componentTypeFacade.findByName(componentType.getName());
        if (existingComponentType != null && !existingComponentType.getId().equals(componentType.getId())) {
            throw new ObjectAlreadyExists("Component type " + componentType.getName() + " already exists.");
        }
        logger.debug("Updating component type " + componentType.getName());
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

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();

        filterByCategory = settingTypeMap.get(FilterByCategorySettingTypeKey).getDefaultValue();
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

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);

        filterByCategory = sessionUser.getUserSettingValueAsString(FilterByCategorySettingTypeKey, filterByCategory);

    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByCategory = filters.get("componentTypeCategory.name");
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

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);

        sessionUser.setUserSettingValue(FilterByCategorySettingTypeKey, filterByCategory);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByCategory = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByCategory = null;
    }

    @Override
    public boolean entityHasCategories() {
        return true;
    }

    public String getFilterByCategory() {
        return filterByCategory;
    }

    public void setFilterByCategory(String filterByCategory) {
        this.filterByCategory = filterByCategory;
    }

    public String getSelectFilterByCategory() {
        return selectFilterByCategory;
    }

    public void setSelectFilterByCategory(String selectFilterByCategory) {
        this.selectFilterByCategory = selectFilterByCategory;
    }

    public Boolean getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(Boolean displayCategory) {
        this.displayCategory = displayCategory;
    }

    public Boolean getSelectDisplayCategory() {
        return selectDisplayCategory;
    }

    public void setSelectDisplayCategory(Boolean selectDisplayCategory) {
        this.selectDisplayCategory = selectDisplayCategory;
    }

    public void savePropertyTypeList() {
        update();
    }

    public void deletePropertyType(PropertyType propertyType) {
        ComponentType componentType = getCurrent();
        List<PropertyType> propertyTypeList = componentType.getPropertyTypeList();
        propertyTypeList.remove(propertyType);
    }

    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        ComponentType componentType = getCurrent();
        List<PropertyType> componentTypePropertyTypeList = componentType.getPropertyTypeList();
        componentTypePropertyTypeList.addAll(propertyTypeList);
    }

    @FacesConverter(value = "componentTypeConverter", forClass = ComponentType.class)
    public static class ComponentTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            try {
                ComponentTypeController controller = (ComponentTypeController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentTypeController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component type object.");
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
        public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ComponentType) {
                ComponentType o = (ComponentType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentType.class.getName());
            }
        }

    }

    public ComponentType getSelectedComponentType() {
        return selectedComponentType;
    }

    public void setSelectedComponentType(ComponentType selectedComponentType) {
        this.selectedComponentType = selectedComponentType;
    }

}
