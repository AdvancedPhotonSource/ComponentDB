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

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Collection.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Collection.List.Display.Id";
    private static final String DisplayParentCollectionSettingTypeKey = "Collection.List.Display.ParentCollection";
    private static final String DisplayOwnerUserSettingTypeKey = "Collection.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Collection.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "Collection.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Collection.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Collection.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Collection.List.Display.LastModifiedOnDateTime";

    private static final String FilterByNameSettingTypeKey = "Collection.List.FilterBy.Name";
    private static final String FilterByParentCollectionSettingTypeKey = "Collection.List.FilterBy.ParentCollection";
    private static final String FilterByOwnerUserSettingTypeKey = "Collection.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Collection.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Collection.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Collection.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.LastModifiedOnDateTime";

    private static final Logger logger = Logger.getLogger(CollectionController.class.getName());

    @EJB
    private CollectionFacade collectionFacade;

    private Boolean displayParentCollection = null;
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

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayParentCollection = Boolean.parseBoolean(settingTypeMap.get(DisplayParentCollectionSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByParentCollection = settingTypeMap.get(FilterByParentCollectionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateListSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayParentCollection = sessionUser.getUserSettingValueAsBoolean(DisplayParentCollectionSettingTypeKey, displayParentCollection);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByParentCollection = sessionUser.getUserSettingValueAsString(FilterByParentCollectionSettingTypeKey, filterByParentCollection);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);
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

    public Boolean getDisplayParentCollection() {
        return displayParentCollection;
    }

    public void setDisplayParentCollection(Boolean displayParentCollection) {
        this.displayParentCollection = displayParentCollection;
    }

    public String getFilterByParentCollection() {
        return filterByParentCollection;
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
