/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemElementController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.utilities.EntityInfoUtility;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperCatalogAssembly extends ImportHelperBase<ItemElement, ItemElementController> {

    private static final String ROW_KEY_PART_NAME = "importPartName";
    private static final String ROW_KEY_PARENT_ITEM = "importParentCatalogItem";

    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec("Catalog Item", ROW_KEY_PARENT_ITEM, "setImportParentCatalogItem", true, "ID or name of parent catalog item. Name must be unique and prefixed with '#'.", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));        
        specs.add(new StringColumnSpec("Part Name", ROW_KEY_PART_NAME, "setImportPartName", true, "Part name for assembly member.", 128));
        specs.add(new StringColumnSpec("Part Description", "importPartDescription", "setImportPartDescription", false, "Part description for assembly member.", 128));
        specs.add(new BooleanColumnSpec("Part Required", "importPartRequired", "setImportPartRequired", false, "True/yes if part is required."));
        specs.add(new StringColumnSpec("Part Catalog Item Name (optional)", "importPartCatalogItemName", "setImportPartCatalogItemName", false, "Catalog item name for part (for documentation purposes only, ignored by import).", 128));
        specs.add(new IdOrNameRefColumnSpec("Part Catalog Item ID", "importPartCatalogItem", "setImportPartCatalogItem", true, "ID or name of catalog item for part. Name must be unique and prefixed with '#'.", ItemDomainCatalogController.getInstance(), ItemDomainCatalog.class, null));        

        return specs;
    }
    
    @Override
    public ItemElementController getEntityController() {
        return ItemElementController.getInstance();
    }

    @Override
    public String getTemplateFilename() {
        return "Catalog Assembly Template";
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemElement itemElement = new ItemElement();
        EntityInfo entityInfo = EntityInfoUtility.createEntityInfo();
        itemElement.setEntityInfo(entityInfo);
        
        // validate name is unique for specified parent item
        String itemName = (String) rowMap.get(ROW_KEY_PART_NAME);
        ItemDomainCatalog itemParentCatalogItem = (ItemDomainCatalog) rowMap.get(ROW_KEY_PARENT_ITEM);
        if (itemParentCatalogItem != null) {
            // check for duplicate part name
            List<ItemElement> ieList = itemParentCatalogItem.getFullItemElementList();
            for (ItemElement ie : ieList) {
                if ((ie.getName() != null) && (ie.getName().equals(itemName))) {
                    isValid = false;
                    validString = "duplicate part name for existing item element";
                }
            }
        }
        
        return new CreateInfo(itemElement, isValid, validString);
    }
    
}
