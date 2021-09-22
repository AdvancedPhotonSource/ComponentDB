/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author craig
 */
public class ImportHelperMachineItemUpdate extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {

    private MachineImportHelperCommon machineImportHelperCommon = null;
    
    private MachineImportHelperCommon getMachineImportHelperCommon() {
        if (machineImportHelperCommon == null) {
            machineImportHelperCommon = new MachineImportHelperCommon();
        }
        return machineImportHelperCommon;
    }
    
    public String getOptionExportNumLevels() {
        return getMachineImportHelperCommon().getOptionExportNumLevels();
    }

    public void setOptionExportNumLevels(String numLevels) {
        getMachineImportHelperCommon().setOptionExportNumLevels(numLevels);
    }
    
    @Override
    protected List<HelperWizardOption> initializeExportWizardOptions() {        
        List<HelperWizardOption> options = new ArrayList<>();        
        options.add(MachineImportHelperCommon.optionExportNumLevels());
        return options;
    }

    @Override
    public ValidInfo validateExportWizardOptions() {
        return HelperWizardOption.validateIntegerOption(
                getMachineImportHelperCommon().getOptionExportNumLevels(),
                MachineImportHelperCommon.OPTION_EXPORT_NUM_LEVELS);
    }
    
    @Override
    public List<ItemDomainMachineDesign> generateExportEntityList_() {
        
        Integer numLevels = null;
        String optionVal = getOptionExportNumLevels();
        if ((optionVal != null) && (!optionVal.isBlank())) {
            numLevels = Integer.valueOf(optionVal);
        }

        ItemDomainMachineDesignTreeNode currentTree = 
                getEntityController().getCurrentMachineDesignListRootTreeNode();
        // List<ItemDomainMachineDesign> filteredItems = currentTree.getFilterResults();

        // create list from tree node hierarchy
        List<ItemDomainMachineDesign> entityList = 
                ItemDomainMachineDesignController.createListForTreeNodeHierarchy(
                        currentTree,
                        numLevels != null,
                        numLevels);
        
        return entityList;
    }

    @Override
    protected List<ColumnSpec> initColumnSpecs() {

        List<ColumnSpec> specs = new ArrayList<>();

        specs.add(existingItemIdColumnSpec());
        specs.add(MachineImportHelperCommon.parentPathColumnSpec(ColumnModeOptions.uUPDATE()));
        specs.add(MachineImportHelperCommon.nameColumnSpec(ColumnModeOptions.rUPDATE()));
        specs.add(MachineImportHelperCommon.altNameColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportHelperCommon.descriptionColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportHelperCommon.sortOrderColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportHelperCommon.assignedItemColumnSpec(ColumnModeOptions.oUPDATE()));
        specs.add(MachineImportHelperCommon.locationColumnSpec(ColumnModeOptions.oUPDATE()));
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

    @Override
    protected ItemDomainMachineDesign newInvalidUpdateInstance() {
        return getEntityController().createEntityInstance();
    }

}
