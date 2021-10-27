/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.ExportColumnData;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.DomainItemTypeListColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class DomainItemTypeListOutputHandler extends OutputHandler {
    
    private RefOutputHandler domainHandler = null;
    private RefOutputHandler itemTypeHandler = null;
    
    public DomainItemTypeListOutputHandler() {
        
        domainHandler = new RefOutputHandler(
                DomainItemTypeListColumnSpec.HEADER_DOMAIN, 
                DomainItemTypeListColumnSpec.DESCRIPTION_DOMAIN, 
                DomainItemTypeListColumnSpec.GETTER_DOMAIN);
        
        itemTypeHandler = new RefOutputHandler(
                DomainItemTypeListColumnSpec.HEADER_ITEM_TYPE, 
                DomainItemTypeListColumnSpec.DESCRIPTION_ITEM_TYPE, 
                DomainItemTypeListColumnSpec.GETTER_ITEM_TYPE);
    }
    
    @Override
    public ColumnValueResult handleOutput(CdbEntity entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public HandleOutputResult handleOutput(List<CdbEntity> entities, ExportMode exportMode) {

        boolean isValid = true;
        String validString = "";
        List<ExportColumnData> columnDataList = new ArrayList<>();

        // handle domain column output
        HandleOutputResult domainResult = domainHandler.handleOutput(entities, exportMode);
        if (!domainResult.getValidInfo().isValid()) {
            isValid = false;
            validString = domainResult.getValidInfo().getValidString();
        } else {
            columnDataList.addAll(domainResult.getColumnData());
        }
        
        // handle item type list column output and return result
        HandleOutputResult itemTypeResult = itemTypeHandler.handleOutput(entities, exportMode);
        if (!itemTypeResult.getValidInfo().isValid()) {
            isValid = false;
            validString = itemTypeResult.getValidInfo().getValidString();
        } else {
            columnDataList.addAll(itemTypeResult.getColumnData());
        }
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        return new HandleOutputResult(validInfo, columnDataList);
    }
        
}
