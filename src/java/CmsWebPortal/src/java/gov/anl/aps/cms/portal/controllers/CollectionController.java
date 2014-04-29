package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cms.portal.model.entities.Collection;
import gov.anl.aps.cms.portal.model.beans.CollectionFacade;
import gov.anl.aps.cms.portal.model.entities.CollectionComponent;
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

    private static final String ViewChildCollectionListDisplayNumberOfItemsPerPageSettingTypeKey = "Collection.View.ChildCollectionList.Display.NumberOfItemsPerPage";
    private static final String ViewChildCollectionListDisplayIdSettingTypeKey = "Collection.View.ChildCollectionList.Display.Id";
    private static final String ViewChildCollectionListDisplayDescriptionSettingTypeKey = "Collection.View.ChildCollectionList.Display.Description";
    private static final String ViewChildCollectionListDisplayOwnerUserSettingTypeKey = "Collection.View.ChildCollectionList.Display.OwnerUser";
    private static final String ViewChildCollectionListDisplayOwnerGroupSettingTypeKey = "Collection.View.ChildCollectionList.Display.OwnerGroup";
    private static final String ViewChildCollectionListDisplayCreatedByUserSettingTypeKey = "Collection.View.ChildCollectionList.Display.CreatedByUser";
    private static final String ViewChildCollectionListDisplayCreatedOnDateTimeSettingTypeKey = "Collection.View.ChildCollectionList.Display.CreatedOnDateTime";
    private static final String ViewChildCollectionListDisplayLastModifiedByUserSettingTypeKey = "Collection.View.ChildCollectionList.Display.LastModifiedByUser";
    private static final String ViewChildCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey = "Collection.View.ChildCollectionList.Display.LastModifiedOnDateTime";

    private static final String ViewParentCollectionListDisplayNumberOfItemsPerPageSettingTypeKey = "Collection.View.ParentCollectionList.Display.NumberOfItemsPerPage";
    private static final String ViewParentCollectionListDisplayIdSettingTypeKey = "Collection.View.ParentCollectionList.Display.Id";
    private static final String ViewParentCollectionListDisplayDescriptionSettingTypeKey = "Collection.View.ParentCollectionList.Display.Description";
    private static final String ViewParentCollectionListDisplayOwnerUserSettingTypeKey = "Collection.View.ParentCollectionList.Display.OwnerUser";
    private static final String ViewParentCollectionListDisplayOwnerGroupSettingTypeKey = "Collection.View.ParentCollectionList.Display.OwnerGroup";
    private static final String ViewParentCollectionListDisplayCreatedByUserSettingTypeKey = "Collection.View.ParentCollectionList.Display.CreatedByUser";
    private static final String ViewParentCollectionListDisplayCreatedOnDateTimeSettingTypeKey = "Collection.View.ParentCollectionList.Display.CreatedOnDateTime";
    private static final String ViewParentCollectionListDisplayLastModifiedByUserSettingTypeKey = "Collection.View.ParentCollectionList.Display.LastModifiedByUser";
    private static final String ViewParentCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey = "Collection.View.ParentCollectionList.Display.LastModifiedOnDateTime";

    private static final String ViewCollectionLogListDisplayNumberOfItemsPerPageSettingTypeKey = "Collection.View.CollectionLogList.Display.NumberOfItemsPerPage";
    private static final String ViewCollectionLogListDisplayIdSettingTypeKey = "Collection.View.CollectionLogList.Display.Id";
    private static final String ViewCollectionLogListDisplayCreatedByUserSettingTypeKey = "Collection.View.CollectionLogList.Display.CreatedByUser";
    private static final String ViewCollectionLogListDisplayCreatedOnDateTimeSettingTypeKey = "Collection.View.CollectionLogList.Display.CreatedOnDateTime";
    
    private static final String FilterByNameSettingTypeKey = "Collection.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "Collection.List.FilterBy.Description";
    private static final String FilterByOwnerUserSettingTypeKey = "Collection.List.FilterBy.OwnerUser";
    private static final String FilterByOwnerGroupSettingTypeKey = "Collection.List.FilterBy.OwnerGroup";
    private static final String FilterByCreatedByUserSettingTypeKey = "Collection.List.FilterBy.CreatedByUser";
    private static final String FilterByCreatedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.CreatedOnDateTime";
    private static final String FilterByLastModifiedByUserSettingTypeKey = "Collection.List.FilterBy.LastModifiedByUser";
    private static final String FilterByLastModifiedOnDateTimeSettingTypeKey = "Collection.List.FilterBy.LastModifiedOnDateTime";

    private static final Logger logger = Logger.getLogger(CollectionController.class.getName());

    protected Integer viewChildCollectionListDisplayNumberOfItemsPerPage = null;
    protected Boolean viewChildCollectionListDisplayId = null;
    protected Boolean viewChildCollectionListDisplayDescription = null;
    protected Boolean viewChildCollectionListDisplayOwnerUser = null;
    protected Boolean viewChildCollectionListDisplayOwnerGroup = null;
    protected Boolean viewChildCollectionListDisplayCreatedByUser = null;
    protected Boolean viewChildCollectionListDisplayCreatedOnDateTime = null;
    protected Boolean viewChildCollectionListDisplayLastModifiedByUser = null;
    protected Boolean viewChildCollectionListDisplayLastModifiedOnDateTime = null;

    protected Integer viewParentCollectionListDisplayNumberOfItemsPerPage = null;
    protected Boolean viewParentCollectionListDisplayId = null;
    protected Boolean viewParentCollectionListDisplayDescription = null;
    protected Boolean viewParentCollectionListDisplayOwnerUser = null;
    protected Boolean viewParentCollectionListDisplayOwnerGroup = null;
    protected Boolean viewParentCollectionListDisplayCreatedByUser = null;
    protected Boolean viewParentCollectionListDisplayCreatedOnDateTime = null;
    protected Boolean viewParentCollectionListDisplayLastModifiedByUser = null;
    protected Boolean viewParentCollectionListDisplayLastModifiedOnDateTime = null;

    protected Integer viewCollectionLogListDisplayNumberOfItemsPerPage = null;
    protected Boolean viewCollectionLogListDisplayId = null;
    protected Boolean viewCollectionLogListDisplayCreatedByUser = null;
    protected Boolean viewCollectionLogListDisplayCreatedOnDateTime = null;    
    
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
                logger.debug("Updating list settings from session user");
                updateViewSettingsFromSessionUser(sessionUser);
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
        for (Collection collection : currentCollection.getChildCollectionList()) {
            selectEntityList.remove(collection);
        }
        for (Collection collection : currentCollection.getParentCollectionList()) {
            selectEntityList.remove(collection);
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
    
    public void prepareSelectChildCollections() {
        clearSelectFiltersAndResetSelectDataModel();
        selectChildCollections = true;
    }

    public void selectChildCollections(List<Collection> collectionList) {
        Collection collection = getCurrent();
        List<Collection> childCollectionList = collection.getChildCollectionList();
        childCollectionList.addAll(collectionList);
    }

    public void saveChildCollectionList() {
        update();
    }

    public void deleteChildCollection(Collection childCollection) {
        Collection collection = getCurrent();
        List<Collection> childCollectionList = collection.getChildCollectionList();
        childCollectionList.remove(childCollection);
        update();
    }

    public void prepareSelectParentCollections() {
        clearSelectFiltersAndResetSelectDataModel();
        selectChildCollections = false;
    }

    public void selectParentCollections(List<Collection> collectionList) {
        Collection collection = getCurrent();
        List<Collection> parentCollectionList = collection.getParentCollectionList();
        parentCollectionList.addAll(collectionList);
    }

    public void saveParentCollectionList() {
        update();
    }

    public void deleteParentCollection(Collection parentCollection) {
        Collection collection = getCurrent();
        List<Collection> parentCollectionList = collection.getParentCollectionList();
        parentCollectionList.remove(parentCollection);
        update();
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

        viewChildCollectionListDisplayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(ViewChildCollectionListDisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayId = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayIdSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayDescription = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayDescriptionSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayOwnerUserSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayOwnerGroupSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayCreatedByUserSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        viewChildCollectionListDisplayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(ViewChildCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        viewParentCollectionListDisplayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(ViewParentCollectionListDisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayId = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayIdSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayDescription = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayDescriptionSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayOwnerUser = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayOwnerUserSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayOwnerGroup = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayOwnerGroupSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayCreatedByUserSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayLastModifiedByUser = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayLastModifiedByUserSettingTypeKey).getDefaultValue());
        viewParentCollectionListDisplayLastModifiedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(ViewParentCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey).getDefaultValue());

        viewCollectionLogListDisplayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(ViewCollectionLogListDisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        viewCollectionLogListDisplayId = Boolean.parseBoolean(settingTypeMap.get(ViewCollectionLogListDisplayIdSettingTypeKey).getDefaultValue());
        viewCollectionLogListDisplayCreatedByUser = Boolean.parseBoolean(settingTypeMap.get(ViewCollectionLogListDisplayCreatedByUserSettingTypeKey).getDefaultValue());
        viewCollectionLogListDisplayCreatedOnDateTime = Boolean.parseBoolean(settingTypeMap.get(ViewCollectionLogListDisplayCreatedOnDateTimeSettingTypeKey).getDefaultValue());
        
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
    public void updateListSettingsFromSessionUser(User sessionUser) {
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

        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayNumberOfItemsPerPageSettingTypeKey, viewChildCollectionListDisplayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayIdSettingTypeKey, viewChildCollectionListDisplayId);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayDescriptionSettingTypeKey, viewChildCollectionListDisplayDescription);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayOwnerUserSettingTypeKey, viewChildCollectionListDisplayOwnerUser);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayOwnerGroupSettingTypeKey, viewChildCollectionListDisplayOwnerGroup);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayCreatedByUserSettingTypeKey, viewChildCollectionListDisplayCreatedByUser);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayCreatedOnDateTimeSettingTypeKey, viewChildCollectionListDisplayCreatedOnDateTime);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayLastModifiedByUserSettingTypeKey, viewChildCollectionListDisplayLastModifiedByUser);
        sessionUser.setUserSettingValue(ViewChildCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey, viewChildCollectionListDisplayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayNumberOfItemsPerPageSettingTypeKey, viewParentCollectionListDisplayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayIdSettingTypeKey, viewParentCollectionListDisplayId);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayDescriptionSettingTypeKey, viewParentCollectionListDisplayDescription);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayOwnerUserSettingTypeKey, viewParentCollectionListDisplayOwnerUser);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayOwnerGroupSettingTypeKey, viewParentCollectionListDisplayOwnerGroup);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayCreatedByUserSettingTypeKey, viewParentCollectionListDisplayCreatedByUser);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayCreatedOnDateTimeSettingTypeKey, viewParentCollectionListDisplayCreatedOnDateTime);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayLastModifiedByUserSettingTypeKey, viewParentCollectionListDisplayLastModifiedByUser);
        sessionUser.setUserSettingValue(ViewParentCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey, viewParentCollectionListDisplayLastModifiedOnDateTime);

        sessionUser.setUserSettingValue(ViewCollectionLogListDisplayNumberOfItemsPerPageSettingTypeKey, viewCollectionLogListDisplayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(ViewCollectionLogListDisplayIdSettingTypeKey, viewCollectionLogListDisplayId);
        sessionUser.setUserSettingValue(ViewCollectionLogListDisplayCreatedByUserSettingTypeKey, viewCollectionLogListDisplayCreatedByUser);
        sessionUser.setUserSettingValue(ViewCollectionLogListDisplayCreatedOnDateTimeSettingTypeKey, viewCollectionLogListDisplayCreatedOnDateTime);
        
        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
        sessionUser.setUserSettingValue(FilterByOwnerUserSettingTypeKey, filterByOwnerUser);
        sessionUser.setUserSettingValue(FilterByOwnerGroupSettingTypeKey, filterByOwnerGroup);
        sessionUser.setUserSettingValue(FilterByCreatedByUserSettingTypeKey, filterByCreatedByUser);
        sessionUser.setUserSettingValue(FilterByCreatedOnDateTimeSettingTypeKey, filterByCreatedOnDateTime);
        sessionUser.setUserSettingValue(FilterByLastModifiedByUserSettingTypeKey, filterByLastModifiedByUser);
        sessionUser.setUserSettingValue(FilterByLastModifiedOnDateTimeSettingTypeKey, filterByLastModifiedByUser);
    }

    @Override
    public void updateViewSettingsFromSessionUser(User sessionUser) {
        if (sessionUser == null) {
            return;
        }

        viewChildCollectionListDisplayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(ViewChildCollectionListDisplayNumberOfItemsPerPageSettingTypeKey, viewChildCollectionListDisplayNumberOfItemsPerPage);
        viewChildCollectionListDisplayId = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayIdSettingTypeKey, viewChildCollectionListDisplayId);
        viewChildCollectionListDisplayDescription = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayDescriptionSettingTypeKey, viewChildCollectionListDisplayDescription);
        viewChildCollectionListDisplayOwnerUser = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayOwnerUserSettingTypeKey, viewChildCollectionListDisplayOwnerUser);
        viewChildCollectionListDisplayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayOwnerGroupSettingTypeKey, viewChildCollectionListDisplayOwnerGroup);
        viewChildCollectionListDisplayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayCreatedByUserSettingTypeKey, viewChildCollectionListDisplayCreatedByUser);
        viewChildCollectionListDisplayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayCreatedOnDateTimeSettingTypeKey, viewChildCollectionListDisplayCreatedOnDateTime);
        viewChildCollectionListDisplayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayLastModifiedByUserSettingTypeKey, viewChildCollectionListDisplayLastModifiedByUser);
        viewChildCollectionListDisplayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(ViewChildCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey, viewChildCollectionListDisplayLastModifiedOnDateTime);

        viewParentCollectionListDisplayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(ViewParentCollectionListDisplayNumberOfItemsPerPageSettingTypeKey, viewParentCollectionListDisplayNumberOfItemsPerPage);
        viewParentCollectionListDisplayId = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayIdSettingTypeKey, viewParentCollectionListDisplayId);
        viewParentCollectionListDisplayDescription = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayDescriptionSettingTypeKey, viewParentCollectionListDisplayDescription);
        viewParentCollectionListDisplayOwnerUser = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayOwnerUserSettingTypeKey, viewParentCollectionListDisplayOwnerUser);
        viewParentCollectionListDisplayOwnerGroup = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayOwnerGroupSettingTypeKey, viewParentCollectionListDisplayOwnerGroup);
        viewParentCollectionListDisplayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayCreatedByUserSettingTypeKey, viewParentCollectionListDisplayCreatedByUser);
        viewParentCollectionListDisplayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayCreatedOnDateTimeSettingTypeKey, viewParentCollectionListDisplayCreatedOnDateTime);
        viewParentCollectionListDisplayLastModifiedByUser = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayLastModifiedByUserSettingTypeKey, viewParentCollectionListDisplayLastModifiedByUser);
        viewParentCollectionListDisplayLastModifiedOnDateTime = sessionUser.getUserSettingValueAsBoolean(ViewParentCollectionListDisplayLastModifiedOnDateTimeSettingTypeKey, viewParentCollectionListDisplayLastModifiedOnDateTime);

        viewCollectionLogListDisplayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(ViewCollectionLogListDisplayNumberOfItemsPerPageSettingTypeKey, viewCollectionLogListDisplayNumberOfItemsPerPage);
        viewCollectionLogListDisplayId = sessionUser.getUserSettingValueAsBoolean(ViewCollectionLogListDisplayIdSettingTypeKey, viewCollectionLogListDisplayId);
        viewCollectionLogListDisplayCreatedByUser = sessionUser.getUserSettingValueAsBoolean(ViewCollectionLogListDisplayCreatedByUserSettingTypeKey, viewCollectionLogListDisplayCreatedByUser);
        viewCollectionLogListDisplayCreatedOnDateTime = sessionUser.getUserSettingValueAsBoolean(ViewCollectionLogListDisplayCreatedOnDateTimeSettingTypeKey, viewCollectionLogListDisplayCreatedOnDateTime);        
    }

    public Integer getViewChildCollectionListDisplayNumberOfItemsPerPage() {
        return viewChildCollectionListDisplayNumberOfItemsPerPage;
    }

    public void setViewChildCollectionListDisplayNumberOfItemsPerPage(Integer viewChildCollectionListDisplayNumberOfItemsPerPage) {
        this.viewChildCollectionListDisplayNumberOfItemsPerPage = viewChildCollectionListDisplayNumberOfItemsPerPage;
    }

    public Boolean getViewChildCollectionListDisplayId() {
        return viewChildCollectionListDisplayId;
    }

    public void setViewChildCollectionListDisplayId(Boolean viewChildCollectionListDisplayId) {
        this.viewChildCollectionListDisplayId = viewChildCollectionListDisplayId;
    }

    public Boolean getViewChildCollectionListDisplayDescription() {
        return viewChildCollectionListDisplayDescription;
    }

    public void setViewChildCollectionListDisplayDescription(Boolean viewChildCollectionListDisplayDescription) {
        this.viewChildCollectionListDisplayDescription = viewChildCollectionListDisplayDescription;
    }

    public Boolean getViewChildCollectionListDisplayOwnerUser() {
        return viewChildCollectionListDisplayOwnerUser;
    }

    public void setViewChildCollectionListDisplayOwnerUser(Boolean viewChildCollectionListDisplayOwnerUser) {
        this.viewChildCollectionListDisplayOwnerUser = viewChildCollectionListDisplayOwnerUser;
    }

    public Boolean getViewChildCollectionListDisplayOwnerGroup() {
        return viewChildCollectionListDisplayOwnerGroup;
    }

    public void setViewChildCollectionListDisplayOwnerGroup(Boolean viewChildCollectionListDisplayOwnerGroup) {
        this.viewChildCollectionListDisplayOwnerGroup = viewChildCollectionListDisplayOwnerGroup;
    }

    public Boolean getViewChildCollectionListDisplayCreatedByUser() {
        return viewChildCollectionListDisplayCreatedByUser;
    }

    public void setViewChildCollectionListDisplayCreatedByUser(Boolean viewChildCollectionListDisplayCreatedByUser) {
        this.viewChildCollectionListDisplayCreatedByUser = viewChildCollectionListDisplayCreatedByUser;
    }

    public Boolean getViewChildCollectionListDisplayCreatedOnDateTime() {
        return viewChildCollectionListDisplayCreatedOnDateTime;
    }

    public void setViewChildCollectionListDisplayCreatedOnDateTime(Boolean viewChildCollectionListDisplayCreatedOnDateTime) {
        this.viewChildCollectionListDisplayCreatedOnDateTime = viewChildCollectionListDisplayCreatedOnDateTime;
    }

    public Boolean getViewChildCollectionListDisplayLastModifiedByUser() {
        return viewChildCollectionListDisplayLastModifiedByUser;
    }

    public void setViewChildCollectionListDisplayLastModifiedByUser(Boolean viewChildCollectionListDisplayLastModifiedByUser) {
        this.viewChildCollectionListDisplayLastModifiedByUser = viewChildCollectionListDisplayLastModifiedByUser;
    }

    public Boolean getViewChildCollectionListDisplayLastModifiedOnDateTime() {
        return viewChildCollectionListDisplayLastModifiedOnDateTime;
    }

    public void setViewChildCollectionListDisplayLastModifiedOnDateTime(Boolean viewChildCollectionListDisplayLastModifiedOnDateTime) {
        this.viewChildCollectionListDisplayLastModifiedOnDateTime = viewChildCollectionListDisplayLastModifiedOnDateTime;
    }

    public Integer getViewParentCollectionListDisplayNumberOfItemsPerPage() {
        return viewParentCollectionListDisplayNumberOfItemsPerPage;
    }

    public void setViewParentCollectionListDisplayNumberOfItemsPerPage(Integer viewParentCollectionListDisplayNumberOfItemsPerPage) {
        this.viewParentCollectionListDisplayNumberOfItemsPerPage = viewParentCollectionListDisplayNumberOfItemsPerPage;
    }

    public Boolean getViewParentCollectionListDisplayId() {
        return viewParentCollectionListDisplayId;
    }

    public void setViewParentCollectionListDisplayId(Boolean viewParentCollectionListDisplayId) {
        this.viewParentCollectionListDisplayId = viewParentCollectionListDisplayId;
    }

    public Boolean getViewParentCollectionListDisplayDescription() {
        return viewParentCollectionListDisplayDescription;
    }

    public void setViewParentCollectionListDisplayDescription(Boolean viewParentCollectionListDisplayDescription) {
        this.viewParentCollectionListDisplayDescription = viewParentCollectionListDisplayDescription;
    }

    public Boolean getViewParentCollectionListDisplayOwnerUser() {
        return viewParentCollectionListDisplayOwnerUser;
    }

    public void setViewParentCollectionListDisplayOwnerUser(Boolean viewParentCollectionListDisplayOwnerUser) {
        this.viewParentCollectionListDisplayOwnerUser = viewParentCollectionListDisplayOwnerUser;
    }

    public Boolean getViewParentCollectionListDisplayOwnerGroup() {
        return viewParentCollectionListDisplayOwnerGroup;
    }

    public void setViewParentCollectionListDisplayOwnerGroup(Boolean viewParentCollectionListDisplayOwnerGroup) {
        this.viewParentCollectionListDisplayOwnerGroup = viewParentCollectionListDisplayOwnerGroup;
    }

    public Boolean getViewParentCollectionListDisplayCreatedByUser() {
        return viewParentCollectionListDisplayCreatedByUser;
    }

    public void setViewParentCollectionListDisplayCreatedByUser(Boolean viewParentCollectionListDisplayCreatedByUser) {
        this.viewParentCollectionListDisplayCreatedByUser = viewParentCollectionListDisplayCreatedByUser;
    }

    public Boolean getViewParentCollectionListDisplayCreatedOnDateTime() {
        return viewParentCollectionListDisplayCreatedOnDateTime;
    }

    public void setViewParentCollectionListDisplayCreatedOnDateTime(Boolean viewParentCollectionListDisplayCreatedOnDateTime) {
        this.viewParentCollectionListDisplayCreatedOnDateTime = viewParentCollectionListDisplayCreatedOnDateTime;
    }

    public Boolean getViewParentCollectionListDisplayLastModifiedByUser() {
        return viewParentCollectionListDisplayLastModifiedByUser;
    }

    public void setViewParentCollectionListDisplayLastModifiedByUser(Boolean viewParentCollectionListDisplayLastModifiedByUser) {
        this.viewParentCollectionListDisplayLastModifiedByUser = viewParentCollectionListDisplayLastModifiedByUser;
    }

    public Boolean getViewParentCollectionListDisplayLastModifiedOnDateTime() {
        return viewParentCollectionListDisplayLastModifiedOnDateTime;
    }

    public void setViewParentCollectionListDisplayLastModifiedOnDateTime(Boolean viewParentCollectionListDisplayLastModifiedOnDateTime) {
        this.viewParentCollectionListDisplayLastModifiedOnDateTime = viewParentCollectionListDisplayLastModifiedOnDateTime;
    }

    public Integer getViewCollectionLogListDisplayNumberOfItemsPerPage() {
        return viewCollectionLogListDisplayNumberOfItemsPerPage;
    }

    public void setViewCollectionLogListDisplayNumberOfItemsPerPage(Integer viewCollectionLogListDisplayNumberOfItemsPerPage) {
        this.viewCollectionLogListDisplayNumberOfItemsPerPage = viewCollectionLogListDisplayNumberOfItemsPerPage;
    }

    public Boolean getViewCollectionLogListDisplayId() {
        return viewCollectionLogListDisplayId;
    }

    public void setViewCollectionLogListDisplayId(Boolean viewCollectionLogListDisplayId) {
        this.viewCollectionLogListDisplayId = viewCollectionLogListDisplayId;
    }

    public Boolean getViewCollectionLogListDisplayCreatedByUser() {
        return viewCollectionLogListDisplayCreatedByUser;
    }

    public void setViewCollectionLogListDisplayCreatedByUser(Boolean viewCollectionLogListDisplayCreatedByUser) {
        this.viewCollectionLogListDisplayCreatedByUser = viewCollectionLogListDisplayCreatedByUser;
    }

    public Boolean getViewCollectionLogListDisplayCreatedOnDateTime() {
        return viewCollectionLogListDisplayCreatedOnDateTime;
    }

    public void setViewCollectionLogListDisplayCreatedOnDateTime(Boolean viewCollectionLogListDisplayCreatedOnDateTime) {
        this.viewCollectionLogListDisplayCreatedOnDateTime = viewCollectionLogListDisplayCreatedOnDateTime;
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
