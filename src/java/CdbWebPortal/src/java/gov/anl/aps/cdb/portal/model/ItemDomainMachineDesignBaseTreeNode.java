/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignBaseTreeNode.MachineTreeBaseConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMachineDesignQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 * @param <MachineNodeConfiguration>
 */
public abstract class ItemDomainMachineDesignBaseTreeNode<MachineNodeConfiguration extends MachineTreeBaseConfiguration> extends DefaultTreeNode {

    private final static Integer MAXIMUM_EXPANDED_NODES = 250;

    private String nameFilter = "";
    protected Domain domain;

    private List<ItemDomainMachineDesign> topLevelItems;

    private List<ItemDomainMachineDesign> rawFilterResults;
    private List<ItemDomainMachineDesign> filterResults;
    private Boolean filterAllNodes;

    boolean childrenLoaded = false;
    MachineNodeConfiguration config;

    protected ItemDomainMachineDesignBaseTreeNode(ItemElement element, MachineNodeConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setTypeForLevel) {
        super(element);
        this.config = config;
        setParent(parent);
        if (setTypeForLevel && config.isSetMachineTreeNodeType()) {
            setTreeNodeTypeMachineDesignTreeList();
        }
    }

    public ItemDomainMachineDesignBaseTreeNode(List<ItemDomainMachineDesign> items, Domain domain, ItemDomainMachineDesignFacade facade) {
        initFirstLevel(items, domain, facade);
    }

    protected final void initFirstLevel(List<ItemDomainMachineDesign> items, Domain domain, ItemDomainMachineDesignFacade facade) {
        MachineNodeConfiguration config = createTreeNodeConfiguration();
        initFirstLevel(items, domain, facade, config);
    }

    protected final void initFirstLevel(List<ItemDomainMachineDesign> items, Domain domain, ItemDomainMachineDesignFacade facade, MachineNodeConfiguration config) {
        this.config = config;
        this.domain = domain;
        config.setDesignFacade(facade);
        this.topLevelItems = items;

        addTopLevelChildren(items);

        this.setExpanded(true);
        childrenLoaded = true;
    }

    public ItemDomainMachineDesignBaseTreeNode(Object data) {
        super(data);
    }
    
    public boolean getIsTopLevel() {
        ItemDomainMachineDesignBaseTreeNode parent = this.getParent();
        return parent.topLevelItems != null;
    }

    private void addTopLevelChildren(List<ItemDomainMachineDesign> topNodes) {
        for (ItemDomainMachineDesign item : topNodes) {
            ItemElement element = createMockItemElement(item);
            createChildNode(element);
        }

        // Expand first node if tree is only one node.
        if (topNodes.size() == 1) {
            List<ItemDomainMachineDesignBaseTreeNode> machineChildren = this.getMachineChildren();
            machineChildren.get(0).setExpanded(true);
        }
    }

    protected ItemElement createMockItemElement(ItemDomainMachineDesign item) {
        ItemElement selfElement = item.getSelfElement();
        ItemElement element = new ItemElement();
        Float sortOrder = selfElement.getSortOrder();
        element.setContainedItem(item);
        element.setSortOrder(sortOrder);
        return element;
    }

    public ItemDomainMachineDesignBaseTreeNode() {
        // Empty model
        config = createTreeNodeConfiguration();
    }

    public abstract MachineNodeConfiguration createTreeNodeConfiguration();

    protected abstract <T extends ItemDomainMachineDesignBaseTreeNode> T createTreeNodeObject(ItemElement itemElement);

    protected abstract <T extends ItemDomainMachineDesignBaseTreeNode> T createTreeNodeObject(ItemElement element, MachineNodeConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setType);

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

    public MachineNodeConfiguration getConfig() {
        return config;
    }

    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createChildNode(ItemElement itemElement) {
        return createChildNode(itemElement, false);
    }
    
    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createChildNode(ItemElement itemElement, boolean childrenLoaded) {
        return createChildNode(itemElement, childrenLoaded, true);
    }
    
    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createChildNode(ItemElement itemElement, boolean childrenLoaded, boolean setType) {
        T machine = createTreeNodeObject(itemElement, config, this, setType);
        if (childrenLoaded) {
            machine.childrenLoaded = childrenLoaded;
        }
        super.getChildren().add(machine);

        return machine;        
    }

        

    public <T extends ItemDomainMachineDesignBaseTreeNode> List<T> getMachineChildren() {
        return (List<T>) (List<?>) this.getChildren();
    }

    private void fetchChildren() {
        unloadRelationships();

        if (!childrenLoaded || loadRelationshipsForNode()) {
            TreeNode parent = getParent();
            if (config.isLoadAllChildren() || parent == null || parent.isExpanded()) {
                ItemElement element = getElement();
                if (element != null) {
                    Item containedItem = element.getContainedItem();
                    List<ItemElement> itemElementList;

                    if (containedItem == null) {
                        return;
                    }

                    ItemDomainMachineDesign idm = null;
                    if (containedItem instanceof ItemDomainMachineDesign) {
                        idm = (ItemDomainMachineDesign) containedItem;
                    }

                    if (!childrenLoaded) {
                        childrenLoaded = true;

                        if (isLoadElementChildren()) {
                            if (idm != null) {
                                itemElementList = idm.getCombinedItemElementList(element);
                            } else {
                                itemElementList = containedItem.getItemElementDisplayList();
                            }

                            for (ItemElement itemElement : itemElementList) {
                                createChildNode(itemElement);
                            }
                        }
                    }

                    loadRelationships();
                }
            }
        }
    }

    protected void loadRelationships() {
    }

    protected void unloadRelationships() {
    }

    // If toggle of relationship is avaialble, override this.
    protected boolean loadRelationshipsForNode() {
        return false;
    }

    protected void loadRelationshipsFromRelationshipList(boolean fetchParents, ItemElementRelationshipTypeNames relationshipTypeName, String customType) {
        ItemElement element = this.getElement();
        Item machineElement = element.getContainedItem();

        List<ItemDomainMachineDesign> results = null;
        ItemDomainMachineDesignFacade designFacade = config.getDesignFacade();
        if (fetchParents) {
            results = designFacade.fetchRelationshipParentItems(machineElement.getId(), relationshipTypeName.getDbId());
        } else {
            results = designFacade.fetchRelationshipChildrenItems(machineElement.getId(), relationshipTypeName.getDbId());
        }

        for (ItemDomainMachineDesign item : results) {
            ItemElement mockElement = createMockItemElement(item);

            ItemDomainMachineDesignBaseTreeNode node = null;
            node = createTreeNodeObject(mockElement, config, this, false);
            if (config.isSetMachineTreeNodeType()) {
                node.setType(customType);
            }
            super.getChildren().add(node);
        }
    }

    protected boolean isLoadElementChildren() {
        return true;
    }

    @Override
    public ItemDomainMachineDesignBaseTreeNode getParent() {
        return (ItemDomainMachineDesignBaseTreeNode) super.getParent();
    }

    private void setTreeNodeTypeMachineDesignTreeList() {
        ItemElement ie = this.getElement();
        Item item = ie.getContainedItem();
        if (item == null) {
            return;
        }
        ItemDomainMachineDesign mdItem = null;
        if (item instanceof ItemDomainMachineDesign) {
            mdItem = (ItemDomainMachineDesign) item;
        } else if (item instanceof ItemDomainCableDesign) {
            this.setType(ItemDomainName.cableDesign.getValue());
            return;
        }
        String domain = item.getDomain().getName();
        int itemDomainId = item.getDomain().getId();
        String defaultDomainAssignment = domain.replace(" ", "");
        if (isItemMachineDesignAndTemplate(item)) {
            defaultDomainAssignment += "Template";
        }

        ItemDomainMachineDesignBaseTreeNode parent = this.getParent();
        if (parent.getData() != null) {
            ItemElement parentIe = parent.getElement();
            Item parentItem = parentIe.getContainedItem();
            int parentDomainId = parentItem.getDomain().getId();

            if (parentDomainId == ItemDomainName.CATALOG_ID
                    || parentDomainId == ItemDomainName.INVENTORY_ID) {
                // Sub item of a catalog or an inventory 
                defaultDomainAssignment += "Member";
            } else if (parentDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                if (isItemMachineDesignAndTemplate(item)) {
                    if (isItemMachineDesignAndTemplate(parentItem)) {
                        // parent is template -- default name is correct
                        defaultDomainAssignment += "Member";
                    } else {
                        // parent is machine desing 
                        defaultDomainAssignment += "Placeholder";
                    }
                } else if (itemDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                    // machine design sub item of a machine design 
                    defaultDomainAssignment += "Member";
                } else if (itemDomainId == ItemDomainName.CATALOG_ID) {
                    // catalog sub item of a machine design 
                    if (isItemMachineDesignAndTemplate(parentItem)) {
                        // catalog sub item of a machine design template
                        defaultDomainAssignment += "TemplateMember";
                    }
                }
            }
        }
        if (mdItem != null) {
            // Verify if contains an item in secondary contained item
            Item assignedItem = mdItem.getAssignedItem();
            if (assignedItem != null) {
                String ci2Domain = assignedItem.getDomain().getName();
                ci2Domain = ci2Domain.replace(" ", "");
                defaultDomainAssignment += ci2Domain;
            }
        }
        this.setType(defaultDomainAssignment);
    }

    public boolean isItemMachineDesignAndTemplate(Item item) {
        if (item instanceof ItemDomainMachineDesign) {
            return ((ItemDomainMachineDesign) item).getIsItemTemplate();
        }

        return false;
    }

    public void clearFilterResults() {
        rawFilterResults = null;
        filterResults = null;
        getChildren().clear();
        // Prevent gui from changing the currently set filters. 
        if (topLevelItems != null) {
            for (ItemDomainMachineDesign item : topLevelItems) {
                boolean filterMachineNode = item.isFilterMachineNode();
                item.updateFilterMachineNode(filterMachineNode);
            }
            addTopLevelChildren(topLevelItems);
        }
    }

    public List<ItemDomainMachineDesign> getFilterResults() {
        if (filterResults != null) {
            return filterResults;
        } else {
            return new ArrayList<>();
        }
    }

    protected List<ItemDomainMachineDesign> fetchMachineItemsByNameFilter(String nameFilter) {
        Map filterMap = new HashMap();
        filterMap.put(ItemQueryBuilder.QueryTranslator.name.getValue(), nameFilter);

        ItemDomainMachineDesignQueryBuilder queryBuilder = new ItemDomainMachineDesignQueryBuilder(domain.getId(), filterMap);

        ItemDomainMachineDesignFacade designFacade = config.getDesignFacade();
        return designFacade.findByDataTableFilterQueryBuilder(queryBuilder);
    }

    public void filterChangeEvent(String onComplete) {
        if (rawFilterResults != null) {
            return;
        }

        if (nameFilter.isEmpty()) {
            clearFilterResults();
        } else {                        
            rawFilterResults = fetchMachineItemsByNameFilter(nameFilter); 

            SessionUtility.addInfoMessage("Hang tight, Loading hierarchy results", "Found " + rawFilterResults.size() + " Results.");
        }
        SessionUtility.executeRemoteCommand(onComplete);
    }

    public void finishFiltering() {
        if (rawFilterResults != null) {
            getChildren().clear();

            int relevantResults = 0;
            filterResults = new ArrayList<>();
            // Passed as array to force pass by reference. 
            Integer[] displayedNodes = new Integer[1];
            displayedNodes[0] = 0;
            for (ItemDomainMachineDesign item : rawFilterResults) {
                ItemDomainMachineDesignBaseTreeNode createTreeFromFilter = createTreeFromFilter(item, true, displayedNodes);
                if (createTreeFromFilter != null) {
                    relevantResults++;
                    filterResults.add(item);
                }
            }

            if (displayedNodes[0] > 600) {
                clearFilterResults();
                SessionUtility.addErrorMessage("Too many results", "Too many results to display. Please provide a more specific search criteria.");
            } else {
                SessionUtility.addInfoMessage("Done", "Showing " + relevantResults + " relevant results.");
            }
        }
    }

    protected ItemDomainMachineDesign getParentItem(ItemDomainMachineDesign item) {
        ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();
        return parentMachineDesign;
    }
    
    protected ItemElement getParentItemElement(ItemDomainMachineDesign item) {
        return item.getParentMachineElement();
    }
    
    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createSearchResultChildNode(ItemDomainMachineDesignBaseTreeNode parentNode, ItemElement ie, boolean childrenLoaded) {
        return (T) parentNode.createChildNode(ie, childrenLoaded);
    }

    private ItemDomainMachineDesignBaseTreeNode createTreeFromFilter(ItemDomainMachineDesign item, boolean searchResultNode, Integer[] displayedNodes) {
        if (item == null) {
            return null;
        }
        ItemDomainMachineDesign parentMachineDesign = getParentItem(item);
        ItemDomainMachineDesignBaseTreeNode parentNode = createTreeFromFilter(parentMachineDesign, false, displayedNodes);

        if (parentMachineDesign != null && parentNode == null) {
            // The top level item is not included in results
            return null;
        }

        ItemElement childElement = null;
        ItemDomainMachineDesignBaseTreeNode childNode = null;
        if (parentNode == null) {
            // Top level
            if (isFilterTopLevelItem(item)) {
                parentNode = this;
                childElement = createMockItemElement(item);
            } else {
                return null;
            }
        } else {
            childElement = getParentItemElement(item); 
        }

        List<ItemDomainMachineDesignBaseTreeNode> children = parentNode.getMachineChildren();
        for (ItemDomainMachineDesignBaseTreeNode node : children) {
            ItemElement element = node.getElement();
            
            if (element.equals(childElement)) {
                childNode = node;
                if (childNode.isExpanded() == false) {
                    displayedNodes[0]++;
                    childNode.setExpanded(true);
                }
                break;
            }
        }

        if (childNode == null) {
            displayedNodes[0]++;
            childNode = createSearchResultChildNode(parentNode, childElement, !searchResultNode);             
            if (searchResultNode == false) {
                childNode.setExpanded(true);
            }
        }

        return childNode;
    }

    private boolean isFilterTopLevelItem(ItemDomainMachineDesign item) {
        for (ItemDomainMachineDesign topItem : topLevelItems) {
            if (topItem.equals(item)) {
                return topItem.isFilterMachineNode();
            }
        }
        return false;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        if (this.nameFilter.equals(nameFilter) == false) {
            // Null filter results will trigger the search. 
            rawFilterResults = null;
        }
        this.nameFilter = nameFilter;
    }

    public Boolean getFilterAllNodes() {
        if (filterAllNodes == null) {
            filterAllNodes = true;
            if (topLevelItems != null) {
                for (ItemDomainMachineDesign item : topLevelItems) {
                    if (item.isFilterMachineNode() == false) {
                        filterAllNodes = false;
                        break;
                    }
                }
            }
        }
        return filterAllNodes;
    }

    public void setFilterAllNodes(Boolean filterAllNodes) {
        if (this.filterAllNodes != filterAllNodes) {
            for (ItemDomainMachineDesign item : topLevelItems) {
                item.updateFilterMachineNode(filterAllNodes);
            }
        }
        this.filterAllNodes = filterAllNodes;
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

    public class MachineTreeBaseConfiguration {

        private boolean setMachineTreeNodeType = true;
        private boolean loadAllChildren = false;
        private ItemDomainMachineDesignControllerUtility mdControllerUtility = null;
        private ItemDomainMachineDesignFacade designFacade;

        public MachineTreeBaseConfiguration() {
        }

        public ItemDomainMachineDesignFacade getDesignFacade() {
            return designFacade;
        }

        public void setDesignFacade(ItemDomainMachineDesignFacade designFacade) {
            this.designFacade = designFacade;
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

        public ItemDomainMachineDesignControllerUtility getMdControllerUtility() {
            if (mdControllerUtility == null) {
                mdControllerUtility = new ItemDomainMachineDesignControllerUtility();
            }
            return mdControllerUtility;
        }

    }

}
