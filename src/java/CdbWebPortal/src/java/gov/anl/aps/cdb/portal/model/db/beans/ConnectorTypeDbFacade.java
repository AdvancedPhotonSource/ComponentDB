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

import gov.anl.aps.cdb.portal.model.db.entities.ConnectorType;
import javax.ejb.Stateless;

/**
 * DB facade for connector types.
 */
@Stateless
public class ConnectorTypeDbFacade extends CdbEntityDbFacade<ConnectorType>
{
    public ConnectorTypeDbFacade() {
        super(ConnectorType.class);
    }
    
}
