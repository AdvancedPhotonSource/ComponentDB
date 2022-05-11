/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.LocatableItemController;
import gov.anl.aps.cdb.portal.controllers.utilities.ItemDomainCableDesignControllerUtility;
import gov.anl.aps.cdb.portal.controllers.utilities.RelationshipTypeControllerUtility;
import gov.anl.aps.cdb.portal.import_export.import_.objects.CreateInfo;
import gov.anl.aps.cdb.portal.import_export.import_.objects.ValidInfo;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author cmcchesney
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_DESIGN_ID + "")
@NamedQueries({
    @NamedQuery(name = "ItemDomainCableDesign.filterAncestorAny",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName AND FUNCTION('cable_design_ancestor_filter', i.id, :nameFilterValue, '')"),
    @NamedQuery(name = "ItemDomainCableDesign.filterAncestorByEnd",
            query = "SELECT i FROM Item i WHERE i.domain.name = :domainName AND FUNCTION('cable_design_ancestor_filter', i.id, :end1Value, '1') AND FUNCTION('cable_design_ancestor_filter', i.id, :end2Value, '2')")
})
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
        name = "itemCableDesign.searchItems",
        procedureName = "search_cable_design_items",
        resultClasses = ItemDomainCableDesign.class,
        parameters = {
            @StoredProcedureParameter(
                    name = "limit_row",
                    mode = ParameterMode.IN,
                    type = Integer.class
            ),
            @StoredProcedureParameter(
                    name = "search_string",
                    mode = ParameterMode.IN,
                    type = String.class
            )
        }
    )
})
public class ItemDomainCableDesign extends Item {

    private static final Logger LOGGER = LogManager.getLogger(ItemDomainCableDesign.class.getName());

    private transient String externalCableName = null;
    private transient String importCableId = null;
    private transient String importAssignedInventoryTag = null;
    private transient Boolean importIsInstalled = null;
    private transient boolean importIsInstalledLoaded = false;
    private transient String alternateCableId = null;
    private transient String legacyQrId = null;
    private transient String laying = null;
    private transient String voltage = null;
    private transient String routedLength = null;
    private transient String route = null;
    private transient String notes = null;
    
    private transient Item endpoint1ItemImport = null;
    private transient String endpoint1PortImport = null;
    private transient String endpoint1ConnectorImport = null;
    private transient String endpoint1Description = null;
    private transient String endpoint1Route = null;
    private transient String endpoint1Pinlist = null;
    private transient String endpoint1EndLength = null;
    private transient String endpoint1Termination = null;
    private transient String endpoint1Notes = null;
    private transient String endpoint1Drawing = null;
    
    private transient Item endpoint2ItemImport = null;
    private transient String endpoint2PortImport = null;
    private transient String endpoint2ConnectorImport = null;
    private transient String endpoint2Description = null;
    private transient String endpoint2Route = null;
    private transient String endpoint2Pinlist = null;
    private transient String endpoint2EndLength = null;
    private transient String endpoint2Termination = null;
    private transient String endpoint2Notes = null;
    private transient String endpoint2Drawing = null;

    private transient List<ItemElementRelationship> deletedIerList = null;
    
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
    public final static String CABLE_DESIGN_PROPERTY_END1_DESCRIPTION_KEY = "endpoint1Description";
    public final static String CABLE_DESIGN_PROPERTY_END1_ROUTE_KEY = "endpoint1Route";
    public final static String CABLE_DESIGN_PROPERTY_END1_ENDLENGTH_KEY = "endpoint1EndLength";
    public final static String CABLE_DESIGN_PROPERTY_END1_TERMINATION_KEY = "endpoint1Termination";
    public final static String CABLE_DESIGN_PROPERTY_END1_PINLIST_KEY = "endpoint1Pinlist";
    public final static String CABLE_DESIGN_PROPERTY_END1_NOTES_KEY = "endpoint1Notes";
    public final static String CABLE_DESIGN_PROPERTY_END1_DRAWING_KEY = "endpoint1Drawing";
    public final static String CABLE_DESIGN_PROPERTY_END2_DESCRIPTION_KEY = "endpoint2Description";
    public final static String CABLE_DESIGN_PROPERTY_END2_ROUTE_KEY = "endpoint2Route";
    public final static String CABLE_DESIGN_PROPERTY_END2_ENDLENGTH_KEY = "endpoint2EndLength";
    public final static String CABLE_DESIGN_PROPERTY_END2_TERMINATION_KEY = "endpoint2Termination";
    public final static String CABLE_DESIGN_PROPERTY_END2_PINLIST_KEY = "endpoint2Pinlist";
    public final static String CABLE_DESIGN_PROPERTY_END2_NOTES_KEY = "endpoint2Notes";
    public final static String CABLE_DESIGN_PROPERTY_END2_DRAWING_KEY = "endpoint2Drawing";
    
    private static final String endpointsSeparator = " | ";
    
    private static RelationshipType cableRelationshipType = null;
    private static PropertyType endPropertyType = null;
    private static PropertyType coreMetadataPropertyType = null;
    
    private final static String RELATIONSHIP_LABEL_PRIMARY = 
            ItemElementRelationship.VALUE_LABEL_PRIMARY_CABLE_CONN;
    private final static String RELATIONSHIP_LABEL_DETAIL = 
            ItemElementRelationship.VALUE_LABEL_DETAIL_CABLE_CONN;

    @Override
    public Item createInstance() {
        return new ItemDomainCableDesign();
    } 

    @Override
    @JsonIgnore
    public ItemDomainCableDesignControllerUtility getItemControllerUtility() {
        return new ItemDomainCableDesignControllerUtility();
    }
    
    @JsonIgnore
    public List<ItemElementRelationship> getDeletedIerList() {
        if (deletedIerList == null) {
            deletedIerList = new ArrayList<>();
        }
        return deletedIerList;
    }
    
    public void clearDeletedIerList() {
        if (deletedIerList != null) {
            deletedIerList.clear();
        }
    }
    
    private RelationshipType getCableConnectionRelationshipType() {
        EntityInfo entityInfo = this.getEntityInfo();
        UserInfo ownerUser = entityInfo.getOwnerUser();
        return getCableConnectionRelationshipType(ownerUser);
    }

    @Override
    protected Item getInheritedItemConnectorParent() {
        return getCatalogItem();
    }

    private RelationshipType getCableConnectionRelationshipType(UserInfo userInfo) {
        if (cableRelationshipType == null) {
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
            cableRelationshipType = relationshipType;
        }
        return cableRelationshipType;
    }

    /**
     * Creates ItemElementRelationship for the 2 specified items.
     *
     * @param item Machine design item for cable endpoint.
     * @return New instance of ItemElementRelationshipo for specified items.
     */
    private ItemElementRelationship createRelationship(
            Item item, String cableEnd, boolean isPrimary, UserInfo userInfo) {
        
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        
        if (item != null) {
            itemElementRelationship.setFirstItemElement(item.getSelfElement());
        }
        
        itemElementRelationship.setSecondItemElement(this.getSelfElement());
        itemElementRelationship.setCableEndDesignation(cableEnd);
        if (isPrimary) {
            itemElementRelationship.setLabel(RELATIONSHIP_LABEL_PRIMARY);
        } else {
            itemElementRelationship.setLabel(RELATIONSHIP_LABEL_DETAIL);
        }

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
            String cableEnd,
            boolean isPrimary) {
        
        EntityInfo entityInfo = this.getEntityInfo();
        UserInfo ownerUser = entityInfo.getOwnerUser();
        
        // create relationships from cable to endpoints
        ItemElementRelationship relationship = 
                createRelationship(endpoint, cableEnd, isPrimary, ownerUser);

        // Create list for cable's relationships. 
        ItemElement selfElement = this.getSelfElement();
        if (selfElement.getItemElementRelationshipList1() == null) {
            selfElement.setItemElementRelationshipList1(new ArrayList<>());
        }

        // Add appropriate item relationships to model.
        if (endpoint != null) {
            addItemElementRelationshipToItem(endpoint, relationship, false);
        }
        addItemElementRelationshipToItem(this, relationship, true);
        
        // set connectors
        relationship.setFirstItemConnector(endpointConnector);
        relationship.setSecondItemConnector(cableConnector);

        return relationship;
    }
    
    public ValidInfo updateCableRelationship(
            ItemElementRelationship cableRelationship, 
            Item itemEndpoint,
            ItemConnector endpointConnector,
            ItemConnector cableConnector,
            String cableEnd) {
        return updateCableRelationship(cableRelationship, itemEndpoint, endpointConnector, cableConnector, cableEnd, this);
    }
    
    public ValidInfo updateCableRelationship(
            ItemElementRelationship cableRelationship, 
            Item itemEndpoint,
            ItemConnector endpointConnector,
            ItemConnector cableConnector,
            String cableEnd,
            CdbEntity connectorPersistenceOwner) {
        
        boolean isValid = true;
        String validStr = "";
        String methodName = "updateCableRelationship() ";
        
        String origCableEnd = cableRelationship.getCableEndDesignation();
        
        if (cableEnd == null) {
            // cable end cannot be null
            isValid = false;
            String msg = "Cable End cannot be null.";
            LOGGER.error(methodName + msg);
            return new ValidInfo(isValid, msg);
            
        } else if (!cableEnd.equals(origCableEnd)) {
            // can't change cable end for primary connection
            if (cableRelationship.isPrimaryCableConnection()) {
                isValid = false;
                String msg = "Cannot change cable end for primary connection.";
                LOGGER.error(methodName + msg);
                return new ValidInfo(isValid, msg);
                
            } else {
                cableRelationship.setCableEndDesignation(cableEnd);
            }
        }
        
        ItemElement origItemElement = cableRelationship.getFirstItemElement();
        ItemElement newItemElement = itemEndpoint.getSelfElement();
        
        // if changing endpoint, remove relationship from original endpoint item
        if ((origItemElement != null)
                && (!newItemElement.getId().equals(origItemElement.getId()))) {
            origItemElement.getItemElementRelationshipList().remove(cableRelationship);
        }
        
        // update endpoint
        cableRelationship.setFirstItemElement(newItemElement);
        
        // delete original connector if updating to new one
        ItemConnector origEndpointConnector = cableRelationship.getFirstItemConnector();
        if ((origEndpointConnector != null) 
                && ((endpointConnector == null) || (!endpointConnector.getConnector().getName().equals(origEndpointConnector.getConnector().getName())))) {
            connectorPersistenceOwner.getDeletedConnectorList().add(origEndpointConnector);
            itemEndpoint.getItemConnectorList().remove(origEndpointConnector);
        }
        
        // update endpoint port
        cableRelationship.setFirstItemConnector(endpointConnector);
        
        // delete original connector if updating to new one
        ItemConnector origCableConnector = cableRelationship.getSecondItemConnector();
        if ((origCableConnector != null)
                && ((cableConnector == null) || (!cableConnector.getConnector().getName().equals(origCableConnector.getConnector().getName())))){
            connectorPersistenceOwner.getDeletedConnectorList().add(origCableConnector);
            this.getItemConnectorList().remove(origCableConnector);
        }
        
        // update cable connector
        cableRelationship.setSecondItemConnector(cableConnector);
        
        return new ValidInfo(isValid, validStr);
    }
    
    @JsonIgnore
    public ItemElementRelationship getPrimaryRelationshipForCableEnd(String cableEnd) {
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList
                = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            // find just the cable relationship items
            RelationshipType cableIerType = getCableConnectionRelationshipType();
            if (cableIerType != null) {
                for (ItemElementRelationship rel : ierList) {
                    if ((rel.getRelationshipType().getName().equals(cableIerType.getName()))
                            && (rel.getCableEndDesignation().equals(cableEnd))
                            && (rel.getLabel().equals(RELATIONSHIP_LABEL_PRIMARY))) {
                        return rel;
                    }
                }
            }
        }
        return null;
    }
    
    public void deleteCableRelationship(ItemElementRelationship cableRelationship) {
        
        // remove relationship from cable design
        getSelfElement().getItemElementRelationshipList1().remove(cableRelationship);
        
        // remove relationship from endpoint device
        cableRelationship.getFirstItemElement().getItemElementRelationshipList().remove(cableRelationship);
        
        // add to deleted relationships list for removal in facade.update()
        getDeletedIerList().add(cableRelationship);
    }

    public void setPrimaryEndpoint(
            Item itemEndpoint,
            ItemConnector endpointConnector,
            ItemConnector cableConnector,
            String cableEnd) {
        
        ItemElementRelationship cableRelationship = getPrimaryRelationshipForCableEnd(cableEnd);
        if (cableRelationship != null) {
            updateCableRelationship(cableRelationship, itemEndpoint, endpointConnector, cableConnector, null);
        } else {
            if (itemEndpoint != null) {
                this.addCableRelationship(itemEndpoint, endpointConnector, cableConnector, cableEnd, true);
            }
        }
    }

    public ValidInfo setEndpointImport(
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName,
            String cableEnd) {
        
        ItemElementRelationship existingRelationship = this.getPrimaryRelationshipForCableEnd(cableEnd);
        CreateInfo endpointInfo = setEndpointImport(
                existingRelationship, itemEndpoint, endpointConnectorName, cableConnectorName, cableEnd, true);
        return endpointInfo.getValidInfo();
    }
    
    public CreateInfo setEndpointImport(
            ItemElementRelationship existingRelationship,
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName,
            String cableEnd,
            boolean isPrimary) {
        return setEndpointImport(existingRelationship, itemEndpoint, endpointConnectorName, cableConnectorName, cableEnd, isPrimary, this);
    }
    
    public CreateInfo setEndpointImport(
            ItemElementRelationship existingRelationship,
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName,
            String cableEnd,
            boolean isPrimary,
            CdbEntity connectorPersistenceOwner) {
        
        boolean isValid = true;
        String validString = "";
        ItemElementRelationship connectionRelationship = existingRelationship;
                
        boolean changedEndpoint = false;
        boolean changedPort = false;
        boolean changedConnector = false;
        boolean changedCableEnd = false;
        
        // validate
        
        if (itemEndpoint == null) {            
            isValid = false;
            validString = "Must specify endpoint machine item.";
        }
        
        if (cableEnd == null) {
            isValid = false;
            validString = "Must specify cable end.";
        }
        
        if (!isValid) {
            return new CreateInfo(connectionRelationship, isValid, validString);
        }
        
        // retrieve existing endpoint and connectors
        ItemDomainMachineDesign origItemEndpoint = null;
        ItemConnector origEndpointConnector = null;
        ItemConnector origCableConnector = null;
        String origCableEnd = null;
        if (existingRelationship != null) {
            if (existingRelationship.getFirstItemElement() != null) {
                origItemEndpoint = 
                        (ItemDomainMachineDesign)existingRelationship.getFirstItem();
                if (itemEndpoint.getId().equals(origItemEndpoint.getId())) {
                    itemEndpoint = origItemEndpoint;
                }
            }
            origEndpointConnector = existingRelationship.getFirstItemConnector();
            origCableConnector = existingRelationship.getSecondItemConnector();
            origCableEnd = existingRelationship.getCableEndDesignation();
            
            // validate cable end change
            if (!cableEnd.equals(origCableEnd)) {
                changedCableEnd = true;
                if (existingRelationship.isPrimaryCableConnection()) {
                    // can't change end for primary connection
                    isValid = false;
                    validString = validString + "Cannot change cable end for primary connection.";
                }
            }
        }
        if (itemEndpoint != origItemEndpoint) {
            changedEndpoint = true;
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
                    endpointConnector = itemEndpoint.getConnectorNamed(endpointConnectorName);
                    if (endpointConnector == null) {
                        isValid = false;
                        validString = validString + "Port: " + endpointConnectorName + " does not exist for specified machine item: " + itemEndpoint.getName() + ".";
                    } else if (endpointConnector.isConnected()) {
                        isValid = false;
                        validString = validString + "Port: " + endpointConnectorName + " is already connected for specified machine item: " + itemEndpoint.getName() + ".";
                    }
                }
            }
        }
        if (endpointConnector != origEndpointConnector) {
            changedPort = true;
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
                    cableConnector = this.getConnectorNamed(cableConnectorName);
                    if (cableConnector == null) {
                        isValid = false;
                        validString = validString + "Cable connector: " + cableConnectorName + " does not exist for specified cable/cable type.";
                    } else if (cableConnector.isConnected()) {
                        isValid = false;
                        validString = validString + "Cable connector: " + cableConnectorName + " is already connected.";
                    } 
                }
                if ((cableConnector != null) && (!cableConnector.getConnector().getCableEndDesignation().equals(cableEnd))) {
                    isValid = false;
                    validString = validString + "Cable end for specified cable connector different than specified cable end.";
                }
            }
        }
        if (cableConnector != origCableConnector) {
            changedConnector = true;
        }
        
        if (isValid) {
            if (existingRelationship == null) {
                connectionRelationship = this.addCableRelationship(
                        itemEndpoint, endpointConnector, cableConnector, cableEnd, isPrimary);
            } else {
                if (changedCableEnd || changedEndpoint || changedPort || changedConnector) {
                    ValidInfo updateInfo = this.updateCableRelationship(
                            connectionRelationship, itemEndpoint, endpointConnector, cableConnector, cableEnd, connectorPersistenceOwner);
                    if (!updateInfo.isValid()) {
                        isValid = false;
                        validString = updateInfo.getValidString();
                    }
                }
            }
        }
        
        return new CreateInfo(connectionRelationship, isValid, validString);
    }
    
    @JsonIgnore
    public List<ItemElementRelationship> getCableRelationshipList() {
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            RelationshipType cableIerType = getCableConnectionRelationshipType();
            if (cableIerType != null) {
                Comparator<ItemElementRelationship> comparator
                        = Comparator
                                .comparing((ItemElementRelationship o) -> o.getCableEndDesignation())
                                .thenComparing(o -> o.getCableEndPrimarySortValue())
                                .thenComparing(o -> o.getFirstItemElement().getParentItem().getName().toLowerCase());
                List<ItemElementRelationship> sortedIerList
                        = ierList.stream()
                                .filter(ier -> (ier.getRelationshipType().getName().equals(cableIerType.getName())))
                                .sorted(comparator)
                                .collect(Collectors.toList());
                return sortedIerList;
            }
        }
        return new ArrayList<>();
    }
    
    @JsonIgnore
    public List<Item> getDevicesForCableEnd(String cableEnd) {
        
        List<Item> deviceList = new ArrayList<>();
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList = selfElement.getItemElementRelationshipList1();
        
        if (ierList != null) {
            
            // find just the cable relationship items
            RelationshipType cableIerType = getCableConnectionRelationshipType();
            
            if (cableIerType != null) {

                Comparator<ItemElementRelationship> comparator
                        = Comparator
                                .comparing((ItemElementRelationship o) -> o.getCableEndPrimarySortValue())
                                .thenComparing(o -> o.getFirstItemElement().getParentItem().getName().toLowerCase());

                List<ItemElementRelationship> sortedIerList
                        = ierList.stream()
//                                .filter(ier -> ier.getRelationshipType().getName().equals(cableIerType.getName()))
                                .filter(ier -> (ier.getRelationshipType().getName().equals(cableIerType.getName())) && (ier.getCableEndDesignation().equals(cableEnd)))
                                .sorted(comparator)
                                .collect(Collectors.toList());

                for (ItemElementRelationship rel : sortedIerList) {
                    Item device = rel.getFirstItemElement().getParentItem();
                    if (!deviceList.contains(device)) {
                        deviceList.add(device);
                    }
                }
            }
        }
        return deviceList;
    }
    
    @JsonIgnore
    public List<Item> getEnd1Devices() {
        return getDevicesForCableEnd(VALUE_CABLE_END_1);
    }
    
    @JsonIgnore
    public String getEnd1DevicesString() {
        return deviceListToString(getEnd1Devices());
    }

    @JsonIgnore
    public List<Item> getEnd2Devices() {
        return getDevicesForCableEnd(VALUE_CABLE_END_2);
    }


    @JsonIgnore
    public String getEnd2DevicesString() {
        return deviceListToString(getEnd2Devices());
    }
    
    @JsonIgnore
    public List<Item> getEndpointList() {
        
        List<Item> deviceList = new ArrayList<>();
        
        List<Item> end1Devices = getEnd1Devices();
        for (Item device : end1Devices) {
            if (!deviceList.contains(device)) {
                deviceList.add(device);
            }
        }
        
        List<Item> end2Devices = getEnd2Devices();
        for (Item device : end2Devices) {
            if (!deviceList.contains(device)) {
                deviceList.add(device);
            }
        }
        
        return deviceList;
    }
    
    private String deviceListToString(List<Item> deviceList) {
        String result = "";
        int count = 0;
        for (Item endpoint : deviceList) {
            count = count + 1;
            result = result + endpoint.getName();
            if (count != deviceList.size()) {
                result = result + endpointsSeparator;
            }
        }
        return result;
    }
    
    /**
     * Returns a string containing the cables endpoints for display.
     */
    @JsonIgnore
    public String getEndpointsString() {
        return deviceListToString(this.getEndpointList());
    }

    @JsonIgnore
    public String getPortForEndpoint(String cableEnd) {
        ItemElementRelationship cableRelationship = getPrimaryRelationshipForCableEnd(cableEnd);
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
    
    @JsonIgnore
    public String getConnectorForEndpoint(String cableEnd) {
        ItemElementRelationship cableRelationship = getPrimaryRelationshipForCableEnd(cableEnd);
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
    
    public void clearCableConnectors() {
        
        // null out connectors used in cable relationships
        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList
                = selfElement.getItemElementRelationshipList1();
        if (ierList != null) {
            // find just the cable relationship items
            RelationshipType cableIerType = getCableConnectionRelationshipType();
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
        if (externalCableName == null) {
            externalCableName = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_EXT_CABLE_NAME_KEY);
        }
        return externalCableName;
    }

    public void setExternalCableName(String n) throws CdbException {
        externalCableName = n;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_EXT_CABLE_NAME_KEY, n);
    }

    @JsonIgnore
    public String getImportCableId() throws CdbException {
        if (importCableId == null) {
            importCableId = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_IMPORT_CABLE_ID_KEY);
        }
        return importCableId;
    }

    public void setImportCableId(String id) throws CdbException {
        importCableId = id;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_IMPORT_CABLE_ID_KEY, id);
    }

    @JsonIgnore
    public String getAlternateCableId() throws CdbException {
        if (alternateCableId == null) {
            alternateCableId = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ALT_CABLE_ID_KEY);
        }
        return alternateCableId;
    }

    public void setAlternateCableId(String id) throws CdbException {
        alternateCableId = id;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ALT_CABLE_ID_KEY, id);
    }

    @JsonIgnore
    public String getLegacyQrId() throws CdbException {
        if (legacyQrId == null) {
            legacyQrId = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LEGACY_QR_ID_KEY);
        }
        return legacyQrId;
    }

    public void setLegacyQrId(String id) throws CdbException {
        legacyQrId = id;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LEGACY_QR_ID_KEY, id);
    }

    @JsonIgnore
    public String getLaying() throws CdbException {
        if (laying == null) {
            laying = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LAYING_KEY);
        }
        return laying;
    }

    public void setLaying(String l) throws CdbException {
        laying = l;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_LAYING_KEY, l);
    }

    @JsonIgnore
    public String getVoltage() throws CdbException {
        if (voltage == null) {
            voltage = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_VOLTAGE_KEY);
        }
        return voltage;
    }

    public void setVoltage(String v) throws CdbException {
        voltage = v;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_VOLTAGE_KEY, v);
    }

    @JsonIgnore
    public String getRoutedLength() throws CdbException {
        if (routedLength == null) {
            routedLength = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTED_LENGTH_KEY);
        }
        return routedLength;
    }

    public void setRoutedLength(String length) throws CdbException {
        routedLength = length;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTED_LENGTH_KEY, length);
    }

    @JsonIgnore
    public String getRoute() throws CdbException {
        if (route == null) {
            route = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTE_KEY);
        }
        return route;
    }

    public void setRoute(String route) throws CdbException {
        this.route = route;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_ROUTE_KEY, route);
    }

    @JsonIgnore
    public String getNotes() throws CdbException {
        if (notes == null) {
            notes = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_NOTES_KEY);
        }
        return notes;
    }

    public void setNotes(String notes) throws CdbException {
        this.notes = notes;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_NOTES_KEY, notes);
    }

    public Item getAssignedItem() {
        ItemElement selfElement = getSelfElement();
        if (selfElement == null) {
            return null;
        } else {
            return selfElement.getContainedItem2();
        }
    }

    public void setAssignedItem(Item assignedItem) {
        ItemElement selfElement = this.getSelfElement();
        selfElement.setContainedItem2(assignedItem);
    }

    public void setCatalogItem(ItemDomainCableCatalog itemCableCatalog) {
        setAssignedItem(itemCableCatalog);
    }

    public ItemDomainCableCatalog getCatalogItem() {
        Item assignedItem = getAssignedItem();
        ItemDomainCableCatalog catalogItem = null;
        if (assignedItem == null) {
            return null;
        } else if (assignedItem instanceof ItemDomainCableInventory) {
            catalogItem = ((ItemDomainCableInventory) assignedItem).getCatalogItem();
        } else if (assignedItem instanceof ItemDomainCableCatalog) {
            catalogItem = (ItemDomainCableCatalog) assignedItem;
        }
        return catalogItem;
    }

    @JsonIgnore
    public String getCatalogItemString() {
        Item iCatalog = this.getCatalogItem();
        if (iCatalog != null) {
            return iCatalog.getName();
        } else {
            return "";
        }
    }

    @JsonIgnore
    public Map<String,String> getCatalogItemAttributeMap() throws CdbException {
        Item catalogItem = getCatalogItem();
        if (catalogItem != null) {
            return catalogItem.getAttributeMap();            
        } else {
            return null;
        }
    }
    
    public void setInventoryItem(ItemDomainCableInventory inventoryItem) {
        setAssignedItem(inventoryItem);
    }

    public ItemDomainCableInventory getInventoryItem() {
        Item assignedItem = getAssignedItem();
        ItemDomainCableInventory inventoryItem = null;
        if (assignedItem == null) {
            return null;
        } else if (assignedItem instanceof ItemDomainCableInventory) {
            inventoryItem = (ItemDomainCableInventory) assignedItem;
        }
        return inventoryItem;
    }

    @JsonIgnore
    public String getInventoryItemString() {
        ItemDomainCableInventory assignedInventory = this.getInventoryItem();
        if (assignedInventory != null) {
            return assignedInventory.getName();
        } else {
            return "";
        }
    }
    
    public void setImportAssignedInventoryTag(String tag) {
        importAssignedInventoryTag = tag;
    }
    
    @JsonIgnore
    public String getImportAssignedInventoryTag() {
        if (importAssignedInventoryTag == null) {
            importAssignedInventoryTag = getInventoryItemString();
        }
        return importAssignedInventoryTag;
    }

    public boolean isIsHoused() {
        ItemElement selfElement = getSelfElement();
        return selfElement.getIsHoused();
    }

    public void setIsHoused(boolean isHoused) {
        ItemElement selfElement = getSelfElement();
        selfElement.setIsHoused(isHoused);
    }
    
    @JsonIgnore
    public String getInstalledStatusString() {
        if (getInventoryItem() != null) {
            if (isIsHoused()) {
                return "installed";
            } else {
                return "planned";
            }
        } else {
            return null;
        }
    }

    @JsonIgnore
    public Boolean getImportIsInstalled() {
        if (!importIsInstalledLoaded) {
            ItemDomainCableInventory assignedInventory = getInventoryItem();
            if (assignedInventory != null) {
                importIsInstalled = isIsHoused();
            } else {
                importIsInstalled = null;
            }
            importIsInstalledLoaded = true;
        }
        return importIsInstalled;
    }

    public void setImportIsInstalled(Boolean isInstalled) {
        this.importIsInstalled = isInstalled;
    }

    @JsonIgnore
    public Item getPrimaryEndpoint(String cableEnd) {
        ItemElementRelationship cableRelationship = getPrimaryRelationshipForCableEnd(cableEnd);
        if (cableRelationship != null) {
            return cableRelationship.getFirstItemElement().getParentItem();
        } else {
            return null;
        }
    }

    @JsonIgnore
    public Item getEndpoint1() {
        return getPrimaryEndpoint(VALUE_CABLE_END_1);
    }

    @JsonIgnore
    public String getEndpoint1String() {
        Item iEndpoint1 = this.getEndpoint1();
        if (iEndpoint1 != null) {
            return iEndpoint1.getName();
        } else {
            return "";
        }
    }
    
    @JsonIgnore
    public Map<String,String> getEndpoint1AttributeMap() throws CdbException {
        Item endpoint = getEndpoint1();
        if (endpoint != null) {
            return endpoint.getAttributeMap();
        } else {
            return null;
        }
    }

    public void setEndpoint1(Item itemEndpoint) {
        setPrimaryEndpoint(itemEndpoint, null, null, VALUE_CABLE_END_1);
    }

    public ValidInfo setEndpoint1Import(
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName) {
        
        return setEndpointImport(itemEndpoint, endpointConnectorName, cableConnectorName, VALUE_CABLE_END_1);
    }
    
    @JsonIgnore
    public String getEndpoint1Connector() {
        return getConnectorForEndpoint(VALUE_CABLE_END_1);
    }
    
    @JsonIgnore
    public String getEndpoint1Port() {
        return getPortForEndpoint(VALUE_CABLE_END_1);
    }
    
    @JsonIgnore
    public Item getEndpoint1ItemImport() {
        if (endpoint1ItemImport == null) {
            endpoint1ItemImport = getEndpoint1();
        }
        return endpoint1ItemImport;
    }
    
    @JsonIgnore
    public String getEndpoint1ItemStringImport() {
        Item item = getEndpoint1ItemImport();
        if (item != null) {
            return item.getName();
        } else {
            return null;
        }
    }
    
    public void setEndpoint1ItemImport(Item item) {
        endpoint1ItemImport = item;
    }

    @JsonIgnore
    public String getEndpoint1PortImport() {
        if (endpoint1PortImport == null) {
            endpoint1PortImport = getEndpoint1Port();
        }
        return endpoint1PortImport;
    }
    
    public void setEndpoint1PortImport(String port) {
        endpoint1PortImport = port;
    }

    @JsonIgnore
    public String getEndpoint1ConnectorImport() {
        if (endpoint1ConnectorImport == null) {
            endpoint1ConnectorImport = getEndpoint1Connector();
        }
        return endpoint1ConnectorImport;
    }
    
    public void setEndpoint1ConnectorImport(String connector) {
        endpoint1ConnectorImport = connector;
    }

    @JsonIgnore
    public Item getEndpoint2() {
        return getPrimaryEndpoint(VALUE_CABLE_END_2);
    }

    @JsonIgnore
    public String getEndpoint2String() {
        Item iEndpoint2 = this.getEndpoint2();
        if (iEndpoint2 != null) {
            return iEndpoint2.getName();
        } else {
            return "";
        }
    }

    @JsonIgnore
    public Map<String, String> getEndpoint2AttributeMap() throws CdbException {
        Item endpoint = getEndpoint2();
        if (endpoint != null) {
            return endpoint.getAttributeMap();
        } else {
            return null;
        }
    }

    public void setEndpoint2(Item itemEndpoint) {
        setPrimaryEndpoint(itemEndpoint, null, null, VALUE_CABLE_END_2);
    }
    
    public ValidInfo setEndpoint2Import(
            ItemDomainMachineDesign itemEndpoint,
            String endpointConnectorName,
            String cableConnectorName) {
        
        return setEndpointImport(itemEndpoint, endpointConnectorName, cableConnectorName, VALUE_CABLE_END_2);
    }

    @JsonIgnore
    public String getEndpoint2Port() {
        return getPortForEndpoint(VALUE_CABLE_END_2);
    }

    @JsonIgnore
    public String getEndpoint2Connector() {
        return getConnectorForEndpoint(VALUE_CABLE_END_2);
    }

    @JsonIgnore
    public Item getEndpoint2ItemImport() {
        if (endpoint2ItemImport == null) {
            endpoint2ItemImport = getEndpoint2();
        }
        return endpoint2ItemImport;
    }
    
    @JsonIgnore
    public String getEndpoint2ItemStringImport() {
        Item item = getEndpoint2ItemImport();
        if (item != null) {
            return item.getName();
        } else {
            return null;
        }
    }
    
    public void setEndpoint2ItemImport(Item item) {
        endpoint2ItemImport = item;
    }

    @JsonIgnore
    public String getEndpoint2PortImport() {
        if (endpoint2PortImport == null) {
            endpoint2PortImport = getEndpoint2Port();
        }
        return endpoint2PortImport;
    }
    
    public void setEndpoint2PortImport(String port) {
        endpoint2PortImport = port;
    }

    @JsonIgnore
    public String getEndpoint2ConnectorImport() {
        if (endpoint2ConnectorImport == null) {
            endpoint2ConnectorImport = getEndpoint2Connector();
        }
        return endpoint2ConnectorImport;
    }
    
    public void setEndpoint2ConnectorImport(String connector) {
        endpoint2ConnectorImport = connector;
    }

    @JsonIgnore
    public String getEndpoint1Description() throws CdbException {

        if (endpoint1Description == null) {
            endpoint1Description = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_DESCRIPTION_KEY);
        }
        return endpoint1Description;
    }

    public void setEndpoint1Description(String description) throws CdbException {
        this.endpoint1Description = description;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_DESCRIPTION_KEY, description);
    }
    
    @JsonIgnore
    public String getEndpoint1Route() throws CdbException {
        if (endpoint1Route == null) {
            endpoint1Route = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_ROUTE_KEY);
        }
        return endpoint1Route;
    }

    public void setEndpoint1Route(String route) throws CdbException {
        endpoint1Route = route;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_ROUTE_KEY, route);
    }
    
    @JsonIgnore
    public String getEndpoint1Pinlist() throws CdbException {
        if (endpoint1Pinlist == null) {
            endpoint1Pinlist = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_PINLIST_KEY);
        }
        return endpoint1Pinlist;
    }

    public void setEndpoint1Pinlist(String pinlist) throws CdbException {
        endpoint1Pinlist = pinlist;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_PINLIST_KEY, pinlist);
    }
    
    @JsonIgnore
    public String getEndpoint1EndLength() throws CdbException {
        if (endpoint1EndLength == null) {
            endpoint1EndLength = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_ENDLENGTH_KEY);
        }
        return endpoint1EndLength;
    }

    public void setEndpoint1EndLength(String endLength) throws CdbException {
        endpoint1EndLength = endLength;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_ENDLENGTH_KEY, endLength);
    }
    
    @JsonIgnore
    public String getEndpoint1Termination() throws CdbException {
        if (endpoint1Termination == null) {
            endpoint1Termination = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_TERMINATION_KEY);
        }
        return endpoint1Termination;
    }

    public void setEndpoint1Termination(String termination) throws CdbException {
        endpoint1Termination = termination;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_TERMINATION_KEY, termination);
    }
    
    @JsonIgnore
    public String getEndpoint1Notes() throws CdbException {
        if (endpoint1Notes == null) {
            endpoint1Notes = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_NOTES_KEY);
        }
        return endpoint1Notes;
    }

    public void setEndpoint1Notes(String notes) throws CdbException {
        endpoint1Notes = notes;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_NOTES_KEY, notes);
    }
    
    @JsonIgnore
    public String getEndpoint1Drawing() throws CdbException {
        if (endpoint1Drawing == null) {
            endpoint1Drawing = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_DRAWING_KEY);
        }
        return endpoint1Drawing;
    }

    public void setEndpoint1Drawing(String drawing) throws CdbException {
        endpoint1Drawing = drawing;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END1_DRAWING_KEY, drawing);
    }

    @JsonIgnore
    public String getEndpoint2Description() throws CdbException {

        if (endpoint2Description == null) {
            endpoint2Description = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_DESCRIPTION_KEY);
        }
        return endpoint2Description;
    }

    public void setEndpoint2Description(String description) throws CdbException {
        this.endpoint2Description = description;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_DESCRIPTION_KEY, description);
    }
    
    @JsonIgnore
    public String getEndpoint2Route() throws CdbException {
        if (endpoint2Route == null) {
            endpoint2Route = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_ROUTE_KEY);
        }
        return endpoint2Route;
    }

    public void setEndpoint2Route(String route) throws CdbException {
        endpoint2Route = route;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_ROUTE_KEY, route);
    }
    
    @JsonIgnore
    public String getEndpoint2Pinlist() throws CdbException {
        if (endpoint2Pinlist == null) {
            endpoint2Pinlist = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_PINLIST_KEY);
        }
        return endpoint2Pinlist;
    }

    public void setEndpoint2Pinlist(String pinlist) throws CdbException {
        endpoint2Pinlist = pinlist;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_PINLIST_KEY, pinlist);
    }
    
    @JsonIgnore
    public String getEndpoint2EndLength() throws CdbException {
        if (endpoint2EndLength == null) {
            endpoint2EndLength = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_ENDLENGTH_KEY);
        }
        return endpoint2EndLength;
    }

    public void setEndpoint2EndLength(String endLength) throws CdbException {
        endpoint2EndLength = endLength;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_ENDLENGTH_KEY, endLength);
    }
    
    @JsonIgnore
    public String getEndpoint2Termination() throws CdbException {
        if (endpoint2Termination == null) {
            endpoint2Termination = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_TERMINATION_KEY);
        }
        return endpoint2Termination;
    }

    public void setEndpoint2Termination(String termination) throws CdbException {
        endpoint2Termination = termination;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_TERMINATION_KEY, termination);
    }
    
    @JsonIgnore
    public String getEndpoint2Notes() throws CdbException {
        if (endpoint2Notes == null) {
            endpoint2Notes = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_NOTES_KEY);
        }
        return endpoint2Notes;
    }

    public void setEndpoint2Notes(String notes) throws CdbException {
        endpoint2Notes = notes;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_NOTES_KEY, notes);
    }
    
    @JsonIgnore
    public String getEndpoint2Drawing() throws CdbException {
        if (endpoint2Drawing == null) {
            endpoint2Drawing = getCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_DRAWING_KEY);
        }
        return endpoint2Drawing;
    }

    public void setEndpoint2Drawing(String drawing) throws CdbException {
        endpoint2Drawing = drawing;
        setCoreMetadataPropertyFieldValue(CABLE_DESIGN_PROPERTY_END2_DRAWING_KEY, drawing);
    }
    
    @JsonIgnore
    public String getEndpoint1Location() {
        ItemDomainMachineDesign endpoint = (ItemDomainMachineDesign) getEndpoint1();
        if (endpoint == null) {
            return null;
        } else {
            // must manually load location details before accessing
            LocatableItemController.getInstance().getLocationStringForItem(endpoint);
            Item location = endpoint.getActiveLocation();
            if (location != null) {
                return location.getName();
            } else {
                return null;
            }
        }
    }

    @JsonIgnore
    public String getEndpoint2Location() {
        ItemDomainMachineDesign endpoint = (ItemDomainMachineDesign) getEndpoint2();
        if (endpoint == null) {
            return null;
        } else {
            // must manually load location details before accessing
            LocatableItemController.getInstance().getLocationStringForItem(endpoint);
            Item location = endpoint.getActiveLocation();
            if (location != null) {
                return location.getName();
            } else {
                return null;
            }
        }
    }

    @Override
    public SearchResult createSearchResultInfo(Pattern searchPattern) {
        SearchResult searchResult = super.createSearchResultInfo(searchPattern);
        
        List<ItemElementRelationship> cableRelationshipList = this.getCableRelationshipList();
        
        for (int i =0; i < cableRelationshipList.size(); i++) {
            ItemElementRelationship cableRelationship = cableRelationshipList.get(i);
            Item firstItem = cableRelationship.getFirstItem();
            String cableEndName = firstItem.getName();
            searchResult.doesValueContainPattern("endpoint " + (i +1), cableEndName, searchPattern);
        }
        
        return searchResult; 
    }
    
}
