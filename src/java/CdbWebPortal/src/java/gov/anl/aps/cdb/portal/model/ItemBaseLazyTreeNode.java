/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemBaseLazyTreeNode.ItemTreeBaseConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemFacadeBase;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.comparator.ItemSelfElementSortOrderComparator;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 * @param <ItemEntity>
 * @param <ItemEntityFacade>
 * @param <ItemNodeConfiguration>
 */
public abstract class ItemBaseLazyTreeNode<ItemEntity extends Item, ItemEntityFacade extends ItemFacadeBase<ItemEntity>, ItemNodeConfiguration extends ItemTreeBaseConfiguration> extends DefaultTreeNode {

    private final static Integer MAXIMUM_EXPANDED_NODES = 250;

    protected Domain domain;

    protected List<ItemEntity> topLevelItems;

    boolean childrenLoaded = false;
    ItemNodeConfiguration config;

    protected ItemBaseLazyTreeNode(ItemElement element, ItemNodeConfiguration config, ItemBaseLazyTreeNode parent) {
        super(element);
        this.config = config;
        setParent(parent);
    }

    public ItemBaseLazyTreeNode(List<ItemEntity> items, Domain domain, ItemEntityFacade facade) {
        initFirstLevel(items, domain, facade);
    }

    protected final void initFirstLevel(List<ItemEntity> items, Domain domain, ItemEntityFacade facade) {
        ItemNodeConfiguration config = createTreeNodeConfiguration();
        initFirstLevel(items, domain, facade, config);
    }

    protected final void initFirstLevel(List<ItemEntity> items, Domain domain, ItemEntityFacade facade, ItemNodeConfiguration config) {
        this.config = config;
        this.domain = domain;
        config.setFacade(facade);
        this.topLevelItems = items;
        // Make sure the sort order is correct for top level nodes 
        this.topLevelItems.sort(new ItemSelfElementSortOrderComparator());

        addTopLevelChildren(this.topLevelItems);

        this.setExpanded(true);
        childrenLoaded = true;
    }

    public boolean getIsTopLevel() {
        ItemBaseLazyTreeNode parent = this.getParent();
        return parent.topLevelItems != null;
    }

    protected void addTopLevelChildren(List<ItemEntity> topNodes) {
        for (ItemEntity item : topNodes) {
            ItemElement element = createMockItemElement(item);
            createChildNode(element);
        }

        // Expand first node if tree is only one node.
        if (topNodes.size() == 1) {
            List<ItemBaseLazyTreeNode> treeNodeItemChildren = this.getTreeNodeItemChildren();
            treeNodeItemChildren.get(0).setExpanded(true);
        }
    }

    protected ItemElement createMockItemElement(ItemEntity item) {
        ItemElement selfElement = item.getSelfElement();
        ItemElement element = new ItemElement();
        Float sortOrder = selfElement.getSortOrder();
        element.setContainedItem(item);
        element.setSortOrder(sortOrder);
        return element;
    }

    public ItemBaseLazyTreeNode() {
        // Empty model
        config = createTreeNodeConfiguration();
    }

    public abstract ItemNodeConfiguration createTreeNodeConfiguration();

    protected abstract <T extends ItemBaseLazyTreeNode> T createTreeNodeObject(ItemElement itemElement);

    protected abstract <T extends ItemBaseLazyTreeNode> T createTreeNodeObject(ItemElement element, ItemNodeConfiguration config, ItemBaseLazyTreeNode parent);

    public ItemElement getElement() {
        Object data = super.getData();
        return (ItemElement) data;
    }

    @Override
    public int getChildCount() {
        fetchChildren();
        return super.getChildCount();
    }

    @Override
    public boolean isLeaf() {
        fetchChildren();
        return super.isLeaf();
    }

    @Override
    public List<TreeNode> getChildren() {
        fetchChildren();
        return super.getChildren();
    }

    public ItemNodeConfiguration getConfig() {
        return config;
    }

    protected <T extends ItemBaseLazyTreeNode> T createChildNode(ItemElement itemElement) {
        return createChildNode(itemElement, false);
    }

    protected <T extends ItemBaseLazyTreeNode> T createChildNode(ItemElement itemElement, boolean childrenLoaded) {
        T machine = createTreeNodeObject(itemElement, config, this);
        if (childrenLoaded) {
            machine.childrenLoaded = childrenLoaded;
        }
        super.getChildren().add(machine);

        return machine;
    }

    public <T extends ItemBaseLazyTreeNode> List<T> getTreeNodeItemChildren() {
        return (List<T>) (List<?>) this.getChildren();
    }

    private void fetchChildren() {
        unloadAdditionalChildren();

        if (!childrenLoaded || loadAdditionalNodes()) {
            TreeNode parent = getParent();
            if (config.isLoadAllChildren() || parent == null || parent.isExpanded()) {
                ItemElement element = getElement();
                if (element != null) {
                    Item containedItem = element.getContainedItem();

                    if (containedItem == null) {
                        return;
                    }                   

                    if (!childrenLoaded) {
                        childrenLoaded = true;

                        if (isLoadElementChildren()) {
                            List<ItemElement> itemElementList = containedItem.getItemElementDisplayList();

                            for (ItemElement itemElement : itemElementList) {
                                createChildNode(itemElement);
                            }
                        }
                    }

                    loadAdditionalChildren();
                }
            }
        }
    }

    protected void loadAdditionalChildren() {
    }

    protected void unloadAdditionalChildren() {
    }

    // If toggle of relationship is avaialble, override this.
    protected boolean loadAdditionalNodes() {
        return false;
    }
    
    protected boolean isLoadElementChildren() {
        return true;
    }

    @Override
    public ItemBaseLazyTreeNode getParent() {
        return (ItemBaseLazyTreeNode) super.getParent();
    }

    public void expandAllChildren(boolean expanded) throws CdbException {
        Integer count = expandAllChildren(this, expanded, 0);
        if (expanded && count > MAXIMUM_EXPANDED_NODES) {
            throw new CdbException("Exceeded maximum expanded nodes. Select a child and expand again.");
        }
    }

    private Integer expandAllChildren(TreeNode treeNode, boolean expanded, Integer count) {
        treeNode.setExpanded(expanded);
        count++;
        if (expanded && count > MAXIMUM_EXPANDED_NODES) {
            treeNode.setExpanded(!expanded);
            return count;
        }

        List<TreeNode> children = treeNode.getChildren();
        if (children != null) {
            boolean keepGoing = children.size() > 0;
            int direction = 1;
            int i = 0;
            while (keepGoing) {
                TreeNode child = children.get(i);
                i += direction;

                if (direction == 1) {
                    count = expandAllChildren(child, expanded, count);
                    if (expanded && count > MAXIMUM_EXPANDED_NODES) {
                        direction = -1;
                        i--;
                    } else {
                        if (i >= children.size()) {
                            keepGoing = false;
                        }
                    }
                } else {
                    expandAllChildren(child, !expanded, 0);
                    if (i < 0) {
                        keepGoing = false;
                    }
                }
            }
        }

        if (expanded && count > MAXIMUM_EXPANDED_NODES) {
            treeNode.setExpanded(!expanded);
        }

        return count;
    }

    public class ItemTreeBaseConfiguration {

        private boolean setMachineTreeNodeType = true;
        private boolean loadAllChildren = false;
        private ItemDomainMachineDesignControllerUtility mdControllerUtility = null;
        private ItemEntityFacade facade;

        public ItemTreeBaseConfiguration() {
        }

        public ItemEntityFacade getFacade() {
            return facade;
        }

        public final void setFacade(ItemEntityFacade facade) {
            this.facade = facade;
        }

        public boolean isSetMachineTreeNodeType() {
            return setMachineTreeNodeType;
        }

        public void setSetMachineTreeNodeType(boolean setMachineTreeNodeType) {
            this.setMachineTreeNodeType = setMachineTreeNodeType;
        }

        public boolean isLoadAllChildren() {
            return loadAllChildren;
        }

        public void setLoadAllChildren(boolean loadAllChildren) {
            this.loadAllChildren = loadAllChildren;
        }       
    }

}
