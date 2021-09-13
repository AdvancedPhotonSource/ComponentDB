/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.common.exceptions.InvalidArgument;
import gov.anl.aps.cdb.portal.constants.EntityTypeName;
import gov.anl.aps.cdb.portal.constants.ItemElementRelationshipTypeNames;
import gov.anl.aps.cdb.portal.constants.SystemPropertyTypeNames;
import gov.anl.aps.cdb.portal.model.db.entities.EntityType;
import gov.anl.aps.cdb.portal.model.db.entities.ItemDomainMachineDesign;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author darek
 */
public class ItemDomainMachineDesignControlControllerUtility extends ItemDomainMachineDesignRelationshipBaseControllerUtility {
    
    private static final Logger logger = LogManager.getLogger(ItemDomainMachineDesignControlControllerUtility.class.getName());            
        
    @Override
    public List<ItemDomainMachineDesign> getItemList() {
        return itemFacade.getTopLevelMachineDesignControl();
    }    

    public void assignControlAttributes(ItemDomainMachineDesign newInventory, UserInfo sessionUser) {
        String controletn = EntityTypeName.control.getValue();
        EntityType inventoryet = entityTypeFacade.findByName(controletn);
        if (newInventory.getEntityTypeList() == null) {
            try {
                newInventory.setEntityTypeList(new ArrayList());
            } catch (CdbException ex) {
                logger.error(ex);
            }
        }
        newInventory.getEntityTypeList().add(inventoryet);                
    } 

    @Override
    public ItemDomainMachineDesign createEntityInstance(UserInfo sessionUser) {
        ItemDomainMachineDesign newControl = super.createEntityInstance(sessionUser); 
        
        assignControlAttributes(newControl, sessionUser);
        
        return newControl; 
    } 

    @Override
    public ItemElementRelationship applyRelationship(ItemDomainMachineDesign machineElement, ItemDomainMachineDesign relatedElement) throws InvalidArgument {
        throw new InvalidArgument("Missing information. Use applyRelationship with the interface to parent input."); 
    }
    
    public ItemElementRelationship applyRelationship(ItemDomainMachineDesign controlledElement, ItemDomainMachineDesign controllingElement, String interfaceToParent, UserInfo enteredByUser) throws InvalidArgument {
        ItemElementRelationship relationship = super.applyRelationship(controlledElement, controllingElement);
        
        createInterfaceToParentPropertyValue(relationship, interfaceToParent, enteredByUser);

        return relationship;        
    }
    
    public PropertyType fetchInterfaceToParentPropertyType() {
        String cotrolInterfacePropertyTypeName = SystemPropertyTypeNames.cotrolInterface.getValue();
        return propertyTypeFacade.findByName(cotrolInterfacePropertyTypeName);
    }
    
    public void createInterfaceToParentPropertyValue(ItemElementRelationship relationship, String controlInterface, UserInfo enteredByUser) throws InvalidArgument {
        List<PropertyValue> propertyValueList = relationship.getPropertyValueList();
        if (propertyValueList == null) {
            propertyValueList = new ArrayList<>();
            relationship.setPropertyValueList(propertyValueList);
        }

        Date enteredOnDateTime = new Date();        

        PropertyValue pv = new PropertyValue();
        PropertyType propertyType = fetchInterfaceToParentPropertyType();
        pv.setPropertyType(propertyType);
        pv.setValue(controlInterface);
        pv.setEnteredByUser(enteredByUser);
        pv.setEnteredOnDateTime(enteredOnDateTime);
        
        boolean valid = PropertyValueUtility.verifyValidValueForPropertyValue(pv); 
        if (!valid) {
            throw new InvalidArgument(controlInterface + " is not a valid control interface.");
        }
        
        propertyValueList.add(pv);
    }

    @Override
    public ItemElementRelationshipTypeNames getRelationshipTypeName() {
        return ItemElementRelationshipTypeNames.control;
    }
    
}
