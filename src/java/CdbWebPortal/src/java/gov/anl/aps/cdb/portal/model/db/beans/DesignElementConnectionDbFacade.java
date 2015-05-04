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

import gov.anl.aps.cdb.portal.model.db.entities.DesignElementConnection;
import javax.ejb.Stateless;

/**
 * DB facade for design element connections.
 */
@Stateless
public class DesignElementConnectionDbFacade extends CdbEntityDbFacade<DesignElementConnection> {

    public DesignElementConnectionDbFacade() {
        super(DesignElementConnection.class);
    }

}
