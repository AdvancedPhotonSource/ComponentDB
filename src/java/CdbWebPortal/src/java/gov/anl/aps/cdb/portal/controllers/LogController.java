/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import gov.anl.aps.cdb.portal.controllers.settings.LogSettings;
import gov.anl.aps.cdb.portal.controllers.utilities.LogControllerUtility;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named("logController")
@SessionScoped
public class LogController extends CdbEntityController<LogControllerUtility, Log, LogFacade, LogSettings> implements Serializable {       

    private final String SPARES_WARNING_LOG_LEVEL_NAME = "Spares Warning";

    private static final Logger logger = LogManager.getLogger(LogController.class.getName());

    @EJB
    private LogFacade logFacade;

    private List<LogLevel> filterViewSelectedLogLevels = null;
    private List<Log> filterViewListDataModelSystemLogs = null;
    
    private static LogController apiInstance; 
    
    public LogController() {
        super();
    } 

    @Override
    protected void loadEJBResourcesManually() {
        super.loadEJBResourcesManually(); 
        logFacade = LogFacade.getInstance();
    }
    
    public static synchronized LogController getApiInstance() {
        if (apiInstance == null) {
            apiInstance = new LogController();            
            apiInstance.prepareApiInstance(); 
        }
        return apiInstance;
    }

    public static LogController getInstance() {
        if (SessionUtility.runningFaces()) {
            return (LogController) SessionUtility.findBean("logController");
        } else {
            return getApiInstance();
        }
    }

    @Override
    protected LogFacade getEntityDbFacade() {
        return logFacade;
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

    @Override
    protected LogControllerUtility createControllerUtilityInstance() {
        return new LogControllerUtility(); 
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
