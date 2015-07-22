/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.api;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawing;
import gov.anl.aps.cdb.common.objects.PdmLinkSearchResults;
import gov.anl.aps.cdb.common.objects.Image;
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
     * @param name drawing name
     * @return PDMLink drawing object
     * @throws InvalidArgument if provided name is empty or null
     * @throws ObjectNotFound when specified drawing does not exist
     * @throws CdbException in case of all other errors
     * @throws ExternalServiceError the PDMLink web service threw an error
     */
    public PdmLinkDrawing getDrawing(String name) throws InvalidArgument, ObjectNotFound, ExternalServiceError, CdbException {
        ArgumentUtility.verifyNonEmptyString("Drawing name", name);
        String requestUrl = "/pdmLink/drawings/" + name;
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
        jsonString = "{\"searchResults\": " + jsonString + "}"; 
        PdmLinkSearchResults searchResults = (PdmLinkSearchResults) CdbObjectFactory.createCdbObject(jsonString, PdmLinkSearchResults.class);
        return searchResults;
    }
    
    /**
     * Retrieve a one time use URL to an image of a drawing. 
     * 
     * @param revisionUFID ufid that relates to a specific drawing revision. 
     * @return image object that includes the url to the image. 
     * @throws InvalidArgument
     * @throws ExternalServiceError
     * @throws ObjectNotFound
     * @throws CdbException 
     */
    public Image getOneTimeImageUrl(String revisionUFID) throws InvalidArgument, ExternalServiceError, ObjectNotFound, CdbException{
        ArgumentUtility.verifyNonEmptyString("Search Pattern", revisionUFID);
        String requestUrl = "/pdmLink/drawingImages/" + revisionUFID;
        String jsonString = invokeGetRequest(requestUrl);
        Image image = (Image) CdbObjectFactory.createCdbObject(jsonString, Image.class);
        return image; 
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
            //Get Search Results 
            PdmLinkSearchResults results =client.search("D14100201-11319?.*"); 
            System.out.println("Search Results: ");
            for(int i =0; i < results.getSearchResults().size(); i++){
                gov.anl.aps.cdb.common.objects.PdmLinkSearchResult result = results.getSearchResults().get(i);
                System.out.println("    " + result.getName() + " " + result.getUfid() + " " +result.getOid());
            }
            
            
        } catch (CdbException ex) {
            System.out.println("Sorry: " + ex);
        }
    }
}
