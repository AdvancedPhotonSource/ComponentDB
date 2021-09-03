/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.ItemDomainMachineDesignTreeNode.MachineTreeConfiguration;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainMachineDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Domain;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.view.objects.MachineDesignConnectorListObject;
import java.util.List;
import org.primefaces.model.TreeNode;

/**
 *
 * @author Dariusz
 */
public class ItemDomainMachineDesignTreeNode extends ItemDomainMachineDesignBaseTreeNode<MachineTreeConfiguration> {

    boolean cablesLoaded = false;
    boolean cableRelatedNode = false;

    boolean runningNodesLoaded = false;

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
    protected void loadRelationships() {
        super.loadRelationships();

        if (!runningNodesLoaded) {
            String relationshipTypeName = ItemElementRelationshipTypeNames.running.getValue();
            loadRelationshipsFromRelationshipList(true, relationshipTypeName, "machineRunninOnNode");
            runningNodesLoaded = true; 
        }

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

                // sync connectors and build list of connectors for this md item
                getConfig().getMdControllerUtility().syncMachineDesignConnectors(idm);
                List<MachineDesignConnectorListObject> connList = MachineDesignConnectorListObject.createMachineDesignConnectorList(idm);

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
    protected void unloadRelationships() {
        super.unloadRelationships();
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

    }

    private boolean loadCables() {
        return !cablesLoaded && (config.cablesNeedLoading());
    }

    @Override
    protected boolean loadRelationshipsForNode() {
        return loadCables();
    }

    @Override
    public MachineTreeConfiguration createTreeNodeConfiguration() {
        return new MachineTreeConfiguration();
    }

    public class MachineTreeConfiguration extends MachineTreeBaseConfiguration {

        public MachineTreeConfiguration() {
            super();
        }

        private boolean showCables = false;
        private boolean showConnectorsOnly = false;

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
