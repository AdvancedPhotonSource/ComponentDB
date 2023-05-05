/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.DomainController;
import gov.anl.aps.cdb.portal.controllers.ItemTypeController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.FloatColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperItemType extends ImportHelperBase<ItemType, ItemTypeController> {
    
    private static final String KEY_SORT_ORDER = "sortOrder";
    private static final String HEADER_SORT_ORDER = "Sort Order";

    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new IdOrNameRefColumnSpec(
                "Domain",
                "domain",
                "setDomain",
                "Id or name of CDB domain (name must be preceded by # character).",
                "getDomain", 
                null,
                ColumnModeOptions.rCREATErUPDATE(),
                DomainController.getInstance(),
                Domain.class,
                ""));
        
        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
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
        
        specs.add(new FloatColumnSpec(
                HEADER_SORT_ORDER, 
                KEY_SORT_ORDER, 
                "setSortOrder", 
                "Sort order within parent catalog item (as decimal), defaults to order in input sheet.", 
                "getSortOrder",
                null,
                ColumnModeOptions.oCREATEoUPDATE()));
        
        return specs;
    } 
   
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }
    
    @Override
    public boolean supportsModeTransfer() {
        return true;
    }

    @Override
    public ItemTypeController getEntityController() {
        return ItemTypeController.getInstance();
    }
    
    @Override
    public String getFilenameBase() {
        return "ItemType (Function)";
    }
    
    @Override
    protected ItemType newInvalidUpdateInstance() {
        return new ItemType();
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ItemType entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  
    
}
