/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.CdbEntityController;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.Source;

/**
 *
 * @author craig
 */
public class ImportHelperSource extends ImportHelperBase<Source, SourceController> {


    protected static String completionUrlValue = "/views/source/list?faces-redirect=true";
    
    @Override
    protected void createColumnModels_() {
        columns.add(new ImportHelperBase.StringColumnModel("Name", "name", "setName", true, "CommScope", 64));
        columns.add(new ImportHelperBase.StringColumnModel("Description", "description", "setDescription", false, "Describe vendor/manufacturer here.", 256));
        columns.add(new ImportHelperBase.StringColumnModel("Contact Info", "contactInfo", "setContactInfo", false, "John Smith 555-555-1212", 64));
        columns.add(new ImportHelperBase.UrlColumnModel("URL", "url", "setUrl", false, "http://www.example.com/example", 256));
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
    }
    
    @Override
    public SourceController getEntityController() {
        return SourceController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Source Template";
    }

    @Override
    protected Source createEntityInstance() {
        return getEntityController().createEntityInstance();
    }  

    protected boolean ignoreDuplicates() {
        return true;
    }
}
