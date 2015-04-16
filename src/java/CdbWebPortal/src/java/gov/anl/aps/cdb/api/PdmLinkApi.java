/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL: $
 *   $Date: $
 *   $Revision: $
 *   $Author: $
 */
package gov.anl.aps.cdb.api;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.ObjectNotFound;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.common.objects.PdmLinkDrawing;
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
     * @param webServiceUrl web service url
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
     */
    public PdmLinkDrawing getDrawing(String name) throws InvalidArgument, ObjectNotFound, CdbException {
        ArgumentUtility.verifyNonEmptyString("Drawing name", name);
        String requestUrl = "/pdmLink/drawings/" + name;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkDrawing drawing = (PdmLinkDrawing) CdbObjectFactory.createCdbObject(jsonString, PdmLinkDrawing.class);
        return drawing;
    }

    /*
     * Main method, used for simple testing.
     *
     * @param args main arguments
     */
    public static void main(String[] args) {
        try {
            PdmLinkApi client = new PdmLinkApi("http://zagreb.svdev.net:10232/cdb");
            PdmLinkDrawing drawing = client.getDrawing("D14100201-113160.asm");
            System.out.println("Drawing: \n" + drawing);
        } catch (CdbException ex) {
            System.out.println("Sorry: " + ex);
        }
    }
}
