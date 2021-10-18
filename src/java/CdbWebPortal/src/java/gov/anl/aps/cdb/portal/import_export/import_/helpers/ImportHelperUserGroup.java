/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.UserGroupController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.beans.UserGroupFacade;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperUserGroup extends ImportHelperBase<UserGroup, UserGroupController> {

    private static final String KEY_NAME = "name";
    
    private UserGroupFacade facade;
    
    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Name", 
                KEY_NAME, 
                "setName", 
                "Name of user group", 
                "getName", 
                ColumnModeOptions.rdCREATErUPDATE(), 
                64));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of user group", 
                "getDescription", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        return specs;
    } 
   
    @Override
    public UserGroupController getEntityController() {
        return UserGroupController.getInstance();
    }
    
    private UserGroupFacade getFacade() {
        if (facade == null) {
            facade = facade.getInstance();
        }
        return facade;
    }

    /**
     * Specifies whether helper supports updating existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "UserGroup";
    }
    
    @Override
    protected UserGroup newInvalidUpdateInstance() {
        return new UserGroup();
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        UserGroup entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  
    
}
