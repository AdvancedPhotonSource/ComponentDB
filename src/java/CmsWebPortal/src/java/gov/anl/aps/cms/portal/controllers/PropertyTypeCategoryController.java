package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.PropertyTypeCategory;
import gov.anl.aps.cms.portal.model.beans.PropertyTypeCategoryFacade;
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

@Named("propertyTypeCategoryController")
@SessionScoped
public class PropertyTypeCategoryController extends CrudEntityController<PropertyTypeCategory, PropertyTypeCategoryFacade> implements Serializable
{
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyTypeCategory.List.Display.NumberOfItemsPerPage";

    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());

    @EJB
    private PropertyTypeCategoryFacade propertyTypeCategoryFacade;

    public PropertyTypeCategoryController() {
    }

    @Override
    protected PropertyTypeCategoryFacade getFacade() {
        return propertyTypeCategoryFacade;
    }

    @Override
    protected PropertyTypeCategory createEntityInstance() {
        PropertyTypeCategory propertyCategory = new PropertyTypeCategory();
        return propertyCategory;
    }

    @Override
    public String getEntityTypeName() {
        return "property type category";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<PropertyTypeCategory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(PropertyTypeCategory propertyTypeCategory) throws ObjectAlreadyExists {
        PropertyTypeCategory existingPropertyTypeCategory = propertyTypeCategoryFacade.findByName(propertyTypeCategory.getName());
        if (existingPropertyTypeCategory != null) {
            throw new ObjectAlreadyExists("Property type category " + propertyTypeCategory.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyTypeCategory.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyTypeCategory propertyCategory) throws ObjectAlreadyExists {
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

    @FacesConverter(forClass = PropertyTypeCategory.class)
    public static class PropertyCategoryControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PropertyTypeCategoryController controller = (PropertyTypeCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "propertyTypeCategoryController");
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
            if (object instanceof PropertyTypeCategory) {
                PropertyTypeCategory o = (PropertyTypeCategory) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyTypeCategory.class.getName());
            }
        }

    }

}
