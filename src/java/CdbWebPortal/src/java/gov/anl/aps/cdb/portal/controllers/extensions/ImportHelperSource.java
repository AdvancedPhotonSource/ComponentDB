/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.SourceController;

/**
 *
 * @author craig
 */
public class ImportHelperSource extends ImportHelperBase {


    protected static String completionUrlValue = "/views/source/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "CommScope"));
        columns.add(new ImportHelperBase.StringColumnModel("Description", "description", "setDescription", false, "Describe vendor/manufacturer here."));
        columns.add(new ImportHelperBase.StringColumnModel("Contact Info", "contactInfo", "setContactInfo", false, "John Smith 555-555-1212"));
        columns.add(new ImportHelperBase.UrlColumnModel("URL", "url", "setUrl", false, "http://www.example.com/example"));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    protected boolean isValidationOnly() {
        return false;
    }
    
    @Override
    public int getDataStartRow() {
        return 1;
    }

    
    @Override
    public CdbEntityController getEntityController() {
        return SourceController.getInstance();
    }
}
