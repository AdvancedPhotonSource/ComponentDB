/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.extensions;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.controllers.ImportHelperBase;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemProjectController;
import gov.anl.aps.cdb.portal.controllers.ItemTypeController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
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
public class ImportHelperCatalog extends ImportHelperBase<ItemDomainCatalog, ItemDomainCatalogController> {


    protected static String completionUrlValue = "/views/itemDomainCatalog/list?faces-redirect=true";
    
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new ImportHelperBase.StringColumnSpec(0, "Name", "name", "setName", true, "Catalog item name.", 128));
        specs.add(new ImportHelperBase.StringColumnSpec(1, "Model Number", "modelNumber", "setModelNumber", false, "Model number.", 128));
        specs.add(new ImportHelperBase.StringColumnSpec(2, "Description", "description", "setDescription", false, "Textual description.", 256));
        specs.add(new ImportHelperBase.StringColumnSpec(3, "Alt Name", "alternateName", "setAlternateName", false, "Alternate item name.", 128));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(4, "Source", "importSource", "setImportSource", false, "Item source.", SourceController.getInstance(), Source.class, null));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(5, "Project", "itemProjectString", "setProject", true, "Numeric ID of CDB project.", ItemProjectController.getInstance(), ItemProject.class, ""));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(6, "Technical System", "technicalSystem", "setTechnicalSystem", false, "Numeric ID of CDB technical system.", ItemCategoryController.getInstance(), ItemCategory.class, ItemDomainName.catalog.getValue()));
        specs.add(new ImportHelperBase.IdOrNameRefColumnSpec(7, "Function", "function", "setFunction", false, "Numeric ID of CDB function.", ItemTypeController.getInstance(), ItemType.class, ItemDomainName.catalog.getValue()));

        return specs;
    }
    
    @Override
    protected String getCompletionUrlValue() {
        return completionUrlValue;
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
        ItemDomainCatalog entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }
}
