/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.constants.CdbPropertyValue;
import gov.anl.aps.cdb.common.exceptions.ExternalServiceError;
import gov.anl.aps.cdb.portal.constants.DisplayType;
import gov.anl.aps.cdb.portal.controllers.settings.PropertyValueSettings;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyMetadataFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValue.PropertyValueMetadata;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyValueFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyMetadata;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyValueBase;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerFactory;
import gov.anl.aps.cdb.portal.model.jsf.handlers.PropertyTypeHandlerInterface;
import gov.anl.aps.cdb.portal.utilities.GalleryUtility;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.utilities.StorageUtility;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;
import org.primefaces.model.StreamedContent;

@Named("propertyValueController")
@SessionScoped
public class PropertyValueController extends CdbEntityController<PropertyValue, PropertyValueFacade, PropertyValueSettings> implements Serializable {

    @EJB
    private PropertyValueFacade propertyValueFacade;

    @EJB
    private PropertyMetadataFacade propertyMetadataFacade;

    private PropertyValueMetadata currentPropertyMetadata;

    private static final Logger logger = Logger.getLogger(PropertyValueController.class.getName());

    public PropertyValueController() {
        super();
    }

    public static PropertyValueController getInstance() {
        return (PropertyValueController) SessionUtility.findBean("propertyValueController");
    }

    public boolean isItemElementAssignedToProperty(PropertyValue propertyValue) {
        if (propertyValue.getItemElementList() != null) {
            return propertyValue.getItemElementList().size() > 0;
        }
        return false;
    }

    @Override
    protected PropertyValueFacade getEntityDbFacade() {
        return propertyValueFacade;
    }

    @Override
    protected PropertyValue createEntityInstance() {
        return new PropertyValue();
    }

    @Override
    public String getEntityTypeName() {
        return "propertyValue";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "Property Value";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<PropertyValue> getAvailableItems() {
        return super.getAvailableItems();
    }

    public PropertyTypeHandlerInterface getPropertyTypeHandler(PropertyValue propertyValue) {
        return PropertyTypeHandlerFactory.getHandler(propertyValue);
    }

    public DisplayType configurePropertyValueDisplay(PropertyValue propertyValue) {
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
        propertyTypeHandler.setInfoActionCommand(propertyValue);
        propertyTypeHandler.setDisplayValue(propertyValue);
        propertyTypeHandler.setTargetValue(propertyValue);
        PropertyType propertyType = propertyValue.getPropertyType();
        DisplayType displayType = propertyTypeHandler.getValueDisplayType();
        if (displayType == null) {
            displayType = DisplayType.FREE_FORM_TEXT;
            if (propertyType.hasAllowedPropertyValues()) {
                displayType = DisplayType.SELECTED_TEXT;
            }
        }
        propertyType.setDisplayType(displayType);
        propertyValue.setHandlerInfoSet(true);
        return displayType;
    }

    public StreamedContent executeFileDownloadActionCommandForPropertyValue(PropertyValue propertyValue) {
        DisplayType propertyValueDisplayType = getPropertyValueDisplayType(propertyValue);
        if (propertyValueDisplayType == DisplayType.FILE_DOWNLOAD
                || propertyValueDisplayType == DisplayType.GENERATED_HTTP_LINK_FILE_DOWNLOAD) {
            PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
            try {
                return propertyTypeHandler.fileDownloadActionCommand(propertyValue);
            } catch (ExternalServiceError ex) {
                SessionUtility.addErrorMessage("Error downloading File", ex.getMessage());
            }
        }
        return null;
    }

    public String getPropertyEditPage(PropertyValue propertyValue) {
        PropertyTypeHandlerInterface propertyTypeHandler = PropertyTypeHandlerFactory.getHandler(propertyValue);
        return propertyTypeHandler.getPropertyEditPage();
    }

    public DisplayType getPropertyValueDisplayType(PropertyValue propertyValue) {
        if (propertyValue == null) {
            return DisplayType.FREE_FORM_TEXT;
        }
        String displayValue = propertyValue.getDisplayValue();
        DisplayType result = propertyValue.getPropertyType().getDisplayType();
        if (result == null || displayValue == null || displayValue.isEmpty() || !propertyValue.isHandlerInfoSet()) {
            result = configurePropertyValueDisplay(propertyValue);
        }
        return result;
    }

    public boolean displayShowMetadataForPropertyValue(PropertyValueBase propertyValue) {
        if (propertyValue.getPropertyMetadataBaseList() != null) {
            return propertyValue.getPropertyMetadataBaseList().size() > 0;
        }
        return false;
    }

    public boolean displayAllowedValueSelection(PropertyValue propertyValue) {
        if (propertyValue != null) {
            PropertyType propertyType = propertyValue.getPropertyType();
            if (propertyType != null) {
                return propertyType.hasAllowedPropertyValues();
            }
        }

        return false;
    }

    public boolean displayFreeFormTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.FREE_FORM_TEXT);
    }

    public boolean displaySelectedTextValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.SELECTED_TEXT);
    }

    public boolean displayImageValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.IMAGE);
    }

    public boolean displayGeneratedHttpLinkValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.GENERATED_HTTP_LINK);
    }

    public boolean displayHttpLinkValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.HTTP_LINK);
    }

    public boolean displayDocumentValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.DOCUMENT);
    }

    public boolean displayBooleanValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.BOOLEAN);
    }

    public boolean displayDateValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.DATE);
    }

    public boolean displayTableRecordReference(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.TABLE_RECORD_REFERENCE);
    }

    public boolean displayInfoActionValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.INFO_ACTION);
    }

    public boolean displayGeneratedHTMLDownloadActionValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.GENERATED_HTTP_LINK_FILE_DOWNLOAD);
    }

    public boolean displayDownloadActionValue(PropertyValue propertyValue) {
        return getPropertyValueDisplayType(propertyValue).equals(DisplayType.FILE_DOWNLOAD);
    }

    public static String getOriginalImageApplicationPath(PropertyValue propertyValue) {
        return getOriginalImageApplicationPathByValue(propertyValue.getValue());
    }

    public static String getOriginalImageApplicationPathByValue(String imageName) {
        if (imageName != null) {
            return StorageUtility.getApplicationPropertyValueImagePath(imageName);
        }
        return null;
    }

    public static String getThumbnailImagePath(PropertyValue propertyValue) {
        return getThumbnailImagePathByValue(propertyValue.getValue());
    }

    public static String getThumbnailImagePathByValue(String imageName) {
        if (imageName != null) {
            return StorageUtility.getPropertyValueImagePath(imageName, CdbPropertyValue.THUMBNAIL_IMAGE_EXTENSION);
        }
        return null;
    }

    public static String getScaledImagePath(PropertyValue propertyValue) {
        return getScaledImagePathByValue(propertyValue.getValue());
    }

    public static String getScaledImagePathByValue(String imageName) {
        if (imageName != null) {
            return StorageUtility.getPropertyValueImagePath(imageName, CdbPropertyValue.SCALED_IMAGE_EXTENSION);
        }
        return null;
    }

    public boolean isPropertyValueViewable(PropertyValue propertyValue) {
        return GalleryUtility.viewableFileName(propertyValue.getValue());
    }

    @Override
    protected PropertyValueSettings createNewSettingObject() {
        return new PropertyValueSettings(this);
    }

    public PropertyValueMetadata getCurrentPropertyMetadata() {
        return currentPropertyMetadata;
    }

    public void setCurrentPropertyMetadata(PropertyValueMetadata currentPropertyMetadata) {
        this.currentPropertyMetadata = currentPropertyMetadata;
    }
    
    public void removeCurrentPropertyMetadata() {
        PropertyMetadata propertyMetadata = currentPropertyMetadata.getPropertyMetadata();
        removePropertyMetadata(propertyMetadata);
    }
    
    public void removePropertyMetadata(PropertyMetadata propertyMetadata) {
        removePropertyMetadata(propertyMetadata, "Property metadata has been removed.");
    }

    public void removePropertyMetadata(PropertyMetadata propertyMetadata, String removedMessage) {
        if (propertyMetadata == null) {
            return;
        }
        if (propertyMetadata.getId() != null) {
            propertyMetadataFacade.remove(propertyMetadata);
            if (removedMessage != null) {
                SessionUtility.addInfoMessage("Removed", removedMessage);
            }
        }

        PropertyValue propertyValue = propertyMetadata.getPropertyValue();
        propertyValue.removePropertyMetadataKey(propertyMetadata.getMetadataKey());
    }

    /**
     * Converter class for property value objects.
     */
    @FacesConverter(forClass = PropertyValue.class)
    public static class PropertyValueControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                PropertyValueController controller = (PropertyValueController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyValueController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property value object.");
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
            if (object instanceof PropertyValue) {
                PropertyValue o = (PropertyValue) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyValue.class.getName());
            }
        }

    }

}
