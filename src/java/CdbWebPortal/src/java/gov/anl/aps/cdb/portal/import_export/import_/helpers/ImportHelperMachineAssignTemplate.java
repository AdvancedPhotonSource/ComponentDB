/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.import_export.import_.helpers;

import com.oracle.wls.shaded.org.apache.bcel.classfile.Utility;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ColumnModeOptions;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.HelperWizardOption;
import gov.anl.aps.cdb.portal.import_export.import_.objects.MachineImportHelperCommon;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.specs.ColumnSpec;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author craig
 */
public class ImportHelperMachineAssignTemplate extends ImportHelperBase<ItemDomainMachineDesign, ItemDomainMachineDesignController> {

    private static final String LABEL_MACHINE_ITEM = "Machine Item";
    
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
    protected List initColumnSpecs() {
        
        List<ColumnSpec> specs = new ArrayList<>();

        specs.add(MachineImportHelperCommon.existingMachineItemColumnSpec(
                ColumnModeOptions.rUPDATErCOMPARE(), 
                getMachineImportHelperCommon().getRootItem(), 
                LABEL_MACHINE_ITEM, 
                "getName"));
        
        specs.add(MachineImportHelperCommon.templateInvocationColumnSpec(
                ColumnModeOptions.rUPDATErCOMPARE()));

        return specs;
    }

    @Override
    public ItemDomainMachineDesignController getEntityController() {
        return ItemDomainMachineDesignController.getInstance();
    }

    @Override
    public String getFilenameBase() {
        return "Machine Element Assign Template";
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
    protected CreateInfo retrieveEntityInstance(Map<String, Object> rowMap) {
        return getMachineImportHelperCommon().retrieveEntityInstance(rowMap);
    }

    @Override
    protected ValidInfo updateEntityInstance(
            ItemDomainMachineDesign item, Map<String, Object> rowMap) {
        
        boolean isValid = true;
        String validString = "";
        
        // check to see if item is associated with a template already
        if (item.getCreatedFromTemplate() != null) {
            isValid = false;
            validString = "Item is already associated with a template.";
            
        } else {
            CreateInfo loadTemplateInfo = MachineImportHelperCommon.loadTemplateAndSetParamValues(rowMap);
            ItemDomainMachineDesign templateItem = (ItemDomainMachineDesign) loadTemplateInfo.getEntity();
            if (templateItem == null) {
                isValid = false;
                validString = loadTemplateInfo.getValidInfo().getValidString();
            } else {
                // TODO Craig - Verify if controller utility used properly. 
                ItemDomainMachineDesignControllerUtility utility =  new ItemDomainMachineDesignControllerUtility(); 
                // TODO Craig - Is this in the spreadsheet? 
                UserInfo user = SessionUtility.getUser();
                List<KeyValueObject> nameKV = utility.generateMachineDesignTemplateNameVars(templateItem);
                ValidInfo assignValidInfo
                        = utility.assignTemplateToItem(item, templateItem, user, nameKV);
                // TODO Craig End
                if (!assignValidInfo.isValid()) {
                    isValid = false;
                    validString = assignValidInfo.getValidString();
                }
            }
        }
        
        return new ValidInfo(isValid, validString);
    }

}
