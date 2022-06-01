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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author djarosz
 */
public class MachineDesignConnectorListObject {

    private ItemConnector itemConnector;
    private List<ItemDomainCableDesign> connectedCables = new ArrayList<>();
    private Set<Item> connectedItems = new HashSet<>();
    private String connectedToItemsString;
    private ItemElementRelationship cableRelationship;

    public MachineDesignConnectorListObject() {
    }

    public static List<MachineDesignConnectorListObject> createMachineDesignConnectorList(ItemDomainMachineDesign item) {
        
        // add entries for each cable relationship (connection to MD item)
        List<MachineDesignConnectorListObject> connections = new ArrayList<>();
        List<ItemElementRelationship> cableRelationshipList;
        Map<String, MachineDesignConnectorListObject> connectorMap = new HashMap<>();
        cableRelationshipList = ItemUtility.getItemRelationshipList(
                item, ItemElementRelationshipTypeNames.itemCableConnection.getValue(), true);
        
        for (ItemElementRelationship cableRelationship : cableRelationshipList) {

            MachineDesignConnectorListObject mdclo = null;
            ItemConnector portConnector = cableRelationship.getFirstItemConnector();
            if (portConnector != null) {
                String connectorName = portConnector.getConnectorName();

                // add entry for port's ItemConnector if not already in map (from a different cable connection to same port)
                if (!connectorMap.containsKey(connectorName)) {
                    mdclo = new MachineDesignConnectorListObject();
                    mdclo.setItemConnector(portConnector);
                    connectorMap.put(connectorName, mdclo);
                    connections.add(mdclo);
                }
                
                mdclo = connectorMap.get(connectorName);
                
            } else {
                mdclo = new MachineDesignConnectorListObject();
                connections.add(mdclo);
            }
            
            ItemElement cableElement = cableRelationship.getSecondItemElement();
            if (cableElement != null) {
                ItemDomainCableDesign cable = (ItemDomainCableDesign) cableElement.getParentItem();
                mdclo.addCableConnection(cable);
            }
        }
        
        // sort list of connections
        Comparator<MachineDesignConnectorListObject> connectionComparator
                = Comparator
                        .comparing((MachineDesignConnectorListObject o) 
                                -> o.getConnectorName())
                        .thenComparing(o -> o.getConnectedToItemsString())  
                        .thenComparing(o -> o.getConnectedCablesString());    
        connections = connections.stream()
                .sorted(connectionComparator)
                .collect(Collectors.toList());
        
        List<MachineDesignConnectorListObject> connectors = new ArrayList<>();
        
        // add entries for each synced connector that is not currently connected
        // (this is a new case now that ports may have multiple connections)
        List<ItemConnector> itemConnectors = item.getItemConnectorList();
        if (itemConnectors != null) {
            for (ItemConnector connector : itemConnectors) {
                boolean nameInUse = false;
                for (MachineDesignConnectorListObject connection : connections) {
                    if (connector.getConnectorName().equals(connection.getConnectorName())) {
                        nameInUse = true;
                        break;
                    }
                }
                if (!nameInUse) {
                    // synced connector is not used in connection so add to list
                    MachineDesignConnectorListObject mdclo = new MachineDesignConnectorListObject();
                    mdclo.setItemConnector(connector);
                    connectors.add(mdclo);
                }
            }
        }
        
        // add entries for each synced connector, only includes connectors that are not already in use
        List<ItemConnector> syncedConnectors = item.getSyncedConnectorList();
        if (syncedConnectors != null) {
            for (ItemConnector itemConnector : syncedConnectors) {
                MachineDesignConnectorListObject mdclo = new MachineDesignConnectorListObject();
                mdclo.setItemConnector(itemConnector);
                connectors.add(mdclo);
            }
        }
        
        // sort list of connectors
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
    
    private void addCableConnection(ItemDomainCableDesign cable) {
        
        // add cable to list of connected cables
        for (ItemDomainCableDesign currentConnection : connectedCables) {
            // skip this cable if already in list of connected cables
            if (currentConnection.getId().equals(cable.getId())) {
                return;
            }
        }
        this.connectedCables.add(cable);     
        
        // update collection of endpoints
        List<Item> endpointList = cable.getEndpointList();
        boolean first = true;
        this.connectedItems.addAll(endpointList);
        this.updateConnectedItemsString();
    }
    
    private void updateConnectedItemsString() {
        boolean first = true;
        String itemNames = "";
        for (Item endpoint : this.connectedItems) {
            if (first) {
                first = false;
            } else {
                itemNames = itemNames + " | ";
            }
            itemNames = itemNames + endpoint.getName();
        }
        this.connectedToItemsString = itemNames;
    }
    
    public List<ItemDomainCableDesign> getConnectedCables() {
        return connectedCables;
    }
    
    public String getConnectedCablesString() {
        String result = "";
        boolean first = true;
        for (ItemDomainCableDesign cable : connectedCables) {
            if (!first) {
                result = result + " | ";
            } else {
                first = false;
            }
            result = result + cable.getName();
        }
        return result;
    }

//    private void setCableRelationship(ItemElementRelationship cablRelationship, ItemDomainMachineDesign currentItem) {
//        this.cableRelationship = cablRelationship;
//        ItemConnector firstItemConnector = cablRelationship.getFirstItemConnector();
//        this.itemConnector = firstItemConnector;
//        String itemNames = "";
//
//        ItemElement cableElement = cablRelationship.getSecondItemElement();
//        if (cableElement != null) {
//            ItemDomainCableDesign parentItem = (ItemDomainCableDesign) cableElement.getParentItem();
//            this.cableItem = parentItem;
//
//            // create list of endpoint items and string of item names for use in connections table
//            List<Item> endpointList = this.cableItem.getEndpointList();
//            boolean first = true;
//            for (Item endpoint : endpointList) {
//                if (endpoint != null) {
//                    if (endpoint.equals(currentItem)) {
//                        continue;
//                    } else {
//                        if (first) {
//                            first = false;
//                        } else {
//                            itemNames = itemNames + " | ";
//                        }
//                        this.connectedToItemsList.add((ItemDomainMachineDesign) endpoint);
//                        itemNames = itemNames + endpoint.getName();
//                    }
//                }
//            }
//        }
//        this.connectedToItemsString = itemNames;
//    }
//
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

    public String getConnectedToItemsString() {
        return connectedToItemsString;
    }

    public ItemElementRelationship getCableRelationship() {
        return cableRelationship;
    }

}
