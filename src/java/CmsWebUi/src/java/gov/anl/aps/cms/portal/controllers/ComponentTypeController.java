package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.ComponentType;
import gov.anl.aps.cms.portal.model.beans.ComponentTypeFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("componentTypeController")
@SessionScoped
public class ComponentTypeController extends EntityController<ComponentType, ComponentTypeFacade> implements Serializable {

    @EJB
    private ComponentTypeFacade ejbFacade;
    
    public ComponentTypeController() {
        super();
    }

    @Override
    protected ComponentTypeFacade getFacade() {
        return ejbFacade;
    }
    
    @Override
    protected ComponentType createEntityInstance() {
        return new ComponentType();
    }
    
    @Override
    public String getEntityTypeName() {
        return "component type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getComponentTypeName();
        }
        return "";
    }

    @Override
    public List<ComponentType> getAvailableItems() {
        return super.getAvailableItems();
    }  
    
    @FacesConverter(forClass = ComponentType.class)
    public static class ComponentTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
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
                return getStringKey(o.getComponentTypeId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentType.class.getName());
            }
        }

    }

}
