/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.common.exceptions.CdbException;
import gov.anl.aps.cdb.mqtt.metadata.ItemLogChange;
import gov.anl.aps.cdb.portal.constants.SystemLogLevel;
import gov.anl.aps.cdb.portal.controllers.utilities.LogControllerUtility;
import gov.anl.aps.cdb.portal.model.db.entities.Attachment;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SearchResult;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    /**
     * Compares persisted item log entries by ID with the submitted list. Attachments are
     * excluded so attachment-only edits are not reported as log updates. New entries
     * (no ID) are handled after persistence since their IDs are DB-assigned.
     */
    public static List<ItemLogChange> prepareLogChanges(List<Log> originalLogList, List<Log> newLogList) {
        List<ItemLogChange> changes = new ArrayList<>();
        List<Log> originals = originalLogList == null ? Collections.emptyList() : originalLogList;
        List<Log> submitted = newLogList == null ? Collections.emptyList() : newLogList;
        Map<Integer, Log> submittedById = new HashMap<>();
        for (Log log : submitted) {
            if (log.getId() != null) {
                submittedById.put(log.getId(), log);
            }
        }
        for (Log original : originals) {
            if (original.getId() == null) {
                continue;
            }
            Log log = submittedById.get(original.getId());
            if (log == null) {
                changes.add(ItemLogChange.deleted(original));
            } else if (isLogEntryModified(original, log)) {
                changes.add(ItemLogChange.updated(original, log));
            }
        }
        return changes;
    }

    private static boolean isLogEntryModified(Log original, Log log) {
        return !Objects.equals(original.getText(), log.getText())
                || !sameInstant(original.getEffectiveFromDateTime(), log.getEffectiveFromDateTime())
                || !sameInstant(original.getEffectiveToDateTime(), log.getEffectiveToDateTime())
                || !Objects.equals(original.getLogTopic(), log.getLogTopic());
    }

    // java.sql.Timestamp.equals(Date) is false for the same instant.
    private static boolean sameInstant(Date first, Date second) {
        return first == null ? second == null : second != null && first.getTime() == second.getTime();
    }

    /** Associates an attachment with a log in memory; the owning entity's update persists it. */
    public static void addAttachment(Log log, Attachment attachment) {
        if (log.getAttachmentList() == null) {
            log.setAttachmentList(new ArrayList<>());
        }
        log.getAttachmentList().add(attachment);
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
