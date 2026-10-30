/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;

/**
 * Inventory-specific state layered on reusable machine-tree selection.
 */
public class InventoryMachineAssignmentSelection extends MachineDesignTreeSelection {

    private ItemDomainMachineDesignTreeNode assignmentTree;
    private boolean installed = true;
    private boolean matchingCatalogOnly;
    private String machineNameFilter = "";
    private boolean replacementConfirmation;
    private boolean destinationPrepared;
    private boolean sourceReleased;
    private boolean inventoryAssigned;
    private String replacementError;

    @Override
    public ItemDomainMachineDesignTreeNode getTree() {
        return assignmentTree == null ? super.getTree() : assignmentTree;
    }

    public void setAssignmentTree(ItemDomainMachineDesignTreeNode assignmentTree) {
        this.assignmentTree = assignmentTree;
        setSelectedNode(null);
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public boolean isMatchingCatalogOnly() {
        return matchingCatalogOnly;
    }

    public void setMatchingCatalogOnly(boolean matchingCatalogOnly) {
        this.matchingCatalogOnly = matchingCatalogOnly;
    }

    public String getMachineNameFilter() {
        return machineNameFilter;
    }

    public void setMachineNameFilter(String machineNameFilter) {
        this.machineNameFilter = machineNameFilter == null ? "" : machineNameFilter;
    }

    public boolean isReplacementConfirmation() {
        return replacementConfirmation;
    }

    public void setReplacementConfirmation(boolean replacementConfirmation) {
        this.replacementConfirmation = replacementConfirmation;
    }

    public boolean isDestinationPrepared() {
        return destinationPrepared;
    }

    public void setDestinationPrepared(boolean destinationPrepared) {
        this.destinationPrepared = destinationPrepared;
    }

    public boolean isSourceReleased() {
        return sourceReleased;
    }

    public void setSourceReleased(boolean sourceReleased) {
        this.sourceReleased = sourceReleased;
    }

    public boolean isInventoryAssigned() {
        return inventoryAssigned;
    }

    public void setInventoryAssigned(boolean inventoryAssigned) {
        this.inventoryAssigned = inventoryAssigned;
    }

    public String getReplacementError() {
        return replacementError;
    }

    public void setReplacementError(String replacementError) {
        this.replacementError = replacementError;
    }

    public void resetReplacementProgress() {
        destinationPrepared = false;
        sourceReleased = false;
        inventoryAssigned = false;
        replacementError = null;
    }

    @Override
    public void reset() {
        super.reset();
        assignmentTree = null;
        installed = true;
        matchingCatalogOnly = false;
        machineNameFilter = "";
        replacementConfirmation = false;
        resetReplacementProgress();
    }
}
