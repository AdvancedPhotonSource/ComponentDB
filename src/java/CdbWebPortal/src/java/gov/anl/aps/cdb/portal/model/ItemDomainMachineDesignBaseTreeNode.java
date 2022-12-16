/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.settings.ItemDomainMachineDesignSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignBaseTreeNode.MachineTreeBaseConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemDomainMachineDesignQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.beans.builder.ItemQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 * @param <MachineNodeConfiguration>
 */
public abstract class ItemDomainMachineDesignBaseTreeNode<MachineNodeConfiguration extends MachineTreeBaseConfiguration> extends ItemBaseLazyTreeNode<ItemDomainMachineDesign, ItemDomainMachineDesignFacade, MachineNodeConfiguration> {

    private String nameFilter = "";

    private List<ItemDomainMachineDesign> rawFilterResults;
    private List<ItemDomainMachineDesign> filterResults;
    private Boolean filterAllNodes;

    protected ItemDomainMachineDesignBaseTreeNode(ItemElement element, MachineNodeConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setTypeForLevel) {
        super(element, config, parent);
        if (setTypeForLevel && config.isSetMachineTreeNodeType()) {
            setTreeNodeTypeMachineDesignTreeList();
        }
    }

    public ItemDomainMachineDesignBaseTreeNode(List<ItemDomainMachineDesign> items, Domain domain, ItemDomainMachineDesignFacade facade, ItemDomainMachineDesignSettings settings) {
        initFirstLevel(items, domain, facade);
        this.config.setSettings(settings);
    }

    public ItemDomainMachineDesignBaseTreeNode() {
        // Empty model
        config = createTreeNodeConfiguration();
    }

    public abstract MachineNodeConfiguration createTreeNodeConfiguration();

    protected abstract <T extends ItemDomainMachineDesignBaseTreeNode> T createTreeNodeObject(ItemElement element, MachineNodeConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setType);

    @Override
    protected <T extends ItemBaseLazyTreeNode> T createTreeNodeObject(ItemElement element, MachineNodeConfiguration config, ItemBaseLazyTreeNode parent) {
        ItemDomainMachineDesignBaseTreeNode nodeObject = createTreeNodeObject(element, config, this, true);
        return (T) nodeObject;
    }

    public List<ItemDomainMachineDesign> getTopLevelItems() {
        return topLevelItems;
    }

    @Override
    protected <T extends ItemBaseLazyTreeNode> T createChildNode(ItemElement itemElement) {
        return (T) createChildNode(itemElement, false);
    }

    @Override
    protected <T extends ItemBaseLazyTreeNode> T createChildNode(ItemElement itemElement, boolean childrenLoaded) {
        return (T) createChildNode(itemElement, childrenLoaded, true);
    }

    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createChildNode(ItemElement itemElement, boolean childrenLoaded, boolean setType) {
        return createChildNode(itemElement, childrenLoaded, setType, false);
    }

    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createChildNode(ItemElement itemElement, boolean childrenLoaded, boolean setType, boolean verifySort) {
        T machine = createTreeNodeObject(itemElement, config, this, setType);
        if (childrenLoaded) {
            machine.childrenLoaded = childrenLoaded;
        }
        if (!verifySort) {
            super.getChildren().add(machine);
        } else {
            List<TreeNode> currentChildren = super.getChildren();
            Float newSort = itemElement.getSortOrder();
            boolean added = false;

            if (newSort != null || currentChildren.size() == 0) {
                for (int i = 0; i < currentChildren.size(); i++) {
                    TreeNode child = currentChildren.get(i);
                    ItemElement data = (ItemElement) child.getData();
                    Float currentSort = data.getSortOrder();

                    if (currentSort == null || currentSort > newSort || currentSort.equals(newSort)) {
                        super.getChildren().add(i, machine);
                        added = true;
                        break;
                    }
                }
            }

            if (!added) {
                super.getChildren().add(machine);
            }
        }

        return machine;
    }

    protected void loadRelationshipsFromRelationshipList(boolean fetchParents, ItemElementRelationshipTypeNames relationshipTypeName, String customType) {
        ItemElement element = this.getElement();
        Item machineElement = element.getContainedItem();
        
        ItemDomainMachineDesignBaseTreeNode parent = this.getParent();
        Integer parentItemId = null; 
        if (parent != null) {
            ItemElement parentElement = parent.getElement();
            if (parentElement != null) {
                Item containedItem = parentElement.getContainedItem();
                if (containedItem != null) {
                    parentItemId = containedItem.getId(); 
                }
            }
        }

        List<ItemDomainMachineDesign> results = null;
        ItemDomainMachineDesignFacade designFacade = config.getFacade();
        if (fetchParents) {
            results = designFacade.fetchRelationshipParentItems(machineElement.getId(), relationshipTypeName.getDbId());
        } else {
            results = designFacade.fetchRelationshipChildrenItems(machineElement.getId(), relationshipTypeName.getDbId(), parentItemId);
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

    @Override
    public ItemDomainMachineDesignBaseTreeNode getParent() {
        return (ItemDomainMachineDesignBaseTreeNode) super.getParent();
    }

    private void setTreeNodeTypeMachineDesignTreeList() {
        ItemElement ie = this.getElement();
        Item item = ie.getContainedItem();
        if (item == null) {
            // Check if element is blank inventory element
            Item parentItem = ie.getParentItem();
            if (parentItem instanceof ItemDomainInventory) {
                this.setType("Inventory");
            }

            return;
        }
        ItemDomainMachineDesign mdItem = null;
        if (item instanceof ItemDomainMachineDesign) {
            mdItem = (ItemDomainMachineDesign) item;
        } else if (item instanceof ItemDomainCableDesign) {
            this.setType("CableDesign");
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
                        
                        ItemElement representsCatalogElement = ((ItemDomainMachineDesign) item).getRepresentsCatalogElement();
                        if (representsCatalogElement != null) {
                            defaultDomainAssignment += "Assembly";
                        }                        
                    } else {
                        // parent is machine design 
                        defaultDomainAssignment += "Placeholder";
                    }
                } else if (itemDomainId == ItemDomainName.MACHINE_DESIGN_ID) {
                    // machine design sub item of a machine design 
                    defaultDomainAssignment += "Member";

                    ItemElement representsCatalogElement = ((ItemDomainMachineDesign) item).getRepresentsCatalogElement();
                    if (representsCatalogElement != null) {
                        defaultDomainAssignment += "Assembly";
                    }
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

        ItemDomainMachineDesignFacade designFacade = config.getFacade();
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

            SessionUtility.addInfoMessage("Hang tight, Loading hierarchy results", "Found " + rawFilterResults.size() + " Results.", true);
        }
        SessionUtility.executeRemoteCommand(onComplete);
    }

    public void finishFiltering() {
        if (rawFilterResults != null) {
            getChildren().clear();

            AtomicInteger relevantResults = new AtomicInteger(0);
            filterResults = new ArrayList<>();
            // Passed as array to force pass by reference. 
            Integer[] displayedNodes = new Integer[1];
            displayedNodes[0] = 0;
            for (ItemDomainMachineDesign item : rawFilterResults) {
                Set<ItemDomainMachineDesign> additionalParents = new HashSet<>();
                List<ItemDomainMachineDesign> processParents = new ArrayList<>();

                ItemDomainMachineDesignBaseTreeNode createTreeFromFilter = createTreeFromFilter(item, true, displayedNodes, additionalParents, null);
                processAdditionalFilterParentResult(item, additionalParents, processParents, relevantResults, displayedNodes);

                if (createTreeFromFilter != null) {
                    relevantResults.incrementAndGet();
                    filterResults.add(item);
                }
            }

            Integer maxSearchNodes = config.getMaxSearchNodes();
            if (displayedNodes[0] > maxSearchNodes) {
                clearFilterResults();
                SessionUtility.addErrorMessage("Too many results rows (" + displayedNodes[0] + ")" , "Too many results to display. Please provide a more specific search criteria.", true);
            } else {
                SessionUtility.addInfoMessage("Done", "Showing " + relevantResults + " relevant results.", true);
            }
        }
    }

    private void processAdditionalFilterParentResult(ItemDomainMachineDesign item, Set<ItemDomainMachineDesign> additionalParents, List<ItemDomainMachineDesign> processParents, AtomicInteger relevantResults, Integer[] displayedNodes) {
        for (ItemDomainMachineDesign additionalParent : additionalParents) {
            relevantResults.incrementAndGet();
            Set<ItemDomainMachineDesign> additionalParentForParent = new HashSet<>();
            processParents.add(additionalParent);
            createTreeFromFilter(item, true, displayedNodes, additionalParentForParent, processParents);
            if (additionalParentForParent.size() > 0) {
                List<ItemDomainMachineDesign> additionalParentProcessParents = new ArrayList<>();
                additionalParentProcessParents.addAll(processParents);
                processAdditionalFilterParentResult(item, additionalParentForParent, additionalParentProcessParents, relevantResults, displayedNodes);
            }
            processParents.remove(additionalParent);
        }
    }

    protected List<ItemDomainMachineDesign> getParentItems(ItemDomainMachineDesign item) {
        ItemDomainMachineDesign parentMachineDesign = item.getParentMachineDesign();
        return Arrays.asList(parentMachineDesign);
    }

    protected ItemElement getParentItemElement(ItemDomainMachineDesign item) {
        return item.getParentMachineElement();
    }

    protected <T extends ItemDomainMachineDesignBaseTreeNode> T createSearchResultChildNode(ItemDomainMachineDesignBaseTreeNode parentNode, ItemElement ie, boolean childrenLoaded) {
        return (T) parentNode.createChildNode(ie, childrenLoaded, true, true);
    }

    private ItemDomainMachineDesignBaseTreeNode createTreeFromFilter(ItemDomainMachineDesign item, boolean searchResultNode, Integer[] displayedNodes,
            Set<ItemDomainMachineDesign> additionalParents, List<ItemDomainMachineDesign> processWithParent) {
        if (item == null) {
            return null;
        }
        List<ItemDomainMachineDesign> parentMachineDesigns = getParentItems(item);
        ItemDomainMachineDesign parentMachineDesign = null;

        if (parentMachineDesigns.size() > 0) {
            parentMachineDesign = parentMachineDesigns.get(0);
            if (parentMachineDesigns.size() > 1) {
                for (ItemDomainMachineDesign potentialParent : parentMachineDesigns) {
                    if (potentialParent.equals(parentMachineDesign)) {
                        // Skip the first node. Already processed or will be process on this run. 
                        continue;
                    }

                    if (processWithParent != null) {
                        if (processWithParent.contains(potentialParent)) {
                            parentMachineDesign = potentialParent;
                            break;
                        }
                    }

                    additionalParents.add(potentialParent);
                }
            }
        }

        ItemDomainMachineDesignBaseTreeNode parentNode = createTreeFromFilter(parentMachineDesign, false, displayedNodes, additionalParents, processWithParent);

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

        List<ItemDomainMachineDesignBaseTreeNode> children = parentNode.getTreeNodeItemChildren();
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

    public class MachineTreeBaseConfiguration extends ItemTreeBaseConfiguration {

        private boolean setMachineTreeNodeType = true;
        private boolean loadAllChildren = false;
        private ItemDomainMachineDesignControllerUtility mdControllerUtility = null;
        private ItemDomainMachineDesignSettings settings = null;

        public MachineTreeBaseConfiguration() {
        }

        @Override
        public ItemDomainMachineDesignFacade getFacade() {
            return (ItemDomainMachineDesignFacade) super.getFacade();
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

        public ItemDomainMachineDesignSettings getSettings() {
            return settings;
        }

        public void setSettings(ItemDomainMachineDesignSettings settings) {
            this.settings = settings;
        }

        public Integer getMaxSearchNodes() {
            if (settings != null) {
                Integer displayMaximumNumberOfSearchResults = settings.getDisplayMaximumNumberOfSearchResults();
                if (displayMaximumNumberOfSearchResults != null) {
                    return displayMaximumNumberOfSearchResults; 
                }
            }
            return 600;
        }

    }

}
