/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ConnectorTypeController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.beans.ConnectorTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperConnectorType extends ImportHelperBase<ConnectorType, ConnectorTypeController> {

    private static final String KEY_NAME = "name";
    
    private ConnectorTypeFacade connectorTypeFacade;
    
    @Override
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(existingItemIdColumnSpec());
        specs.add(deleteExistingItemColumnSpec());
        
        specs.add(new StringColumnSpec(
                "Name", 
                KEY_NAME, 
                "setName", 
                "Name of connector type, includes gender if appropriate", 
                "getName", 
                ColumnModeOptions.rdCREATErUPDATE(), 
                64));
        
        specs.add(new StringColumnSpec(
                "Description", 
                "description", 
                "setDescription", 
                "Description of connectorType", 
                "getDescription", 
                ColumnModeOptions.oCREATEoUPDATE(), 
                256));
        
        return specs;
    } 
   
    @Override
    public ConnectorTypeController getEntityController() {
        return ConnectorTypeController.getInstance();
    }
    
    private ConnectorTypeFacade getConnectorTypeFacade() {
        if (connectorTypeFacade == null) {
            connectorTypeFacade = connectorTypeFacade.getInstance();
        }
        return connectorTypeFacade;
    }

    /**
     * Specifies whether helper supports updating existing instances.  Defaults
     * to false. Subclasses override to customize.
     */
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    @Override
    public boolean supportsModeDelete() {
        return true;
    }

    @Override
    public String getFilenameBase() {
        return "ConnectorType";
    }
    
    @Override
    protected ConnectorType newInvalidUpdateInstance() {
        return new ConnectorType();
    }

    @Override
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {
        ConnectorType entity = getEntityController().createEntityInstance();
        return new CreateInfo(entity, true, "");
    }  
    
}