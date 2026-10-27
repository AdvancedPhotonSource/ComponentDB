/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainInventorySettings;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.InventoryMachineAssignmentSelection;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemDomainInventoryControllerTest {

    private int nextMachineId;
    private TestInventoryController controller;
    private ItemDomainCatalog catalog;
    private ItemDomainInventory inventory;

    @Before
    public void setUp() {
        nextMachineId = 100;
        catalog = catalog(1);
        inventory = inventory(catalog);
        UserInfo user = user(1);
        SessionUtility.reset();
        SessionUtility.setUser(user);
        setOwner(inventory, user);
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
    public void identifiesSourceAndWhetherItMustBeReleased() {
        assertFalse(controller.isCurrentAssignedToMachine());
        assertFalse(controller.isSourceMachineReleaseRequired());

        ItemDomainMachineDesign source = house(inventory, true);
        assertTrue(controller.isCurrentAssignedToMachine());

        controller.setSelectedMachineAssignmentNode(node(source));
        assertFalse(controller.isSourceMachineReleaseRequired());

        controller.setSelectedMachineAssignmentNode(node(machine()));
        assertTrue(controller.isSourceMachineReleaseRequired());
    }

    @Test
    public void prepareAssignmentRestoresPlannedSourceState() {
        ItemDomainMachineDesign source = house(inventory, false);

        controller.prepareMachineAssignment();

        assertSame(source, controller.getMachineAssignmentSelection().getSelectedMachine());
        assertFalse(controller.isMachineAssignmentInstalled());
    }

    @Test
    public void rejectsAssignmentWithoutInventoryPermission() {
        ItemDomainMachineDesign destination = machine();
        controller.setSelectedMachineAssignmentNode(node(destination));
        setOwner(inventory, user(2));

        controller.saveMachineAssignment();

        assertTrue(controller.updates.isEmpty());
        assertEquals("Write permission is required for both the inventory item and machine element.", SessionUtility.getErrorMessage());
    }

    @Test
    public void rejectsAssignmentWithoutDestinationPermission() {
        ItemDomainMachineDesign destination = machine();
        controller.setSelectedMachineAssignmentNode(node(destination));
        setOwner(destination, user(2));

        controller.saveMachineAssignment();

        assertTrue(controller.updates.isEmpty());
        assertEquals("Write permission is required for both the inventory item and machine element.", SessionUtility.getErrorMessage());
    }

    @Test
    public void rejectsMoveWithoutSourcePermission() {
        ItemDomainMachineDesign source = house(inventory, true);
        ItemDomainMachineDesign destination = machine();
        controller.setSelectedMachineAssignmentNode(node(destination));
        setOwner(source, user(2));

        controller.saveMachineAssignment();

        assertTrue(controller.updates.isEmpty());
        assertEquals("Write permission is required for both the inventory item and machine element.", SessionUtility.getErrorMessage());
    }

    @Test
    public void differentCatalogRequiresConfirmationBeforeUpdates() {
        ItemDomainMachineDesign destination = machine();
        destination.setAssignedItem(inventory(catalog(2)));
        controller.setSelectedMachineAssignmentNode(node(destination));

        controller.saveMachineAssignment();

        assertTrue(controller.isMachineAssignmentReplacementConfirmation());
        assertEquals("showMachineAssignmentReplacementConfirmDialog()", SessionUtility.getRemoteCommand());
        assertTrue(controller.updates.isEmpty());
    }

    @Test
    public void clearAssignmentRestoresCatalogOnSource() {
        ItemDomainMachineDesign source = house(inventory, true);

        controller.clearMachineAssignment();

        assertEquals(1, controller.updates.size());
        assertUpdate(controller.updates.get(0), source, catalog, null);
        assertTrue(controller.finished);
    }

    @Test
    public void clearWithNoHousingDoesNotUpdate() {
        controller.clearMachineAssignment();

        assertTrue(controller.updates.isEmpty());
        assertEquals("Inventory item is not assigned to a machine element.", SessionUtility.getInfoMessage());
    }

    @Test
    public void movingInventoryReleasesSourceThenAssignsDestination() {
        ItemDomainMachineDesign source = house(inventory, false);
        ItemDomainMachineDesign destination = machine();
        destination.setAssignedItem(catalog);
        controller.setSelectedMachineAssignmentNode(node(destination));
        controller.setMachineAssignmentInstalled(true);

        controller.saveMachineAssignment();

        assertEquals(2, controller.updates.size());
        assertUpdate(controller.updates.get(0), source, catalog, null);
        assertUpdate(controller.updates.get(1), destination, inventory, true);
        assertTrue(controller.finished);
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

    private static UserInfo user(int id) {
        UserInfo user = new UserInfo();
        user.setId(id);
        return user;
    }

    private static void setOwner(Item item, UserInfo owner) {
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setOwnerUser(owner);
        item.setEntityInfo(entityInfo);
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
        ItemElement selfElement = new ItemElement();
        selfElement.setParentItem(item);
        item.setFullItemElementList(Collections.singletonList(selfElement));
        return item;
    }

    private ItemDomainMachineDesign machine() {
        ItemDomainMachineDesign machine = new ItemDomainMachineDesign();
        machine.setId(nextMachineId++);
        ItemElement selfElement = new ItemElement();
        selfElement.setParentItem(machine);
        machine.setFullItemElementList(Collections.singletonList(selfElement));
        setOwner(machine, SessionUtility.getUser());
        return machine;
    }

    private ItemDomainMachineDesign house(ItemDomainInventory item, boolean installed) {
        ItemDomainMachineDesign machine = machine();
        machine.setAssignedItem(item);
        machine.setIsHoused(installed);
        ItemElement housing = new ItemElement();
        housing.setParentItem(machine);
        item.appendItemElementMemberList2(housing);
        return machine;
    }

    private static void assertUpdate(Update update, ItemDomainMachineDesign machine,
            Item assignment, Boolean installed) {
        assertSame(machine, update.machine);
        assertSame(assignment, update.assignment);
        assertEquals(installed, update.installed);
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
        private final ArrayList<Update> updates = new ArrayList<>();
        private boolean finished;

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

        @Override
        protected void selectMachineAssignment(ItemDomainMachineDesign machine) {
            setSelectedMachineAssignmentNode(ItemDomainInventoryControllerTest.node(machine));
        }

        @Override
        protected void updateMachineAssignment(ItemDomainMachineDesign machine,
                Item assignment, Boolean installed) throws CdbException {
            updates.add(new Update(machine, assignment, installed));
            machine.setAssignedItem(assignment);
            if (installed != null) {
                machine.setIsHoused(installed);
            }
        }

        @Override
        public void reloadCurrent() {
        }

        @Override
        protected void finishMachineAssignmentUpdate() {
            finished = true;
        }
    }

    private static class Update {

        private final ItemDomainMachineDesign machine;
        private final Item assignment;
        private final Boolean installed;

        Update(ItemDomainMachineDesign machine, Item assignment, Boolean installed) {
            this.machine = machine;
            this.assignment = assignment;
            this.installed = installed;
        }
    }
}
