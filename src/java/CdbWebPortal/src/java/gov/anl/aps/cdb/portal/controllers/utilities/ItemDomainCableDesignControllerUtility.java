/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Connector;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import static gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign.CABLE_DESIGN_INTERNAL_PROPERTY_TYPE;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainCableDesignControllerUtility extends ItemControllerUtility<ItemDomainCableDesign, ItemDomainCableDesignFacade> {

    private static final Logger LOGGER = LogManager.getLogger(CdbEntityControllerUtility.class.getName());
    
    private static final String GROUP_CABLE = "Cable";
    private static final String GROUP_END1 = "End 1";
    private static final String GROUP_END2 = "End 2";

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
    
    @Override
    public ItemMetadataPropertyInfo createCoreMetadataPropertyInfo() {
        
        ItemMetadataPropertyInfo info = 
                new ItemMetadataPropertyInfo("Cable Design Metadata", CABLE_DESIGN_INTERNAL_PROPERTY_TYPE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_EXT_CABLE_NAME_KEY, 
                "Ext Cable Name", 
                "External cable name (e.g., from CAD or routing tool).", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_IMPORT_CABLE_ID_KEY, 
                "Import Cable ID", 
                "Import cable identifier.", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_ALT_CABLE_ID_KEY, 
                "Alt Cable ID", 
                "Alternate (e.g., group-specific) cable identifier.", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_LAYING_KEY, 
                "Laying", 
                "Laying style e.g., S=single-layer, M=multi-layer, T=triangular, B=bundle", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_VOLTAGE_KEY, 
                "Voltage", 
                "Voltage aplication e.g., COM=communication, CTRL=control, IW=instrumentation, LV=low voltage, MV=medium voltage", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_ROUTED_LENGTH_KEY, 
                "Routed Length", 
                "Calculated length for cable", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_ROUTE_KEY, 
                "Route", 
                "Description of cable route", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_NOTES_KEY, 
                "Notes", 
                "Notes about this cable", 
                ItemMetadataFieldType.STRING, 
                "", 
                null,
                GROUP_CABLE);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_DESCRIPTION_KEY,
                "End1 Description",
                "End description.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_ROUTE_KEY,
                "End1 Route",
                "Routing waypoint for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_ENDLENGTH_KEY,
                "End1 Length",
                "Calculated length for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_TERMINATION_KEY,
                "End1 Termination",
                "Termination for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_PINLIST_KEY,
                "End1 Pinlist",
                "Pin mapping details for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_NOTES_KEY,
                "End1 Notes",
                "Notes for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END1_DRAWING_KEY,
                "End1 Drawing",
                "Drawing for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END1);
        
        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_DESCRIPTION_KEY,
                "End2 Description",
                "End description.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_ROUTE_KEY,
                "End2 Route",
                "Routing waypoint for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_ENDLENGTH_KEY,
                "End2 Length",
                "Calculated length for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_TERMINATION_KEY,
                "End2 Termination",
                "Termination for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_PINLIST_KEY,
                "End2 Pinlist",
                "Pin mapping details for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_NOTES_KEY,
                "End2 Notes",
                "Notes for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);

        info.addField(
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_END2_DRAWING_KEY,
                "End2 Drawing",
                "Drawing for cable end.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_END2);
        
        return info;
    }
    
    @Override
    protected ItemDomainCableDesign instenciateNewItemDomainEntity() {
        return new ItemDomainCableDesign();
    }     

    public void syncConnectors(ItemDomainCableDesign item) {
        
        List<ItemConnector> connectorsFromAssignedCatalogItem = getConnectorsFromAssignedCatalogItem(item);
        if (connectorsFromAssignedCatalogItem == null) {
            return;
        }
        
        List<ItemConnector> itemConnectorList = item.getItemConnectorList();
        if (itemConnectorList == null) {
            itemConnectorList = new ArrayList<>();
            item.setItemConnectorList(itemConnectorList);
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

    private List<ItemConnector> getConnectorsFromAssignedCatalogItem(ItemDomainCableDesign item) {
        
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

        Connector connector = catalogConnector.getConnector();
        clone.setConnector(connector);
        clone.setItem(item);
        
       return clone;
    }
    
}
