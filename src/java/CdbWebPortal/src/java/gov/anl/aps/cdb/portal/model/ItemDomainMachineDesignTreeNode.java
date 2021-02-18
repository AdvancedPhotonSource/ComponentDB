/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import java.util.List;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMachineDesignTreeNode extends DefaultTreeNode {

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

    public ItemDomainMachineDesignTreeNode(List<ItemDomainMachineDesign> items) {
        config = new MachineTreeConfiguration();

        for (ItemDomainMachineDesign item : items) {            
            ItemElement selfElement = item.getSelfElement();
            ItemElement element = new ItemElement();
            Float sortOrder = selfElement.getSortOrder();
            element.setContainedItem(item);
            element.setSortOrder(sortOrder);
            createChildNode(element);
        }
        
        // Expand first node if tree is only one node.
        if (items.size() == 1) {
            List<ItemDomainMachineDesignTreeNode> machineChildren = this.getMachineChildren();
            machineChildren.get(0).setExpanded(true);
        }

        this.setExpanded(true);
        childrenLoaded = true;
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
        ItemDomainMachineDesignTreeNode machine = new ItemDomainMachineDesignTreeNode(itemElement, config, this);
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
    
    private List<ItemDomainMachineDesignTreeNode> getMachineChildren() {
        return (List<ItemDomainMachineDesignTreeNode>)(List<?>)super.getChildren();
    }

    private void fetchChildren() {
        boolean loadCables = !cablesLoaded && (config.cablesNeedLoading());
        boolean unloadCables = cablesLoaded && (!config.cablesNeedLoading()); 
        
        if (unloadCables) { 
            TreeNode parent = getParent();
            if (parent == null || parent.isExpanded()) {
                cablesLoaded = false; 
                List<ItemDomainMachineDesignTreeNode> machineChildren = getMachineChildren();
                for (int i = machineChildren.size() -1; i >= 0; i--) {
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
