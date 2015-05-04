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

import gov.anl.aps.cdb.portal.model.db.entities.AssemblyComponentConnection;
import javax.ejb.Stateless;

/**
 * DB facade for assembly component connections.
 */
@Stateless
public class AssemblyComponentConnectionDbFacade extends CdbEntityDbFacade<AssemblyComponentConnection> {

    public AssemblyComponentConnectionDbFacade() {
        super(AssemblyComponentConnection.class);
    }

}
