/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.controllers.utilities;

import gov.anl.aps.cdb.portal.model.db.beans.LogTopicFacade;
import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import gov.anl.aps.cdb.portal.model.db.entities.UserInfo;

/**
 *
 * @author darek
 */
public class LogTopicControllerUtility extends CdbEntityControllerUtility<LogTopic, LogTopicFacade> {

    @Override
    protected LogTopicFacade getEntityDbFacade() {
        return LogTopicFacade.getInstance();
    }

    @Override
    public String getEntityInstanceName(LogTopic entity) {
        if (entity != null) {
            return entity.getName();
        }
        return "";
    }
    
    @Override
    public String getEntityTypeName() {
        return "logTopic";
    }   

    @Override
    public LogTopic createEntityInstance(UserInfo sessionUser) {
        return new LogTopic();
    }
    
}
