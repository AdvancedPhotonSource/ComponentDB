/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.controllers.ItemTypeController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemCategory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.ItemType;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
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
        specs.add(new IdOrNameRefColumnSpec(4, "Source", ImportHelperCatalogBase.KEY_MFR, "", false, "Item source.", SourceController.getInstance(), Source.class, null));
        specs.add(new IdOrNameRefColumnSpec(5, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        specs.add(new IdOrNameRefColumnSpec(6, "Technical System", "technicalSystem", "setTechnicalSystem", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.catalog.getValue()));
        specs.add(new IdOrNameRefColumnSpec(7, "Function", "function", "setFunction", false, "Numeric ID of CDB function.", ItemTypeController.getInstance(), ItemType.class, ItemDomainName.catalog.getValue()));

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
