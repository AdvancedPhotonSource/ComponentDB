/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperMachineItemUpdate extends ImportHelperBase {

    @Override
    protected List<ColumnSpec> getColumnSpecs() {

        List<ColumnSpec> specs = new ArrayList<>();

        specs.add(existingItemIdColumnSpec());
        specs.add(MachineImportCommon.nameColumnSpec(ColumnModeOptions.rUPDATE()));
        specs.add(MachineImportCommon.altNameColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportCommon.descriptionColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportCommon.sortOrderColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportCommon.assignedItemColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportCommon.locationColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(locationDetailsColumnSpec());
        specs.add(projectListColumnSpec());
        specs.add(ownerUserColumnSpec());
        specs.add(ownerGroupColumnSpec());

        return specs;
    }
    
    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Machine Element Update";
    }

    /**
     * Specifies whether helper supports creation of new instances.
     */
    @Override
    public boolean supportsModeCreate() {
        return false;
    }

    /**
     * Specifies whether helper supports updating existing instances.
     */
    @Override
    public boolean supportsModeUpdate() {
        return true;
    }

    /**
     * Specifies whether helper supports deleting existing instances.
     */
    @Override
    public boolean supportsModeDelete() {
        return false;
    }

}
