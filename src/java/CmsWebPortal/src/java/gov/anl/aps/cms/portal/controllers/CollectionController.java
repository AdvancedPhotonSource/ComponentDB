package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.Collection;
import gov.anl.aps.cms.portal.model.beans.CollectionFacade;
import gov.anl.aps.cms.portal.model.beans.UserFacade;
import gov.anl.aps.cms.portal.model.entities.CollectionComponent;
import gov.anl.aps.cms.portal.model.entities.EntityInfo;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.model.entities.UserGroup;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("collectionController")
@SessionScoped
public class CollectionController extends CrudEntityController<Collection, CollectionFacade> implements Serializable
{

    private static final String CollectionDisplayNumberOfItemsPerPageSettingTypeKey = "Collection.List.Display.NumberOfItemsPerPage";
    private static final String CollectionDisplayIdSettingTypeKey = "Collection.List.Display.Id";
    private static final String CollectionDisplayParentCollectionSettingTypeKey = "Collection.List.Display.ParentCollection";
    private static final String CollectionDisplayOwnerUserSettingTypeKey = "Collection.List.Display.OwnerUser";
    private static final String CollectionDisplayOwnerGroupSettingTypeKey = "Collection.List.Display.OwnerGroup";
    private static final String CollectionDisplayCreatedByUserSettingTypeKey = "Collection.List.Display.CreatedByUser";
    private static final String CollectionDisplayCreatedOnDateTimeSettingTypeKey = "Collection.List.Display.CreatedOnDateTime";
    private static final String CollectionDisplayLastModifiedByUserSettingTypeKey = "Collection.List.Display.LastModifiedByUser";
    private static final String CollectionDisplayLastModifiedOnDateTimeSettingTypeKey = "Collection.List.Display.LastModifiedOnDateTime";

    private static final String CollectionComponentDisplayNumberOfItemsPerPageSettingTypeKey = "CollectionComponent.List.Display.NumberOfItemsPerPage";
    private static final String CollectionComponentDisplayIdSettingTypeKey = "CollectionComponent.List.Display.Id";
    private static final String CollectionComponentDisplayTagSettingTypeKey = "CollectionComponent.List.Display.Tag";
    private static final String CollectionComponentDisplayQuantitySettingTypeKey = "CollectionComponent.List.Display.Quantity";
    private static final String CollectionComponentDisplayPrioritySettingTypeKey = "CollectionComponent.List.Display.Priority";
    private static final String CollectionComponentDisplayDescriptionSettingTypeKey = "CollectionComponent.List.Display.Description";

    private static final String CollectionFilterByNameSettingTypeKey = "Collection.List.FilterBy.Name";
    private static final String CollectionFilterByParentCollectionSettingTypeKey = "Collection.List.FilterBy.ParentCollection";
    private static final String CollectionFilterByOwnerUserSettingTypeKey = "Collection.List.FilterBy.OwnerUser";
    private static final String CollectionFilterByOwnerGroupSettingTypeKey = "Collection.List.FilterBy.OwnerGroup";
    private static final String CollectionFilterByCreatedByUserSettingTypeKey = "Collection.List.FilterBy.CreatedByUser";
    private static final String CollectionFilterByCreatedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.CreatedOnDateTime";
    private static final String CollectionFilterByLastModifiedByUserSettingTypeKey = "Collection.List.FilterBy.LastModifiedByUser";
    private static final String CollectionFilterByLastModifiedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.LastModifiedOnDateTime";

    private static final Logger logger = Logger.getLogger(CollectionController.class.getName());

    @EJB
    private CollectionFacade collectionFacade;

    private Boolean displayParentCollection = null;
    private Integer collectionComponentDisplayNumberOfItemsPerPage = null;
    private Boolean collectionComponentDisplayId = null;
    private Boolean collectionComponentDisplayTag = null;
    private Boolean collectionComponentDisplayQuantity = null;
    private Boolean collectionComponentDisplayPriority = null;
    private Boolean collectionComponentDisplayDescription = null;

    private String filterByParentCollection = null;

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
        User ownerUser = (User) SessionUtility.getUser();
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
        User createdByUser = (User) SessionUtility.getUser();
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
        User lastModifiedByUser = (User) SessionUtility.getUser();
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

    @Override
    public void updateListSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(CollectionDisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayIdSettingTypeKey).getDefaultValue());
        displayParentCollection = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayParentCollectionSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(CollectionDisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(CollectionFilterByNameSettingTypeKey).getDefaultValue();
        filterByParentCollection = settingTypeMap.get(CollectionFilterByParentCollectionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(CollectionFilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(CollectionFilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(CollectionFilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(CollectionFilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(CollectionFilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(CollectionFilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateListSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(CollectionDisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayIdSettingTypeKey, displayId);
        displayParentCollection = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayParentCollectionSettingTypeKey, displayParentCollection);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(CollectionDisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        filterByName = sessionUser.getUserSettingValueAsString(CollectionFilterByNameSettingTypeKey, filterByName);
        filterByParentCollection = sessionUser.getUserSettingValueAsString(CollectionFilterByParentCollectionSettingTypeKey, filterByParentCollection);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(CollectionFilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(CollectionFilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(CollectionFilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(CollectionFilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(CollectionFilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(CollectionFilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        if (dataTable == null) {
            return;
        }

        Map<String, String> filters = dataTable.getFilters();
        filterByName = filters.get("name");
        filterByParentCollection = filters.get("parentCollection");
        filterByOwnerUser = filters.get("entityInfo.ownerUser.username");
        filterByOwnerGroup = filters.get("entityInfo.ownerUserGroup.name");
        filterByCreatedByUser = filters.get("entityInfo.createdByUser.username");
        filterByCreatedOnDateTime = filters.get("entityInfo.createdOnDateTime");
        filterByLastModifiedByUser = filters.get("entityInfo.lastModifiedByUser.username");
        filterByLastModifiedOnDateTime = filters.get("entityInfo.lastModifiedOnDateTime");
    }

    @Override
    public void clearListFilters() {
        filterByName = null;
        filterByParentCollection = null;
        filterByOwnerUser = null;
        filterByOwnerGroup = null;
        filterByCreatedByUser = null;
        filterByCreatedOnDateTime = null;
        filterByLastModifiedByUser = null;
        filterByLastModifiedOnDateTime = null;
    }

    @Override
    public void updateViewSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        collectionComponentDisplayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(CollectionComponentDisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        collectionComponentDisplayId = Boolean.parseBoolean(settingTypeMap.get(CollectionComponentDisplayIdSettingTypeKey).getDefaultValue());
        collectionComponentDisplayTag = Boolean.parseBoolean(settingTypeMap.get(CollectionComponentDisplayTagSettingTypeKey).getDefaultValue());
        collectionComponentDisplayQuantity = Boolean.parseBoolean(settingTypeMap.get(CollectionComponentDisplayQuantitySettingTypeKey).getDefaultValue());
        collectionComponentDisplayPriority = Boolean.parseBoolean(settingTypeMap.get(CollectionComponentDisplayPrioritySettingTypeKey).getDefaultValue());
        collectionComponentDisplayDescription = Boolean.parseBoolean(settingTypeMap.get(CollectionComponentDisplayDescriptionSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateViewSettingsFromSessionUser(User sessionUser) {
        collectionComponentDisplayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(CollectionComponentDisplayNumberOfItemsPerPageSettingTypeKey, collectionComponentDisplayNumberOfItemsPerPage);
        collectionComponentDisplayId = sessionUser.getUserSettingValueAsBoolean(CollectionComponentDisplayIdSettingTypeKey, collectionComponentDisplayId);
        collectionComponentDisplayTag = sessionUser.getUserSettingValueAsBoolean(CollectionComponentDisplayTagSettingTypeKey, collectionComponentDisplayTag);
        collectionComponentDisplayQuantity = sessionUser.getUserSettingValueAsBoolean(CollectionComponentDisplayQuantitySettingTypeKey, collectionComponentDisplayQuantity);
        collectionComponentDisplayPriority = sessionUser.getUserSettingValueAsBoolean(CollectionComponentDisplayPrioritySettingTypeKey, collectionComponentDisplayPriority);
        collectionComponentDisplayDescription = sessionUser.getUserSettingValueAsBoolean(CollectionComponentDisplayDescriptionSettingTypeKey, collectionComponentDisplayDescription);
    }

    public Boolean getDisplayParentCollection() {
        return displayParentCollection;
    }

    public void setDisplayParentCollection(Boolean displayParentCollection) {
        this.displayParentCollection = displayParentCollection;
    }

    public String getFilterByParentCollection() {
        return filterByParentCollection;
    }

    public Integer getCollectionComponentDisplayNumberOfItemsPerPage() {
        return collectionComponentDisplayNumberOfItemsPerPage;
    }

    public void setCollectionComponentDisplayNumberOfItemsPerPage(Integer collectionComponentDisplayNumberOfItemsPerPage) {
        this.collectionComponentDisplayNumberOfItemsPerPage = collectionComponentDisplayNumberOfItemsPerPage;
    }

    public Boolean isCollectionComponentDisplayId() {
        return collectionComponentDisplayId;
    }

    public void setCollectionComponentDisplayId(Boolean collectionComponentDisplayId) {
        this.collectionComponentDisplayId = collectionComponentDisplayId;
    }

    public Boolean isCollectionComponentDisplayTag() {
        return collectionComponentDisplayTag;
    }

    public void setCollectionComponentDisplayTag(Boolean collectionComponentDisplayTag) {
        this.collectionComponentDisplayTag = collectionComponentDisplayTag;
    }

    public Boolean isCollectionComponentDisplayQuantity() {
        return collectionComponentDisplayQuantity;
    }

    public void setCollectionComponentDisplayQuantity(Boolean collectionComponentDisplayQuantity) {
        this.collectionComponentDisplayQuantity = collectionComponentDisplayQuantity;
    }

    public Boolean isCollectionComponentDisplayPriority() {
        return collectionComponentDisplayPriority;
    }

    public void setCollectionComponentDisplayPriority(Boolean collectionComponentDisplayPriority) {
        this.collectionComponentDisplayPriority = collectionComponentDisplayPriority;
    }

    public Boolean isCollectionComponentDisplayDescription() {
        return collectionComponentDisplayDescription;
    }

    public void setCollectionComponentDisplayDescription(Boolean collectionComponentDisplayDescription) {
        this.collectionComponentDisplayDescription = collectionComponentDisplayDescription;
    }

    public void setFilterByParentCollection(String filterByParentCollection) {
        this.filterByParentCollection = filterByParentCollection;
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
