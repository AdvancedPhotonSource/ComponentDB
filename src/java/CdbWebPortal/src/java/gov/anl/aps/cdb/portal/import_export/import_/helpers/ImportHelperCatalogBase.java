/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogBaseController;
import gov.anl.aps.cdb.portal.controllers.SourceController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import gov.anl.aps.cdb.portal.model.db.entities.Source;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class ImportHelperCatalogBase <CatalogEntityType extends ItemDomainCatalogBase, CatalogEntityControllerType extends ItemDomainCatalogBaseController> 
        extends ImportHelperBase<CatalogEntityType, CatalogEntityControllerType> {
    
    protected final static String KEY_MFR = "sourceString";
    protected final static String KEY_PART_NUM = "partNumber";
    
    public IdOrNameRefColumnSpec sourceColumnSpec(int colIndex) {
        return new IdOrNameRefColumnSpec(
                "Source", 
                ImportHelperCatalogBase.KEY_MFR, 
                "", 
                "Item source.", 
                null,
                ColumnModeOptions.oCREATEoUPDATE(),
                SourceController.getInstance(), 
                Source.class, 
                null);
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemDomainCatalogBase entity = getEntityController().createEntityInstance();
        
        // create ItemSource for manufacturer and part number
        Source itemSource = (Source) rowMap.get(KEY_MFR);
        String itemPartNum = (String) rowMap.get(KEY_PART_NUM);
        if (itemSource != null) {
            entity.setManufacturerInfo(itemSource, itemPartNum);
        }
        
        return new CreateInfo(entity, true, "");
    }  

    @Override
    protected ValidInfo updateEntityInstance(CatalogEntityType entity, Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        // update or delete ItemSource if manufacturer is updated
        Source itemSource = (Source) rowMap.get(KEY_MFR);
        String itemPartNum = (String) rowMap.get(KEY_PART_NUM);
        
        if (itemSource != null) {
            entity.updateManufacturerInfo(itemSource, itemPartNum);
        } else {
            entity.removeManufactuterInfo();
        }
        
        return new ValidInfo(isValid, validString);
    }
    
}
