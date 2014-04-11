package gov.anl.aps.cms.portal.controllers;

import gov.anl.aps.cms.portal.exceptions.CmsPortalException;
import gov.anl.aps.cms.portal.model.beans.AbstractFacade;
import gov.anl.aps.cms.portal.model.beans.SettingTypeFacade;
import gov.anl.aps.cms.portal.model.entities.SettingType;
import gov.anl.aps.cms.portal.model.entities.User;
import gov.anl.aps.cms.portal.utilities.CollectionUtility;
import gov.anl.aps.cms.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

public abstract class CrudEntityController<EntityType, FacadeType extends AbstractFacade<EntityType>> implements Serializable
{

    private static final Logger logger = Logger.getLogger(CrudEntityController.class.getName());

    @EJB
    private SettingTypeFacade settingTypeFacade;

    private EntityType current = null;
    private DataModel listDataModel = null;
    private DataTable listDataTable = null;
    private Date listSettingsTimestamp = null;
    private List<SettingType> settingTypeList;
    private Map<String, SettingType> settingTypeMap;
    private List<EntityType> filteredItems = null;

    protected Integer displayNumberOfItemsPerPage = null;
    protected Boolean displayId = null;
    protected Boolean displayOwnerUser = null;
    protected Boolean displayOwnerGroup = null;
    protected Boolean displayCreatedByUser = null;
    protected Boolean displayCreatedOnDateTime = null;
    protected Boolean displayLastModifiedByUser = null;
    protected Boolean displayLastModifiedOnDateTime = null;

    protected String filterByName = null;
    protected String filterByOwnerUser = null;
    protected String filterByOwnerGroup = null;
    protected String filterByCreatedByUser = null;
    protected String filterByCreatedOnDateTime = null;
    protected String filterByLastModifiedByUser = null;
    protected String filterByLastModifiedOnDateTime = null;

    public CrudEntityController() {
    }

    @PostConstruct
    public void initialize() {
        initializeListSettings();
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

    public void selectByRequestParams() {
    }

    public EntityType getSelected() {
        selectByRequestParams();
        if (current == null) {
            current = createEntityInstance();
        }
        return current;
    }

    public DataModel createDataModel() {
        return new ListDataModel(getFacade().findAll());
    }

    public void initializeListSettings() {
        User sessionUser = (User) SessionUtility.getUser();
        if (sessionUser != null) {
            updateListSettingsFromSessionUser(sessionUser);
        }
        else {
            updateListSettingsFromSettingTypeDefaults(getSettingTypeMap());
        }
    }

    public void updateListSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
    }

    public void updateListSettingsFromSessionUser(User sessionUser) {
    }

    public void updateListSettingsFromListDataTable(DataTable dataTable) {
    }

    public void clearListFilters() {
    }

    public String resetList() {
        logger.debug("Resetting list");
        clearListFilters();
        resetDataModel();
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

    public DataTable getListDataTable() {
        logger.debug("Getting data table");
        User sessionUser = (User) SessionUtility.getUser();
        if (sessionUser != null) {
            logger.debug("Session user is not null, list settings timestamp: " + sessionUser.getUserSettingsModificationDate());
            if (listSettingsTimestamp == null || sessionUser.areUserSettingsModifiedAfterDate(listSettingsTimestamp)) {
                logger.debug("Updating list settings from session user");
                updateListSettingsFromSessionUser(sessionUser);
                resetDataModel();
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

    public boolean isViewValid() {
        selectByRequestParams();
        return current != null;
    }

    public String prepareView(EntityType entity) {
        logger.debug("Preparing view");
        current = entity;
        return "view?faces-redirect=true";
    }

    public String view() {
        return "view?faces-redirect=true";
    }

    public String prepareCreate() {
        current = createEntityInstance();
        return "create?faces-redirect=true";
    }

    protected void prepareEntityInsert(EntityType entity) throws CmsPortalException {
    }

    public String create() {
        try {
            prepareEntityInsert(current);
            getFacade().create(current);
            SessionUtility.addInfoMessage("Success", "Created " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return view();
        }
        catch (CmsPortalException ex) {
            SessionUtility.addErrorMessage("Error", "Could not create " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public String prepareEdit(EntityType entity) {
        current = entity;
        return "edit?faces-redirect=true";
    }

    public String edit() {
        return "edit?faces-redirect=true";
    }

    protected void prepareEntityUpdate(EntityType entity) throws CmsPortalException {
    }

    public String update() {
        try {
            prepareEntityUpdate(current);
            getFacade().edit(current);
            SessionUtility.addInfoMessage("Success", "Updated " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return view();
        }
        catch (CmsPortalException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public String destroy() {
        if (current == null) {
            // Do nothing if current item is not set.
            return null;
        }
        try {
            getFacade().remove(current);
            SessionUtility.addInfoMessage("Success", "Deleted " + getEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetDataModel();
            return prepareList();
        }
        catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getEntityTypeName() + ": " + ex.getMessage());
            return null;
        }
    }

    public DataModel getItems() {
        if (listDataModel == null) {
            listDataModel = createDataModel();
        }
        return listDataModel;
    }

    public List<EntityType> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<EntityType> filteredItems) {
        this.filteredItems = filteredItems;
    }

    private void resetDataModel() {
        listDataModel = null;
        filteredItems = null;
        current = null;
        listDataTable = null;
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
}
