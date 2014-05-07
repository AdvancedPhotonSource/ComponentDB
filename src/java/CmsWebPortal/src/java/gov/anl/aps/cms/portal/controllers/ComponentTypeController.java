package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.ComponentType;
import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.beans.ComponentTypeFacade;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;

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

@Named("componentTypeController")
@SessionScoped
public class ComponentTypeController extends CrudEntityController<ComponentType, ComponentTypeFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentType.List.Display.NumberOfItemsPerPage";

    private static final Logger logger = Logger.getLogger(ComponentTypeController.class.getName());

    @EJB
    private ComponentTypeFacade componentTypeFacade;

    private String filterByTypeCategory = null;
    private String selectFilterByTypeCategory = null;

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

    @Override
    protected ComponentType createEntityInstance() {
        ComponentType componentType = new ComponentType();
        return componentType;
    }

    @Override
    public String getEntityTypeName() {
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
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByName = filters.get("name");
        filterByDescription = filters.get("description");
        filterByTypeCategory = filters.get("componentTypeCategory.name");
    }

    @Override
    public void clearListFilters() {
        filterByName = null;
        filterByDescription = null;
        filterByTypeCategory = null;
    }

    @Override
    public void clearSelectFilters() {
        super.clearSelectFilters();
        selectFilterByTypeCategory = null;
    }

    @Override
    public boolean entityHasCategories() {
        return true;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
    }

    public String getSelectFilterByTypeCategory() {
        return selectFilterByTypeCategory;
    }

    public void setSelectFilterByTypeCategory(String selectFilterByTypeCategory) {
        this.selectFilterByTypeCategory = selectFilterByTypeCategory;
    }

    @FacesConverter(value = "componentTypeConverter", forClass = ComponentType.class)
    public static class ComponentTypeControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
                return null;
            }
            ComponentTypeController controller = (ComponentTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentTypeController");
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
            if (object instanceof ComponentType) {
                ComponentType o = (ComponentType) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentType.class.getName());
            }
        }

    }

}
