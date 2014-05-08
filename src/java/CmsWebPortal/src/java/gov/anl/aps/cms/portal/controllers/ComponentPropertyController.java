package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.model.entities.ComponentProperty;
import gov.anl.aps.cms.portal.model.beans.ComponentPropertyFacade;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.log4j.Logger;

@Named("componentPropertyController")
@SessionScoped
public class ComponentPropertyController extends CrudEntityController<ComponentProperty, ComponentPropertyFacade> implements Serializable
{

    @EJB
    private ComponentPropertyFacade componentPropertyFacade;
    private static final Logger logger = Logger.getLogger(ComponentPropertyController.class.getName());

    public ComponentPropertyController() {
    }

    @Override
    protected ComponentPropertyFacade getFacade() {
        return componentPropertyFacade;
    }

    @Override
    protected ComponentProperty createEntityInstance() {
        ComponentProperty componentProperty = new ComponentProperty();
        return componentProperty;
    }

    @Override
    public String getEntityTypeName() {
        return "componentProperty";
    }
    
    @Override
    public String getDisplayEntityTypeName() {
        return "component property";
    }
    
    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<ComponentProperty> getAvailableItems() {
        return super.getAvailableItems();
    }

    public DataModel getListDataModelByComponentId(Integer componentId) {
        return new ListDataModel(componentPropertyFacade.findAllByComponentId(componentId));
    }
    
    public List<ComponentProperty> findAllByComponentId(Integer componentId) {
        return componentPropertyFacade.findAllByComponentId(componentId);
    }

    @FacesConverter(forClass = ComponentProperty.class)
    public static class ComponentPropertyControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ComponentPropertyController controller = (ComponentPropertyController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "componentPropertyController");
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
            if (object instanceof ComponentProperty) {
                ComponentProperty o = (ComponentProperty) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentProperty.class.getName());
            }
        }

    }

}
