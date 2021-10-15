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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        
        // add entries for each cable relationship (connection to MD item)
        List<MachineDesignConnectorListObject> connections = new ArrayList<>();
        List<ItemElementRelationship> cableRelationshipList;
        cableRelationshipList = ItemUtility.getItemRelationshipList(
                item, ItemElementRelationshipTypeNames.itemCableConnection.getValue(), true);
        for (ItemElementRelationship cableRelationship : cableRelationshipList) {
            MachineDesignConnectorListObject mdclo = new MachineDesignConnectorListObject();
            mdclo.setCableRelationship(cableRelationship, item);
            connections.add(mdclo);
        }
        Comparator<MachineDesignConnectorListObject> connectionComparator
                = Comparator
                        .comparing((MachineDesignConnectorListObject o) 
                                -> o.getConnectorName())
                        .thenComparing(o -> o.getConnectedToItemsString())  
                        .thenComparing(o -> o.getCableName());    
        connections = connections.stream()
                .sorted(connectionComparator)
                .collect(Collectors.toList());
        
        // add entries for each synced connector, only includes connectors that are not already in use
        List<MachineDesignConnectorListObject> connectors = new ArrayList<>();
        List<ItemConnector> syncedConnectors = item.getSyncedConnectorList();
        if (syncedConnectors != null) {
            for (ItemConnector itemConnector : syncedConnectors) {
                MachineDesignConnectorListObject mdclo = new MachineDesignConnectorListObject();
                mdclo.setItemConnector(itemConnector);
                connectors.add(mdclo);
            }
        }
        Comparator<MachineDesignConnectorListObject> connectorComparator
                = Comparator
                        .comparing((MachineDesignConnectorListObject o) 
                                -> o.getConnectorName());    
        connectors = connectors.stream()
                .sorted(connectorComparator)
                .collect(Collectors.toList());

        List<MachineDesignConnectorListObject> mdConnectorList = new ArrayList<>();
        mdConnectorList.addAll(connections);
        mdConnectorList.addAll(connectors);
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
    
    public String getConnectorName() {
        if ((itemConnector != null) && (itemConnector.getConnector() != null)) {
            return itemConnector.getConnector().getName();
        } else {
            return "";
        }
    }

    public ItemDomainCableDesign getCableItem() {
        return cableItem;
    }
    
    public String getCableName() {
        if (cableItem != null) {
            return cableItem.getName();
        } else {
            return "";
        }
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
