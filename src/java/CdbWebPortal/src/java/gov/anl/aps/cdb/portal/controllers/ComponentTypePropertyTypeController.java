package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.entities.PropertyType;
import gov.anl.aps.cdb.portal.model.beans.PropertyTypeFacade;
import gov.anl.aps.cdb.portal.model.entities.SettingType;
import gov.anl.aps.cdb.portal.model.entities.UserInfo;

import java.io.Serializable;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.apache.log4j.Logger;
import org.primefaces.component.datatable.DataTable;

@Named("componentTypePropertyTypeController")
@SessionScoped
public class ComponentTypePropertyTypeController extends CrudEntityController<PropertyType, PropertyTypeFacade> implements Serializable
{

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentTypePropertyType.List.Display.NumberOfItemsPerPage";

    private static final String FilterByPropertyTypeNameSettingTypeKey = "ComponentTypePropertyType.List.FilterBy.PropertyTypeName";
    
    
    private static final Logger logger = Logger.getLogger(ComponentTypePropertyTypeController.class.getName());

    @EJB
    private PropertyTypeFacade propertyTypeFacade;
    
    private String filterByPropertyTypeName = null;

    public ComponentTypePropertyTypeController() {
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

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        filterByPropertyTypeName = settingTypeMap.get(FilterByPropertyTypeNameSettingTypeKey).getDefaultValue();                
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        filterByPropertyTypeName = sessionUser.getUserSettingValueAsString(FilterByPropertyTypeNameSettingTypeKey, filterByPropertyTypeName);        
    }

@Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, String> filters = dataTable.getFilters();
        filterByPropertyTypeName = filters.get("propertyType.name");
    }
    
    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

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
