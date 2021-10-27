/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignRelationshipBaseControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignBaseTreeNode;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignRelationshipTreeNode;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.NodeSelectEvent;

public abstract class ItemDomainMachineDesignRelationshipBaseController<MachineRelationshipControllerUtility extends ItemDomainMachineDesignRelationshipBaseControllerUtility> extends ItemDomainMachineDesignBaseController<ItemDomainMachineDesignRelationshipTreeNode, MachineRelationshipControllerUtility> {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainMachineDesignRelationshipBaseController.class.getName());

    private ItemDomainMachineDesign machineRelatedByCurrent;
    private ItemDomainMachineDesignTreeNode selectedMachineRelatedByCurrentNode = null;
    private boolean displaySelectMachineRelatedByCurrent;

    private ItemDomainMachineDesignTreeNode standardMachineDesignRootTreeNode;

    private ItemDomainMachineDesignRelationshipTreeNode removeMachineDesignRootTreeNode;

    protected abstract EntityTypeName getRelationshipMachineEntityType();

    public List<ItemDomainMachineDesign> getMachineElementsRelatedToCurrent() {
        ItemDomainMachineDesign current = getCurrent();
        List<ItemDomainMachineDesign> machineElementsRelatedToCurrent = null;
        if (current != null) {
            machineElementsRelatedToCurrent = current.getMachineElementsRelatedToThis();

            if (machineElementsRelatedToCurrent == null) {
                ItemElementRelationshipTypeNames relationshipTypeName = getRelationshipTypeName();
                int relationshipId = relationshipTypeName.getDbId();
                machineElementsRelatedToCurrent = itemDomainMachineDesignFacade.fetchRelationshipChildrenItems(current.getId(), relationshipId);

                current.setMachineElementsRelatedToThis(machineElementsRelatedToCurrent);
            }
        }

        return machineElementsRelatedToCurrent;
    }

    @Override
    public boolean isDisplayFollowInstructionOnRightOnBlockUI() {
        return super.isDisplayFollowInstructionOnRightOnBlockUI()
                || displaySelectMachineRelatedByCurrent;
    }

    @Override
    public void resetListConfigurationVariables() {
        super.resetListConfigurationVariables();
        displaySelectMachineRelatedByCurrent = false;
    }

    @Override
    public void resetListDataModel() {
        super.resetListDataModel();
        standardMachineDesignRootTreeNode = null;
    }

    public void prepareRemoveMachinedRelatedRelationship() {
        updateCurrentUsingSelectedItemInTreeTable();
        ItemDomainMachineDesign current = getCurrent();

        removeMachineDesignRootTreeNode = new ItemDomainMachineDesignRelationshipTreeNode(current, getDefaultDomain(), getEntityDbFacade(), getRelationshipTypeName(), getRelationshipMachineEntityType(), false, true);
    }

    public void removeMachinedRelatedRelationship() {
        List<ItemElementRelationship> relationshipsToDestroy = new ArrayList<>();
        ItemDomainMachineDesign current = getCurrent();

        removeRelationshipHiearchically(current, relationshipsToDestroy, true);

        ItemElementRelationshipController instance = ItemElementRelationshipController.getInstance();

        try {
            for (ItemElementRelationship ier : relationshipsToDestroy) {
                instance.destroy(ier);
            }
        } catch (RuntimeException ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());
        }

        resetListConfigurationVariables();
        resetListDataModel();
    }

    private void removeRelationshipHiearchically(ItemDomainMachineDesign machine, List<ItemElementRelationship> relationshipsToDestroy, boolean first) {
        ItemElementRelationshipTypeNames relationshipTypeName = getRelationshipTypeName();
        String relationshipName = relationshipTypeName.getValue();
        if (first) {
            // When it goes from control element to machine element
            List<ItemElementRelationship> itemElementRelationshipList = machine.getItemElementRelationshipList();

            for (int i = 0; i < itemElementRelationshipList.size(); i++) {
                ItemElementRelationship ier = itemElementRelationshipList.get(i);
                if (ier.getRelationshipType().getName().equals(relationshipName)) {
                    ItemDomainMachineDesignBaseTreeNode selectedItemInListTreeTable = getSelectedItemInListTreeTable();
                    ItemElement element = selectedItemInListTreeTable.getParent().getElement();
                    Item containedItem = element.getContainedItem();
                    ItemElement parentSelfElement = containedItem.getSelfElement();
                    ItemElement secondItemElement = ier.getSecondItemElement();

                    if (parentSelfElement.equals(secondItemElement)) {
                        itemElementRelationshipList.remove(i);
                        relationshipsToDestroy.add(ier);
                        break;
                    }
                }
            }
        }

        // When it goes from machine element to machine element (children) 
        List<ItemElementRelationship> itemElementRelationshipList = machine.getItemElementRelationshipList1();
        for (int i = 0; i < itemElementRelationshipList.size(); i++) {
            ItemElementRelationship ier = itemElementRelationshipList.get(i);
            if (ier.getRelationshipType().getName().equals(relationshipName)) {
                ItemElement firstItemElement = ier.getFirstItemElement();
                Item parentItem = firstItemElement.getParentItem();
                ItemDomainMachineDesign parentMachine = (ItemDomainMachineDesign) parentItem;
                relationshipsToDestroy.add(ier);
                itemElementRelationshipList.remove(i);
                removeRelationshipHiearchically(parentMachine, relationshipsToDestroy, false);
            }
        }
    }

    public void prepareSelectMachinedRelatedByNode() {
        prepareAddNewMachineDesignListConfiguration();
        displaySelectMachineRelatedByCurrent = true;
    }

    public void saveSelectedMachineRelatedByCurrent() {
        if (machineRelatedByCurrent == null) {
            SessionUtility.addWarningMessage("No machine element selected", "Please select machine and try again.");
            return;
        }

        updateCurrentUsingSelectedItemInTreeTable();

        try {
            performApplyRelationship();
        } catch (InvalidArgument ex) {
            LOGGER.error(ex);
            SessionUtility.addErrorMessage("Error", ex.getMessage());

            return;
        }

        update();

        resetListConfigurationVariables();
        resetListDataModel();
        expandToSelectedTreeNodeAndSelect();
    }

    protected void performApplyRelationship() throws InvalidArgument {
        ItemDomainMachineDesignRelationshipBaseControllerUtility controllerUtility = getControllerUtility();
        controllerUtility.applyRelationship(machineRelatedByCurrent, getCurrent());
    }

    @Override
    protected final ItemDomainMachineDesign performItemRedirection(ItemDomainMachineDesign item, String paramString, boolean forceRedirection) {
        boolean matchEntityType = isMachineDesignAndEntityType(item, getRelationshipMachineEntityType());
        if (matchEntityType) {
            setCurrent(item);
            prepareView(item);
            resetListDataModel();
            return item;
        }

        // Do default action. 
        return super.performItemRedirection(item, paramString, forceRedirection); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected final void prepareEntityView(ItemDomainMachineDesign entity) {
        processPreRenderList();
        boolean matchEntityType = isMachineDesignAndEntityType(entity, getRelationshipMachineEntityType());
        if (matchEntityType) {
            loadViewModeUrlParameter();
        }
    }

    @Override
    public void processPreRenderList() {
        super.processPreRenderList();

        String paramValue = SessionUtility.getRequestParameterValue("id");

        if (paramValue != null) {
            Integer idParam = Integer.parseInt(paramValue);
            ItemDomainMachineDesign result = itemDomainMachineDesignFacade.find(idParam);

            if (result != null) {
                ItemDomainMachineDesignRelationshipTreeNode machineDesignTreeRootTreeNode = getMachineDesignTreeRootTreeNode();
                expandToSpecificMachineDesignItemByRelationship(getRelationshipTypeName(), machineDesignTreeRootTreeNode, result);
            }
        }
    }

    public String showInHousingHierarchyForSelectedTreeNode() {
        updateCurrentUsingSelectedItemInTreeTable();

        ItemDomainMachineDesign item = getCurrent();

        if (item != null) {
            String redirect = "/views/itemDomainMachineDesign/list";
            redirect += "?id=" + item.getId() + "&faces-redirect=true";
            return redirect;
        }

        SessionUtility.addErrorMessage("Error", "Cannot load details for a non machine design.");
        return null;
    }

    @Override
    public ItemDomainMachineDesignRelationshipTreeNode loadMachineDesignRootTreeNode(List<ItemDomainMachineDesign> itemsWithoutParents) {
        ItemDomainMachineDesignRelationshipTreeNode rootTreeNode = new ItemDomainMachineDesignRelationshipTreeNode(
                itemsWithoutParents,
                getDefaultDomain(),
                getEntityDbFacade(),
                getRelationshipTypeName(),
                getRelationshipMachineEntityType()
        );

        return rootTreeNode;
    }

    @Override
    public ItemDomainMachineDesignRelationshipTreeNode createMachineTreeNodeInstance() {
        return new ItemDomainMachineDesignRelationshipTreeNode();
    }

    public void machineRelatedByCurrentItemSelected(NodeSelectEvent nodeSelection) {
        machineRelatedByCurrent = getMachineFromNodeSelectEvent(nodeSelection);
    }

    public ItemDomainMachineDesignTreeNode getStandardMachineDesignRootTreeNode() {
        if (standardMachineDesignRootTreeNode == null) {
            List<ItemDomainMachineDesign> defaultTopLevelMachineList = super.getDefaultTopLevelMachineList();
            standardMachineDesignRootTreeNode = new ItemDomainMachineDesignTreeNode(defaultTopLevelMachineList, getDefaultDomain(), getEntityDbFacade());
        }
        return standardMachineDesignRootTreeNode;
    }

    public void setStandardMachineDesignRootTreeNode(ItemDomainMachineDesignTreeNode standardMachineDesignRootTreeNode) {
        this.standardMachineDesignRootTreeNode = standardMachineDesignRootTreeNode;
    }

    public ItemDomainMachineDesignTreeNode getSelectedMachineRelatedByCurrentNode() {
        return selectedMachineRelatedByCurrentNode;
    }

    public void setSelectedMachineRelatedByCurrentNode(ItemDomainMachineDesignTreeNode SelectedMachineRelatedByCurrentNode) {
        this.selectedMachineRelatedByCurrentNode = SelectedMachineRelatedByCurrentNode;
    }

    public ItemDomainMachineDesignRelationshipTreeNode getRemoveMachineDesignRootTreeNode() {
        return removeMachineDesignRootTreeNode;
    }

    public void setRemoveMachineDesignRootTreeNode(ItemDomainMachineDesignRelationshipTreeNode removeMachineDesignRootTreeNode) {
        this.removeMachineDesignRootTreeNode = removeMachineDesignRootTreeNode;
    }

    public boolean isDisplaySelectMachineRelatedByCurrent() {
        return displaySelectMachineRelatedByCurrent;
    }

    public ItemDomainMachineDesign getMachineRelatedByCurrent() {
        return machineRelatedByCurrent;
    }

    public void setMachineRelatedByCurrent(ItemDomainMachineDesign machineRelatedByCurrent) {
        this.machineRelatedByCurrent = machineRelatedByCurrent;
    }

    protected final ItemElementRelationshipTypeNames getRelationshipTypeName() {
        ItemDomainMachineDesignRelationshipBaseControllerUtility controllerUtility = getControllerUtility();
        return controllerUtility.getRelationshipTypeName();
    }

    @Override
    public boolean isCurrentViewIsStandard() {
        // Disable favorites + drag & drop
        return false;
    }

    @Override
    public boolean getMachineHasHousingColumn() {
        return true;
    }

    @Override
    public boolean getEntityDisplayTemplates() {
        return false;
    }

    @Override
    public boolean getEntityDisplayDeletedItems() {
        return false;
    }

    @Override
    public boolean getEntityDisplayImportButton() {
        return false; 
    }

}
