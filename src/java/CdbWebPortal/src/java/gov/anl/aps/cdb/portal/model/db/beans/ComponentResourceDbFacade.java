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

import gov.anl.aps.cdb.portal.model.db.entities.ComponentResource;
import javax.ejb.Stateless;

/**
 * DB facade for component resources.
 */
@Stateless
public class ComponentResourceDbFacade extends CdbEntityDbFacade<ComponentResource> {

    public ComponentResourceDbFacade() {
        super(ComponentResource.class);
    }

}
