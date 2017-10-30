/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.plugins.support.docManagament.api;

import gov.anl.aps.cdb.api.CdbRestApi;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.common.utilities.ArgumentUtility;
import gov.anl.aps.cdb.portal.plugins.support.docManagament.objects.Container;

/**
 *
 * @author djarosz
 */
public class DocumentManagamentApi extends CdbRestApi {
    
    protected static final String REST_GET_CONTAINER_BY_ID_PATH = "/data/getDossier/";
    
    /**
     * Constructor.
     *
     * @throws ConfigurationError if web service URL property is malformed or
     * null
     */
    public DocumentManagamentApi() throws ConfigurationError {
        super();
    }

    /**
     * Constructor.
     *
     * @param webServiceUrl web service URL
     * @throws ConfigurationError if web service URL is malformed or null
     */
    public DocumentManagamentApi(String webServiceUrl) throws ConfigurationError {
        super(webServiceUrl);
    }
    
    public Container getContainerById(Integer containerId) throws InvalidArgument, CdbException {
        ArgumentUtility.verifyPositiveInteger("collection id", containerId);
        String requestPath = REST_GET_CONTAINER_BY_ID_PATH + containerId; 
        String jsonString = invokeGetRequest(requestPath);
        Container container = (Container) CdbObjectFactory.createCdbObject(jsonString, Container.class);
        return container;        
    }
    
    /*
     public PdmLinkDrawing getDrawing(String drawingNumber) throws Exception {
        ArgumentUtility.verifyNonEmptyString("Drawing name", drawingNumber);
        String requestUrl = "/pdmLink/drawings/" + drawingNumber;
        String jsonString = invokeGetRequest(requestUrl);
        PdmLinkDrawing drawing = (PdmLinkDrawing) CdbObjectFactory.createCdbObject(jsonString, PdmLinkDrawing.class);
        return drawing;
    }
    */

    
}
