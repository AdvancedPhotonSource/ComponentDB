/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.LogLevelFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;

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
    
}
