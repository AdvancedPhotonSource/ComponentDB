/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.controllers.settings.PropertyTypeSettings;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.db.entities.AllowedPropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.CdbDomainEntity;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeCategory;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.log4j.Logger;

@Named("propertyTypeController")
@SessionScoped
public class PropertyTypeController extends CdbEntityController<PropertyType, PropertyTypeFacade, PropertyTypeSettings> implements Serializable {

    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());
    
    private Boolean selectFilterViewDisplayCategory = null;
    private Boolean selectFilterViewDisplayHandler = null;

    @EJB
    private PropertyTypeFacade propertyTypeFacade;    

    private final Boolean FILTER_VIEW_IS_INTERNAL = false; 
    private List<PropertyTypeCategory> fitlerViewSelectedPropertyTypeCategories = null;
    private List<PropertyTypeHandler> fitlerViewSelectedPropertyTypeHandlers = null;

    private DataModel filterViewDataModel;

    public PropertyTypeController() {
        super(); 
        settingObject.setSelectDisplayDescription(true);
    }
    
    public static PropertyTypeController getInstance(){ 
        return (PropertyTypeController) SessionUtility.findBean("propertyTypeController"); 
    }

    @Override
    protected PropertyTypeFacade getEntityDbFacade() {
        return propertyTypeFacade;
    }

    @Override
    protected PropertyType createEntityInstance() {
        PropertyType propertyType = new PropertyType();
        propertyType.setIsInternal(false);
        propertyType.setIsActive(true);
        propertyType.setIsUserWriteable(false);
        propertyType.setIsDynamic(false);
        return propertyType;
    }

    @Override
    public String getEntityTypeName() {
        return "propertyType";
    }

    @Override
    public String getEntityTypeCategoryName() {
        return "propertyTypeCategory";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "property type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public PropertyType findById(Integer id) {
        return propertyTypeFacade.findById(id);
    }

    public PropertyType findByName(String name) {
        return propertyTypeFacade.findByName(name);
    }

    @Override
    public List<PropertyType> getAvailableItems() {
        return super.getAvailableItems();
    }
    
    public List<PropertyType> getAvailableExternalItems() { 
        return propertyTypeFacade.findByPropertyInternalStatus(false); 
    }

    @Override
    public void prepareEntityInsert(PropertyType propertyType) throws ObjectAlreadyExists {
        PropertyType existingPropertyType = propertyTypeFacade.findByName(propertyType.getName());
        if (existingPropertyType != null) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyType.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyType propertyType) throws ObjectAlreadyExists {
        propertyType.resetCachedVales();
        PropertyType existingPropertyType = propertyTypeFacade.findByName(propertyType.getName());
        if (existingPropertyType != null && !existingPropertyType.getId().equals(propertyType.getId())) {
            throw new ObjectAlreadyExists("Property type " + propertyType.getName() + " already exists.");
        }
        logger.debug("Updating property type " + propertyType.getName());
    }

    @Override
    public boolean entityHasCategories() {
        return true;
    }

    public void prepareAddAllowedPropertyValue(String value) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        AllowedPropertyValue allowedPropertyValue = new AllowedPropertyValue();
        allowedPropertyValue.setValue(value);
        allowedPropertyValue.setPropertyType(propertyType);
        allowedPropertyValueList.add(0, allowedPropertyValue);
    }

    public void saveAllowedPropertyValueList() {
        update();
    }

    public void deleteAllowedPropertyValue(AllowedPropertyValue allowedPropertyValue) {
        PropertyType propertyType = getCurrent();
        List<AllowedPropertyValue> allowedPropertyValueList = propertyType.getAllowedPropertyValueList();
        allowedPropertyValueList.remove(allowedPropertyValue);
    }

    public void prepareSelectPropertyTypesForDomainEntity(CdbDomainEntity domainEntity) {
        settingObject.clearSelectFilters();
        resetSelectDataModel();
        List<PropertyType> selectPropertyTypeList = getEntityDbFacade().findAll();
        createSelectDataModel(selectPropertyTypeList);
    }   

    public List<PropertyTypeCategory> getFitlerViewSelectedPropertyTypeCategories() {
        return fitlerViewSelectedPropertyTypeCategories;
    }

    public void setFitlerViewSelectedPropertyTypeCategories(List<PropertyTypeCategory> fitlerViewSelectedPropertyTypeCategories) {
        if (!Objects.equals(this.fitlerViewSelectedPropertyTypeCategories, fitlerViewSelectedPropertyTypeCategories)) {
            this.fitlerViewSelectedPropertyTypeCategories = fitlerViewSelectedPropertyTypeCategories;
            resetFilterViewDataModel();
        }
    }

    public List<PropertyTypeHandler> getFitlerViewSelectedPropertyTypeHandlers() {
        return fitlerViewSelectedPropertyTypeHandlers;
    }

    public void setFitlerViewSelectedPropertyTypeHandlers(List<PropertyTypeHandler> fitlerViewSelectedPropertyTypeHandlers) {
        if (!Objects.equals(this.fitlerViewSelectedPropertyTypeHandlers, fitlerViewSelectedPropertyTypeHandlers)) {
            this.fitlerViewSelectedPropertyTypeHandlers = fitlerViewSelectedPropertyTypeHandlers;
            resetFilterViewDataModel();
        }
    }

    public void resetFilterViewDataModel() {
        filterViewDataModel = null;
        selectFilterViewDisplayCategory = null;
        selectFilterViewDisplayHandler = null;
    }

    public DataModel getFilterViewDataModel() {
        if (filterViewDataModel == null) {
            List<PropertyType> results;
            results = propertyTypeFacade.findByFilterViewAttributes(
                    fitlerViewSelectedPropertyTypeCategories, 
                    fitlerViewSelectedPropertyTypeHandlers,
                    FILTER_VIEW_IS_INTERNAL);
            if (results != null) {
                filterViewDataModel = new ListDataModel(results);
            }
        }
        return filterViewDataModel;

    }

    @Override
    protected PropertyTypeSettings createNewSettingObject() {
        return new PropertyTypeSettings(this);
    }
    
    public Boolean getSelectFilterViewDisplayCategory() {
        if (selectFilterViewDisplayCategory == null) {
            if (fitlerViewSelectedPropertyTypeCategories != null) {
                int size = fitlerViewSelectedPropertyTypeCategories.size();
                selectFilterViewDisplayCategory = size == 0 || size > 1;
            } else {
                selectFilterViewDisplayCategory = true;
            }
        }
        return selectFilterViewDisplayCategory;
    }

    public Boolean getSelectFilterViewDisplayHandler() {
        if (selectFilterViewDisplayHandler == null) {
            if (fitlerViewSelectedPropertyTypeHandlers != null) {
                int size = fitlerViewSelectedPropertyTypeHandlers.size();
                selectFilterViewDisplayHandler = size == 0 || size > 1;
            } else {
                selectFilterViewDisplayHandler = true;
            }
        }
        return selectFilterViewDisplayHandler;
    }

    /**
     * Converter class for property type objects.
     */
    @FacesConverter(forClass = PropertyType.class)
    public static class PropertyTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                PropertyTypeController controller = (PropertyTypeController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyTypeController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + "cannot be converted to property type object.");
                return null;
            }
        }

        Integer getIntegerKey(String value) {
            return Integer.valueOf(value);
        }

        String getStringKey(Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof PropertyType) {
                PropertyType o = (PropertyType) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyType.class.getName());
            }
        }

    }
}
