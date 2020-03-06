/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.entities;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.controllers.ItemCategoryController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableCatalogController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainCableDesignController;
import gov.anl.aps.cdb.portal.controllers.ItemDomainMachineDesignController;
import gov.anl.aps.cdb.portal.controllers.RelationshipTypeController;
import gov.anl.aps.cdb.portal.model.db.beans.RelationshipTypeFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author cmcchesney
 */
@Entity
@DiscriminatorValue(value = ItemDomainName.CABLE_DESIGN_ID + "")
public class ItemDomainCableDesign extends Item {
    
    private transient String laying = null;
    private transient String voltage = null;
    private transient String team = null;

    public final static String CABLE_DESIGN_INTERNAL_PROPERTY_TYPE = "cable_design_internal_property_type"; 
    private final static String CABLE_DESIGN_PROPERTY_LAYING_KEY = "laying"; 
    private final static String CABLE_DESIGN_PROPERTY_VOLTAGE_KEY = "voltage"; 

    private static final String endpointsSeparator = " | ";

    @Override
    public Item createInstance() {
        return new ItemDomainCableDesign();
    }

    public List<Item> getEndpointList() {
        {
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
                            map(ier -> ier.getFirstItemElement().getParentItem()).
                            collect(Collectors.toList());
                }
            }

            return null;
        }
    }
    
    private RelationshipType getCableConnectionRelationshipType() {
        RelationshipType relationshipType = 
                RelationshipTypeFacade.getInstance().findByName(
                        ItemElementRelationshipTypeNames.itemCableConnection.getValue());
        if (relationshipType == null) {
            RelationshipTypeController controller = RelationshipTypeController.getInstance();
            String name = ItemElementRelationshipTypeNames.itemCableConnection.getValue();
            relationshipType = controller.createRelationshipTypeWithName(name);
        }
        return relationshipType;
    }    

    /**
     * Creates ItemElementRelationship for the 2 specified items.
     * @param item Machine design item for cable endpoint.
     * @return New instance of ItemElementRelationshipo for specified items.
     */
    private ItemElementRelationship createRelationship(Item item) {
        
        ItemElementRelationship itemElementRelationship = new ItemElementRelationship();
        itemElementRelationship.setFirstItemElement(item.getSelfElement());
        itemElementRelationship.setSecondItemElement(this.getSelfElement());

        RelationshipType cableConnectionRelationshipType = getCableConnectionRelationshipType();
        itemElementRelationship.setRelationshipType(cableConnectionRelationshipType);

        return itemElementRelationship;
    }

    /**
     * Adds specified relationship for specified item.
     * @param item Item to add relationship for.
     * @param ier Relationship to add.
     * @param secondItem True if the item is the second item in the relationship.
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
    
    private void addCableRelationship(Item endpoint) {       
        // create relationships from cable to endpoints
        ItemElementRelationship relationship = createRelationship(endpoint);

        // Create list for cable's relationships. 
        ItemElement selfElement = this.getSelfElement();
        if (selfElement.getItemElementRelationshipList1() == null) {
            selfElement.setItemElementRelationshipList1(new ArrayList<>());
        }

        // Add appropriate item relationships to model.
        addItemElementRelationshipToItem(endpoint, relationship, false);
        addItemElementRelationshipToItem(this, relationship, true);
    }
    
    public void setEndpoint1(Item itemEndpoint1) {
        this.addCableRelationship(itemEndpoint1);
    }
    
    public void setEndpoint1Id(String id) {
        Item itemEndpoint1 = ItemDomainMachineDesignController.getInstance().findById(Integer.valueOf(id));
        if (itemEndpoint1 != null) {
            setEndpoint1(itemEndpoint1);
        }
    }
    
    public void setEndpoint2(Item itemEndpoint2) {
        this.addCableRelationship(itemEndpoint2);
    }
    
    public void setEndpoint2Id(String id) {
        Item itemEndpoint2 = ItemDomainMachineDesignController.getInstance().findById(Integer.valueOf(id));
        if (itemEndpoint2 != null) {
            setEndpoint2(itemEndpoint2);
        }
    }
    
    /**
     * Updates oldEndpoint to newEndpoint.
     * @param oldEndpoint
     * @param newEndpoint 
     */
    public Boolean updateEndpoint(Item oldEndpoint, Item newEndpoint) {

        ItemElement selfElement = this.getSelfElement();
        List<ItemElementRelationship> ierList = selfElement.getItemElementRelationshipList1();
        
        if (ierList != null) {

            RelationshipType cableIerType
                    = RelationshipTypeFacade.getInstance().findByName(
                            ItemElementRelationshipTypeNames.itemCableConnection.getValue());

            // find cable relationship for old endpoint
            ItemElementRelationship cableRelationship = ierList.stream()
                    .filter(ier -> (ier.getRelationshipType().getName().equals(cableIerType.getName()))
                    && (ier.getFirstItemElement().equals(oldEndpoint.getSelfElement())))
                    .findAny()
                    .orElse(null);
            
            // update cable relationship to new endpoint
            if (cableRelationship != null) {
                cableRelationship.setFirstItemElement(newEndpoint.getSelfElement());
                // null out connector too, for when we add support for port-level connections
                cableRelationship.setFirstItemConnector(null);
            }
        }
        
        return true;
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

    public Item getEndpoint1() {
        List<Item> iList = this.getEndpointList();
        if (iList.size() > 0) {
            return iList.get(0);
        }
        else {
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

    public Item getEndpoint2() {
        List<Item> iList = this.getEndpointList();
        if (iList.size() > 1) {
            return iList.get(1);
        }
        else {
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
    
    public void setCatalogItem(Item itemCableCatalog) {
        // "assign" catalog item to cable design
        ItemElement selfElement = this.getSelfElement();
        selfElement.setContainedItem2(itemCableCatalog);
    }
    
    public void setCatalogItemId(String catalogItemId) {
        Item catalogItem = ItemDomainCableCatalogController.getInstance().findById(Integer.valueOf(catalogItemId));
        if (catalogItem != null) {
            setCatalogItem(catalogItem);
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
    
    private PropertyValue getInternalCableDesignPropertyValue() {
        List<PropertyValue> propertyValueList = getPropertyValueList(); 
        for (PropertyValue propertyValue: propertyValueList) {
            if (propertyValue.getPropertyType().getName().equals(CABLE_DESIGN_INTERNAL_PROPERTY_TYPE)) {
                return propertyValue; 
            }
        }
        return null; 
    }
    
    private void setInternalCableDesignPropertyFieldValue(String key, String value) {
        
        PropertyValue propertyValue = getInternalCableDesignPropertyValue();

        if (propertyValue == null) {
            propertyValue = ItemDomainCableDesignController.getInstance().prepareInternalCableDesignPropertyValue(this);
        } 
            
        propertyValue.setPropertyMetadataValue(key, value);
    }
    
    public String getLaying() {
        if (laying == null) {
            PropertyValue propertyValue = getInternalCableDesignPropertyValue();
            if (propertyValue == null) {
                laying = "";
            } else {
                laying = propertyValue.getPropertyMetadataValueForKey(CABLE_DESIGN_PROPERTY_LAYING_KEY);
            }
        }
        return laying;
    }

    public void setLaying(String l) {
        laying = l;
        setInternalCableDesignPropertyFieldValue(CABLE_DESIGN_PROPERTY_LAYING_KEY, l);
    }

    public String getVoltage() {
        if (voltage == null) {
            PropertyValue propertyValue = getInternalCableDesignPropertyValue();
            if (propertyValue == null) {
                voltage = "";
            } else {
                voltage = propertyValue.getPropertyMetadataValueForKey(CABLE_DESIGN_PROPERTY_LAYING_KEY);
            }
        }
        return voltage;
    }

    public void setVoltage(String v) {
        voltage = v;
        setInternalCableDesignPropertyFieldValue(CABLE_DESIGN_PROPERTY_LAYING_KEY, v);
    }

    public String getTeam() {
        if (team == null) {
            team = this.getItemCategoryString();
        }
        return team;
    }
    
    public void setTeamId(String categoryId) {
        ItemCategory category = ItemCategoryController.getInstance().findById(Integer.valueOf(categoryId));
        if (category != null) {
            List<ItemCategory> categoryList = new ArrayList<>();
            categoryList.add(category);
            this.setItemCategoryList(categoryList);
            team = this.getItemCategoryString();
        }
    }
    
}
