/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemProject;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.view.objects.KeyValueObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemDomainMachineDesignControllerUtilityTest {

    private TestUtility utility;
    private UserInfo user;

    @Before
    public void setUp() {
        user = user(1);
        utility = new TestUtility();
    }

    @Test
    public void movesExistingChildToNewParentAndUpdatesParent() throws Exception {
        ItemDomainMachineDesign oldParent = machine("Old");
        ItemDomainMachineDesign newParent = machine("New");
        ItemDomainMachineDesign moved = machine("Moved");
        ItemElement membership = child(oldParent, moved, "OldName");

        ItemElement result = utility.performMachineMoveWithUpdate(newParent, moved, user);

        assertSame(membership, result);
        assertSame(newParent, membership.getParentItem());
        assertEquals("E1", membership.getName());
        assertTrue(newParent.getFullItemElementList().contains(membership));
        assertEquals(1, utility.updateCount);
    }

    @Test
    public void movesTopLevelMachineByCreatingMembership() throws Exception {
        ItemDomainMachineDesign newParent = machine("New");
        ItemDomainMachineDesign moved = machine("Moved");

        ItemElement result = utility.performMachineMoveWithUpdate(newParent, moved, user);

        assertSame(newParent, result.getParentItem());
        assertSame(moved, result.getContainedItem());
        assertEquals("E1", result.getName());
        assertTrue(newParent.getFullItemElementList().contains(result));
        assertEquals(1, utility.updateCount);
    }

    @Test
    public void rejectsMoveOfRepresentingMachineWithoutUpdate() {
        ItemDomainMachineDesign moved = machine("Moved");
        moved.setRepresentsCatalogElement(new ItemElement());

        CdbException error = expect(CdbException.class,
                () -> utility.performMachineMoveWithUpdate(machine("Parent"), moved, user));

        assertEquals("Cannot move assembly representing machine element.", error.getMessage());
        assertEquals(0, utility.updateCount);
    }

    @Test
    public void rejectsMoveWhenEitherMachineHasEntityTypeWithoutUpdate() {
        ItemDomainMachineDesign parent = machine("Parent");
        parent.setEntityTypeListFromApi(Collections.singletonList(entityType(1, "template")));

        CdbException error = expect(CdbException.class,
                () -> utility.performMachineMoveWithUpdate(parent, machine("Child"), user));

        assertEquals("Moving machines is currently only supported for standard machines.", error.getMessage());
        assertEquals(0, utility.updateCount);
    }

    @Test
    public void createsRepresentingMachineFromCatalogElement() throws Exception {
        ItemProject project = project(1, "APS-U");
        ItemDomainCatalog assembly = catalog("Assembly", project);
        ItemDomainCatalog part = catalog("Part", project);
        ItemElement represented = child(assembly, part, "Part-1");
        ItemDomainMachineDesign parent = machine("Parent");
        parent.setItemProjectList(Collections.singletonList(project(2, "Parent Project")));

        ItemDomainMachineDesign created
                = utility.createRepresentingMachineForAssemblyElement(parent, represented, user);

        assertSame(represented, created.getRepresentsCatalogElement());
        assertEquals("Part-1", created.getName());
        assertEquals(created.getViewUUID(), created.getItemIdentifier2());
        assertSame(part.getItemProjectList(), created.getItemProjectList());
        assertEquals(2, parent.getFullItemElementList().size());
        ItemElement placeholder = parent.getFullItemElementList().get(1);
        assertSame(created, placeholder.getContainedItem());
        assertTrue(created.getItemElementMemberList().contains(placeholder));
    }

    @Test
    public void createsRepresentingMachineFromInventoryAndNormalizesDerivedElement() throws Exception {
        ItemProject project = project(1, "APS-U");
        ItemDomainCatalog catalogAssembly = catalog("Catalog Assembly");
        ItemDomainCatalog catalogPart = catalog("Catalog Part", project);
        ItemElement catalogElement = child(catalogAssembly, catalogPart, "Catalog E1");
        ItemDomainInventory inventoryAssembly = inventory("Inventory Assembly", catalogAssembly);
        ItemDomainInventory inventoryPart = inventory("Inventory Part", catalogPart, project);
        ItemElement inventoryElement = child(inventoryAssembly, inventoryPart, "Inventory E1");
        inventoryElement.setDerivedFromItemElement(catalogElement);

        ItemDomainMachineDesign created = utility.createRepresentingMachineForAssemblyElement(
                machine("Parent"), inventoryElement, user);

        assertSame(catalogElement, created.getRepresentsCatalogElement());
        assertEquals("Catalog E1", created.getName());
        assertSame(catalogPart.getItemProjectList(), created.getItemProjectList());
    }

    @Test
    public void rejectsRepresentingElementOutsideCatalogOrInventoryWithoutMutation() {
        ItemDomainMachineDesign unsupportedAssembly = machine("Unsupported");
        ItemElement element = child(unsupportedAssembly, machine("Part"), "E1");
        ItemDomainMachineDesign parent = machine("Parent");
        int originalSize = parent.getFullItemElementList().size();

        InvalidArgument error = expect(InvalidArgument.class,
                () -> utility.createRepresentingMachineForAssemblyElement(parent, element, user));

        assertEquals("Cannot create representing machine element for selected item.", error.getMessage());
        assertEquals(originalSize, parent.getFullItemElementList().size());
        assertTrue(parent.getItemElementDisplayList().isEmpty());
    }

    @Test
    public void updatesCatalogAndInventoryAssignmentsAndInstalledState() throws Exception {
        ItemDomainMachineDesign node = machine("Node");
        ItemDomainCatalog catalog = catalog("Catalog");
        ItemDomainInventory inventory = inventory("Inventory", catalog);

        utility.updateAssignedItem(node, catalog, user, null);
        assertSame(catalog, node.getAssignedItem());
        assertTrue(node.isIsHoused());

        utility.updateAssignedItem(node, inventory, user, false);
        assertSame(inventory, node.getAssignedItem());
        assertFalse(node.isIsHoused());

        utility.updateAssignedItem(node, null, user, false);
        assertNull(node.getAssignedItem());
        assertTrue(node.isIsHoused());
    }

    @Test
    public void rejectsInstalledFlagForNonInventoryAssignment() {
        CdbException error = expect(CdbException.class,
                () -> utility.updateAssignedItem(machine("Node"), catalog("Catalog"), user, false));
        assertEquals("Is installed can only be set for inventory assignments.", error.getMessage());
    }

    @Test
    public void rejectsInventoryAlreadyAssignedToAnotherMachine() {
        ItemDomainInventory inventory = inventory("Inventory", catalog("Catalog"));
        ItemDomainMachineDesign existing = machine("Existing");
        ItemElement housing = new ItemElement();
        housing.setParentItem(existing);
        inventory.getItemElementMemberList2().add(housing);

        CdbException error = expect(CdbException.class,
                () -> utility.updateAssignedItem(machine("New"), inventory, user));
        assertTrue(error.getMessage().startsWith("Inventory item used."));
    }

    @Test
    public void rejectsAssignmentOnRepresentedNode() {
        ItemDomainMachineDesign node = machine("Node");
        node.setRepresentsCatalogElement(new ItemElement());

        CdbException error = expect(CdbException.class,
                () -> utility.updateAssignedItem(node, catalog("Catalog"), user));
        assertEquals("Cannot update assigned item of machine design. Must update parent assembly for promotoed machine elements.",
                error.getMessage());
    }

    @Test
    public void representedChildConstrainsParentAssignmentAndClearFallsBackToCatalog() throws Exception {
        ItemDomainCatalog required = catalog("Required");
        ItemElement represented = child(required, catalog("Part"), "E1");
        ItemDomainMachineDesign parent = machine("Parent");
        ItemDomainMachineDesign representedChild = machine("Child");
        representedChild.setRepresentsCatalogElement(represented);
        child(parent, representedChild, "E1");

        CdbException error = expect(CdbException.class,
                () -> utility.updateAssignedItem(parent, catalog("Wrong"), user));
        assertTrue(error.getMessage().startsWith("Existing assembly representing machine elements exist."));

        utility.updateAssignedItem(parent, null, user);
        assertSame(required, parent.getAssignedItem());
    }

    @Test
    public void assignsAndClearsAvailableRepresentedElement() throws Exception {
        RepresentationFixture fixture = representationFixture();

        utility.updateRepresentingAssemblyElementForMachine(fixture.node, fixture.available, false);
        assertSame(fixture.available, fixture.node.getRepresentsCatalogElement());

        utility.updateRepresentingAssemblyElementForMachine(fixture.node, null, false);
        assertNull(fixture.node.getRepresentsCatalogElement());
    }

    @Test
    public void rejectsUnavailableRepresentedElement() {
        RepresentationFixture fixture = representationFixture();

        InvalidArgument error = expect(InvalidArgument.class,
                () -> utility.updateRepresentingAssemblyElementForMachine(
                        fixture.node, new ItemElement(), false));
        assertTrue(error.getMessage().startsWith("Element provided is not an available element"));
    }

    @Test
    public void rejectsRepresentationWhenNodeHasAssignment() {
        RepresentationFixture fixture = representationFixture();
        fixture.node.setAssignedItem(catalog("Assigned"));

        InvalidObjectState error = expect(InvalidObjectState.class,
                () -> utility.updateRepresentingAssemblyElementForMachine(
                        fixture.node, fixture.available, false));
        assertEquals("Node has an assigned item set.", error.getMessage());
    }

    @Test
    public void rejectsRepresentationForSpecialEntityType() {
        RepresentationFixture fixture = representationFixture();
        fixture.node.setEntityTypeListFromApi(Collections.singletonList(entityType(10, "control")));

        InvalidObjectState error = expect(InvalidObjectState.class,
                () -> utility.updateRepresentingAssemblyElementForMachine(
                        fixture.node, fixture.available, false));
        assertTrue(error.getMessage().startsWith("Cannot apply representing element for"));
    }

    @Test
    public void rejectsRepresentationForNodeCreatedFromTemplate() {
        RepresentationFixture fixture = representationFixture();
        TestMachine templateNode = testMachine("Node");
        templateNode.assignCreatedFromTemplate(machine("Template"));
        fixture.parent.getFullItemElementList().remove(
                fixture.node.getItemElementMemberList().get(0));
        child(fixture.parent, templateNode, "M1");

        InvalidObjectState error = expect(InvalidObjectState.class,
                () -> utility.updateRepresentingAssemblyElementForMachine(
                        templateNode, fixture.available, false));
        assertTrue(error.getMessage().contains("is created from template Template"));
    }

    @Test
    public void rejectsNestedRepresentationBelowRepresentedParent() {
        RepresentationFixture fixture = representationFixture();
        fixture.parent.setRepresentsCatalogElement(new ItemElement());

        InvalidObjectState error = expect(InvalidObjectState.class,
                () -> utility.updateRepresentingAssemblyElementForMachine(
                        fixture.node, fixture.available, false));
        assertTrue(error.getMessage().contains("with represented element reference"));
    }

    @Test
    public void availableElementsExcludeElementsRepresentedBySiblings() {
        RepresentationFixture fixture = representationFixture();
        ItemElement second = child(fixture.catalog, catalog("Second Part"), "E2");
        ItemDomainMachineDesign sibling = machine("Sibling");
        sibling.setRepresentsCatalogElement(fixture.available);
        child(fixture.parent, sibling, "M2");

        assertEquals(Collections.singletonList(second),
                utility.fetchElementsAvaiableForNodeRepresentation(fixture.node));
    }

    private static RepresentationFixture representationFixture() {
        RepresentationFixture fixture = new RepresentationFixture();
        fixture.catalog = catalog("Assembly");
        fixture.available = child(fixture.catalog, catalog("Part"), "E1");
        fixture.parent = machine("Parent");
        fixture.parent.setAssignedItem(fixture.catalog);
        fixture.node = machine("Node");
        child(fixture.parent, fixture.node, "M1");
        return fixture;
    }

    private static class RepresentationFixture {
        private ItemDomainCatalog catalog;
        private ItemElement available;
        private ItemDomainMachineDesign parent;
        private ItemDomainMachineDesign node;
    }

    @Test
    public void extractsDeduplicatedTemplateVariablesAndSubstitutesValues() {
        ItemDomainMachineDesign template = machine("Sector {sector} {device}");
        template.setItemIdentifier1("{sector}-{device}-{sector}");

        List<KeyValueObject> variables = utility.generateMachineDesignTemplateNameVars(template);

        assertEquals(2, variables.size());
        assertEquals("sector", variables.get(0).getKey());
        assertEquals("device", variables.get(1).getKey());
        variables.get(0).setValue("S01");
        variables.get(1).setValue("Q1");
        assertEquals("Sector S01 Q1",
                utility.generateMachineDesignNameForTemplateItem(template.getName(), variables));

        ItemDomainMachineDesign target = machine("Target");
        utility.setMachineDesginIdentifiersFromTemplateItem(template, target, variables);
        assertEquals("Sector S01 Q1", target.getName());
        assertEquals("S01-Q1-S01", target.getItemIdentifier1());
    }

    @Test
    public void recursivelyCollectsTemplateVariablesWithoutDuplicates() {
        ItemDomainMachineDesign root = machine("{sector}");
        ItemDomainMachineDesign child = machine("{sector}-{device}");
        child(root, child, "E1");

        List<KeyValueObject> variables
                = utility.generateMachineDesignTemplateNameListRecursivelly(root);

        assertEquals(2, variables.size());
        assertEquals("sector", variables.get(0).getKey());
        assertEquals("device", variables.get(1).getKey());
    }

    @Test
    public void searchDelegatesToNoEntityTypeFacade() {
        List<ItemDomainMachineDesign> expected
                = Collections.singletonList(machine("Result"));
        FakeMachineFacade facade = new FakeMachineFacade(expected);
        utility.useFacade(facade);

        assertSame(expected, utility.searchEntities("sector*"));
        assertEquals("sector*", facade.searchString);
    }

    private static UserInfo user(int id) {
        UserInfo value = new UserInfo();
        value.setId(id);
        value.setUserGroupList(new ArrayList<>());
        return value;
    }

    private static ItemProject project(int id, String name) {
        return new ItemProject(id, name);
    }

    private static EntityType entityType(int id, String name) {
        return new EntityType(id, name);
    }

    private static ItemDomainMachineDesign machine(String name) {
        ItemDomainMachineDesign item = new ItemDomainMachineDesign();
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        item.setItemProjectList(new ArrayList<>());
        item.setItemElementMemberList(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        item.setEntityInfo(new EntityInfo());
        return item;
    }

    private static ItemElement child(Item parent, Item contained, String name) {
        ItemElement element = new ItemElement();
        element.setParentItem(parent);
        element.setContainedItem(contained);
        element.setName(name);
        parent.getFullItemElementList().add(element);
        contained.appendItemElementMemberList(element);
        parent.resetItemElementVars();
        return element;
    }

    private static ItemDomainCatalog catalog(String name, ItemProject... projects) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        initializeAssembly(item, name, projects);
        return item;
    }

    private static ItemDomainInventory inventory(String name, ItemDomainCatalog catalog,
            ItemProject... projects) {
        ItemDomainInventory item = new ItemDomainInventory();
        initializeAssembly(item, name, projects);
        item.setCatalogItem(catalog);
        item.setItemElementMemberList2(new ArrayList<>());
        return item;
    }

    private static void initializeAssembly(Item item, String name, ItemProject... projects) {
        item.setName(name);
        item.setItemProjectList(new ArrayList<>(Arrays.asList(projects)));
        item.setItemElementMemberList(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        item.setEntityInfo(new EntityInfo());
    }

    private static <T extends Throwable> T expect(Class<T> type, ThrowingAction action) {
        try {
            action.run();
            fail("Expected " + type.getSimpleName());
        } catch (Throwable thrown) {
            if (!type.isInstance(thrown)) {
                throw new AssertionError("Unexpected exception", thrown);
            }
            return type.cast(thrown);
        }
        throw new AssertionError("Expected exception was not thrown");
    }

    private interface ThrowingAction {
        void run() throws Exception;
    }

    private static class TestMachine extends ItemDomainMachineDesign {

        void assignCreatedFromTemplate(Item template) {
            createdFromTemplate = template;
        }
    }

    private static TestMachine testMachine(String name) {
        TestMachine item = new TestMachine();
        item.setName(name);
        item.setEntityTypeListFromApi(new ArrayList<>());
        item.setItemProjectList(new ArrayList<>());
        item.setItemElementMemberList(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Collections.singletonList(self)));
        item.setEntityInfo(new EntityInfo());
        return item;
    }

    private static class FakeMachineFacade extends ItemDomainMachineDesignFacade {

        private final List<ItemDomainMachineDesign> results;
        private String searchString;

        FakeMachineFacade(List<ItemDomainMachineDesign> results) {
            this.results = results;
        }

        @Override
        public List<ItemDomainMachineDesign> searchEntitiesNoEntityType(String value) {
            searchString = value;
            return results;
        }
    }

    private static class TestUtility extends ItemDomainMachineDesignControllerUtility {

        private int updateCount;

        void useFacade(ItemDomainMachineDesignFacade facade) {
            itemFacade = facade;
        }

        @Override
        public ItemDomainMachineDesign createEntityInstanceBasedOnParent(
                ItemDomainMachineDesign parentMachine, UserInfo sessionUser) {
            ItemDomainMachineDesign item = machine("New Machine");
            item.setItemProjectList(parentMachine.getItemProjectList());
            return item;
        }

        @Override
        public ItemDomainMachineDesign update(ItemDomainMachineDesign entity,
                UserInfo updatedByUserInfo) {
            updateCount++;
            return entity;
        }
    }
}
