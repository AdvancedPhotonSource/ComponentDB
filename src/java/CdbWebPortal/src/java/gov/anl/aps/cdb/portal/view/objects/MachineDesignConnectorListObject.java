/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

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
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author djarosz
 */
public class MachineDesignConnectorListObject {

    private ItemConnector itemConnector;
    
    private Set<ItemDomainCableDesign> connectedCables = new TreeSet<>(new Comparator<ItemDomainCableDesign>() {
        @Override
        public int compare(ItemDomainCableDesign o1, ItemDomainCableDesign o2) {
            // Define comparing logic here
            return o1.getName().compareTo(o2.getName());
        }
    });
    
    private Set<Item> connectedItems = new TreeSet<>(new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            // Define comparing logic here
            return o1.getName().compareTo(o2.getName());
        }
    });
    
    private String connectedToItemsString;

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

    public Set<ItemDomainCableDesign> getConnectedCables() {
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
    
    public Set<Item> getConnectedItems() {
        return this.connectedItems;
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
    
    public String getConnectedToItemsString() {
        return connectedToItemsString;
    }

}
