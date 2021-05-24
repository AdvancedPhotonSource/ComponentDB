/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ConnectorTypeController;
import gov.anl.aps.cdb.portal.controllers.ItemConnectorController;
import gov.anl.aps.cdb.portal.controllers.ItemController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.IdOrNameRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public abstract class ImportHelperConnectorBase extends ImportHelperBase<ItemConnector, ItemConnectorController> {
    
    private static final String KEY_NAME = "importConnectorName";
    private static final String KEY_DESCRIPTION = "importConnectorDescription";
    private static final String KEY_GENDER = "importConnectorGenderIsMale";
    private static final String KEY_TYPE = "importConnectorTypeString";

    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new IdOrNameRefColumnSpec(
                getItemColumnHeader(), 
                "item", 
                "setItem", 
                "ID or name of parent cable catalog item. Name must be unique and prefixed with '#'.", 
                null,
                ColumnModeOptions.rCREATE(), 
                getItemControllerInstance(),
                Item.class, 
                null));   
        
        specs.add(new StringColumnSpec(
                "Connector Name", 
                KEY_NAME, 
                "setImportConnectorName", 
                "Name for cable connector.", 
                null,
                ColumnModeOptions.rCREATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                "Description", 
                KEY_DESCRIPTION, 
                "setImportConnectorDescription", 
                "Connector description.", 
                null,
                ColumnModeOptions.oCREATE(), 
                128));
        
        specs.add(new IdOrNameRefColumnSpec(
                "Connector Type", 
                KEY_TYPE, 
                "setImportConnectorType", 
                "ID or name of connector type. Name must be unique and prefixed with '#'.", 
                null,
                ColumnModeOptions.oCREATE(), 
                ConnectorTypeController.getInstance(), 
                ConnectorType.class, 
                null));   
        
        specs.add(new BooleanColumnSpec(
                "Is Male", 
                KEY_GENDER, 
                "setImportConnectorGenderIsMale", 
                "True/yes/1 if connector gender is male.", 
                null,
                ColumnModeOptions.oCREATE()));
        
        return specs;
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
        String connectorDesc = (String) rowMap.get(KEY_DESCRIPTION);
        Boolean connectorIsMale = (Boolean) rowMap.get(KEY_GENDER);
        ConnectorType connectorType = (ConnectorType) rowMap.get(KEY_TYPE);

        itemConnector.setImportConnectorDetails(connectorName, connectorDesc, connectorIsMale, connectorType);
            
        return new CreateInfo(itemConnector, isValid, validString);
    }
    
    protected abstract String getItemColumnHeader();
    
    protected abstract ItemController getItemControllerInstance();
    
}
