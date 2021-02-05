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
 * @author craig
 */
public class CableDesignConnectionListObject {

    private ItemConnector itemConnector;
    private ItemElementRelationship cableRelationship;
    private ItemDomainMachineDesign mdItem;
    private ItemConnector mdConnector;

    public ItemConnector getItemConnector() {
        return itemConnector;
    }

    private void setItemConnector(ItemConnector itemConnector) {
        this.itemConnector = itemConnector;
    }

    public ItemElementRelationship getCableRelationship() {
        return cableRelationship;
    }
        
    private void setCableRelationship(
            ItemElementRelationship cableRelationship, 
            ItemDomainCableDesign currentItem) {
        
        this.cableRelationship = cableRelationship;
        ItemConnector itemConnector = cableRelationship.getSecondItemConnector();
        this.itemConnector = itemConnector;

        ItemElement mdElement = cableRelationship.getFirstItemElement();
        if (mdElement != null) {
            this.mdItem = (ItemDomainMachineDesign) mdElement.getParentItem();
            this.mdConnector = cableRelationship.getFirstItemConnector();
        }
    }

    public ItemDomainMachineDesign getMdItem() {
        return mdItem;
    }

    public ItemConnector getMdConnector() {
        return mdConnector;
    }

    public static List<CableDesignConnectionListObject> getConnectionList(ItemDomainCableDesign item) {
        
        List<CableDesignConnectionListObject> connList = new ArrayList<>();

        // add entries for each cable relationship (connection to MD item)
        List<ItemElementRelationship> cableRelationshipList;
        cableRelationshipList = ItemUtility.getItemRelationshipList(item, ItemElementRelationshipTypeNames.itemCableConnection.getValue(), false);
        for (ItemElementRelationship cableRelationship : cableRelationshipList) {
            CableDesignConnectionListObject connection = new CableDesignConnectionListObject();
            connection.setCableRelationship(cableRelationship, item);
            connList.add(connection);
        }

        // add unmapped connectors
        List<ItemConnector> connectors = item.getItemConnectorList();
        if (connectors != null) {
            connectorLoop:
            for (ItemConnector itemConnector : connectors) {
                for (CableDesignConnectionListObject connection : connList) {
                    ItemConnector connConnector = connection.itemConnector;
                    if (ObjectUtility.equals(connConnector, itemConnector)) {
                        // Item already connected
                        continue connectorLoop;
                    }
                }

                CableDesignConnectionListObject connection = new CableDesignConnectionListObject();
                connection.setItemConnector(itemConnector);
                connList.add(connection);
            }
        }

        return connList;
    }
    
}
