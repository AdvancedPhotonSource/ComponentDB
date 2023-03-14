/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.pdmLink.api;

import gov.anl.aps.cdb.api.CdbRestApi;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects.PdmLinkDrawing;
import gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects.PdmLinkSearchResults;
import gov.anl.aps.cdb.common.objects.Image;
import gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects.PdmLinkComponent;
import gov.anl.aps.cdb.common.utilities.ArgumentUtility;

/**
 * PDMLink API class, used for retrieving information about PDMLink drawings.
 */
public class PdmLinkApi extends CdbRestApi {

    /**
     * Constructor.
     *
     * @throws ConfigurationError if web service URL property is malformed or
     * null
     */
    public PdmLinkApi() throws ConfigurationError {
        super();
    }

    /**
     * Constructor.
     *
     * @param webServiceUrl web service URL
     * @throws ConfigurationError if web service URL is malformed or null
     */
    public PdmLinkApi(String webServiceUrl) throws ConfigurationError {
        super(webServiceUrl);
    }

    /**
     * Retrieve drawing information.
     *
     * @param drawingNumber drawing number
     * @return PDMLink drawing object
     * @throws InvalidArgument if provided name is empty or null
     * @throws ObjectNotFound when specified drawing does not exist
     * @throws CdbException in case of all other errors
     * @throws ExternalServiceError the PDMLink web service threw an error
     */
    public PdmLinkDrawing getDrawing(String drawingNumber) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Drawing name", drawingNumber);
        String requestUrl = "/pdmLink/drawings/" + drawingNumber;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkDrawing drawing = (PdmLinkDrawing) CdbObjectFactory.createCdbObject(jsonString, PdmLinkDrawing.class);
        return drawing;
    }
    
    /**
     * Retrieve drawing information providing only ufid and oid from search result
     * 
     * @param ufid
     * @param oid
     * @return PDMLink drawing object
     * @throws InvalidArgument
     * @throws ObjectNotFound
     * @throws CdbException 
     * @throws ExternalServiceError the PDMLink web service threw an error
     */
    public PdmLinkDrawing completeDrawingInfo(String ufid, String oid) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException {
        ArgumentUtility.verifyNonEmptyString("Drawing ufid", ufid);
        ArgumentUtility.verifyNonEmptyString("Drawing ufid", oid);
        String requestUrl = "/pdmLink/completeDrawings/" + ufid + "/" + oid;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkDrawing drawing = (PdmLinkDrawing) CdbObjectFactory.createCdbObject(jsonString, PdmLinkDrawing.class);
        return drawing;
    }
    
    /**
     * Retrieve a list of search results from PdmLink 
     *
     * @param searchPattern name, keywords, wildcards to perform search
     * @return PdmLink Search Results object 
     * @throws InvalidArgument if provided name is empty or null
     * @throws ObjectNotFound when specified drawing does not exist
     * @throws CdbException in case of all other errors
     * @throws ExternalServiceError the PDMLink web service threw an error
    */
    public PdmLinkSearchResults search(String searchPattern) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException {
        //Use web standards for special characters 
        searchPattern = searchPattern.replace(" ", "%20");
        searchPattern = searchPattern.replace("?", "%3F");
        ArgumentUtility.verifyNonEmptyString("Search Pattern", searchPattern);
        String requestUrl = "/pdmLink/search/" + searchPattern;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkSearchResults searchResults = (PdmLinkSearchResults) CdbObjectFactory.createCdbObject(jsonString, PdmLinkSearchResults.class);
        return searchResults;
    }
    
    /**
     * 
     * @param drawingNumberBase drawing number or drawing number without extension. 
     * @return PdmLink Search Results object 
     * @throws InvalidArgument if provided drawing number base is empty or null
     * @throws ObjectNotFound when specified drawing does not exist
     * @throws CdbException in case of all other errors
     * @throws ExternalServiceError the PDMLink web service threw an error
     */
    public PdmLinkSearchResults searchRelatedDrawings(String drawingNumberBase) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException {
        ArgumentUtility.verifyNonEmptyString("Drawing number base", drawingNumberBase);
        String requestUrl = "/pdmLink/searchRelated/" + drawingNumberBase;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkSearchResults searchResults = (PdmLinkSearchResults) CdbObjectFactory.createCdbObject(jsonString, PdmLinkSearchResults.class);
        return searchResults;
    }
    
    /**
     * Retrieve a one time use URL to an image of a drawing. 
     * 
     * @param revisionUFID ufid that relates to a specific drawing revision. 
     * @return image object that includes the url to the image. 
     * @throws InvalidArgument if provided revision is empty or null
     * @throws ObjectNotFound when specified image does not exist
     * @throws CdbException in case of all other errors
     * @throws ExternalServiceError the PDMLink web service threw an error
     */
    public Image getOneTimeImageUrl(String revisionUFID) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException{
        ArgumentUtility.verifyNonEmptyString("Search Pattern", revisionUFID);
        String requestUrl = "/pdmLink/drawingImages/" + revisionUFID;
        String jsonString = invokeGetRequest(requestUrl);
        Image image = (Image) CdbObjectFactory.createCdbObject(jsonString, Image.class);
        return image; 
    }
    
    /**
     * Generates information required to create a cdb component. Used for the form
     * @param drawingNumber drawingNumber ofthe drawing to create component from. 
     * @return pdmLinkComponent Object 
     * @throws InvalidArgument if provided drawing number is empty or null
     * @throws ObjectNotFound when specified drawing cannot be found
     * @throws CdbException in case of all other errors
     * @throws ExternalServiceError the PDMLink web service threw an error
     */
    public PdmLinkComponent generateComponentInformation(String drawingNumber) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException{
        ArgumentUtility.verifyNonEmptyString("Drawing Number", drawingNumber);
        String requestUrl = "/pdmLink/componentInfo/" + drawingNumber;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkComponent pdmLinkComponent = (PdmLinkComponent) CdbObjectFactory.createCdbObject(jsonString, PdmLinkComponent.class); 
        return pdmLinkComponent;
    }
    
    
    
    /*
     * Main method, used for simple testing.
     *
     * @param args main arguments
     */
    public static void main(String[] args) {
        String testHost = "http://localhost:10232/cdb";
        
        try {
            PdmLinkApi client = new PdmLinkApi(testHost);
            //Get Drawing Test
            PdmLinkDrawing drawing = client.getDrawing("D14100201-113160.asm");
            System.out.println("Drawing: \n" + drawing);
            System.out.println("Drawing Name: " + drawing.getName());
            System.out.println("Drawing Url: " + drawing.getWindchillUrl());
            //Get search results 
            PdmLinkSearchResults results =client.search("D14100201-11319?.*"); 
            System.out.println("Search Results: ");
            for(int i =0; i < results.getSearchResults().size(); i++){
                gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects.PdmLinkSearchResult result = results.getSearchResults().get(i);
                System.out.println("    " + result.getName() + " " + result.getUfid() + " " +result.getOid());
            }
            //Get related search results
            PdmLinkSearchResults relatedDrawings = client.searchRelatedDrawings("test.drw"); 
            System.out.println("Related Drawing Results: ");
            for(int i =0; i < relatedDrawings.getSearchResults().size(); i++){
                gov.anl.aps.cdb.portal.plugins.support.pdmLink.objects.PdmLinkSearchResult result = relatedDrawings.getSearchResults().get(i);
                System.out.println("    " + result.getName() + " " + result.getUfid() + " " +result.getOid());
            }
            
            // Generate component info
            PdmLinkComponent pdmLinkComponent = client.generateComponentInformation("D14100201-113160.asm"); 
            System.out.println("Name: " + pdmLinkComponent.getName());
            System.out.println("Suggested Comonent Types:"); 
            for(int i = 0; i<pdmLinkComponent.getSuggestedComponentTypes().size(); i++){
                System.out.println("    " + pdmLinkComponent.getSuggestedComponentTypes().get(i).getName());
            }
            System.out.println("WBS: " + pdmLinkComponent.getWbsDescription());
            System.out.println("Pdm Property Values:"); 
            for(int i = 0; i<pdmLinkComponent.getPdmPropertyValues().length; i++){
                System.out.println("    " +pdmLinkComponent.getPdmPropertyValues()[i]);
            }
            
        } catch (CdbException ex) {
            System.out.println("Sorry: " + ex);
        }
    }
}
