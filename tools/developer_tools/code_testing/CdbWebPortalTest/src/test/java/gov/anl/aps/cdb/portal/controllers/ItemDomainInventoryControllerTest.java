/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainInventorySettings;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.view.objects.InventoryMachineAssignmentSelection;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemDomainInventoryControllerTest {

    private TestInventoryController controller;
    private ItemDomainCatalog catalog;
    private ItemDomainInventory inventory;

    @Before
    public void setUp() {
        catalog = catalog(1);
        inventory = inventory(catalog);
        controller = new TestInventoryController(inventory);
    }

    @Test
    public void normalizesInventoryAssignmentsToCatalog() {
        assertNull(controller.getCatalogItem(null));
        assertSame(catalog, controller.getCatalogItem(catalog));
        assertSame(catalog, controller.getCatalogItem(inventory));
    }

    @Test
    public void requestsConfirmationOnlyForDifferentCatalogAssignment() {
        ItemDomainMachineDesign destination = machine();

        assertFalse(controller.requiresCatalogReplacementConfirmation(destination));

        destination.setAssignedItem(inventory);
        assertFalse(controller.requiresCatalogReplacementConfirmation(destination));

        destination.setAssignedItem(inventory(catalog));
        assertFalse(controller.requiresCatalogReplacementConfirmation(destination));

        destination.setAssignedItem(inventory(catalog(2)));
        assertTrue(controller.requiresCatalogReplacementConfirmation(destination));
    }

    @Test
    public void exposesSelectedDestinationAndAssignment() {
        ItemDomainMachineDesign destination = machine();
        destination.setAssignedItem(inventory);
        controller.setSelectedMachineAssignmentNode(node(destination));

        assertSame(destination, controller.getMachineAssignmentSelection().getSelectedMachine());
        assertSame(inventory, controller.getSelectedMachineCurrentAssignment());
        assertSame(catalog, controller.getSelectedMachineCurrentCatalog());
        assertSame(catalog, controller.getCurrentInventoryCatalog());
    }

    @Test
    public void tracksInstalledAndReplacementProgressAndResetsState() {
        InventoryMachineAssignmentSelection selection = controller.getMachineAssignmentSelection();
        controller.setMachineAssignmentInstalled(false);
        controller.setMachineAssignmentReplacementConfirmation(true);
        selection.setDestinationPrepared(true);
        selection.setSourceReleased(true);
        selection.setInventoryAssigned(true);
        selection.setReplacementError("failed");

        assertFalse(controller.isMachineAssignmentInstalled());
        assertTrue(controller.isMachineAssignmentReplacementConfirmation());

        controller.cancelMachineAssignmentReplacement();
        assertFalse(controller.isMachineAssignmentReplacementConfirmation());

        controller.resetMachineAssignmentState();
        assertTrue(controller.isMachineAssignmentInstalled());
        assertNull(controller.getSelectedMachineAssignmentNode());
        assertFalse(selection.isDestinationPrepared());
        assertFalse(selection.isSourceReleased());
        assertFalse(selection.isInventoryAssigned());
        assertNull(selection.getReplacementError());
    }

    private static ItemDomainCatalog catalog(int id) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(id);
        return item;
    }

    private static ItemDomainInventory inventory(ItemDomainCatalog catalogItem) {
        ItemDomainInventory item = new ItemDomainInventory();
        item.setCatalogItem(catalogItem);
        item.setItemElementMemberList2(new ArrayList<>());
        return item;
    }

    private static ItemDomainMachineDesign machine() {
        ItemDomainMachineDesign machine = new ItemDomainMachineDesign();
        ItemElement selfElement = new ItemElement();
        selfElement.setParentItem(machine);
        machine.setFullItemElementList(Collections.singletonList(selfElement));
        return machine;
    }

    private static ItemDomainMachineDesignTreeNode node(ItemDomainMachineDesign machine) {
        ItemElement element = new ItemElement();
        element.setContainedItem(machine);
        ItemDomainMachineDesignTreeNode node = new ItemDomainMachineDesignTreeNode();
        node.setData(element);
        return node;
    }

    private static class TestInventoryController extends ItemDomainInventoryController {

        private final ItemDomainInventory current;

        TestInventoryController(ItemDomainInventory current) {
            this.current = current;
        }

        @Override
        protected ItemDomainInventorySettings createNewSettingObject() {
            return null;
        }

        @Override
        public ItemDomainInventory getCurrent() {
            return current;
        }
    }
}
