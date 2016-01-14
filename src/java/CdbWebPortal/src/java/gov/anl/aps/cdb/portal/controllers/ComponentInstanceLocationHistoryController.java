/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstanceLocationHistory;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentInstanceLocationHistoryDbFacade;
import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstance;
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
import org.primefaces.component.datatable.DataTable;

/**
 * Component instance location history controller class.
 */
@Named("componentInstanceLocationHistoryController")
@SessionScoped
public class ComponentInstanceLocationHistoryController extends CdbEntityController<ComponentInstanceLocationHistory, ComponentInstanceLocationHistoryDbFacade> implements Serializable {

    /*
     * Controller specific settings
     */
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "ComponentInstanceLocationHistory.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "ComponentInstanceLocationHistory.List.Display.Id";
    private static final String DisplayEnteredByUserSettingTypeKey = "ComponentInstanceLocationHistory.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "ComponentInstanceLocationHistory.List.Display.EnteredOnDateTime";
    private static final String DisplayLocationSettingTypeKey = "ComponentInstanceLocationHistory.List.Display.Location";
    private static final String DisplayLocationDetailsSettingTypeKey = "ComponentInstanceLocationHistory.List.Display.LocationDetails";
    private static final String FilterByEnteredByUserSettingTypeKey = "ComponentInstanceLocationHistory.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "ComponentInstanceLocationHistory.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByLocationSettingTypeKey = "ComponentInstanceLocationHistory.List.FilterBy.Location";
    private static final String FilterByLocationDetailsSettingTypeKey = "ComponentInstanceLocationHistory.List.FilterBy.LocationDetails";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayLocation = null;
    private Boolean displayLocationDetails = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByLocation = null;
    private String filterByLocationDetails = null;
    
    private ComponentInstance selectedComponentInstance = null;
    private List<ComponentInstanceLocationHistory> selectedComponentInstanceHistoryList; 

    private static final Logger logger = Logger.getLogger(ComponentInstanceLocationHistoryController.class.getName());

    @EJB
    private ComponentInstanceLocationHistoryDbFacade componentInstanceLocationHistoryFacade;

    public ComponentInstanceLocationHistoryController() {
        super();
    }

    @Override
    protected ComponentInstanceLocationHistoryDbFacade getEntityDbFacade() {
        return componentInstanceLocationHistoryFacade;
    }

    @Override
    protected ComponentInstanceLocationHistory createEntityInstance() {
        return new ComponentInstanceLocationHistory();
    }

    @Override
    public String getEntityTypeName() {
        return "componentInstanceLocationHistory";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
        }
        return "";
    }

    @Override
    public List<ComponentInstanceLocationHistory> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayLocation = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationSettingTypeKey).getDefaultValue());
        displayLocationDetails = Boolean.parseBoolean(settingTypeMap.get(DisplayLocationDetailsSettingTypeKey).getDefaultValue());

        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByLocation = settingTypeMap.get(FilterByLocationSettingTypeKey).getDefaultValue();
        filterByLocationDetails = settingTypeMap.get(FilterByLocationDetailsSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayEnteredByUser = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayLocation = sessionUser.getUserSettingValueAsBoolean(DisplayLocationSettingTypeKey, displayLocation);
        displayLocationDetails = sessionUser.getUserSettingValueAsBoolean(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);

        filterByEnteredByUser = sessionUser.getUserSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = sessionUser.getUserSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByLocation = sessionUser.getUserSettingValueAsString(FilterByLocationSettingTypeKey, filterByLocation);
        filterByLocationDetails = sessionUser.getUserSettingValueAsString(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
    }

    @Override
    public void updateListSettingsFromListDataTable(DataTable dataTable) {
        super.updateListSettingsFromListDataTable(dataTable);
        if (dataTable == null) {
            return;
        }
        Map<String, Object> filters = dataTable.getFilters();
        filterByEnteredByUser = (String) filters.get("enteredByUser");
        filterByEnteredOnDateTime = (String) filters.get("enteredOnDateTime");
        filterByLocation = (String) filters.get("location");
        filterByLocationDetails = (String) filters.get("locationDetails");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        sessionUser.setUserSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        sessionUser.setUserSettingValue(DisplayLocationSettingTypeKey, displayLocation);
        sessionUser.setUserSettingValue(DisplayLocationDetailsSettingTypeKey, displayLocationDetails);

        sessionUser.setUserSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        sessionUser.setUserSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        sessionUser.setUserSettingValue(FilterByLocationSettingTypeKey, filterByLocation);
        sessionUser.setUserSettingValue(FilterByLocationDetailsSettingTypeKey, filterByLocationDetails);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByLocation = null;
        filterByLocationDetails = null;
    }

    public List<ComponentInstanceLocationHistory> getSelectedComponentInstanceHistoryList() {
        return selectedComponentInstanceHistoryList;
    }

    public void setSelectedComponentInstance(ComponentInstance selectedComponentInstance) {
        this.selectedComponentInstance = selectedComponentInstance;
        this.selectedComponentInstanceHistoryList = selectedComponentInstance.getComponentInstanceLocationHistoryList(); 
    }

    public ComponentInstance getSelectedComponentInstance() {
        return selectedComponentInstance;
    }
    
    @Override
    public Boolean getDisplayId(){
        return displayId;
    }

    public Boolean getDisplayEnteredByUser() {
        return displayEnteredByUser;
    }

    public Boolean getDisplayEnteredOnDateTime() {
        return displayEnteredOnDateTime;
    }

    public Boolean getDisplayLocation() {
        return displayLocation;
    }

    public Boolean getDisplayLocationDetails() {
        return displayLocationDetails;
    }

    public String getFilterByEnteredByUser() {
        return filterByEnteredByUser;
    }

    public String getFilterByEnteredOnDateTime() {
        return filterByEnteredOnDateTime;
    }

    public String getFilterByLocation() {
        return filterByLocation;
    }

    public String getFilterByLocationDetails() {
        return filterByLocationDetails;
    }

    public void setDisplayEnteredByUser(Boolean displayEnteredByUser) {
        this.displayEnteredByUser = displayEnteredByUser;
    }

    public void setDisplayEnteredOnDateTime(Boolean displayEnteredOnDateTime) {
        this.displayEnteredOnDateTime = displayEnteredOnDateTime;
    }

    public void setDisplayLocation(Boolean displayLocation) {
        this.displayLocation = displayLocation;
    }

    public void setDisplayLocationDetails(Boolean displayLocationDetails) {
        this.displayLocationDetails = displayLocationDetails;
    }

    public void setFilterByEnteredByUser(String filterByEnteredByUser) {
        this.filterByEnteredByUser = filterByEnteredByUser;
    }

    public void setFilterByEnteredOnDateTime(String filterByEnteredOnDateTime) {
        this.filterByEnteredOnDateTime = filterByEnteredOnDateTime;
    }

    public void setFilterByLocation(String filterByLocation) {
        this.filterByLocation = filterByLocation;
    }

    public void setFilterByLocationDetails(String filterByLocationDetails) {
        this.filterByLocationDetails = filterByLocationDetails;
    }
    
    /**
     * Converter class for component instance location history objects.
     */
    @FacesConverter(forClass = ComponentInstanceLocationHistory.class)
    public static class ComponentInstanceLocationHistoryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                ComponentInstanceLocationHistoryController controller = (ComponentInstanceLocationHistoryController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "componentInstanceLocationHistoryController");
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component instance location history object.");
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
            if (object instanceof ComponentInstanceLocationHistory) {
                ComponentInstanceLocationHistory o = (ComponentInstanceLocationHistory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentInstanceLocationHistory.class.getName());
            }
        }

    }

}
