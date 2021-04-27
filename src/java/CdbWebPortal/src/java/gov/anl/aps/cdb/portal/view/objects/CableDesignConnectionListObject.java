/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.view.objects;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.utilities.ObjectUtility;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainCableDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElement;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.utilities.ItemUtility;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author craig
 */
public class CableDesignConnectionListObject {

    private ItemDomainCableDesign cableDesign;
    private ItemConnector itemConnector;
    private ItemElementRelationship cableRelationship;
    private ItemDomainMachineDesign mdItem;
    private ItemConnector mdConnector;
    
    public CableDesignConnectionListObject(ItemDomainCableDesign cableDesign) {
        this.cableDesign = cableDesign;
    }

    public ItemDomainCableDesign getCableDesign() {
        return cableDesign;
    }

    public void setCableDesign(ItemDomainCableDesign cableDesign) {
        this.cableDesign = cableDesign;
    }

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

    public String getDescription() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_DESCRIPTION_KEY);
    }

    public void setDescription(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_DESCRIPTION_KEY, value);
    }
    
    public String getRoute() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_ROUTE_KEY);
    }

    public void setRoute(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_ROUTE_KEY, value);
    }
    
    public String getPinlist() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_PINLIST_KEY);
    }

    public void setPinlist(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_PINLIST_KEY, value);
    }
    
    public String getEndLength() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_END_LENGTH_KEY);
    }

    public void setEndLength(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_END_LENGTH_KEY, value);
    }
    
    public String getTermination() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_TERMINATION_KEY);
    }

    public void setTermination(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_TERMINATION_KEY, value);
    }
    
    public String getNotes() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_NOTES_KEY);
    }

    public void setNotes(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_NOTES_KEY, value);
    }
    
    public String getDrawing() throws CdbException {
        return cableDesign.getConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_DRAWING_KEY);
    }

    public void setDrawing(String value) throws CdbException {
        cableDesign.setConnectionPropertyFieldValue(
                cableRelationship, ItemDomainCableDesign.CONNECTION_PROPERTY_DRAWING_KEY, value);
    }
    
    public static List<CableDesignConnectionListObject> getConnectionList(ItemDomainCableDesign item) {
        
        List<CableDesignConnectionListObject> connList = new ArrayList<>();

        // add entries for each cable relationship (connection to MD item)
        List<ItemElementRelationship> cableRelationshipList;
        cableRelationshipList = 
                ItemUtility.getItemRelationshipList(
                        item, 
                        ItemElementRelationshipTypeNames.itemCableConnection.getValue(), 
                        false);
        // sort relationships by sort order
        cableRelationshipList = 
                cableRelationshipList.stream()
                        .sorted(Comparator.comparing(ItemElementRelationship::getSecondSortOrder))
                        .collect(Collectors.toList());
        for (ItemElementRelationship cableRelationship : cableRelationshipList) {
            CableDesignConnectionListObject connection = new CableDesignConnectionListObject(item);
            connection.setCableRelationship(cableRelationship, item);
            connList.add(connection);
        }

//        // add unmapped connectors
//        List<ItemConnector> connectors = item.getItemConnectorList();
//        if (connectors != null) {
//            connectorLoop:
//            for (ItemConnector itemConnector : connectors) {
//                for (CableDesignConnectionListObject connection : connList) {
//                    ItemConnector connConnector = connection.itemConnector;
//                    if (ObjectUtility.equals(connConnector, itemConnector)) {
//                        // Item already connected
//                        continue connectorLoop;
//                    }
//                }
//
//                CableDesignConnectionListObject connection = new CableDesignConnectionListObject(item);
//                connection.setItemConnector(itemConnector);
//                connList.add(connection);
//            }
//        }

        return connList;
    }
    
    public PropertyValue getConnectionMetadataPropertyValue() {
        
        if (cableRelationship == null) {
            return null;
        }
        
        ItemMetadataPropertyInfo info = ItemDomainCableDesign.getConnectionPropertyInfo();

        List<PropertyValue> propertyValueList = cableRelationship.getPropertyValueList();
        if (propertyValueList == null) {
            return null;
        }
        for (PropertyValue propertyValue : propertyValueList) {
            if (propertyValue.getPropertyType().getName().equals(info.getPropertyName())) {
                return propertyValue;
            }
        }
        
        return null;
    }
}
