/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.api;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.ConfigurationError;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.objects.CdbObjectFactory;
import gov.anl.aps.cdb.common.objects.Design;
import gov.anl.aps.cdb.common.utilities.ArgumentUtility;

/**
 *
 * @author djarosz
 */
public class CdbDbApi extends CdbRestApi{
    
    public CdbDbApi() throws ConfigurationError{
        super();
    }
    
    public CdbDbApi(String webServiceUrl) throws ConfigurationError{
        super(webServiceUrl);
    }
    
    public Design getDesign(int id) throws InvalidArgument, CdbException{
        String idStr = id + ""; 
        ArgumentUtility.verifyNonEmptyString("Design Id", idStr);
        String requestUrl = "/designs/" + idStr;
        String jsonString = invokeGetRequest(requestUrl);
        Design design = (Design) CdbObjectFactory.createCdbObject(jsonString, Design.class);
        return design; 
    }
    
    public static void main(String[] args) {
        String testHost = "http://localhost:10232/cdb";
        try {
            CdbDbApi client = new CdbDbApi(testHost); 
            Design design = client.getDesign(4); 
            System.out.println(design.getName());
        } catch (CdbException ex){
            System.out.println(ex.getErrorMessage());
        }
    }
}
