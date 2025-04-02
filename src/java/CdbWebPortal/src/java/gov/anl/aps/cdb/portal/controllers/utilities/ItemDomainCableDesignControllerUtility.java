/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.beans.ItemDomainCableDesignFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableCatalog;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import static gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign.CABLE_DESIGN_INTERNAL_PROPERTY_TYPE;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableInventory;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
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
    public void checkItem(ItemDomainCableDesign item) throws CdbException {
        super.checkItem(item);

        // Ensure unique cable connectors.
        List<ItemConnector> connectorUsed = new ArrayList<>();
        List<ItemElementRelationship> cableEndpoints = ItemUtility.getItemRelationshipList(
                item,
                ItemElementRelationshipTypeNames.itemCableConnection.getValue(),
                false);

        for (ItemElementRelationship ier : cableEndpoints) {
            ItemConnector connector = ier.getSecondItemConnector();
            if (connector == null) {
                continue;
            }

            if (connectorUsed.contains(connector)) {
                throw new CdbException("Cannot use the same connector more than once: " + connector.getConnectorName());
            }
            connectorUsed.add(connector);
        }
    }

    @Override
    public ItemMetadataPropertyInfo createCoreMetadataPropertyInfo() {

        ItemMetadataPropertyInfo info
                = new ItemMetadataPropertyInfo("Cable Design Metadata", CABLE_DESIGN_INTERNAL_PROPERTY_TYPE);

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
                "Routed Length (ft)",
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
                ItemDomainCableDesign.CABLE_DESIGN_PROPERTY_TOTAL_REQ_LENGTH_KEY,
                "Total Required Cable Length (ft)",
                "Total length of the cable required, including routed and end lengths.",
                ItemMetadataFieldType.STRING,
                "",
                null,
                GROUP_CABLE
        );

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
                "End1 Length (ft)",
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
                "Space-separated list of links for cable end 1.",
                ItemMetadataFieldType.URLLIST,
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
                "End2 Length (ft)",
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
                "Space-separated list of links for cable end 2.",
                ItemMetadataFieldType.URLLIST,
                "",
                null,
                GROUP_END2);

        return info;
    }

    @Override
    protected ItemDomainCableDesign instenciateNewItemDomainEntity() {
        return new ItemDomainCableDesign();
    }

    public void updateAssignedInventory(
            ItemDomainCableDesign designItem,
            ItemDomainCableCatalog catalogItem,
            String inventoryName,
            Boolean isInstalled,
            UserInfo userInfo) throws CdbException {

        ItemDomainCableInventory inventoryItem = catalogItem.getInventoryItemNamed(inventoryName);
        if (inventoryItem == null) {
            // catalog item doesn't have inventory item with specified name
            throw new CdbException(
                    "Cable catalog item '" + catalogItem.getName()
                    + "' does not have inventory item named '" + inventoryName + "'");
        }

        updateAssignedItem(designItem, inventoryItem, userInfo, isInstalled);
    }

    public void updateAssignedItem(
            ItemDomainCableDesign cdItem,
            Item newAssignment,
            UserInfo userInfo,
            Boolean isInstalled) throws CdbException {

        if (newAssignment != null) {
            if (isInstalled != null && (newAssignment instanceof ItemDomainCableInventory) == false) {
                throw new CdbException("Is installed can only be set for inventory assignments");
            }

            if (newAssignment instanceof ItemDomainCableInventory == false) {
                isInstalled = null;
            }
        } else {
            isInstalled = null;
        }
        if (isInstalled == null) {
            // Reset to default
            isInstalled = true;
        }

        ItemDomainCableCatalog newCatalogItem = null; // determine new catalog item from newAssignment
        if (newAssignment instanceof ItemDomainCableInventory) {

            ItemDomainCableInventory assignedInventoryItem = (ItemDomainCableInventory) newAssignment;
            newCatalogItem = assignedInventoryItem.getCatalogItem();

            // Check if inventory already assigned to another cable design
            for (ItemElement itemElement : newAssignment.getItemElementMemberList2()) {
                Item item = itemElement.getParentItem();
                if (item instanceof ItemDomainCableDesign) {
                    // Allow updates for the same cable design item. 
                    if (!cdItem.equals(item)) {
                        String exMessage = "Inventory item '" + assignedInventoryItem.getName() + "' already assigned to cable design: '" + item.toString() + "'";
                        if (item.getIsItemDeleted()) {
                            exMessage = exMessage + " (located in trash)";
                        }
                        throw new CdbException(exMessage);
                    }
                }
            }
        } else {
            newCatalogItem = (ItemDomainCableCatalog) newAssignment;
        }

        // if changing catalog item, we need to remove cable connectors 
        // since they are inherited from old catalog item
        Item currCatalogItem = cdItem.getCatalogItem();
        if (((newCatalogItem == null) && (currCatalogItem != null))
                || ((newCatalogItem != null) && (!newCatalogItem.equals(currCatalogItem)))) {
            cdItem.clearCableConnectors();
        }

        cdItem.setAssignedItem(newAssignment);
        cdItem.setIsHoused(isInstalled);
    }

}
