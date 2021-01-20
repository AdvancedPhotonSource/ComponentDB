/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.LogLevelFacade;
import gov.anl.aps.cdb.portal.model.db.beans.UserInfoFacade;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import java.util.Date;
import javax.ejb.EJB;

/**
 *
 * @author darek
 */
public class LogControllerUtility extends CdbEntityControllerUtility<Log, LogFacade>{
    
    @EJB
    private LogFacade logFacade; 
    
    @EJB
    private LogLevelFacade logLevelFacade;

    @EJB
    private UserInfoFacade userInfoFacade;
    
    private final String DEFAULT_SYSTEM_ADMIN_USERNAME = "cdb";
    
    private static LogControllerUtility systemLogInstance;

    public LogControllerUtility() {
        if (logFacade == null) {
            logFacade = LogFacade.getInstance(); 
        }
        if (logLevelFacade == null) {
            logLevelFacade = LogLevelFacade.getInstance();
        }
        if (userInfoFacade == null) {
            userInfoFacade = UserInfoFacade.getInstance(); 
        }
    }

    @Override
    protected LogFacade getEntityDbFacade() {                
        return logFacade; 
    }
    
    public static synchronized LogControllerUtility getSystemLogInstance() {
        if (systemLogInstance == null) {
            systemLogInstance = new LogControllerUtility();
        }
        return systemLogInstance;
    }
    
    public void addSystemLog(String logLevelName, String logMessage) throws CdbException {
        UserInfo enteredByUser = userInfoFacade.findByUsername(DEFAULT_SYSTEM_ADMIN_USERNAME);
        if (enteredByUser == null) {
            throw new CdbException("User '" + DEFAULT_SYSTEM_ADMIN_USERNAME + "' needs to be in the system. Please notify system administrator.");
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
        
        create(newSystemLog, enteredByUser);
    }
    
    protected Log createEntityInstance() {
        return new Log();
    }

    @Override
    protected void addCreatedSystemLog(Log entity, UserInfo createdByUserInfo) {
        // No need to create a system log when creating a log. 
        return;
    }

    @Override
    protected void addCreatedWarningSystemLog(Exception exception, Log entity, UserInfo createdByUserInfo) {
        // No need to create a system log when creating a log. 
        return;
    }
    
    @Override
    public String getEntityTypeName() {
        return "log";
    }

    @Override
    public Log createEntityInstance(UserInfo sessionUser) {
        return new Log(); 
    }

    
}
