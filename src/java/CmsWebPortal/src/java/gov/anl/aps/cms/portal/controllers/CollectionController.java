package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.Collection;
import gov.anl.aps.cms.portal.model.beans.CollectionFacade;
import gov.anl.aps.cms.portal.model.beans.UserFacade;
import gov.anl.aps.cms.portal.model.entities.CollectionComponent;
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

@Named("collectionController")
@SessionScoped
public class CollectionController extends CrudEntityController<Collection, CollectionFacade> implements Serializable
{

    @EJB
    private CollectionFacade collectionFacade;
    @EJB
    private UserFacade userFacade;
    private static final Logger logger = Logger.getLogger(CollectionController.class.getName());

    public CollectionController() {
    }

    @Override
    protected CollectionFacade getFacade() {
        return collectionFacade;
    }

    @Override
    protected Collection createEntityInstance() {
        Collection collection = new Collection();
        EntityInfo entityInfo = new EntityInfo();
        User ownerUser = (User)SessionUtility.getUser();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        collection.setEntityInfo(entityInfo);
        return collection;
    }

    @Override
    public String getEntityTypeName() {
        return "collection";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<Collection> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(Collection collection) throws ObjectAlreadyExists {
        Collection existingComponent = collectionFacade.findByName(collection.getName());
        if (existingComponent != null) {
            throw new ObjectAlreadyExists("Collection " + collection.getName() + " already exists.");
        }
        EntityInfo entityInfo = collection.getEntityInfo();
        User createdByUser = (User)SessionUtility.getUser();
        Date createdOnDateTime = new Date();
        entityInfo.setCreatedOnDateTime(createdOnDateTime);
        entityInfo.setCreatedByUser(createdByUser);
        entityInfo.setLastModifiedOnDateTime(createdOnDateTime);
        entityInfo.setLastModifiedByUser(createdByUser);
        logger.debug("Inserting new collection " + collection.getName() + " (user: " + createdByUser.getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Collection collection) throws ObjectAlreadyExists {
        EntityInfo entityInfo = collection.getEntityInfo();
        User lastModifiedByUser = (User)SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
        logger.debug("Updating collection " + collection.getName() + " (user: " + lastModifiedByUser.getUsername() + ")");
    }

    public void prepareAddComponent() {
        Collection collection = getCurrent();
        List<CollectionComponent> componentList = collection.getCollectionComponentList();
        CollectionComponent component = new CollectionComponent();
        component.setCollection(collection);
        componentList.add(component);
    }

    public void saveComponentList() {
        update();
    }

    public void deleteComponent(CollectionComponent component) {
        Collection collection = getCurrent();
        List<CollectionComponent> componentList = collection.getCollectionComponentList();
        componentList.remove(component);
        update();
    }
    
    @FacesConverter(value = "collectionConverter", forClass = Collection.class)
    public static class CollectionControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CollectionController controller = (CollectionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "collectionController");
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
            if (object instanceof Collection) {
                Collection o = (Collection) object;
                return getStringKey(o.getId());
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Collection.class.getName());
            }
        }

    }

}
