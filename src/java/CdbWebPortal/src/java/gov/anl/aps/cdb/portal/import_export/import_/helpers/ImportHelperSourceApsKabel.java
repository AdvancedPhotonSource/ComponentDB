/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IgnoreColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperSourceApsKabel extends ImportHelperBase<Source, SourceController> {
    
    private static final String KEY_NAME = "name";
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec("Cable Type Name", "importItemName", "setImportItemName", true, "Name of cable type specifying this manufacturer.", 64));
        specs.add(new IgnoreColumnSpec(1));
        specs.add(new StringColumnSpec("Manufacturer (Source)", KEY_NAME, "setName", true, "Name of vendor/manufacturer.", 64));
        specs.add(new IgnoreColumnSpec(20));
        
        return specs;
    } 
   
    @Override
    public SourceController getEntityController() {
        return SourceController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "APS Kabel Workbook Source Template";
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        Source entity;
        String itemName = (String) rowMap.get(KEY_NAME);
        if ((itemName == null) || (itemName.isEmpty())) {
            return new CreateInfo(null, true, "no manufacturer specified for row");
        } else {
            entity = getEntityController().createEntityInstance();
        }
        return new CreateInfo(entity, true, "");
    }  

    @Override
    protected boolean ignoreDuplicates() {
        return true;
    }
}
