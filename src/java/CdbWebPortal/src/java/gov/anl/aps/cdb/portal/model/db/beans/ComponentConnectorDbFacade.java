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

import gov.anl.aps.cdb.portal.model.db.entities.ComponentConnector;
import javax.ejb.Stateless;

/**
 * DB facade for component connectors.
 */
@Stateless
public class ComponentConnectorDbFacade extends CdbEntityDbFacade<ComponentConnector> {

    public ComponentConnectorDbFacade() {
        super(ComponentConnector.class);
    }

}
