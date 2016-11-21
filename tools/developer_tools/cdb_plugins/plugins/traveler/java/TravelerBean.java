/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.traveler;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;

import gov.anl.aps.cdb.portal.controllers.CdbDomainEntityController;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.plugins.support.traveler.api.TravelerApi;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Form;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Forms;
import gov.anl.aps.cdb.portal.plugins.support.traveler.objects.Traveler;
import java.io.Serializable;
import javax.faces.model.SelectItem;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.html.HtmlInputText;
import javax.inject.Named;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@Named("travelerBean")
@SessionScoped
public class TravelerBean implements Serializable {

    private final String TRAVELER_TEMPLATE_HANDLER_NAME = "Traveler Template";
    
    private TravelerApi travelerApi;
    private static final Logger logger = Logger.getLogger(TravelerBean.class.getName());

    private PropertyValue propertyValue;
    private Traveler currentTravelerInstance;
    private DateFormat dateFormat;
    private Date currentTravelerDeadline; 
    private final Map statusOptions = new HashMap(); 
    private final Map statusNames = new HashMap(); 
    private String currentTravelerSelectedStatus; 
    private String currentTravelerEditTitle; 
    private String currentTravelerEditDescription; 
    
    private Integer travelerInstanceProgress; 

    private Form selectedTemplate;
    private Form selectedTravelerInstanceTemplate;

    private String travelerTemplateTitle;
    private Forms travelerTemplates;

    private String travelerInstanceTitle;

    private List<Form> availableTemplates;

    private HtmlInputText travelerInstanceTitleInputText;

    private final String TRAVELER_WEB_APP_URL = TravelerPluginManager.getTravelerWebApplicationUrl(); 
    private final String TRAVELER_WEB_APP_TEMPLATE_PATH = TravelerPluginManager.getTravelerWebApplicationTemplatePath();
    private final String TRAVELER_WEB_APP_TRAVELER_PATH = TravelerPluginManager.getTravelerWebApplicationTravelerPath(); 
    private final String TRAVELER_WEB_APP_TRAVELER_CONFIG_PATH = TravelerPluginManager.getTravelerWebApplicationTravelerConfigPath();

    @PostConstruct
    public void init() {
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
    
    public static TravelerBean getInstance() {
        return (TravelerBean) SessionUtility.findBean("travelerBean");
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
     * Checks for template and displays error message when null.
     * Used to determine if a function should execute. 
     * 
     * @param template Template variable that needs to be checked. 
     * @return boolean that determines if function should execute.  
     */
    private boolean checkSelectedTemplate(Form template) {
        if (template != null) {
            return true;
        } else {
            SessionUtility.addWarningMessage("No Template Selected", "Traveler Template is not selected");
            logger.error("Traveler Template is not selected");
            return false;
        }
    }
    
    /**
     * Creates a traveler template or Form. 
     * Uses traveler API to create a traveler template or Form. Assigns the newly created id with property and saves. 
     * 
     * @param entityController controller for the entity currently being edited by the user. 
     * @param onSuccessCommand Remote command to execute only on successful completion. 
     */
    public void createTravelerTemplate(CdbDomainEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {

            if (!travelerTemplateTitle.equals("") && travelerTemplateTitle != null) {
                Form form;
                try {
                    UserInfo currentUser = (UserInfo) SessionUtility.getUser();
                    form = travelerApi.createForm(travelerTemplateTitle, currentUser.getUsername(), "");
                    SessionUtility.addInfoMessage("Template Created", "Traveler Template '" + form.getId() + "' has been created");
                    propertyValue.setValue(form.getId());
                    entityController.savePropertyList();
                    RequestContext.getCurrentInstance().execute(onSuccessCommand);
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
     * Links to an existing traveler template. 
     * Uses an traveler template or Form fetched from traveler web service to assign id to property value and saves. 
     * 
     * @param entityController controller for the entity currently being edited by the user. 
     * @param onSuccessCommand Remote command to execute only on successful completion. 
     */
    public void linkTravelerTemplate(CdbDomainEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {
            if (checkSelectedTemplate(selectedTemplate)) {
                propertyValue.setValue(selectedTemplate.getId());
                entityController.savePropertyList();
                RequestContext.getCurrentInstance().execute(onSuccessCommand);
            }
        }
    }

    /**
     * Removes the id associated with template property. 
     * 
     * @param onSuccessCommand Remote command to execute only on successful completion. 
     */
    public void unlinkTravelerTemplate(String onSuccessCommand) {
        if (checkPropertyValue()) {
            travelerTemplateTitle = "";
            propertyValue.setValue("");
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        }
    }

    /**
     * Generates a (traveler template)/form view URL using id and CDB properties. 
     * 
     * @param formId traveler template or form id to generate URL for. 
     * @return generated URL to traveler web application.
     */
    public String getTravelerTemplateUrl(String formId) {
        String travelerTemplateUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TEMPLATE_PATH;
        return travelerTemplateUrl.replace("FORM_ID", formId);
    }

    /**
     * Generates a (traveler instance)/traveler view URL using id and CDB properties. 
     * Uses currentTravlerInstance variable and its ID along with CDB properties to generate URL.
     * 
     * @return generated URL to traveler web application.
     */
    public String getCurrentTravelerInstanceUrl() {
        if (currentTravelerInstance != null) {
            String travelerInstanceUrl = TRAVELER_WEB_APP_URL + TRAVELER_WEB_APP_TRAVELER_PATH;
            return travelerInstanceUrl.replace("TRAVELER_ID", currentTravelerInstance.getId());
        }
        return "";

    }
    
    /**
     * Generates a (traveler instance)/traveler configuration URL using id and CDB properties. 
     * Uses currentTravlerInstance variable and its ID along with CDB properties to generate URL.
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
     * Gets a string representation of status stored in traveler db.  
     * Uses currentTravelerInstance variable and its status to generate string status. 
     * 
     * @return string representation of status. 
     */
    public String getCurrentTravelerStatus() {
        if (currentTravelerInstance != null) {
            return (String)statusNames.get(currentTravelerInstance.getStatus()); 
        }
        return "";
    }
    
    public List<String> getCurrentTravelerStatusOptions() {
        if(currentTravelerInstance != null) {
            return (List<String>)statusOptions.get(currentTravelerInstance.getStatus()); 
        }
        return null; 
    }
    
    public Double getStatusKey(String statusName) {
        Iterator statusNamesIterator = statusNames.entrySet().iterator(); 
        
        while(statusNamesIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)statusNamesIterator.next(); 
            if (entry.getValue().equals(statusName)) {
                return (Double)entry.getKey(); 
            }
        }
        
        return -1.0; 
    }

    /**
     * Determines if the current owner owns the traveler instance.
     * Uses currentTravlerInstance to determine if current user owns it.
     * Currently, the traveler could only be configured by user who owns it. 
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
     * Gets a (traveler instance)/traveler title. 
     * Uses the traveler web API to get a title. 
     * Default text is returned if fetch fails. 
     * returned value is used for a link that user may click to get to traveler.
     * 
     * @param propertyValue value of propertyValue holds an id to a traveler instance. 
     * @return String that will be displayed to user on link to traveler instance.
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
     * Determines all entities that need to have (traveler templates)/forms loaded
     * 
     * @param entityController controller for the entity currently being edited by the user. 
     */
    public void loadEntityAvailableTemplateList(CdbEntityController entityController) {
        if (checkPropertyValue()) {
            availableTemplates = new ArrayList<>();            
            if (entityController instanceof ItemController) {                
                Item item = (Item) entityController.getCurrent(); 
                loadPropertyTravelerTemplateList(item.getPropertyValueDisplayList(), availableTemplates);
                if (item.getDerivedFromItem() != null) {
                    loadPropertyTravelerTemplateList(item.getDerivedFromItem().getPropertyValueDisplayList(), availableTemplates);
                }
            } else if (entityController instanceof ItemElementController) {
                ItemElement itemElement = (ItemElement) entityController.getCurrent(); 
                loadPropertyTravelerTemplateList(itemElement.getPropertyValueDisplayList(), availableTemplates);
                Item parentItem = itemElement.getParentItem();
                Item containedItem = itemElement.getContainedItem();
                loadPropertyTravelerTemplateList(parentItem.getPropertyValueDisplayList(), availableTemplates);
                loadPropertyTravelerTemplateList(containedItem.getPropertyValueDisplayList(), availableTemplates);
            }          
        }
    }

    /**
     * Check all property values for templates and call function that add each one. 
     * 
     * @param propertyValues List of properties for a specific entity.
     * @param formList List of (traveler templates)/forms that will be displayed to the user. 
     */
    private void loadPropertyTravelerTemplateList(List<PropertyValue> propertyValues, List<Form> formList) {
        for (PropertyValue curPropertyValue : propertyValues) {
            // Check that they use the traveler template handler. 
            if (curPropertyValue.getPropertyType().getPropertyTypeHandler() != null) {
                if (curPropertyValue.getPropertyType().getPropertyTypeHandler().getName().equals(TRAVELER_TEMPLATE_HANDLER_NAME)) {
                    addFormFromPropertyValue(curPropertyValue.getValue(), formList);
                }
            }
        }
    }
    
    /**
     * Load the form from web service using its ID and add to list. 
     * 
     * @param formId id of the (traveler template)/form to fetch from web service
     * @param formList List of (traveler templates)/forms that will be displayed to the user. 
     */
    private void addFormFromPropertyValue(String formId, List<Form> formList) {
        if (formId == null || formId.equals("")) {
            return;
        }
        try {
            formList.add(travelerApi.getForm(formId));
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
    }
    
    /**
     * Generates a name to be used in traveler application. 
     * Allows the traveler application to request information from CDB. 
     * 
     * @param deviceObject Item or ItemElement that will get traveler instance property. 
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
     * Create a traveler instance based on the currently selected template and entered title. 
     * 
     * @param entityController controller for the entity currently being edited by the user. 
     * @param onSuccessCommand Remote command to execute only on successful completion
     */
    public void createTravelerInstance(CdbDomainEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {
            if (checkSelectedTemplate(selectedTravelerInstanceTemplate)) {
                if (!travelerInstanceTitle.equals("") || travelerInstanceTitle != null) {
                    try {
                        String device = getDeviceName(entityController.getCurrent());
                        UserInfo currentUser = (UserInfo) SessionUtility.getUser();
                        Traveler travelerInstance = travelerApi.createTraveler(
                                selectedTravelerInstanceTemplate.getId(),
                                currentUser.getUsername(),
                                travelerInstanceTitle,
                                device);
                        setCurrentTravelerInstance(travelerInstance);
                        
                        SessionUtility.addInfoMessage(
                                "Traveler Instance Created",
                                "Traveler Instance '" + travelerInstance.getId() + "' has been created");

                        propertyValue.setValue(travelerInstance.getId());
                        entityController.savePropertyList();
                        RequestContext.getCurrentInstance().execute(onSuccessCommand);

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
    
    /**
     * Load information about the traveler instance property currently loaded. 
     * Add progress information based on its information which will be displayed to user.
     * 
     * @param onSuccessCommand Remote command to execute only on successful completion 
     */
    public void loadCurrentTravelerInstance(String onSuccessCommand) {
        if (checkPropertyValue()) {
            try {
                Traveler travelerInstance = travelerApi.getTraveler(propertyValue.getValue());
                setCurrentTravelerInstance(travelerInstance);
                //Load completion pie chart model
                int totalInput = travelerInstance.getTotalInput();
                int finishedInput = travelerInstance.getFinishedInput();
                double progress = (finishedInput * 1.0) / (totalInput * 1.0) * 100; 
                this.travelerInstanceProgress = (int) progress; 
                //Show the GUI since all execution was successful. 
                RequestContext.getCurrentInstance().execute(onSuccessCommand);
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        }
    }
    
    /**
     * Get a list of all (traveler templates)/forms for user to view. 
     * 
     * @param onSuccessCommand Remote command to execute only on successful completion
     */
    public void loadTravelerTemplates(String onSuccessCommand) {
        try {
            travelerTemplates = travelerApi.getForms();
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
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
    
    /**
     * Function gets called when user selects traveler template to set the default traveler instance title.
     */
    public void updateTravelerInstanceTitleInputText() {
        if (selectedTravelerInstanceTemplate != null) {
            if (travelerInstanceTitleInputText != null) {
                travelerInstanceTitleInputText.setValue(selectedTravelerInstanceTemplate.getTitle());
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
        if(currentTravelerEditDescription == null) {
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
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
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

    public List<Form> getTravelerTemplates() {
        if (travelerTemplates == null) {
            return null;
        }
        return travelerTemplates.getForms();
    }

    public void setSelectedTemplate(Form selectedTemplate) {
        this.selectedTemplate = selectedTemplate;
    }

    public Form getSelectedTemplate() {
        return selectedTemplate;
    }

    public List<Form> getAvailableTemplates() {
        return availableTemplates;
    }

    public PropertyValue getPropertyValue() {
        return propertyValue;
    }

    public void setTravelerInstanceTitle(String travelerInstanceTitle) {
        this.travelerInstanceTitle = travelerInstanceTitle;
    }

    public String getTravelerInstanceTitle() {
        return travelerInstanceTitle;
    }

    public void setSelectedTravelerInstanceTemplate(Form selectedTravelerInstanceTemplate) {
        this.selectedTravelerInstanceTemplate = selectedTravelerInstanceTemplate;
    }

    public Form getSelectedTravelerInstanceTemplate() {
        return selectedTravelerInstanceTemplate;
    }

    public void setTravelerInstanceTitleInputText(HtmlInputText travelerInstanceTitleInputText) {
        this.travelerInstanceTitleInputText = travelerInstanceTitleInputText;
    }

    public HtmlInputText getTravelerInstanceTitleInputText() {
        return travelerInstanceTitleInputText;
    }

    public Integer getTravelerInstanceProgress() {
        return travelerInstanceProgress;
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
}
