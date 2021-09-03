/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;

/**
 *
 * @author djarosz
 */
@Named(ItemDomainMachineDesignController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignController extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignTreeNode> implements ItemDomainCableDesignWizardClient {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignController.class.getName());

    public final static String controllerNamed = "itemDomainMachineDesignController";

    private ItemDomainMachineDesign controlElementRunningOnCurrent;
    private ItemDomainMachineDesignTreeNode selectedControlElementRunningOnCurrent = null;
    private boolean displaySelectControlElementRunningOnCurrent;

    private ItemDomainMachineDesignTreeNode machineDesignControlHiearchy;

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

    public void prepareSelectMachinedRunningOnCurrent() {
        prepareAddNewMachineDesignListConfiguration();
        displaySelectControlElementRunningOnCurrent = true;
    }

    public void saveSelectedMachineRunningOnCurrent() {
        if (controlElementRunningOnCurrent == null) {
            SessionUtility.addWarningMessage("No machine element selected", "Please select machine and try again.");
            return;
        }

        updateCurrentUsingSelectedItemInTreeTable();

        performApplyRelationship();

        update();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    private void performApplyRelationship() {
        // TODO add validations.
        String runningRelationshipTypeName = ItemElementRelationshipTypeNames.running.getValue();
        RelationshipType runningRelationship = relationshipTypeFacade.findByName(runningRelationshipTypeName);

        ItemDomainMachineDesign current = getCurrent();
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(runningRelationship);
        itemElementRelationship.setFirstItemElement(current.getSelfElement());
        itemElementRelationship.setSecondItemElement(controlElementRunningOnCurrent.getSelfElement());

        current.getItemElementRelationshipList().add(itemElementRelationship);
        controlElementRunningOnCurrent.getItemElementRelationshipList1().add(itemElementRelationship);
    }

    public void machineRunningOnCurrentItemSelected(NodeSelectEvent nodeSelection) {
        controlElementRunningOnCurrent = getMachineFromNodeSelectEvent(nodeSelection);
    }

    public ItemDomainMachineDesignTreeNode getMachineDesignControlHiearchy() {
        if (machineDesignControlHiearchy == null) {
            List<ItemDomainMachineDesign> topLevelMachineDesignControl = itemDomainMachineDesignFacade.getTopLevelMachineDesignControl();
            machineDesignControlHiearchy = new ItemDomainMachineDesignTreeNode(topLevelMachineDesignControl, getDefaultDomain(), getEntityDbFacade());
        }
        return machineDesignControlHiearchy;
    }

    public void setMachineDesignControlHiearchy(ItemDomainMachineDesignTreeNode machineDesignControlHiearchy) {
        this.machineDesignControlHiearchy = machineDesignControlHiearchy;
    }

    public ItemDomainMachineDesignTreeNode getSelectedControlElementRunningOnCurrent() {
        return selectedControlElementRunningOnCurrent;
    }

    public void setSelectedControlElementRunningOnCurrent(ItemDomainMachineDesignTreeNode selectedControlElementRunningOnCurrent) {
        this.selectedControlElementRunningOnCurrent = selectedControlElementRunningOnCurrent;
    }

    public boolean isDisplaySelectControlElementRunningOnCurrent() {
        return displaySelectControlElementRunningOnCurrent;
    }

    public ItemDomainMachineDesign getControlElementRunningOnCurrent() {
        return controlElementRunningOnCurrent;
    }

    public void setControlElementRunningOnCurrent(ItemDomainMachineDesign controlElementRunningOnCurrent) {
        this.controlElementRunningOnCurrent = controlElementRunningOnCurrent;
    }

    @Override
    public boolean isDisplayFollowInstructionOnRightOnBlockUI() {
        return super.isDisplayFollowInstructionOnRightOnBlockUI()
                || displaySelectControlElementRunningOnCurrent;
    }

    @Override
    public void resetListConfigurationVariables() {
        super.resetListConfigurationVariables();
        displaySelectControlElementRunningOnCurrent = false;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        machineDesignControlHiearchy = null;
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
