package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.ComponentTypeCategory;
import gov.anl.aps.cms.portal.model.beans.ComponentTypeCategoryFacade;
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

@Named("componentTypeCategoryController")
@SessionScoped
public class ComponentTypeCategoryController extends CrudEntityController<ComponentTypeCategory, ComponentTypeCategoryFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentTypeCategory.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "ComponentTypeCategory.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "ComponentTypeCategory.List.Display.Description";

    private static final Logger logger = Logger.getLogger(ComponentTypeController.class.getName());

    @EJB
    private ComponentTypeCategoryFacade componentTypeCategoryFacade;

    public ComponentTypeCategoryController() {
    }

    @Override
    protected ComponentTypeCategoryFacade getFacade() {
        return componentTypeCategoryFacade;
    }

    public ComponentTypeCategory findById(Integer id) {
        return componentTypeCategoryFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            ComponentTypeCategory componentTypeCategory = findById(idViewParam);
            setCurrent(componentTypeCategory);
            idViewParam = null;
        }
    }

    @Override
    protected ComponentTypeCategory createEntityInstance() {
        ComponentTypeCategory componentCategory = new ComponentTypeCategory();
        return componentCategory;
    }

    @Override
    public String getEntityTypeName() {
        return "componentTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "component type category";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<ComponentTypeCategory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(ComponentTypeCategory componentTypeCategory) throws ObjectAlreadyExists {
        ComponentTypeCategory existingComponentTypeCategory = componentTypeCategoryFacade.findByName(componentTypeCategory.getName());
        if (existingComponentTypeCategory != null) {
            throw new ObjectAlreadyExists("Component type category " + componentTypeCategory.getName() + " already exists.");
        }
        logger.debug("Inserting new component type category " + componentTypeCategory.getName());
    }

    @Override
    public void prepareEntityUpdate(ComponentTypeCategory componentCategory) throws ObjectAlreadyExists {
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
    }

    @Override
    public void saveSettingsForSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
    }

    @FacesConverter(forClass = ComponentTypeCategory.class)
    public static class ComponentCategoryControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentTypeCategoryController controller = (ComponentTypeCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentTypeCategoryController");
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
            if (object instanceof ComponentTypeCategory) {
                ComponentTypeCategory o = (ComponentTypeCategory) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentTypeCategory.class.getName());
            }
        }

    }

}
