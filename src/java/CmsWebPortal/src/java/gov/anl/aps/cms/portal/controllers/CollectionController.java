package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.Collection;
import gov.anl.aps.cms.portal.model.beans.CollectionFacade;
import gov.anl.aps.cms.portal.model.entities.CollectionComponent;
import gov.anl.aps.cms.portal.model.entities.CollectionLink;
import gov.anl.aps.cms.portal.model.entities.Component;
import gov.anl.aps.cms.portal.model.entities.EntityInfo;
import gov.anl.aps.cms.portal.model.entities.Log;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.model.entities.UserGroup;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;

@Named("collectionController")
@SessionScoped
public class CollectionController extends CrudEntityController<Collection, CollectionFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Collection.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Collection.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "Collection.List.Display.Description";
    private static final String DisplayOwnerUserSettingTypeKey = "Collection.List.Display.OwnerUser";
    private static final String DisplayOwnerGroupSettingTypeKey = "Collection.List.Display.OwnerGroup";
    private static final String DisplayCreatedByUserSettingTypeKey = "Collection.List.Display.CreatedByUser";
    private static final String DisplayCreatedOnDateTimeSettingTypeKey = "Collection.List.Display.CreatedOnDateTime";
    private static final String DisplayLastModifiedByUserSettingTypeKey = "Collection.List.Display.LastModifiedByUser";
    private static final String DisplayLastModifiedOnDateTimeSettingTypeKey = "Collection.List.Display.LastModifiedOnDateTime";

    private static final String FilterByNameSettingTypeKey = "Collection.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Collection.List.FilterBy.Description";
    private static final String FilterByOwnerUserSettingTypeKey = "Collection.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Collection.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Collection.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Collection.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.LastModifiedOnDateTime";

    private static final Logger logger = Logger.getLogger(CollectionController.class.getName());

    private boolean selectChildCollections = false;

    @EJB
    private CollectionFacade collectionFacade;

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
    public Collection cloneEntityInstance(Collection collection) {
        Collection clonedCollection = super.cloneEntityInstance(collection);
        User ownerUser = (User) SessionUtility.getUser();
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(ownerUser);
        List<UserGroup> ownerUserGroupList = ownerUser.getUserGroupList();
        if (!ownerUserGroupList.isEmpty()) {
            entityInfo.setOwnerUserGroup(ownerUserGroupList.get(0));
        }
        clonedCollection.setEntityInfo(entityInfo);
        return clonedCollection;
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
        String logText = getLogText();
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = new Log();
            logEntry.setText(logText);
            logEntry.setCreatedByUser(createdByUser);
            logEntry.setCreatedOnDateTime(createdOnDateTime);
            List<Log> logList = new ArrayList<>();
            logList.add(logEntry);
            collection.setLogList(logList);
            resetLogText();
        }
        logger.debug("Inserting new collection " + collection.getName() + " (user: " + createdByUser.getUsername() + ")");
    }

    @Override
    public void prepareEntityUpdate(Collection collection) throws ObjectAlreadyExists {
        EntityInfo entityInfo = collection.getEntityInfo();
        User lastModifiedByUser = (User) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        entityInfo.setLastModifiedOnDateTime(lastModifiedOnDateTime);
        entityInfo.setLastModifiedByUser(lastModifiedByUser);
        String logText = getLogText();
        if (logText != null && !logText.isEmpty()) {
            Log logEntry = new Log();
            logEntry.setText(logText);
            logEntry.setCreatedByUser(lastModifiedByUser);
            logEntry.setCreatedOnDateTime(lastModifiedOnDateTime);
            collection.getLogList().add(logEntry);
            resetLogText();
        }
        logger.debug("Updating collection " + collection.getName() + " (user: " + lastModifiedByUser.getUsername() + ")");
    }

    public Collection findById(Integer id) {
        return collectionFacade.findById(id);
    }

    @Override
    public void selectByRequestParams() {
        if (idViewParam != null) {
            Collection collection = findById(idViewParam);
            setCurrent(collection);
            idViewParam = null;
        }
    }

    public void prepareAddComponent() {
        Collection collection = getCurrent();
        List<CollectionComponent> componentList = collection.getCollectionComponentList();
        CollectionComponent component = new CollectionComponent();
        component.setCollection(collection);
        componentList.add(component);
    }

    public void prepareSelectComponents() {
    }

    public void selectComponents(List<Component> componentList) {
        Collection collection = getCurrent();
        List<CollectionComponent> collectionComponentList = collection.getCollectionComponentList();
        for (Component component : componentList) {
            CollectionComponent collectionComponent = new CollectionComponent();
            collectionComponent.setCollection(collection);
            collectionComponent.setComponent(component);
            collectionComponentList.add(collectionComponent);
        }
    }

    public void saveComponentList() {
        update();
    }

    public void deleteComponent(CollectionComponent collectionComponent) {
        logger.debug("Removing component " + collectionComponent.getComponent().getName() + " from collection " + collectionComponent.getCollection().getName());
        Collection collection = getCurrent();
        List<CollectionComponent> collectionComponentList = collection.getCollectionComponentList();
        collectionComponentList.remove(collectionComponent);
    }

    public void deleteLog(Log collectionLog) {
        Collection collection = getCurrent();
        List<Log> collectionLogList = collection.getLogList();
        collectionLogList.remove(collectionLog);
    }

    public List<Log> getLogList() {
        Collection collection = getCurrent();
        List<Log> collectionLogList = collection.getLogList();
        User sessionUser = (User) SessionUtility.getUser();
        if (sessionUser != null) {
            Date settingsTimestamp = getSettingsTimestamp();
            if (settingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(settingsTimestamp)) {
                updateSettingsFromSessionUser(sessionUser);
                settingsTimestamp = new Date();
                setSettingsTimestamp(settingsTimestamp);
            }
        }
        return collectionLogList;
    }

    public void saveLogList() {
        update();
    }

    @Override
    public void prepareEntityListForSelection(List<Collection> selectEntityList) {
        // For now, prevent selecting current collection, or any children or parents.
        Collection currentCollection = getCurrent();
        selectEntityList.remove(currentCollection);
        if (selectChildCollections) {
            for (CollectionLink collectionLink : currentCollection.getParentCollectionLinkList()) {
                selectEntityList.remove(collectionLink.getParentCollection());
            }
        }
        else {
            for (CollectionLink collectionLink : currentCollection.getChildCollectionLinkList()) {
                selectEntityList.remove(collectionLink.getChildCollection());
            }
        }
    }

    public void selectCollections(List<Collection> collectionList) {
        if (selectChildCollections) {
            selectChildCollections(collectionList);
        }
        else {
            selectParentCollections(collectionList);
        }
    }

    public void prepareSelectChildCollectionsActionListener(ActionEvent actionEvent) {
        prepareSelectChildCollections();
    }

    public void prepareSelectChildCollections() {
        clearSelectFiltersAndResetSelectDataModel();
        selectChildCollections = true;
    }

    public void selectChildCollections(List<Collection> childCollectionList) {
        Collection collection = getCurrent();
        List<CollectionLink> childCollectionLinkList = collection.getChildCollectionLinkList();
        for (Collection childCollection : childCollectionList) {
            CollectionLink collectionLink = new CollectionLink();
            collectionLink.setParentCollection(collection);
            collectionLink.setChildCollection(childCollection);
            childCollectionLinkList.add(collectionLink);
        }
    }

    public void saveChildCollectionList() {
        update();
    }

    public void deleteChildCollectionLink(CollectionLink childCollectionLink) {
        Collection collection = getCurrent();
        List<CollectionLink> childCollectionLinkList = collection.getChildCollectionLinkList();
        childCollectionLinkList.remove(childCollectionLink);
    }

    public void prepareSelectParentCollectionsActionListener(ActionEvent actionEvent) {
        prepareSelectParentCollections();
    }

    public void prepareSelectParentCollections() {
        clearSelectFiltersAndResetSelectDataModel();
        selectChildCollections = false;
    }

    public void selectParentCollections(List<Collection> parentCollectionList) {
        Collection collection = getCurrent();
        List<CollectionLink> parentCollectionLinkList = collection.getParentCollectionLinkList();
        for (Collection parentCollection : parentCollectionList) {
            CollectionLink collectionLink = new CollectionLink();
            collectionLink.setParentCollection(parentCollection);
            collectionLink.setChildCollection(collection);
            parentCollectionLinkList.add(collectionLink);
        }
    }

    public void saveParentCollectionList() {
        update();
    }

    public void deleteParentCollectionLink(CollectionLink parentCollectionLink) {
        Collection collection = getCurrent();
        List<CollectionLink> parentCollectionLinkList = collection.getParentCollectionLinkList();
        parentCollectionLinkList.remove(parentCollectionLink);
    }

    public Double getCollectionComponentEstimatedCostSum(Collection collection) {
        return collection.getEstimatedComponentCostSum();
    }
 
    public Double getCollectionComponentEstimatedCostAverage(Collection collection) {
        return collection.getEstimatedComponentCostAverage();
    }
    
    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
        displayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerUserSettingTypeKey).getDefaultValue());
        displayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(DisplayOwnerGroupSettingTypeKey).getDefaultValue());
        displayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedByUserSettingTypeKey).getDefaultValue());
        displayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        displayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        displayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
        filterByOwnerUser = settingTypeMap.get(FilterByOwnerUserSettingTypeKey).getDefaultValue();
        filterByOwnerGroup = settingTypeMap.get(FilterByOwnerGroupSettingTypeKey).getDefaultValue();
        filterByCreatedByUser = settingTypeMap.get(FilterByCreatedByUserSettingTypeKey).getDefaultValue();
        filterByCreatedOnDateTime = settingTypeMap.get(FilterByCreatedOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLastModifiedByUser = settingTypeMap.get(FilterByLastModifiedByUserSettingTypeKey).getDefaultValue();
        filterByLastModifiedOnDateTime = settingTypeMap.get(FilterByLastModifiedOnDateTimeSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
        displayOwnerUser = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        displayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        displayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        displayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        displayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        displayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
        filterByOwnerUser = sessionUser.getUserSettingValueAsString(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        filterByOwnerGroup = sessionUser.getUserSettingValueAsString(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        filterByCreatedByUser = sessionUser.getUserSettingValueAsString(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        filterByCreatedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        filterByLastModifiedByUser = sessionUser.getUserSettingValueAsString(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        filterByLastModifiedOnDateTime = sessionUser.getUserSettingValueAsString(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);

    }

    @Override
    public void saveSettingsForSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
        sessionUser.setUserSettingValue(DisplayOwnerUserSettingTypeKey, displayOwnerUser);
        sessionUser.setUserSettingValue(DisplayOwnerGroupSettingTypeKey, displayOwnerGroup);
        sessionUser.setUserSettingValue(DisplayCreatedByUserSettingTypeKey, displayCreatedByUser);
        sessionUser.setUserSettingValue(DisplayCreatedOnDateTimeSettingTypeKey, displayCreatedOnDateTime);
        sessionUser.setUserSettingValue(DisplayLastModifiedByUserSettingTypeKey, displayLastModifiedByUser);
        sessionUser.setUserSettingValue(DisplayLastModifiedOnDateTimeSettingTypeKey, displayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);
    }

    public boolean isSelectChildCollections() {
        return selectChildCollections;
    }

    public void setSelectChildCollections(boolean selectChildCollections) {
        this.selectChildCollections = selectChildCollections;
    }

    @FacesConverter(value = "collectionConverter", forClass = Collection.class)
    public static class CollectionControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0 || value.equals("Select")) {
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
