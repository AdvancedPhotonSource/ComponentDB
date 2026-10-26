/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;

/**
 * Reusable state for selecting one item from the machine-design tree.
 */
public class MachineDesignTreeSelection {

    private ItemDomainMachineDesignTreeNode tree;
    private ItemDomainMachineDesignTreeNode selectedNode;

    public ItemDomainMachineDesignTreeNode getTree() {
        if (tree == null) {
            tree = ItemDomainMachineDesignController.getInstance().loadMachineDesignRootTreeNode();
        }
        return tree;
    }

    public ItemDomainMachineDesignTreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(ItemDomainMachineDesignTreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void selectMachine(ItemDomainMachineDesign machine) {
        reset();
        if (machine != null) {
            selectedNode = (ItemDomainMachineDesignTreeNode)
                    ItemDomainMachineDesignController.expandToSpecificMachineDesignItem(getTree(), machine);
            if (selectedNode != null) {
                selectedNode.setSelected(true);
            }
        }
    }

    public ItemDomainMachineDesign getSelectedMachine() {
        if (selectedNode == null || selectedNode.getData() == null) {
            return null;
        }
        ItemElement element = (ItemElement) selectedNode.getData();
        Item item = element.getContainedItem();
        return item instanceof ItemDomainMachineDesign ? (ItemDomainMachineDesign) item : null;
    }

    public void reset() {
        tree = null;
        selectedNode = null;
    }
}
