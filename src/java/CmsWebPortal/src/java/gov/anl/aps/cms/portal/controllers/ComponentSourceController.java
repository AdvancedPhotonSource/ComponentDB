package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.ComponentSource;
import gov.anl.aps.cms.portal.model.beans.ComponentSourceFacade;

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

@Named("componentSourceController")
@SessionScoped
public class ComponentSourceController extends CrudEntityController<ComponentSource, ComponentSourceFacade> implements Serializable
{

    @EJB
    private ComponentSourceFacade componentSourceFacade;
    private static final Logger logger = Logger.getLogger(ComponentSourceController.class.getName());

    public ComponentSourceController() {
    }

    @Override
    protected ComponentSourceFacade getFacade() {
        return componentSourceFacade;
    }

    @Override
    protected ComponentSource createEntityInstance() {
        ComponentSource componentSource = new ComponentSource();
        return componentSource;
    }

    @Override
    public String getEntityTypeName() {
        return "component source";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<ComponentSource> getAvailableItems() {
        return super.getAvailableItems();
    }

    public List<ComponentSource> findAllByComponentId(Integer componentId) {
        return componentSourceFacade.findAllByComponentId(componentId);
    }
    
    @FacesConverter(forClass = ComponentSource.class)
    public static class ComponentSourceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentSourceController controller = (ComponentSourceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentSourceController");
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
            if (object instanceof ComponentSource) {
                ComponentSource o = (ComponentSource) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentSource.class.getName());
            }
        }

    }

}
