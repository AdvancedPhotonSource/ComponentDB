/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.controllers.DomainController;
import gov.anl.aps.cdb.portal.controllers.ItemTypeController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ParseInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.DomainItemTypeListColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
public class DomainItemTypeListInputHandler extends InputHandler {
    
    private final RefInputHandler domainHandler;
    private final RefInputHandler itemTypeListHandler;
    
    public DomainItemTypeListInputHandler(int firstIndex) {
        super(firstIndex, firstIndex+1);
        
        domainHandler = new RefInputHandler(
                firstIndex,
                DomainItemTypeListColumnSpec.HEADER_DOMAIN,
                DomainItemTypeListColumnSpec.PROPERTY_DOMAIN,
                DomainItemTypeListColumnSpec.SETTER_DOMAIN,
                DomainController.getInstance(),
                Domain.class,
                "",
                false,
                true);
        
        itemTypeListHandler = new RefInputHandler(
                firstIndex+1,
                DomainItemTypeListColumnSpec.HEADER_ITEM_TYPE,
                DomainItemTypeListColumnSpec.PROPERTY_ITEM_TYPE,
                DomainItemTypeListColumnSpec.SETTER_ITEM_TYPE,
                ItemTypeController.getInstance(),
                List.class,
                null,
                false,
                false);
    }
    
    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        // process domain column value
        String domainCellValue = cellValueMap.get(domainHandler.getColumnIndex());
        Domain domain = null;
        if (domainCellValue != null && (!domainCellValue.isEmpty())) {
            ParseInfo result = domainHandler.parseCellValue(domainCellValue);
            if (!result.getValidInfo().isValid()) {
                isValid = false;
                validString = result.getValidInfo().getValidString();
            } else {
                // add domain to row dictionary
                Object parsedValue = result.getValue();
                rowMap.put(domainHandler.getPropertyName(), parsedValue);
                domain = (Domain) parsedValue;
                if (domain == null) {
                    isValid = false;
                    validString = "Unexpected object returned from domain column: " + parsedValue.getClass().getName();
                }
            }
        }
        if (!isValid) {
            return new ValidInfo(isValid, validString);
        }
        
        // process item type column value
        String itemTypeCellValue = cellValueMap.get(itemTypeListHandler.getColumnIndex());
        if ((itemTypeCellValue != null) && (!itemTypeCellValue.isEmpty())) {
            if (domain == null) {
                isValid = false;
                validString = "Domain must be specified to use item type list column.";
            } else {
                itemTypeListHandler.setDomainNameFilter(domain.getName());
                ParseInfo itemTypeResult = itemTypeListHandler.parseCellValue(itemTypeCellValue);
                if (!itemTypeResult.getValidInfo().isValid()) {
                    isValid = false;
                    validString = itemTypeResult.getValidInfo().getValidString();
                } else {
                    Object parsedValue = itemTypeResult.getValue();
                    rowMap.put(itemTypeListHandler.getPropertyName(), parsedValue);
                    List<Domain> itemTypeList = (List<Domain>) parsedValue;
                    if (itemTypeList == null) {
                        isValid = false;
                        validString = "Unexpected object returned from item type list column: " + parsedValue.getClass().getName();
                    }
                }
            }
        }

        return new ValidInfo(isValid, validString);
    }

    @Override
    public ValidInfo updateEntity(Map<String, Object> rowMap, CdbEntity entity) {
        ValidInfo domainValidInfo = domainHandler.updateEntity(rowMap, entity);
        if (!domainValidInfo.isValid()) {
            return domainValidInfo;
        }
        return itemTypeListHandler.updateEntity(rowMap, entity);
    }
}
