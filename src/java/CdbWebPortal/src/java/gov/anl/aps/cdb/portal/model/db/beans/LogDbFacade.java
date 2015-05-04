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

import gov.anl.aps.cdb.portal.model.db.entities.Log;
import javax.ejb.Stateless;

/**
 * DB facade for log objects.
 */
@Stateless
public class LogDbFacade extends CdbEntityDbFacade<Log> {

    public LogDbFacade() {
        super(Log.class);
    }  
    
}
