package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.PropertyCategory;
import gov.anl.aps.cms.portal.model.beans.PropertyCategoryFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("propertyCategoryController")
@SessionScoped
public class PropertyCategoryController extends CrudEntityController<PropertyCategory, PropertyCategoryFacade> implements Serializable
{

    @EJB
    private PropertyCategoryFacade propertyCategoryFacade;
 
    public PropertyCategoryController() {
    }

    @Override
    protected PropertyCategoryFacade getFacade() {
        return propertyCategoryFacade;
    }

    @Override
    protected PropertyCategory createEntityInstance() {
        PropertyCategory propertyCategory = new PropertyCategory();
        return propertyCategory;
    }

    @Override
    public String getEntityTypeName() {
        return "property category";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<PropertyCategory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(PropertyCategory propertyCategory) throws ObjectAlreadyExists {
    }

    @Override
    public void prepareEntityUpdate(PropertyCategory propertyCategory) throws ObjectAlreadyExists {
    }

    @FacesConverter(forClass = PropertyCategory.class)
    public static class PropertyCategoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PropertyCategoryController controller = (PropertyCategoryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "propertyCategoryController");
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
            if (object instanceof PropertyCategory) {
                PropertyCategory o = (PropertyCategory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyCategory.class.getName());
            }
        }

    }

}
