package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.ComponentType;
import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
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
import org.apache.log4j.Logger;

@Named("componentTypeController")
@SessionScoped
public class ComponentTypeController extends CrudEntityController<ComponentType, ComponentTypeFacade> implements Serializable
{

    @EJB
    private ComponentTypeFacade componentTypeFacade;
    private static final Logger logger = Logger.getLogger(ComponentTypeController.class.getName());

    public ComponentTypeController() {
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
    }

    @Override
    public void prepareEntityUpdate(ComponentType componentType) throws ObjectAlreadyExists {
    }

    @FacesConverter(value = "componentTypeConverter", forClass = ComponentType.class)
    public static class ComponentTypeControllerConverter implements Converter
    {

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
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentType.class.getName());
            }
        }

    }

}
