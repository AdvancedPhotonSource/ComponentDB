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
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 *
 * @author djarosz
 * @param <EntityType>
 * @param <FacadeType>
 */
public abstract class CdbDomainEntityController<EntityType extends CdbDomainEntity, FacadeType extends CdbEntityDbFacade<EntityType>> extends CdbEntityController<EntityType, FacadeType> implements Serializable {

    private PropertyValue currentEditPropertyValue; 
    private DataTable entityPropertyValueListDataTable = null;
    
    public CdbDomainEntityController() {
        super();
    }
    
    public void selectPropertyTypes(List<PropertyType> propertyTypeList) {
        for (PropertyType propertyType : propertyTypeList) {
            preparePropertyTypeValueAdd(propertyType);
        }
    }
    
    public void selectPropertyType(PropertyType propertyType, String onSuccessCommand) {
        if (propertyType != null) {
            PropertyValue propertyValue = preparePropertyTypeValueAdd(propertyType); 
            setCurrentEditPropertyValue(propertyValue);
            RequestContext.getCurrentInstance().execute(onSuccessCommand);
        } else {
            SessionUtility.addWarningMessage("No property type selected", "Please select a property type.");
            currentEditPropertyValue = null; 
        }
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType) {
        return preparePropertyTypeValueAdd(propertyType, propertyType.getDefaultValue());
    }

    public PropertyValue preparePropertyTypeValueAdd(PropertyType propertyType, String propertyValueString) {
        return preparePropertyTypeValueAdd(propertyType, propertyValueString, null);
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

    public PropertyValue getCurrentEditPropertyValue() {
        return currentEditPropertyValue;
    }

    public void setCurrentEditPropertyValue(PropertyValue currentEditPropertyValue) {
        this.currentEditPropertyValue = currentEditPropertyValue;
    }
    
    public void removeCurrentEditPropertyValue(){
        if(currentEditPropertyValue != null) {
            EntityType domainEntity = getCurrent();
            if(currentEditPropertyValue.getId() == null) {
                // Never saved so it should be removed from the property value list
                domainEntity.getPropertyValueList().remove(currentEditPropertyValue);
            } else {
                // Will cause refetching of display value.
                currentEditPropertyValue.setDisplayValue("");
            }
            currentEditPropertyValue = null; 
        }
        
    }
    
    public void updateEditProperty(){
        // Will cause refetching of display value.
        currentEditPropertyValue.setDisplayValue("");
        this.update(); 
    }
    
    public void deleteCurrentEditPropertyValue(){
        this.deleteProperty(currentEditPropertyValue);
        currentEditPropertyValue = null; 
    }
    
    public void deleteProperty(PropertyValue componentProperty) {
        EntityType entity = getCurrent();
        List<PropertyValue> componentPropertyList = entity.getPropertyValueList();
        componentPropertyList.remove(componentProperty);
        updateOnRemoval();
    }
    
    
    public DataTable getEntityPropertyValueListDataTable() {
        if (userSettingsChanged() || shouldResetListDataModel()) {
            entityPropertyValueListDataTable = new DataTable();
        }
        return entityPropertyValueListDataTable;
    }

    public void setEntityPropertyValueListDataTable(DataTable componentPropertyValueListDataTable) {
        this.entityPropertyValueListDataTable = componentPropertyValueListDataTable;
    }
}
