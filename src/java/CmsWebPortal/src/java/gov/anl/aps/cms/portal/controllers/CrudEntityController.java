package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.CmsPortalException;
import gov.anl.aps.cms.portal.model.beans.AbstractFacade;
import gov.anl.aps.cms.portal.model.beans.SettingTypeFacade;
import gov.anl.aps.cms.portal.model.entities.CloneableEntity;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.model.entities.UserSetting;
import gov.anl.aps.cms.portal.utilities.CollectionUtility;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

public abstract class CrudEntityController<EntityType extends CloneableEntity, FacadeType extends AbstractFacade<EntityType>> implements Serializable
{

    private static final Logger logger = Logger.getLogger(CrudEntityController.class.getName());

    @EJB
    private SettingTypeFacade settingTypeFacade;

    private EntityType current = null;

    private DataModel listDataModel = null;
    private DataTable listDataTable = null;
    private boolean listDataModelReset = true;
    private List<EntityType> filteredObjectList = null;

    private Date listSettingsTimestamp = null;
    private List<SettingType> settingTypeList;
    private Map<String, SettingType> settingTypeMap;
    private boolean settingsInitializedFromDefaults = false;

    private DataModel selectDataModel = null;
    private DataTable selectDataTable = null;
    private boolean selectDataModelReset = false;
    private List<EntityType> selectedObjectList = null;

    protected Integer displayNumberOfItemsPerPage = null;
    protected Boolean displayId = null;
    protected Boolean displayDescription = null;
    protected Boolean displayOwnerUser = null;
    protected Boolean displayOwnerGroup = null;
    protected Boolean displayCreatedByUser = null;
    protected Boolean displayCreatedOnDateTime = null;
    protected Boolean displayLastModifiedByUser = null;
    protected Boolean displayLastModifiedOnDateTime = null;

    protected String filterByName = null;
    protected String filterByDescription = null;
    protected String filterByOwnerUser = null;
    protected String filterByOwnerGroup = null;
    protected String filterByCreatedByUser = null;
    protected String filterByCreatedOnDateTime = null;
    protected String filterByLastModifiedByUser = null;
    protected String filterByLastModifiedOnDateTime = null;

    protected Integer selectDisplayNumberOfItemsPerPage = null;
    protected Boolean selectDisplayId = true;
    protected Boolean selectDisplayDescription = false;
    protected Boolean selectDisplayOwnerUser = true;
    protected Boolean selectDisplayOwnerGroup = true;
    protected Boolean selectDisplayCreatedByUser = false;
    protected Boolean selectDisplayCreatedOnDateTime = false;
    protected Boolean selectDisplayLastModifiedByUser = false;
    protected Boolean selectDisplayLastModifiedOnDateTime = false;

    protected String selectFilterByName = null;
    protected String selectFilterByDescription = null;
    protected String selectFilterByOwnerUser = null;
    protected String selectFilterByOwnerGroup = null;
    protected String selectFilterByCreatedByUser = null;
    protected String selectFilterByCreatedOnDateTime = null;
    protected String selectFilterByLastModifiedByUser = null;
    protected String selectFilterByLastModifiedOnDateTime = null;

    protected Integer idViewParam = null;
    protected String breadcrumbViewParam = null;

    public CrudEntityController() {
    }

    @PostConstruct
    public void initialize() {
        updateListSettings();
    }

    public List<SettingType> getSettingTypeList() {
        if (settingTypeList == null) {
            settingTypeList = settingTypeFacade.findAll();
        }
        return settingTypeList;
    }

    public Map<String, SettingType> getSettingTypeMap() {
        if (settingTypeMap == null) {
            settingTypeMap = new HashMap<>();
            for (SettingType settingType : getSettingTypeList()) {
                settingTypeMap.put(settingType.getName(), settingType);
            }
        }
        return settingTypeMap;
    }

    protected abstract FacadeType getFacade();

    protected abstract EntityType createEntityInstance();

    public abstract String getEntityTypeName();

    public abstract String getCurrentEntityInstanceName();

    public EntityType getCurrent() {
        return current;
    }

    public void setCurrent(EntityType current) {
        this.current = current;
    }

    public void processViewRequestParams() {
        breadcrumbViewParam = SessionUtility.getRequestParameterValue("breadcrumb");
        selectByRequestParams();
    }

    public void selectByRequestParams() {
    }

    public EntityType getSelected() {
        if (current == null) {
            current = createEntityInstance();
        }
        return current;
    }

    public DataModel createDataModel() {
        return new ListDataModel(getFacade().findAll());
    }

    public void updateListSettingsActionListener(ActionEvent actionEvent) {
        updateListSettings();
    }

    public void updateListSettings() {
        User sessionUser = (User) SessionUtility.getUser();
        boolean settingsUpdated = false;
        if (sessionUser != null) {
            List<UserSetting> userSettingList = sessionUser.getUserSettingList();
            if (userSettingList != null && !userSettingList.isEmpty() && sessionUser.areUserSettingsModifiedAfterDate(listSettingsTimestamp)) {
                updateListSettingsFromSessionUser(sessionUser);
                settingsUpdated = true;
            }
        }

        if (!settingsUpdated && !settingsInitializedFromDefaults) {
            settingsInitializedFromDefaults = true;
            updateSettingsFromSettingTypeDefaults(getSettingTypeMap());
        }
    }

    public String customizeListDisplay() {
        resetListDataModel();
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    public String customizeSelectDisplay() {
        resetSelectDataModel();
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
    }

    public void updateListSettingsFromSessionUser(User sessionUser) {
    }

    public void saveSettingsForSessionUser(User sessionUser) {
    }

    public void saveListSettingsForSessionUserActionListener(ActionEvent actionEvent) {
        logger.debug("Saving settings");
        User sessionUser = (User) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Updating list settings for session user");
            saveSettingsForSessionUser(sessionUser);
            resetListDataModel();
            listSettingsTimestamp = new Date();
        }
    }

    public void updateListSettingsFromListDataTable(DataTable dataTable) {
    }

    public void clearListFilters() {
    }

    public void clearSelectFilters() {
        if (selectDataTable != null) {
            selectDataTable.getFilters().clear();
        }
    }

    public String resetList() {
        logger.debug("Resetting list");
        clearListFilters();
        resetListDataModel();
        return prepareList();
    }

    public String prepareList() {
        logger.debug("Preparing list");
        current = null;
        if (listDataTable != null) {
            updateListSettingsFromListDataTable(listDataTable);
        }
        return "list?faces-redirect=true";
    }

    public String followBreadcrumbOrPrepareList() {
        String loadView = breadcrumbViewParam;
        if (loadView == null) {
            loadView = prepareList();
        }
        return loadView;
    }

    public DataTable getListDataTable() {
        logger.debug("Getting data table");
        User sessionUser = (User) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Session user is not null, list settings timestamp: " + sessionUser.getUserSettingsModificationDate());
            if (listSettingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(listSettingsTimestamp)) {
                logger.debug("Updating list settings from session user");
                updateListSettingsFromSessionUser(sessionUser);
                resetListDataModel();
                listSettingsTimestamp = new Date();
            }
            else {
                logger.debug("Session user settings have not been changed since " + listSettingsTimestamp);
            }
        }

        if (listDataTable == null) {
            logger.debug("Recreating data table");
            listDataTable = new DataTable();
        }
        return listDataTable;
    }

    public void setListDataTable(DataTable listDataTable) {
        logger.debug("Setting data table");
        this.listDataTable = listDataTable;
        updateListSettingsFromListDataTable(listDataTable);
    }

    public DataTable getSelectDataTable() {
        return selectDataTable;
    }

    public void setSelectDataTable(DataTable selectDataTable) {
        this.selectDataTable = selectDataTable;
    }

    public boolean isAnyListFilterSet() {
        if (listDataTable == null) {
            return false;
        }
        Map<String, String> filterMap = listDataTable.getFilters();
        for (String filter : filterMap.values()) {
            if (filter != null && !filter.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void updateViewSettingsFromSessionUser(User sessionUser) {
    }

    public void updateViewSettings() {
        User sessionUser = (User) SessionUtility.getUser();
        boolean settingsUpdated = false;
        if (sessionUser != null) {
            List<UserSetting> userSettingList = sessionUser.getUserSettingList();
            if (userSettingList != null && !userSettingList.isEmpty() && sessionUser.areUserSettingsModifiedAfterDate(listSettingsTimestamp)) {
                updateViewSettingsFromSessionUser(sessionUser);
                settingsUpdated = true;
            }
        }

        if (!settingsUpdated && !settingsInitializedFromDefaults) {
            settingsInitializedFromDefaults = true;
            updateSettingsFromSettingTypeDefaults(getSettingTypeMap());
        }
    }

    public void saveViewSettingsForSessionUserActionListener(ActionEvent actionEvent) {
        User sessionUser = (User) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Saving settings for session user");
            saveSettingsForSessionUser(sessionUser);
        }
    }

    public String customizeViewDisplay() {
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    public boolean isViewValid() {
        selectByRequestParams();
        return current != null;
    }

    public String prepareView(EntityType entity) {
        logger.debug("Preparing view");
        current = entity;
        updateViewSettings();
        return "view?faces-redirect=true";
    }

    public String view() {
        return "view?faces-redirect=true";
    }

    public String prepareCreate() {
        current = createEntityInstance();
        return "create?faces-redirect=true";
    }

    public EntityType cloneEntityInstance(EntityType entity) {
        EntityType clonedEntity;
        try {
            clonedEntity = (EntityType) (entity.clone());
        }
        catch (CloneNotSupportedException ex) {
            logger.error("Object cannot be cloned: " + ex);
            clonedEntity = createEntityInstance();
        }
        return clonedEntity;
    }

    public String prepareClone(EntityType entity) {
        current = cloneEntityInstance(entity);
        return "create?faces-redirect=true";
    }

    protected void prepareEntityInsert(EntityType entity) throws CmsPortalException {
    }

    public String create() {
        try {
            EntityType newEntity = current;
            prepareEntityInsert(current);
            getFacade().create(current);
            SessionUtility.addInfoMessage("Success", "Created " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            current = newEntity;
            return view();
        }
        catch (CmsPortalException | RuntimeException ex) {
            SessionUtility.addErrorMessage("Error", "Could not create " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public String prepareEdit(EntityType entity) {
        current = entity;
        return edit();
    }

    public String edit() {
        clearSelectFiltersAndResetSelectDataModel();
        return "edit?faces-redirect=true";
    }

    protected void prepareEntityUpdate(EntityType entity) throws CmsPortalException {
    }

    public String update() {
        try {
            logger.debug("Updating " + getEntityTypeName() + " " + getCurrentEntityInstanceName());
            prepareEntityUpdate(current);
            EntityType updatedEntity = getFacade().edit(current);
            SessionUtility.addInfoMessage("Success", "Updated " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            current = updatedEntity;
            return view();
        }
        catch (CmsPortalException | RuntimeException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public void destroy(EntityType entity) {
        current = entity;
        destroy();
    }

    public String destroy() {
        if (current == null) {
            logger.warn("Current item is not set");
            // Do nothing if current item is not set.
            return null;
        }
        try {
            logger.debug("Destroying " + getCurrentEntityInstanceName());
            getFacade().remove(current);
            SessionUtility.addInfoMessage("Success", "Deleted " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            return prepareList();
        }
        catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public DataModel getListDataModel() {
        if (listDataModel == null) {
            listDataModel = createDataModel();
        }
        return listDataModel;
    }

    public DataModel getSelectDataModel() {
        if (selectDataModel == null) {
            selectDataModel = createDataModel();
        }
        return selectDataModel;
    }

    public DataModel getItems() {
        return getListDataModel();
    }

    public List<EntityType> getSelectedObjectListAndResetSelectDataModel() {
        List<EntityType> returnList = selectedObjectList;
        resetSelectDataModel();
        return returnList;
    }

    public List<EntityType> getSelectedObjectList() {
        return selectedObjectList;
    }

    public List<EntityType> getFilteredObjectList() {
        return filteredObjectList;
    }

    public List<EntityType> getFilteredItems() {
        return filteredObjectList;
    }

    public void resetSelectedObjectList() {
        selectedObjectList = null;
    }

    public void setSelectedObjectList(List<EntityType> selectedObjectList) {
        this.selectedObjectList = selectedObjectList;
    }

    public void setFilteredObjectList(List<EntityType> filteredObjectList) {
        this.filteredObjectList = filteredObjectList;
    }

    public void setFilteredItems(List<EntityType> filteredItems) {
        this.filteredObjectList = filteredItems;
    }

    public void resetListDataModel() {
        listDataModel = null;
        listDataTable = null;
        listDataModelReset = true;
        filteredObjectList = null;
        current = null;
    }

    public void resetSelectDataModel() {
        selectDataModel = null;
        selectDataTable = null;
        selectedObjectList = null;
        selectDataModelReset = true;
    }

    public void clearListFiltersAndResetListDataModel() {
        clearListFilters();
        resetListDataModel();
    }

    public void clearSelectFiltersAndResetSelectDataModel() {
        clearSelectFilters();
        resetSelectDataModel();
    }

    public List<EntityType> getAvailableItems() {
        return getFacade().findAll();
    }

    public EntityType getEntity(Integer id) {
        return getFacade().find(id);
    }

    public SelectItem[] getAvailableItemsForSelectMany() {
        return CollectionUtility.getSelectItems(getFacade().findAll(), false);
    }

    public SelectItem[] getAvailableItemsForSelectOne() {
        return CollectionUtility.getSelectItems(getFacade().findAll(), true);
    }

    public String getCurrentViewId() {
        return SessionUtility.getCurrentViewId();
    }

    public static String displayEntityList(List<?> entityList) {
        String itemDelimiter = ", ";
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(entityList, itemDelimiter);
    }

    public Integer getDisplayNumberOfItemsPerPage() {
        return displayNumberOfItemsPerPage;
    }

    public void setDisplayNumberOfItemsPerPage(Integer displayNumberOfItemsPerPage) {
        this.displayNumberOfItemsPerPage = displayNumberOfItemsPerPage;
    }

    public Boolean getDisplayId() {
        return displayId;
    }

    public void setDisplayId(Boolean displayId) {
        this.displayId = displayId;
    }

    public Boolean getDisplayDescription() {
        return displayDescription;
    }

    public void setDisplayDescription(Boolean displayDescription) {
        this.displayDescription = displayDescription;
    }

    public Boolean getDisplayOwnerUser() {
        return displayOwnerUser;
    }

    public void setDisplayOwnerUser(Boolean displayOwnerUser) {
        this.displayOwnerUser = displayOwnerUser;
    }

    public Boolean getDisplayOwnerGroup() {
        return displayOwnerGroup;
    }

    public void setDisplayOwnerGroup(Boolean displayOwnerGroup) {
        this.displayOwnerGroup = displayOwnerGroup;
    }

    public Boolean getDisplayCreatedByUser() {
        return displayCreatedByUser;
    }

    public void setDisplayCreatedByUser(Boolean displayCreatedByUser) {
        this.displayCreatedByUser = displayCreatedByUser;
    }

    public Boolean getDisplayCreatedOnDateTime() {
        return displayCreatedOnDateTime;
    }

    public void setDisplayCreatedOnDateTime(Boolean displayCreatedOnDateTime) {
        this.displayCreatedOnDateTime = displayCreatedOnDateTime;
    }

    public Boolean getDisplayLastModifiedByUser() {
        return displayLastModifiedByUser;
    }

    public void setDisplayLastModifiedByUser(Boolean displayLastModifiedByUser) {
        this.displayLastModifiedByUser = displayLastModifiedByUser;
    }

    public Boolean getDisplayLastModifiedOnDateTime() {
        return displayLastModifiedOnDateTime;
    }

    public void setDisplayLastModifiedOnDateTime(Boolean displayLastModifiedOnDateTime) {
        this.displayLastModifiedOnDateTime = displayLastModifiedOnDateTime;
    }

    public void setFilterByName(String filterByName) {
        this.filterByName = filterByName;
    }

    public String getFilterByName() {
        return filterByName;
    }

    public String getFilterByDescription() {
        return filterByDescription;
    }

    public void setFilterByDescription(String filterByDescription) {
        this.filterByDescription = filterByDescription;
    }

    public String getFilterByOwnerUser() {
        return filterByOwnerUser;
    }

    public void setFilterByOwnerUser(String filterByOwnerUser) {
        this.filterByOwnerUser = filterByOwnerUser;
    }

    public String getFilterByOwnerGroup() {
        return filterByOwnerGroup;
    }

    public void setFilterByOwnerGroup(String filterByOwnerGroup) {
        this.filterByOwnerGroup = filterByOwnerGroup;
    }

    public String getFilterByCreatedByUser() {
        return filterByCreatedByUser;
    }

    public void setFilterByCreatedByUser(String filterByCreatedByUser) {
        this.filterByCreatedByUser = filterByCreatedByUser;
    }

    public String getFilterByCreatedOnDateTime() {
        return filterByCreatedOnDateTime;
    }

    public void setFilterByCreatedOnDateTime(String filterByCreatedOnDateTime) {
        this.filterByCreatedOnDateTime = filterByCreatedOnDateTime;
    }

    public String getFilterByLastModifiedByUser() {
        return filterByLastModifiedByUser;
    }

    public void setFilterByLastModifiedByUser(String filterByLastModifiedByUser) {
        this.filterByLastModifiedByUser = filterByLastModifiedByUser;
    }

    public String getFilterByLastModifiedOnDateTime() {
        return filterByLastModifiedOnDateTime;
    }

    public void setFilterByLastModifiedOnDateTime(String filterByLastModifiedOnDateTime) {
        this.filterByLastModifiedOnDateTime = filterByLastModifiedOnDateTime;
    }

    public boolean isListDataModelReset() {
        if (listDataModelReset) {
            listDataModelReset = false;
            return true;
        }
        return false;
    }

    public boolean isSelectDataModelReset() {
        if (selectDataModelReset) {
            selectDataModelReset = false;
            return true;
        }
        return false;
    }

    public Integer getSelectDisplayNumberOfItemsPerPage() {
        return selectDisplayNumberOfItemsPerPage;
    }

    public void setSelectDisplayNumberOfItemsPerPage(Integer selectDisplayNumberOfItemsPerPage) {
        this.selectDisplayNumberOfItemsPerPage = selectDisplayNumberOfItemsPerPage;
    }

    public Boolean getSelectDisplayId() {
        return selectDisplayId;
    }

    public void setSelectDisplayId(Boolean selectDisplayId) {
        this.selectDisplayId = selectDisplayId;
    }

    public Boolean getSelectDisplayDescription() {
        return selectDisplayDescription;
    }

    public void setSelectDisplayDescription(Boolean selectDisplayDescription) {
        this.selectDisplayDescription = selectDisplayDescription;
    }

    public Boolean getSelectDisplayOwnerUser() {
        return selectDisplayOwnerUser;
    }

    public void setSelectDisplayOwnerUser(Boolean selectDisplayOwnerUser) {
        this.selectDisplayOwnerUser = selectDisplayOwnerUser;
    }

    public Boolean getSelectDisplayOwnerGroup() {
        return selectDisplayOwnerGroup;
    }

    public void setSelectDisplayOwnerGroup(Boolean selectDisplayOwnerGroup) {
        this.selectDisplayOwnerGroup = selectDisplayOwnerGroup;
    }

    public Boolean getSelectDisplayCreatedByUser() {
        return selectDisplayCreatedByUser;
    }

    public void setSelectDisplayCreatedByUser(Boolean selectDisplayCreatedByUser) {
        this.selectDisplayCreatedByUser = selectDisplayCreatedByUser;
    }

    public Boolean getSelectDisplayCreatedOnDateTime() {
        return selectDisplayCreatedOnDateTime;
    }

    public void setSelectDisplayCreatedOnDateTime(Boolean selectDisplayCreatedOnDateTime) {
        this.selectDisplayCreatedOnDateTime = selectDisplayCreatedOnDateTime;
    }

    public Boolean getSelectDisplayLastModifiedByUser() {
        return selectDisplayLastModifiedByUser;
    }

    public void setSelectDisplayLastModifiedByUser(Boolean selectDisplayLastModifiedByUser) {
        this.selectDisplayLastModifiedByUser = selectDisplayLastModifiedByUser;
    }

    public Boolean getSelectDisplayLastModifiedOnDateTime() {
        return selectDisplayLastModifiedOnDateTime;
    }

    public void setSelectDisplayLastModifiedOnDateTime(Boolean selectDisplayLastModifiedOnDateTime) {
        this.selectDisplayLastModifiedOnDateTime = selectDisplayLastModifiedOnDateTime;
    }

    public String getSelectFilterByName() {
        return selectFilterByName;
    }

    public void setSelectFilterByName(String selectFilterByName) {
        this.selectFilterByName = selectFilterByName;
    }

    public String getSelectFilterByDescription() {
        return selectFilterByDescription;
    }

    public void setSelectFilterByDescription(String selectFilterByDescription) {
        this.selectFilterByDescription = selectFilterByDescription;
    }

    public String getSelectFilterByOwnerUser() {
        return selectFilterByOwnerUser;
    }

    public void setSelectFilterByOwnerUser(String selectFilterByOwnerUser) {
        this.selectFilterByOwnerUser = selectFilterByOwnerUser;
    }

    public String getSelectFilterByOwnerGroup() {
        return selectFilterByOwnerGroup;
    }

    public void setSelectFilterByOwnerGroup(String selectFilterByOwnerGroup) {
        this.selectFilterByOwnerGroup = selectFilterByOwnerGroup;
    }

    public String getSelectFilterByCreatedByUser() {
        return selectFilterByCreatedByUser;
    }

    public void setSelectFilterByCreatedByUser(String selectFilterByCreatedByUser) {
        this.selectFilterByCreatedByUser = selectFilterByCreatedByUser;
    }

    public String getSelectFilterByCreatedOnDateTime() {
        return selectFilterByCreatedOnDateTime;
    }

    public void setSelectFilterByCreatedOnDateTime(String selectFilterByCreatedOnDateTime) {
        this.selectFilterByCreatedOnDateTime = selectFilterByCreatedOnDateTime;
    }

    public String getSelectFilterByLastModifiedByUser() {
        return selectFilterByLastModifiedByUser;
    }

    public void setSelectFilterByLastModifiedByUser(String selectFilterByLastModifiedByUser) {
        this.selectFilterByLastModifiedByUser = selectFilterByLastModifiedByUser;
    }

    public String getSelectFilterByLastModifiedOnDateTime() {
        return selectFilterByLastModifiedOnDateTime;
    }

    public void setSelectFilterByLastModifiedOnDateTime(String selectFilterByLastModifiedOnDateTime) {
        this.selectFilterByLastModifiedOnDateTime = selectFilterByLastModifiedOnDateTime;
    }

    public Integer getIdViewParam() {
        return idViewParam;
    }

    public void setIdViewParam(Integer idViewParam) {
        this.idViewParam = idViewParam;
    }

    public String getBreadcrumbViewParam() {
        return breadcrumbViewParam;
    }

    public void setBreadcrumbViewParam(String breadcrumbViewParam) {
        this.breadcrumbViewParam = breadcrumbViewParam;
    }

}
