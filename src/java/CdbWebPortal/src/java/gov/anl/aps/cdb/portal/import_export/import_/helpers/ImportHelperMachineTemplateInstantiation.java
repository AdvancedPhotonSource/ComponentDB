/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserGroup;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author craig
 */
public class ImportHelperMachineTemplateInstantiation 
        extends ImportHelperTreeViewBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {
    
    private static final Logger LOGGER = LogManager.getLogger(ImportHelperMachineTemplateInstantiation.class.getName());
    
    private Map<String, ItemDomainMachineDesign> itemByNameMap = new HashMap<>();

    private int templateInstantiationCount = 0;
    
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
    protected List<ColumnSpec> initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();
        
        specs.add(MachineImportHelperCommon.existingMachineItemColumnSpec(ColumnModeOptions.oCREATE(), getMachineImportHelperCommon().getRootItem(), null, null));
        specs.add(MachineImportHelperCommon.templateInvocationColumnSpec(ColumnModeOptions.rCREATE()));
        specs.add(MachineImportHelperCommon.altNameColumnSpec(ColumnModeOptions.oCREATE()));
        specs.add(MachineImportHelperCommon.descriptionColumnSpec(ColumnModeOptions.oCREATE()));
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
        return "Machine Template Instantiation";
    }
    
    @Override
    protected void reset_() {
        itemByNameMap = new HashMap<>();
        templateInstantiationCount = 0;
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
    protected CreateInfo createEntityInstance(Map<String, Object> rowMap) {

        String methodLogName = "createEntityInstance() ";
        boolean isValid = true;
        String validString = "";
        ItemDomainMachineDesign invalidInstance = getEntityController().createEntityInstance();
        
        // check for and set parent item
        ItemDomainMachineDesign itemParent = (ItemDomainMachineDesign) rowMap.get(MachineImportHelperCommon.KEY_MD_ITEM);
        if (itemParent == null) {
            // must specify parent for template invocation
            isValid = false;
            validString = "parent ID must be specified for template invocation";
            LOGGER.info(methodLogName + validString);
            return new CreateInfo(invalidInstance, isValid, validString);
        }
        invalidInstance.setImportMdItem(itemParent);

        UserInfo user = (UserInfo) rowMap.get(KEY_USER);
        UserGroup group = (UserGroup) rowMap.get(KEY_GROUP);
        
        CreateInfo loadTemplateInfo = MachineImportHelperCommon.loadTemplateAndSetParamValues(rowMap);
        ItemDomainMachineDesign templateItem = (ItemDomainMachineDesign) loadTemplateInfo.getEntity();
        if (templateItem == null) {
            return loadTemplateInfo;
        }

        ItemDomainMachineDesign item = 
                ItemDomainMachineDesign.importInstantiateTemplateUnderParent(
                        templateItem, itemParent, user, group);
        
        // update tree view with item and parent
        updateTreeView(item, itemParent, true);

        // add entry to name map for new item
        itemByNameMap.put(item.getName(), item);
        
        templateInstantiationCount = templateInstantiationCount + 1;

        return new CreateInfo(item, isValid, validString);
    }
    
    @Override
    protected String getCustomSummaryDetails() {        
        String summaryDetails = "";        
        if (templateInstantiationCount > 0) {
            summaryDetails = 
                    templateInstantiationCount + " template instantiations including " +
                    getTreeNodeChildCount() + "  items";                    
        }
        return summaryDetails;
    }
    
}
