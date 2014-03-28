package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.PropertyType;
import gov.anl.aps.cms.portal.model.beans.PropertyTypeFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("propertyTypeController")
@SessionScoped
public class PropertyTypeController extends CrudEntityController<PropertyType, PropertyTypeFacade> implements Serializable
{

    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());

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
    }

    @Override
    public void prepareEntityUpdate(PropertyType propertyType) throws ObjectAlreadyExists {
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

}
