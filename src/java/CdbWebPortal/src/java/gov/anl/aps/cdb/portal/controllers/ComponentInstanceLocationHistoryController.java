package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.ComponentInstanceLocationHistory;
import gov.anl.aps.cdb.portal.model.db.beans.ComponentInstanceLocationHistoryFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Location;
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

@Named("componentInstanceLocationHistoryController")
@SessionScoped
public class ComponentInstanceLocationHistoryController extends CrudEntityController<ComponentInstanceLocationHistory, ComponentInstanceLocationHistoryFacade> implements Serializable {

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

    private List<ComponentInstanceLocationHistory> selectedComponentInstanceLocationHistoryList;
    private Location selectedComponentInstanceLocation = null;

    private static final Logger logger = Logger.getLogger(ComponentInstanceLocationHistoryController.class.getName());

    @EJB
    private ComponentInstanceLocationHistoryFacade componentInstanceLocationHistoryFacade;

    public ComponentInstanceLocationHistoryController() {
        super();
    }

    @Override
    protected ComponentInstanceLocationHistoryFacade getFacade() {
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
        Map<String, String> filters = dataTable.getFilters();
        filterByEnteredByUser = filters.get("enteredByUser");
        filterByEnteredOnDateTime = filters.get("enteredOnDateTime");
        filterByLocation = filters.get("location");
        filterByLocationDetails = filters.get("locationDetails");
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
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to component instance location history object.");
                return null;
            }
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
            if (object instanceof ComponentInstanceLocationHistory) {
                ComponentInstanceLocationHistory o = (ComponentInstanceLocationHistory) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ComponentInstanceLocationHistory.class.getName());
            }
        }

    }

}
