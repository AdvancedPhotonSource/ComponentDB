/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.controllers.settings.PropertyValueHistorySettings;
import gov.anl.aps.cdb.portal.controllers.utilities.PropertyValueHistoryControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueHistory;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueHistoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("propertyValueHistoryController")
@SessionScoped
public class PropertyValueHistoryController extends CdbEntityController<PropertyValueHistoryControllerUtility, PropertyValueHistory, PropertyValueHistoryFacade, PropertyValueHistorySettings> implements Serializable {       

    private List<PropertyValueHistory> selectedPropertyValueHistoryList;
    private String selectedPropertyValueTypeName = null;
    private PropertyValue selectedPropertyValue = null;
    private DisplayType displayType = null;

    private static final Logger logger = LogManager.getLogger(PropertyValueHistoryController.class.getName());

    @EJB
    private PropertyValueHistoryFacade propertyValueHistoryFacade;

    public PropertyValueHistoryController() {
        super();
    }

    @Override
    protected PropertyValueHistoryFacade getEntityDbFacade() {
        return propertyValueHistoryFacade;
    }

    @Override
    public List<PropertyValueHistory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    protected PropertyValueHistorySettings createNewSettingObject() {
        return new PropertyValueHistorySettings(this);
    }

    @Override
    protected PropertyValueHistoryControllerUtility createControllerUtilityInstance() {
        return new PropertyValueHistoryControllerUtility(); 
    }
  
    /**
     * Converter class for property value history objects.
     */
    @FacesConverter(forClass = PropertyValueHistory.class)
    public static class PropertyValueHistoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                PropertyValueHistoryController controller = (PropertyValueHistoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyValueHistoryController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property value history object.");
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
            if (object instanceof PropertyValueHistory) {
                PropertyValueHistory o = (PropertyValueHistory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyValueHistory.class.getName());
            }
        }

    }    

    public List<PropertyValueHistory> getSelectedPropertyValueHistoryList() {
        return selectedPropertyValueHistoryList;
    }

    public String getSelectedPropertyValueTypeName() {
        return selectedPropertyValueTypeName;
    }

    public PropertyValue getSelectedPropertyValue() {
        return selectedPropertyValue;
    }

    public void setSelectedPropertyValue(PropertyValue selectedPropertyValue) {
        this.selectedPropertyValue = selectedPropertyValue;

        // Reset history list adding the current entry and reversing the order, set property type name
        PropertyValueHistory currentEntry = new PropertyValueHistory();
        currentEntry.updateFromPropertyValue(selectedPropertyValue);
        selectedPropertyValueHistoryList = new ArrayList<>();
        selectedPropertyValueHistoryList.addAll(selectedPropertyValue.getPropertyValueHistoryList());
        selectedPropertyValueHistoryList.add(currentEntry);
        Collections.reverse(selectedPropertyValueHistoryList);
        selectedPropertyValueTypeName = selectedPropertyValue.getPropertyType().getName();
        displayType = selectedPropertyValue.getPropertyType().getDisplayType();
        if (displayType == null) {
            PropertyValueController pvc = PropertyValueController.getInstance();
            displayType = pvc.getPropertyValueDisplayType(selectedPropertyValue);
        }
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(selectedPropertyValue);
        for (PropertyValueHistory propertyValueHistory : selectedPropertyValueHistoryList) {
            propertyTypeHandler.setDisplayValue(propertyValueHistory);
            propertyTypeHandler.setTargetValue(propertyValueHistory);
            propertyTypeHandler.setInfoActionCommand(propertyValueHistory);            
        }
    }

    public boolean displayTextValue() {
        return displayType.equals(DisplayType.FREE_FORM_TEXT) || displayType.equals(DisplayType.SELECTED_TEXT);
    }

    public boolean displayImageValue() {
        return displayType.equals(DisplayType.IMAGE);
    }

    public boolean displayHttpLinkValue() {
        return displayType.equals(DisplayType.HTTP_LINK);
    }
    
    public boolean displayInfoActionValue(){
        return displayType.equals(displayType.INFO_ACTION); 
    }
    
    public boolean displayTableRecordReferenceValue(){
        return displayType.equals(displayType.TABLE_RECORD_REFERENCE); 
    }

    public boolean displayDocumentValue() {
        return displayType.equals(DisplayType.DOCUMENT);
    }

    public boolean displayBooleanValue() {
        return displayType.equals(DisplayType.BOOLEAN);
    }

    public boolean displayDateValue() {
        return displayType.equals(DisplayType.DATE);
    }
        
    public String getOriginalImageApplicationPath(PropertyValueHistory propertyValueHistory) {
        return StorageUtility.getApplicationPropertyValueImagePath(propertyValueHistory.getValue() + CdbPropertyValue.ORIGINAL_IMAGE_EXTENSION);   
    }

    public String getThumbnailImagePath(PropertyValueHistory propertyValueHistory) {
        return StorageUtility.getPropertyValueImagePath(propertyValueHistory.getValue(), CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);   
    }
    
}
