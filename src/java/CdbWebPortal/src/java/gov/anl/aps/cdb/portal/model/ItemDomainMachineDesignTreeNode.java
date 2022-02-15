/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode.MachineTreeConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMachineDesignTreeNode extends ItemDomainMachineDesignBaseTreeNode<MachineTreeConfiguration> {

    boolean assemblyElementsLoaded = false;
    boolean cablesLoaded = false;
    boolean cableRelatedNode = false;

    public ItemDomainMachineDesignTreeNode(ItemElement element, MachineTreeConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setTypeForLevel) {
        super(element, config, parent, setTypeForLevel);
    }

    public ItemDomainMachineDesignTreeNode(List<ItemDomainMachineDesign> items, Domain domain, ItemDomainMachineDesignFacade facade) {
        super(items, domain, facade);
    }

    public ItemDomainMachineDesignTreeNode() {
    }

    @Override
    protected ItemDomainMachineDesignTreeNode createTreeNodeObject(ItemElement element, MachineTreeConfiguration config, ItemDomainMachineDesignBaseTreeNode parent, boolean setType) {
        return new ItemDomainMachineDesignTreeNode(element, config, parent, setType);
    }

    @Override
    protected ItemDomainMachineDesignTreeNode createTreeNodeObject(ItemElement itemElement) {
        return new ItemDomainMachineDesignTreeNode(itemElement, config, this, true);
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

    @Override
    protected void loadAdditionalChildren() {
        super.loadAdditionalChildren();

        loadAssemblyChildren();
        loadCableChildren();
    }

    private void loadAssemblyChildren() {
        boolean loadAssembly = loadAssemblyNodes();

        if (loadAssembly) {
            assemblyElementsLoaded = true;
            ItemElement element = getElement();

            Item containedItem = element.getContainedItem();

            if (containedItem == null) {
                return;
            }

            if (!(containedItem instanceof ItemDomainMachineDesign)) {
                return;
            }

            ItemDomainMachineDesign idm = (ItemDomainMachineDesign) containedItem;
            List<ItemElement> itemElementList = idm.getCombinedItemElementList(element);

            List<Integer> skipElementIds = new ArrayList<>();
            for (ItemElement itemElement : itemElementList) {
                if (idm != null) {
                    Integer id = itemElement.getId();
                    if (skipElementIds.contains(id)) {
                        continue;
                    }
                    Item containedItem1 = itemElement.getContainedItem();
                    if (containedItem1 instanceof ItemDomainMachineDesign) {
                        ItemElement asnElement = ((ItemDomainMachineDesign) containedItem1).getAssignedRepresentedElement();
                        if (asnElement != null) {
                            skipElementIds.add(asnElement.getId());
                            ItemElement derivedFromItemElement = asnElement.getDerivedFromItemElement();
                            if (derivedFromItemElement != null) {
                                skipElementIds.add(derivedFromItemElement.getId());
                            }
                        }
                        continue;
                    } else if (containedItem1 instanceof ItemDomainCableCatalog) {
                        // Do not show cable assembly elements in machine
                        continue;
                    } else if (containedItem1 instanceof ItemDomainCableInventory) {
                        // Do not show cable assembly elements in machine
                        continue;
                    }
                }
                createChildNode(itemElement);
            }
        }
    }

    private void loadCableChildren() {
        boolean loadCables = loadCables();

        ItemElement element = getElement();
        Item containedItem = element.getContainedItem();
        ItemDomainMachineDesign idm = null;
        if (containedItem instanceof ItemDomainMachineDesign) {
            idm = (ItemDomainMachineDesign) containedItem;
        }

        if (loadCables && !cablesLoaded) {
            cablesLoaded = true;
            if (idm != null) {

                // build list of connectors for this md item (syncs available connectors from catalog item)
                List<MachineDesignConnectorListObject> connList
                        = MachineDesignConnectorListObject.createMachineDesignConnectorList(idm);

                for (MachineDesignConnectorListObject connObj : connList) {
                    ItemDomainMachineDesignTreeNode connectorNode = null;
                    if (connObj.getItemConnector() != null) {
                        connectorNode = createChildNode(connObj.getItemConnector());
                    }

                    if (getConfig().isShowCables()) {
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

    @Override
    protected void unloadAdditionalChildren() {
        super.unloadAdditionalChildren();

        unloadAssemblyChildren();
        unloadCableChildren();
    }

    private void unloadAssemblyChildren() {
        boolean unloadAssemblyChildren = assemblyElementsLoaded && (!config.isShowAssemblyElements());

        if (unloadAssemblyChildren) {
            TreeNode parent = getParent();
            if (parent == null || parent.isExpanded()) {
                assemblyElementsLoaded = false;
                List<ItemDomainMachineDesignTreeNode> machineChildren = getTreeNodeItemChildren();
                
                for (int i = machineChildren.size() - 1; i >= 0; i--) {
                    ItemDomainMachineDesignTreeNode node = machineChildren.get(i);
                    ItemElement element = node.getElement();
                    Item containedItem = element.getContainedItem();
                    if (containedItem instanceof ItemDomainCatalog ||
                            containedItem instanceof ItemDomainInventory) {
                        machineChildren.remove(i);
                    }                                             
                }
            }
        }
    }

    private void unloadCableChildren() {
        boolean unloadCables = cablesLoaded && (!config.cablesNeedLoading());

        if (unloadCables) {
            TreeNode parent = getParent();
            if (parent == null || parent.isExpanded()) {
                cablesLoaded = false;
                List<ItemDomainMachineDesignTreeNode> machineChildren = getTreeNodeItemChildren();
                for (int i = machineChildren.size() - 1; i >= 0; i--) {
                    ItemDomainMachineDesignTreeNode node = machineChildren.get(i);
                    if (node.cableRelatedNode) {
                        machineChildren.remove(i);
                    }
                }
            }
        }
    }

    private boolean loadCables() {
        return !cablesLoaded && (config.cablesNeedLoading());
    }
    
    private boolean loadAssemblyNodes() {
        return !assemblyElementsLoaded && (config.isShowAssemblyElements());
    }

    @Override
    protected boolean loadAdditionalNodes() {
        return loadCables() || loadAssemblyNodes();
    }

    @Override
    public MachineTreeConfiguration createTreeNodeConfiguration() {
        return new MachineTreeConfiguration();
    }

    public class MachineTreeConfiguration extends MachineTreeBaseConfiguration {

        public MachineTreeConfiguration() {
            super();
        }

        private boolean showAssemblyElements = false;
        private boolean showCables = false;
        private boolean showConnectorsOnly = false;

        public boolean isShowAssemblyElements() {
            return showAssemblyElements;
        }

        public void setShowAssemblyElements(boolean showAssemblyElements) {
            this.showAssemblyElements = showAssemblyElements;
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
