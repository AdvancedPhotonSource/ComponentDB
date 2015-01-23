package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;

public class LogUtility {
    
    public static Log createLogEntry() {
        UserInfo enteredByUser = (UserInfo) SessionUtility.getUser();
        Date enteredOnDateTime = new Date();
        Log logEntry = new Log();
        logEntry.setEnteredByUser(enteredByUser);
        logEntry.setEnteredOnDateTime(enteredOnDateTime);
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
}
