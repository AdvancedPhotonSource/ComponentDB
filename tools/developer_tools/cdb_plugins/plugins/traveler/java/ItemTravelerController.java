/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;

import gov.anl.aps.cdb.portal.controllers.ICdbDomainEntityController;
import gov.anl.aps.cdb.portal.controllers.ItemControllerExtensionHelper;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.controllers.PropertyValueController;
import gov.anl.aps.cdb.portal.controllers.extensions.ItemMultiEditController;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.plugins.support.traveler.api.TravelerApi;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Binder;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.BinderTraveler;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.BinderWorksReference;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.FormContent;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Forms;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.ReleasedForm;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.ReleasedForms;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import javax.faces.model.SelectItem;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;

public abstract class ItemTravelerController extends ItemControllerExtensionHelper {

    protected TravelerApi travelerApi;    
    private static final Logger logger = Logger.getLogger(ItemTravelerController.class.getName());        

    protected static final double FORM_ARCHIVED_STATUS = 2;
    protected static final String FORM_TYPE_DISCREPANCY = "discrepancy"; 
    protected static final String TEMPLATE_PREFERRED_VERSION_ID_KEY = "preferredVer";
    protected static final String TEMPLATE_PREFERRED_CACHE_VER_KEY = "preferredVerStringCache";
    
    protected PropertyValueController propertyValueController; 

    protected PropertyValue propertyValue;
    private Traveler currentTravelerInstance;
    private Binder selectedBinder;
    private DateFormat dateFormat;
    private Date currentTravelerDeadline;
    private final Map statusOptions = new HashMap();
    private final Map statusNames = new HashMap();
    private String currentTravelerSelectedStatus;
    private String currentTravelerEditTitle;
    private String currentTravelerEditDescription;

    private boolean renderAddTravelerToBinderDialog;
    private boolean renderAddNewTravelerToBinderDialog;
    private boolean renderTravelerTemplateLinkDialog;
    private boolean renderTravelerTemplateAddDialog;
    private boolean renderTravelerTemplateUpdatePrefVersionDialog;
    private boolean renderAddNewTravelerDialog;

    private Form selectedTemplate;
    private Form selectedTravelerInstanceTemplate;
    //private ReleasedForms releasedFormsForSelectedTravelerInstanceTemplate;
    //private ReleasedForm selectedReleasedTemplateForTravelerInstance;

    private String travelerTemplateTitle;
    private Forms activeTravelerTemplates;

    private String travelerInstanceTitle;

    private List<Form> availableTemplates;
    private List<Form> defaultTemplates;

    private final String TRAVELER_WEB_APP_URL = TravelerPluginManager.getTravelerWebApplicationUrl();
    private final String TRAVELER_WEB_APP_TEMPLATE_PATH = TravelerPluginManager.getTravelerWebApplicationTemplatePath();
    private final String TRAVELER_WEB_APP_TRAVELER_PATH = TravelerPluginManager.getTravelerWebApplicationTravelerPath();
    private final String TRAVELER_WEB_APP_BINDER_PATH = TravelerPluginManager.getTravelerWebApplicationBinderPath();
    private final String TRAVELER_WEB_APP_TRAVELER_CONFIG_PATH = TravelerPluginManager.getTravelerWebApplicationTravelerConfigPath();

    private List<Form> activeTemplatesForCurrent;
    private List<Form> archivedTemplatesForCurrent;
    private List<BinderTraveler> travelersForCurrent;
    private List<Binder> bindersForCurrent;

    private PropertyType travelerTemplatePropertyType;
    private PropertyType travelerInstancePropertyType;
    private PropertyType travelerBinderPropertyType;

    private Binder newBinder;
    private List<Form> newBinderTemplateSelection;

    // Multi-Edit 
    private Map<String, List<Form>> multiEditTravelerTemplateListForItems;
    private Map<String, List<Traveler>> multiEditTravelerInstanceListForItems;
    protected Form multiEditSelectedTemplate;
    protected List<Form> multiEditAvailableTemplateForApplyAll;

    @PostConstruct
    public void init() {
        getItemController().subscribeResetVariablesForCurrent(this);
        getItemController().getItemMultiEditController().subscribeResetForMultiEdit(this);
        resetExtensionVariablesForCurrent();
        resetExtensionVariablesForMultiEdit();

        String webServiceUrl = TravelerPluginManager.getTravelerWebServiceUrl();
        String username = TravelerPluginManager.getTravelerBasicAuthUsername();
        String password = TravelerPluginManager.getTravelerBasicAuthPassword();
        try {
            travelerApi = new TravelerApi(webServiceUrl, username, password);
        } catch (ConfigurationError ex) {
            String error = "Traveler Service is not accessible:  " + ex.getErrorMessage();
            SessionUtility.addErrorMessage("Error", error);
            logger.error(error);
        }

        dateFormat = new SimpleDateFormat("y-M-d");

        String initalized = "Initialized";
        String active = "Active";
        String submitted = "Submitted";
        String completed = "Complted";
        String frozen = "Frozen";

        statusNames.put(0.0, initalized);
        statusNames.put(1.0, active);
        statusNames.put(1.5, submitted);
        statusNames.put(2.0, completed);
        statusNames.put(3.0, frozen);

        List<SelectItem> initalizedStatusList = new ArrayList<>();
        initalizedStatusList.add(getNewSelectItem(initalized, false));
        initalizedStatusList.add(getNewSelectItem(active, false));
        initalizedStatusList.add(getNewSelectItem(frozen, true));
        initalizedStatusList.add(getNewSelectItem(submitted, true));
        initalizedStatusList.add(getNewSelectItem(completed, true));
        List<SelectItem> activeStatusList = new ArrayList<>();
        activeStatusList.add(getNewSelectItem(initalized, true));
        activeStatusList.add(getNewSelectItem(active, false));
        activeStatusList.add(getNewSelectItem(frozen, false));
        activeStatusList.add(getNewSelectItem(submitted, true));
        activeStatusList.add(getNewSelectItem(completed, true));
        List<SelectItem> submittedStatusList = new ArrayList<>();
        submittedStatusList.add(getNewSelectItem(initalized, true));
        submittedStatusList.add(getNewSelectItem(active, false));
        submittedStatusList.add(getNewSelectItem(frozen, true));
        submittedStatusList.add(getNewSelectItem(submitted, false));
        submittedStatusList.add(getNewSelectItem(completed, false));
        List<SelectItem> completedStatusList = new ArrayList<>();
        completedStatusList.add(getNewSelectItem(initalized, true));
        completedStatusList.add(getNewSelectItem(active, true));
        completedStatusList.add(getNewSelectItem(frozen, true));
        completedStatusList.add(getNewSelectItem(submitted, true));
        completedStatusList.add(getNewSelectItem(completed, false));

        statusOptions.put(0.0, initalizedStatusList);
        statusOptions.put(1.0, activeStatusList);
        statusOptions.put(1.5, submittedStatusList);
        statusOptions.put(2.0, completedStatusList);
        statusOptions.put(3.0, activeStatusList);
    }

    @Override
    public void resetExtensionVariablesForCurrent() {
        super.resetExtensionVariablesForCurrent();
        activeTemplatesForCurrent = null;
        archivedTemplatesForCurrent = null;
        travelersForCurrent = null;
        bindersForCurrent = null;
        propertyValue = null;
        newBinder = null;
        newBinderTemplateSelection = null;
        resetRenderBooleans();
    }

    protected void resetRenderBooleans() {
        renderAddTravelerToBinderDialog = false;
        renderAddNewTravelerToBinderDialog = false;
        renderAddNewTravelerDialog = false;
        renderTravelerTemplateAddDialog = false;
        renderTravelerTemplateLinkDialog = false;
        renderTravelerTemplateUpdatePrefVersionDialog = false;
    }

    @Override
    public void resetExtensionVariablesForMultiEdit() {
        super.resetExtensionVariablesForMultiEdit();
        multiEditTravelerTemplateListForItems = new HashMap<>();
        multiEditTravelerInstanceListForItems = new HashMap<>();
    }

    public void addTravelerBinderToCurrent(String onSuccess) {
        PropertyType travelerBinderPropertyType = getTravelerBinderPropertyType();

        if (travelerBinderPropertyType != null) {
            propertyValue = getItemController().preparePropertyTypeValueAdd(travelerBinderPropertyType);
            newBinder = new Binder();
            newBinderTemplateSelection = new ArrayList<>();
            loadEntityAvailableTemplateList(getCurrent());

            // Load the released list
            for (Form availableTemplate : availableTemplates) {
                loadReleasedTemplatesForTemplate(availableTemplate, true);
            }

            SessionUtility.executeRemoteCommand(onSuccess);
        } else {
            SessionUtility.addErrorMessage("Traveler binder property type not found ",
                    " Please contact your admin to add a property type with traveler binder handler");
        }

    }

    public boolean isSelectedNewBinderTemplate(Form form) {
        return newBinderTemplateSelection.contains(form);
    }

    public void addNewTravelerToBinder(String onSuccessCommand) {
        Traveler instance;
        try {
            instance = createTravelerInstance();
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", "Cannot create new traveler: " + ex.getMessage());
            return;
        }

        setCurrentTravelerInstance(instance);

        try {
            addCurrentTravelerInstanceToSelectedBinder();
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", "Cannot add new traveler to binder: " + ex.getMessage());
            return;
        }

        resetExtensionVariablesForCurrent();
        SessionUtility.executeRemoteCommand(onSuccessCommand);

    }

    public void addTravelerToBinder(String onSuccess) {
        if (currentTravelerInstance != null) {
            if (selectedBinder == null) {
                SessionUtility.addErrorMessage("No Binder Selected", "Please select a binder and try again.");
                return;
            } else {
                // Add traveler to current binder 
                Item current = getCurrent();
                if (getItemController().getItemMultiEditController().performSaveOperationsOnItem(current)) {
                    // Item did not fail saving, save to proceed
                    String currentTravelerId = currentTravelerInstance.getId();
                    try {
                        addCurrentTravelerInstanceToSelectedBinder();
                    } catch (CdbException ex) {
                        logger.error(ex);
                        SessionUtility.addErrorMessage("Failed adding a traveler to binder", ex.getErrorMessage());
                        return;
                    }

                    List<PropertyValue> propertyValueList = getTravelerInstanceTypePropertyValueList();

                    for (int i = 0; i < propertyValueList.size(); i++) {
                        PropertyValue propertyValue = propertyValueList.get(i);

                        if (propertyValue.getValue().equals(currentTravelerId)) {
                            deleteProperty(propertyValue);
                            break;
                        }
                    }

                    SessionUtility.executeRemoteCommand(onSuccess);
                } else {
                    SessionUtility.addErrorMessage("Error", "An error occured attempting to save the item. Will not proceed with move operation.");
                }
            }
        }
    }

    public void addCurrentTravelerInstanceToSelectedBinder() throws CdbException {
        String binderId = selectedBinder.getId();
        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
        String username = currentUser.getUsername();
        String currentTravelerId = currentTravelerInstance.getId();
        String[] travelerIds = new String[1];
        travelerIds[0] = currentTravelerId;

        Binder binder = travelerApi.addWorkToBinder(binderId, travelerIds, username);

        // Replace with latest binder in the lsit 
        if (travelersForCurrent != null) {
            for (int i = 0; i < travelersForCurrent.size(); i++) {
                BinderTraveler travelerBinder = travelersForCurrent.get(i);
                if (travelerBinder instanceof Binder) {
                    if (travelerBinder.getId() == binderId) {
                        travelersForCurrent.remove(i);
                        travelersForCurrent.add(i, binder);
                        break;
                    }
                }
            }
        }

    }

    public String createBinder() {
        // Validataion before submission of anything... 
        String newBinderTitle = newBinder.getTitle();
        if (newBinderTitle == null || newBinderTitle.equals("")) {
            SessionUtility.addErrorMessage("Error",
                    "Please specify a binder title.");
            return null;
        }

        if (newBinderTemplateSelection.size() == 0) {
            SessionUtility.addErrorMessage("Error",
                    "Please select traveler templates to create travelers for the binder.");
            return null;
        } else {
            for (Form template : newBinderTemplateSelection) {
                if (template.getTravelerInstanceName().equals("")) {
                    SessionUtility.addErrorMessage("Error",
                            "Please specify a title for traveler of template: '" + template.getTitle() + "'.");
                    return null;
                }
            }
        }

        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
        String username = currentUser.getUsername();
        try {
            newBinder = travelerApi.createBinder(newBinderTitle,
                    newBinder.getDescription(), username);

            boolean failedValidation = false;

            for (int i = 0; i < newBinderTemplateSelection.size(); i++) {
                Form selectedTemplate = newBinderTemplateSelection.get(i);

                ReleasedForm releasedForm = selectedTemplate.getSelectedReleasedForm();
                if (releasedForm == null) {
                    failedValidation = true;
                    SessionUtility.addWarningMessage("No Released Version specified",
                            "Please specify released version for: " + selectedTemplate.getTitle());
                }
            }

            if (failedValidation) {
                return null;
            }

            String[] travelerIds = new String[newBinderTemplateSelection.size()];

            for (int i = 0; i < newBinderTemplateSelection.size(); i++) {
                Form selectedTemplate = newBinderTemplateSelection.get(i);
                String templateId = selectedTemplate.getSelectedReleasedForm().getId();

                String travelerName = selectedTemplate.getTravelerInstanceName();
                Traveler newTraveler = createActiveTraveler(templateId, travelerName);
                travelerIds[i] = newTraveler.getId();
            }

            String newBinderId = newBinder.getId();
            travelerApi.addWorkToBinder(newBinderId, travelerIds, username);

            propertyValue.setValue(newBinderId);
            this.savePropertyList();

            return SessionUtility.getRedirectToCurrentView();

        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
        return null;
    }

    public void destroyTravelerTemplateFromCurrent(Form travelerTemplate) {
        List<PropertyValue> propertyValueInternalList = getCurrent().getPropertyValueInternalList();
        for (PropertyValue propertyValue : propertyValueInternalList) {
            PropertyTypeHandler propertyTypeHandler = propertyValue.getPropertyType().getPropertyTypeHandler();
            if (propertyTypeHandler != null) {
                if (propertyTypeHandler.getName().equals(TravelerTemplatePropertyTypeHandler.HANDLER_NAME)) {
                    if (propertyValue.getValue().equals(travelerTemplate.getId())) {
                        setCurrentEditPropertyValue(propertyValue);
                        deleteCurrentEditPropertyValue();
                        activeTemplatesForCurrent.remove(travelerTemplate);
                        break;
                    }
                }
            }
        }
    }

    public void addTravelerInstanceToCurrent(String onSuccess) {
        PropertyType travelerInstancePropertyType = getTravelerInstancePropertyType();

        if (travelerInstancePropertyType != null) {
            propertyValue = getItemController().preparePropertyTypeValueAdd(travelerInstancePropertyType);
            loadEntityAvailableTemplateList(getCurrent());
            prepareShowAddNewTravelerDialog();
            if (onSuccess != null) {
                SessionUtility.executeRemoteCommand(onSuccess);
            }
        } else {
            SessionUtility.addErrorMessage("Traveler instance property type not found ",
                    " Please contact your admin to add a property type with traveler instance handler");
        }
    }

    public void addTravelerTemplateToNewCurrentItem(Item currentItem) {
        setCurrent(currentItem);
        addTravelerTemplateToCurrent(null);
    }

    public void prepareMultiEditAppplyInstanceToAllItems() {
        List<Item> selectedItemsToEdit = getItemController().getItemMultiEditController().getSelectedItemsToEdit();
        multiEditAvailableTemplateForApplyAll = null;

        for (Item item : selectedItemsToEdit) {
            if (multiEditAvailableTemplateForApplyAll == null) {
                // Get initial list 
                multiEditAvailableTemplateForApplyAll = new ArrayList<>();
                loadEntityActiveAvailableTemplateList(item, multiEditAvailableTemplateForApplyAll);
            } else if (multiEditAvailableTemplateForApplyAll.size() == 0) {
                // No more common templates will be found since the list is empty. 
                break;
            } else {
                List<Form> ittrTemplateList = new ArrayList<>();
                loadEntityActiveAvailableTemplateList(item, ittrTemplateList);

                List<Form> templatesToKeep = new ArrayList<>();

                for (Form form : multiEditAvailableTemplateForApplyAll) {
                    for (Form ittr : ittrTemplateList) {
                        if (form.getId().equals(ittr.getId())) {
                            templatesToKeep.add(form);
                            break;
                        }
                    }
                }

                multiEditAvailableTemplateForApplyAll = templatesToKeep;
            }
        }
    }

    public void createTravelerInstanceForEachSelectedItem(String onSuccess) {
        if (selectedTravelerInstanceTemplate != null) {
            if (travelerInstanceTitle != null && !travelerInstanceTitle.isEmpty()) {
                List<Item> selectedItemsToEdit = getItemController().getItemMultiEditController().getSelectedItemsToEdit();
                for (int i = 0; i < selectedItemsToEdit.size(); i++) {
                    Item item = selectedItemsToEdit.get(i);
                    setCurrent(item);
                    saveMultiEditTravelerInstance(null);
                }
            } else {
                SessionUtility.addWarningMessage("e-Traveler title empty", "Please provide a traveler instance title before proceeding.");
                return;
            }
        } else {
            SessionUtility.addWarningMessage("Template not selected", "Please select a template before proceeding.");
            return;
        }

        SessionUtility.executeRemoteCommand(onSuccess);
    }

    public void saveMultiEditTravelerInstance(String onSuccessCommand) {
        Item current = getCurrent();
        List<Item> selectedItemsToEdit = getItemController().getItemMultiEditController().getSelectedItemsToEdit();
        int index = -1;
        for (int i = 0; i < selectedItemsToEdit.size(); i++) {
            if (current == selectedItemsToEdit.get(i)) {
                index = i;
                break;
            }
        }

        if (getItemController().getItemMultiEditController().performSaveOperationsOnItem(current)) {
            // Save to proceed with creation of traveler instance. 
            addTravelerInstanceToCurrent(null);
            createTravelerInstance(getItemController(), onSuccessCommand);
        }

        if (index != -1) {
            selectedItemsToEdit.remove(index);
            selectedItemsToEdit.add(index, getCurrent());
        }

    }

    public void prepareAddTravelerInstanceOnMultiEdit(Item currentItem) {
        setCurrent(currentItem);
        addTravelerInstanceToCurrent(null);
    }

    public void addTravelerTemplateToCurrent(String onSuccess) {
        resetRenderBooleans();
        renderTravelerTemplateAddDialog = true;
        renderTravelerTemplateLinkDialog = true;

        PropertyType travelerTemplatePropertyType = getTravelerTemplatePropertyType();

        if (travelerTemplatePropertyType != null) {
            propertyValue = getItemController().preparePropertyTypeValueAdd(travelerTemplatePropertyType);
            if (onSuccess != null) {
                SessionUtility.executeRemoteCommand(onSuccess);
            }
        } else {
            SessionUtility.addErrorMessage("Traveler template property type not found ",
                    " Please contact your admin to add a property type with traveler template handler");
        }
    }

    public String getTravelerTemplateURL(String id) {
        String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TEMPLATE_PATH;
        return travelerInstanceUrl.replace("FORM_ID", id);
    }

    public PropertyType getTravelerBinderPropertyType() {
        if (travelerBinderPropertyType == null) {
            String travelerBinderPropertyTypeHandlerName = TravelerBinderPropertyTypeHandler.HANDLER_NAME;
            travelerBinderPropertyType = getFirstPropertyTypeWithHandler(travelerBinderPropertyTypeHandlerName);
        }

        return travelerBinderPropertyType;
    }

    public PropertyType getTravelerInstancePropertyType() {
        if (travelerInstancePropertyType == null) {
            String travelerInstancePropertyTypeHandlerName = TravelerInstancePropertyTypeHandler.HANDLER_NAME;
            travelerInstancePropertyType = getFirstPropertyTypeWithHandler(travelerInstancePropertyTypeHandlerName);
        }
        return travelerInstancePropertyType;
    }

    public PropertyType getTravelerTemplatePropertyType() {
        if (travelerTemplatePropertyType == null) {
            String travelerTemplateHandlerName = TravelerTemplatePropertyTypeHandler.HANDLER_NAME;
            travelerTemplatePropertyType = getFirstPropertyTypeWithHandler(travelerTemplateHandlerName);
        }
        return travelerTemplatePropertyType;
    }

    public PropertyType getFirstPropertyTypeWithHandler(String propertyTypeHandlerName) {
        List<PropertyType> availableItems = PropertyTypeController.getInstance().getAvailableItems();
        for (PropertyType propertyType : availableItems) {
            PropertyTypeHandler propertyTypeHandler = propertyType.getPropertyTypeHandler();
            if (propertyTypeHandler != null) {
                if (propertyTypeHandler.getName().equals(propertyTypeHandlerName)) {
                    return propertyType;
                }
            }
        }
        return null;
    }

    public void loadTravelerInstancesForMultiEditItem(Item item) {
        List<Traveler> instanceList = new ArrayList<>();

        if (item.getPropertyValueInternalList() != null) {
            loadPropertyTravelerInstanceList(item.getPropertyValueInternalList(), instanceList);
        }

        multiEditTravelerInstanceListForItems.put(item.getViewUUID(), instanceList);
    }

    public List<Traveler> getInstancesForMultiEditItem(Item item) {
        try {
            return multiEditTravelerInstanceListForItems.get(item.getViewUUID());
        } catch (Exception ex) {
            return null;
        }
    }

    public void applyMultiEditSelectedTemplateToAllSelectedItems() {
        ItemMultiEditController itemMultiEditController = getItemController().getItemMultiEditController();
        List<Item> selectedItemsToEdit = itemMultiEditController.getSelectedItemsToEdit();
        PropertyType travelerTemplatePropertyType = getTravelerTemplatePropertyType();

        for (Item item : selectedItemsToEdit) {
            loadTemplatesFormMultiEditItem(item);
            List<Form> templatesForMultiEditItem = getTemplatesForMultiEditItem(item);
            if (checkSelectedTemplate(templatesForMultiEditItem, multiEditSelectedTemplate)) {
                PropertyValue propValue = getItemController().preparePropertyTypeValueAdd(item, travelerTemplatePropertyType);
                propValue.setValue(multiEditSelectedTemplate.getId());
            }
        }
    }

    public void loadTemplatesFormMultiEditItem(Item item) {
        List<Form> templateList = new ArrayList<>();

        if (item.getPropertyValueInternalList() != null) {
            loadPropertyTravelerTemplateList(item.getPropertyValueInternalList(), templateList);
        }

        multiEditTravelerTemplateListForItems.put(item.getViewUUID(), templateList);
    }

    public List<Form> getTemplatesForMultiEditItem(Item item) {
        try {
            return multiEditTravelerTemplateListForItems.get(item.getViewUUID());
        } catch (Exception ex) {
            return null;
        }
    }

    public List<Binder> getBindersForCurrent() {
        if (bindersForCurrent == null) {
            bindersForCurrent = new ArrayList<>();
            loadPropertyTravelerBinderList(getCurrent().getPropertyValueInternalList(), bindersForCurrent);
        }
        return bindersForCurrent;
    }

    private void loadTravelerTemplatesForCurrent() {
        if (activeTemplatesForCurrent == null || archivedTemplatesForCurrent == null) {
            List<Form> allTemplatesForCurrent = new ArrayList<>();
            activeTemplatesForCurrent = new ArrayList<>();
            archivedTemplatesForCurrent = new ArrayList<>();
            loadPropertyTravelerTemplateList(getCurrent().getPropertyValueInternalList(), allTemplatesForCurrent);

            for (Form form : allTemplatesForCurrent) {
                double status = form.getStatus();
                if (status == FORM_ARCHIVED_STATUS) {
                    archivedTemplatesForCurrent.add(form);
                } else {
                    activeTemplatesForCurrent.add(form);
                }
            }
        }
    }

    public List<Form> getTemplatesForCurrent() {
        loadTravelerTemplatesForCurrent();
        return activeTemplatesForCurrent;
    }

    public List<Form> getArchivedTemplatesForCurrent() {
        loadTravelerTemplatesForCurrent();
        return archivedTemplatesForCurrent;
    }

    public boolean isRenderAddTravelerToBinderDialog() {
        return renderAddTravelerToBinderDialog;
    }

    public void prepareShowAddTravelerToBinderDialog() {
        resetRenderBooleans();
        renderAddTravelerToBinderDialog = true;
    }

    public boolean isRenderAddNewTravelerToBinderDialog() {
        return renderAddNewTravelerToBinderDialog;
    }

    public void prepareShowAddNewTravelerToBinderDialog() {
        resetRenderBooleans();
        loadEntityAvailableTemplateList(getCurrent());
        renderAddNewTravelerToBinderDialog = true;
    }

    public void prepareShowAddNewTravelerDialog() {
        resetRenderBooleans();
        renderAddNewTravelerDialog = true;
    }

    public boolean isRenderAddNewTravelerDialog() {
        return renderAddNewTravelerDialog;
    }

    public boolean isRenderTravelerTemplateLinkDialog() {
        return renderTravelerTemplateLinkDialog;
    }

    public boolean isRenderTravelerTemplateAddDialog() {
        return renderTravelerTemplateAddDialog;
    }

    public boolean isRenderTravelerTemplateUpdatePrefVersionDialog() {
        return renderTravelerTemplateUpdatePrefVersionDialog;
    }

    public List<BinderTraveler> getTravelersForCurrent() {
        if (travelersForCurrent == null) {
            travelersForCurrent = new ArrayList<>();
            List<Traveler> travelerList = new ArrayList<>();
            loadPropertyTravelerInstanceList(getCurrent().getPropertyValueInternalList(), travelerList);

            List<Binder> binderList = new ArrayList<>();
            loadPropertyTravelerBinderList(getCurrent().getPropertyValueInternalList(), binderList);

            travelersForCurrent.addAll(travelerList);
            travelersForCurrent.addAll(binderList);
        }
        return travelersForCurrent;
    }

    public void loadTravelerListForBinder(Binder binder) {
        List<Traveler> travelerList = binder.getTravelerList();
        if (travelerList == null) {
            travelerList = new ArrayList<>();
            for (BinderWorksReference work : binder.getWorks()) {
                String travelerId = work.getId();
                addTravelerFromTravelerId(travelerId, travelerList);
            }
            binder.setTravelerList(travelerList);
        }
    }

    public String getFormName(BinderTraveler binderTraveler) {
        if (binderTraveler.getFormName() == null) {
            if (binderTraveler instanceof Traveler) {
                Traveler traveler = (Traveler) binderTraveler;
                try {
                    Form form = travelerApi.getForm(traveler.getReferenceForm());
                    traveler.setFormName(form.getTitle());
                } catch (Exception ex) {
                    traveler.setFormName(traveler.getReferenceForm());
                }
            }
        }
        return binderTraveler.getFormName();
    }

    private SelectItem getNewSelectItem(String label, Boolean disabled) {
        return new SelectItem(label, label, label, disabled);
    }

    /**
     * Checks for property value and displays proper error message when null.
     * Used to determine if a function should execute.
     *
     * @return boolean that determines if function should execute.
     */
    private boolean checkPropertyValue() {
        if (propertyValue != null) {
            return true;
        } else {
            SessionUtility.addErrorMessage("Error", "Property value object is missing");
            logger.error("Property value object is missing");
            return false;
        }
    }

    /**
     * Checks for template and displays error message when null. Used to
     * determine if a function should execute.
     *
     * @param template Template variable that needs to be checked.
     * @return boolean that determines if function should execute.
     */
    private boolean checkSelectedTemplate(Form template) {
        return checkSelectedTemplate(getTemplatesForCurrent(), template);
    }

    /**
     * Checks for template and displays error message when null. Used to
     * determine if a function should execute.
     *
     * @param templatesList list of templates to look through for the template
     * @param template Template variable that needs to be checked.
     * @return boolean that determines if function should execute.
     */
    private boolean checkSelectedTemplate(List<Form> templatesList, Form template) {
        if (template != null) {
            if (template.getId() != null) {
                for (Form ittrTemplate : templatesList) {
                    if (template.getId().equals(ittrTemplate.getId())) {
                        SessionUtility.addWarningMessage("Template has already been added to current item", "Selected traveler template is already a part of current item.");
                        return false;
                    }
                }
            }
            return true;
        } else {
            SessionUtility.addWarningMessage("No Template Selected", "Traveler Template is not selected");
            logger.error("Traveler Template is not selected");
            return false;
        }
    }

    public void createTravelerTemplateAndSelectNewTemplate(String onSuccess) {
        try {
            multiEditSelectedTemplate = createTravelerTemplate();
            SessionUtility.executeRemoteCommand(onSuccess);
        } catch (CdbException ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            logger.error(ex);
        }
    }

    public Form createTravelerTemplate() throws CdbException {
        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
        return travelerApi.createForm(travelerTemplateTitle, currentUser.getUsername(), "");
    }

    /**
     * Creates a traveler template or Form. Uses traveler API to create a
     * traveler template or Form. Assigns the newly created id with property and
     * saves.
     *
     * @param entityController controller for the entity currently being edited
     * by the user.
     * @param onSuccessCommand Remote command to execute only on successful
     * completion.
     */
    public void createTravelerTemplate(ICdbDomainEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {

            if (!travelerTemplateTitle.equals("") && travelerTemplateTitle != null) {
                Form form;
                try {
                    form = createTravelerTemplate();
                    SessionUtility.addInfoMessage("Template Created", "Traveler Template '" + form.getId() + "' has been created");
                    propertyValue.setValue(form.getId());
                    if (getCurrent().getId() != null) {
                        entityController.savePropertyList();
                    }
                    SessionUtility.executeRemoteCommand(onSuccessCommand);
                } catch (CdbException ex) {
                    SessionUtility.addErrorMessage("Error", ex.getMessage());
                    logger.error(ex);
                }
            } else {
                SessionUtility.addWarningMessage("Template Title Missing", "Please specify a traveler template title.");
            }
        }
    }

    /**
     * Links to an existing traveler template. Uses an traveler template or Form
     * fetched from traveler web service to assign id to property value and
     * saves.
     *
     * @param entityController controller for the entity currently being edited
     * by the user.
     * @param onSuccessCommand Remote command to execute only on successful
     * completion.
     */
    public void linkTravelerTemplate(String onSuccessCommand) {
        if (checkPropertyValue()) {
            if (checkSelectedTemplate(selectedTemplate)) {
                saveSelectedTemplatePropertyValue(onSuccessCommand);
            }
        }
    }

    public void saveSelectedTemplatePropertyValue(String onSuccessCommand) {
        propertyValue.setValue(selectedTemplate.getId());

        ReleasedForm selectedReleasedForm = selectedTemplate.getSelectedReleasedForm();
        if (selectedReleasedForm != null) {
            if (!selectedReleasedForm.isItemFake()) {
                // Valid form selected 
                propertyValue.setPropertyMetadataValue(TEMPLATE_PREFERRED_VERSION_ID_KEY, selectedReleasedForm.getId());
                propertyValue.setPropertyMetadataValue(TEMPLATE_PREFERRED_CACHE_VER_KEY, selectedReleasedForm.getVer());
            } else {
                PropertyMetadata md1 = propertyValue.getPropertyMetadataForKey(TEMPLATE_PREFERRED_VERSION_ID_KEY);
                PropertyMetadata md2 = propertyValue.getPropertyMetadataForKey(TEMPLATE_PREFERRED_CACHE_VER_KEY);
                
                PropertyValueController propertyValueController = getPropertyValueController();
                String removeMessage = "Preference Removed. Will automatically fetch latest."; 
                propertyValueController.removePropertyMetadata(md1, removeMessage);
                propertyValueController.removePropertyMetadata(md2, null);
            }
        }

        if (getCurrent().getId() != null) {
            savePropertyList();
        }
        SessionUtility.executeRemoteCommand(onSuccessCommand);
    }

    /**
     * Removes the id associated with template property.
     *
     * @param onSuccessCommand Remote command to execute only on successful
     * completion.
     */
    public void unlinkTravelerTemplate(String onSuccessCommand) {
        if (checkPropertyValue()) {
            travelerTemplateTitle = "";
            propertyValue.setValue("");
            SessionUtility.executeRemoteCommand(onSuccessCommand);
        }
    }

    /**
     * Generates a (traveler template)/form view URL using id and CDB
     * properties.
     *
     * @param formId traveler template or form id to generate URL for.
     * @return generated URL to traveler web application.
     */
    public String getTravelerTemplateUrl(String formId) {
        String travelerTemplateUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TEMPLATE_PATH;
        return travelerTemplateUrl.replace("FORM_ID", formId);
    }

    /**
     * Generates a (traveler instance)/traveler view URL using id and CDB
     * properties. Uses currentTravlerInstance variable and its ID along with
     * CDB properties to generate URL.
     *
     * @return generated URL to traveler web application.
     */
    public String getBinderTravelerUrl(BinderTraveler binderTraveler) {
        String binderTravelerInstanceUrl = TRAVELER_WEB_APP_URL;
        String id = binderTraveler.getId();
        if (binderTraveler instanceof Traveler) {
            binderTravelerInstanceUrl += TRAVELER_WEB_APP_TRAVELER_PATH;
            return binderTravelerInstanceUrl.replace("TRAVELER_ID", id);
        } else {
            binderTravelerInstanceUrl += TRAVELER_WEB_APP_BINDER_PATH;
            return binderTravelerInstanceUrl.replace("BINDER_ID", id);
        }
    }

    /**
     * Generates a (traveler instance)/traveler configuration URL using id and
     * CDB properties. Uses currentTravlerInstance variable and its ID along
     * with CDB properties to generate URL.
     *
     * @return generated configuration URL to traveler web application.
     */
    public String getCurrentTravelerInstanceConfigUrl() {
        if (currentTravelerInstance != null) {
            String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TRAVELER_CONFIG_PATH;
            return travelerInstanceUrl.replace("TRAVELER_ID", currentTravelerInstance.getId());
        }
        return "";
    }

    /**
     * Gets a string representation of status stored in traveler db. Uses
     * currentTravelerInstance variable and its status to generate string
     * status.
     *
     * @return string representation of status.
     */
    public String getCurrentTravelerStatus() {
        if (currentTravelerInstance != null) {
            return (String) statusNames.get(currentTravelerInstance.getStatus());
        }
        return "";
    }

    public List<String> getCurrentTravelerStatusOptions() {
        if (currentTravelerInstance != null) {
            return (List<String>) statusOptions.get(currentTravelerInstance.getStatus());
        }
        return null;
    }

    public Double getStatusKey(String statusName) {
        Iterator statusNamesIterator = statusNames.entrySet().iterator();

        while (statusNamesIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) statusNamesIterator.next();
            if (entry.getValue().equals(statusName)) {
                return (Double) entry.getKey();
            }
        }

        return -1.0;
    }

    /**
     * Determines if the current owner owns the traveler instance. Uses
     * currentTravlerInstance to determine if current user owns it. Currently,
     * the traveler could only be configured by user who owns it.
     *
     * @return boolean, if user can configure the traveler instance.
     */
    public boolean getCurrentTravelerConfigPermission() {
        if (currentTravelerInstance != null) {
            String travelerUser = currentTravelerInstance.getCreatedBy();
            UserInfo currentUser = (UserInfo) SessionUtility.getUser();
            if (currentUser != null && travelerUser.equals(currentUser.getUsername())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a (traveler instance)/traveler title. Uses the traveler web API to
     * get a title. Default text is returned if fetch fails. returned value is
     * used for a link that user may click to get to traveler.
     *
     * @param propertyValue value of propertyValue holds an id to a traveler
     * instance.
     * @return String that will be displayed to user on link to traveler
     * instance.
     */
    public String getTravelerInstanceTitle(PropertyValue propertyValue) {
        if (propertyValue.getDisplayValue() != null) {
            return propertyValue.getDisplayValue();
        }
        try {
            Traveler traveler = travelerApi.getTraveler(propertyValue.getValue());
            propertyValue.setDisplayValue(traveler.getTitle());
            return traveler.getTitle();
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
        return "Go to Traveler Instance";

    }

    /**
     * Loads default templates. Stored as allowed value in the template property
     * type.
     *
     * @return
     */
    private List<Form> getDefaultTemplates() {
        if (defaultTemplates == null) {
            defaultTemplates = new ArrayList<>();
            PropertyType templatePropertyType = getTravelerTemplatePropertyType();
            List<AllowedPropertyValue> allowedPropertyValueList = templatePropertyType.getAllowedPropertyValueList();
            for (AllowedPropertyValue allowedValue : allowedPropertyValueList) {
                String templateId = allowedValue.getValue();
                addFormFromPropertyValue(templateId, defaultTemplates);
            }
        }
        return defaultTemplates;
    }

    /**
     * Determines all entities that need to have (traveler templates)/forms
     * loaded
     *
     * @param entityController controller for the entity currently being edited
     * by the user.
     */
    public void loadEntityActiveAvailableTemplateList(CdbDomainEntity domainEntity, List<Form> outTemplateList) {
        List<Form> templateList = new ArrayList();
        templateList.addAll(getDefaultTemplates());

        if (domainEntity instanceof Item) {
            Item item = (Item) domainEntity;
            loadPropertyTravelerTemplateList(item.getPropertyValueInternalList(), templateList);
            if (item.getDerivedFromItem() != null) {
                loadPropertyTravelerTemplateList(item.getDerivedFromItem().getPropertyValueInternalList(), templateList);
            }
        } else if (domainEntity instanceof ItemElement) {
            ItemElement itemElement = (ItemElement) domainEntity;
            loadPropertyTravelerTemplateList(itemElement.getPropertyValueList(), templateList);
            Item parentItem = itemElement.getParentItem();
            Item containedItem = itemElement.getContainedItem();
            loadPropertyTravelerTemplateList(parentItem.getPropertyValueInternalList(), templateList);
            loadPropertyTravelerTemplateList(containedItem.getPropertyValueInternalList(), templateList);
        }

        for (Form template : templateList) {
            if (template.getStatus() != FORM_ARCHIVED_STATUS) {
                outTemplateList.add(template);
            }
        }

    }

    /**
     * Determines all entities that need to have (traveler templates)/forms
     * loaded
     *
     * @param entityController controller for the entity currently being edited
     * by the user.
     */
    public void loadEntityAvailableTemplateList(CdbDomainEntity domainEntity) {
        availableTemplates = new ArrayList<>();
        loadEntityActiveAvailableTemplateList(domainEntity, availableTemplates);
    }

    /**
     * Check all property values for templates and call function that add each
     * one.
     *
     * @param propertyValues List of properties for a specific entity.
     * @param formList List of (traveler templates)/forms that will be displayed
     * to the user.
     */
    private void loadPropertyTravelerTemplateList(List<PropertyValue> propertyValues, List<Form> formList) {
        propertyValues = getTemplatePropertyValueList(propertyValues);
        if (propertyValues != null) {
            for (PropertyValue curPropertyValue : propertyValues) {
                addFormFromPropertyValue(curPropertyValue.getValue(), formList, curPropertyValue);
            }
        }
    }

    private List<PropertyValue> getTemplatePropertyValueListForCurrent() {
        return getTemplatePropertyValueList(getCurrent().getPropertyValueInternalList());
    }

    private List<PropertyValue> getTemplatePropertyValueList(List<PropertyValue> propertyValues) {
        if (propertyValues != null) {
            List<PropertyValue> pvList = new ArrayList<>();
            for (PropertyValue curPropertyValue : propertyValues) {
                // Check that they use the traveler template handler. 
                if (curPropertyValue.getPropertyType().getPropertyTypeHandler() != null) {
                    if (curPropertyValue.getPropertyType().getPropertyTypeHandler().getName().equals(TravelerTemplatePropertyTypeHandler.HANDLER_NAME)) {
                        pvList.add(curPropertyValue);
                    }
                }
            }
            return pvList;
        }
        return null;
    }

    private void addFormFromPropertyValue(String formId, List<Form> formList) {
        addFormFromPropertyValue(formId, formList, null);
    }

    /**
     * Load the form from web service using its ID and add to list.
     *
     * @param formId id of the (traveler template)/form to fetch from web
     * service
     * @param formList List of (traveler templates)/forms that will be displayed
     * to the user.
     */
    private void addFormFromPropertyValue(String formId, List<Form> formList, PropertyValue pv) {
        if (formId == null || formId.equals("")) {
            return;
        }
        try {
            Form form = travelerApi.getForm(formId);
            if (pv != null) {
                String verId = pv.getPropertyMetadataValueForKey(TEMPLATE_PREFERRED_VERSION_ID_KEY);
                String verCache = pv.getPropertyMetadataValueForKey(TEMPLATE_PREFERRED_CACHE_VER_KEY);
                form.setPreferredReleasedId(verId);
                form.setPreferredReleasedVerCache(verCache);
            }
            formList.add(form);
        } catch (CdbException ex) {
            Form form = new Form(formId);
            form.setTitle("Error fetching id: " + formId);
            formList.add(form);
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
    }

    /**
     * Check all property values for traveler instances and call function that
     * add each one.
     *
     * @param propertyValues List of properties for a specific entity.
     * @param travelerList List of (traveler templates)/forms that will be
     * displayed to the user.
     */
    private void loadPropertyTravelerInstanceList(List<PropertyValue> propertyValues, List<Traveler> travelerList) {
        List<PropertyValue> travelerInstanceTypePropertyValueList = getTravelerInstanceTypePropertyValueList(propertyValues);
        for (PropertyValue pv : travelerInstanceTypePropertyValueList) {
            addTravelerFromTravelerId(pv.getValue(), travelerList);
        }
    }

    protected List<PropertyValue> getBinderPropertyValueList() {
        return getBinderPropertyValueList(getCurrent().getPropertyValueInternalList());
    }

    protected List<PropertyValue> getTravelerInstanceTypePropertyValueList() {
        return getTravelerInstanceTypePropertyValueList(getCurrent().getPropertyValueInternalList());
    }

    protected List<PropertyValue> getBinderPropertyValueList(List<PropertyValue> propertyValues) {
        return getTravelerPropertyValueList(propertyValues, TravelerBinderPropertyTypeHandler.HANDLER_NAME);
    }

    protected List<PropertyValue> getTravelerInstanceTypePropertyValueList(List<PropertyValue> propertyValues) {
        return getTravelerPropertyValueList(propertyValues, TravelerInstancePropertyTypeHandler.HANDLER_NAME);
    }

    private List<PropertyValue> getTravelerPropertyValueList(List<PropertyValue> propertyValues, String handlerName) {
        List<PropertyValue> propertyValueList = new ArrayList<>();
        for (PropertyValue curPropertyValue : propertyValues) {
            // Check that they use the traveler template handler. 
            if (curPropertyValue.getPropertyType().getPropertyTypeHandler() != null) {
                if (curPropertyValue.getPropertyType().getPropertyTypeHandler().getName().equals(handlerName)) {
                    propertyValueList.add(curPropertyValue);
                }
            }
        }
        return propertyValueList;
    }

    private void loadPropertyTravelerBinderList(List<PropertyValue> propertyValues, List<Binder> binderList) {
        List<PropertyValue> propertyValueList = getBinderPropertyValueList(propertyValues);

        for (PropertyValue curPropertyValue : propertyValueList) {
            String binderId = curPropertyValue.getValue();
            addBinderFromBinderId(binderId, binderList);
        }
    }

    protected void addBinderFromBinderId(String binderId, List<Binder> binderList) {
        try {
            Binder binder = travelerApi.getBinder(binderId);
            binderList.add(binder);
        } catch (Exception ex) {
            Binder binder = new Binder(binderId);
            binder.setTitle("Error fetching id: " + binderId);
            binderList.add(binder);
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());

        }
    }

    /**
     * Load the traveler from web service using its ID and add to list.
     *
     * @param travelerId id of the (traveler instance)/traveler to fetch from
     * web service
     * @param travelerList List of (traveler instances)/travelers that will be
     * displayed to the user.
     */
    private void addTravelerFromTravelerId(String travelerId, List<Traveler> travelerList) {
        if (travelerId == null || travelerId.equals("")) {
            return;
        }
        try {
            travelerList.add(travelerApi.getTraveler(travelerId));
        } catch (CdbException ex) {
            Traveler traveler = new Traveler(travelerId);
            traveler.setTitle("Error fetching id: " + travelerId);
            travelerList.add(traveler);
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
    }

    /**
     * Generates a name to be used in traveler application. Allows the traveler
     * application to request information from CDB.
     *
     * @param deviceObject Item or ItemElement that will get traveler instance
     * property.
     * @return device name for traveler application.
     * @throws CdbException when non valid object was submitted for device name.
     */
    private String getDeviceName(Object deviceObject) throws CdbException {
        if (deviceObject instanceof Item) {
            return "item:" + ((Item) deviceObject).getId();
        } else if (deviceObject instanceof ItemElement) {
            return "itemElement:" + ((ItemElement) deviceObject).getId();
        } else {
            throw new CdbException("Device for traveler is not of valid CDB entity");
        }
    }

    /**
     * Create a traveler instance based on the currently selected template and
     * entered title.
     *
     * @param entityController controller for the entity currently being edited
     * by the user.
     * @param onSuccessCommand Remote command to execute only on successful
     * completion
     */
    public void createTravelerInstance(ICdbDomainEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {
            if (checkSelectedTemplate(selectedTravelerInstanceTemplate)) {
                if (!travelerInstanceTitle.equals("") || travelerInstanceTitle != null) {
                    try {
                        ReleasedForm selectedReleasedForm = selectedTravelerInstanceTemplate.getSelectedReleasedForm();
                        if (selectedReleasedForm != null) {
                            FormContent formContent = selectedReleasedForm.getBase();
                            String id = formContent.getId();
                            if (!id.equals(selectedTravelerInstanceTemplate.getId())) {
                                selectedReleasedForm = null;
                            }
                        }
                        if (selectedReleasedForm == null) {
                            throw new CdbException("Invalid released template selected.");
                        }

                        Traveler travelerInstance = createTravelerInstance();

                        setCurrentTravelerInstance(travelerInstance);

                        SessionUtility.addInfoMessage(
                                "Traveler Instance Created",
                                "Traveler Instance '" + travelerInstance.getId() + "' has been created");

                        propertyValue.setValue(travelerInstance.getId());
                        entityController.savePropertyList();
                        if (onSuccessCommand != null) {
                            SessionUtility.executeRemoteCommand(onSuccessCommand);
                        }
                    } catch (CdbException ex) {
                        logger.error(ex);
                        SessionUtility.addErrorMessage("Error", ex.getMessage());
                    }
                } else {
                    SessionUtility.addWarningMessage("No title", "Please enter a title for a traveler instance");
                    logger.error("No title for traveler instance was provided");
                }

            }
        }
    }

    private Traveler createTravelerInstance() throws CdbException {
        if (checkSelectedTemplate(selectedTravelerInstanceTemplate)) {
            if (!travelerInstanceTitle.equals("") || travelerInstanceTitle != null) {
                ReleasedForm selectedReleasedForm = selectedTravelerInstanceTemplate.getSelectedReleasedForm();
                return createActiveTraveler(selectedReleasedForm.getId(),
                        travelerInstanceTitle);
            } else {
                throw new CdbException("Traveler instance title has not been specified");
            }
        } else {
            throw new CdbException("Traveler template has not been selected.");
        }
    }

    private Traveler createActiveTraveler(String templateId, String travelerName) throws CdbException {
        String device = getDeviceName(this.getCurrent());
        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
        String userName = currentUser.getUsername();

        Traveler travelerInstance = travelerApi.createTraveler(
                templateId,
                userName,
                travelerName,
                device);

        String travelerId = travelerInstance.getId();
        travelerInstance = travelerApi.updateTraveler(travelerId,
                userName, travelerName,
                "", null, getStatusKey("Active"));

        return travelerInstance;
    }

    /**
     * Load information about the traveler instance property currently loaded.
     * Add progress information based on its information which will be displayed
     * to user.
     *
     * @param onSuccessCommand Remote command to execute only on successful
     * completion
     */
    public void reloadCurrentTravelerInstance(String onSuccessCommand) {
        try {
            Traveler travelerInstance = travelerApi.getTraveler(currentTravelerInstance.getId());
            setCurrentTravelerInstance(travelerInstance);
            //Show the GUI since all execution was successful. 
            SessionUtility.executeRemoteCommand(onSuccessCommand);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
    }

    /**
     * Get a list of all (traveler templates)/forms for user to view.
     *
     * @param onSuccessCommand Remote command to execute only on successful
     * completion
     */
    public void loadActiveTravelerTemplates(String onSuccessCommand) {
        try {
            Forms allForms = travelerApi.getForms();

            LinkedList<Form> activeForms = new LinkedList<>();
            activeTravelerTemplates = new Forms();
            activeTravelerTemplates.setForms(activeForms);

            for (Form template : allForms.getForms()) {
                if (template.getStatus() == FORM_ARCHIVED_STATUS) {
                    continue;
                }
                if (template.getFormType().equals(FORM_TYPE_DISCREPANCY)) {
                    continue;
                }
                activeForms.add(template);
            }

            if (onSuccessCommand != null) {
                SessionUtility.executeRemoteCommand(onSuccessCommand);
            }
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }

    }

    /**
     * Load information about the traveler template property currently loaded.
     */
    public void loadTravelerTemplateInformation() {
        if (propertyValue != null) {
            if (!propertyValue.getValue().equals("") || propertyValue.getDisplayValue() == null) {
                try {
                    TravelerTemplatePropertyTypeHandler travelerTemplatePropertyTypeHandler
                            = (TravelerTemplatePropertyTypeHandler) PropertyTypeHandlerFactory.getHandler(propertyValue);
                    travelerTemplatePropertyTypeHandler.setDisplayValue(propertyValue);
                    travelerTemplateTitle = propertyValue.getDisplayValue();
                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        }

    }

    public void updateTravelerInstanceConfiguration(String onSuccessCommand) {
        String travelerId = currentTravelerInstance.getId();
        String travelerTitle;
        if (currentTravelerEditTitle == null) {
            travelerTitle = getCurrentTravelerInstanceTitle();
        } else {
            travelerTitle = currentTravelerEditTitle;
        }
        String travelerDescription;
        if (currentTravelerEditDescription == null) {
            travelerDescription = getCurrentTravelerInstanceDescription();
        } else {
            travelerDescription = currentTravelerEditDescription;
        }

        Double status = getStatusKey(currentTravelerSelectedStatus);

        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
        String userName = currentUser.getUsername();
        try {
            Traveler travelerInstance = travelerApi.updateTraveler(travelerId, userName, travelerTitle, travelerDescription, currentTravelerDeadline, status);
            setCurrentTravelerInstance(travelerInstance);
            SessionUtility.executeRemoteCommand(onSuccessCommand);
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
    }

    public void resetUpdateTravelerInstanceConfiguration() {
        currentTravelerSelectedStatus = null;
        currentTravelerDeadline = null;
        currentTravelerEditTitle = null;
        currentTravelerEditDescription = null;
    }

    public void setPropertyValue(PropertyValue propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getTravelerTemplateTitle() {
        return travelerTemplateTitle;
    }

    public void setTravelerTemplateTitle(String travelerTemplateTitle) {
        this.travelerTemplateTitle = travelerTemplateTitle;
    }

    public List<Form> getActiveTravelerTemplates() {
        if (activeTravelerTemplates == null) {
            return null;
        }
        return activeTravelerTemplates.getForms();
    }

    public void setSelectedTemplate(Form selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    public void loadReleasedTemplatesForSelectedTemplate() {
        if (selectedTemplate != null) {
            loadReleasedTemplatesForTemplate(selectedTemplate, false, true);
        }
    }

    public Form getSelectedTemplate() {
        return selectedTemplate;
    }

    public Form getMultiEditSelectedTemplate() {
        return multiEditSelectedTemplate;
    }

    public void setMultiEditSelectedTemplate(Form multiEditSelectedTemplate) {
        this.multiEditSelectedTemplate = multiEditSelectedTemplate;
    }

    public List<Form> getAvailableTemplates() {
        return availableTemplates;
    }

    public List<Form> getMultiEditAvailableTemplateForApplyAll() {
        return multiEditAvailableTemplateForApplyAll;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public Binder getNewBinder() {
        return newBinder;
    }

    public void setNewBinder(Binder newBinder) {
        this.newBinder = newBinder;
    }

    public List<Form> getNewBinderTemplateSelection() {
        return newBinderTemplateSelection;
    }

    public void setNewBinderTemplateSelection(List<Form> newBinderTemplateSelection) {
        this.newBinderTemplateSelection = newBinderTemplateSelection;
    }

    public void setTravelerInstanceTitle(String travelerInstanceTitle) {
        this.travelerInstanceTitle = travelerInstanceTitle;
    }

    public String getTravelerInstanceTitle() {
        return travelerInstanceTitle;
    }
    
    public void setSelectedTravelerInstanceTemplate(Form selectedTravelerInstanceTemplate) {
        if (selectedTravelerInstanceTemplate != null) {
            if (this.selectedTravelerInstanceTemplate == null
                    || !this.selectedTravelerInstanceTemplate.getId().equals(selectedTravelerInstanceTemplate.getId())) {
                try {
                    travelerInstanceTitle = selectedTravelerInstanceTemplate.getTitle();

                    loadReleasedTemplatesForTemplate(selectedTravelerInstanceTemplate, false);
                } catch (Exception ex) {
                    SessionUtility.addErrorMessage("Error", ex.getMessage());
                }

            }
        } else {
            travelerInstanceTitle = "";
        }
        this.selectedTravelerInstanceTemplate = selectedTravelerInstanceTemplate;
    }

    public void prepareUpdatePreferredVersionForTemplate(Form template, String onSuccessCommand) {
        resetRenderBooleans();
        renderTravelerTemplateUpdatePrefVersionDialog = true;

        List<PropertyValue> templatePropertyValueListForCurrent = getTemplatePropertyValueListForCurrent();
        String templateId = template.getId();
        propertyValue = null;
        for (PropertyValue pv : templatePropertyValueListForCurrent) {
            if (pv.getValue().equals(templateId)) {
                propertyValue = pv;
                break;
            }
        }
        if (propertyValue == null) {
            SessionUtility.addErrorMessage("Error", "An error occurred finding the template requested.");
            return;
        }

        setSelectedTemplate(template);
        loadReleasedTemplatesForSelectedTemplate();

        SessionUtility.executeRemoteCommand(onSuccessCommand);

    }

    private void loadReleasedTemplatesForTemplate(Form template, boolean quiet) {
        loadReleasedTemplatesForTemplate(template, quiet, false);
    }

    private void loadReleasedTemplatesForTemplate(Form template, boolean quiet, boolean includeSelectLatestDefault) {

        template.setSelectedReleasedForm(null);

        ReleasedForms releasedFormsForSelectedTravelerInstanceTemplate;

        try {
            releasedFormsForSelectedTravelerInstanceTemplate = travelerApi.getReleasedFormsCreatedFromForm(template.getId());
            template.setReleasedForms(releasedFormsForSelectedTravelerInstanceTemplate);
        } catch (Exception ex) {
            SessionUtility.addErrorMessage("Error", ex.getMessage());
            return;
        }

        LinkedList<ReleasedForm> releasedForms = releasedFormsForSelectedTravelerInstanceTemplate.getReleasedForms();

        long prevInstant = 0;

        String preferredReleasedId = template.getPreferredReleasedId();
        ReleasedForm latestForm = null;
        ReleasedForm noPrefForm = null;
        if (includeSelectLatestDefault) {
            noPrefForm = ReleasedForm.createCustomAlwaysSelectLatest();
            releasedForms.add(0, noPrefForm);
            latestForm = noPrefForm;
        }

        for (int i = 0; i < releasedForms.size(); i++) {
            ReleasedForm releasedForm = releasedForms.get(i);

            int status = releasedForm.getStatus();
            if (status == FORM_ARCHIVED_STATUS) {
                releasedForms.remove(i);
                i--;
                continue;
            }

            // Find latest form
            if (noPrefForm == null) {
                Instant instant = Instant.parse(releasedForm.getReleasedOn());
                if (instant.getEpochSecond() > prevInstant) {
                    latestForm = releasedForm;
                    prevInstant = instant.getEpochSecond();
                }
            }

            if (preferredReleasedId != null) {
                if (releasedForm.getId().equals(preferredReleasedId)) {
                    // Select preferred version
                    template.setSelectedReleasedForm(releasedForm);
                }
            }
        }

        if (template.getSelectedReleasedForm() == null) {
            if (preferredReleasedId != null) {
                String errMessage = "The preferred version: "
                        + template.getPreferredReleasedVerCache()
                        + " could not be found.";
                if (!includeSelectLatestDefault) {
                    errMessage += " Latest automatically selected.";
                }
                SessionUtility.addWarningMessage("Preferred version not selected",
                        errMessage);
            }
            template.setSelectedReleasedForm(latestForm);
        }

        int releasedFormsSize = releasedForms.size();

        if (releasedFormsSize == 0 && quiet == false) {
            SessionUtility.addInfoMessage("No Released Templates",
                    "No templates were released for this template. Please create a released version of this template before proceeding.");
        }
    }

    public Form getSelectedTravelerInstanceTemplate() {
        return selectedTravelerInstanceTemplate;
    }

    public Traveler getCurrentTravelerInstance() {
        return currentTravelerInstance;
    }

    public String getCurrentTravelerInstanceTitle() {
        if (currentTravelerInstance != null && currentTravelerInstance.getTitle() != null) {
            return currentTravelerInstance.getTitle();
        }

        return "";
    }

    public String getCurrentTravelerInstanceDescription() {
        if (currentTravelerInstance != null && currentTravelerInstance.getDescription() != null) {
            return currentTravelerInstance.getDescription();
        }

        return "";
    }

    public void setCurrentTravelerInstance(Traveler currentTravelerInstance) {
        this.currentTravelerInstance = currentTravelerInstance;
        resetUpdateTravelerInstanceConfiguration();
    }

    public Binder getSelectedBinder() {
        return selectedBinder;
    }

    public void setSelectedBinder(Binder selectedBinder) {
        this.selectedBinder = selectedBinder;
    }

    public String getTRAVELER_WEB_APP_URL() {
        return TRAVELER_WEB_APP_URL;
    }

    public Date getCurrentTravelerDeadline() {
        if (currentTravelerInstance.getDeadline() != null) {
            String deadline = currentTravelerInstance.getDeadline();
            String dateString = deadline.split("T")[0];
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException ex) {
                logger.error(ex);
            }
        }

        return null;
    }

    public void setCurrentTravelerDeadline(Date currentTravelerDeadline) {
        this.currentTravelerDeadline = currentTravelerDeadline;
    }

    /**
     * Makes the default selection in the select one button widget.
     *
     * @return status of current traveler.
     */
    public String getCurrentTravelerSelectedStatus() {
        return getCurrentTravelerStatus();
    }

    public void setCurrentTravelerSelectedStatus(String currentTravelerSelectedStatus) {
        this.currentTravelerSelectedStatus = currentTravelerSelectedStatus;
    }

    public String getCurrentTravelerEditTitle() {
        return getCurrentTravelerInstanceTitle();
    }

    public void setCurrentTravelerEditTitle(String currentTravelerEditTitle) {
        this.currentTravelerEditTitle = currentTravelerEditTitle;
    }

    public String getCurrentTravelerEditDescription() {
        return getCurrentTravelerInstanceDescription();
    }

    public void setCurrentTravelerEditDescription(String currentTravelerEditDescription) {
        this.currentTravelerEditDescription = currentTravelerEditDescription;
    }

    private PropertyValueController getPropertyValueController() {
        if (propertyValueController == null) {
            propertyValueController = PropertyValueController.getInstance();
        }
        return propertyValueController;
    }

}
