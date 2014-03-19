package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.Component;
import gov.anl.aps.cms.portal.model.beans.ComponentFacade;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("componentController")
@SessionScoped
public class ComponentController extends EntityController<Component, ComponentFacade> implements Serializable {

    @EJB
    private ComponentFacade ejbFacade;

    public ComponentController() {
        super();
    }

    @Override
    protected ComponentFacade getFacade() {
        return ejbFacade;
    }

    @Override
    protected Component createEntityInstance() {
        return new Component();
    }

    @Override
    public String getEntityTypeName() {
        return "component type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getComponentInstanceName();
        }
        return "";
    }

    @FacesConverter(forClass = Component.class)
    public static class ComponentControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentController controller = (ComponentController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentController");
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
            if (object instanceof Component) {
                Component o = (Component) object;
                return getStringKey(o.getComponentId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Component.class.getName());
            }
        }

    }

}
