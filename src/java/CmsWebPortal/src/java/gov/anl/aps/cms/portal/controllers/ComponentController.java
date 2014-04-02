package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.Component;
import gov.anl.aps.cms.portal.model.beans.ComponentFacade;
import gov.anl.aps.cms.portal.model.beans.ComponentStateFacade;
import gov.anl.aps.cms.portal.model.beans.UserFacade;
import gov.anl.aps.cms.portal.model.entities.ComponentProperty;
import gov.anl.aps.cms.portal.model.entities.ComponentSource;
import gov.anl.aps.cms.portal.model.entities.ComponentState;
import gov.anl.aps.cms.portal.model.entities.EntityInfo;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.model.entities.UserGroup;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("componentController")
@SessionScoped
public class ComponentController extends CrudEntityController<Component, ComponentFacade> implements Serializable
{

    private static final Logger logger = Logger.getLogger(ComponentController.class.getName());

    @EJB
    private ComponentFacade componentFacade;
    @EJB
    private UserFacade userFacade;
    @EJB
    private ComponentStateFacade componentStateFacade;
    private Integer componentIdViewParam = null;
            
    public ComponentController() {
        super();
    }

    @Override
    protected ComponentFacade getFacade() {
        return componentFacade;
    }

    @Override
    protected Component createEntityInstance() {
        Component component = new Component();
        Integer userId = SessionUtility.getUserId();
        List<ComponentState> componentStateList = componentStateFacade.findAll();
        if (!componentStateList.isEmpty()) {
            component.setComponentState(componentStateList.get(0));
        }
        EntityInfo entityInfo = new EntityInfo();
        User ownerUser = userFacade.find(userId);
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        component.setEntityInfo(entityInfo);
        return component;
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
    public List<Component> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(Component component) throws ObjectAlreadyExists {
        Component existingComponent = componentFacade.findByName(component.getName());
        if (existingComponent != null) {
            throw new ObjectAlreadyExists("Component " + component.getName() + " already exists.");
        }
        Integer userId = SessionUtility.getUserId();
        EntityInfo entityInfo = component.getEntityInfo();
        User createdByUser = userFacade.find(userId);
        Date createdOnDateTime = new Date();
        entityInfo.setCreatedOnDateTime(createdOnDateTime);
        entityInfo.setCreatedByUser(createdByUser);
        entityInfo.setLastModifiedOnDateTime(createdOnDateTime);
        entityInfo.setLastModifiedByUser(createdByUser);
        logger.debug("Inserting new component " + component.getName() + " (user: " + createdByUser.getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Component component) throws ObjectAlreadyExists {
        Integer userId = SessionUtility.getUserId();
        EntityInfo entityInfo = component.getEntityInfo();
        User lastModifiedByUser = userFacade.find(userId);
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
        logger.debug("Updating component " + component.getName() + " (user: " + lastModifiedByUser.getUsername() + ")");
    }
    
    public Component findById(Integer id) {
        return componentFacade.findById(id);
    }
    
    @Override
    public void selectByRequestParams() {
        if (componentIdViewParam != null) {
            Component component = findById(componentIdViewParam);
            setCurrent(component);
            componentIdViewParam = null;
        }
    }

    public Integer getComponentIdViewParam() {
        return componentIdViewParam;
    }

    public void setComponentIdViewParam(Integer componentIdViewParam) {
        this.componentIdViewParam = componentIdViewParam;
    }
    
    public void prepareAddProperty() {
        Component component = getCurrent();
        List<ComponentProperty> propertyList = component.getComponentPropertyList();
        ComponentProperty property = new ComponentProperty();
        property.setComponent(component);
        propertyList.add(property);
    }

    public void savePropertyList() {
        update();
    }

    public void deleteProperty(ComponentProperty property) {
        Component component = getCurrent();
        List<ComponentProperty> propertyList = component.getComponentPropertyList();
        propertyList.remove(property);
        update();
    }

    public void prepareAddSource() {
        Component component = getCurrent();
        List<ComponentSource> sourceList = component.getComponentSourceList();
        ComponentSource source = new ComponentSource();
        source.setComponent(component);
        sourceList.add(source);
    }

    public void saveSourceList() {
        update();
    }

    public void deleteSource(ComponentSource source) {
        Component component = getCurrent();
        List<ComponentSource> sourceList = component.getComponentSourceList();
        sourceList.remove(source);
        update();
    }
    
    @FacesConverter(forClass = Component.class)
    public static class ComponentControllerConverter implements Converter
    {

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
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Component.class.getName());
            }
        }

    }

}
