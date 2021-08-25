/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.RelationshipType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMachineDesignRelationshipTreeNode extends ItemDomainMachineDesignTreeNode {

    ItemElementRelationshipTypeNames relationshipToLoad;

    public ItemDomainMachineDesignRelationshipTreeNode(List<ItemDomainMachineDesign> items, Domain domain,
            ItemDomainMachineDesignFacade facade, ItemElementRelationshipTypeNames relationshipToLoad) {
        MachineTreeConfiguration config = new MachineTreeConfiguration();
        initRelationshipTree(items, domain, facade, relationshipToLoad, config);
    }

    public ItemDomainMachineDesignRelationshipTreeNode(ItemDomainMachineDesign item, Domain domain,
            ItemDomainMachineDesignFacade facade, ItemElementRelationshipTypeNames relationshipToLoad, 
            boolean setTypes, boolean loadAllChildren) {
        List<ItemDomainMachineDesign> items = new ArrayList<>();
        items.add(item);
        MachineTreeConfiguration config = new MachineTreeConfiguration();
        config.setLoadAllChildren(loadAllChildren);
        config.setSetMachineTreeNodeType(setTypes);
        initRelationshipTree(items, domain, facade, relationshipToLoad, config);
    }

    protected final void initRelationshipTree(List<ItemDomainMachineDesign> items, Domain domain,
            ItemDomainMachineDesignFacade facade, ItemElementRelationshipTypeNames relationshipToLoad, MachineTreeConfiguration config) {
        this.relationshipToLoad = relationshipToLoad;
        initFirstLevel(items, domain, facade, config);
    }

    protected ItemDomainMachineDesignRelationshipTreeNode(ItemElement element, MachineTreeConfiguration config, ItemDomainMachineDesignTreeNode parent, boolean setType, ItemElementRelationshipTypeNames relationshipToLoad) {
        super(element, config, parent, setType);
        this.relationshipToLoad = relationshipToLoad;
    }

    @Override
    protected ItemDomainMachineDesignTreeNode createTreeNodeObject(ItemElement itemElement) {
        return new ItemDomainMachineDesignRelationshipTreeNode(itemElement, config, this, true, relationshipToLoad);
    }

    @Override
    protected void loadRelationships() {
        ItemElement element = this.getElement();
        Item machineElement = element.getContainedItem();

        List<ItemElementRelationship> itemElementRelationshipList1 = machineElement.getItemElementRelationshipList1();
        for (ItemElementRelationship ier : itemElementRelationshipList1) {
            RelationshipType relationshipType = ier.getRelationshipType();
            String relationshipTypeName = relationshipToLoad.getValue();

            if (relationshipType.getName().equals(relationshipTypeName)) {
                ItemElement firstItemElement = ier.getFirstItemElement();
                Item parentItem = firstItemElement.getParentItem();
                ItemElement mockElement = createMockItemElement((ItemDomainMachineDesign) parentItem);

                ItemDomainMachineDesignRelationshipTreeNode node = null;
                node = new ItemDomainMachineDesignRelationshipTreeNode(mockElement, config, this, false, relationshipToLoad);
                if (config.isSetMachineTreeNodeType()) {
                    node.setType("machineDesignRelationshipNode");
                }
                super.getChildren().add(node);
            }
        }
    }

    @Override
    protected boolean isLoadElementChildren() {
        ItemElement element = this.getElement();
        Item containedItem = element.getContainedItem();
        if (containedItem instanceof ItemDomainMachineDesign) {
            return ItemDomainMachineDesign.isItemControl(containedItem);
        }

        return true;
    }

}
