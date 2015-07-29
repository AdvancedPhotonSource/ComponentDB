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
import gov.anl.aps.cdb.portal.utilities.ConfigurationUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
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
    //Getting image from server 
    private StreamedContent pdmLinkImage;

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
     * Determine whether the user wants to search for a particular drawing or
     * a list of drawing names, perform correct action based on criteria. 
     */
    public void searchDrawings() {
        
        //Check if the required search result is already loaded. 
        if(prevSearchKeywords == null){
            prevSearchKeywords = ""; 
        }
        else if(prevSearchKeywords.equals(searchKeywords) && drawing != null){
            //Show only loaded search results 
            if(searchResults != null){
                drawing = null; 
                return;
            }
        }else if(searchKeywords.equals(drawingName) && searchResults != null){
            //Show only loaded drawing 
            if(drawing != null){
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
