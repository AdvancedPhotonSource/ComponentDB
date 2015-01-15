package gov.anl.aps.cdb.portal.model.db.utilities;

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;
import gov.anl.aps.cdb.portal.utilities.SessionUtility;
import java.util.Date;

public class LogUtility {

    public static Log createLogEntry() {
        UserInfo lastModifiedByUser = (UserInfo) SessionUtility.getUser();
        Date lastModifiedOnDateTime = new Date();
        Log logEntry = new Log();
        logEntry.setEnteredByUser(lastModifiedByUser);
        logEntry.setEnteredOnDateTime(lastModifiedOnDateTime);
        return logEntry;
    }
}
