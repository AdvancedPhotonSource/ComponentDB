/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.common.exceptions.InvalidObjectState;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.view.objects.CatalogItemElementConstraintInformation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemDomainMachineDesignRepresentationTest {

    private final ItemDomainMachineDesignControllerUtility utility
            = new ItemDomainMachineDesignControllerUtility();

    @Test
    public void supportsThreeLevelRepresentation() throws Exception {
        ItemDomainCatalog assemblyA = catalog(10, "A");
        ItemDomainCatalog assemblyB = catalog(11, "B");
        ItemDomainCatalog assemblyC = catalog(12, "C");
        ItemElement bElement = catalogElement(assemblyA, assemblyB, "B element");
        ItemElement cElement = catalogElement(assemblyB, assemblyC, "C element");

        ItemDomainMachineDesign root = machine(1, "root", assemblyA);
        ItemDomainMachineDesign middle = childMachine(2, "middle", root);
        utility.updateRepresentingAssemblyElementForMachine(middle, bElement, false);
        ItemDomainMachineDesign leaf = childMachine(3, "leaf", middle);
        utility.updateRepresentingAssemblyElementForMachine(leaf, cElement, false);

        assertSame(bElement, middle.getRepresentsCatalogElement());
        assertSame(cElement, leaf.getRepresentsCatalogElement());
    }

    @Test(expected = InvalidObjectState.class)
    public void cannotUnlinkParentWhileChildRepresentsItsAssembly() throws Exception {
        ItemDomainCatalog assemblyA = catalog(10, "A");
        ItemDomainCatalog assemblyB = catalog(11, "B");
        ItemElement bElement = catalogElement(assemblyA, assemblyB, "B element");
        ItemElement childElement = catalogElement(assemblyB, catalog(12, "C"), "C element");
        ItemDomainMachineDesign root = machine(1, "root", assemblyA);
        ItemDomainMachineDesign middle = childMachine(2, "middle", root);
        middle.setRepresentsCatalogElement(bElement);
        ItemDomainMachineDesign leaf = childMachine(3, "leaf", middle);
        leaf.setRepresentsCatalogElement(childElement);

        utility.updateRepresentingAssemblyElementForMachine(middle, null, false);
    }

    @Test
    public void assemblyRepresentationSwapRequiresCompatibleChildren() throws Exception {
        ItemDomainCatalog assemblyA = catalog(10, "A");
        ItemDomainCatalog assemblyB = catalog(11, "B");
        ItemDomainCatalog assemblyD = catalog(12, "D");
        ItemElement originalB = catalogElement(assemblyA, assemblyB, "B original");
        ItemElement replacementB = catalogElement(assemblyA, assemblyB, "B replacement");
        ItemElement replacementD = catalogElement(assemblyA, assemblyD, "D replacement");
        ItemElement childElement = catalogElement(assemblyB, catalog(13, "C"), "C element");
        ItemDomainMachineDesign root = machine(1, "root", assemblyA);
        ItemDomainMachineDesign middle = childMachine(2, "middle", root);
        middle.setRepresentsCatalogElement(originalB);
        ItemDomainMachineDesign leaf = childMachine(3, "leaf", middle);
        leaf.setRepresentsCatalogElement(childElement);

        utility.updateRepresentingAssemblyElementForMachine(middle, replacementB, false);
        assertSame(replacementB, middle.getRepresentsCatalogElement());

        try {
            utility.updateRepresentingAssemblyElementForMachine(middle, replacementD, false);
            fail("Incompatible assembly replacement was accepted.");
        } catch (InvalidObjectState expected) {
        }
        assertSame(replacementB, middle.getRepresentsCatalogElement());
    }

    @Test
    public void promotionValidationRejectsForeignAndDuplicateElements() throws Exception {
        ItemDomainCatalog assembly = catalog(10, "assembly");
        ItemElement valid = catalogElement(assembly, catalog(11, "part"), "part");
        ItemElement foreign = catalogElement(catalog(12, "foreign"), catalog(13, "other"), "other");
        ItemDomainMachineDesign parent = machine(1, "parent", assembly);

        assertInvalid(parent, foreign);
        ItemDomainMachineDesign sibling = childMachine(2, "sibling", parent);
        sibling.setRepresentsCatalogElement(valid);
        assertInvalid(parent, valid);
        assertInvalid(parent, null);
    }

    @Test
    public void representedCatalogElementRemovalAndReplacementConstraints() {
        ItemElement represented = catalogElement(catalog(10, "assembly"), catalog(11, "part"), "part");
        ItemDomainMachineDesign representingMachine = machine(1, "representing", null);
        ItemElement machineSelf = representingMachine.getSelfElement();
        machineSelf.setRepresentsItemElement(represented);
        represented.setRepresentsItemElementList(new ArrayList<>(Arrays.asList(machineSelf)));
        CatalogItemElementConstraintInformation constraint
                = new CatalogItemElementConstraintInformation(represented);

        assertFalse(constraint.isSafeToRemove());
        assertTrue(constraint.isSafeToUpdateContainedItem());

        ItemDomainMachineDesign child = childMachine(2, "child", representingMachine);
        child.setRepresentsCatalogElement(catalogElement(catalog(12, "child assembly"),
                catalog(13, "child part"), "child part"));
        assertFalse(constraint.isSafeToUpdateContainedItem());

        child.setRepresentsCatalogElement(null);
        assertTrue(constraint.isSafeToUpdateContainedItem());

        represented.getRepresentsItemElementList().clear();
        assertTrue(constraint.isSafeToRemove());
    }

    private void assertInvalid(ItemDomainMachineDesign parent, ItemElement element) throws CdbException {
        try {
            utility.validateElementAvailableForRepresentation(parent, null, element);
            fail("Invalid representation was accepted.");
        } catch (InvalidArgument expected) {
        }
    }

    private static ItemDomainCatalog catalog(int id, String name) {
        ItemDomainCatalog item = new ItemDomainCatalog();
        item.setId(id);
        item.setName(name);
        item.setDomain(new Domain(ItemDomainName.CATALOG_ID, ItemDomainName.catalog.getValue()));
        ItemElement self = new ItemElement();
        self.setParentItem(item);
        item.setFullItemElementList(new ArrayList<>(Arrays.asList(self)));
        return item;
    }

    private static ItemElement catalogElement(ItemDomainCatalog parent, Item contained, String name) {
        ItemElement element = new ItemElement();
        element.setName(name);
        element.setParentItem(parent);
        element.setContainedItem(contained);
        parent.getFullItemElementList().add(element);
        parent.resetItemElementDisplayList();
        return element;
    }

    private static ItemDomainMachineDesign machine(int id, String name, Item assigned) {
        ItemDomainMachineDesign machine = new ItemDomainMachineDesign();
        machine.setId(id);
        machine.setName(name);
        machine.setDomain(new Domain(ItemDomainName.MACHINE_DESIGN_ID, ItemDomainName.machineDesign.getValue()));
        machine.setEntityTypeListFromApi(new ArrayList<>());
        machine.setItemElementMemberList(new ArrayList<>());
        ItemElement self = new ItemElement();
        self.setParentItem(machine);
        self.setContainedItem2(assigned);
        machine.setFullItemElementList(new ArrayList<>(Arrays.asList(self)));
        return machine;
    }

    private static ItemDomainMachineDesign childMachine(int id, String name, ItemDomainMachineDesign parent) {
        ItemDomainMachineDesign child = machine(id, name, null);
        ItemElement membership = new ItemElement();
        membership.setName(name);
        membership.setParentItem(parent);
        membership.setContainedItem(child);
        parent.getFullItemElementList().add(membership);
        parent.resetItemElementDisplayList();
        child.setItemElementMemberList(new ArrayList<>(Collections.singletonList(membership)));
        return child;
    }
}
