/*
 * Copyright (c) 2014-2015, Argonne National Laboratory.
 *
 * SVN Information:
 *   $HeadURL$
 *   $Date$
 *   $Revision$
 *   $Author$
 */
package gov.anl.aps.cdb.portal.model.db.beans;

import gov.anl.aps.cdb.portal.model.db.entities.LogTopic;
import javax.ejb.Stateless;

/**
 * DB facade for log topics.
 */
@Stateless
public class LogTopicDbFacade extends CdbEntityDbFacade<LogTopic> {

    public LogTopicDbFacade() {
        super(LogTopic.class);
    }

}
