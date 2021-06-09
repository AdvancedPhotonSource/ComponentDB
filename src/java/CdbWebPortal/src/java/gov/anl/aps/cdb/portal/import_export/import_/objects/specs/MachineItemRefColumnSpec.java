/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.objects.specs;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.InputHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.MachineItemRefInputHandler;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.List;

/**
 *
 * @author craig
 */
public class MachineItemRefColumnSpec extends IdOrNameRefColumnSpec {
    
    ItemDomainMachineDesign rootItem = null;
    
    public MachineItemRefColumnSpec(
            String header, 
            String importPropertyName, 
            String importSetterMethod, 
            String description,
            String exportGetterMethod,
            List<ColumnModeOptions> options,
            ItemDomainMachineDesign rootItem) {
        
        super(header, 
                importPropertyName, 
                importSetterMethod, 
                description, 
                exportGetterMethod, null, 
                options, 
                ItemDomainMachineDesignController.getInstance(), 
                ItemDomainMachineDesign.class, 
                null);
        
        this.rootItem = rootItem;
    }

    @Override
    public InputHandler getInputHandler(int colIndex) {
        return new MachineItemRefInputHandler(
                colIndex,
                getHeader(),
                getPropertyName(),
                getEntitySetterMethod(),
                controller,
                paramType,
                rootItem,
                false,
                true,
                true);
    }
}
