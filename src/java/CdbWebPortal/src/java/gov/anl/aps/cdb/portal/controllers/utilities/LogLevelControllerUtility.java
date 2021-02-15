/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.LogLevelFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class LogLevelControllerUtility extends CdbEntityControllerUtility<LogLevel, LogLevelFacade> {

    @Override
    protected LogLevelFacade getEntityDbFacade() {
        return LogLevelFacade.getInstance(); 
    }
    
    @Override
    public String getEntityTypeName() {
        return "logLevel";
    }

    @Override
    public LogLevel createEntityInstance(UserInfo sessionUser) {
        return new LogLevel();
    }
    
}
