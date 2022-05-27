/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.ItemElementRelationshipFacade;
import gov.anl.aps.cdb.portal.model.db.entities.EntityInfo;
import gov.anl.aps.cdb.portal.model.db.entities.Item;
import gov.anl.aps.cdb.portal.model.db.entities.ItemElementRelationship;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class ItemElementRelationshipControllerUtility extends CdbEntityControllerUtility<ItemElementRelationship, ItemElementRelationshipFacade> {

    @Override
    protected ItemElementRelationshipFacade getEntityDbFacade() {
        return ItemElementRelationshipFacade.getInstance(); 
    }

    @Override
    public ItemElementRelationship createEntityInstance(UserInfo sessionUser) {
        return new ItemElementRelationship(); 
    }
    
    @Override
    public PropertyValue preparePropertyTypeValueAdd(ItemElementRelationship cdbDomainEntity,
            PropertyType propertyType, String propertyValueString, String tag) {        
        Item item = null; 
        
        if (cdbDomainEntity.getFirstItem() != null) {
            item = cdbDomainEntity.getFirstItem(); 
        } else {
            item = cdbDomainEntity.getSecondItem(); 
        }
        
        if (item != null) {
            EntityInfo entityInfo = item.getEntityInfo();
            UserInfo ownerUser = entityInfo.getOwnerUser();
            return preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyValueString, tag, ownerUser);
        }
        else {
            return super.preparePropertyTypeValueAdd(cdbDomainEntity, propertyType, propertyValueString, tag); 
        }
    }
    
    

    
    
    @Override
    public String getEntityTypeName() {
        return "itemElementRelationship";
    }
    
}
