package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.ComponentState;
import gov.anl.aps.cms.portal.model.beans.ComponentStateFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("componentStateController")
@SessionScoped
public class ComponentStateController extends CrudEntityController<ComponentState, ComponentStateFacade> implements Serializable {

    @EJB
    private ComponentStateFacade ejbFacade;

    public ComponentStateController() {
        super();
    }

    @Override
    protected ComponentStateFacade getFacade() {
        return ejbFacade;
    }

    @Override
    protected ComponentState createEntityInstance() {
        return new ComponentState();
    }

    @Override
    public String getEntityTypeName() {
        return "component";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<ComponentState> getAvailableItems() {
        return super.getAvailableItems();
    }


    @FacesConverter(forClass = ComponentState.class)
    public static class ComponentStateControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentStateController controller = (ComponentStateController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentStateController");
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
            if (object instanceof ComponentState) {
                ComponentState o = (ComponentState) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentState.class.getName());
            }
        }

    }

}
