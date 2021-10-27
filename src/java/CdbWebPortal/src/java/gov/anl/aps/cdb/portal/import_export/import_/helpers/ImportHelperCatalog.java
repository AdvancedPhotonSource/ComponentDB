/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCatalog extends ImportHelperCatalogBase<ItemDomainCatalog, ItemDomainCatalogController> {


    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());

        specs.add(new StringColumnSpec(
                "Name", 
                "name", 
                "setName", 
                "Catalog item name.", 
                "getName",
                ColumnModeOptions.rdCREATErUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Model Number", 
                ImportHelperCatalogBase.KEY_PART_NUM, 
                "setPartNumber", 
                "Model number.", 
                "getPartNumber",
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Textual description.", 
                "getDescription",
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        specs.add(new StringColumnSpec(
                "Alt Name", 
                "alternateName", 
                "setAlternateName", 
                "Alternate item name.", 
                "getAlternateName",
                ColumnModeOptions.oCREATEoUPDATE(), 
                128));
        
        specs.add(sourceColumnSpec());
        specs.add(projectListColumnSpec());
        specs.add(technicalSystemListColumnSpec(ItemDomainName.catalog.getValue()));
        specs.add(functionListColumnSpec(ItemDomainName.catalog.getValue()));
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());

        return specs;
    }
    
    @Override
    public ItemDomainCatalogController getEntityController() {
        return ItemDomainCatalogController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Component Catalog";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        return super.createEntityInstance(rowMap);
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
    protected ItemDomainCatalog newInvalidUpdateInstance() {
        return getEntityController().createEntityInstance();
    }

}
