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
    protected List<InputColumnModel> initializeInputColumns_() {
        List<InputColumnModel> cols = new ArrayList<>();
        
        cols.add(new InputColumnModel(0, "Name", true, "Name of vendor/manufacturer"));
        cols.add(new InputColumnModel(1, "Description", false, "Description of vendor/manufacturer"));
        cols.add(new InputColumnModel(2, "Contact Info", false, "Contact name and phone number etc"));
        cols.add(new InputColumnModel(3, "URL", false, "URL for vendor/manufacturer"));
        
        return cols;
    }
    
    @Override
    protected List<InputHandler> initializeInputHandlers_() {
        List<InputHandler> specs = new ArrayList<>();
        
        specs.add(new StringInputHandler(0, "setName", 64));
        specs.add(new StringInputHandler(1, "setDescription", 256));
        specs.add(new StringInputHandler(2, "setContactInfo", 64));
        specs.add(new StringInputHandler(3, "setUrl", 256));
        
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
