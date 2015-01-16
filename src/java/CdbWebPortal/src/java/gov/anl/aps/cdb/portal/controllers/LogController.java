package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
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

@Named("logController")
@SessionScoped
public class LogController extends CrudEntityController<Log, LogFacade> implements Serializable {

    private static final String DisplayAttachmentsSettingTypeKey = "Log.List.Display.Attachments";
    private static final String DisplayNumberOfItemsPerPageSettingTypeKey = "Log.List.Display.NumberOfItemsPerPage";
    private static final String DisplayIdSettingTypeKey = "Log.List.Display.Id";
    private static final String DisplayEnteredByUserSettingTypeKey = "Log.List.Display.EnteredByUser";
    private static final String DisplayEnteredOnDateTimeSettingTypeKey = "Log.List.Display.EnteredOnDateTime";
    private static final String DisplayTopicSettingTypeKey = "Log.List.Display.Topic";

    private static final String FilterByEnteredByUserSettingTypeKey = "Log.List.FilterBy.EnteredByUser";
    private static final String FilterByEnteredOnDateTimeSettingTypeKey = "Log.List.FilterBy.EnteredOnDateTime";
    private static final String FilterByTextSettingTypeKey = "Log.List.FilterBy.Text";
    private static final String FilterByTopicSettingTypeKey = "Log.List.FilterBy.Topic";

    private Boolean displayAttachments = null;
    private Boolean displayEnteredByUser = null;
    private Boolean displayEnteredOnDateTime = null;
    private Boolean displayTopic = null;

    private String filterByEnteredByUser = null;
    private String filterByEnteredOnDateTime = null;
    private String filterByText = null;
    private String filterByTopic = null;

    private static final Logger logger = Logger.getLogger(LogController.class.getName());

    @EJB
    private LogFacade logFacade;

    private DataTable designLogListDataTable = null;
    private DataTable componentLogListDataTable = null;
    private DataTable componentInstanceLogListDataTable = null;
    private DataTable designElementLogListDataTable = null;

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

        displayAttachments = Boolean.parseBoolean(settingTypeMap.get(DisplayAttachmentsSettingTypeKey).getDefaultValue());
        displayNumberOfItemsPerPage = Integer.parseInt(settingTypeMap.get(DisplayNumberOfItemsPerPageSettingTypeKey).getDefaultValue());
        displayId = Boolean.parseBoolean(settingTypeMap.get(DisplayIdSettingTypeKey).getDefaultValue());
        displayEnteredByUser = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredByUserSettingTypeKey).getDefaultValue());
        displayEnteredOnDateTime = Boolean.parseBoolean(settingTypeMap.get(DisplayEnteredOnDateTimeSettingTypeKey).getDefaultValue());
        displayTopic = Boolean.parseBoolean(settingTypeMap.get(DisplayTopicSettingTypeKey).getDefaultValue());

        filterByEnteredByUser = settingTypeMap.get(FilterByEnteredByUserSettingTypeKey).getDefaultValue();
        filterByEnteredOnDateTime = settingTypeMap.get(FilterByEnteredOnDateTimeSettingTypeKey).getDefaultValue();
        filterByText = settingTypeMap.get(FilterByTextSettingTypeKey).getDefaultValue();
        filterByTopic = settingTypeMap.get(FilterByTopicSettingTypeKey).getDefaultValue();
    }

    @Override
    public void updateSettingsFromSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        displayAttachments = sessionUser.getUserSettingValueAsBoolean(DisplayAttachmentsSettingTypeKey, displayAttachments);
        displayNumberOfItemsPerPage = sessionUser.getUserSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = sessionUser.getUserSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayEnteredByUser = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = sessionUser.getUserSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayTopic = sessionUser.getUserSettingValueAsBoolean(DisplayTopicSettingTypeKey, displayTopic);

        filterByEnteredByUser = sessionUser.getUserSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = sessionUser.getUserSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByText = sessionUser.getUserSettingValueAsString(FilterByTextSettingTypeKey, filterByText);
        filterByTopic = sessionUser.getUserSettingValueAsString(FilterByTopicSettingTypeKey, filterByTopic);
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
        filterByTopic = filters.get("topic");
    }

    @Override
    public void saveSettingsForSessionUser(UserInfo sessionUser) {
        if (sessionUser == null) {
            return;
        }

        sessionUser.setUserSettingValue(DisplayAttachmentsSettingTypeKey, displayAttachments);
        sessionUser.setUserSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        sessionUser.setUserSettingValue(DisplayIdSettingTypeKey, displayId);
        sessionUser.setUserSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        sessionUser.setUserSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        sessionUser.setUserSettingValue(DisplayTopicSettingTypeKey, displayTopic);

        sessionUser.setUserSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        sessionUser.setUserSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        sessionUser.setUserSettingValue(FilterByTextSettingTypeKey, filterByText);
        sessionUser.setUserSettingValue(FilterByTopicSettingTypeKey, filterByTopic);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByText = null;
        filterByTopic = null;
    }

    @FacesConverter(forClass = Log.class)
    public static class LogControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            try {
                if (value == null || value.length() == 0) {
                    return null;
                }
                LogController controller = (LogController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "logController");
                return controller.getEntity(getKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to log object.");
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

    public Boolean getDisplayAttachments() {
        return displayAttachments;
    }

    public void setDisplayAttachments(Boolean displayAttachments) {
        this.displayAttachments = displayAttachments;
    }

    public Boolean getDisplayTopic() {
        return displayTopic;
    }

    public void setDisplayTopic(Boolean displayTopic) {
        this.displayTopic = displayTopic;
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

    public String getFilterByTopic() {
        return filterByTopic;
    }

    public void setFilterByTopic(String filterByTopic) {
        this.filterByTopic = filterByTopic;
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

    public DataTable getComponentInstanceLogListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            componentInstanceLogListDataTable = new DataTable();
        }
        return componentInstanceLogListDataTable;
    }

    public void setComponentInstanceLogListDataTable(DataTable componentInstanceLogListDataTable) {
        this.componentInstanceLogListDataTable = componentInstanceLogListDataTable;
    } 
    
    public DataTable getDesignElementLogListDataTable() {
        if (userSettingsChanged() || isListDataModelReset()) {
            designElementLogListDataTable = new DataTable();
        }
        return designElementLogListDataTable;
    }

    public void setDesignElementLogListDataTable(DataTable designElementLogListDataTable) {
        this.designElementLogListDataTable = designElementLogListDataTable;
    }    
}
    

