 /*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.constants.ItemDomainName;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.ItemConnector;
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
    
    private final String CABLE_INTERNAL_PROPERTY_TYPE = "cable_internal_property_type"; 
    private final String CABLE_PROPERTY_LENGTH_KEY = "length"; 
    private final String CABLE_PROPERTY_LENGTH_UNIT_KEY = "length unit"; 
    private final List<String> CABLE_ALLOWED_LENGTH_UNITS = Arrays.asList("feet", "meters"); 
    
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
    
    private PropertyType createRequiredPropertyTypeForCables() {
        PropertyTypeController propertyTypeController = PropertyTypeController.getInstance();
        PropertyType propertyType = propertyTypeController.createEntityInstance();
        propertyType.setIsInternal(true);
        propertyType.setName(CABLE_INTERNAL_PROPERTY_TYPE);
        propertyTypeController.setCurrent(propertyType);
        propertyTypeController.create(true, false); 
        return propertyType; 
    }
    
    private PropertyValue getInternalCablePropertyValue(Item cableItem) {
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
            List<ItemConnector> itemConnectorList = item.getItemConnectorList(); 
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemIdentifier2() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemCategory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayDerivedFromItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayQrId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemGallery() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemLogs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemSources() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemElements() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemsDerivedFromItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemMemberships() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getEntityDisplayItemEntityTypes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
