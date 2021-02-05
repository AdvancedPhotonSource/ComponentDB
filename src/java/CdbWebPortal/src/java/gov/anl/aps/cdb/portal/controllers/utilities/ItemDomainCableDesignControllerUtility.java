/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainCableDesignControllerUtility extends ItemControllerUtility<ItemDomainCableDesign, ItemDomainCableDesignFacade> {

    private static final Logger LOGGER = LogManager.getLogger(CdbEntityControllerUtility.class.getName());

    @Override
    public boolean isEntityHasQrId() {
        return false;
    }

    @Override
    public boolean isEntityHasName() {
        return true;
    }

    @Override
    public boolean isEntityHasProject() {
        return true;
    }
    
    @Override
    public boolean isEntityHasItemIdentifier2() {
        return false;
    }

    @Override
    public String getDefaultDomainName() {
        return ItemDomainName.cableDesign.getValue(); 
    }

    @Override
    protected ItemDomainCableDesignFacade getItemFacadeInstance() {
        return ItemDomainCableDesignFacade.getInstance(); 
    }
        
    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
        
    @Override
    public String getEntityTypeName() {
        return "cableDesign";
    }

    public void syncConnectors(ItemDomainCableDesign item) {
        
        List<ItemConnector> itemConnectorList = item.getItemConnectorList();
        List<ItemConnector> connectorsFromAssignedCatalogItem = getConnectorsFromAssignedCatalogItem(item);

        if (connectorsFromAssignedCatalogItem == null) {
            return;
        }

        if (itemConnectorList.isEmpty()) {
            // Sync all connectors into cable design
            for (ItemConnector connector : connectorsFromAssignedCatalogItem) {
                ItemConnector clone = cloneInheritedConnector(connector, item);

                itemConnectorList.add(clone);
            }
            
        } else {
            // Verify if any new connections were created on the catalog             
            catConnFor:
            for (ItemConnector catalogItemConn : connectorsFromAssignedCatalogItem) {
                for (ItemConnector itemConn : itemConnectorList) {
                    Connector itemCconnector = itemConn.getConnector();
                    Connector catConnector = catalogItemConn.getConnector();

                    if (itemCconnector.equals(catConnector)) {
                        continue catConnFor;
                    }
                }
                ItemConnector itemCconnector = cloneInheritedConnector(catalogItemConn, item);
                itemConnectorList.add(itemCconnector);
            }
        }
    }

    private static List<ItemConnector> getConnectorsFromAssignedCatalogItem(ItemDomainCableDesign item) {
        
        Item assignedItem = item.getCatalogItem();

        Item catalogItem = null;
        if (assignedItem instanceof ItemDomainCableInventory) {
            catalogItem = ((ItemDomainCableInventory) assignedItem).getCatalogItem();
        } else if (assignedItem instanceof ItemDomainCableCatalog) {
            catalogItem = assignedItem;
        }

        if (catalogItem != null) {
            return catalogItem.getItemConnectorList();
        }
        return null;
    }

    private ItemConnector cloneInheritedConnector(
            ItemConnector catalogConnector, 
            ItemDomainCableDesign item) {
        
        ItemConnector clone = new ItemConnector();

        clone.setConnector(catalogConnector.getConnector());
        clone.setItem(item);

        return clone;
    }
    
}
