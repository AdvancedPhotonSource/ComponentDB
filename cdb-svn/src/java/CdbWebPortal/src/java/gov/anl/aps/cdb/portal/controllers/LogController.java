package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.model.db.entities.SettingEntity;
import gov.anl.aps.cdb.portal.model.db.entities.SettingType;

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
public class LogController extends CdbEntityController<Log, LogFacade> implements Serializable {

    /*
     * Controller specific settings
     */
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
    
    private final String SPARES_WARNING_LOG_LEVEL_NAME = "Spares Warning";

    private static final Logger logger = Logger.getLogger(LogController.class.getName());

    @EJB
    private LogFacade logFacade;

    public LogController() {
        super();
    }

    @Override
    protected LogFacade getEntityDbFacade() {
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
    
    public String getLogRowStyle(Log log) {
        if (log.getLogLevelList() == null || log.getLogLevelList().isEmpty()){
            return "";
        }
        
        for (LogLevel logLevel : log.getLogLevelList()) {
            if (logLevel.getName().equals(SPARES_WARNING_LOG_LEVEL_NAME)) {
                return "logWarningRow"; 
            }
        }
        
        return ""; 
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
    public void updateSettingsFromSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        displayAttachments = settingEntity.getSettingValueAsBoolean(DisplayAttachmentsSettingTypeKey, displayAttachments);
        displayNumberOfItemsPerPage = settingEntity.getSettingValueAsInteger(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        displayId = settingEntity.getSettingValueAsBoolean(DisplayIdSettingTypeKey, displayId);
        displayEnteredByUser = settingEntity.getSettingValueAsBoolean(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        displayEnteredOnDateTime = settingEntity.getSettingValueAsBoolean(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        displayTopic = settingEntity.getSettingValueAsBoolean(DisplayTopicSettingTypeKey, displayTopic);

        filterByEnteredByUser = settingEntity.getSettingValueAsString(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        filterByEnteredOnDateTime = settingEntity.getSettingValueAsString(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        filterByText = settingEntity.getSettingValueAsString(FilterByTextSettingTypeKey, filterByText);
        filterByTopic = settingEntity.getSettingValueAsString(FilterByTopicSettingTypeKey, filterByTopic);
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
        filterByText = (String) filters.get("text");
        filterByTopic = (String) filters.get("topic");
    }

    @Override
    public void saveSettingsForSessionSettingEntity(SettingEntity settingEntity) {
        if (settingEntity == null) {
            return;
        }

        settingEntity.setSettingValue(DisplayAttachmentsSettingTypeKey, displayAttachments);
        settingEntity.setSettingValue(DisplayNumberOfItemsPerPageSettingTypeKey, displayNumberOfItemsPerPage);
        settingEntity.setSettingValue(DisplayIdSettingTypeKey, displayId);
        settingEntity.setSettingValue(DisplayEnteredByUserSettingTypeKey, displayEnteredByUser);
        settingEntity.setSettingValue(DisplayEnteredOnDateTimeSettingTypeKey, displayEnteredOnDateTime);
        settingEntity.setSettingValue(DisplayTopicSettingTypeKey, displayTopic);

        settingEntity.setSettingValue(FilterByEnteredByUserSettingTypeKey, filterByEnteredByUser);
        settingEntity.setSettingValue(FilterByEnteredOnDateTimeSettingTypeKey, filterByEnteredOnDateTime);
        settingEntity.setSettingValue(FilterByTextSettingTypeKey, filterByText);
        settingEntity.setSettingValue(FilterByTopicSettingTypeKey, filterByTopic);
    }

    @Override
    public void clearListFilters() {
        super.clearListFilters();
        filterByEnteredByUser = null;
        filterByEnteredOnDateTime = null;
        filterByText = null;
        filterByTopic = null;
    }

    /**
     * Converter class for log objects.
     */
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
                return controller.getEntity(getIntegerKey(value));
            } catch (Exception ex) {
                // we cannot get entity from a given key
                logger.warn("Value " + value + " cannot be converted to log object.");
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

}
