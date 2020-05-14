/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperSource extends ImportHelperBase<Source, SourceController> {


    protected static String completionUrlValue = "/views/source/list?faces-redirect=true";
    
    @Override
    protected List<InputSpec> initializeInputSpecs_() {
        List<InputSpec> specs = new ArrayList<>();
        
        specs.add(new StringInputSpec("Name", "setName", true, "Name of vendor/manufacturer", 64));
        specs.add(new StringInputSpec("Description", "setDescription", false, "Description of vendor/manufacturer", 256));
        specs.add(new StringInputSpec("Contact Info", "setContactInfo", false, "Contact name and phone number etc", 64));
        specs.add(new StringInputSpec("URL", "setUrl", false, "URL for vendor/manufacturer", 256));
        
        return specs;
    }
    
    @Override
    protected List<OutputColumnModel> initializeTableViewColumns_() {
        List<OutputColumnModel> columns = new ArrayList<>();
        
        columns.add(new OutputColumnModel("Name", "name"));
        columns.add(new OutputColumnModel("Description", "description"));
        columns.add(new OutputColumnModel("Contact Info", "contactInfo"));
        columns.add(new OutputColumnModel("URL", "url"));
        
        return columns;
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
