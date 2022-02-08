/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperMachineItemUpdate extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {

    public static final String PROPERTY_PARENT = "importParent";
    
    private MachineImportHelperCommon machineImportHelperCommon = null;
    
    private ItemDomainMachineDesignControllerUtility controllerUtility = null;
    
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
        
        specs.add(MachineImportHelperCommon.existingMachineItemColumnSpec(
                ColumnModeOptions.rUPDATErCOMPARE(),
                getMachineImportHelperCommon().getRootItem(),
                null,
                PROPERTY_PARENT,
                "setImportParent",
                "getImportParentPath"));
        
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
    
    private ItemDomainMachineDesignControllerUtility getControllerUtility() {
        if (controllerUtility == null) {
            controllerUtility = new ItemDomainMachineDesignControllerUtility();
        }
        return controllerUtility;
    }

    /**
     * Updates entity for row in spreadsheet. Only the parent item column is dealt
     * with here since the other columns are handled automatically by the framework.
     */
    @Override
    protected ValidInfo updateEntityInstance(
            ItemDomainMachineDesign item, Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        boolean validRow = (boolean) rowMap.get(KEY_IS_VALID);
        if (validRow) {
        
            // get parent item specified in import spreadsheet
            ItemDomainMachineDesign newParentItem = (ItemDomainMachineDesign) rowMap.get(PROPERTY_PARENT);
            ItemDomainMachineDesign oldParentItem = item.getParentMachineDesign();
            
            // The following restrictions on moving items to/from root level of hierarchy
            // are due to the persistence cascades for update.  To allow moving from the root
            // level of the hierarchy, we'll need to call persistence create for the new ItemElement
            // when we update the Item.  Similarly for moving to the root level, we'll
            // need to delete the existing ItemElement when we update the item.
            // In the past, similar things have been accomplished by adding the ItemElement
            // to a transient variable list like elementsToCreate/Delete, and then overriding update() in the facade
            // to also create or delete the associated ItemElements in elementsToCreate/Delete.
            if ((newParentItem == null)) {
                if (oldParentItem != null) {
                    isValid = false;
                    validString = "Item cannot be moved to root level of hierarchy";
                }

            } else {
                
                // check to see if parent item changed
                if (!newParentItem.equals(item.getParentMachineDesign())) {
                    UserInfo user = item.getOwnerUser();
                    ItemElement relationshipElement = null;
                    try {
                        relationshipElement = getControllerUtility().performMachineMove(newParentItem, item, user);
                        if (oldParentItem == null) {
                            // handle case where moving item from root level of hierarchy, 
                            // need to create new ItemElement for relationship 
                            // between new parent and child in database, so add to collection and
                            // facade will create ItemElement when updating the item.
                            item.getNewElementList().add(relationshipElement);
                        }
                    } catch (CdbException ex) {
                        isValid = false;
                        validString = "Exception moving item to new parent: " + ex.getMessage();
                    }
                }
            }
        }
        
        return new ValidInfo(isValid, validString);
    }
    
}
