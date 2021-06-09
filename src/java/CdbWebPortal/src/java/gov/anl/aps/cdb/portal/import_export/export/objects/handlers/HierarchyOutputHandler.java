/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.export.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.export.objects.ColumnValueResult;
import gov.anl.aps.cdb.portal.import_export.export.objects.ExportColumnData;
import gov.anl.aps.cdb.portal.import_export.export.objects.HandleOutputResult;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ExportMode;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class HierarchyOutputHandler extends OutputHandler {

    @Override
    public ColumnValueResult handleOutput(CdbEntity entity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public HandleOutputResult handleOutput(List<CdbEntity> entities, ExportMode exportMode) {
        
        boolean isValid = true;
        String validString = "";  
        List<ExportColumnData> columns = new ArrayList<>();
        Map<Integer,Integer> indentMap = new HashMap<>();
        int maxIndentLevel = 0;
        
        // determine indent level for each item from its parent
        for (CdbEntity entity : entities) {
            
            if (entity instanceof ItemDomainMachineDesign) {
                
                ItemDomainMachineDesign item = (ItemDomainMachineDesign) entity;
                
                Integer indentLevel = 0;
                ItemDomainMachineDesign parent = item.getParentMachineDesign();
                if (parent != null) {
                    if (!indentMap.containsKey(parent.getId())) {
                        isValid = false;
                        validString = "Parent machine item with id: " 
                                + parent.getId() + " not included in export entity list";
                        break;
                    } else {
                        indentLevel = indentMap.get(parent.getId()) + 1;
                    }
                }
                indentMap.put(item.getId(), indentLevel);
                if (indentLevel > maxIndentLevel) {
                    maxIndentLevel = indentLevel;
                }
                
            } else {
                isValid = false;
                validString = "Export entity: " 
                        + entity.toString() + " is not instance of ItemDomainMachineDesign";
                break;
            }
        }
        
        if (isValid) {
            
            // create output columns
            for (int i = 0 ; i <= maxIndentLevel ; ++i) {
                ExportColumnData column = new ExportColumnData(
                        MachineImportHelperCommon.HEADER_BASE_LEVEL + " " + String.valueOf(i+1), 
                        "Machine hierarchy level.", 
                        new ArrayList<>());
                columns.add(column);
            }
            
            // generate output column values
            for (CdbEntity entity : entities) {
                ItemDomainMachineDesign item = (ItemDomainMachineDesign) entity;
                Integer itemIndentLevel = indentMap.get(item.getId());
                int colIndex = 0;
                for (ExportColumnData column : columns) {
                    if (colIndex == itemIndentLevel) {
                        column.add(item.getName());
                    } else {
                        column.add("");
                    }
                    colIndex = colIndex + 1;
                }
            }
        }
        
        ValidInfo validInfo = new ValidInfo(isValid, validString);
        HandleOutputResult result = new HandleOutputResult(validInfo, columns);
        return result;
    }

}
