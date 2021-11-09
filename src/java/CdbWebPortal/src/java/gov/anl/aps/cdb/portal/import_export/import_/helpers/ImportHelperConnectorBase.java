/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ConnectorTypeController;
import gov.anl.aps.cdb.portal.controllers.ItemConnectorController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCatalogBaseController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class ImportHelperConnectorBase extends ImportHelperBase<ItemConnector, ItemConnectorController> {
    
    private static final String KEY_NAME = "importConnectorName";
    private static final String KEY_DESCRIPTION = "importConnectorDescription";
    private static final String KEY_TYPE = "importConnectorTypeString";
    private static final String KEY_CABLE_END = "importCableEnd";
    
    protected IdOrNameRefColumnSpec parentItemColumnSpec(String label, String description) {
        return new IdOrNameRefColumnSpec(
                label,
                "item",
                "setItem",
                description,
                null, 
                null,
                ColumnModeOptions.rCREATE(),
                getItemControllerInstance(),
                Item.class,
                null);        
    }
    
    protected StringColumnSpec connectorNameColumnSpec(String label, String description) {
        return new StringColumnSpec(
                label,
                KEY_NAME,
                "setImportConnectorName",
                description,
                null,
                ColumnModeOptions.rCREATE(),
                128);
    }
    
    protected StringColumnSpec cableEndColumnSpec() {
        return new StringColumnSpec(
                "Cable End",
                KEY_CABLE_END,
                "setImportCableEnd",
                "Cable end designation ('1' or '2').",
                "getCableEndDesignation",
                ColumnModeOptions.rCREATE(),
                256);
    }
    
    protected StringColumnSpec connectorDescriptionColumnSpec(String description) {
        return new StringColumnSpec(
                "Description",
                KEY_DESCRIPTION,
                "setImportConnectorDescription",
                description,
                null,
                ColumnModeOptions.oCREATE(),
                128);
    }
    
    protected IdOrNameRefColumnSpec connectorTypeColumnSpec() {
        return new IdOrNameRefColumnSpec(
                "Connector Type",
                KEY_TYPE,
                "setImportConnectorType",
                "ID or name of connector type. Name must be unique and prefixed with '#'.",
                null, 
                null,
                ColumnModeOptions.oCREATE(),
                ConnectorTypeController.getInstance(),
                ConnectorType.class,
                null);
    }

    @Override
    public ItemConnectorController getEntityController() {
        return ItemConnectorController.getInstance();
    }
    
    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemConnector itemConnector = new ItemConnector();
        
        // create Connector
        String connectorName = (String) rowMap.get(KEY_NAME);
        String cableEnd = (String) rowMap.get(KEY_CABLE_END);
        String connectorDesc = (String) rowMap.get(KEY_DESCRIPTION);
        ConnectorType connectorType = (ConnectorType) rowMap.get(KEY_TYPE);
        
        itemConnector.setImportConnectorDetails(connectorName, cableEnd, connectorDesc, connectorType);
            
        ValidInfo validationInfo = getItemControllerInstance().validateNewItemConnector(itemConnector);
        if (!validationInfo.isValid()) {
            isValid = false;
            validString = validationInfo.getValidString();
        }

        return new CreateInfo(itemConnector, isValid, validString);
    }
    
    protected abstract ItemDomainCatalogBaseController getItemControllerInstance();
    
}
