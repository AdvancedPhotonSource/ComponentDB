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

import gov.anl.aps.cdb.portal.model.db.entities.ResourceType;
import javax.ejb.Stateless;

/**
 * DB facade for resource types.
 */
@Stateless
public class ResourceTypeDbFacade extends CdbEntityDbFacade<ResourceType> {

    public ResourceTypeDbFacade() {
        super(ResourceType.class);
    }

}
