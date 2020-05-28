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
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperSource extends ImportHelperBase<Source, SourceController> {


    protected static String completionUrlValue = "/views/source/list?faces-redirect=true";
    
    private List<InputColumnModel> getInputColumns() {
        List<InputColumnModel> inputColumns = new ArrayList<>();        
        inputColumns.add(new InputColumnModel(0, "Name", true, "Name of vendor/manufacturer"));
        inputColumns.add(new InputColumnModel(1, "Description", false, "Description of vendor/manufacturer"));
        inputColumns.add(new InputColumnModel(2, "Contact Info", false, "Contact name and phone number etc"));
        inputColumns.add(new InputColumnModel(3, "URL", false, "URL for vendor/manufacturer"));
        return inputColumns;
    }
    
    @Override
    protected List<InputColumnModel> getTemplateColumns() {
        return getInputColumns();
    }
    
    @Override
    protected InitializeInfo initialize_(
            int actualColumnCount,
            Map<Integer, String> headerValueMap) {

        List<InputHandler> handlers = new ArrayList<>();        
        handlers.add(new StringInputHandler(0, "setName", 64));
        handlers.add(new StringInputHandler(1, "setDescription", 256));
        handlers.add(new StringInputHandler(2, "setContactInfo", 64));
        handlers.add(new StringInputHandler(3, "setUrl", 256));

        List<OutputColumnModel> outputColumns = new ArrayList<>();        
        outputColumns.add(new OutputColumnModel("Name", "name"));
        outputColumns.add(new OutputColumnModel("Description", "description"));
        outputColumns.add(new OutputColumnModel("Contact Info", "contactInfo"));
        outputColumns.add(new OutputColumnModel("URL", "url"));
        
        ValidInfo validInfo = new ValidInfo(true, "");
        
        return new InitializeInfo(getInputColumns(), handlers, outputColumns, validInfo);
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
