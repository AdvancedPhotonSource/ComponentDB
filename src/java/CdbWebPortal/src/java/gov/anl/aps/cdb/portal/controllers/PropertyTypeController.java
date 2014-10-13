package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.exceptions.ObjectAlreadyExists;
import gov.anl.aps.cdb.portal.model.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;

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
import org.primefaces.component.datatable.DataTable;

@Named("propertyTypeController")
@SessionScoped
public class PropertyTypeController extends CrudEntityController<PropertyType, PropertyTypeFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "PropertyType.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "PropertyType.List.Display.Id";
    private static final String DisplayDescriptionSettingTypeKey = "PropertyType.List.Display.Description";

    private static final Logger logger = Logger.getLogger(PropertyTypeController.class.getName());

    @EJB
    private PropertyTypeFacade propertyTypeFacade;

    private String filterByTypeCategory = null;

    public PropertyTypeController() {
    }

    @Override
    protected PropertyTypeFacade getFacade() {
        return propertyTypeFacade;
    }

    @Override
    protected PropertyType createEntityInstance() {
        PropertyType propertyType = new PropertyType();
        return propertyType;
    }

    @Override
    public String getEntityTypeName() {
        return "propertyType";
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
    public List<PropertyType> getAvailableItems() {
        return super.getAvailableItems();
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
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayDescription = Boolean.parseBoolean(settingTypeMap.get(DisplayDescriptionSettingTypeKey).getDefaultValue());
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayDescription = sessionUser.getUserSettingValueAsBoolean(DisplayDescriptionSettingTypeKey, displayDescription);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, String> filters = dataTable.getFilters();
        filterByTypeCategory = filters.get("propertyTypeCategory.name");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayDescriptionSettingTypeKey, displayDescription);
    }
    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByTypeCategory = null;
    }

    public String getFilterByTypeCategory() {
        return filterByTypeCategory;
    }

    public void setFilterByTypeCategory(String filterByTypeCategory) {
        this.filterByTypeCategory = filterByTypeCategory;
    }

    @Override
    public boolean entityHasCategories() {
        return true;
    }

    @FacesConverter(forClass = PropertyType.class)
    public static class PropertyTypeControllerConverter implements Converter
    {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PropertyTypeController controller = (PropertyTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "propertyTypeController");
            return controller.getEntity(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
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
            }
            else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + PropertyType.class.getName());
            }
        }

    }

}
