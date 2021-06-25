/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.import_export.import_.wizard.ItemDomainImportWizard;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.constants.CdbRole;
import gov.anl.aps.cdb.common.exceptions.AuthorizationError;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidRequest;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LogTopicFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.LogUtility;
import gov.anl.aps.cdb.portal.utilities.AuthorizationUtility;
import gov.anl.aps.cdb.common.utilities.CollectionUtility;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.common.utilities.StringUtility;
import gov.anl.aps.cdb.portal.constants.PortalStyles;
import gov.anl.aps.cdb.portal.controllers.settings.ICdbSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.CdbEntityControllerUtility;
import gov.anl.aps.cdb.portal.import_export.export.wizard.ItemDomainExportWizard;
import gov.anl.aps.cdb.portal.model.ItemLazyDataModel;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.view.objects.DomainImportExportInfo;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

/**
 * Base class for all CDB entity controllers. It encapsulates common
 * functionality related to entity creation, views, updates and deletion,
 * manages user settings and session customization relevant for a given entity
 * type, entity search, etc.
 *
 * @param <EntityType> CDB entity type
 * @param <FacadeType> CDB DB facade type
 */
public abstract class CdbEntityController<ControllerUtility extends CdbEntityControllerUtility<EntityType, FacadeType>, EntityType extends CdbEntity, FacadeType extends CdbEntityFacade<EntityType>, SettingObject extends ICdbSettings> implements Serializable, ICdbEntityController<EntityType> {    

    private static final Logger logger = LogManager.getLogger(CdbEntityController.class.getName());

    @EJB
    private LogTopicFacade logTopicFacade;

    protected SettingObject settingObject = null;

    protected DataModel listDataModel = null;
    protected DataTable listDataTable = null;
    protected boolean listDataModelReset = true;
    protected List<EntityType> filteredObjectList = null;
    protected List<EntityType> allObjectList = null;

    protected DataModel selectDataModel = null;
    protected DataTable selectDataTable = null;
    protected boolean selectDataModelReset = false;
    protected EntityType selectedObject = null;
    protected List<EntityType> selectedObjectList = null;

    protected String logText = null;
    protected Integer logTopicId = null;

    protected String breadcrumbViewParam = null;
    protected String breadcrumbObjectIdViewParam = null;

    private String searchString = null;
    private boolean caseInsensitive = true;
    private LinkedList<SearchResult> searchResultList;

    protected String contextRootPermanentUrl;

    // TODO create a base cdbentitycontrollerextension helper. 
    private Set<ItemControllerExtensionHelper> subscribedResetForCurrentControllerHelpers;
    private Set<ItemControllerExtensionHelper> subscribePrepareInsertForCurrentControllerHelpers;   
    
    private DomainImportExportInfo domainImportInfo;
    private DomainImportExportInfo domainExportInfo;
    
    ControllerUtility controllerUtility = null; 

    /**
     * Default constructor.
     */
    public CdbEntityController() {
        settingObject = createNewSettingObject();
        subscribedResetForCurrentControllerHelpers = new HashSet<>();
        subscribePrepareInsertForCurrentControllerHelpers = new HashSet<>();
        contextRootPermanentUrl = ConfigurationUtility.getPortalProperty(CdbProperty.PERMANENT_CONTEXT_ROOT_URL_PROPERTY_NAME);
    }
    
    protected ControllerUtility getControllerUtility() {
        if (controllerUtility == null) {
            controllerUtility = createControllerUtilityInstance(); 
        }
        return controllerUtility; 
    }
    
    protected abstract ControllerUtility createControllerUtilityInstance(); 

    /**
     * Initialize controller and update its settings.
     */
    @PostConstruct
    public void initialize() {
        settingObject.updateSettings();
    }

    public void registerSearchable() {
        SearchController searchController = SearchController.getInstance();
        searchController.registerSearchableController(this);
    }

    protected abstract SettingObject createNewSettingObject();

    /**
     * Navigate to invalid request error page.
     *
     * @param error error message
     */
    public void handleInvalidSessionRequest(String error) {
        SessionUtility.setLastSessionError(error);
        SessionUtility.navigateTo("/views/error/invalidRequest?faces-redirect=true");
    }

    /**
     * Reset log text and topic fields.
     */
    public void resetLogText() {
        logText = "";
        logTopicId = null;
    }

    /**
     * Abstract method for returning entity DB facade.
     *
     * @return entity DB facade
     */
    protected abstract FacadeType getEntityDbFacade();   

    /**
     * Abstract method for creating new entity instance.
     *
     * @return created entity instance
     */
    protected EntityType createEntityInstance() {
        UserInfo user = SessionUtility.getUser();
        return getControllerUtility().createEntityInstance(user); 
    }

    /**
     * Abstract method for retrieving entity type name.
     *
     * @return entity type name
     */
    public final String getEntityTypeName() {
        return getControllerUtility().getEntityTypeName(); 
    }

    /**
     * Get entity type group name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers when corresponding entities have groups.
     *
     * @return entity type group name
     */
    public String getEntityTypeGroupName() {
        return null;
    }

    /**
     * Get entity type category name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers when corresponding entities have categories.
     *
     * @return entity type category name
     */
    public String getEntityTypeCategoryName() {
        return null;
    }

    /**
     * Get entity type type name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers when corresponding entities have types.
     *
     * @return entity type type name
     */
    public String getEntityTypeTypeName() {
        return null;
    }

    /**
     * Get display string for entity type name.
     *
     * By default this method simply returns entity type name, and should be
     * overridden in derived controllers for entities with complex names (i.e.,
     * those that consist of two or more words).
     *
     * @return entity type name display string
     */
    public String getDisplayEntityTypeName() {
        return getControllerUtility().getDisplayEntityTypeName(); 
    }

    /**
     * Get current entity instance name.
     *
     * By default this method returns null, and should be overridden in derived
     * controllers.
     *
     * @return current entity instance name
     */
    public final String getCurrentEntityInstanceName() {
        return getControllerUtility().getEntityInstanceName(getCurrent());
    }

    /**
     * Get current entity instance.
     *
     * @return entity
     */
    public EntityType getCurrent() {
        ViewScopedCompanionController instance = ViewScopedCompanionController.getInstance();        
//        Object current = instance.getCurrent(getEntityTypeName());
        Object current = instance.getCurrent(getFlashCurrentKey());
        return (EntityType) current;
    }

    /**
     * Find entity instance by id.
     *
     * @param id entity instance id
     * @return entity instance
     */
    public EntityType findById(Integer id) {
        return getControllerUtility().findById(id); 
    }

    /**
     * Find unique entity by name. Returns null if none is found, or raises
     * CdbException if multiple instances are found.  This is used by the
     * import framework across controllers of different types, that may or may
     * not implement findByName().
     */
    public EntityType findUniqueByName(String name, String domainName) throws CdbException {

        if ((name == null) || (name.isEmpty())) {
            return null;
        }

        return getEntityDbFacade().findUniqueByName(name, domainName);
    }

    public EntityType findUniqueByPath(String path) throws CdbException {

        if ((path == null) || (path.isEmpty())) {
            return null;
        }

        return getControllerUtility().findByPath(path);
    }

    public EntityType findUniqueWithAttributes(Map<String,String> attributeMap) throws CdbException {
        return getEntityDbFacade().findUniqueWithAttributes(attributeMap);
    }

    /**
     * New current is being set, reset related variables.
     */
    protected final void resetVariablesForCurrent() {
        for (ItemControllerExtensionHelper helper : subscribedResetForCurrentControllerHelpers) {
            helper.resetExtensionVariablesForCurrent();
        }
    }

    /**
     * Subscription will call resetExtensionVariablesForCurrent
     *
     * @param entityController
     */
    public void subscribeResetVariablesForCurrent(ItemControllerExtensionHelper entityController) {
        subscribedResetForCurrentControllerHelpers.add(entityController);
    }

    public void subscribePrepareInsertForCurrent(ItemControllerExtensionHelper entityController) {
        subscribePrepareInsertForCurrentControllerHelpers.add(entityController);
    }

    /**
     * Set current entity instance.
     *
     * @param current entity instance
     */
    public void setCurrent(EntityType current) {
        resetVariablesForCurrent();
        ViewScopedCompanionController instance = ViewScopedCompanionController.getInstance();
        instance.setCurrent(current, getFlashCurrentKey());
    }

    /**
     * Get entity info object that belongs to the given entity.
     *
     * By default this method returns null. It should be overridden in derived
     * controllers for those entities that contain entity info object.
     *
     * @param entity entity object
     * @return entity info
     */
    public EntityInfo getEntityInfo(EntityType entity) {
        return null;
    }

    /**
     * Process view request parameters.
     *
     * If request is not valid, user will be redirected to appropriate error
     * page.
     */
    public void processViewRequestParams() {
        try {
            EntityType entity = selectByViewRequestParams();
            if (entity != null) {
                prepareEntityView(entity);
            }
        } catch (CdbException ex) {
            handleInvalidSessionRequest(ex.getErrorMessage());
        }
        processPreRender();
    }

    /**
     * Set breadcrumb variables from request parameters.
     */
    protected void setBreadcrumbRequestParams() {
        if (breadcrumbViewParam == null) {
            breadcrumbViewParam = SessionUtility.getRequestParameterValue("breadcrumb");
        }
        if (breadcrumbObjectIdViewParam == null) {
            breadcrumbObjectIdViewParam = SessionUtility.getRequestParameterValue("breadcrumbObjectId");
        }
    }

    /**
     * Select current entity instance for view from request parameters.
     *
     * @return selected entity instance
     * @throws CdbException in case of invalid request parameter values
     */
    public EntityType selectByViewRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");
        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            EntityType entity = findById(idParam);
            if (entity == null) {
                throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " id " + idParam + " does not exist.");
            }
            setCurrent(entity);
            return entity;
        } 
        if (getCurrent() == null) {
            EntityType currentFlash = getCurrentFlash();
            setCurrent(currentFlash);
        }
        
        EntityType current = getCurrent();
        if (current == null || current.getId() == null) {
            throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " has not been selected.");
        }
        return current;
    }

    /**
     * Process edit request parameters.
     *
     * If request is not valid or not authorized, user will be redirected to
     * appropriate error page.
     */
    public void processEditRequestParams() {
        try {
            selectByEditRequestParams();
        } catch (CdbException ex) {
            handleInvalidSessionRequest(ex.getErrorMessage());
        }
        processPreRender();
    }

    /**
     * Select current entity instance for edit from request parameters.
     *
     * @return selected entity instance
     * @throws CdbException in case of invalid request parameter values
     */
    public EntityType selectByEditRequestParams() throws CdbException {
        setBreadcrumbRequestParams();
        Integer idParam = null;
        String paramValue = SessionUtility.getRequestParameterValue("id");
        try {
            if (paramValue != null) {
                idParam = Integer.parseInt(paramValue);
            }
        } catch (NumberFormatException ex) {
            throw new InvalidRequest("Invalid value supplied for " + getDisplayEntityTypeName() + " id: " + paramValue);
        }
        if (idParam != null) {
            EntityType entity = findById(idParam);
            if (entity == null) {
                throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " id " + idParam + " does not exist.");
            }
            setCurrent(entity);
        }
                
        if (getCurrent() == null) {
            EntityType currentFlash = getCurrentFlash();
            setCurrent(currentFlash);
        }
        
        EntityType current = getCurrent();
        if (current == null || current.getId() == null) {
            throw new InvalidRequest(StringUtility.capitalize(getDisplayEntityTypeName()) + " has not been selected.");
        }

        // Make sure user is logged in
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser == null) {
            SessionUtility.pushViewOnStack("/views/" + getEntityViewsDirectory() + "/edit.xhtml?id=" + idParam + "&faces-redirect=true");
            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
            return null;
        } else {
            CdbRole sessionRole = (CdbRole) SessionUtility.getRole();
            if (sessionRole != CdbRole.ADMIN) {
                // Make sure user is authorized to edit entity
                // Try entity info first, then entity itself second
                boolean userAuthorized = verifyUserIsAuthorizedToEdit(current, sessionUser); 
                if (!userAuthorized) {
                    userAuthorized = AuthorizationUtility.isEntityWriteableByUser(current, sessionUser);
                    if (!userAuthorized) {
                        throw new AuthorizationError("User " + sessionUser.getUsername() + " is not authorized to edit "
                                + getDisplayEntityTypeName() + " object with id " + current.getId() + ".");
                    }
                }
            }
        }
        return current;
    }
    
    protected boolean verifyUserIsAuthorizedToEdit(EntityType entity, UserInfo userInfo) {
        return AuthorizationUtility.isEntityWriteableByUser(getEntityInfo(entity), userInfo); 
    }

    /**
     * Process create request parameters.
     *
     * If request is not valid or not authorized, user will be redirected to
     * appropriate error page.
     */
    public void processCreateRequestParams() {
        try {
            selectByCreateRequestParams();
        } catch (CdbException ex) {
            handleInvalidSessionRequest(ex.getErrorMessage());
        }
    }

    /**
     * Get a entity views directory name.
     *
     * @return String of the directory name in views directory.
     */
    protected String getEntityViewsDirectory() {
        return getEntityTypeName();
    }

    /**
     * Create new entity instance based on request parameters.
     *
     * @throws CdbException in case of invalid request parameter values
     */
    public void selectByCreateRequestParams() throws CdbException {
        setBreadcrumbRequestParams();

        // Make sure user is logged in
        UserInfo sessionUser = (UserInfo) SessionUtility.getUser();
        if (sessionUser == null) {
            SessionUtility.pushViewOnStack("/views/" + getEntityViewsDirectory() + "/create.xhtml?faces-redirect=true");
            SessionUtility.navigateTo("/views/login.xhtml?faces-redirect=true");
        } else {
            CdbRole sessionRole = (CdbRole) SessionUtility.getRole();
            if (sessionRole != CdbRole.ADMIN) {
                // Make sure user is authorized to create entity
                boolean userAuthorized = entityCanBeCreatedByUsers();
                if (!userAuthorized) {
                    throw new AuthorizationError("User " + sessionUser.getUsername() + " is not authorized to create "
                            + getDisplayEntityTypeName() + " entities.");
                }
            }
            // User authorized.
            EntityType entity = getCurrentFlash();
            if (entity == null || entity.getId() != null) {
                // entity is not yet set, or current entity is already in db. 
                prepareCreate();
            } else {
                setCurrent(entity);
            }
        }

    }

    /**
     * Get selected entity instance, or create new instance if none has been
     * selected previously.
     *
     * @return entity instance
     */
    public EntityType getSelected() {
        if (getCurrent() == null) {
            setCurrent(createEntityInstance());
        }
        return getCurrent();
    }

    /**
     * Override this function if a derived controller needs to process when new
     * settings are present.
     */
    public void settingsAreReloaded() {
        resetListDataModel();
    }

    /**
     * Customize display for entity list.
     *     
     */
    public void customizeListDisplay() {
    }

    /**
     * Customize display for selection dialog.
     *
     * @return current view URL for page reload
     */
    public String customizeSelectDisplay() {
        resetSelectDataModel();
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    /**
     * Reset list variables and associated filter values and data model.
     *
     * @return URL to entity list view
     */
    public String resetList() {
        logger.debug("Resetting list data model for " + getDisplayEntityTypeName());
        settingObject.clearListFilters();
        resetListDataModel();
        return prepareList();
    }

    /**
     * Prepare entity list view.
     *
     * @return URL to entity list view
     */
    public String prepareList() {
        logger.debug("Preparing list data model for " + getDisplayEntityTypeName());        
        if (listDataTable != null) {
            settingObject.updateListSettingsFromListDataTable(listDataTable);
        }
        return "list?faces-redirect=true";
    }

    /**
     * Prepare list view from a given view path.
     *
     * @param viewPath
     * @return URL to entity list view
     */
    public String prepareListFromViewPath(String viewPath) {
        return viewPath + "/" + prepareList();
    }

    /**
     * Reset list and return entity view.
     *
     * @return URL to selected entity instance view
     */
    public String resetListForView() {
        logger.debug("Resetting list for " + getDisplayEntityTypeName() + " view ");
        settingObject.clearListFilters();
        resetListDataModel();
        return view();
    }

    /**
     * Reset list and return entity instance edit page.
     *
     * @return URL to selected entity instance edit page.
     */
    public String resetListForEdit() {
        logger.debug("Resetting list for " + getDisplayEntityTypeName() + " edit");
        settingObject.clearListFilters();
        resetListDataModel();
        return edit();
    }

    /**
     * Follow breadcrumb if it is set, or prepare entity list view.
     *
     * @return previous view if breadcrumb parameters are set, or entity list
     * view otherwise
     */
    public String followBreadcrumbOrPrepareList() {
        // TODO investigate for breadcrumb issue
        String loadView = breadcrumbViewParam;
        if (loadView == null) {
            loadView = prepareList();
        } else if (breadcrumbObjectIdViewParam != null) {
            Integer entityId = Integer.parseInt(breadcrumbObjectIdViewParam);
            loadView = breadcrumbViewParam + "?faces-redirect=true&id=" + entityId;
        }
        breadcrumbViewParam = null;
        breadcrumbObjectIdViewParam = null;
        return loadView;
    }

    public void processPreRender() {
        settingObject.updateSettings();
    }

    public void processPreRenderList() {
        if (settingObject.updateSettings()) {
            resetListDataModel();
        }
    }

    /**
     * Get entity selection list data table.
     *
     * @return selection list data table
     */
    public DataTable getSelectDataTable() {
        return selectDataTable;
    }

    /**
     * Set entity selection list data table.
     *
     * @param selectDataTable selection list data table
     */
    public void setSelectDataTable(DataTable selectDataTable) {
        this.selectDataTable = selectDataTable;
    }

    /**
     * Customize view display and reload current page.
     *
     * @return URL for the current view
     */
    public String customizeViewDisplay() {
        return getUrlForCurrentView();
    }

    /**
     * Gets a redirection string for the current view.
     *
     * @return redirection string for the current view
     */
    protected String getUrlForCurrentView() {
        String returnPage = SessionUtility.getCurrentViewId() + "?faces-redirect=true";
        logger.debug("Returning to page: " + returnPage);
        return returnPage;
    }

    /**
     * Verify that view is valid.
     *
     * If not, redirect user to appropriate error page.
     *
     * @throws IOException in case of IO errors
     */
    public void verifyView() throws IOException {
        EntityType current = getCurrent();
        if (current != null) {
            SessionUtility.redirectTo("/views/error/invalidRequest.xhtml");
        }
    }

    /**
     * Prepare entity view.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     */
    protected void prepareEntityView(EntityType entity) {
    }

    /**
     * Prepare entity view and return view URL.
     *
     * @param entity entity instance
     * @return entity view URL
     */
    public String prepareView(EntityType entity) {
        logger.debug("Preparing view");        
        setCurrentFlash(entity);
        settingObject.updateSettings();
        prepareEntityView(entity);
        return view();
    }
    
    /**
     * Prepare entity view from current and return view URL.
     * 
     * @return 
     */
    public String prepareView() {
        EntityType current = getCurrent();
        return prepareView(current);         
    }
    
    protected void loadCurrentFromFlash() {
        EntityType currentFlash = getCurrentFlash();
        
        setCurrent(currentFlash);
    }
    
    protected EntityType getCurrentFlash() {
        return (EntityType) SessionUtility.getFlashValue(getFlashCurrentKey()); 
    }
    
    protected void setCurrentFlash() {
        EntityType current = getCurrent();
        setCurrentFlash(current);        
    }
    
    protected void setCurrentFlash(EntityType current) {
        SessionUtility.setFlashValue(getFlashCurrentKey(), current);
    }
    
    private String getFlashCurrentKey() {
        return getEntityTypeName() + "Current"; 
    }

    /**
     * Return entity view page.
     *
     * @return URL to view page in the entity folder
     */
    public String view() {
        return "view?faces-redirect=true";
    }

    /**
     * Return entity view page with query parameters of id.
     *
     * @return URL to view page in the entity folder with id query paramter.
     */
    public String viewForCurrentEntity() {
        EntityType current = getCurrent();
        return "view?id=" + current.getId() + "&faces-redirect=true";
    }

    /**
     * Return entity list page.
     *
     * @return URL to list page in the entity folder
     */
    public String list() {
        return "list?faces-redirect=true";
    }

    /**
     * Return entity create page.
     *
     * @return URL to create page in the entity folder
     */
    public String prepareCreate() {
        setCurrentFlash(createEntityInstance());
        return "create?faces-redirect=true";
    }

    /**
     * Clone entity instance.
     *
     * @param entity entity instance to be cloned
     * @return cloned entity instance
     */
    public EntityType cloneEntityInstance(EntityType entity) {
        EntityType clonedEntity;
        try {
            UserInfo user = SessionUtility.getUser();                    
            clonedEntity = (EntityType) (entity.clone(user));
        } catch (CloneNotSupportedException ex) {
            logger.error("Object cannot be cloned: " + ex);
            clonedEntity = createEntityInstance();
        }
        return clonedEntity;
    }

    /**
     * Prepare entity instance clone and return view to the cloned instance.
     *
     * @param entity entity instance to be cloned
     * @return URL to cloned instance view
     */
    public String prepareClone(EntityType entity) {        
        EntityType cloneEntityInstance = cloneEntityInstance(entity);
        setCurrentFlash(cloneEntityInstance);
        return getEntityApplicationViewPath() + "/" + getCloneCreatePageName() + "?faces-redirect=true";
    }

    protected String getCloneCreatePageName() {
        return "create";
    }

    public String getEntityApplicationViewPath() {
        return "/views/" + getEntityViewsDirectory();
    }

    public final String getCurrentEntityPermalink() {
        EntityType current = getCurrent();
        if (current != null) {
            String viewPath = contextRootPermanentUrl;
            viewPath += getCurrentEntityRelativePermalink();
            return viewPath;
        }
        return null;
    }

    public String getCurrentEntityRelativePermalink() {
        EntityType current = getCurrent();
        return getEntityApplicationViewPath() + "/view?id=" + current.getId();
    }

    public String getEntityEditRowStyle(EntityType entity) {
        if (entity.getPersitanceErrorMessage() != null) {
            return PortalStyles.rowStyleErrorInEntity.getValue();
        }
        if (entity.getId() == null) {
            return PortalStyles.rowStyleNewEntity.getValue();
        }
        return "";
    }
    
    // TODO remove this method
    protected final void prepareEntityInsert(EntityType entity) throws CdbException {
        // TODO: This needs to be placed in item controller. 
        EntityType current = getCurrent();
        if (entity == current) {
            if (entity instanceof Item) {
                for (ItemControllerExtensionHelper helper : subscribedResetForCurrentControllerHelpers) {
                    helper.prepareInsertForCurrent();
                }
            }
        }
    }      
    
    public void createWithoutRedirect() {
        create(); 
    }

    public String create() {
        return create(false);
    }

    /**
     * Create new entity instance and return view to the new instance.
     *
     * @param silent determines if a message should be shown at completion to
     * the user.
     * @return URL to the new entity instance view
     */
    public String create(Boolean silent) {
        try {
            EntityType current = getCurrent();
            performCreateOperations(current);
            if (!silent) {
                SessionUtility.addInfoMessage("Success", "Created " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            }
            return viewForCurrentEntity(); 
        } catch (CdbException ex) {
            if (!silent) {
                SessionUtility.addErrorMessage("Error", "Could not create " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            }
            return null;
        } catch (RuntimeException ex) {
            if (!silent) {
                Throwable t = ExceptionUtils.getRootCause(ex);
                SessionUtility.addErrorMessage("Error", "Could not create " + getDisplayEntityTypeName() + ": " + t.getMessage());
            }
            return null;
        }
    }

    public void performCreateOperations(EntityType entity) throws CdbException, RuntimeException {
        performCreateOperations(entity, false);
    }

    public void performCreateOperations(EntityType entity, boolean skipUpdateCurrent) throws CdbException, RuntimeException {
        if (!skipUpdateCurrent) {
            setCurrent(entity);
        }

        UserInfo user = (UserInfo) SessionUtility.getUser();

        ControllerUtility controllerUtility = getControllerUtility();
        EntityType newEntity = controllerUtility.create(entity, user);

        resetListDataModel();
        resetSelectDataModel();
        // Best to reload the entity after creation to ensure all connections are updated and initalized. 
        Object newEntityId = newEntity.getId();

        if (!skipUpdateCurrent) {
            setCurrent(findById((Integer) newEntityId));
        }
    }

    public void performListCreateOperations(List<EntityType> entities) throws CdbException, RuntimeException {
        UserInfo user = SessionUtility.getUser();
        CdbEntityControllerUtility controllerUtility = getControllerUtility();

        controllerUtility.createList(entities, user);

        resetListDataModel();
        resetSelectDataModel();
    }

    public void createList(List<EntityType> entities) throws CdbException, RuntimeException {
        createList(entities, false);
    }

    public void createList(List<EntityType> entities, boolean silent) throws CdbException, RuntimeException {
        try {
            performListCreateOperations(entities);
            if (!silent) {
                SessionUtility.addInfoMessage("Success", "Created " + entities.size() + " " + getDisplayEntityTypeName() + " instances.");
            }
        } catch (CdbException ex) {
            if (!silent) {
                SessionUtility.addErrorMessage("Error", "Could not create list of " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (!silent) {
                Throwable t = ExceptionUtils.getRootCause(ex);
                SessionUtility.addErrorMessage("Error", "Could not create list of " + getDisplayEntityTypeName() + ": " + t.getMessage());
            }
            throw ex;
        }
    }

    /**
     * Prepare entity instance edit.
     *
     * @param entity entity instance to be updated
     * @return URL to edit page
     */
    public String prepareEdit(EntityType entity) {
        resetLogText();
        setCurrentFlash(entity);        
        return edit();
    }

    /**
     * Return entity edit page.
     *
     * @return URL to edit page in the entity folder
     */
    public String edit() {
        clearSelectFiltersAndResetSelectDataModel();
        return "edit?faces-redirect=true";
    }

    /**
     * Perform any addition actions after an entity has been updated.
     *
     * @param entity
     */
    protected void completeEntityUpdate(EntityType entity) {
    }

    public void updateWithoutRedirect() {
        update();
    }

    /**
     * Update current entity and save changes in the database.
     *
     * @return URL to current entity instance view page
     */
    public String update() {
        try {
            EntityType current = getCurrent();
            performUpdateOperations(current);
            reloadCurrent();
            SessionUtility.addInfoMessage("Success", "Updated " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");

            return viewForCurrentEntity();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + t.getMessage());
            return null;
        }
    }

    public void performUpdateOperations(EntityType entity) throws CdbException, RuntimeException {
        CdbEntityControllerUtility controllerUtility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();

        controllerUtility.update(entity, user);

        resetListDataModel();
        resetSelectDataModel();
        resetLogText();
        
        completeEntityUpdate(entity);
    }

    public void updateList(List<EntityType> entities) throws CdbException, RuntimeException {
        try {
            performUpdateOperations(entities);
            SessionUtility.addInfoMessage("Success", "Updated " + entities.size() + " " + getDisplayEntityTypeName() + " instances.");
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update list of " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            SessionUtility.addErrorMessage("Error", "Could not update list of " + getDisplayEntityTypeName() + ": " + t.getMessage());
            throw ex;
        }
    }

    public void performUpdateOperations(List<EntityType> entities) throws CdbException, RuntimeException {
        CdbEntityControllerUtility controllerUtility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();

        controllerUtility.updateList(entities, user);

        for (EntityType entity : entities) {
            completeEntityUpdate(entity);
        }

        resetListDataModel();
        resetSelectDataModel();
        resetLogText();
    }

    public void reloadCurrent() {
        EntityType current = getCurrent(); 
        current = getEntityDbFacade().find(current.getId());
        setCurrent(current);
    }

    public String inlineUpdate() {
        String updateResult = update();

        // An error occured, reload the page with correct information. 
        if (updateResult == null) {
            return viewForCurrentEntity();
        }

        return null;
    }

    /**
     * Prepare entity update when changes involve removing associated objects
     * from the database.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected final void prepareEntityUpdateOnRemoval(EntityType entity) throws CdbException {
    }

    /**
     * Update current entity and save changes in the database.
     *
     * This method is used when changes involve only removing associated objects
     * from the database.
     *
     * @return URL to current entity instance view page
     */
    public String updateOnRemoval() {
        try {
            logger.debug("Updating " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName());
            
            CdbEntityControllerUtility controllerUtility = getControllerUtility();
            UserInfo user = SessionUtility.getUser();
            EntityType current = getCurrent();
            
            controllerUtility.updateOnRemoval(current, user); 
                                                
            SessionUtility.addInfoMessage("Success", "Updated " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            resetListDataModel();
            resetSelectDataModel();
            resetLogText();
            reloadCurrent();
            
            return view();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);            
            SessionUtility.addErrorMessage("Error", "Could not update " + getDisplayEntityTypeName() + ": " + t.getMessage());
            return null;
        }
    }

    /**
     * Prepare entity removal from the database.
     *
     * This method should be overridden in the derived controller.
     *
     * @param entity entity instance
     * @throws CdbException in case of any errors
     */
    protected final void prepareEntityDestroy(EntityType entity) throws CdbException {
    }

    /**
     * Perform any additional actions upon successful removal of an entity.
     */
    protected void completeEntityDestroy(EntityType entity) {
    }

    /**
     * Remove entity instance from the database.
     *
     * @param entity entity instance to be deleted
     */
    public void destroy(EntityType entity) {
        setCurrent(entity);
        destroy();
    }

    /**
     * Executes destroy but does not return redirection string.
     *
     * @return redirection to current view when successful.
     */
    public String destroyInCurrentView() {
        String result = destroy();

        if (result != null) {
            return getUrlForCurrentView();
        }

        return null;
    }

    public void performDestroyOperations(EntityType entity) throws CdbException, RuntimeException {
        
        if (entity == null) {
            logger.warn("entity item is not set");
            // Do nothing if entity item is not set.
            return;
        } else if (entity.getId() == null) {
            logger.warn("entity item id is null");
            completeEntityDestroy(entity);
            // Do nothing if there is no id.
            return;
        }

        CdbEntityControllerUtility controllerUtility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();
        
        controllerUtility.destroy(entity, user);

        completeEntityDestroy(entity);
        
        resetListDataModel();
        resetSelectDataModel();
        settingObject.clearListFilters();        
    }

    /**
     * Remove current (selected) entity instance from the database and reset
     * list variables and data model.
     *
     * @return URL to entity list page
     */
    public String destroy() {
        logger.debug("Destroying " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName());
        try {
            EntityType current = getCurrent();
            performDestroyOperations(current);
            SessionUtility.addInfoMessage("Success", "Deleted " + getDisplayEntityTypeName() + " " + getCurrentEntityInstanceName() + ".");
            return prepareList();
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + ex.getMessage());            
            return null;
        } catch (RuntimeException ex) {
            Throwable t = ExceptionUtils.getRootCause(ex);
            logger.error("Could not delete " + getDisplayEntityTypeName() + " "
                    + getCurrentEntityInstanceName() + ": " + t.getMessage());
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + ": " + t.getMessage());            
            return null;
        }
    }

    public void performListDestroyOperations(
            List<EntityType> entities,
            EntityType updateEntity)
            throws CdbException, RuntimeException {

        CdbEntityControllerUtility controllerUtility = getControllerUtility();
        UserInfo user = SessionUtility.getUser();
        
        controllerUtility.destroyList(entities, updateEntity, user);

        for (EntityType entity : entities) {
            completeEntityDestroy(entity);
        }

        resetListDataModel();
        resetSelectDataModel();
        settingObject.clearListFilters();
    }

    public void destroyList(List<EntityType> entities, EntityType updateEntity) {
        logger.debug("Destroying " + entities.size() + " " + getDisplayEntityTypeName() + " instances.");
        try {
            performListDestroyOperations(entities, updateEntity);
            SessionUtility.addInfoMessage("Success", "Deleted " + entities.size() + " " + getDisplayEntityTypeName() + " instances.");
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete " + getDisplayEntityTypeName() + "instances: " + ex.getMessage());
        } catch (RuntimeException ex) {
            SessionUtility.addErrorMessage("Error", "Could not delete list of " + getDisplayEntityTypeName() + ": " + ex.getMessage());
        }
    }

    /**
     * Create data model from list of all available entity instances.
     */
    public void createListDataModel() {
        listDataModel = new ListDataModel(getAllObjectList());
    }

    public final List<EntityType> getAllObjectList() {
        if (allObjectList == null) {
            allObjectList = getControllerUtility().getAllEntities();
        }
        return allObjectList;
    }

    public final List<EntityType> getAllEntities() {
        return getEntityDbFacade().findAll();
    }

    public void setListDataModel(ListDataModel listDataModel) {
        this.listDataModel = listDataModel;
    }

    /**
     * Get list data model.
     *
     * If model is not set, this method will create it.
     *
     * @return list data model
     */
    public DataModel getListDataModel() {
        if (listDataModel == null) {
            createListDataModel();
        }
        return listDataModel;
    }

    /**
     * Prepare entity list for selection.
     *
     * This method should be overridden in the derived controller.
     *
     * @param selectEntityList entity list to be used for selection
     */
    public void prepareEntityListForSelection(List<EntityType> selectEntityList) {
    }

    /**
     * Create data model for entity selection.
     *
     * @return selection data model
     */
    public DataModel createSelectDataModel() {
        List<EntityType> selectEntityList = getEntityDbFacade().findAll();
        prepareEntityListForSelection(selectEntityList);
        selectDataModel = new ListDataModel(selectEntityList);
        return selectDataModel;
    }

    /**
     * Create data model for entity selection using provided list.
     *
     * @param selectEntityList entity list to be used for selection
     * @return selection data model
     */
    public DataModel createSelectDataModel(List<EntityType> selectEntityList) {
        selectDataModel = new ListDataModel(selectEntityList);
        return selectDataModel;
    }

    /**
     * Get data model for entity selection.
     *
     * If selection data model is null, it will be created.
     *
     * @return selection data model
     */
    public DataModel getSelectDataModel() {
        if (selectDataModel == null) {
            createSelectDataModel();
        }
        return selectDataModel;
    }

    /**
     * Create data model for entity selection, but without current (selected)
     * entity.
     *
     * @return selection data model
     */
    public DataModel createSelectDataModelWithoutCurrent() {
        List<EntityType> selectEntityList = getAvailableItemsWithoutCurrent();

        prepareEntityListForSelection(selectEntityList);
        selectDataModel = new ListDataModel(selectEntityList);
        return selectDataModel;
    }

    /**
     * Get data model for entity selection, but without current (selected)
     * entity.
     *
     * If selection data model is null, it will be created.
     *
     * @return selection data model
     */
    public DataModel getSelectDataModelWithoutCurrent() {
        if (selectDataModel == null) {
            createSelectDataModelWithoutCurrent();
        }
        return selectDataModel;
    }

    /**
     * Get data model using list of all entity instances.
     *
     * @return data model
     */
    public DataModel getItems() {
        return getListDataModel();
    }

    /**
     * Get list of selected objects and reset selection data model.
     *
     * @return list of selected objects
     */
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
    
    public List<EntityType> getFilteredEntities() {
        return filteredObjectList;
    }

    /**
     * Reset selected object list to null.
     */
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

    public EntityType getSelectedObjectAndResetDataModel() {
        EntityType entity = getSelectedObject();
        selectedObject = null;
        resetSelectDataModel();
        return entity;

    }

    public EntityType getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(EntityType selectedObject) {
        this.selectedObject = selectedObject;
    }

    /**
     * Reset list data model and set current entity.
     *
     * @param currentEntity current entity
     */
    public void resetListDataModelAndSetCurrent(EntityType currentEntity) {
        resetListDataModel();
        setCurrent(currentEntity);
    }

    /**
     * Reset various list variables to null so that they can be recreated for
     * next request.
     */
    public void resetListDataModel() {
        listDataModel = null;
        listDataTable = null;
        listDataModelReset = true;
        filteredObjectList = null;
        allObjectList = null;
        // Flush cache 
        //getFacade().flush();
    }

    /**
     * Reset various selection variables so that they can be recreated for next
     * request.
     */
    public void resetSelectDataModel() {
        selectDataModel = null;
        selectDataTable = null;
        selectedObjectList = null;
        selectDataModelReset = true;
        // Flush cache 
        //getFacade().flush();
    }

    public void clearListFiltersAndResetListDataModel() {
        settingObject.clearListFilters();
        resetListDataModel();
    }

    public void clearSelectFiltersAndResetSelectDataModelActionListener(ActionEvent actionEvent) {
        clearSelectFiltersAndResetSelectDataModel();
    }

    public void clearSelectFiltersAndResetSelectDataModel() {
        if (selectDataTable != null) {
            selectDataTable.getFilterBy().clear();
        }
        settingObject.clearSelectFilters();
        resetSelectDataModel();
    }

    /**
     * Create log object if log text is not null.
     *
     * @return new log entry
     */
    public Log prepareLogEntry() {
        Log logEntry = null;
        if (logText != null && !logText.isEmpty()) {
            logEntry = LogUtility.createLogEntry(logText);
            if (logTopicId != null) {
                LogTopic logTopic = logTopicFacade.find(logTopicId);
                logEntry.setLogTopic(logTopic);
            }
            resetLogText();
        }
        return logEntry;
    }

    public List<EntityType> getAvailableItems() {
        return getEntityDbFacade().findAll();
    }

    public List<EntityType> getAvailableItemsWithoutCurrent() {
        List<EntityType> entityList = getEntityDbFacade().findAll();
        EntityType current = getCurrent();
        if (current.getId() != null) {
            entityList.remove(current);
        }
        return entityList;
    }

    public EntityType getEntity(Integer id) {
        return getEntityDbFacade().find(id);
    }

    public SelectItem[] getAvailableItemsForSelectMany() {
        return CollectionUtility.getSelectItems(getEntityDbFacade().findAll(), false);
    }

    public SelectItem[] getAvailableItemsForSelectOne() {
        return CollectionUtility.getSelectItems(getEntityDbFacade().findAll(), true);
    }

    public String getCurrentViewId() {
        return SessionUtility.getCurrentViewId();
    }

    public static String displayEntityList(List<?> entityList) {
        String itemDelimiter = ", ";
        return CollectionUtility.displayItemListWithoutOutsideDelimiters(entityList, itemDelimiter);
    }

    /**
     * Search all entities for a given string.
     *
     * @param searchString search string
     * @param caseInsensitive use case insensitive search
     */
    public final void performEntitySearch(String searchString, boolean caseInsensitive) {        
        if (searchString.equals(this.searchString) && caseInsensitive == this.caseInsensitive) {
            // Return old results
            return;
        }
        this.searchString = searchString;
        this.caseInsensitive = caseInsensitive;
        resetSearchVariables();
        
        searchResultList = getControllerUtility().performEntitySearch(searchString, caseInsensitive); 
    }

    public void resetSearchVariables() {
        searchResultList = new LinkedList<>();
    }

    public LinkedList<SearchResult> getSearchResultList() {
        return searchResultList;
    }

    public boolean getDisplaySearchResultList() {
        return searchResultList != null && !searchResultList.isEmpty();
    }

    public boolean searchHasResults() {
        return !searchResultList.isEmpty();
    }

    public boolean entityHasCategories() {
        return getEntityTypeCategoryName() != null;
    }

    public boolean entityHasTypes() {
        return getEntityTypeTypeName() != null;
    }

    public String getEntityEntityCategoryName() {
        return "Category";
    }

    public String getEntityEntityTypeName() {
        return "Type";
    }

    public boolean entityHasGroups() {
        return getEntityTypeGroupName() != null;
    }

    public boolean entityCanBeCreatedByUsers() {
        return false;
    }

    public String getLogText() {
        return logText;
    }

    public void setLogText(String logText) {
        this.logText = logText;
    }

    public Integer getLogTopicId() {
        return logTopicId;
    }

    public void setLogTopicId(Integer logTopicId) {
        this.logTopicId = logTopicId;
    }

    public SettingObject getSettingObject() {
        return settingObject;
    }

    public Boolean getDisplayLoadPropertyValuesButton() {
        return false;
    }

    public Boolean getDisplayUpdateSortOrderButton() {
        return false;
    }

    /**
     * If list data model needs to be reset this method will return true, and
     * modify reset flag.
     *
     * @return true if list data model needs to be reset, false otherwise
     */
    public boolean shouldResetListDataModel() {
        if (listDataModelReset) {
            listDataModelReset = false;
            return true;
        }
        return false;
    }

    /**
     * If select data model needs to be reset this method will return true, and
     * modify reset flag.
     *
     * @return true if select data model needs to be reset, false otherwise
     */
    public boolean shouldResetSelectDataModel() {
        if (selectDataModelReset) {
            selectDataModelReset = false;
            return true;
        }
        return false;
    }

    public String getBreadcrumbViewParam() {
        return breadcrumbViewParam;
    }

    public void setBreadcrumbViewParam(String breadcrumbViewParam) {
        this.breadcrumbViewParam = breadcrumbViewParam;
    }

    /**
     * Throws an exception when item does not pass uniqueness check.
     *
     * @param item
     * @throws CdbException
     */
    public void checkItemUniqueness(EntityType entity) throws CdbException {
        throw new CdbException("Uniqueness check not implemented by controller: " + this.getClass().getName());
    }

    public boolean getEntityDisplayImportButton() {
        return false;
    }

    /**
     * Prepares import wizard.
     */
    public String prepareImport() throws CdbException {
        ItemDomainImportWizard.getInstance().setDomainInfo(getDomainImportInfo());
        return "import?faces-redirect=true";
    }
    
    protected DomainImportExportInfo initializeDomainImportInfo() {
        return null;
    }
    
    public DomainImportExportInfo getDomainImportInfo() {
        if (domainImportInfo == null) {
            domainImportInfo = initializeDomainImportInfo();
        }
        return domainImportInfo;
    }
    
    public boolean getEntityDisplayExportButton() {
        return false;
    }
    
    /**
     * Return list of entities for current export operation.  Subclasses
     * may override to customize.
     */
    public List<EntityType> getExportEntityList() {
        
        DataModel dataModel = getListDataModel();
        
        List<EntityType> entityList = null;
        if (dataModel instanceof ItemLazyDataModel) {
            ItemLazyDataModel lazyDataModel = (ItemLazyDataModel) dataModel;
            entityList = lazyDataModel.getFilteredEntities();
        } else {
            entityList = getFilteredEntities();
        }
        
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        
        return entityList;
    }
    
    /**
     * Prepares export wizard.
     */
    public String prepareExport() throws CdbException {          
        ItemDomainExportWizard wizard = ItemDomainExportWizard.getInstance();
        wizard.setDomainInfo(getDomainExportInfo());
        return "export?faces-redirect=true";
    }
    
    protected DomainImportExportInfo initializeDomainExportInfo() {
        return null;
    }
    
    public DomainImportExportInfo getDomainExportInfo() {
        if (domainExportInfo == null) {
            domainExportInfo = initializeDomainExportInfo();
        }
        return domainExportInfo;
    }
    
}
