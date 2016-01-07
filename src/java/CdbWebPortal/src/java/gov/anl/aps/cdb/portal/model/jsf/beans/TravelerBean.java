/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
import gov.anl.aps.cdb.portal.model.db.entities.Design;
import gov.anl.aps.cdb.portal.model.db.entities.DesignElement;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.TravelerTemplatePropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.traveler.api.TravelerApi;
import gov.anl.aps.traveler.common.objects.Form;
import gov.anl.aps.traveler.common.objects.Forms;
import gov.anl.aps.traveler.common.objects.Traveler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
    private Integer travelerInstanceProgress; 

    private Form selectedTemplate;
    private Form selectedTravelerInstanceTemplate;

    private String travelerTemplateTitle;
    private Forms travelerTemplates;

    private String travelerInstanceTitle;

    private List<Form> availableTemplates;

    private HtmlInputText travelerInstanceTitleInputText;

    private final String TRAVELER_WEB_APP_URL = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_URL_PROPERTY_NAME);
    private final String TRAVELER_WEB_APP_TEMPLATE_PATH = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_TEMPLATE_PATH_PROPERTY_NAME);
    private final String TRAVELER_WEB_APP_TRAVELER_PATH = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_TRAVELER_PATH_PROPERTY_NAME);
    private final String TRAVELER_WEB_APP_TRAVELER_CONFIG_PATH = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_APPLICATION_TRAVELER_CONFIG_PATH_PROPERTY_NAME);

    @PostConstruct
    public void init() {
        String webServiceUrl = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_SERVICE_URL_PROPERTY_NAME);
        String username = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_SERVICE_BASIC_AUTH_USERNAME_PROPERTY_NAME);
        String password = ConfigurationUtility.getPortalProperty(CdbProperty.TRAVELER_WEB_SERVICE_BASIC_AUTH_PASSWORD_PROPERTY_NAME);
        try {
            travelerApi = new TravelerApi(webServiceUrl, username, password);
        } catch (ConfigurationError ex) {
            String error = "Traveler Service is not accessible:  " + ex.getErrorMessage();
            SessionUtility.addErrorMessage("Error", error);
            logger.error(error);
        }
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
    public void createTravelerTemplate(CdbEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {

            if (!travelerTemplateTitle.equals("") && travelerTemplateTitle != null) {
                Form form;
                try {

                    form = travelerApi.createForm(travelerTemplateTitle, SessionUtility.getUser().toString(), "");
                    SessionUtility.addInfoMessage("Template Created", "Traveler Template '" + form.getId() + "' has been created");
                    propertyValue.setValue(form.getId());
                    entityController.update();
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
    public void linkTravelerTemplate(CdbEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {
            if (checkSelectedTemplate(selectedTemplate)) {
                propertyValue.setValue(selectedTemplate.getId());
                entityController.update();
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
            String status = currentTravelerInstance.getStatus() + "";
            switch (status) {
                case "0.0":
                    return "initalized";
                case "1.0":
                    return "active";
                case "1.5":
                    return "submitted";
                case "2.0":
                    return "completed";
                case "3.0":
                    return "frozen";
                default:
                    return "";
            }
        }
        return "";
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
            if (SessionUtility.getUser() != null && travelerUser.equals(SessionUtility.getUser().toString())) {
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
            switch (entityController.getEntityTypeName()) {
                case "component": {
                    Component component = (Component) entityController.getSelected();
                    loadPropertyTravelerTemplateList(component.getPropertyValueList(), availableTemplates);
                    break;
                }
                case "componentInstance": {
                    // Load forms from component instance
                    ComponentInstance componentInstance = (ComponentInstance) entityController.getSelected();
                    loadPropertyTravelerTemplateList(componentInstance.getPropertyValueList(), availableTemplates);
                    // Load forms from parent component
                    Component component = componentInstance.getComponent();
                    loadPropertyTravelerTemplateList(component.getPropertyValueList(), availableTemplates);
                    break;
                }
                case "design":
                    Design design = (Design) entityController.getSelected();
                    loadPropertyTravelerTemplateList(design.getPropertyValueList(), availableTemplates);
                    break;
                case "designElement":
                    // Load Design element templates
                    DesignElement designElement = (DesignElement) entityController.getSelected();
                    loadPropertyTravelerTemplateList(designElement.getPropertyValueList(), availableTemplates);
                    // Load Component templates
                    Component component = designElement.getComponent();
                    loadPropertyTravelerTemplateList(component.getPropertyValueList(), availableTemplates);
                    // Load design templates 
                    Design parentDesign = designElement.getParentDesign();
                    loadPropertyTravelerTemplateList(parentDesign.getPropertyValueList(), availableTemplates);
                    break;
                //TODO Add Desgin Instance
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
     * Create a traveler instance based on the currently selected template and entered title. 
     * 
     * @param entityController controller for the entity currently being edited by the user. 
     * @param onSuccessCommand Remote command to execute only on successful completion
     */
    public void createTravelerInstance(CdbEntityController entityController, String onSuccessCommand) {
        if (checkPropertyValue()) {
            if (checkSelectedTemplate(selectedTravelerInstanceTemplate)) {
                if (!travelerInstanceTitle.equals("") || travelerInstanceTitle != null) {
                    try {
                        String device = entityController.getEntityTypeName();
                        device += ":" + entityController.getSelected().getId();
                        currentTravelerInstance = travelerApi.createTraveler(
                                selectedTravelerInstanceTemplate.getId(),
                                SessionUtility.getUser().toString(),
                                travelerInstanceTitle,
                                device);
                        SessionUtility.addInfoMessage(
                                "Traveler Instance Created",
                                "Traveler Instance '" + currentTravelerInstance.getId() + "' has been created");

                        propertyValue.setValue(currentTravelerInstance.getId());
                        entityController.update();
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
                currentTravelerInstance = travelerApi.getTraveler(propertyValue.getValue());
                //Load completion pie chart model
                int totalInput = currentTravelerInstance.getTotalInput();
                int finishedInput = currentTravelerInstance.getFinishedInput();
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

    public String getTRAVELER_WEB_APP_URL() {
        return TRAVELER_WEB_APP_URL;
    }
}
