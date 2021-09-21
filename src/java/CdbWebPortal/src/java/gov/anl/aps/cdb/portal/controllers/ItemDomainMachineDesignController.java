/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignTreeNode, ItemDomainMachineDesignControllerUtility> implements ItemDomainCableDesignWizardClient {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignController.class.getName());

    public final static String controllerNamed = "itemDomainMachineDesignController";      

    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }

    public static ItemDomainMachineDesignController getInstance() {
        return (ItemDomainMachineDesignController) SessionUtility.findBean(controllerNamed);
    }

    public boolean isCablesShown() {
        ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = getCurrentMachineDesignListRootTreeNode().getConfig();
        return config.isShowCables();
    }

    public void setCablesShown(boolean cablesShown) {
        ItemDomainMachineDesignTreeNode.MachineTreeConfiguration config = getCurrentMachineDesignListRootTreeNode().getConfig();
        config.setShowCables(cablesShown);
    }  
    
    @Override
    public void processPreRenderList() {        
        super.processPreRenderList();
        
        String paramValue = SessionUtility.getRequestParameterValue("id");
        
        if (paramValue != null) {
            Integer idParam = Integer.parseInt(paramValue);
            ItemDomainMachineDesign result = itemDomainMachineDesignFacade.find(idParam);
            
            if (result != null) {
                ItemDomainMachineDesignTreeNode machineDesignTreeRootTreeNode = getMachineDesignTreeRootTreeNode();
                expandToSpecificMachineDesignItem(machineDesignTreeRootTreeNode, result);
            }
        }
    }
    
    public String showInControlHierarchyForSelectedTreeNode() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign item = getCurrent();
        
        // Verify if has control relationship..
        int relationshipId = ItemElementRelationshipTypeNames.control.getDbId();
        List<ItemDomainMachineDesign> machines = itemDomainMachineDesignFacade.fetchRelationshipParentItems(item.getId(), relationshipId);        
        
        if (machines.size() == 0) {
            SessionUtility.addErrorMessage("No control relationship", 
                    "This item does not show up in control hierarchy.");
            return null;
        }

        if (item != null) {
            String redirect = "/views/itemDomainMachineDesignControl/list";
            redirect += "?id=" + item.getId() + "&faces-redirect=true";
            return redirect;
        }

        SessionUtility.addErrorMessage("Error", "Cannot load details for a non machine design.");
        return null;
    }

    @Override
    protected ItemDomainMachineDesignControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignControllerUtility();
    }

    @Override
    public ItemDomainMachineDesignTreeNode loadMachineDesignRootTreeNode(List<ItemDomainMachineDesign> itemsWithoutParents) {
        ItemDomainMachineDesignTreeNode rootTreeNode = new ItemDomainMachineDesignTreeNode(itemsWithoutParents, getDefaultDomain(), getEntityDbFacade());

        return rootTreeNode;
    }

    @Override
    public ItemDomainMachineDesignTreeNode createMachineTreeNodeInstance() {
        return new ItemDomainMachineDesignTreeNode();
    }
    
}
