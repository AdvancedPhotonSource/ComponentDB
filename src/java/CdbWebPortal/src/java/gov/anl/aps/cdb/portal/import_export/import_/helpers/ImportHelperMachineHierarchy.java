/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperMachineHierarchy 
        extends ImportHelperHierarchyBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {
        
    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineHierarchy.class.getName());
    
    private Map<Integer, ItemDomainMachineDesign> parentIndentMap = new HashMap<>();
    
    private int nonTemplateItemCount = 0;
    private int templateItemCount = 0;
    
    private MachineImportHelperCommon machineImportHelperCommon = null;
    
    private MachineImportHelperCommon getMachineImportHelperCommon() {
        if (machineImportHelperCommon == null) {
            machineImportHelperCommon = new MachineImportHelperCommon();
        }
        return machineImportHelperCommon;
    }
    
    public String getOptionImportRootItemName() {
        return getMachineImportHelperCommon().getOptionImportRootItemName();
    }

    public void setOptionImportRootItemName(String optionRootItemName) {
        getMachineImportHelperCommon().setOptionImportRootItemName(optionRootItemName);
    }
    
    @Override
    protected List<HelperWizardOption> initializeImportWizardOptions() {        
        List<HelperWizardOption> options = new ArrayList<>();        
        options.add(MachineImportHelperCommon.optionImportRootMachineItem());
        return options;
    }

    @Override
    public ValidInfo validateImportWizardOptions() {
        return getMachineImportHelperCommon().validateOptionImportRootItemName();
    }
    
    @Override
    protected List<ItemDomainMachineDesign> generateExportEntityList_() {
        
        ItemDomainMachineDesignTreeNode currentTree = 
                getEntityController().getCurrentMachineDesignListRootTreeNode();

        // create list from tree node hierarchy
        List<ItemDomainMachineDesign> entityList = 
                ItemDomainMachineDesignController.createListForTreeNodeHierarchy(
                        currentTree,
                        false,
                        null);
        
        return entityList;
    }

    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(MachineImportHelperCommon.parentItemColumnSpec(
                ColumnModeOptions.oCREATE(), getMachineImportHelperCommon()));
        specs.add(MachineImportHelperCommon.nameHierarchyColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.altNameColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.descriptionColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.sortOrderColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.assignedItemDescriptionColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.assignedItemColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.locationColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(locationDetailsColumnSpec());
        specs.add(MachineImportHelperCommon.isTemplateColumnSpec(ColumnModeOptions.rCREATE()));        
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
        return "Machine Hierarchy";
    }
    
    /**
     * Specifies whether helper supports export.
     * Subclasses override to customize.
     */
    public boolean supportsModeExport() {
        return false;
    }

    @Override
    public boolean supportsModeTransfer() {
        return true;
    }

    @Override
    protected void reset_() {
        super.reset_();
        nonTemplateItemCount = 0;
        templateItemCount = 0;
        getMachineImportHelperCommon().reset();
    }
    
    @Override
    protected ItemDomainMachineDesign getItemParent(ItemDomainMachineDesign item) {
        return null;
    }
    
    @Override
    protected String getItemName(ItemDomainMachineDesign item) {
        return item.getName();
    }
    
    @Override
    protected List<ItemDomainMachineDesign> getItemChildren(ItemDomainMachineDesign item) {
        List<ItemElement> children = item.getItemElementDisplayList();
        return children.stream()
                .map((child) -> (ItemDomainMachineDesign) child.getContainedItem())
                .collect(Collectors.toList());
    }
            
    @Override
    protected ItemDomainMachineDesign newInstance_() {
        return getEntityController().createEntityInstance();
    }
    
    @Override
    protected String getKeyName_() {
        return MachineImportHelperCommon.KEY_NAME;
    }
            
    @Override
    protected String getKeyIndent_() {
        return MachineImportHelperCommon.KEY_INDENT;
    }
            
    @Override
    protected String getKeyParent_() {
        return MachineImportHelperCommon.KEY_PARENT;
    }
            
    @Override
    protected ValidInfo initEntityInstance_(
            ItemDomainMachineDesign item,
            ItemDomainMachineDesign itemParent,
            Map<String, Object> rowMap,
            String itemName,
            String itemPath,
            int itemSiblingNumber) {
        
        boolean isValid = true;
        String validString = "";

        // determine sort order
        Float itemSortOrder = (Float) rowMap.get(MachineImportHelperCommon.KEY_SORT_ORDER);
        if ((itemSortOrder == null) && (itemParent != null)) {
            // get maximum sort order value for existing children
            Float maxSortOrder = itemParent.getMaxSortOrder();
            itemSortOrder = maxSortOrder + 1;
        }
        item.setImportSortOrder(itemSortOrder);
        
        item.setName(itemName);
        item.setImportPath(itemPath);
        
        // set uuid in case there are duplicate names
        String viewUUID = item.getViewUUID();
        item.setItemIdentifier2(viewUUID);

        // set flag indicating item is template
        Boolean itemIsTemplate = (Boolean) rowMap.get(MachineImportHelperCommon.KEY_IS_TEMPLATE);
        if (itemIsTemplate != null) {
            item.setImportIsTemplate(itemIsTemplate);
        } else {
            // return because we need this value to continue
            isValid = false;
            validString = ""; // we don't need a message because this is already flagged as invalid because it is a required column"
            return new ValidInfo(isValid, validString);
        }

        if (item.getIsItemTemplate()) {
            templateItemCount = templateItemCount + 1;
        } else {            
            nonTemplateItemCount = nonTemplateItemCount + 1;
        }

        if (itemParent != null) {
            // handling for all items with parent, template or non-template
            if (!Objects.equals(item.getIsItemTemplate(), itemParent.getIsItemTemplate())) {
                // parent and child must both be templates or both not be
                String msg = "parent and child must both be templates or both not be templates";
                validString = appendToString(validString, msg);
                isValid = false;
            }            
            item.setImportChildParentRelationship(itemParent, itemSortOrder);
        }
        
        return new ValidInfo(isValid, validString);
    }

    protected String getCustomSummaryDetails() {
        
        String summaryDetails = "";
        
        if (templateItemCount > 0) {
            String templateItemDetails = templateItemCount + " template items";
            if (summaryDetails.isEmpty()) {
                summaryDetails = templateItemDetails;                        
            } else {
                summaryDetails = summaryDetails + ", " + templateItemDetails;
            }
        }
        
        if (nonTemplateItemCount > 0) {
            String nontemplateItemDetails = nonTemplateItemCount + " regular items";
            if (summaryDetails.isEmpty()) {
                summaryDetails = nontemplateItemDetails;
            } else {
                summaryDetails = summaryDetails + ", " + nontemplateItemDetails;
            }
        }
        
        return summaryDetails;
    }
}