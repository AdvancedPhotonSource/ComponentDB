/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.RootMachineItemWizardOptionHelper;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.AssignedItemHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.handlers.LocationHandler;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.BooleanColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.CustomColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.FloatColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.MachineItemRefColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.NameHierarchyColumnSpec;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.StringColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainLocation;
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
    
    private static final String KEY_NAME = "name";
    private static final String KEY_INDENT = "indentLevel";
    private static final String KEY_ASSIGNED_ITEM = "assignedItem";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_PARENT = "importMdItem";
    private static final String KEY_IS_TEMPLATE = "importIsTemplateString";
    private static final String KEY_SORT_ORDER = "importSortOrder";
    
    private static final String HEADER_PARENT = "Parent ID";
    private static final String HEADER_BASE_LEVEL = "Level";
    private static final String HEADER_ALT_NAME = "Machine Design Alternate Name";
    private static final String HEADER_DESCRIPTION = "Machine Design Item Description";
    private static final String HEADER_SORT_ORDER = "Sort Order";
    private static final String HEADER_ASSIGNED_ITEM = "Assigned Catalog/Inventory Item Description";
    private static final String HEADER_ASSIGNED_ITEM_ID = "Assigned Catalog/Inventory Item";
    private static final String HEADER_LOCATION = "Location";
    private static final String HEADER_TEMPLATE = "Is Template?";

    private Map<Integer, ItemDomainMachineDesign> parentIndentMap = new HashMap<>();
    
    private int nonTemplateItemCount = 0;
    private int templateItemCount = 0;
    
    private RootMachineItemWizardOptionHelper rootMachineItemWizardOptionHelper = null;
    
    private RootMachineItemWizardOptionHelper getRootMachineItemWizardOptionHelper() {
        if (rootMachineItemWizardOptionHelper == null) {
            rootMachineItemWizardOptionHelper = new RootMachineItemWizardOptionHelper();
        }
        return rootMachineItemWizardOptionHelper;
    }
    
    public String getOptionRootItemName() {
        return getRootMachineItemWizardOptionHelper().getOptionRootItemName();
    }

    public void setOptionRootItemName(String optionRootItemName) {
        getRootMachineItemWizardOptionHelper().setOptionRootItemName(optionRootItemName);
    }
    
    @Override
    protected List<HelperWizardOption> initializeWizardOptions() {        
        List<HelperWizardOption> options = new ArrayList<>();        
        options.add(RootMachineItemWizardOptionHelper.rootMachineItemWizardOption());
        return options;
    }

    @Override
    public ValidInfo validateWizardOptions() {
        return getRootMachineItemWizardOptionHelper().validate();
    }
    
    @Override
    protected List<ColumnSpec> getColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(new MachineItemRefColumnSpec(
                HEADER_PARENT, 
                KEY_PARENT, 
                "setImportMdItem", 
                "CDB ID, name, or path of parent machine design item.  Can only be provided for level 0 item. Name must be unique and prefixed with '#'. Path must be prefixed with '#', start with a '/', and use '/' as a delimiter. If name includes an embedded '/' character, escape it by preceding with a '\' character.", 
                null,
                ColumnModeOptions.oCREATE(),
                getRootMachineItemWizardOptionHelper().getRootItem()));
        
        specs.add(new NameHierarchyColumnSpec(
                "Name hierarchy column", 
                ColumnModeOptions.oCREATE(),
                HEADER_BASE_LEVEL, 
                KEY_NAME, 
                KEY_INDENT, 
                3));
        
        specs.add(new StringColumnSpec(
                HEADER_ALT_NAME, 
                "alternateName", 
                "setAlternateName", 
                "Alternate machine design item name.", 
                null,
                ColumnModeOptions.oCREATE(), 
                128));
        
        specs.add(new StringColumnSpec(
                HEADER_DESCRIPTION, 
                "description", 
                "setDescription", 
                "Textual description of machine design item.", 
                null,
                ColumnModeOptions.oCREATE(), 
                256));
        
        specs.add(new FloatColumnSpec(
                HEADER_SORT_ORDER, 
                KEY_SORT_ORDER, 
                "setImportSortOrder", 
                "Sort order within parent item (as decimal), defaults to order in input sheet.", 
                null,
                ColumnModeOptions.oCREATE()));
        
        specs.add(new StringColumnSpec(
                HEADER_ASSIGNED_ITEM, 
                "importAssignedItemDescription", 
                "setImportAssignedItemDescription", 
                "Textual description of machine design item.", 
                null,
                ColumnModeOptions.oCREATE(), 
                256));

        AssignedItemHandler assignedItemHandler = new AssignedItemHandler();
        
        specs.add(new CustomColumnSpec(
                HEADER_ASSIGNED_ITEM_ID, 
                "importAssignedItemString", 
                "CDB ID or name of assigned catalog or inventory item. Name must be unique and prefixed with '#'.", 
                null,
                false,
                ColumnModeOptions.oCREATE(), 
                assignedItemHandler));
        
        LocationHandler locationHandler = new LocationHandler();
        
        specs.add(new CustomColumnSpec(
                HEADER_LOCATION, 
                "importLocationItemString", 
                "CDB ID or name of CDB location item (use of word 'parent' allowed for documentation purposes, it is ignored). Name must be unique and prefixed with '#'.", 
                null,
                false,
                ColumnModeOptions.oCREATE(), 
                locationHandler));

        specs.add(locationDetailsColumnSpec());
        
        specs.add(new BooleanColumnSpec(
                HEADER_TEMPLATE, 
                KEY_IS_TEMPLATE, 
                "setImportIsTemplate", 
                "True/yes if item is template, false/no otherwise.", 
                null,
                ColumnModeOptions.rCREATE()));
        
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
    
    @Override
    protected void reset_() {
        super.reset();
        nonTemplateItemCount = 0;
        templateItemCount = 0;
        getRootMachineItemWizardOptionHelper().reset();
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
        return KEY_NAME;
    }
            
    @Override
    protected String getKeyIndent_() {
        return KEY_INDENT;
    }
            
    @Override
    protected String getKeyParent_() {
        return KEY_PARENT;
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
        Float itemSortOrder = (Float) rowMap.get(KEY_SORT_ORDER);
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
        Boolean itemIsTemplate = (Boolean) rowMap.get(KEY_IS_TEMPLATE);
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