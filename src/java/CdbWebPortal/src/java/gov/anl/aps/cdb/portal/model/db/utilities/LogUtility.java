/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.portal.constants.SystemLogLevel;
import gov.anl.aps.cdb.portal.controllers.utilities.LogControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * DB utility class for log objects.
 */
public class LogUtility {

    public static Log createLogEntry() {
        UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
        return createLogEntry(enteredByUser); 
    }
    
    public static Log createLogEntry(UserInfo enteredByUser) {        
        Date enteredOnDateTime = new Date();
        Log logEntry = new Log();
        logEntry.setEnteredByUser(enteredByUser);
        logEntry.setEnteredOnDateTime(enteredOnDateTime);
        logEntry.setEffectiveFromDateTime(enteredOnDateTime);
        return logEntry;
    }

    public static Log createLogEntry(String logText) {
        UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
        Date enteredOnDateTime = new Date();
        Log logEntry = new Log();
        logEntry.setText(logText);
        logEntry.setEnteredByUser(enteredByUser);
        logEntry.setEnteredOnDateTime(enteredOnDateTime);
        return logEntry;
    }

    public static Log createLogEntry(String logText, LogTopic logTopic) {
        Log logEntry = createLogEntry(logText);
        logEntry.setLogTopic(logTopic);
        return logEntry;
    }

    public static void searchLogList(List<Log> logList, Pattern searchPattern, SearchResult searchResult) {
        for (Log logEntry : logList) {
            String baseKey = "log";
            String logEntryKey = baseKey + "/text";
            searchResult.doesValueContainPattern(logEntryKey, logEntry.getText(), searchPattern);
        }
    }
    
    
    public static void addSystemLog(SystemLogLevel logLevelName, String logMessage) {
        LogControllerUtility logControllerUtility = LogControllerUtility.getSystemLogInstance();    
                   
        try {
            logControllerUtility.addSystemLog(logLevelName, logMessage);
        } catch (CdbException ex) {
            Logger.getLogger(LogUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
