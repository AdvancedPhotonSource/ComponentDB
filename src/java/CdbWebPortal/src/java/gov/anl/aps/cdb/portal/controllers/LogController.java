package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.entities.Log;
import gov.anl.aps.cdb.portal.model.beans.LogFacade;
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
import org.primefaces.component.datatable.DataTable;

@Named("logController")
@SessionScoped
public class LogController extends CrudEntityController<Log, LogFacade> implements Serializable {

    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Log.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Log.List.Display.Id";
    private static final String DisplayEnteredByUserSettingTypeKey = "Log.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "Log.List.Display.EnteredOnDateTime";

    private static final String FilterByEnteredByUserSettingTypeKey = "Log.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "Log.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByTextSettingTypeKey = "Log.List.FilterBy.Text";

    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByText = null;

    @EJB
    private LogFacade logFacade;

    private DataTable designLogListDataTable = null;
    private DataTable componentLogListDataTable = null;

    public LogController() {
        super();
    }

    @Override
    protected LogFacade getFacade() {
        return logFacade;
    }

    @Override
    protected Log createEntityInstance() {
        return new Log();
    }

    @Override
    public String getEntityTypeName() {
        return "log";
    }

    @Override
    public String getCurrentEntityInstanceName() {
        if (getCurrent() != null) {
            return getCurrent().getId().toString();
        }
        return "";
    }

    @Override
    public List<Log> getAvailableItems() {
        return super.getAvailableItems();
    }

    @Override
    public void updateSettingsFromSettingTypeDefaults(Map<String, SettingType> settingTypeMap) {
        if (settingTypeMap == null) {
            return;
        }

        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());

        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByText = settingTypeMap.get(FilterByTextSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayEnteredByUser = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);

        filterByEnteredByUser = sessionUser.getUserSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = sessionUser.getUserSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
         filterByText = sessionUser.getUserSettingValueAsString(FilterByTextSettingTypeKey, filterByText);
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
        filterByText = filters.get("text");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        sessionUser.setUserSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);

        sessionUser.setUserSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        sessionUser.setUserSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        sessionUser.setUserSettingValue(FilterByTextSettingTypeKey, filterByText);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByText = null;
    }

    @FacesConverter(forClass = Log.class)
    public static class LogControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LogController controller = (LogController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "logController");
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
            if (object instanceof Log) {
                Log o = (Log) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Log.class.getName());
            }
        }

    }

    public Boolean getDisplayEnteredByUser() {
        return displayEnteredByUser;
    }

    public void setDisplayEnteredByUser(Boolean displayEnteredByUser) {
        this.displayEnteredByUser = displayEnteredByUser;
    }

    public Boolean getDisplayEnteredOnDateTime() {
        return displayEnteredOnDateTime;
    }

    public void setDisplayEnteredOnDateTime(Boolean displayEnteredOnDateTime) {
        this.displayEnteredOnDateTime = displayEnteredOnDateTime;
    }

    public String getFilterByEnteredByUser() {
        return filterByEnteredByUser;
    }

    public void setFilterByEnteredByUser(String filterByEnteredByUser) {
        this.filterByEnteredByUser = filterByEnteredByUser;
    }

    public String getFilterByEnteredOnDateTime() {
        return filterByEnteredOnDateTime;
    }

    public void setFilterByEnteredOnDateTime(String filterByEnteredOnDateTime) {
        this.filterByEnteredOnDateTime = filterByEnteredOnDateTime;
    }

    public String getFilterByText() {
        return filterByText;
    }

    public void setFilterByText(String filterByText) {
        this.filterByText = filterByText;
    }

    public DataTable getDesignLogListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            designLogListDataTable = new DataTable();
        }
        return designLogListDataTable;
    }

    public void setDesignLogListDataTable(DataTable designLogListDataTable) {
        this.designLogListDataTable = designLogListDataTable;
    }

    public DataTable getComponentLogListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            componentLogListDataTable = new DataTable();
        }
        return componentLogListDataTable;
    }

    public void setComponentLogListDataTable(DataTable componentLogListDataTable) {
        this.componentLogListDataTable = componentLogListDataTable;
    }
    
}
