 /*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named("itemDomainCableController")
@SessionScoped
public class ItemDomainCableController extends ItemController {
    
    private final String DOMAIN_TYPE_NAME = ItemDomainName.cable.getValue();
    
    private final static String CABLE_INTERNAL_PROPERTY_TYPE = "cable_internal_property_type"; 
    private final static String CABLE_PROPERTY_LENGTH_KEY = "length"; 
    private final static String CABLE_PROPERTY_LENGTH_UNIT_KEY = "length unit"; 
    private final static String CABLE_PROPERTY_DIRECT_KEY = "is direct"; 
    private final static List<String> CABLE_ALLOWED_LENGTH_UNITS = Arrays.asList("feet", "meters"); 
    
    private boolean setCableLengthAttribute; 
    
    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    
    public static ItemDomainCableController getInstance() {
        return (ItemDomainCableController) SessionUtility.findBean("itemDomainCableController");
    } 

    @Override
    public void setCurrent(Item current) {
        super.setCurrent(current); 
        setCableLengthAttribute = false; 
        
    }

    @Override
    protected Item createEntityInstance() {
        Item item = super.createEntityInstance(); 
        setCurrent(item);
        
        // Create the two connectors
        item.setItemConnectorList(new ArrayList<>());
        this.prepareAddItemConnector(item);
        this.prepareAddItemConnector(item);
        
        // Add cable internal property type
        PropertyType propertyType = propertyTypeFacade.findByName(CABLE_INTERNAL_PROPERTY_TYPE);
        
        if (propertyType == null) {
            // Will happen only once. 
            propertyType = createRequiredPropertyTypeForCables();
        }
        
        item.setPropertyValueList(new ArrayList<>());
        preparePropertyTypeValueAdd(propertyType);
        
        return item; 
    }
    
    public void destroyCableConnection(Item cableItem) {
        // Cascade nature of item relationship list will remove all connection relationships automatically. 
        this.setCurrent(cableItem);
        this.destroy(); 
    }

    @Override
    protected void checkItemUniqueness(Item item) throws CdbException {
        // Cables are only unique by primary key (id). 
    }
    
    public static List<ItemElementRelationship> getConnectionRelationshipList(Item item, boolean second) {
        List<ItemElementRelationship> itemElementRelationshipList;
        
        if (second) {
            itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList1(); 
        }else {
            itemElementRelationshipList = item.getSelfElement().getItemElementRelationshipList();
        }
        List<ItemElementRelationship> resultingList = new ArrayList<>(); 
        
        for(ItemElementRelationship ier : itemElementRelationshipList) {
            if (ier.getRelationshipType().getName().equals(ItemElementRelationshipTypeNames.itemCableConnection.getValue())) {
                resultingList.add(ier);
            }
        }
        
        return resultingList;
    }
    
    private PropertyType createRequiredPropertyTypeForCables() {
        PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
        PropertyType propertyType = propertyTypeController.createEntityInstance();
        propertyType.setIsInternal(true);
        propertyType.setName(CABLE_INTERNAL_PROPERTY_TYPE);
        propertyTypeController.setCurrent(propertyType);
        propertyTypeController.create(true, false); 
        return propertyType; 
    }
    
    private static PropertyValue getInternalCablePropertyValue(Item cableItem) {
        List<PropertyValue> propertyValueList = cableItem.getPropertyValueList(); 
        for (PropertyValue propertyValue: propertyValueList) {
            if (propertyValue.getPropertyType().getName().equals(CABLE_INTERNAL_PROPERTY_TYPE)) {
                return propertyValue; 
            }
        }
        return null; 
    }
    
    public double getLengthForCurrent() {
        return getLengthForItem(getCurrent()); 
    }
    
    public double getLengthForItem(Item item) {
        PropertyValue propertyValue = getInternalCablePropertyValue(item); 
        
        if (propertyValue != null) {
            PropertyMetadata value = propertyValue.getPropertyMetadataForKey(CABLE_PROPERTY_LENGTH_KEY);
            if (value != null) {
                String metadataValue = value.getMetadataValue(); 
                return Double.parseDouble(metadataValue); 
            }
        }
        
        return -1;
    }
    
    public void setLengthForCurrent(double length) {
        PropertyValue propertyValue = getInternalCablePropertyValue(getCurrent()); 
        
        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_LENGTH_KEY, length + "");            
        }        
    }
    
    public boolean getIsDirectConnectionForCurrent() {
        return getIsDirectConnectionForItem(getCurrent()); 
    }
    
    public static boolean getIsDirectConnectionForItem(Item item) {
        PropertyValue propertyValue = getInternalCablePropertyValue(item); 
        
        if (propertyValue != null) {
            PropertyMetadata value = propertyValue.getPropertyMetadataForKey(CABLE_PROPERTY_DIRECT_KEY);
            
            if (value != null) {
                String metadataValue = value.getMetadataValue();
                return Boolean.parseBoolean(metadataValue);
            }
        }
        
        return false; 
    }
    
    public void setIsDirectConnectionForCurrent(boolean isDirectValue) {
        PropertyValue propertyValue = getInternalCablePropertyValue(getCurrent()); 
        
        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_DIRECT_KEY, Boolean.toString(isDirectValue));
        }
    }
    
    public String getUnitForCurrent() {
        return getUnitForItem(getCurrent()); 
    }
    
    public String getUnitForItem(Item item) {
        PropertyValue propertyValue = getInternalCablePropertyValue(item); 
        
        if (propertyValue != null) {
            PropertyMetadata value = propertyValue.getPropertyMetadataForKey(CABLE_PROPERTY_LENGTH_UNIT_KEY);
            if (value != null) {
                String metadataValue = value.getMetadataValue(); 
                return metadataValue; 
            }
        }
        
        return ""; 
    }
    
    public void setUnitForCurrent(String unit) {
        PropertyValue propertyValue = getInternalCablePropertyValue(getCurrent()); 
        
        if (propertyValue != null) {
            propertyValue.setPropertyMetadataValue(CABLE_PROPERTY_LENGTH_UNIT_KEY, unit);
        }
    }
    
    public String getConnectedViaString(Item item) {
        String result = "";
        if (item != null) {
            if (getIsDirectConnectionForItem(item)) {
                result = "Direct Connection"; 
            } else {
                List <ItemConnector> itemConnectorList = item.getItemConnectorList(); 
                if (itemConnectorList.size() == 2) {
                    String firstConnectorType = itemConnectorList.get(0).getConnector().getConnectorType().getName(); 
                    String secondConnectorType = itemConnectorList.get(1).getConnector().getConnectorType().getName();

                    if (firstConnectorType.equals(secondConnectorType)) {
                        result += firstConnectorType + " "; 
                    } else {
                        result += firstConnectorType + " to " + secondConnectorType + " "; 
                    }
                }

                result += "Cable"; 

                double length = getLengthForItem(item); 
                if (length > 0) {
                    String unit = getUnitForItem(item);
                    result += " (" + length + " " + unit + ")"; 
                }
            }            
        }        
        
        return result; 
    }

    public boolean isSetCableLengthAttribute() {
        return setCableLengthAttribute;
    }

    public void enableCableLengthAttribute(boolean setCableLengthAttribute) {
        this.setCableLengthAttribute = setCableLengthAttribute;
    }

    public List<String> getAllowedLengthUnits() {
        return CABLE_ALLOWED_LENGTH_UNITS;
    }

    @Override
    public String getDefaultDomainName() {
        return DOMAIN_TYPE_NAME; 
    }

    @Override
    public boolean getEntityDisplayItemConnectors() {
        return true; 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier1() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemName() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemType() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayQrId() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        return false;
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        return false; 
    }
    
    @Override
    public boolean getEntityDisplayItemMemberships() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        return false; 
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        return false; 
    }

    @Override
    public String getItemIdentifier1Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdentifier2Title() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemsDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDerivedFromItemTitle() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStyleName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedFromDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDefaultDomainDerivedToDomainName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEntityTypeName() {
        return "cable";
    }
    
}
