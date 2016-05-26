package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.common.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.db.beans.CdbEntityFacade;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeHandlerFacade;
import gov.anl.aps.cdb.portal.model.db.entities.PropertyTypeHandler;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("propertyTypeHandlerController")
@SessionScoped
public class PropertyTypeHandlerController extends CdbEntityController<PropertyTypeHandler, PropertyTypeHandlerFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyTypeHandler.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyTypeHandler.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyTypeHandler.List.Display.Description";
    private static final String FilterByNameSettingTypeKey = "PropertyTypeHandler.List.FilterBy.Name";
    private static final String FilterByDescriptionSettingTypeKey = "PropertyTypeHandler.List.FilterBy.Description";

    private static final Logger logger = Logger.getLogger(PropertyTypeHandlerController.class.getName());

    @EJB
    private PropertyTypeHandlerFacade propertyTypeHandlerFacade;

    public PropertyTypeHandlerController() {
    }

    @Override
    protected PropertyTypeHandlerFacade getEntityDbFacade() {
        return propertyTypeHandlerFacade;
    }

    @Override
    protected PropertyTypeHandler createEntityInstance() {
        PropertyTypeHandler propertyHandler = new PropertyTypeHandler();
        return propertyHandler;
    }

    @Override
    public String getEntityTypeName() {
        return "propertyTypeHandler";
    }

    @Override
    public String getDisplayEntityTypeName() {
        return "property type handler";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public List<PropertyTypeHandler> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void prepareEntityInsert(PropertyTypeHandler propertyTypeHandler) throws ObjectAlreadyExists {
        PropertyTypeHandler existingPropertyTypeHandler = propertyTypeHandlerFacade.findByName(propertyTypeHandler.getName());
        if (existingPropertyTypeHandler != null) {
            throw new ObjectAlreadyExists("Property type handler " + propertyTypeHandler.getName() + " already exists.");
        }
        logger.debug("Inserting new property type " + propertyTypeHandler.getName());
    }

    @Override
    public void prepareEntityUpdate(PropertyTypeHandler propertyHandler) throws ObjectAlreadyExists {
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());

        filterByName = settingTypeMap.get(FilterByNameSettingTypeKey).getDefaultValue();
        filterByDescription = settingTypeMap.get(FilterByDescriptionSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);

        filterByName = sessionUser.getUserSettingValueAsString(FilterByNameSettingTypeKey, filterByName);
        filterByDescription = sessionUser.getUserSettingValueAsString(FilterByDescriptionSettingTypeKey, filterByDescription);
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);

        sessionUser.setUserSettingValue(FilterByNameSettingTypeKey, filterByName);
        sessionUser.setUserSettingValue(FilterByDescriptionSettingTypeKey, filterByDescription);
    }

    /**
     * Converter class for property handler objects.
     */    
    @FacesConverter(forClass = PropertyTypeHandler.class)
    public static class PropertyHandlerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                PropertyTypeHandlerController controller = (PropertyTypeHandlerController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "propertyTypeHandlerController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to property type handler object.");
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
            if (object instanceof PropertyTypeHandler) {
                PropertyTypeHandler o = (PropertyTypeHandler) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyTypeHandler.class.getName());
            }
        }

    }

}
