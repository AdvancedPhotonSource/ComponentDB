/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.model.db.utilities.PropertyValueUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author djarosz
 * @param <EntityType>
 * @param <FacadeType>
 */
public abstract class CdbDomainEntityController<EntityType extends CdbDomainEntity, FacadeType extends CdbEntityDbFacade<EntityType>> extends CdbEntityController<EntityType, FacadeType> implements Serializable {

    public CdbDomainEntityController() {
        super();
    }
    
    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        for (PropertyType propertyType : propertyTypeList) {
            preparePropertyTypeValueAdd(propertyType);
        }
    }

    public void preparePropertyTypeValueAdd(PropertyType propertyType) {
        preparePropertyTypeValueAdd(propertyType, propertyType.getDefaultValue());
    }

    public void preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString) {
        preparePropertyTypeValueAdd(propertyType, propertyValueString, null);
    }
    
    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString, String tag) {
        EntityType domainEntity = getCurrent();
        List<PropertyValue> propertyValueList = domainEntity.getPropertyValueList();
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();

        PropertyValue propertyValue = new PropertyValue();
        propertyValue.setPropertyType(propertyType);
        propertyValue.setValue(propertyValueString);
        propertyValue.setUnits(propertyType.getDefaultUnits());
        propertyValueList.add(propertyValue);
        propertyValue.setEnteredByUser(lastModifiedByUser);
        propertyValue.setEnteredOnDateTime(lastModifiedOnDateTime);
        if (tag != null) {
            propertyValue.setTag(tag);
        }
        
        return propertyValue; 
    }
    
    public List<PropertyValue> prepareImageList(EntityType domainEntity) {
        if (domainEntity == null) {
            return null;
        }
        List<PropertyValue> imageList = PropertyValueUtility.prepareImagePropertyValueList(domainEntity.getPropertyValueList());
        domainEntity.setImagePropertyList(imageList);
        return imageList;
    }
    
    public List<PropertyValue> getImageList() {
        EntityType domainEntity = getCurrent();
        if (domainEntity == null) {
            return null;
        }
        List<PropertyValue> domainEntityImageList = domainEntity.getImagePropertyList();
        if (domainEntityImageList == null) {
            domainEntityImageList = prepareImageList(domainEntity);
        }
        return domainEntityImageList;
    }
    
    public Boolean getDisplayImages() {
        List<PropertyValue> domainEntityImageList = getImageList();
        return (domainEntityImageList != null && !domainEntityImageList.isEmpty());
    }
}
