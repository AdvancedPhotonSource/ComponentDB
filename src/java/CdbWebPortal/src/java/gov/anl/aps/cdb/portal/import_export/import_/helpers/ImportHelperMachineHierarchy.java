/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import static gov.anl.aps.cdb.portal.import_export.import_.helpers.ImportHelperBase.KEY_USER;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import static gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon.KEY_ASSIGNED_ITEM;
import static gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon.KEY_ASSEMBLY_PART;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.ArrayList;
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
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(MachineImportHelperCommon.existingMachineItemColumnSpec(
                ColumnModeOptions.oCREATE(), getMachineImportHelperCommon().getRootItem(), null, null, null, null));
        specs.add(MachineImportHelperCommon.nameHierarchyColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.altNameColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.descriptionColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.sortOrderColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.assignedItemColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.assemblyPartColumnSpec(ColumnModeOptions.oCREATE()));
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
    protected String getKeyName_() {
        return MachineImportHelperCommon.KEY_NAME;
    }
            
    @Override
    protected String getKeyIndent_() {
        return MachineImportHelperCommon.KEY_INDENT;
    }
            
    @Override
    protected String getKeyParent_() {
        return MachineImportHelperCommon.KEY_MD_ITEM;
    }
            
    @Override
    protected CreateInfo createEntityInstance_(
            ItemDomainMachineDesign itemParent,
            Map<String, Object> rowMap,
            String itemName,
            String itemPath,
            int itemSiblingNumber) {
        
        boolean isValid = true;
        String validString = "";
        
        ItemDomainMachineDesign item = getEntityController().createEntityInstance();

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
            return new CreateInfo(item, isValid, validString);
        }

        // template items cannot have assigned inventory - only catalog
        Item assignedItem = (Item) rowMap.get(KEY_ASSIGNED_ITEM);
        if ((item.getIsItemTemplate()) && ((assignedItem instanceof ItemDomainInventory))) {
            isValid = false;
            validString = "Template cannot have assigned inventory item, must use catalog item";
            return new CreateInfo(item, isValid, validString);
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
                return new CreateInfo(item, isValid, validString);
            }
        }
        
        String assemblyPartName = (String) rowMap.get(KEY_ASSEMBLY_PART);
        if (assemblyPartName != null) {
            assemblyPartName = assemblyPartName.trim();
        }
        
        // handling for non-promoted items
        if (assemblyPartName == null) {
            
            item.setImportChildParentRelationship(itemParent, itemSortOrder);

        } else {
            // create promoted machine item from assembly element if "assembly part" column specified
            // until we invoke utility to create promoted item, "item" variable is used as a placeholder
            // for reporting errors so that the entity shows up correctly in the import wizard validation table
            
            // can't specify assigned item when using assembly part column
            if (assignedItem != null) {
                isValid = false;
                validString = appendToString(validString, "Assigned item cannot be used with assembly part column");
                return new CreateInfo(item, isValid, validString);
            }
            
            // parent must be specified
            if (itemParent == null) {
                isValid = false;
                validString = appendToString(validString, "Parent item must be specified to use assembly part column");
                return new CreateInfo(item, isValid, validString);
            }
            
            // get catalog assembly for parent
            ItemDomainCatalog parentCatalogItem = null;            
            ItemElement parentCatalogElement = itemParent.getRepresentsCatalogElement();
            if (parentCatalogElement != null) {
                // treat parent as promoted machine item that represents catalog element                
                parentCatalogItem = (ItemDomainCatalog) parentCatalogElement.getContainedItem();                
            } else {
                // otherwise get parent's assigned catalog item            
                parentCatalogItem = itemParent.getCatalogItem();
            }
            
            // check that parent has assigned catalog item or represents a catalog item
            if ((parentCatalogItem == null)) {
                isValid = false;
                validString = appendToString(validString, "Catalog item assigned to or represented by parent item must be assembly");
                return new CreateInfo(item, isValid, validString);
            }
            
            // check that parent catalog item is an assembly
            if (parentCatalogItem.isItemElementDisplayListEmpty()) {
                isValid = false;
                validString = appendToString(validString, "Assigned catalog item for parent must be assembly");
                return new CreateInfo(item, isValid, validString);
            }

            // check that name not already in use for parent
            if (nameInUse(itemParent, assemblyPartName)) {
                isValid = false;
                validString = appendToString(validString, "Assembly part name: '" + assemblyPartName + "' already in use for another spreadsheet row");
            } else {
                addNameInUse(itemParent, assemblyPartName);
            }
            
            // get element for specified part name
            ItemElement assemblyPartElement = null;
            for (ItemElement element : parentCatalogItem.getItemElementDisplayList()) {
                if (element.getName().equals(assemblyPartName)) {
                    assemblyPartElement = element;
                    break;
                }
            }
            if (assemblyPartElement == null) {
                isValid = false;
                validString = appendToString(validString, "Unable to find element with specified part name: '" + assemblyPartName + "' in parent assembly");
                return new CreateInfo(item, isValid, validString);
            }
            
            ItemDomainMachineDesignControllerUtility utility = new ItemDomainMachineDesignControllerUtility();
            UserInfo user = (UserInfo) rowMap.get(KEY_USER);
            boolean exception = false;
            String exceptionInfo = "";
            try {
                // create new promoted machine item using utility function
                item = utility.createRepresentingMachineForAssemblyElement(itemParent, assemblyPartElement, user);
                
                // added this line to ItemControllerUtility.generateUniqueElementNameForItem() to force it to
                // rebuild the cached itemElementDisplayList when multiple children are added in sequence to the same parent.
                // itemParent.resetItemElementDisplayList();
                
                // reapply import values to newly created promoted item
                item.setImportSortOrder(itemSortOrder);
                item.setName(itemName);
                item.setImportPath(itemPath);
                item.setItemIdentifier2(viewUUID);
                item.setImportIsTemplate(itemIsTemplate);
                
            } catch (InvalidArgument ex) {
                exception = true;
                exceptionInfo = ex.getMessage();
            } catch (CdbException ex) {
                exception = true;
                exceptionInfo = ex.getMessage();
            }
            if (exception) {
                isValid = false;
                validString = 
                        appendToString(validString, "Error creating machine item for assembly element: " 
                                + assemblyPartName + " details: " + exceptionInfo);
            }
        }
            
        return new CreateInfo(item, isValid, validString);
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