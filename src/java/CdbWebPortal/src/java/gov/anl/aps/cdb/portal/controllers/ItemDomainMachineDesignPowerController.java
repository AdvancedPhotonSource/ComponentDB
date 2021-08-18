/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignPowerControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;

@Named(ItemDomainMachineDesignPowerController.controllerNamed)
@SessionScoped
public class ItemDomainMachineDesignPowerController extends ItemDomainMachineDesignRelationshipBaseController {

    public final static String controllerNamed = "itemDomainMachineDesignPowerController";
    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignPowerController.class.getName());

    private boolean displaySelectMachinePoweredByCurrent;
    private ItemDomainMachineDesign machinePoweredByCurrent;

    @Override
    protected String getViewPath() {
        return "/views/itemDomainMachineDesignPower/view.xhtml";
    }

    @Override
    public String getItemListPageTitle() {
        return "Power Machine Elements";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Power Machine Element";
    }

    @Override
    public DataModel getTopLevelMachineDesignSelectionList() {
        ItemDomainMachineDesign current = getCurrent();
        DataModel topLevelMachineDesignSelectionList = current.getTopLevelMachineDesignSelectionList();

        if (topLevelMachineDesignSelectionList == null) {
            List<ItemDomainMachineDesign> topLevelMachineDesignInventory = itemDomainMachineDesignFacade.getTopLevelMachineDesignPower();

            removeTopLevelParentOfItemFromList(current, topLevelMachineDesignInventory);

            topLevelMachineDesignSelectionList = new ListDataModel(topLevelMachineDesignInventory);
            current.setTopLevelMachineDesignSelectionList(topLevelMachineDesignSelectionList);
        }

        return topLevelMachineDesignSelectionList;
    }

    public static ItemDomainMachineDesignPowerController getInstance() {
        return (ItemDomainMachineDesignPowerController) SessionUtility.findBean(controllerNamed);
    }

    @Override
    public List<ItemDomainMachineDesign> getDefaultTopLevelMachineList() {
        return itemDomainMachineDesignFacade.getTopLevelMachineDesignPower();
    }

    @Override
    protected ItemDomainMachineDesignPowerControllerUtility createControllerUtilityInstance() {
        return new ItemDomainMachineDesignPowerControllerUtility();
    }

    // todo resolve this 
    @Override
    protected ItemDomainMachineDesignSettings createNewSettingObject() {
        return new ItemDomainMachineDesignSettings(this);
    }

    @Override
    public boolean isDisplayFollowInstructionOnRightOnBlockUI() {
        return super.isDisplayFollowInstructionOnRightOnBlockUI()
                || displaySelectMachinePoweredByCurrent;
    }

    @Override
    public void resetListConfigurationVariables() {
        super.resetListConfigurationVariables();
        displaySelectMachinePoweredByCurrent = false;
    }

    public void prepareSelectMachinedPoweredByNode() {
        prepareAddNewMachineDesignListConfiguration();
        displaySelectMachinePoweredByCurrent = true;
    }

    public void saveSelectedMachinePoweredByCurrent() {
        if (machinePoweredByCurrent == null) {
            SessionUtility.addWarningMessage("No machine element selected", "Please select machine and try again.");
            return;
        }

        updateCurrentUsingSelectedItemInTreeTable();

        applyPoweredByRelationship(machinePoweredByCurrent, getCurrent());

        update();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    public void applyPoweredByRelationship(ItemDomainMachineDesign machineElement, ItemDomainMachineDesign controlElement) {
        RelationshipType templateRelationship
                = relationshipTypeFacade.findByName(ItemElementRelationshipTypeNames.power.getValue());

        // Todo check if a control relationship already exists.
        // Create item element relationship between the template and the clone 
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setRelationshipType(templateRelationship);
        itemElementRelationship.setFirstItemElement(machineElement.getSelfElement());
        itemElementRelationship.setSecondItemElement(controlElement.getSelfElement());

        machineElement.getItemElementRelationshipList().add(itemElementRelationship);
        controlElement.getItemElementRelationshipList().add(itemElementRelationship);
    }

    public boolean isDisplaySelectMachinePoweredByCurrent() {
        return displaySelectMachinePoweredByCurrent;
    }

    public ItemDomainMachineDesign getMachinePoweredByCurrent() {
        return machinePoweredByCurrent;
    }

    public void machinePoweredByCurrentItemSelected(NodeSelectEvent nodeSelection) {
        machinePoweredByCurrent = getMachineFromNodeSelectEvent(nodeSelection);
    }

    @Override
    protected ItemDomainMachineDesign performItemRedirection(ItemDomainMachineDesign item, String paramString, boolean forceRedirection) {
        if (isItemMachineDesignAndPower(item)) {
            setCurrent(item);
            prepareView(item);
            resetListDataModel();
            return item;
        }

        // Do default action. 
        return super.performItemRedirection(item, paramString, forceRedirection); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void prepareEntityView(ItemDomainMachineDesign entity) {
        processPreRenderList();
        if (isItemMachineDesignAndPower(entity)) {
            loadViewModeUrlParameter();
        }
    }

    @Override
    protected String getRelationshipTypeName() {
        return ItemElementRelationshipTypeNames.power.getValue();
    }

}
