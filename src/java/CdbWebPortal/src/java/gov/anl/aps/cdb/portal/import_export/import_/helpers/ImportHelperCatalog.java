/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
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
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new StringColumnSpec(0, "Name", "name", "setName", true, "Catalog item name.", 128));
        specs.add(new StringColumnSpec(1, "Model Number", ImportHelperCatalogBase.KEY_PART_NUM, "setPartNumber", false, "Model number.", 128));
        specs.add(new StringColumnSpec(2, "Description", "description", "setDescription", false, "Textual description.", 256));
        specs.add(new StringColumnSpec(3, "Alt Name", "alternateName", "setAlternateName", false, "Alternate item name.", 128));
        specs.add(sourceColumnSpec(4));
        specs.add(projectListColumnSpec(5));
        specs.add(technicalSystemListColumnSpec(6, ItemDomainName.catalog.getValue()));
        specs.add(functionListColumnSpec(7, ItemDomainName.catalog.getValue()));
        specs.add(ownerUserColumnSpec(8));
        specs.add(ownerGroupColumnSpec(9));

        return specs;
    }
    
    @Override
    public ItemDomainCatalogController getEntityController() {
        return ItemDomainCatalogController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Component Catalog Template";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        return super.createEntityInstance(rowMap);
    }  
}
