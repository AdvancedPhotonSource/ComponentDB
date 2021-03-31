/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.constants.ItemMetadataFieldType;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainMachineDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.RelationshipTypeControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.view.objects.ItemMetadataPropertyInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author cmcchesney
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_DESIGN_ID + "")
public class ItemDomainCableDesign extends Item {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainCableDesign.class.getName());

    private transient String endpoint1Port = null;
    private transient String endpoint1Connector = null;
    
    private transient String endpoint2Port = null;
    private transient String endpoint2Connector = null;

    private transient List<ItemElementRelationship> deletedRelationshipList = null;
    private transient List<ItemConnector> deletedConnectorList = null;
    
    public final static String CABLE_DESIGN_INTERNAL_PROPERTY_TYPE = "cable_design_internal_property_type";
    public final static String CABLE_DESIGN_PROPERTY_EXT_CABLE_NAME_KEY = "externalCableName";
    public final static String CABLE_DESIGN_PROPERTY_IMPORT_CABLE_ID_KEY = "importCableId";
    public final static String CABLE_DESIGN_PROPERTY_ALT_CABLE_ID_KEY = "alternateCableId";
    public final static String CABLE_DESIGN_PROPERTY_LEGACY_QR_ID_KEY = "legacyQrId";
    public final static String CABLE_DESIGN_PROPERTY_LAYING_KEY = "laying";
    public final static String CABLE_DESIGN_PROPERTY_VOLTAGE_KEY = "voltage";
    public final static String CABLE_DESIGN_PROPERTY_ROUTED_LENGTH_KEY = "routedLength";
    public final static String CABLE_DESIGN_PROPERTY_ROUTE_KEY = "route";
    public final static String CABLE_DESIGN_PROPERTY_NOTES_KEY = "notes";
    public final static String CABLE_DESIGN_PROPERTY_ENDPOINT1_DESC_KEY = "endpoint1Description";
    public final static String CABLE_DESIGN_PROPERTY_ENDPOINT2_DESC_KEY = "endpoint2Description";
    public final static String CABLE_DESIGN_PROPERTY_ENDPOINT1_ROUTE_KEY = "endpoint1Route";
    public final static String CABLE_DESIGN_PROPERTY_ENDPOINT2_ROUTE_KEY = "endpoint2Route";
    
    private static ItemMetadataPropertyInfo connectionMetadataPropertyInfo = null;
    public final static String CABLE_DESIGN_CONNECTION_PROPERTY_TYPE = "cable_design_connection_property_type";
    public final static String CONNECTION_PROPERTY_DESCRIPTION_KEY = "description";
    public final static String CONNECTION_PROPERTY_ROUTE_KEY = "route";
    public final static String CONNECTION_PROPERTY_PINLIST_KEY = "pinlist";
    public final static String CONNECTION_PROPERTY_END_LENGTH_KEY = "endLength";
    public final static String CONNECTION_PROPERTY_TERMINATION_KEY = "termination";
    public final static String CONNECTION_PROPERTY_NOTES_KEY = "notes";
    public final static String CONNECTION_PROPERTY_DRAWING_KEY = "drawing";

    private static final String endpointsSeparator = " | ";

    @Override
    public Item createInstance() {
        return new ItemDomainCableDesign();
    } 

    @Override
    public ItemDomainCableDesignControllerUtility getItemControllerUtility() {
        return new ItemDomainCableDesignControllerUtility();
    }
    
    public List<ItemElementRelationship> getDeletedRelationshipList() {
        if (deletedRelationshipList == null) {
            deletedRelationshipList = new ArrayList<>();
        }
        return deletedRelationshipList;
    }
    
    public void clearDeletedRelationshipList() {
        if (deletedRelationshipList != null) {
            deletedRelationshipList.clear();
        }
    }

    public List<ItemConnector> getDeletedConnectorList() {
        if (deletedConnectorList == null) {
            deletedConnectorList = new ArrayList<>();
        }
        return deletedConnectorList;
    }
    
    public void clearDeletedConnectorList() {
        if (deletedConnectorList != null) {
            deletedConnectorList.clear();
        }
    }

    private RelationshipType getCableConnectionRelationshipType(UserInfo userInfo) {
        RelationshipType relationshipType
                = RelationshipTypeFacade.getInstance().findByName(
                        ItemElementRelationshipTypeNames.itemCableConnection.getValue());
        if (relationshipType == null) {
            RelationshipTypeControllerUtility rtcu = new RelationshipTypeControllerUtility();             
            String name = ItemElementRelationshipTypeNames.itemCableConnection.getValue();
            try {
                relationshipType = rtcu.createRelationshipTypeWithName(name, userInfo);
            } catch (CdbException ex) {
                LOGGER.error(ex);
                return null; 
            }
        }
        return relationshipType;
    }

    /**
     * Creates ItemElementRelationship for the 2 specified items.
     *
     * @param item Machine design item for cable endpoint.
     * @return New instance of ItemElementRelationshipo for specified items.
     */
    private ItemElementRelationship createRelationship(Item item, float sortOrder, UserInfo userInfo) {
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setFirstItemElement(item.getSelfElement());
        itemElementRelationship.setSecondItemElement(this.getSelfElement());
        itemElementRelationship.setSecondSortOrder(sortOrder);

        RelationshipType cableConnectionRelationshipType = getCableConnectionRelationshipType(userInfo);
        itemElementRelationship.setRelationshipType(cableConnectionRelationshipType);

        return itemElementRelationship;
    }

    /**
     * Adds specified relationship for specified item.
     *
     * @param item Item to add relationship for.
     * @param ier Relationship to add.
     * @param secondItem True if the item is the second item in the
     * relationship.
     */
    private void addItemElementRelationshipToItem(Item item, ItemElementRelationship ier, boolean secondItem) {
        ItemElement selfElement = item.getSelfElement();
        List<ItemElementRelationship> ierList;
        if (secondItem) {
            ierList = selfElement.getItemElementRelationshipList1();
        } else {
            ierList = selfElement.getItemElementRelationshipList();
        }
        ierList.add(ier);
    }

    public ItemElementRelationship addCableRelationship(
            Item endpoint,
            ItemConnector endpointConnector,
            ItemConnector cableConnector, 
            Float sortOrder) {
        
        if (endpoint == null) {
            return null;
        }
        
        // calculate sortOrder if not provided
        if (sortOrder == null) {
            float maxSortOrder = this.getMaxRelationshipSortOrder();
            sortOrder = maxSortOrder + 1;
        }
        
        EntityInfo entityInfo = this.getEntityInfo();
        UserInfo ownerUser = entityInfo.getOwnerUser();
        
        // create relationships from cable to endpoints
        ItemElementRelationship relationship = createRelationship(endpoint, sortOrder, ownerUser);

        // Create list for cable's relationships. 
        ItemElement selfElement = this.getSelfElement();
        if (selfElement.getItemElementRelationshipList1() == null) {
            selfElement.setItemElementRelationshipList1(new ArrayList<>());
        }

        // Add appropriate item relationships to model.
        addItemElementRelationshipToItem(endpoint, relationship, false);
        addItemElementRelationshipToItem(this, relationship, true);
        
        // set connectors
        relationship.setFirstItemConnector(endpointConnector);
        relationship.setSecondItemConnector(cableConnector);

        return relationship;
    }
    
    private void removeCableRelationship(
            ItemElementRelationship relationship,
            boolean isImport) {
        
        this.getSelfElement().getItemElementRelationshipList1().remove(relationship);
        // remove relationship from old endpoint's relationship list
        relationship.getFirstItemElement().getItemElementRelationshipList().remove(relationship);
        if (isImport) {
            getDeletedRelationshipList().add(relationship);
        }
    }
    
    private void updateCableRelationship(
            Item itemEndpoint,
            ItemElementRelationship cableRelationship,
            ItemConnector endpointConnector,
            ItemConnector cableConnector,
            boolean isImport) {
        
        if (itemEndpoint == null) {
            // remove relationship from cable's relationship list
            removeCableRelationship(cableRelationship, isImport);
        } else {
            // update existing relationship with new endpoint
            ItemDomainCableDesign.this.updateCableRelationship(
                    cableRelationship, itemEndpoint, endpointConnector, cableConnector);
        }
    }
    
    public void updateCableRelationship(
            ItemElementRelationship cableRelationship, 
            Item itemEndpoint,
            ItemConnector endpointConnector,
            ItemConnector cableConnector) {
        
        cableRelationship.setFirstItemElement(itemEndpoint.getSelfElement());
        cableRelationship.setFirstItemConnector(endpointConnector);
        cableRelationship.setSecondItemConnector(cableConnector);
    }

    private PropertyValue getInternalCableDesignPropertyValue() {
        List<PropertyValue> propertyValueList = getPropertyValueList();
        for (PropertyValue propertyValue : propertyValueList) {
            if (propertyValue.getPropertyType().getName().equals(CABLE_DESIGN_INTERNAL_PROPERTY_TYPE)) {
                return propertyValue;
            }
        }
        return null;
    }

   public static ItemMetadataPropertyInfo getConnectionPropertyInfo() {
        
        if (connectionMetadataPropertyInfo == null) {
            
            connectionMetadataPropertyInfo = new ItemMetadataPropertyInfo(
                    "Cable Connection Metadata", CABLE_DESIGN_CONNECTION_PROPERTY_TYPE);
            
            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_DESCRIPTION_KEY, 
                    "Description", 
                    "Connection description.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);
            
            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_ROUTE_KEY, 
                    "Route", 
                    "Routing waypoint for connection.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);
            
            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_END_LENGTH_KEY, 
                    "End Length", 
                    "Calculated end length for connection.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);
            
            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_TERMINATION_KEY, 
                    "Termination", 
                    "Termination for connection.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);
            
            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_PINLIST_KEY, 
                    "Pinlist", 
                    "Pin mapping details for connection.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);

            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_NOTES_KEY, 
                    "Notes", 
                    "Notes for connection.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);
            
            connectionMetadataPropertyInfo.addField(
                    CONNECTION_PROPERTY_DRAWING_KEY, 
                    "Drawing", 
                    "Drawing for connection.", 
                    ItemMetadataFieldType.STRING, 
                    "", 
                    null);
            
        }
        return connectionMetadataPropertyInfo;
    }

    public PropertyValue getConnectionPropertyValue(ItemElementRelationship ier) {

        ItemMetadataPropertyInfo info = getConnectionPropertyInfo();

        if (info != null) {
            List<PropertyValue> propertyValueList = ier.getPropertyValueList();
            if (propertyValueList == null) {
                return null;
            }
            for (PropertyValue propertyValue : propertyValueList) {
                if (propertyValue.getPropertyType().getName().equals(info.getPropertyName())) {
                    return propertyValue;
                }
            }
        }

        return null;
    }

    private void setConnectionPropertyFieldValue(
            ItemElementRelationship ier, String key, String value) throws CdbException {
        
        if (ier == null) {
            return;
        }

        validateConnectionPropertyFieldKey(key);

        PropertyValue propertyValue = getConnectionPropertyValue(ier);

        if (propertyValue == null) {
            propertyValue = 
                    getItemControllerUtility().prepareConnectionPropertyValue(ier, getConnectionPropertyInfo());
        }
        
        if (value == null) {
            value = "";
        }
        
        propertyValue.setPropertyMetadataValue(key, value);
    }

    private void validateConnectionPropertyFieldKey(String key) throws CdbException {
        ItemMetadataPropertyInfo info = getConnectionPropertyInfo();
        if (!info.hasKey(key)) {
            throw new CdbException("Invalid metadata key used to get/set connection property field value: " + key);
        }
    }
    
    protected String getConnectionPropertyFieldValue(ItemElementRelationship ier, String key) throws CdbException {

        if (ier == null) {
            return null;
        }
        
        validateConnectionPropertyFieldKey(key);

        PropertyValue propertyValue = getConnectionPropertyValue(ier);
        if (propertyValue != null) {
            return propertyValue.getPropertyMetadataValueForKey(key);
        } else {
            return "";
        }
    }

    public void setEndpoint(
            Item itemEndpoint,
            ItemConnector endpointConnector,
            ItemConnector cableConnector,
            float sortOrder,
            boolean isImport) {

        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(sortOrder);
        if (cableRelationship != null) {
            updateCableRelationship(
                    itemEndpoint, cableRelationship, endpointConnector, cableConnector, isImport);
        } else {
            if (itemEndpoint != null) {
                this.addCableRelationship(itemEndpoint, endpointConnector, cableConnector, sortOrder);
            }
        }
    }
    
    public ValidInfo setEndpointImport(
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName,
            float sortOrder) {
        
        boolean isValid = true;
        String validString = "";
        
        // validate
        if ((itemEndpoint == null)
                && (((endpointConnectorName != null) && (!endpointConnectorName.isBlank()))
                || ((cableConnectorName != null) && (!cableConnectorName.isBlank())))) {
            
            isValid = false;
            validString = "Must specify endpoint item to specify port or connector.";
            return new ValidInfo(isValid, validString);
        }
        
        // retrieve existing cable relationship, endpoint, and connectors
        ItemDomainMachineDesign origItemEndpoint = null;
        ItemConnector origEndpointConnector = null;
        ItemConnector origCableConnector = null;
        ItemElementRelationship existingRelationship = this.getCableConnectionBySortOrder(sortOrder);
        if (existingRelationship != null) {
            origItemEndpoint = 
                    (ItemDomainMachineDesign)existingRelationship.getFirstItemElement().getParentItem();
            origEndpointConnector = existingRelationship.getFirstItemConnector();
            origCableConnector = existingRelationship.getSecondItemConnector();
        }
        
        // get endpoint connector
        ItemConnector endpointConnector = null;
        if (itemEndpoint != null) {
            if (endpointConnectorName != null && !endpointConnectorName.isBlank()) {
                if ((origItemEndpoint != null) 
                        && (itemEndpoint.getId().equals(origItemEndpoint.getId()))
                        && (origEndpointConnector != null)
                        && (endpointConnectorName.equals(origEndpointConnector.getConnector().getName()))) {
                    endpointConnector = origEndpointConnector;
                } else {
                    ItemDomainMachineDesignControllerUtility mdUtility = new ItemDomainMachineDesignControllerUtility();
                    mdUtility.syncMachineDesignConnectors(itemEndpoint);
                    endpointConnector = itemEndpoint.getConnectorNamed(endpointConnectorName);
                    if (endpointConnector == null) {
                        isValid = false;
                        validString = validString + "Endpoint: " + itemEndpoint.getName() + " port: " + endpointConnectorName + " does not exist.";
                    } else if (endpointConnector.isConnected()) {
                        isValid = false;
                        validString = validString + "Endpoint: " + itemEndpoint.getName() + " port: " + endpointConnectorName + " is already connected.";
                    }
                }
            }
        }
        
        // get cable connector
        ItemConnector cableConnector = null;
        if (cableConnectorName != null && !cableConnectorName.isBlank()) {
            if (this.getCatalogItem() == null) {
                isValid = false;
                validString = validString + "Must specify cable type to use cable connectors.";
            } else {
                if ((origCableConnector != null)
                        && (cableConnectorName.equals(origCableConnector.getConnector().getName()))) {
                    cableConnector = origCableConnector;
                } else {
                    getItemControllerUtility().syncConnectors(this);
                    cableConnector = this.getConnectorNamed(cableConnectorName);
                    if (cableConnector == null) {
                        isValid = false;
                        validString = validString + "Cable connector: " + cableConnectorName + " does not exist.";
                    } else if (cableConnector.isConnected()) {
                        isValid = false;
                        validString = validString + "Cable connector: " + cableConnectorName + " is already connected.";
                    }
                }
            }
        }
        
        setEndpoint(itemEndpoint, endpointConnector, cableConnector, sortOrder, true);
        
        return new ValidInfo(isValid, validString);
    }

    public List<Item> getEndpointList() {
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList
                = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            // find just the cable relationship items
            RelationshipType cableIerType
                    = RelationshipTypeFacade.getInstance().findByName(
                            ItemElementRelationshipTypeNames.itemCableConnection.getValue());
            if (cableIerType != null) {
                return ierList.stream().
                        filter(ier -> ier.getRelationshipType().getName().equals(cableIerType.getName())).
                        sorted((ier1, ier2) -> (ier1.getSecondSortOrder() == null || ier2.getSecondSortOrder() == null) ? 0 : ier1.getSecondSortOrder().compareTo(ier2.getSecondSortOrder())).
                        map(ier -> ier.getFirstItemElement().getParentItem()).
                        collect(Collectors.toList());
            }
        }

        return null;
    }
    
    public ItemElementRelationship getCableConnectionBySortOrder(float sortOrder) {
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList
                = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            // find just the cable relationship items
            RelationshipType cableIerType
                    = RelationshipTypeFacade.getInstance().findByName(
                            ItemElementRelationshipTypeNames.itemCableConnection.getValue());
            if (cableIerType != null) {
                for (ItemElementRelationship rel : ierList) {
                    if ((rel.getRelationshipType().getName().equals(cableIerType.getName()))
                            && (rel.getSecondSortOrder() == sortOrder)) {
                        return rel;
                    }
                }
            }
        }
        return null;
    }

    public float getMaxRelationshipSortOrder() {
        float maxSortOrder = 0;
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList
                = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            // find just the cable relationship items
            RelationshipType cableIerType
                    = RelationshipTypeFacade.getInstance().findByName(
                            ItemElementRelationshipTypeNames.itemCableConnection.getValue());
            if (cableIerType != null) {
                for (ItemElementRelationship rel : ierList) {
                    if ((rel.getRelationshipType().getName().equals(cableIerType.getName()))
                            && (rel.getSecondSortOrder() > maxSortOrder)) {
                        maxSortOrder = rel.getSecondSortOrder();
                    }
                }
            }
        }
        return maxSortOrder;
    }

    /**
     * Returns a string containing the cables endpoints for display.
     */
    public String getEndpointsString() {
        String result = "";
        int count = 0;
        List<Item> iList = this.getEndpointList();
        for (Item endpoint : iList) {
            count = count + 1;
            result = result + endpoint.getName();
            if (count != iList.size()) {
                result = result + endpointsSeparator;
            }
        }
        return result;
    }

    public String getPortForEndpoint(float sortOrder) {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(sortOrder);
        if (cableRelationship != null) {
            ItemConnector connector = cableRelationship.getFirstItemConnector();
            if (connector != null) {
                return connector.getConnector().getName();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public String getConnectorForEndpoint(float sortOrder) {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(sortOrder);
        if (cableRelationship != null) {
            ItemConnector connector = cableRelationship.getSecondItemConnector();
            if (connector != null) {
                return connector.getConnector().getName();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    private void clearCableConnectors() {
        
        // null out connectors used in cable relationships
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList
                = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            // find just the cable relationship items
            RelationshipType cableIerType
                    = RelationshipTypeFacade.getInstance().findByName(
                            ItemElementRelationshipTypeNames.itemCableConnection.getValue());
            if (cableIerType != null) {
                for (ItemElementRelationship rel : ierList) {
                    if (rel.getRelationshipType().getName().equals(cableIerType.getName())) {
                        ItemConnector cableConnector = rel.getSecondItemConnector();
                        if (cableConnector != null) {
                            rel.setSecondItemConnector(null);
                        }
                    }
                }
            }
        }
        
        // add all connectors to list of connectors to remove
        if (getItemConnectorList() != null) {
            for (ItemConnector connector : getItemConnectorList()) {
                getDeletedConnectorList().add(connector);
            }
            getItemConnectorList().clear();
        }
    }
    
    public String getAlternateName() {
        return getItemIdentifier1();
    }

    public void setAlternateName(String n) {
        setItemIdentifier1(n);
    }

    public void setTechnicalSystemList(List<ItemCategory> technicalSystemList) {
        setItemCategoryList(technicalSystemList);
    }
    
    @JsonIgnore
    public String getExternalCableName() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_EXT_CABLE_NAME_KEY);
    }

    public void setExternalCableName(String name) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_EXT_CABLE_NAME_KEY, name);
    }

    @JsonIgnore
    public String getImportCableId() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_IMPORT_CABLE_ID_KEY);
    }

    public void setImportCableId(String id) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_IMPORT_CABLE_ID_KEY, id);
    }

    @JsonIgnore
    public String getAlternateCableId() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ALT_CABLE_ID_KEY);
    }

    public void setAlternateCableId(String id) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ALT_CABLE_ID_KEY, id);
    }

    @JsonIgnore
    public String getLegacyQrId() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LEGACY_QR_ID_KEY);
    }

    public void setLegacyQrId(String id) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LEGACY_QR_ID_KEY, id);
    }

    @JsonIgnore
    public String getLaying() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LAYING_KEY);
    }

    public void setLaying(String l) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LAYING_KEY, l);
    }

    @JsonIgnore
    public String getVoltage() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_VOLTAGE_KEY);
    }

    public void setVoltage(String v) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_VOLTAGE_KEY, v);
    }

    @JsonIgnore
    public String getRoutedLength() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTED_LENGTH_KEY);
    }

    public void setRoutedLength(String length) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTED_LENGTH_KEY, length);
    }

    @JsonIgnore
    public String getRoute() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTE_KEY);
    }

    public void setRoute(String route) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTE_KEY, route);
    }

    @JsonIgnore
    public String getNotes() throws CdbException {
        return getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_NOTES_KEY);
    }

    public void setNotes(String notes) throws CdbException {
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_NOTES_KEY, notes);
    }

    public void setCatalogItem(Item itemCableCatalog) {
        // "assign" catalog item to cable design
        ItemElement selfElement = this.getSelfElement();
        if (!itemCableCatalog.equals(selfElement.getContainedItem2())) {
            // if changing catalog item, we need to remove cable connectors since they are inherited from catalog item
            clearCableConnectors();
        }
        selfElement.setContainedItem2(itemCableCatalog);
    }

    public void setCatalogItemId(String catalogItemId) {
        ItemDomainCableCatalog catalogItem = (ItemDomainCableCatalog) (getEntityById(catalogItemId));

        if (catalogItem != null) {
            setCatalogItem(catalogItem);
        } else {
            LOGGER.error("setCatalogItemId() unknown cable catalog item id " + catalogItemId);
        }
     }

    public Item getCatalogItem() {
        ItemElement selfElementCable = this.getSelfElement();
        return selfElementCable.getContainedItem2();
    }

    public String getCatalogItemString() {
        Item iCatalog = this.getCatalogItem();
        if (iCatalog != null) {
            return iCatalog.getName();
        } else {
            return "";
        }
    }

    public Item getEndpoint1() {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        if (cableRelationship != null) {
            return cableRelationship.getFirstItemElement().getParentItem();
        } else {
            return null;
        }
    }

    public String getEndpoint1String() {
        Item iEndpoint1 = this.getEndpoint1();
        if (iEndpoint1 != null) {
            return iEndpoint1.getName();
        } else {
            return "";
        }
    }

    public void setEndpoint1(Item itemEndpoint) {
        setEndpoint(itemEndpoint, null, null, 1.0f, false);
    }

    public ValidInfo setEndpoint1Import(
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName) {
        
        endpoint1Port = endpointConnectorName;
        endpoint1Connector = cableConnectorName;
        return setEndpointImport(itemEndpoint, endpointConnectorName, cableConnectorName, 1.0f);
    }

    @JsonIgnore
    public String getEndpoint1PortImport() {
        return endpoint1Port;
    }

    @JsonIgnore
    public String getEndpoint1Port() {
        return getPortForEndpoint(1.0f);
    }

    @JsonIgnore
    public String getEndpoint1ConnectorImport() {
        return endpoint1Connector;
    }

    @JsonIgnore
    public String getEndpoint1Connector() {
        return getConnectorForEndpoint(1.0f);
    }

    @JsonIgnore
    public String getEndpoint1Description() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DESCRIPTION_KEY);
    }

    public void setEndpoint1Description(String description) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DESCRIPTION_KEY, description);
    }
    
    @JsonIgnore
    public String getEndpoint1Route() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_ROUTE_KEY);
    }

    public void setEndpoint1Route(String route) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_ROUTE_KEY, route);
    }

    @JsonIgnore
    public String getEndpoint1Pinlist() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_PINLIST_KEY);
    }

    public void setEndpoint1Pinlist(String pinlist) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_PINLIST_KEY, pinlist);
    }

    @JsonIgnore
    public String getEndpoint1EndLength() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_END_LENGTH_KEY);
    }

    public void setEndpoint1EndLength(String endLength) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_END_LENGTH_KEY, endLength);
    }

    @JsonIgnore
    public String getEndpoint1Termination() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_TERMINATION_KEY);
    }

    public void setEndpoint1Termination(String termination) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_TERMINATION_KEY, termination);
    }

    @JsonIgnore
    public String getEndpoint1Notes() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_NOTES_KEY);
    }

    public void setEndpoint1Notes(String notes) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_NOTES_KEY, notes);
    }

    @JsonIgnore
    public String getEndpoint1Drawing() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DRAWING_KEY);
    }

    public void setEndpoint1Drawing(String drawing) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(1.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DRAWING_KEY, drawing);
    }

    public Item getEndpoint2() {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        if (cableRelationship != null) {
            return cableRelationship.getFirstItemElement().getParentItem();
        } else {
            return null;
        }
    }

    public String getEndpoint2String() {
        Item iEndpoint2 = this.getEndpoint2();
        if (iEndpoint2 != null) {
            return iEndpoint2.getName();
        } else {
            return "";
        }
    }

    public void setEndpoint2(Item itemEndpoint) {
        setEndpoint(itemEndpoint, null, null, 2.0f, false);
    }
    
    public ValidInfo setEndpoint2Import(
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName) {
        
        endpoint2Port = endpointConnectorName;
        endpoint2Connector = cableConnectorName;
        return setEndpointImport(itemEndpoint, endpointConnectorName, cableConnectorName, 2.0f);
    }

    @JsonIgnore
    public String getEndpoint2PortImport() {
        return endpoint2Port;
    }

    @JsonIgnore
    public String getEndpoint2Port() {
        return getPortForEndpoint(2.0f);
    }

    @JsonIgnore
    public String getEndpoint2ConnectorImport() {
        return endpoint2Connector;
    }

    @JsonIgnore
    public String getEndpoint2Connector() {
        return getConnectorForEndpoint(2.0f);
    }

    @JsonIgnore
    public String getEndpoint2Description() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DESCRIPTION_KEY);
    }

    public void setEndpoint2Description(String description) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DESCRIPTION_KEY, description);
    }

    @JsonIgnore
    public String getEndpoint2Route() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_ROUTE_KEY);
    }

    public void setEndpoint2Route(String route) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_ROUTE_KEY, route);
    }
    
    
    @JsonIgnore
    public String getEndpoint2Pinlist() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_PINLIST_KEY);
    }

    public void setEndpoint2Pinlist(String pinlist) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
            setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_PINLIST_KEY, pinlist);
    }

    @JsonIgnore
    public String getEndpoint2EndLength() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_END_LENGTH_KEY);
    }

    public void setEndpoint2EndLength(String endLength) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_END_LENGTH_KEY, endLength);
    }
    
    @JsonIgnore
    public String getEndpoint2Termination() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_TERMINATION_KEY);
    }

    public void setEndpoint2Termination(String termination) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_TERMINATION_KEY, termination);
    }
    
    @JsonIgnore
    public String getEndpoint2Notes() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_NOTES_KEY);
    }

    public void setEndpoint2Notes(String notes) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_NOTES_KEY, notes);
    }
    
    @JsonIgnore
    public String getEndpoint2Drawing() throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        return getConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DRAWING_KEY);
    }

    public void setEndpoint2Drawing(String drawing) throws CdbException {
        ItemElementRelationship cableRelationship = getCableConnectionBySortOrder(2.0f);
        setConnectionPropertyFieldValue(cableRelationship, CONNECTION_PROPERTY_DRAWING_KEY, drawing);
    }
    
}
