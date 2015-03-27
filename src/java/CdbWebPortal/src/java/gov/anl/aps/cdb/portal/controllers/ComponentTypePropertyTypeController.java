package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.db.beans.PropertyTypeDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("componentTypePropertyTypeController")
@SessionScoped
public class ComponentTypePropertyTypeController extends CdbEntityController<PropertyType, PropertyTypeDbFacade> implements Serializable
{

    private static final String DisplayIdSettingTypeKey = "ComponentTypePropertyType.List.Display.Id";    
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentTypePropertyType.List.Display.NumberOfItemsPerPage";

    private static final String FilterByPropertyTypeNameSettingTypeKey = "ComponentTypePropertyType.List.FilterBy.PropertyTypeName";
    
    
    private static final Logger logger = Logger.getLogger(ComponentTypePropertyTypeController.class.getName());

    @EJB
    private PropertyTypeDbFacade propertyTypeFacade;
    
    private String filterByPropertyTypeName = null;

    public ComponentTypePropertyTypeController() {
    }

    @Override
    protected PropertyTypeDbFacade getFacade() {
        return propertyTypeFacade;
    }

    @Override
    protected PropertyType createEntityInstance() {
        PropertyType propertyType = new PropertyType();
        return propertyType;
    }

    @Override
    public String getEntityTypeName() {
        return "componentTypePropertyType";
    }

    
    @Override
    public String getDisplayEntityTypeName() {
        return "component type property type";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getName();
        }
        return "";
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());        
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        filterByPropertyTypeName = settingTypeMap.get(FilterByPropertyTypeNameSettingTypeKey).getDefaultValue();                
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        filterByPropertyTypeName = sessionUser.getUserSettingValueAsString(FilterByPropertyTypeNameSettingTypeKey, filterByPropertyTypeName);        
    }

@Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByPropertyTypeName = (String) filters.get("propertyType.name");
    }
    
    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(FilterByPropertyTypeNameSettingTypeKey, filterByPropertyTypeName);        
    }

    
    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByPropertyTypeName = null;
    }
    
    public String getFilterByPropertyTypeName() {
        return filterByPropertyTypeName;
    }

    public void setFilterByPropertyTypeName(String filterByPropertyTypeName) {
        this.filterByPropertyTypeName = filterByPropertyTypeName;
    }

    
}
