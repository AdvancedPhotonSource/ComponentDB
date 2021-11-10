/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ConnectorTypeController;
import gov.anl.aps.cdb.portal.controllers.ItemConnectorController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogBaseController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalogBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class ImportHelperConnectorBase extends ImportHelperBase<ItemConnector, ItemConnectorController> {
    
    private static final String KEY_ITEM = "item";
    private static final String KEY_NAME = "importConnectorName";
    private static final String KEY_DESCRIPTION = "importConnectorDescription";
    private static final String KEY_TYPE = "importConnectorType";
    private static final String KEY_CABLE_END = "importCableEnd";
    
    private String labelItem = null;
    private String labelName = null;
    
    private String getLabelItem() {
        return labelItem;
    }
    
    private void setLabelItem(String label) {
        labelItem = label;
    }
    
    private String getLabelName() {
        return labelName;
    }
    
    private void setLabelName(String label) {
        labelName = label;
    }
    
    protected IdOrNameRefColumnSpec parentItemColumnSpec(String label, String description) {
        setLabelItem(label);
        return new IdOrNameRefColumnSpec(
                label,
                KEY_ITEM,
                "setItem",
                description,
                "getItem", 
                "getItem",
                ColumnModeOptions.rCREATErUPDATE(),
                getItemControllerInstance(),
                Item.class,
                null);        
    }
    
    protected StringColumnSpec connectorNameColumnSpec(String label, String description) {
        setLabelName(label);
        return new StringColumnSpec(
                label,
                KEY_NAME,
                "setImportConnectorName",
                description,
                "getImportConnectorName",
                ColumnModeOptions.rCREATErUPDATE(),
                128);
    }
    
    protected StringColumnSpec cableEndColumnSpec() {
        return new StringColumnSpec(
                "Cable End",
                KEY_CABLE_END,
                "setImportCableEnd",
                "Cable end designation ('1' or '2').",
                "getImportCableEnd",
                ColumnModeOptions.rCREATErUPDATE(),
                256);
    }
    
    protected StringColumnSpec connectorDescriptionColumnSpec(String description) {
        return new StringColumnSpec(
                "Description",
                KEY_DESCRIPTION,
                "setImportConnectorDescription",
                description,
                "getImportConnectorDescription",
                ColumnModeOptions.oCREATEoUPDATE(),
                128);
    }
    
    protected IdOrNameRefColumnSpec connectorTypeColumnSpec() {
        return new IdOrNameRefColumnSpec(
                "Connector Type",
                KEY_TYPE,
                "setImportConnectorType",
                "ID or name of connector type. Name must be unique and prefixed with '#'.",
                "getImportConnectorType", 
                "getImportConnectorType",
                ColumnModeOptions.oCREATEoUPDATE(),
                ConnectorTypeController.getInstance(),
                ConnectorType.class,
                null);
    }

    @Override
    public ItemConnectorController getEntityController() {
        return ItemConnectorController.getInstance();
    }
    
    protected abstract ItemDomainCatalogBaseController getItemControllerInstance();
    
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public boolean supportsModeTransfer() {
        return true;
    }
    
    @Override
    protected List<ItemConnector> generateExportEntityList_() {
        List<ItemConnector> entityList = new ArrayList<>();
        List<ItemDomainCatalogBase> catalogList = getItemControllerInstance().getExportEntityList();
        for (ItemDomainCatalogBase catalogItem : catalogList) {
            ItemDomainCatalogBase updatedItem = (ItemDomainCatalogBase) getItemControllerInstance().getEntityDbFacade().find(catalogItem.getId());
            entityList.addAll(updatedItem.getItemConnectorList());
        }
        return entityList;
    }
    
    private CreateInfo createUpdateEntityCommon(
            ItemConnector existingConnector, 
            Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validStr = "";
        
        // extract column values
        Item catalogItem = (Item) rowMap.get(KEY_ITEM);
        String connectorName = (String) rowMap.get(KEY_NAME);
        String cableEnd = (String) rowMap.get(KEY_CABLE_END);
        String connectorDesc = (String) rowMap.get(KEY_DESCRIPTION);
        ConnectorType connectorType = (ConnectorType) rowMap.get(KEY_TYPE);
        
        // validation for both modes
        
        if (catalogItem == null) {
            // item must be specified
            isValid = false;
            validStr = getLabelItem() + " must be specified";
        }
        
        if (connectorName == null || connectorName.isBlank()) {
            // name must be specified
            isValid = false;
            validStr = appendToString(validStr, getLabelName() + " must be specified");
            
        } else { // connectorName specified
            
            if (catalogItem != null) {
            
                // check if name is duplicated within spreadsheet
                if (nameInUse(catalogItem, connectorName)) {
                    isValid = false;
                    validStr = appendToString(
                            validStr,
                            "Duplicate use of " + getLabelName() + ": " + connectorName + " in spreadsheet");
                } else {
                    addNameInUse(catalogItem, connectorName);
                }
            }
        }
        
        ItemConnector itemConnector = null;
        boolean isUpdate = true;
        
        if (existingConnector == null) { // create new ItemConnector
            isUpdate = false;
            itemConnector = new ItemConnector();
            
            // create mode validation
            
            if (catalogItem.getConnectorNamed(connectorName) != null) {
                // name already in use for catalog item
                isValid = false;
                validStr = appendToString(validStr, getLabelName() + " already in use for " + getLabelItem());
            }

            
        } else { // update existing ItemConnector
            isUpdate = true;
            itemConnector = existingConnector;
            
            // update mode validation
            
            if (catalogItem != null) {
                
                if (!catalogItem.getId().equals(itemConnector.getItem().getId())) {
                    // can't change catalog item in update
                    isValid = false;
                    validStr = appendToString(validStr, getLabelItem() + " cannot be changed in update mode");
                }
                
                if ((!connectorName.equals(itemConnector.getConnectorName())) 
                        && (catalogItem.getConnectorNamed(connectorName) != null)) {
                    // changing name but name already in use
                    isValid = false;
                    validStr = appendToString(validStr, getLabelName() + " already in use for " + getLabelItem());
                }

            }
            
            if (cableEnd != null && (!cableEnd.equals(itemConnector.getCableEndDesignation()))) {
                // can't change cable end if connector in use for other connections
                List<Item> itemsUsingConnector = itemConnector.otherItemsUsingConnector(catalogItem);
                if (!itemsUsingConnector.isEmpty()) {
                    String itemString = "";
                    boolean first = true;
                    for (Item item : itemsUsingConnector) {
                        if (!first) {
                            itemString = itemString + ", ";
                        } else {
                            first = false;
                        }
                        itemString = itemString + item.getName();
                    }
                    isValid = false;
                    validStr = appendToString(
                            validStr, 
                            "Cable end cannot be changed because connector is in use by other items: " + itemString);
                }
            }
        }

        // set import values on ItemConnector
        itemConnector.setImportConnectorDetails(isUpdate, connectorName, cableEnd, connectorDesc, connectorType);

        // validate ItemConnector
        ValidInfo validationInfo = getItemControllerInstance().validateItemConnector(isUpdate, itemConnector);
        if (!validationInfo.isValid()) {
            isValid = false;
            validStr = appendToString(validStr, validationInfo.getValidString());
        }

        return new CreateInfo(itemConnector, isValid, validStr);
    }
        
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        return createUpdateEntityCommon(null, rowMap);
    }
    
    @Override
    protected ValidInfo updateEntityInstance(ItemConnector entity, Map<String, Object> rowMap) {
        CreateInfo updateInfo = createUpdateEntityCommon(entity, rowMap);
        return updateInfo.getValidInfo();
    }

}
