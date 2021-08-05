/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.handlers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.RefObjectManager;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbEntity;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.io.IOException;
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
public class AssignedItemHandler extends RefInputHandler {

    public AssignedItemHandler() {
        super(MachineImportHelperCommon.HEADER_ASSIGNED_ITEM);
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

            if (parsedValue.charAt(0) == '{') {
                // parse as catalog item attribute map

                Map<String, String> attributeMap = null;
                try {
                    attributeMap = RefInputHandler.mapFromJson(parsedValue);
                } catch (CdbException ex) {
                    isValid = false;
                    validString = "Exception parsing assigned item attribute map for column: " + getColumnName();
                } catch (IOException ex) {
                    isValid = false;
                    validString = "Exception parsing assigned item attribute map for column: " + getColumnName();
                }

                if (attributeMap == null) {
                    isValid = false;
                    validString = "Exception parsing attribute map for column: " + getColumnName();
                }

                RefObjectManager mgr = getObjectManager(ItemDomainCatalogController.getInstance(), null);
                CdbEntity entity = null;
                try {
                    entity = mgr.getObjectWithAttributes(attributeMap);
                } catch (CdbException ex) {
                    isValid = false;
                    validString = "Exception looking up by attribute map for column: " + getColumnName();
                }

                if (entity == null) {
                    isValid = false;
                    validString = "Unable to find object for: " + getColumnName() + " by attribute map";
                }
                assignedItem = (Item) entity;

            } else {
                // parse as id
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
                } catch (NumberFormatException ex) {
                    String msg = "Invalid id number: " + parsedValue + " for column: " + getColumnName();
                    isValid = false;
                    validString = msg;
                }
            }

            rowMap.put(MachineImportHelperCommon.KEY_ASSIGNED_ITEM, assignedItem);
        }

        return new ValidInfo(isValid, validString);
    }

    @Override
    public ValidInfo updateEntity(Map<String, Object> rowMap, CdbEntity entity) {

        boolean isValid = true;
        String validString = "";

        ItemDomainMachineDesign item = null;
        if (!(entity instanceof ItemDomainMachineDesign)) {
            isValid = false;
            validString = "Item must be ItemDomainMachineDesign to use AssignedItemHandler.";
            return new ValidInfo(isValid, validString);
        } else {
            item = (ItemDomainMachineDesign) entity;
        }

        // set assigned item
        Item assignedItem = (Item) rowMap.get(MachineImportHelperCommon.KEY_ASSIGNED_ITEM);
        if (assignedItem != null) {
            if (assignedItem instanceof ItemDomainCatalog) {
                item.setImportAssignedCatalogItem((ItemDomainCatalog) assignedItem);
            } else if (assignedItem instanceof ItemDomainInventory) {
                item.setImportAssignedInventoryItem((ItemDomainInventory) assignedItem);
            } else {
                isValid = false;
                validString = "Invalid object type for assigned item: " + assignedItem.getClass().getName();
            }
        }

        if ((item.getIsItemTemplate()) && ((item.getImportAssignedInventoryItem() != null))) {
            // template not allowed to have assigned inventory
            isValid = false;
            validString = "Template cannot have assigned inventory item";
            return new ValidInfo(isValid, validString);
        }

        item.applyImportAssignedItem();

        return new ValidInfo(isValid, validString);
    }

}
