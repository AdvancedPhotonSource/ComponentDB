/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.jsf.beans;

import gov.anl.aps.cdb.api.PdmLinkApi;
import gov.anl.aps.cdb.common.constants.CdbProperty;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawing;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawingRevision;
import gov.anl.aps.cdb.common.objects.PdmLinkSearchResults;
import gov.anl.aps.cdb.common.objects.PdmLinkSearchResult;
import gov.anl.aps.cdb.common.objects.Image;
import gov.anl.aps.cdb.common.objects.PdmLinkComponent;
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentType;
import gov.anl.aps.cdb.portal.controllers.ComponentTypeController;
import gov.anl.aps.cdb.portal.controllers.ComponentController;
import gov.anl.aps.cdb.portal.model.db.entities.Component;
import gov.anl.aps.cdb.portal.controllers.PropertyTypeController;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.log4j.Logger;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.imageio.ImageIO;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * JSF bean for handling PDMLink drawings.
 *
 * This bean uses CDB web service.
 */
@Named("pdmLinkDrawingBean")
@SessionScoped
public class PdmLinkDrawingBean implements Serializable {

    private static final Logger logger = Logger.getLogger(PdmLinkDrawingBean.class.getName());

    private String drawingName;
    private String searchKeywords;
    private String prevSearchKeywords;
    private PdmLinkDrawing drawing;
    private PdmLinkApi pdmLinkApi;
    private PdmLinkSearchResults searchResults;
    private PdmLinkComponent pdmLinkComponent;
    //Getting image from server 
    private StreamedContent pdmLinkImage;
    //Add component form 
    private boolean exposeSuggestedComponentType;
    private ListDataModel suggestedComponentTypeListDataModel;
    private String PDMLINK_PROPERTY_NAME = "PDMLink Drawing";
    private String WBS_PROPERTY_NAME = "WBS"; 
    private PropertyType pdmPropertyType; 
    private PropertyType wbsPropertyType; 

    @PostConstruct
    public void init() {
        String webServiceUrl = ConfigurationUtility.getPortalProperty(CdbProperty.WEB_SERVICE_URL_PROPERTY_NAME);
        try {
            pdmLinkApi = new PdmLinkApi(webServiceUrl);
        } catch (ConfigurationError ex) {
            String error = "PDMLink Service is not accessible:  " + ex.getErrorMessage();
            logger.error(error);
            SessionUtility.addErrorMessage("Error", error);
        }
    }

    public PdmLinkDrawing getDrawing() {
        return drawing;
    }

    public void setDrawingName(String drawingName) {
        this.drawingName = drawingName;
    }

    public String getDrawingName() {
        return drawingName;
    }

    public List<PdmLinkSearchResult> getSearchResults() {
        if (searchResults == null) {
            return null;
        }
        return searchResults.getSearchResults();
    }

    public List<PdmLinkDrawingRevision> getDrawingRevisionList() {
        if (drawing == null) {
            return null;
        }
        return drawing.getRevisionList();
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public StreamedContent getPdmLinkImage() {
        return pdmLinkImage;
    }

    private PropertyType getPdmPropertyType(PropertyTypeController propertyTypeController) {
        if(pdmPropertyType == null){
            pdmPropertyType = propertyTypeController.findByName(PDMLINK_PROPERTY_NAME); 
        }
        return pdmPropertyType;
    }

    private PropertyType getWbsPropertyType(PropertyTypeController propertyTypeController) {
        if(wbsPropertyType == null){
            wbsPropertyType = propertyTypeController.findByName(WBS_PROPERTY_NAME);
        }
        return wbsPropertyType;
    }
    
    public void setExposeSuggestedComponentType(boolean exposeSuggestedComponentType) {
        this.exposeSuggestedComponentType = exposeSuggestedComponentType;
    }

    public boolean isExposeSuggestedComponentType() {
        return exposeSuggestedComponentType;
    }
    
    public PdmLinkComponent getPdmLinkComponent() {
        return pdmLinkComponent;
    }

    /**
     * Check if the pdmLinkApi was successfully initialized.
     *
     * @return boolean that specifies if error occurred
     */
    private boolean checkAPIStatus() {
        if (pdmLinkApi == null) {
            SessionUtility.addErrorMessage("Error", "PDMLink Service is not accessible.");
            return false;
        }
        return true;
    }

    /**
     * Using the currently loaded drawing. Load up information required to create a component. 
     * 
     * @param componentController allows clearing a componentObject. The component object is binded to UI fields on the pdmLink add component dialog. 
     */
    public void generateComponentInfo(ComponentController componentController) {
        //Create a new empty cdb component. 
        componentController.prepareCreate();
        //Generate a pdmLinkComponent information object 
        pdmLinkComponent = null;
        suggestedComponentTypeListDataModel = null;

        if (!checkAPIStatus()) {
            return;
        }
        if (drawing == null) {
            SessionUtility.addErrorMessage("Error", "No drawing is loaded");
        }

        String drawingNumber = drawing.getNumber();
        if (drawingNumber != null && !drawingNumber.isEmpty()) {
            try {
                pdmLinkComponent = pdmLinkApi.generateComponentInformation(drawingNumber);
                // Add generated description to current component 
                componentController.getSelected().setDescription(pdmLinkComponent.getCdbDescription()) ;
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        } else {
            SessionUtility.addWarningMessage("Error", "Drawing Number is empty");
        }
    }

    /**
     * Create a component using the component db entity. Once created add pdmLink properties and WBS description if possible. 
     * 
     * @param componentController componentController contains methods required to create and save component in database. 
     * @param propertyTypeController propertyTypeController allows getting a propertyType entity object which define component properties. 
     */
    public void createComponentFromDrawingNumber(ComponentController componentController, PropertyTypeController propertyTypeController) {
        if (pdmLinkComponent != null) {
            Component currentComponent = componentController.getSelected();
            currentComponent.setName(pdmLinkComponent.getName());

            componentController.create();

            //Add properties if a component was created 
            if (currentComponent.getId() != null) {
                //Attempt to add PDMLink properties
                getPdmPropertyType(propertyTypeController);
                if (pdmPropertyType != null) {
                    for (String pdmPropertyValue : pdmLinkComponent.getPdmPropertyValues()) {
                        componentController.preparePropertyTypeValueAdd(pdmPropertyType, pdmPropertyValue);
                    }
                }else {
                    SessionUtility.addErrorMessage("Error", "Couldn't find " + PDMLINK_PROPERTY_NAME + " property type");
                }
                //Attempt to add WBS property
                getWbsPropertyType(propertyTypeController); 
                if(wbsPropertyType != null){
                    List<AllowedPropertyValue> wbsPropertyAllowedValueList = wbsPropertyType.getAllowedPropertyValueList();
                    String wbsNumber = pdmLinkComponent.getWbsDescription(); 
                    boolean foundAllowedValue = false; 
                    for(AllowedPropertyValue wbsAllowedPropertyValue : wbsPropertyAllowedValueList){
                        if(wbsAllowedPropertyValue.getValue().equalsIgnoreCase(wbsNumber)){
                            wbsNumber = wbsAllowedPropertyValue.getValue(); 
                            foundAllowedValue = true; 
                            break; 
                        } 
                    }
                    if(foundAllowedValue){
                        componentController.preparePropertyTypeValueAdd(wbsPropertyType, wbsNumber);  
                    }else{
                        SessionUtility.addWarningMessage("WBS not added", "WBS number is not in the allowed value list for WBS property.");
                    }
                }else{
                    SessionUtility.addErrorMessage("Error", "Couldn't find " + WBS_PROPERTY_NAME + " property type");
                }
                
                //Attempt to update the component with the new properties 
                if(currentComponent.getPropertyValueList().size() > 0){
                    componentController.update(); 
                }
            }
        }else{
            SessionUtility.addErrorMessage("Error", "No pdmLink drawing information was generated");
        }
    }
    
    /**
     * Create a dataModel using information in the the pdmLinkComponent from web service. 
     * returning null will result the UI to use a dataModel with all component types. 
     * @param componentTypeController passed from the UI. The standard componentTypeController bean that UI would use. 
     * @return dataModel that could be used with dataTable and passed to auto-complete drop-down of add component on pdmLink interface. 
     */
    public DataModel createSuggestedTypesDataModel(ComponentTypeController componentTypeController) {
        // When exposeSuggesteedComponentType is false the user will get a different datatable with all component types. 
        if (pdmLinkComponent != null && exposeSuggestedComponentType == true) {
            if (suggestedComponentTypeListDataModel == null) {
                componentTypeController.setSelectedComponentType(null);
                componentTypeController.setFilteredObjectList(null);
                List<ComponentType> componentTypeEntityList = new ArrayList<>();

                for (gov.anl.aps.cdb.common.objects.ComponentType suggestedComponentType : pdmLinkComponent.getSuggestedComponentTypes()) {
                    ComponentType componentType = componentTypeController.findById(suggestedComponentType.getId().intValue());
                    componentTypeEntityList.add(componentType);
                }

                suggestedComponentTypeListDataModel = new ListDataModel(componentTypeEntityList);
            }
            return suggestedComponentTypeListDataModel;
        }else{
            return null;
        }
    }

    /**
     * Determine whether the user wants to search for a particular drawing or a
     * list of drawing names, perform correct action based on criteria.
     */
    public void searchDrawings() {

        //Check if the required search result is already loaded. 
        if (prevSearchKeywords == null) {
            prevSearchKeywords = "";
        } else if (prevSearchKeywords.equals(searchKeywords) && drawing != null) {
            //Show only loaded search results 
            if (searchResults != null) {
                drawing = null;
                return;
            }
        } else if (searchKeywords.equals(drawingName) && searchResults != null) {
            //Show only loaded drawing 
            if (drawing != null) {
                searchResults = null;
                //The image needs to be reloaded. 
                loadImageForDrawing();
                return;
            }
        }

        prevSearchKeywords = searchKeywords;
        searchResults = null;
        drawing = null;

        if (searchKeywords != null && !searchKeywords.isEmpty()) {
            logger.debug("Search keywords: " + searchKeywords);
            if (searchKeywords.contains("*") || searchKeywords.contains("?")
                    || !PdmLinkDrawing.isExtensionValid(searchKeywords)) {
                searchPdmLink();
            } else {
                drawingName = searchKeywords;
                findDrawing();
            }
        }else{
            SessionUtility.addWarningMessage("Warning", "Search pattern cannot be empty.");
        }
    }

    /**
     * Use drawing name variable to attempt to get information for the
     * particular drawing.
     * Set drawing variable. 
     */
    public void findDrawing() {

        if (!checkAPIStatus()) {
            return;
        }

        drawing = null;
        pdmLinkImage = null;

        if (drawingName != null && !drawingName.isEmpty()) {
            if (!PdmLinkDrawing.isExtensionValid(drawingName)) {
                SessionUtility.addWarningMessage("Warning", "Valid drawing name extensions are: " + PdmLinkDrawing.VALID_EXTENSION_LIST);
                return;
            }

            try {
                logger.debug("Searching for drawing: " + drawingName);
                drawing = pdmLinkApi.getDrawing(drawingName);
                loadImageForDrawing();
                logger.debug("Found drawing, windchill URL: " + drawing.getWindchillUrl());
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        } else {
            SessionUtility.addWarningMessage("Warning", "Drawing name cannot be empty.");
        }
    }

    /**
     * Polymorphic function that call the function after setting drawingName
     * from its parameter.
     *
     * @param drawingName drawing number/name to search for
     */
    public void findDrawing(String drawingName) {
        this.drawingName = drawingName;
        findDrawing();
    }

    /**
     * Search the PDMLink for a list of drawings resulting from the keywords
     * entered by user. 
     * Set the searchResults variable
     */
    public void searchPdmLink() {
        if (!checkAPIStatus()) {
            return;
        }

        try {
            searchResults = pdmLinkApi.search(searchKeywords);
            logger.debug("Found " + searchResults.getSearchResults().size() + " drwaing(s)");
        } catch (CdbException ex) {
            logger.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
        }
    }

    /**
     * Loads an image of a particular drawing for the latest revision. 
     * Set pdmLinkImage variable
     */
    public void loadImageForDrawing() {
        if (!checkAPIStatus()) {
            return;
        }

        try {
            //Check if image already has something for this version. 
            String ufid = (drawing.getRevisionList().get(0).getUfid());
            Image imageInfo = pdmLinkApi.getOneTimeImageUrl(ufid);
            String link = imageInfo.getImageUrl();
            URL url = new URL(link);

            BufferedImage buffImg;
            buffImg = ImageIO.read(url.openStream());
            ByteArrayOutputStream os;
            os = new ByteArrayOutputStream();
            ImageIO.write(buffImg, "png", os);

            pdmLinkImage = new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/jpg");

        } catch (CdbException ex) {
            logger.error(ex);
            //No need to notify user since many drawings do not include image
            //SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            logger.error(ex);
            pdmLinkImage = null;
        } catch (Exception ex) {
            logger.error(ex);
            //The extension of the image is not in standard format cannot be loaded
            if(ex.getMessage().equals("image == null!")){
                //Don't inform user of this exception. No valid image exits. 
                return;
            }
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }
    }

    /**
     * Gets information about a particular drawing using information from 
     * search result. 
     * Set the drawing variable. 
     * @param ufid attribute of a search result
     * @param oid attribute of a search result
     */
    public void completeDrawing(String ufid, String oid) {
        if (!checkAPIStatus()) {
            return;
        }

        drawing = null;
        pdmLinkImage = null;

        if (ufid != null && !ufid.isEmpty() && oid != null && !oid.isEmpty()) {
            try {
                logger.debug("Completing drawing with oid: " + oid);
                drawing = pdmLinkApi.completeDrawingInfo(ufid, oid);
                drawingName = drawing.getName();
                loadImageForDrawing();
                logger.debug("Found drawing, windchill URL: " + drawing.getWindchillUrl());
            } catch (CdbException ex) {
                logger.error(ex);
                SessionUtility.addErrorMessage("Error", ex.getErrorMessage());
            }
        } else {
            SessionUtility.addWarningMessage("Warning", "UFID and or OID were not provided by search result");
        }

    }
}
