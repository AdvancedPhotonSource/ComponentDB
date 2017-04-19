/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LogLevelFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.controllers.settings.LogSettings;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.log4j.Logger;

@Named("logController")
@SessionScoped
public class LogController extends CdbEntityController<Log, LogFacade, LogSettings> implements Serializable {       

    private final String SPARES_WARNING_LOG_LEVEL_NAME = "Spares Warning";

    private static final Logger logger = Logger.getLogger(LogController.class.getName());

    private final String DEFAULT_SYSTEM_ADMIN_USERNAME = "cdb";

    @EJB
    private LogFacade logFacade;

    @EJB
    private LogLevelFacade logLevelFacade;

    @EJB
    private UserInfoFacade userInfoFacade;

    private List<LogLevel> filterViewSelectedLogLevels = null;
    private List<Log> filterViewListDataModelSystemLogs = null;

    public LogController() {
        super();
    }

    public static LogController getInstance() {
        return (LogController) SessionUtility.findBean("logController");
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
        if (log.getLogLevelList() == null || log.getLogLevelList().isEmpty()) {
            return "";
        }

        for (LogLevel logLevel : log.getLogLevelList()) {
            if (logLevel.getName().equals(SPARES_WARNING_LOG_LEVEL_NAME)) {
                return "logWarningRow";
            }
        }

        return "";
    }

    public void addSystemLog(String logLevelName, String logMessage) {
        UserInfo enteredByUser = userInfoFacade.findByUsername(DEFAULT_SYSTEM_ADMIN_USERNAME);
        if (enteredByUser == null) {
            SessionUtility.addErrorMessage("System Admin Missing",
                    "User '" + DEFAULT_SYSTEM_ADMIN_USERNAME + "' needs to be in the system. Please notify system administrator.");
        }

        Date enteredOnDateTime = new Date();

        LogLevel logLevel = logLevelFacade.findLogLevelByName(logLevelName);
        if (logLevel == null) {
            logLevel = new LogLevel();
            logLevel.setName(logLevelName);
            logLevelFacade.create(logLevel);
        }

        Log newSystemLog = createEntityInstance();
        newSystemLog.addLogLevel(logLevel);
        newSystemLog.setText(logMessage);
        newSystemLog.setEnteredOnDateTime(enteredOnDateTime);
        newSystemLog.setEnteredByUser(enteredByUser);

        setCurrent(newSystemLog);
        create(true, true);
    }
    
    public List<LogLevel> getFilterViewSelectedLogLevels() {
        return filterViewSelectedLogLevels;
    }

    public void setFilterViewSelectedLogLevels(List<LogLevel> fitlerViewSelectedLogLevels) {
        this.filterViewSelectedLogLevels = fitlerViewSelectedLogLevels;
        this.filterViewListDataModelSystemLogs = null;
    }

    public List<Log> getFilterViewListDataModelSystemLogs() {
        if (filterViewListDataModelSystemLogs == null) {
            if (filterViewSelectedLogLevels != null && !filterViewSelectedLogLevels.isEmpty()) {
                // Load in the list data model for system logs
                filterViewListDataModelSystemLogs = logFacade.findByFilterViewSystemLogAttributes(filterViewSelectedLogLevels);
            }
        }
        return filterViewListDataModelSystemLogs;
    }

    @Override
    protected LogSettings createNewSettingObject() {
        return new LogSettings(this);
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

}
