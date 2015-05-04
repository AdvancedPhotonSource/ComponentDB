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

import gov.anl.aps.cdb.portal.model.db.entities.ResourceTypeCategory;
import javax.ejb.Stateless;

/**
 * DB facade for resource type categories.
 */
@Stateless
public class ResourceTypeCategoryDbFacade extends CdbEntityDbFacade<ResourceTypeCategory> {

    public ResourceTypeCategoryDbFacade() {
        super(ResourceTypeCategory.class);
    }

}
