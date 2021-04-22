/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMachineDesignQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMachineDesignTreeNode extends DefaultTreeNode {

    private String nameFilter = "";
    private Domain domain;
    private ItemDomainMachineDesignFacade designFacade;

    private List<ItemDomainMachineDesign> topLevelItems;

    private List<ItemDomainMachineDesign> filterResults;
    private Boolean filterAllNodes;

    boolean childrenLoaded = false;
    boolean cablesLoaded = false;
    MachineTreeConfiguration config;

    boolean cableRelatedNode = false;

    private ItemDomainMachineDesignTreeNode(ItemElement element, MachineTreeConfiguration config, ItemDomainMachineDesignTreeNode parent) {
        super(element);
        this.config = config;
        setParent(parent);
        setTreeNodeTypeMachineDesignTreeList();
    }

    public ItemDomainMachineDesignTreeNode(List<ItemDomainMachineDesign> items, Domain domain, ItemDomainMachineDesignFacade facade) {
        config = new MachineTreeConfiguration();
        this.domain = domain;
        this.designFacade = facade;
        this.topLevelItems = items;

        addTopLevelChildren(items);       

        this.setExpanded(true);
        childrenLoaded = true;
    }

    private void addTopLevelChildren(List<ItemDomainMachineDesign> topNodes) {
        for (ItemDomainMachineDesign item : topNodes) {
            ItemElement element = createTopLevelMockItemElement(item);
            createChildNode(element);
        }
        
        // Expand first node if tree is only one node.
        if (topNodes.size() == 1) {
            List<ItemDomainMachineDesignTreeNode> machineChildren = this.getMachineChildren();
            machineChildren.get(0).setExpanded(true);
        }
    }

    private ItemElement createTopLevelMockItemElement(ItemDomainMachineDesign item) {
        ItemElement selfElement = item.getSelfElement();
        ItemElement element = new ItemElement();
        Float sortOrder = selfElement.getSortOrder();
        element.setContainedItem(item);
        element.setSortOrder(sortOrder);
        return element;
    }

    public ItemDomainMachineDesignTreeNode() {
        // Empty model
        config = new MachineTreeConfiguration();
    }

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

    public MachineTreeConfiguration getConfig() {
        return config;
    }

    private ItemDomainMachineDesignTreeNode createChildNode(ItemElement itemElement) {
        return createChildNode(itemElement, false);
    }

    private ItemDomainMachineDesignTreeNode createChildNode(ItemElement itemElement, boolean childrenLoaded) {
        ItemDomainMachineDesignTreeNode machine = new ItemDomainMachineDesignTreeNode(itemElement, config, this);
        if (childrenLoaded) {
            machine.childrenLoaded = childrenLoaded;
        }
        super.getChildren().add(machine);

        return machine;
    }

    private ItemDomainMachineDesignTreeNode createChildNode(ItemConnector itemConnector) {
        ItemElement mockIE = new ItemElement();
        mockIE.setMdConnector(itemConnector);
        ItemDomainMachineDesignTreeNode connectorNode = this.createChildNode(mockIE);
        connectorNode.setType("Connector");
        connectorNode.cableRelatedNode = true;

        return connectorNode;
    }

    private ItemDomainMachineDesignTreeNode createChildNode(ItemDomainCableDesign cableDesign) {
        ItemElement mockIE = new ItemElement();
        mockIE.setContainedItem(cableDesign);
        ItemDomainMachineDesignTreeNode cable = createChildNode(mockIE);
        cable.cableRelatedNode = true;

        return cable;
    }

    public List<ItemDomainMachineDesignTreeNode> getMachineChildren() {
        return (List<ItemDomainMachineDesignTreeNode>) (List<?>) super.getChildren();
    }

    private void fetchChildren() {
        boolean loadCables = !cablesLoaded && (config.cablesNeedLoading());
        boolean unloadCables = cablesLoaded && (!config.cablesNeedLoading());

        if (unloadCables) {
            TreeNode parent = getParent();
            if (parent == null || parent.isExpanded()) {
                cablesLoaded = false;
                List<ItemDomainMachineDesignTreeNode> machineChildren = getMachineChildren();
                for (int i = machineChildren.size() - 1; i >= 0; i--) {
                    ItemDomainMachineDesignTreeNode node = machineChildren.get(i);
                    if (node.cableRelatedNode) {
                        machineChildren.remove(i);
                    }
                }
            }
        }

        if (!childrenLoaded || loadCables) {
            TreeNode parent = getParent();
            if (config.loadAllChildren || parent == null || parent.isExpanded()) {
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
                        itemElementList = idm.getCombinedItemElementList(element);
                    } else {
                        itemElementList = containedItem.getItemElementDisplayList();
                    }
                    if (!childrenLoaded) {
                        childrenLoaded = true;
                        for (ItemElement itemElement : itemElementList) {
                            createChildNode(itemElement);
                        }
                    }
                    if (loadCables && !cablesLoaded) {
                        cablesLoaded = true;
                        if (idm != null) {
                            List<MachineDesignConnectorListObject> connList = MachineDesignConnectorListObject.createMachineDesignConnectorList(idm);

                            for (MachineDesignConnectorListObject connObj : connList) {
                                ItemDomainMachineDesignTreeNode connectorNode = null;
                                if (connObj.getItemConnector() != null) {
                                    connectorNode = createChildNode(connObj.getItemConnector());
                                }

                                if (getConfig().showCables) {
                                    ItemDomainCableDesign cableItem = connObj.getCableItem();
                                    if (cableItem != null) {
                                        if (connectorNode != null) {
                                            connectorNode.createChildNode(cableItem);
                                        } else {
                                            createChildNode(cableItem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ItemDomainMachineDesignTreeNode getParent() {
        return (ItemDomainMachineDesignTreeNode) super.getParent();
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

        ItemDomainMachineDesignTreeNode parent = this.getParent();
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
        filterResults = null;
        getChildren().clear();
        // Prevent gui from changing the currently set filters. 
        for (ItemDomainMachineDesign item : topLevelItems) {
            boolean filterMachineNode = item.isFilterMachineNode();
            item.updateFilterMachineNode(filterMachineNode);
        }
        addTopLevelChildren(topLevelItems);
    }

    public void filterChangeEvent(String onComplete) {
        if (filterResults != null) {
            return;
        }
        
        Map filterMap = new HashMap();

        if (nameFilter.isEmpty()) {
            clearFilterResults();
        } else {
            filterMap.put(ItemQueryBuilder.QueryTranslator.name.getValue(), nameFilter);

            ItemDomainMachineDesignQueryBuilder queryBuilder = new ItemDomainMachineDesignQueryBuilder(domain, filterMap);

            filterResults = designFacade.findByDataTableFilterQueryBuilder(queryBuilder);

            SessionUtility.addInfoMessage("Hang tight, Loading hierarchy results", "Found " + filterResults.size() + " Results.");
        }
        SessionUtility.executeRemoteCommand(onComplete);
    }

    public void finishFiltering() {
        if (filterResults != null) {
            getChildren().clear();

            int relevantResults = 0;
            // Passed as array to force pass by reference. 
            Integer[] displayedNodes = new Integer[1];
            displayedNodes[0] = 0;
            for (ItemDomainMachineDesign item : filterResults) {
                ItemDomainMachineDesignTreeNode createTreeFromFilter = createTreeFromFilter(item, true, displayedNodes);
                if (createTreeFromFilter != null) {
                    relevantResults++;
                }
            }

            if (displayedNodes[0] > 400) {
                clearFilterResults();
                SessionUtility.addErrorMessage("Too many results", "Too many results to display. Please provide a more specific search criteria.");
            } else {
                SessionUtility.addInfoMessage("Done", "Showing " + relevantResults + " relevant results.");
            }
        }
    }

    private ItemDomainMachineDesignTreeNode createTreeFromFilter(ItemDomainMachineDesign item, boolean searchResultNode, Integer[] displayedNodes) {
        if (item == null) {
            return null;
        }
        ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();
        ItemDomainMachineDesignTreeNode parentNode = createTreeFromFilter(parentMachineDesign, false, displayedNodes);

        if (parentMachineDesign != null && parentNode == null) {
            // The top level item is not included in results
            return null;
        }

        ItemElement childElement = null;
        ItemDomainMachineDesignTreeNode childNode = null;
        if (parentNode == null) {
            if (itemIncludedInSearch(item)) {
                parentNode = this;
                childElement = createTopLevelMockItemElement(item);
            } else {
                return null;
            }
        } else {
            childElement = item.getParentMachineElement();
        }

        List<ItemDomainMachineDesignTreeNode> children = parentNode.getMachineChildren();
        for (ItemDomainMachineDesignTreeNode node : children) {
            ItemElement element = node.getElement();

            if (element.equals(childElement)) {
                childNode = node;
                break;
            }
        }

        if (childNode == null) {
            displayedNodes[0]++;
            childNode = parentNode.createChildNode(childElement, !searchResultNode);
            if (searchResultNode == false) {
                childNode.setExpanded(true);
            }
        }

        return childNode;
    }

    private boolean itemIncludedInSearch(ItemDomainMachineDesign item) {
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
            filterResults = null; 
        }
        this.nameFilter = nameFilter;
    }

    public Boolean getFilterAllNodes() {
        if (filterAllNodes == null) {
            filterAllNodes = true;
            for (ItemDomainMachineDesign item : topLevelItems) {
                if (item.isFilterMachineNode() == false) {
                    filterAllNodes = false;
                    break;
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

    public class MachineTreeConfiguration {

        private boolean loadAllChildren = false;
        private boolean showCables = false;
        private boolean showConnectorsOnly = false;

        public MachineTreeConfiguration() {
        }

        public boolean isLoadAllChildren() {
            return loadAllChildren;
        }

        public void setLoadAllChildren(boolean loadAllChildren) {
            this.loadAllChildren = loadAllChildren;
        }

        public boolean isShowCables() {
            return showCables;
        }

        public void setShowCables(boolean showCables) {
            this.showCables = showCables;
        }

        public boolean isShowConnectorsOnly() {
            return showConnectorsOnly;
        }

        public void setShowConnectorsOnly(boolean showConnectorsOnly) {
            this.showConnectorsOnly = showConnectorsOnly;
        }

        private boolean cablesNeedLoading() {
            return showConnectorsOnly || showCables;
        }
    }

}
