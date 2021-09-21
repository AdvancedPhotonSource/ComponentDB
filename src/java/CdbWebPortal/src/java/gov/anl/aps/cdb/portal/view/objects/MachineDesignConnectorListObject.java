/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author djarosz
 */
public class MachineDesignConnectorListObject {

    private ItemConnector itemConnector;
    private ItemDomainCableDesign cableItem;
    private List<ItemDomainMachineDesign> connectedToItemsList = new ArrayList<>();
    private String connectedToItemsString;
    private ItemElementRelationship cableRelationship;

    public MachineDesignConnectorListObject() {
    }

    public static List<MachineDesignConnectorListObject> createMachineDesignConnectorList(ItemDomainMachineDesign item) {
        List<MachineDesignConnectorListObject> mdConnectorList = new ArrayList<>();

        List<ItemConnector> connectors = item.getItemConnectorList();

        //TODO filter out any other relationships
        List<ItemElementRelationship> cableRelationshipList;
        cableRelationshipList = ItemUtility.getItemRelationshipList(item, ItemElementRelationshipTypeNames.itemCableConnection.getValue(), true);

        for (ItemElementRelationship cableRelationship : cableRelationshipList) {
            MachineDesignConnectorListObject mdclo = new MachineDesignConnectorListObject();
            mdclo.setCableRelationship(cableRelationship, item);

            mdConnectorList.add(mdclo);
        }

        if (connectors != null) {
            connectorLoop:
            for (ItemConnector itemConnector : connectors) {
                for (MachineDesignConnectorListObject mdConn : mdConnectorList) {
                    ItemConnector mdItemConn = mdConn.itemConnector;
                    if (ObjectUtility.equals(mdItemConn, itemConnector)) {
                        // Item already connected
                        continue connectorLoop;
                    }
                }

                MachineDesignConnectorListObject mdclo = new MachineDesignConnectorListObject();
                mdclo.setItemConnector(itemConnector);
                mdConnectorList.add(mdclo);
            }
        }

        return mdConnectorList;
    }

    private void setItemConnector(ItemConnector itemConnector) {
        this.itemConnector = itemConnector;
    }

    private void setCableRelationship(ItemElementRelationship cablRelationship, ItemDomainMachineDesign currentItem) {
        this.cableRelationship = cablRelationship;
        ItemConnector firstItemConnector = cablRelationship.getFirstItemConnector();
        this.itemConnector = firstItemConnector;
        String itemNames = "";

        ItemElement cableElement = cablRelationship.getSecondItemElement();
        if (cableElement != null) {
            ItemDomainCableDesign parentItem = (ItemDomainCableDesign) cableElement.getParentItem();
            this.cableItem = parentItem;

            // create list of endpoint items and string of item names for use in connections table
            List<Item> endpointList = this.cableItem.getEndpointList();
            boolean first = true;
            for (Item endpoint : endpointList) {
                if (endpoint != null) {
                    if (endpoint.equals(currentItem)) {
                        continue;
                    } else {
                        if (first) {
                            first = false;
                        } else {
                            itemNames = itemNames + " | ";
                        }
                        this.connectedToItemsList.add((ItemDomainMachineDesign) endpoint);
                        itemNames = itemNames + endpoint.getName();
                    }
                }
            }
        }
        this.connectedToItemsString = itemNames;
    }

    public ItemConnector getItemConnector() {
        return itemConnector;
    }

    public ItemDomainCableDesign getCableItem() {
        return cableItem;
    }
    
    public List<ItemDomainMachineDesign> getConnectedToItemsList() {
        return connectedToItemsList;
    }

    public String getConnectedToItemsString() {
        return connectedToItemsString;
    }

    public ItemElementRelationship getCableRelationship() {
        return cableRelationship;
    }

}
