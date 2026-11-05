/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemDomainMachineDesignBaseTreeNodeTest {

    @Test
    public void normalFilteringStillRespectsDisabledTopLevelMachines() throws Exception {
        ItemDomainMachineDesign top = machine(1, "Top");
        top.updateFilterMachineNode(false);
        ItemDomainMachineDesign child = childMachine(2, "Child", top);
        ItemDomainMachineDesignTreeNode root = root(top);
        setRawFilterResults(root, Collections.singletonList(child));

        root.finishFiltering();

        assertTrue(root.getChildren().isEmpty());
        assertTrue(root.getFilterResults().isEmpty());
    }

    @Test
    public void suppliedResultsIgnoreNormalTopLevelSearchSelection() {
        ItemDomainMachineDesign top = machine(1, "Top");
        ItemDomainMachineDesign child = childMachine(2, "Child", top);
        ItemDomainMachineDesignTreeNode root = root(top);

        root.buildResultTree(Collections.singletonList(child));

        assertEquals(1, root.getChildren().size());
        ItemDomainMachineDesignTreeNode topNode
                = (ItemDomainMachineDesignTreeNode) root.getChildren().get(0);
        assertSame(top, containedMachine(topNode));
        assertEquals(1, topNode.getChildren().size());
        assertSame(child, containedMachine(
                (ItemDomainMachineDesignTreeNode) topNode.getChildren().get(0)));
        assertEquals(Collections.singletonList(child), root.getFilterResults());
    }

    @Test
    public void suppliedResultsDeduplicateSharedAncestors() {
        ItemDomainMachineDesign top = machine(1, "Top");
        ItemDomainMachineDesign first = childMachine(2, "First", top);
        ItemDomainMachineDesign second = childMachine(3, "Second", top);
        ItemDomainMachineDesignTreeNode root = root(top);

        root.buildResultTree(Arrays.asList(first, second));

        assertEquals(1, root.getChildren().size());
        ItemDomainMachineDesignTreeNode topNode
                = (ItemDomainMachineDesignTreeNode) root.getChildren().get(0);
        assertEquals(2, topNode.getChildren().size());
        assertEquals(Arrays.asList(first, second), root.getFilterResults());
    }

    @Test
    public void emptySuppliedResultsProduceEmptyTree() {
        ItemDomainMachineDesignTreeNode root = root(machine(1, "Top"));

        root.buildResultTree(Collections.emptyList());

        assertTrue(root.getChildren().isEmpty());
        assertTrue(root.getFilterResults().isEmpty());
    }

    @Test
    public void suppliedResultModeDoesNotAffectLaterNormalFiltering() throws Exception {
        ItemDomainMachineDesign top = machine(1, "Top");
        top.updateFilterMachineNode(false);
        ItemDomainMachineDesign child = childMachine(2, "Child", top);
        ItemDomainMachineDesignTreeNode root = root(top);
        root.buildResultTree(Collections.singletonList(child));
        setRawFilterResults(root, Collections.singletonList(child));

        root.finishFiltering();

        assertTrue(root.getChildren().isEmpty());
    }

    private static ItemDomainMachineDesignTreeNode root(ItemDomainMachineDesign... topItems) {
        return new ItemDomainMachineDesignTreeNode(
                new ArrayList<>(Arrays.asList(topItems)),
                new Domain(ItemDomainName.MACHINE_DESIGN_ID, ItemDomainName.machineDesign.getValue()),
                null,
                null);
    }

    private static ItemDomainMachineDesign machine(int id, String name) {
        ItemDomainMachineDesign machine = new ItemDomainMachineDesign();
        machine.setId(id);
        machine.setName(name);
        machine.setDomain(new Domain(ItemDomainName.MACHINE_DESIGN_ID,
                ItemDomainName.machineDesign.getValue()));
        machine.setItemElementMemberList(new ArrayList<>());

        ItemElement self = new ItemElement();
        self.setParentItem(machine);
        self.setSortOrder((float) id);
        machine.setFullItemElementList(Collections.singletonList(self));
        return machine;
    }

    private static ItemDomainMachineDesign childMachine(
            int id, String name, ItemDomainMachineDesign parent) {
        ItemDomainMachineDesign child = machine(id, name);
        ItemElement membership = new ItemElement();
        membership.setParentItem(parent);
        membership.setContainedItem(child);
        membership.setSortOrder((float) id);
        child.setItemElementMemberList(Collections.singletonList(membership));
        return child;
    }

    private static ItemDomainMachineDesign containedMachine(
            ItemDomainMachineDesignTreeNode node) {
        return (ItemDomainMachineDesign) ((ItemElement) node.getData()).getContainedItem();
    }

    private static void setRawFilterResults(ItemDomainMachineDesignTreeNode root,
            List<ItemDomainMachineDesign> results) throws Exception {
        Field field = ItemDomainMachineDesignBaseTreeNode.class
                .getDeclaredField("rawFilterResults");
        field.setAccessible(true);
        field.set(root, results);
    }
}
