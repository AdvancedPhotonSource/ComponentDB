/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author craig
 */
/**
 * Using a custom handler so that we can use catalog or inventory item id's in a
 * single column. There is not a way to do this with IdRef handler, as it needs
 * a particular controller instance to use for the lookup. The query we need
 * here is on the ItemFacade.
 */
public class AssignedItemHandler extends SingleColumnInputHandler {

    public AssignedItemHandler() {
        super(MachineImportCommon.HEADER_ASSIGNED_ITEM);
    }

    @Override
    public ValidInfo handleInput(
            Row row,
            Map<Integer, String> cellValueMap,
            Map<String, Object> rowMap) {

        boolean isValid = true;
        String validString = "";

        String parsedValue = cellValueMap.get(getColumnIndex());

        Item assignedItem = null;
        if ((parsedValue != null) && (!parsedValue.isEmpty())) {
            // assigned item is specified

            int id;
            try {
                id = Integer.valueOf(parsedValue);
                assignedItem = ItemFacade.getInstance().findById(id);
                if (assignedItem == null) {
                    String msg = "Unable to find object for: " + getColumnName()
                            + " with id: " + parsedValue;
                    isValid = false;
                    validString = msg;
                }
                rowMap.put(MachineImportCommon.KEY_ASSIGNED_ITEM, assignedItem);

            } catch (NumberFormatException ex) {
                String msg = "Invalid id number: " + parsedValue + " for column: " + getColumnName();
                isValid = false;
                validString = msg;
            }
        }

        return new ValidInfo(isValid, validString);
    }
    
}
